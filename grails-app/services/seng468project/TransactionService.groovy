package seng468project

import grails.transaction.Transactional
import seng468project.beans.QuoteServerTypeBean
import seng468project.enums.TransactionStatusEnum
import seng468project.enums.TriggerStatusEnum

import java.math.RoundingMode

import java.math.MathContext
import java.sql.Timestamp

@Transactional
class TransactionService {
    def quoteService
    def dbService

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

        BigDecimal shareAmount = transaction.amount.divideToIntegralValue(transaction.quotedPrice, new MathContext(2))
        BigDecimal value = user.stockShareMap.get(transaction.stockSymbol) as BigDecimal
        if (value) {
            user.stockShareMap[transaction.stockSymbol] += shareAmount
        } else {
            user.stockShareMap.put(transaction.stockSymbol, shareAmount.toString())
        }
        user.balance = user.balance - transaction.amount + (transaction.amount.remainder(transaction.quotedPrice))
        user.save()
        transaction.status = TransactionStatusEnum.COMMIT_BUY
        transaction.save()

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
        transaction.delete()
        return "Canceled successfully"
    }

    String sell(User user, String stockSymbol, BigDecimal amountShare) {
        QuoteServerTypeBean quote = quoteService.getQuote(user, stockSymbol)
        if((user.stockShareMap[stockSymbol] as Integer) < amountShare) {
            log.info("you don't have enough share")
            return "you don't have enough share"
        }
        Transaction transaction = new Transaction(
                user: user,
                status: TransactionStatusEnum.SELL,
                stockSymbol: stockSymbol,
                quotedPrice: quote.price,
                amount: amountShare
        ).save()
        log.info("User $user.username requested to sell $amountShare\$ shares of $stockSymbol at price $quote.price, Please send COMMIT_SELL to confirm")
        return "User $user.username requested to sell $amountShare\$ shares of $stockSymbol at price $quote.price, Please send COMMIT_SELL to confirm"
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

        BigDecimal sellPriceAmount = transaction.amount.multiply(transaction.quotedPrice, new MathContext(2))
        user.stockShareMap[transaction.stockSymbol] -= transaction.amount
        user.balance = user.balance + sellPriceAmount
        user.save()
        transaction.status = TransactionStatusEnum.COMMIT_SELL
        transaction.save()

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
        transaction.delete()
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
                new BigDecimal("0.00"),
                amount,
                0,
                TriggerStatusEnum.SET_BUY
        ).save()

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

        record.delete()
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

        return " Amount: '$amount' is set for stockSymbol: '$stockSymbol', please also set trigger"
    }

    String setSellTrigger(User user, String stockSymbol, BigDecimal amount){
        def record = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol',stockSymbol
            eq 'status', TriggerStatusEnum.SET_SELL
        } as TransactionTrigger

        if(!TransactionTrigger) return "no record found, please set buy amount first"

        BigDecimal sharesCanSell = record.buySellAmount/amount
        BigDecimal sharesToSell = sharesCanSell.setScale(0, RoundingMode.FLOOR)

        // check user has sufficient shares
        if(dbService.getUserStocks(user.username,stockSymbol)[1] < sharesToSell) return "not enough shares to sell"

        //reserve shares
        //TODO: need to implement another map for reservedshares
        dbService.removeStockShares(user.username,stockSymbol,sharesToSell)

        record.reservedShares = sharesToSell
        record.triggerPrice = amount
        record.status = TriggerStatusEnum.SET_SELL_TRIGGER
        record.save()

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

        record.delete()
        return "set_sell canceled"
    }

}
