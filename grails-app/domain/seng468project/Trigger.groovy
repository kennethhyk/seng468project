package seng468project

import seng468project.enums.TriggerStatusEnum

class Trigger {

    User user
    String stockSymbol
    BigDecimal triggerPrice
    BigDecimal reservedBalance
    BigDecimal sellingAmount
    Integer reservedShares
    TriggerStatusEnum status


    static constraints = {
    }

    Trigger(User user, String stockSymbol, BigDecimal triggerPrice, BigDecimal reservedBalance, BigDecimal sellingAmount, Integer reservedShares, TriggerStatusEnum status) {
        this.user = user
        this.stockSymbol = stockSymbol
        this.triggerPrice = triggerPrice
        this.reservedBalance = reservedBalance
        this.sellingAmount = sellingAmount
        this.reservedShares = reservedShares
        this.status = status
    }
}
