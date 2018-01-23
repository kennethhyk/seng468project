package seng468project

import grails.transaction.Transactional
import seng468project.beans.QuoteServerTypeBean
import seng468project.enums.TransactionStatusEnum

@Transactional
class TransactionService {
    def quoteService

    String buy(User user, String stockSymbol, BigDecimal amountPrice) {
        QuoteServerTypeBean quote = quoteService.getQuote() //todo: get quote using stock symbol
        if(user.realBalance() < quote.price) {
            return "you don't have enough money"
        }
        Transaction transaction = new Transaction(
                user: user,
                status: TransactionStatusEnum.BUY,
                stockSymbol: stockSymbol,
                quotedPrice: amountPrice
        ).save()
        return "User $user.username requested to purchase $amountPrice\$ value of stock $stockSymbol at price $quote.price, Please send COMMIT_BUY to confirm"
    }

    String commitBuy(User user) {
        Transaction transaction = User.createCriteria().get {
            eq('user', user.username)
            projections {
                properties ''
            }
        }
    }
}
