package seng468project

import seng468project.enums.TransactionStatusEnum

class Transaction {

    static hasOne = [user:User]
    TransactionStatusEnum status
    String stockSymbol
    BigDecimal quotedPrice
    BigDecimal amount

    static constraints = {
    }

    static mapping = {
        dateCreated
        lastUpdated
    }
}
