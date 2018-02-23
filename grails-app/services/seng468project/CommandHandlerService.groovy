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

    CommandBean parseCommandAndCreateCommandBean(String command) {
//        log.info(command)
        List<String> aCommand = command.split(",")
        CommandBean commandBean = new CommandBean(aCommand)
        return commandBean
    }

    String commandHandling(String command, int transactionNum) {
        String res = ""
        CommandBean commandBean = parseCommandAndCreateCommandBean(command)
        if(commandBean.command == null) {
            System.out.println("Command $command is not recognized")
//            log.info("Command $command is not recognized")
        } else if(commandBean.parameterList == null) {
                System.out.println("number of parameters does not match command $commandBean.command, required ${commandBean.command.numberOfParameters}")
//                log.info("number of parameters does not match command $commandBean.command, required ${commandBean.command.numberOfParameters}")
            } else {
                User user = User.findByUsername(commandBean.parameterList[0] as String)
                switch(commandBean.command as String) {
                case "ADD":
                    UserCommandTypeBean obj = new UserCommandTypeBean(
                            System.currentTimeMillis(),
                            "TRANSACTION SERVER: ZaaS",
                            transactionNum,
                            "ADD",
                            commandBean.parameterList[0],
                            "",
                            "",
                            commandBean.parameterList[1]
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = dbService.addAmount(commandBean.parameterList[0],commandBean.parameterList[1], transactionNum)
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
                    // get the corresponding formatted XML block
                    String str = auditService.getUserCommandString(obj)
                    // save to db
                    new LogHistory(User.get(1), str).save()
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
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.buy(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum,obj)
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
                            ""
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.commitBuy(user, transactionNum,obj)
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
                            ""
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.cancelBuy(user, transactionNum,obj)
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
                            ""
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.sell(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum,obj)
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
                            ""
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.commitSell(user, transactionNum,obj)
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
                            ""
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.cancelSell(user, transactionNum,obj)
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
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.setBuyAmount(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum,obj)
                    break

                case "CANCEL_SET_BUY":
                    UserCommandTypeBean obj = new UserCommandTypeBean(
                            System.currentTimeMillis(),
                            "TRANSACTION SERVER: ZaaS",
                            transactionNum,
                            "CANCEL_SET_BUY",
                            commandBean.parameterList[0],
                            commandBean.parameterList[1],
                            "",
                            ""
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.cancelSetBuy(user, commandBean.parameterList[1], transactionNum,obj)
//                    log.debug("this is the CANCEL_SET_BUY function")
                    break

                case "SET_BUY_TRIGGER":
                    UserCommandTypeBean obj = new UserCommandTypeBean(
                            System.currentTimeMillis(),
                            "TRANSACTION SERVER: ZaaS",
                            transactionNum,
                            "SET_BUY_TRIGGER",
                            commandBean.parameterList[0],
                            commandBean.parameterList[1],
                            "",
                            commandBean.parameterList[2]
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.setBuyTrigger(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum,obj)
//                    log.debug("this is the SET_BUY_TRIGGER function")
                    break

                case "SET_SELL_AMOUNT":
                    UserCommandTypeBean obj = new UserCommandTypeBean(
                            System.currentTimeMillis(),
                            "TRANSACTION SERVER: ZaaS",
                            transactionNum,
                            "SET_SELL_AMOUNT",
                            commandBean.parameterList[0],
                            commandBean.parameterList[1],
                            "",
                            commandBean.parameterList[2]
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.setSellAmount(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum,obj)
                    break

                case "SET_SELL_TRIGGER":
                    UserCommandTypeBean obj = new UserCommandTypeBean(
                            System.currentTimeMillis(),
                            "TRANSACTION SERVER: ZaaS",
                            transactionNum,
                            "SET_SELL_TRIGGER",
                            commandBean.parameterList[0],
                            commandBean.parameterList[1],
                            "",
                            commandBean.parameterList[2]
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.setSellTrigger(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum,obj)
                    break
                case "CANCEL_SET_SELL":
                    UserCommandTypeBean obj = new UserCommandTypeBean(
                            System.currentTimeMillis(),
                            "TRANSACTION SERVER: ZaaS",
                            transactionNum,
                            "SET_SELL_TRIGGER",
                            commandBean.parameterList[0],
                            commandBean.parameterList[1],
                            "",
                            ""
                    )
                    String str = auditService.getUserCommandString(obj)
                    new LogHistory(user, str).save()
                    res = transactionService.cancelSetSell(user, commandBean.parameterList[1], transactionNum,obj)
                    break

                case "DUMPLOG":
                    UserCommandTypeBean obj = new UserCommandTypeBean(
                            System.currentTimeMillis(),
                            "TRANSACTION SERVER: ZaaS",
                            transactionNum,
                            "DUMPLOG",
                            "",
                            "",
                            commandBean.parameterList[0],
                            "0.00"
                    )
                    // get the corresponding formatted XML block
                    String str = auditService.getUserCommandString(obj)
                    // save to db
                    new LogHistory(User.get(1), str).save()
                    res = auditService.dumpLog(commandBean.parameterList[0])
                    res = "DUMPLOG DONE!"
                    break
                case "DISPLAY_SUMMARY":
                    UserCommandTypeBean obj = new UserCommandTypeBean(
                            System.currentTimeMillis(),
                            "TRANSACTION SERVER: ZaaS",
                            transactionNum,
                            "DISPLAY_SUMMARY",
                            commandBean.parameterList[0],
                            "",
                            "",
                            "0.00"
                    )
                    // get the corresponding formatted XML block
                    String str = auditService.getUserCommandString(obj)
                    // save to db
                    new LogHistory(user, str).save()

                    res = "RESPONSE OF DISPLAY SUMMARY"
                    break
            }
        }
        return res
    }
}
