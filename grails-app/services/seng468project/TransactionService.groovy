package seng468project

import grails.transaction.Transactional
import seng468project.beans.QuoteServerTypeBean

@Transactional
class TransactionService {
    def quoteService

    String buy(User user, String stockSymbol, BigDecimal amountPrice) {
        QuoteServerTypeBean quote = quoteService.getQuote()
        if(user.realBalance() < quote.price){
           return "you don't have enough money"
        }
        return "success!"
    }
}
