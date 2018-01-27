package seng468project

import grails.transaction.Transactional
import seng468project.beans.AccountTransactionTypeBean
import seng468project.beans.QuoteServerTypeBean
import seng468project.beans.UserCommandTypeBean
import seng468project.enums.TransactionStatusEnum
import seng468project.enums.TriggerStatusEnum

import java.math.RoundingMode

import java.math.MathContext
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

    String buy(User user, String stockSymbol, BigDecimal amountPrice) {
        QuoteServerTypeBean quote = quoteService.getQuote(user, stockSymbol)
        if(user.realBalance() < amountPrice && user.realBalance() < quote.price) {
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

        // object for XML block
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transaction.id as Integer,
                "BUY",
                user.username,
                stockSymbol,
                "",
                amountPrice.toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()

        log.info("User $user.username requested to purchase $amountPrice\$ value of stock $stockSymbol at price $quote.price, Please send COMMIT_BUY to confirm")
        return "User $user.username requested to purchase $amountPrice\$ value of stock $stockSymbol at price $quote.price, Please send COMMIT_BUY to confirm"
    }

    String commitBuy(User user) {
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
        } as Transaction

        if(!transaction) {
            log.info("No active buy was found for user $user.username")
            return "No active buy was found for user $user.username"
        }
        if(user.realBalance() < transaction.amount) {
            log.info("you don't have enough money")
            return "you don't have enough money"
        }
        if(transaction.amount < transaction.quotedPrice){
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
                transaction.id as Integer,
                "COMMIT_BUY",
                user.username,
                (sharesToBuy*transaction.quotedPrice).toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getAccountTransactionString(obj_1)
        // save to db
        new LogHistory(user, str).save()


        // object for XML block
        // TODO: generate transactionNumber
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transaction.id as Integer,
                "COMMIT_BUY",
                user.username,
                transaction.stockSymbol,
                "",
                transaction.amount.toString()
        )
        // get the corresponding formatted XML block
        str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()


        String res = "Success! You just purchased ${shareAmount}shares of \"$transaction.stockSymbol\", the remaining ${transaction.amount.remainder(transaction.quotedPrice)} has returned to your account."
        log.info(res)
        return res
    }

    String cancelBuy(User user) {
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
        } as Transaction
        if(!transaction) {
            return "You dont have an active trasaction"
        }

        transaction.status = TransactionStatusEnum.CANCEL_BUY
        transaction.save()

        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transaction.id as Integer,
                "CANCEL_BUY",
                user.username,
                transaction.stockSymbol,
                "",
                transaction.amount.toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()

        return "Canceled successfully"
    }

    String sell(User user, String stockSymbol, BigDecimal sellPriceAmount) {
        QuoteServerTypeBean quote = quoteService.getQuote(user, stockSymbol)

        BigDecimal sharesCanSell = sellPriceAmount/quote.price
        BigDecimal sharesToSell = sharesCanSell.setScale(0, RoundingMode.FLOOR)

        if((user.stockShareMap[stockSymbol] as Integer) < sharesToSell) {
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

        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transaction.id as Integer,
                "SELL",
                user.username,
                transaction.stockSymbol,
                "",
                sellPriceAmount.toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()

        log.info("User $user.username requested to sell $sharesToSell\$ shares of $stockSymbol at price $quote.price, Please send COMMIT_SELL to confirm")
        return "User $user.username requested to sell $sharesToSell\$ shares of $stockSymbol at price $quote.price, Please send COMMIT_SELL to confirm"
    }

    String commitSell(User user) {
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
        } as Transaction

        if(!transaction) {
            log.info("No active sell was found for user $user.username")
            return "No active sell was found for user $user.username"
        }
        if((user.stockShareMap[transaction.stockSymbol] as Integer) < transaction.amount) {
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
                transaction.id as Integer,
                "COMMIT_SELL",
                user.username,
                sellPriceAmount.toString()
        )
        String str = auditService.getAccountTransactionString(obj_1)
        new LogHistory(user, str).save()

        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transaction.id as Integer,
                "COMMIT_SELL",
                user.username,
                transaction.stockSymbol,
                "",
                sellPriceAmount.toString()
        )
        // get the corresponding formatted XML block
        str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()


        String res = "Success! You just sell ${transaction.amount}shares of \"$transaction.stockSymbol\", $sellPriceAmount has added to your account."
        log.info(res)
        return res
    }

    String cancelSell(User user) {
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
        } as Transaction
        if(!transaction) {
            return "You dont have an active transaction"
        }

        transaction.status = TransactionStatusEnum.CANCEL_SELL
        transaction.save()

        // TODO: IMPORTANT here, amount is number of shares
        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transaction.id as Integer,
                "CANCEL_SELL",
                user.username,
                transaction.stockSymbol,
                "",
                transaction.amount.toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()

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

    String setBuyAmount(User user, String stockSymbol, BigDecimal amount){
        // if trigger already exitst, don't proceed any further
        if(triggerExists(user,stockSymbol,"BUY")) return "TransactionTrigger for $stockSymbol already exists"
        // get quote
        QuoteServerTypeBean quote = quoteService.getQuote(user,stockSymbol)
        // check have enough money
        if(!hasSufficientBalance(user.realBalance(),amount,quote.price)) return "you son't have enough money"
        // TODO check if can access db through user.
        // reserve money
        if(!dbService.reserveMoney(user.username, amount))return "user doesn't exist"

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

        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                new_trig as Integer,
                "SET_BUY_AMOUNT",
                user.username,
                stockSymbol,
                "",
                amount.toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()

        return " Amount: '$amount' is set for stockSymbol: '$stockSymbol', please also set trigger"
    }

    String cancelSetBuy(User user,String stockSymbol){
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
        if(!record) return "no trigger record found"

        // release money
        if(!dbService.releaseReservedMoney(user.username,record.reservedBalance))return "user not found"

        record.status = TriggerStatusEnum.CANCELED
        record.save()

        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                record.id as Integer,
                "CANCEL_SET_BUY",
                user.username,
                stockSymbol,
                "",
                record.reservedBalance.toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()

        return "set_buy canceled"
    }

    String setBuyTrigger(User user, String stockSymbol, BigDecimal amount){
        def record = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol',stockSymbol
            eq 'status', TriggerStatusEnum.SET_BUY
        } as TransactionTrigger

        if(!record) return "no record found, please set buy amount first"

        record.status = TriggerStatusEnum.SET_BUY_TRIGGER
        record.triggerPrice = amount
        record.save()

        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                record.id as Integer,
                "SET_BUY_TRIGGER",
                user.username,
                stockSymbol,
                "",
                amount.toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()

        return "buy trigger set"
    }

    //TODO: add function to handle buy trigger
    String setSellAmount(User user, String stockSymbol, BigDecimal amount){

        // check no other triggers for the same symbol
        if(triggerExists(user,stockSymbol,"SELL")) return "TransactionTrigger for $stockSymbol already exists"

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

        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                new_trig.id as Integer,
                "SET_SELL_AMOUNT",
                user.username,
                stockSymbol,
                "",
                amount.toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()

        return " Amount: '$amount' is set for stockSymbol: '$stockSymbol', please also set trigger"
    }

    String setSellTrigger(User user, String stockSymbol, BigDecimal amount){
        def record = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol',stockSymbol
            eq 'status', TriggerStatusEnum.SET_SELL
        } as TransactionTrigger

        if(!record) return "no record found, please set sell amount first"

        BigDecimal sharesCanSell = record.buySellAmount/amount
        BigDecimal sharesToSell = sharesCanSell.setScale(0, RoundingMode.FLOOR)

        // check user has sufficient shares
        if(dbService.getUserStocks(user.username,stockSymbol)[1] < sharesToSell) return "not enough shares to sell"

        //reserve shares
        //TODO: need to implement another map for reservedshares
        dbService.removeStockShares(user.username,stockSymbol,sharesToSell.intValueExact())

        record.reservedShares = sharesToSell
        record.triggerPrice = amount
        record.status = TriggerStatusEnum.SET_SELL_TRIGGER
        record.save()

        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                record.id as Integer,
                "SET_SELL_TRIGGER",
                user.username,
                stockSymbol,
                "",
                amount.toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()


        return "sell trigger set"
    }

    String cancelSetSell(User user,String stockSymbol){
        TransactionTrigger record = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol',stockSymbol
            or {
                eq 'status', TriggerStatusEnum.SET_SELL
                eq 'status', TriggerStatusEnum.SET_SELL_TRIGGER
            }
        }
        if(!record) return "no trigger record found"

        // release shares
        dbService.addStockShares(user.username,record.stockSymbol,record.reservedShares)

        record.status = TriggerStatusEnum.CANCELED
        record.save()

        UserCommandTypeBean obj = new UserCommandTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                record.id as Integer,
                "SET_SELL_TRIGGER",
                user.username,
                stockSymbol,
                "",
                record.triggerPrice.toString()
        )
        // get the corresponding formatted XML block
        String str = auditService.getUserCommandString(obj)
        // save to db
        new LogHistory(user, str).save()

        return "set_sell canceled"
    }




    def checkTrigger() {
        def records = TransactionTrigger.createCriteria().list {
            or {
                eq 'status', TriggerStatusEnum.SET_BUY_TRIGGER
                eq 'status', TriggerStatusEnum.SET_SELL_TRIGGER
            }
        } as ArrayList<TransactionTrigger>

        QuoteServerTypeBean quote = null

        //TODO cache
        records.each {
            quote = quoteService.getQuote(it.user,it.stockSymbol)
            if(it.status == TriggerStatusEnum.SET_BUY_TRIGGER && quote.price <= it.triggerPrice){
                // TODO: use commit buy
                BigDecimal sharesCanBuy = it.reservedBalance/quote.price
                BigDecimal sharesToBuy = sharesCanBuy.setScale(0, RoundingMode.FLOOR)
                BigDecimal amountToReturn = it.reservedBalance - (sharesToBuy*quote.price)

                //TODO: use db methods
                dbService.removeAmount(it.user.username,it.reservedBalance.toString())
                dbService.addAmount(it.user.username,amountToReturn.toString())
                dbService.addStockShares(it.user.username,it.stockSymbol,sharesToBuy.intValueExact())
                it.user.reservedBalance -= it.reservedBalance
                it.user.save()
                it.status = TriggerStatusEnum.DONE

                AccountTransactionTypeBean obj_1 = new AccountTransactionTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        it.user.id as Integer,
                        "BUY_TRIGGERED",
                        it.user.username,
                        it.user.reservedBalance.toString()
                )
                String str = auditService.getAccountTransactionString(obj_1)
                new LogHistory(it.user, str).save()

                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        it.user.id as Integer,
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

                dbService.addAmount(it.user.username,moneyToAdd.toString())
                it.status = TriggerStatusEnum.DONE

                AccountTransactionTypeBean obj_1 = new AccountTransactionTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        it.user.id as Integer,
                        "SELL_TRIGGERED",
                        it.user.username,
                        moneyToAdd.toString()
                )
                String str = auditService.getAccountTransactionString(obj_1)
                new LogHistory(it.user, str).save()

                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        it.user.id as Integer,
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
