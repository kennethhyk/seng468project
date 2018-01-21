package seng468project.beans

class AccountTransactionTypeBean {
    Long    timestamp
    String  server
    Integer transactionNum
    String  action
    String  username
    String  funds

    AccountTransactionTypeBean(Long timestamp, String server, Integer transactionNum, String action, String username, String funds) {
        this.timestamp = timestamp
        this.server = server
        this.transactionNum = transactionNum
        this.action = action
        this.username = username
        this.funds = funds
    }

    @Override
    public String toString() {
        return "AccountTransactionTypeBean{" +
                "timestamp=" + timestamp +
                ", server='" + server + '\'' +
                ", transactionNum=" + transactionNum +
                ", action='" + action + '\'' +
                ", username='" + username + '\'' +
                ", funds='" + funds + '\'' +
                '}';
    }
}
