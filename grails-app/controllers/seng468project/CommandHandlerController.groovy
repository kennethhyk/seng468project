package seng468project

class CommandHandlerController {

    def commandHandlerService
    def dbService

    def index() {
        //todo:put the try back
//        try {
            def res = request.JSON
            String commandString = res.command
            int transactionNum = res.transaction as Integer
            String response = commandHandlerService.commandHandling(commandString, transactionNum)
            render contentType: "application/json", text: "[{'content':'command recieved $commandString', 'res': '$response'}]"
//        }
//        catch(Exception e) {
//            log.error("$e")
//            render "$e"
//        }
    }

    def addUser(){
        def res = request.JSON
        String userid = res.username
        log.info("command recieved user: $userid")
        dbService.addNewUser(res.username, '0.00')
        render contentType: "application/json", text: "user Table initilizes"
    }
}
