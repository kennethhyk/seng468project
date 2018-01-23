package seng468project

import grails.transaction.Transactional
import seng468project.beans.QuoteServerTypeBean
import seng468project.enums.TransactionStatusEnum

import java.math.MathContext
import java.sql.Timestamp

@Transactional
class TransactionService {
    def quoteService

    String buy(User user, String stockSymbol, BigDecimal amountPrice) {
        QuoteServerTypeBean quote = quoteService.getQuote() //todo: get quote using stock symbol
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
        Date date = new Date()
        Long sixtySecondsAgo = new Timestamp(date.getTime()).getTime() - 60
        def test = Transaction.createCriteria().get {
            projections {
                max("dateCreated")
                eq('status', TransactionStatusEnum.BUY)
                eq('user', user)
            }
        }

        def transaction = Transaction.createCriteria().get {
            eq('dateCreated', test )
        } as Transaction

        log.debug(transaction.dateCreated as String)
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
        def test = Transaction.createCriteria().get {
            projections {
                max("dateCreated")
                eq('status', TransactionStatusEnum.BUY)
                eq('user', user)
            }
        }
        def transaction = Transaction.createCriteria().get {
            eq('dateCreated', test )
        } as Transaction
        transaction.status = TransactionStatusEnum.CANCEL_BUY
    }
}
