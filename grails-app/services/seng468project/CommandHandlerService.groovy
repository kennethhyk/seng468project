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
        log.info(command)
        List<String> aCommand = command.split(",")
        CommandBean commandBean = new CommandBean(aCommand)
        return commandBean
    }

    String commandHandling(String command, int transactionNum) {
        String res = ""
        CommandBean commandBean = parseCommandAndCreateCommandBean(command)
        if(commandBean.command == null) {
            System.out.println("Command $command is not recognized")
            log.info("Command $command is not recognized")
        } else if(commandBean.parameterList == null) {
                System.out.println("number of parameters does not match command $commandBean.command, required ${commandBean.command.numberOfParameters}")
                log.info("number of parameters does not match command $commandBean.command, required ${commandBean.command.numberOfParameters}")
            } else {
                User user = User.findByUsername(commandBean.parameterList[0] as String)
                switch(commandBean.command as String) {
                case "ADD":
                    res = dbService.addAmount(commandBean.parameterList[0],commandBean.parameterList[1], transactionNum)
                    break
                case "QUOTE":
                    res = quoteService.getQuote(user, commandBean.parameterList[1], transactionNum)
                    break
                case "BUY":
                    res = transactionService.buy(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
                    break
                case "COMMIT_BUY":
                    res = transactionService.commitBuy(user, transactionNum)
                    break
                case "CANCEL_BUY":
                    res = transactionService.cancelBuy(user, transactionNum)
                    break
                case "SELL":
                    res = transactionService.sell(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
                    break
                case "COMMIT_SELL":
                    res = transactionService.commitSell(user, transactionNum)
                    break
                case "CANCEL_SELL":
                    res = transactionService.cancelSell(user, transactionNum)
                    break
                case "SET_BUY_AMOUNT":
                    res = transactionService.setBuyAmount(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
                    log.debug("this is the SET_BUY_AMOUNT function")
                    break
                case "CANCEL_SET_BUY":
                    res = transactionService.cancelSetBuy(user, commandBean.parameterList[1], transactionNum)
                    log.debug("this is the CANCEL_SET_BUY function")
                    break
                case "SET_BUY_TRIGGER":
                    res = transactionService.setBuyTrigger(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
                    log.debug("this is the SET_BUY_TRIGGER function")
                    break
                case "SET_SELL_AMOUNT":
                    res = transactionService.setSellAmount(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
                    log.debug("this is the SET_SELL_AMOUNT function")
                    break
                case "SET_SELL_TRIGGER":
                    res = transactionService.setSellTrigger(user, commandBean.parameterList[1], new BigDecimal(commandBean.parameterList[2]), transactionNum)
                    log.debug("this is the SET_SELL_TRIGGER function")
                    break
                case "CANCEL_SET_SELL":
                    res = transactionService.cancelSetSell(user, commandBean.parameterList[1], transactionNum)
                    log.debug("this is the CANCEL_SET_SELL function")
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

                    res = auditService.displaySummary(user)
                    break
            }
        }
        return res
    }
}
