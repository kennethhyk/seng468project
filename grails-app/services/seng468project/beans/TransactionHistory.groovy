package seng468project.beans

import seng468project.enums.CommandEnum

import java.sql.Timestamp

class TransactionHistory {
    Timestamp   commandStartTime
    Timestamp   quoteServerTime
    Integer     transactionNum
    CommandEnum command
    String      userid
    String      stockSymbol
    BigDecimal  price
    Integer     shares
    String      cryptoKey

}
