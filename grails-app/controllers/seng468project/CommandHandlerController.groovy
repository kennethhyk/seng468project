package seng468project

//import grails.plugin.dropwizard.metrics.timers.Timed

class CommandHandlerController {

    def commandHandlerService
    def dbService

//    @Timed(value='commandhandlerController.index', useClassPrefix = false)
    def index() {
            def res = request.JSON
            String commandString = res.command
            int transactionNum = res.transaction as Integer
            String response = commandHandlerService.commandHandling(commandString, transactionNum)
            render text: "$commandString"
    }

    def addUser(){
        def res = request.JSON
        String userid = res.username
        dbService.addNewUser(res.username, '0.00')
        render text: "Created user $userid"
    }
}
