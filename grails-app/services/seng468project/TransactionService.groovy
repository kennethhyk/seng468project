package seng468project

import grails.transaction.Transactional
import seng468project.beans.AccountTransactionTypeBean
import seng468project.beans.QuoteServerTypeBean
import seng468project.beans.UserCommandTypeBean
import seng468project.enums.TransactionStatusEnum
import seng468project.enums.TriggerStatusEnum

import java.math.RoundingMode

import java.sql.Timestamp

@Transactional
class TransactionService {
    def quoteService
    def dbService
    def auditService

    Boolean hasSufficientBalance(BigDecimal userBalance, BigDecimal buyAmount, BigDecimal price){
        if(userBalance.compareTo(buyAmount) == -1) return false
        if(userBalance.compareTo(price) == -1) return false
        return true
    }

    String buy(User user, String stockSymbol, BigDecimal amountPrice, int transactionNum) {
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "BUY",
                user.username,
                stockSymbol,
                "",
                amountPrice.toString()
        )
        QuoteServerTypeBean quote = quoteService.getQuote(user, stockSymbol, transactionNum)

        if(user.realBalance() < amountPrice && user.realBalance() < quote.price) {
            auditService.saveErrorEvent(user, obj, "you don't have enough money")
            log.info("you don't have enough money")
            return "you don't have enough money"
        }
        Transaction transaction = new Transaction(
                user: user,
                status: TransactionStatusEnum.BUY,
                stockSymbol: stockSymbol,
                quotedPrice: quote.price,
                amount: amountPrice
        ).save()

        log.info("User $user.username requested to purchase $amountPrice\$ value of stock $stockSymbol at price $quote.price, Please send COMMIT_BUY to confirm")
        return "User $user.username requested to purchase $amountPrice\$ value of stock $stockSymbol at price $quote.price, Please send COMMIT_BUY to confirm"
    }

    String commitBuy(User user, int transactionNum) {
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "COMMIT_BUY",
                user.username,
                "",
                "",
                ""
        )
        Long sixtySecondsAgo = new Timestamp(new Date().getTime()).getTime() - 60000
        def lastestTransactionTime = Transaction.createCriteria().get {
            projections {
                max("dateCreated")
                gt("dateCreated", sixtySecondsAgo)
                eq('status', TransactionStatusEnum.BUY)
                eq('user', user)
            }
        }
        def transaction = Transaction.createCriteria().get {
            eq('dateCreated', lastestTransactionTime)
            eq('user',user)
        } as Transaction

        if(!transaction) {
            auditService.saveErrorEvent(user,obj,"No active buy was found for user $user.username")
            log.info("No active buy was found for user $user.username")
            return "No active buy was found for user $user.username"
        }
        if(user.realBalance() < transaction.amount) {
            auditService.saveErrorEvent(user,obj,"you don't have enough money")
            log.info("you don't have enough money")
            return "you don't have enough money"
        }
        if(transaction.amount < transaction.quotedPrice){
            auditService.saveErrorEvent(user,obj,"your purchase amount wont be able to buy one share")
            log.info("your purchase amount wont be able to buy one share")
            return "your purchase amount wont be able to buy one share"
        }

        BigDecimal shareAmount = transaction.amount/transaction.quotedPrice
        BigDecimal sharesToBuy = shareAmount.setScale(0, RoundingMode.FLOOR)
        dbService.addStockShares(transaction.user.username,transaction.stockSymbol,sharesToBuy.intValueExact())
        user.balance = user.balance - (sharesToBuy*transaction.quotedPrice)
        user.save()
        transaction.status = TransactionStatusEnum.COMMIT_BUY
        transaction.save()

        AccountTransactionTypeBean obj_1 = new AccountTransactionTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "COMMIT_BUY",
                user.username,
                (sharesToBuy*transaction.quotedPrice).toString()
        )
        String str = auditService.getAccountTransactionString(obj_1)
        new LogHistory(user, str).save()

        String res = "Success! You just purchased ${shareAmount}shares of \"$transaction.stockSymbol\", the remaining ${transaction.amount.remainder(transaction.quotedPrice)} has returned to your account."
        log.info(res)
        return res
    }

    String cancelBuy(User user, int transactionNum) {
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "CANCEL_BUY",
                user.username,
                "",
                "",
                ""
        )
        Long sixtySecondsAgo = new Timestamp(new Date().getTime()).getTime() - 60000
        def lastestTransactionTime = Transaction.createCriteria().get {
            projections {
                max("dateCreated")
                gt("dateCreated", sixtySecondsAgo)
                eq('status', TransactionStatusEnum.BUY)
                eq('user', user)
            }
        }
        def transaction = Transaction.createCriteria().get {
            eq('dateCreated', lastestTransactionTime )
            eq('user', user )
        } as Transaction

        if(!transaction) {
            auditService.saveErrorEvent(user,obj,"You dont have an active trasaction")
            return "You dont have an active trasaction"
        }

        transaction.status = TransactionStatusEnum.CANCEL_BUY
        transaction.save()

        return "Canceled successfully"
    }

    String sell(User user, String stockSymbol, BigDecimal sellPriceAmount, int transactionNum) {
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "SELL",
                user.username,
                "",
                "",
                ""
        )
        QuoteServerTypeBean quote = quoteService.getQuote(user, stockSymbol, transactionNum)

        BigDecimal sharesCanSell = sellPriceAmount/quote.price
        BigDecimal sharesToSell = sharesCanSell.setScale(0, RoundingMode.FLOOR)

        if((user.stockShareMap[stockSymbol] as Integer) < sharesToSell) {
            auditService.saveErrorEvent(user,obj,"you don't have enough share")
            log.info("you don't have enough share")
            return "you don't have enough share"
        }
        Transaction transaction = new Transaction(
                user: user,
                status: TransactionStatusEnum.SELL,
                stockSymbol: stockSymbol,
                quotedPrice: quote.price,
                amount: sharesToSell
        ).save()

        log.info("User $user.username requested to sell $sharesToSell\$ shares of $stockSymbol at price $quote.price, Please send COMMIT_SELL to confirm")
        return "User $user.username requested to sell $sharesToSell\$ shares of $stockSymbol at price $quote.price, Please send COMMIT_SELL to confirm"
    }

    String commitSell(User user, int transactionNum) {
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "COMMIT_SELL",
                user.username,
                "",
                "",
                ""
        )
        Long sixtySecondsAgo = new Timestamp(new Date().getTime()).getTime() - 60000
        def lastestTransactionTime = Transaction.createCriteria().get {
            projections {
                max("dateCreated")
                gt("dateCreated", sixtySecondsAgo)
                eq('status', TransactionStatusEnum.SELL)
                eq('user', user)
            }
        }
        def transaction = Transaction.createCriteria().get {
            eq('dateCreated', lastestTransactionTime )
            eq('user', user )
        } as Transaction

        if(!transaction) {
            auditService.saveErrorEvent(user,obj,"No active sell was found for user $user.username")
            log.info("No active sell was found for user $user.username")
            return "No active sell was found for user $user.username"
        }
        if((user.stockShareMap[transaction.stockSymbol] as Integer) < transaction.amount) {
            auditService.saveErrorEvent(user,obj,"you don't have enough share, you have ${(user.stockShareMap[transaction.stockSymbol] as Integer)}, and you tried to sell $transaction.amount")
            log.info("you don't have enough share, you have ${(user.stockShareMap[transaction.stockSymbol] as Integer)}, and you tried to sell $transaction.amount")
            return "you don't have enough share, you have ${(user.stockShareMap[transaction.stockSymbol] as Integer)}, and you tried to sell $transaction.amount"
        }

        // TODO: use DbServices for modifying db records later
        BigDecimal sellPriceAmount = (transaction.amount * transaction.quotedPrice).setScale(2)
        user.stockShareMap[transaction.stockSymbol] -= transaction.amount
        user.balance = user.balance + sellPriceAmount
        user.save()
        transaction.status = TransactionStatusEnum.COMMIT_SELL
        transaction.save()

        AccountTransactionTypeBean obj_1 = new AccountTransactionTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "COMMIT_SELL",
                user.username,
                sellPriceAmount.toString()
        )
        String str = auditService.getAccountTransactionString(obj_1)
        new LogHistory(user, str).save()

        String res = "Success! You just sell ${transaction.amount}shares of \"$transaction.stockSymbol\", $sellPriceAmount has added to your account."
        log.info(res)
        return res
    }

    String cancelSell(User user, int transactionNum) {
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "CANCEL_SELL",
                user.username,
                "",
                "",
                ""
        )
        Long sixtySecondsAgo = new Timestamp(new Date().getTime()).getTime() - 60000
        def lastestTransactionTime = Transaction.createCriteria().get {
            projections {
                max("dateCreated")
                gt("dateCreated", sixtySecondsAgo)
                eq('status', TransactionStatusEnum.SELL)
                eq('user', user)
            }
        }
        def transaction = Transaction.createCriteria().get {
            eq('dateCreated', lastestTransactionTime )
            eq('user', user )
        } as Transaction
        if(!transaction) {
            auditService.saveErrorEvent(user,obj,"You dont have an active transaction")
            return "You dont have an active transaction"
        }

        transaction.status = TransactionStatusEnum.CANCEL_SELL
        transaction.save()

        return "Canceled successfully"
    }

    /***************************************************************
        TRIGGER SECTION
     **************************************************************/

    Boolean triggerExists(User user, String stockSymbol, String type){
        if(type.equals("BUY") ){
            if(TransactionTrigger.createCriteria().get{
                and {
                    eq'user',user
                    eq 'stockSymbol',stockSymbol
                    or {
                        eq 'status', TriggerStatusEnum.SET_BUY
                        eq 'status', TriggerStatusEnum.SET_BUY_TRIGGER
                    }
                }
            }) return true
        }else if(type.equals("SELL")){
            if(TransactionTrigger.createCriteria().get{
                eq'user',user
                eq 'stockSymbol',stockSymbol
                or {
                    eq 'status', TriggerStatusEnum.SET_SELL
                    eq 'status', TriggerStatusEnum.SET_SELL_TRIGGER
                }
            }) return true
        }
        return false
    }

    String setBuyAmount(User user, String stockSymbol, BigDecimal amount, int transactionNum){
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "SET_BUY_AMOUNT",
                user.username,
                stockSymbol,
                "",
                amount.toString()
        )
        // if trigger already exitst, don't proceed any further
        if(triggerExists(user,stockSymbol,"BUY")) {
            auditService.saveErrorEvent(user,obj, "TransactionTrigger for $stockSymbol already exists")
            return "TransactionTrigger for $stockSymbol already exists"
        }
        // get quote
        QuoteServerTypeBean quote = quoteService.getQuote(user,stockSymbol, transactionNum)
        // check have enough money
        if(!hasSufficientBalance(user.realBalance(),amount,quote.price)) {
            auditService.saveErrorEvent(user,obj, "you don't have enough money")
            return "you don't have enough money"
        }
        // TODO check if can access db through user.
        // reserve money
        if(!dbService.reserveMoney(user.username, amount)){
            auditService.saveErrorEvent(user,obj, "user doesn't exist")
            return "user doesn't exist"
        }

        // TODO: relate trigger table to user table
        // create new trigger record in db
        TransactionTrigger new_trig = new TransactionTrigger(
                user,
                stockSymbol,
                new BigDecimal("-1.00"),
                amount,
                amount,
                0,
                TriggerStatusEnum.SET_BUY
        ).save()

        return " Amount: '$amount' is set for stockSymbol: '$stockSymbol', please also set trigger"
    }

    String cancelSetBuy(User user,String stockSymbol, int transactionNum){
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "CANCEL_SET_BUY",
                user.username,
                stockSymbol,
                "",
                ""
        )
        // find the trigger
        def record = TransactionTrigger.createCriteria().get{
            and {
                eq'user',user
                eq 'stockSymbol',stockSymbol
                or {
                    eq 'status', TriggerStatusEnum.SET_BUY
                    eq 'status', TriggerStatusEnum.SET_BUY_TRIGGER
                }
            }
        } as TransactionTrigger
        if(!record) {
            auditService.saveErrorEvent(user,obj,"no trigger record found")
            return "no trigger record found"
        }

        // release money
        if(!dbService.releaseReservedMoney(user.username,record.reservedBalance)){
            auditService.saveErrorEvent(user,obj,"user not found")
            return "user not found"
        }

        record.status = TriggerStatusEnum.CANCELED
        record.save()

        return "set_buy canceled"
    }

    String setBuyTrigger(User user, String stockSymbol, BigDecimal amount, int transactionNum){
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "SET_BUY_TRIGGER",
                user.username,
                stockSymbol,
                "",
                amount.toString()
        )
        def record = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol',stockSymbol
            eq 'status', TriggerStatusEnum.SET_BUY
        } as TransactionTrigger

        if(!record) {
            auditService.saveErrorEvent(user,obj,"no record found, please set buy amount first")
            return "no record found, please set buy amount first"
        }

        record.status = TriggerStatusEnum.SET_BUY_TRIGGER
        record.triggerPrice = amount
        record.save()

        return "buy trigger set"
    }

    //TODO: add function to handle buy trigger
    String setSellAmount(User user, String stockSymbol, BigDecimal amount, int transactionNum){
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "SET_SELL_AMOUNT",
                user.username,
                stockSymbol,
                "",
                amount.toString()
        )
        // check no other triggers for the same symbol
        if(triggerExists(user,stockSymbol,"SELL")) {
            auditService.saveErrorEvent(user,obj,"TransactionTrigger for $stockSymbol already exists")
            return "TransactionTrigger for $stockSymbol already exists"
        }

        // TODO: relate trigger table to user table
        // create new trigger to table
        TransactionTrigger new_trig = new TransactionTrigger(
                user,
                stockSymbol,
                new BigDecimal("-1.00"),
                new BigDecimal("0.00"),
                amount,
                0,
                TriggerStatusEnum.SET_SELL
        ).save()

        return " Amount: '$amount' is set for stockSymbol: '$stockSymbol', please also set trigger"
    }

    String setSellTrigger(User user, String stockSymbol, BigDecimal amount, int transactionNum){
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "SET_SELL_TRIGGER",
                user.username,
                stockSymbol,
                "",
                amount.toString()
        )
        def record = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol',stockSymbol
            eq 'status', TriggerStatusEnum.SET_SELL
        } as TransactionTrigger

        if(!record) {
            auditService.saveErrorEvent(user,obj,"no record found, please set sell amount first")
            return "no record found, please set sell amount first"
        }

        BigDecimal sharesCanSell = record.buySellAmount/amount
        BigDecimal sharesToSell = sharesCanSell.setScale(0, RoundingMode.FLOOR)

        // check user has sufficient shares
        if(dbService.getUserStocks(user.username,stockSymbol)[1] < sharesToSell.intValueExact()) {
            auditService.saveErrorEvent(user,obj,"not enough shares to sell")
            return "not enough shares to sell"
        }

        //reserve shares
        //TODO: need to implement another map for reservedshares
        dbService.removeStockShares(user.username,stockSymbol,sharesToSell.intValueExact())

        record.reservedShares = sharesToSell.intValueExact()
        record.triggerPrice = amount
        record.status = TriggerStatusEnum.SET_SELL_TRIGGER
        record.save()

        return "sell trigger set"
    }

    String cancelSetSell(User user,String stockSymbol, int transactionNum){
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "SET_SELL_TRIGGER",
                user.username,
                stockSymbol,
                "",
                ""
        )
        TransactionTrigger record = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol',stockSymbol
            or {
                eq 'status', TriggerStatusEnum.SET_SELL
                eq 'status', TriggerStatusEnum.SET_SELL_TRIGGER
            }
        }
        if(!record) {
            auditService.saveErrorEvent(user,obj,"no trigger record found")
            return "no trigger record found"
        }

        // release shares
        dbService.addStockShares(user.username,record.stockSymbol,record.reservedShares)

        record.status = TriggerStatusEnum.CANCELED
        record.save()

        return "set_sell canceled"
    }



    //TODO add back later
    def checkTrigger() {
        def records = TransactionTrigger.createCriteria().list {
            or {
                eq 'status', TriggerStatusEnum.SET_BUY_TRIGGER
                eq 'status', TriggerStatusEnum.SET_SELL_TRIGGER
            }
        } as ArrayList<TransactionTrigger>

        QuoteServerTypeBean quote = null
        int transactionNum = 99999
        //TODO cache
        records.each {
            quote = quoteService.getQuote(it.user,it.stockSymbol, transactionNum)
            if(it.status == TriggerStatusEnum.SET_BUY_TRIGGER && quote.price <= it.triggerPrice){
                // TODO: use commit buy
                BigDecimal sharesCanBuy = it.reservedBalance/quote.price
                BigDecimal sharesToBuy = sharesCanBuy.setScale(0, RoundingMode.FLOOR)
                BigDecimal amountToReturn = it.reservedBalance - (sharesToBuy*quote.price)

                //TODO: use db methods
                dbService.removeAmount(it.user.username,it.reservedBalance.toString())
                dbService.addAmount(it.user.username,amountToReturn.toString(), transactionNum)
                dbService.addStockShares(it.user.username,it.stockSymbol,sharesToBuy.intValueExact())
                it.user.reservedBalance -= it.reservedBalance
                it.user.save()
                it.status = TriggerStatusEnum.DONE

                AccountTransactionTypeBean obj_1 = new AccountTransactionTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "BUY_TRIGGERED",
                        it.user.username,
                        it.user.reservedBalance.toString()
                )
                String str = auditService.getAccountTransactionString(obj_1)
                new LogHistory(it.user, str).save()

                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "COMMIT_BUY",
                        it.user.username,
                        it.stockSymbol,
                        "",
                        it.reservedBalance.toString()
                )
                String str_1 = auditService.getSystemEventString(obj)
                new LogHistory(it.user,str_1).save()


            }else if(it.status == TriggerStatusEnum.SET_SELL_TRIGGER && quote.price >= it.triggerPrice){
                // TODO: use sell
                BigDecimal moneyToAdd = new BigDecimal(it.reservedShares)* quote.price

                dbService.addAmount(it.user.username,moneyToAdd.toString(), transactionNum)
                it.status = TriggerStatusEnum.DONE

                AccountTransactionTypeBean obj_1 = new AccountTransactionTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "SELL_TRIGGERED",
                        it.user.username,
                        moneyToAdd.toString()
                )
                String str = auditService.getAccountTransactionString(obj_1)
                new LogHistory(it.user, str).save()

                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "COMMIT_SELL",
                        it.user.username,
                        it.stockSymbol,
                        "",
                        moneyToAdd.toString()
                )
                String str_1 = auditService.getSystemEventString(obj)
                new LogHistory(it.user,str_1).save()
            }

        }
    }

}
