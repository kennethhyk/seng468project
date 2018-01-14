package seng468project

import java.sql.Timestamp

class Transactions {

    String      userId
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
