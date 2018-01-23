package seng468project

import seng468project.enums.TransactionStatusEnum

class Transaction {

    User user
    TransactionStatusEnum status
    String stockSymbol
    BigDecimal quotedPrice


    static constraints = {
    }
}
