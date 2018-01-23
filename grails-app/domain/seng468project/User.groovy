package seng468project

class User {

    String username
    BigDecimal balance
    BigDecimal reservedBalance
    Map stockShareMap
    static hasMany = [transactionList: Transaction]

    BigDecimal realBalance() {
        return this.balance - this.reservedBalance
    }

    static constraints = {
        username unique: true
    }
}
