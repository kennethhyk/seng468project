package seng468project

import seng468project.beans.CommandBean
import seng468project.beans.UserCommandTypeBean

//import grails.plugin.dropwizard.metrics.timers.Timed

class CommandHandlerController {

    def commandHandlerService
    def dbService


    def index() {
        def res = request.JSON
        String commandString = res.command
        int transactionNum = res.transaction as Integer

        CommandBean commandBean = commandHandlerService.parseCommandAndCreateCommandBean(commandString)
        if(commandBean.command == null) {
            render text: "COMMAND NOT RECOGNIZED"
        } else if(commandBean.parameterList == null) {
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

    def addUser(){
        def res = request.JSON
        String userid = res.username
        dbService.addNewUser(res.username, '0.00')
        render text: "Created user $userid"
    }
}
