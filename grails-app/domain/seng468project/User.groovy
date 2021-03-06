package seng468project

class User {

    String username
    BigDecimal balance
    BigDecimal reservedBalance

    BigDecimal realBalance() {
        return this.balance.subtract(this.reservedBalance)
    }

    static mapping = {
        username indexColumn:[name:'Username_Idx',unique:true]
    }

    @Override
    public String toString() {
        return "User{" +
                ", version=" + version +
//                ", transactionList=" + transactionList +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                ", reservedBalance=" + reservedBalance +
                '}'
    }
}
