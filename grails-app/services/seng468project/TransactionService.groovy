package seng468project

import grails.transaction.Transactional

@Transactional
class TransactionService {
    def quoteService

    def buy() {
        quoteService.getQuote()

    }
}
