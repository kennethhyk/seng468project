package seng468project

import java.sql.Timestamp

class Transaction {

    Users       user
    String      action
    String      stockSymbol
    Float       price
    Integer     shares
    Boolean     status
    String      note
    Timestamp   time


    static constraints = {
    }
}
