package seng468project.beans

class UserCommandTypeBean {
    Long    timestamp
    String  server
    Integer transactionNum
    String  command
    String  username
    String  stockSymbol
    String  filename
    String  funds

    UserCommandTypeBean(Long timestamp, String server, Integer transactionNum, String command, String username, String stockSymbol, String filename, String funds) {
        this.timestamp = timestamp
        this.server = server
        this.transactionNum = transactionNum
        this.command = command
        this.username = username
        this.stockSymbol = stockSymbol
        this.filename = filename
        this.funds = funds
    }

    @Override
    public String toString() {
        return "UserCommandTypeBean{" +
                "timestamp=" + timestamp +
                ", server='" + server + '\'' +
                ", transactionNum=" + transactionNum +
                ", command='" + command + '\'' +
                ", username='" + username + '\'' +
                ", stockSymbol='" + stockSymbol + '\'' +
                ", filename='" + filename + '\'' +
                ", funds='" + funds + '\'' +
                '}';
    }
}
