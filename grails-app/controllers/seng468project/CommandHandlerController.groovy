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
        String resp = ""
        CommandBean commandBean = commandHandlerService.parseCommandAndCreateCommandBean(commandString)
        if(commandBean.command == null) {
            resp = "COMMAND NOT RECOGNIZED"
            render status:200, contentType: "text/json", text: "[{\"response\": \"$resp\"}]"
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
            auditService.dispatch("transaction" + Integer.toString(transactionNum), auditService.getErrorEventString(obj,"number of parameters not match"))
            resp = "number of parameters does not match command $commandBean.command, required ${commandBean.command.numberOfParameters}"
            render status:200, contentType: "text/json", text: "[{\"response\": \"$resp\"}]"
        } else {
            try {
                resp = commandHandlerService.commandHandling(commandBean, transactionNum)
            } catch (Exception e) {
                System.out.println(e)
            }
            render status:200, contentType: "text/json", text: "[{\"response\": \"$resp\"}]"
        }
    }

    def addUser(){
        def res = request.JSON
        String userid = res.username
        dbService.addNewUser(res.username, '0.00')
        render status:200, contentType: "text/json", text: "[{\"response\": \"Created user $userid\"}]"
    }

    def front(){ }
}
