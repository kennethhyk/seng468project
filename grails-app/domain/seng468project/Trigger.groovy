package seng468project

import seng468project.enums.TriggerStatusEnum

class Trigger {

    User user
    String stockSymbol
    BigDecimal triggerPrice
    BigDecimal reservedBalance
    TriggerStatusEnum status


    static constraints = {
    }
}
