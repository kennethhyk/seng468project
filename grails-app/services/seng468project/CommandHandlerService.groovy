package seng468project

import seng468project.beans.CommandBean
import seng468project.beans.UserCommandTypeBean

//@Transactional
class CommandHandlerService {

    def quoteService
    def transactionService
    def dbService
    def auditService
    static transactional = false

    CommandBean parseCommandAndCreateCommandBean(String command) {
        List<String> aCommand = command.split(",")
        CommandBean commandBean = new CommandBean(aCommand)
        return commandBean
    }
    String commandHandling(String command, int transactionNum) {
        String res = ""
        CommandBean commandBean = parseCommandAndCreateCommandBean(command)
        if(commandBean.command == null) {
            System.out.println("Command $command is not recognized")
        } else if(commandBean.parameterList == null) {
                System.out.println("number of parameters does not match command $commandBean.command, required ${commandBean.command.numberOfParameters}")
            } else {
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = dbService.addAmount(user,commandBean.parameterList[1])
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = quoteService.getQuote(user, commandBean.parameterList[1], transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )

                    res = transactionService.buy(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.commitBuy(user, transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.cancelBuy(user, transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.sell(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.commitSell(user, transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.cancelSell(user, transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.setBuyAmount(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.cancelSetBuy(user, commandBean.parameterList[1], transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.setBuyTrigger(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
//                    log.debug("this is the SET_BUY_TRIGGER function")
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.setSellAmount(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.setSellTrigger(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = transactionService.cancelSetSell(user, commandBean.parameterList[1], transactionNum)
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
                    auditService.dispatch( "zddbuzuoshi", auditService.getUserCommandString(obj) )
                    res = "DUMPLOG DONE!"
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
                    auditService.dispatch( user.username, auditService.getUserCommandString(obj) )
                    res = "RESPONSE OF DISPLAY SUMMARY"
                    break
            }
        }
        return res
    }
}
