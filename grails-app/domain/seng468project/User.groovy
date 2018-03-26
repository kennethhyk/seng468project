package seng468project

class User {

    String username
    BigDecimal balance
    BigDecimal reservedBalance
    Map stockShareMap
    static hasMany = [transactionList: Transaction]

    BigDecimal realBalance() {
        return this.balance.subtract(this.reservedBalance)
    }

    static mapping = {
        id column: "username", name: "username", type: "string", generator: "assigned"
    }

    @Override
    public String toString() {
        return "User{" +
                ", version=" + version +
                ", transactionList=" + transactionList +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                ", reservedBalance=" + reservedBalance +
                '}'
    }
}
