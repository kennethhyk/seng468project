package seng468project

import grails.transaction.Transactional
import seng468project.beans.CommandBean

@Transactional
class CommandHandlerService {

    CommandBean parseCommandAndCreateCommandBean(String command) {
        List<String> aCommand = command.split(",")
        CommandBean commandBean = new CommandBean(aCommand)
        return commandBean
    }

    def commandHandling(String command) {
        CommandBean commandBean = parseCommandAndCreateCommandBean(command)
        if(commandBean.command == null) {
            log.info("Command $command is not recognized")
        } else if(commandBean.parameterList == null) {
                log.info("number of parameters does not match command $commandBean.command, required ${commandBean.command.numberOfParameters}")
            } else {
                switch(commandBean.command as String) {
                case "ADD":
                    log.debug("this is the ADD function")
                    break
                case "QUOTE":
                    log.debug("this is the QUOTE function")
                    break
                case "BUY":
                    log.debug("this is the BUY function")
                    break
                case "COMMIT_BUY":
                    log.debug("this is the COMMIT_BUY function")
                    break
                case "CANCEL_BUY":
                    log.debug("this is the CANCEL_BUY function")
                    break
                case "SELL":
                    log.debug("this is the SELL function")
                    break
                case "COMMIT_SELL":
                    log.debug("this is the COMMIT_SELL function")
                    break
                case "CANCEL_SELL":
                    log.debug("this is the CANCEL_SELL function")
                    break
                case "SET_BUY_AMOUNT":
                    log.debug("this is the SET_BUY_AMOUNT function")
                    break
                case "CANCEL":
                    log.debug("this is the CANCEL function")
                    break
                case "SET_BUY_TRIGGER":
                    log.debug("this is the SET_BUY_TRIGGER function")
                    break
                case "SET_SELL_AMOUNT":
                    log.debug("this is the SET_SELL_AMOUNT function")
                    break
                case "SET_SELL_TRIGGER":
                    log.debug("this is the SET_SELL_TRIGGER function")
                    break
                case "CANCEL_SET_SELL":
                    log.debug("this is the CANCEL_SET_SELL function")
                    break
                case "DUMPLOG":
                    log.debug("this is the DUMPLOG function")
                    break
                case "DISPLAY_SUMMARY":
                    log.debug("this is the DISPLAY_SUMMARY function")
                    break
            }
        }
    }
}
