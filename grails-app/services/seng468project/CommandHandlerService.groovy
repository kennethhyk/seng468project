package seng468project

import grails.transaction.Transactional
import seng468project.beans.CommandBean
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
        User user = User.findByUsername(commandBean.parameterList[0] as String)
        if(!user) {
            return "no such user error"
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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                dbService.addAmount(user, commandBean.parameterList[1])
                return "added"
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (quoteService.getQuote(user, commandBean.parameterList[1], transactionNum))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))

                return (transactionService.buy(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.commitBuy(user))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.cancelBuy(user))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.sell(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.commitSell(user))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.cancelSell(user))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.setBuyAmount(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.cancelSetBuy(user, commandBean.parameterList[1]))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.setBuyTrigger(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2])))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.setSellAmount(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2])))
                break

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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.setSellTrigger(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2])))
                break
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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return (transactionService.cancelSetSell(user, commandBean.parameterList[1]))
                break

            case "DUMPLOG":
                UserCommandTypeBean obj = new UserCommandTypeBean(
                        System.currentTimeMillis(),
                        "TRANSACTION SERVER: ZaaS",
                        transactionNum,
                        "DUMPLOG",
                        "zddbuzuoshi",
                        "",
                        commandBean.parameterList[0],
                        "0.00"
                )
                auditService.dispatch("zddbuzuoshi", auditService.getUserCommandString(obj))
                return "DUMPLOG DONE!"
                break
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
                auditService.dispatch(user.username, auditService.getUserCommandString(obj))
                return "RESPONSE OF DISPLAY SUMMARY"
                break
        }

    }
}
