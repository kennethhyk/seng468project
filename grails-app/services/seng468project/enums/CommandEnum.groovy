package seng468project.enums

/**
 * Created by kenneth on 2018-01-17.
 */
enum CommandEnum {
    ADD(2),
    QUOTE(2),
    BUY(3),
    COMMIT_BUY(1),
    CANCEL_BUY(1),
    SELL(3),
    COMMIT_SELL(1),
    CANCEL_SELL(1),
    SET_BUY_AMOUNT(3),
    CANCEL_SET_BUY(2),
    SET_BUY_TRIGGER(3),
    SET_SELL_AMOUNT(3),
    SET_SELL_TRIGGER(3),
    CANCEL_SET_SELL(2),
    DUMPLOG(2),
    DISPLAY_SUMMARY(1)

    private int numberOfParameters
    CommandEnum(int numberOfParameters) {
        this.numberOfParameters = numberOfParameters
    }

    int getNumberOfParameters() {
        return numberOfParameters
    }
}