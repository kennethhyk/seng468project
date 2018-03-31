package seng468project

import seng468project.enums.TransactionStatusEnum

class StockTransaction {

    static hasOne = [user:User]
    TransactionStatusEnum status
    String stockSymbol
    BigDecimal quotedPrice
    BigDecimal amount
    Long dateCreated
    Long lastUpdated

    static mapping = {
        dateCreated indexColumn:[name:'DateCreated_Idx']
        status indexColumn:[name:'Status_Idx']
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", user=" + user.username+
                ", status=" + status +
                ", stockSymbol='" + stockSymbol + '\'' +
                ", quotedPrice=" + quotedPrice +
                ", amount=" + amount +
                ", dateCreated=" + dateCreated +
                ", lastUpdated=" + lastUpdated +
                '}'
    }
}
