package seng468project

import seng468project.beans.CommandBean
import seng468project.beans.UserCommandTypeBean


class CommandHandlerController {

    def commandHandlerService
    def dbService
    def auditService

    def index() {
        def res = request.JSON
        String commandString = res.command
        int transactionNum = res.transaction as Integer

        CommandBean commandBean = commandHandlerService.parseCommandAndCreateCommandBean(commandString)
        if(commandBean.command == null) {
            render text: "COMMAND NOT RECOGNIZED"
        } else if(commandBean.parameterList == null) {
            UserCommandTypeBean obj = new UserCommandTypeBean(
                    System.currentTimeMillis(),
                    "TRANSACTION SERVER: ZaaS",
                    transactionNum,
                    commandBean.command as String,
                    commandBean.parameterList[0],
                    "",
                    "",
                    ""
            )
            auditService.dispatch(commandBean.parameterList[0], auditService.getErrorEventString(obj,"number of parameters not match"))
            render text:"number of parameters does not match command $commandBean.command, required ${commandBean.command.numberOfParameters}"
        } else {
            try {
                String response = commandHandlerService.commandHandling(commandBean, transactionNum)
            } catch (Exception e) {
                System.out.println(e)
            }
            render text: "$response"
        }
    }

    def front(){

    }

    def addUser(){
        def res = request.JSON
        String userid = res.username
        dbService.addNewUser(res.username, '0.00')
        render text: "Created user $userid"
    }
}
