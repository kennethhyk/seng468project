package seng468project

import grails.transaction.Transactional
import seng468project.beans.CommandBean
import seng468project.beans.QuoteServerTypeBean
import seng468project.beans.UserCommandTypeBean

@Transactional
class CommandHandlerService {

    def quoteService
    def transactionService
    def dbService
    def auditService
//    static transactional = false

    CommandBean parseCommandAndCreateCommandBean(String command) {
        List<String> aCommand = command.split(",")
        CommandBean commandBean = new CommandBean(aCommand)
        return commandBean
    }
    String commandHandling(CommandBean commandBean, int transactionNum) {

        User user = null
        if(commandBean.command as String != "DUMPLOG"){
            user = User.findByUsername(commandBean.parameterList[0] as String)
            if(!user) {
                return "no such user error"
            }
        }

        switch(commandBean.command as String) {
            case "ADD":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "ADD",
                        user.username,
                        "",
                        "",
                        commandBean.parameterList[1]
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (dbService.addAmount(user, commandBean.parameterList[1]))

            case "QUOTE":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "QUOTE",
                        user.username,
                        commandBean.parameterList[1],
                        "",
                        "0.00"
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                QuoteServerTypeBean quoteServerTypeBean = quoteService.getQuote(user, commandBean.parameterList[1], transactionNum)
                return "$quoteServerTypeBean.stockSymbol, $quoteServerTypeBean.price"

            case "BUY":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "BUY",
                        user.username,
                        commandBean.parameterList[1],
                        "",
                        commandBean.parameterList[2]
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))

                return (transactionService.buy(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum))

            case "COMMIT_BUY":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "COMMIT_BUY",
                        user.username,
                        "",
                        "",
                        "0.00"
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.commitBuy(user))

            case "CANCEL_BUY":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "CANCEL_BUY",
                        user.username,
                        "",
                        "",
                        "0.00"
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.cancelBuy(user))

            case "SELL":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "SELL",
                        user.username,
                        "",
                        "",
                        "0.00"
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.sell(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum))

            case "COMMIT_SELL":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "COMMIT_SELL",
                        user.username,
                        "",
                        "",
                        "0.00"
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.commitSell(user))

            case "CANCEL_SELL":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "CANCEL_SELL",
                        user.username,
                        "",
                        "",
                        "0.00"
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.cancelSell(user))

            case "SET_BUY_AMOUNT":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "SET_BUY_AMOUNT",
                        user.username,
                        commandBean.parameterList[1],
                        "",
                        commandBean.parameterList[2]
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.setBuyAmount(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum))

            case "CANCEL_SET_BUY":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "CANCEL_SET_BUY",
                        user.username,
                        commandBean.parameterList[1],
                        "",
                        "0.00"
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.cancelSetBuy(user, commandBean.parameterList[1]))

            case "SET_BUY_TRIGGER":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "SET_BUY_TRIGGER",
                        user.username,
                        commandBean.parameterList[1],
                        "",
                        commandBean.parameterList[2]
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.setBuyTrigger(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2])))

            case "SET_SELL_AMOUNT":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "SET_SELL_AMOUNT",
                        user.username,
                        commandBean.parameterList[1],
                        "",
                        commandBean.parameterList[2]
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.setSellAmount(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2])))

            case "SET_SELL_TRIGGER":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "SET_SELL_TRIGGER",
                        user.username,
                        commandBean.parameterList[1],
                        "",
                        commandBean.parameterList[2]
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.setSellTrigger(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2])))
            case "CANCEL_SET_SELL":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "SET_SELL_TRIGGER",
                        user.username,
                        commandBean.parameterList[1],
                        "",
                        "0.00"
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                return (transactionService.cancelSetSell(user, commandBean.parameterList[1]))

            case "DUMPLOG":
                String filename = commandBean.parameterList[0]
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "DUMPLOG",
                        "zddbuzuoshi",
                        "",
                        filename,
                        "0.00"
                )
                auditService.dispatch("zddbuzuoshi", auditService.getUserCommandString(obj))
                return auditService.dumpLog(filename)
            case "DISPLAY_SUMMARY":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "DISPLAY_SUMMARY",
                        user.username,
                        "",
                        "",
                        "0.00"
                )
                auditService.dispatch( "transaction" + Integer.toString(transactionNum), auditService.getUserCommandString(obj))
                String summary = auditService.displaySummary(user)
                return summary
        }

        return "ERROR: command not handled!"
    }
}
