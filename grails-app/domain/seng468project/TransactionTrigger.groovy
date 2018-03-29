package seng468project

import seng468project.enums.TriggerStatusEnum

class TransactionTrigger {

    User user
    String stockSymbol
    BigDecimal triggerPrice
    BigDecimal reservedBalance
    BigDecimal buySellAmount
    Integer reservedShares
    TriggerStatusEnum status


    static mapping = {
        stockSymbol indexColumn:[name:'StockSymbol_Idx']
        status indexColumn:[name:'Status_Idx']
    }

    TransactionTrigger(User user, String stockSymbol, BigDecimal triggerPrice, BigDecimal reservedBalance, BigDecimal sellingAmount, Integer reservedShares, TriggerStatusEnum status) {
        this.user = user
        this.stockSymbol = stockSymbol
        this.triggerPrice = triggerPrice
        this.reservedBalance = reservedBalance
        this.buySellAmount = sellingAmount
        this.reservedShares = reservedShares
        this.status = status
    }

    @Override
    public String toString() {
        return "TransactionTrigger{" +
                "user=" + user +
                ", stockSymbol='" + stockSymbol + '\'' +
                ", triggerPrice=" + triggerPrice +
                ", reservedBalance=" + reservedBalance +
                ", buySellAmount=" + buySellAmount +
                ", reservedShares=" + reservedShares +
                ", status=" + status +
                '}';
    }
}
