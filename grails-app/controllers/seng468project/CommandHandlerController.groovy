package seng468project

class CommandHandlerController {

    def commandHandlerService
    def dbService

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
