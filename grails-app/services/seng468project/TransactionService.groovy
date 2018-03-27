package seng468project

import grails.transaction.Transactional
import seng468project.beans.QuoteServerTypeBean
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
//    @Timed(value='TransactionService.buy', useClassPrefix = false)
    String buy(User user, String stockSymbol, BigDecimal amountPrice, int transactionNum) {
        QuoteServerTypeBean quote = quoteService.getQuote(user, stockSymbol, transactionNum)

        if(user.realBalance() < amountPrice && user.realBalance() < quote.price) {
            return "you don't have enough money"
        }
        Transaction transaction = new Transaction(
                user: user,
                status: TransactionStatusEnum.BUY,
                stockSymbol: stockSymbol,
                quotedPrice: quote.price,
                amount: amountPrice
        ).save()
        return "User $user.username requested to purchase $amountPrice\$ value of stock $stockSymbol at price $quote.price, Please send COMMIT_BUY to confirm"
    }

//    @Timed(value='TransactionService.commitbuy', useClassPrefix = false)
    String commitBuy(User user, int transactionNum) {

        Long sixtySecondsAgo = new Timestamp(new Date().getTime()).getTime() - 60000
        def t = Transaction.executeQuery("select t from Transaction t where t.user = ? and t.dateCreated = (select distinct max(a.dateCreated) from Transaction a " +
                "where a.dateCreated > ? and a.status = ? and a.user = ?)",
                [user ,sixtySecondsAgo, TransactionStatusEnum.BUY, user])

        Transaction transaction = t[0]

        if(!transaction) {
            return "No active buy was found for user $user.username"
        }
        if(user.realBalance() < transaction.amount) {
            return "you don't have enough money"
        }
        if(transaction.amount < transaction.quotedPrice){
            return "your purchase amount wont be able to buy one share"
        }

        BigDecimal shareAmount = transaction.amount/transaction.quotedPrice
        BigDecimal sharesToBuy = shareAmount.setScale(0, RoundingMode.FLOOR)
        dbService.addStockShares(transaction.user.username,transaction.stockSymbol,sharesToBuy.intValueExact())
        user.balance = user.balance - (sharesToBuy*transaction.quotedPrice)
        user.save()
        transaction.status = TransactionStatusEnum.COMMIT_BUY
        transaction.save()

        String res = "Success! You just purchased ${shareAmount}shares of \"$transaction.stockSymbol\", the remaining ${transaction.amount.remainder(transaction.quotedPrice)} has returned to your account."
        return res
    }

//    @Timed(value='TransactionService.cancelbuy', useClassPrefix = false)
    String cancelBuy(User user, int transactionNum) {
        Long sixtySecondsAgo = new Timestamp(new Date().getTime()).getTime() - 60000
        def t = Transaction.executeQuery("select t from Transaction t where t.user = ? and t.dateCreated = (select distinct max(a.dateCreated) from Transaction a " +
                "where a.dateCreated > ? and a.status = ? and a.user = ?)",
                [user ,sixtySecondsAgo, TransactionStatusEnum.BUY, user])

        Transaction transaction = t[0]

        if(!transaction) {
            return "You dont have an active trasaction"
        }

        transaction.status = TransactionStatusEnum.CANCEL_BUY
        transaction.save()

        return "Canceled successfully"
    }

//    @Timed(value='TransactionService.sell', useClassPrefix = false)
    String sell(User user, String stockSymbol, BigDecimal sellPriceAmount, int transactionNum) {
        QuoteServerTypeBean quote = quoteService.getQuote(user, stockSymbol, transactionNum)

        BigDecimal sharesCanSell = sellPriceAmount/quote.price
        BigDecimal sharesToSell = sharesCanSell.setScale(0, RoundingMode.FLOOR)

        if((user.stockShareMap[stockSymbol] as Integer) < sharesToSell) {
            return "you don't have enough share"
        }
        Transaction transaction = new Transaction(
                user: user,
                status: TransactionStatusEnum.SELL,
                stockSymbol: stockSymbol,
                quotedPrice: quote.price,
                amount: sharesToSell
        ).save()

        return "User $user.username requested to sell $sharesToSell\$ shares of $stockSymbol at price $quote.price, Please send COMMIT_SELL to confirm"
    }

//    @Timed(value='TransactionService.commitsell', useClassPrefix = false)
    String commitSell(User user, int transactionNum) {
        Long sixtySecondsAgo = new Timestamp(new Date().getTime()).getTime() - 60000
        def t = Transaction.executeQuery("select t from Transaction t where t.user = ? and t.dateCreated = (select distinct max(a.dateCreated) from Transaction a " +
                "where a.dateCreated > ? and a.status = ? and a.user = ?)",
                [user ,sixtySecondsAgo, TransactionStatusEnum.BUY, user])

        Transaction transaction = t[0]

        if(!transaction) {
            return "No active sell was found for user $user.username"
        }
        if((user.stockShareMap[transaction.stockSymbol] as Integer) < transaction.amount) {
            return "you don't have enough share, you have ${(user.stockShareMap[transaction.stockSymbol] as Integer)}, and you tried to sell $transaction.amount"
        }

        // TODO: use DbServices for modifying db records later
        System.out.println(transaction.amount)
        System.out.println(transaction.quotedPrice)
        System.out.println(transaction.amount * transaction.quotedPrice)

        BigDecimal sellPriceAmount = (transaction.amount * transaction.quotedPrice).setScale(2, RoundingMode.FLOOR)
        user.stockShareMap[transaction.stockSymbol] -= transaction.amount
        user.balance = user.balance + sellPriceAmount
        user.save()
        transaction.status = TransactionStatusEnum.COMMIT_SELL
        transaction.save()

        String res = "Success! You just sell ${transaction.amount}shares of \"$transaction.stockSymbol\", $sellPriceAmount has added to your account."
        return res
    }
//    @Timed(value='TransactionService.cancelsell', useClassPrefix = false)
    String cancelSell(User user, int transactionNum) {

        Long sixtySecondsAgo = new Timestamp(new Date().getTime()).getTime() - 60000
        def t = Transaction.executeQuery("select t from Transaction t where t.user = ? and t.dateCreated = (select distinct max(a.dateCreated) from Transaction a " +
                "where a.dateCreated > ? and a.status = ? and a.user = ?)",
                [user ,sixtySecondsAgo, TransactionStatusEnum.BUY, user])

        Transaction transaction = t[0]

        if(!transaction) {
            return "You dont have an active transaction"
        }

        transaction.status = TransactionStatusEnum.CANCEL_SELL
        transaction.save()

        return "Canceled successfully"
    }

    /***************************************************************
        TRIGGER SECTION
     **************************************************************/
//    @Timed(value='TransactionService.triggerExists', useClassPrefix = false)
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

//    @Timed(value='TransactionService.setBuyAmount', useClassPrefix = false)
    String setBuyAmount(User user, String stockSymbol, BigDecimal amount, int transactionNum){
        // if trigger already exitst, don't proceed any further
        if(triggerExists(user,stockSymbol,"BUY")) {
            return "TransactionTrigger for $stockSymbol already exists"
        }

        QuoteServerTypeBean quote = quoteService.getQuote(user,stockSymbol, transactionNum)

        if(!hasSufficientBalance(user.realBalance(),amount,quote.price)) {
            return "you don't have enough money"
        }
        // TODO check if can access db through user.

        if(!dbService.reserveMoney(user.username, amount)){
            return "user doesn't exist"
        }

        // TODO: relate trigger table to user table
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

//    @Timed(value='TransactionService.canselSetBuy', useClassPrefix = false)
    String cancelSetBuy(User user,String stockSymbol, int transactionNum){
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
            return "no trigger record found"
        }

        // release money
        if(!dbService.releaseReservedMoney(user.username,record.reservedBalance)){
            return "user not found"
        }

        record.status = TriggerStatusEnum.CANCELED
        record.save()

        return "set_buy canceled"
    }

//    @Timed(value='TransactionService.setBuyTrigger', useClassPrefix = false)
    String setBuyTrigger(User user, String stockSymbol, BigDecimal amount, int transactionNum){
        def record = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol',stockSymbol
            eq 'status', TriggerStatusEnum.SET_BUY
        } as TransactionTrigger

        if(!record) {
            return "no record found, please set buy amount first"
        }

        record.status = TriggerStatusEnum.SET_BUY_TRIGGER
        record.triggerPrice = amount
        record.save()

        return "buy trigger set"
    }

    //TODO: add function to handle buy trigger
//    @Timed(value='TransactionService.setSellAmount', useClassPrefix = false)
    String setSellAmount(User user, String stockSymbol, BigDecimal amount, int transactionNum){
        // check no other triggers for the same symbol
        if(triggerExists(user,stockSymbol,"SELL")) {
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

//    @Timed(value='TransactionService.setSellTrigger', useClassPrefix = false)
    String setSellTrigger(User user, String stockSymbol, BigDecimal amount, int transactionNum){
        def record = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol',stockSymbol
            eq 'status', TriggerStatusEnum.SET_SELL
        } as TransactionTrigger

        if(!record) {
            return "no record found, please set sell amount first"
        }

        BigDecimal sharesCanSell = record.buySellAmount/amount
        BigDecimal sharesToSell = sharesCanSell.setScale(0, RoundingMode.FLOOR)

        // check user has sufficient shares
        if(dbService.getUserStocks(user.username,stockSymbol) < sharesToSell.intValueExact()) {
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

//    @Timed(value='TransactionService.cancelSetCell', useClassPrefix = false)
    String cancelSetSell(User user,String stockSymbol, int transactionNum){
        TransactionTrigger record = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol',stockSymbol
            or {
                eq 'status', TriggerStatusEnum.SET_SELL
                eq 'status', TriggerStatusEnum.SET_SELL_TRIGGER
            }
        }
        if(!record) {
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
            }else if(it.status == TriggerStatusEnum.SET_SELL_TRIGGER && quote.price >= it.triggerPrice){
                // TODO: use sell
                BigDecimal moneyToAdd = new BigDecimal(it.reservedShares)* quote.price

                dbService.addAmount(it.user.username,moneyToAdd.toString(), transactionNum)
                it.status = TriggerStatusEnum.DONE
            }

        }
    }

}
