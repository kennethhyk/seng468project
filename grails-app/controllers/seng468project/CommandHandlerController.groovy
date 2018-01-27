package seng468project

class CommandHandlerController {

    def commandHandlerService
    def dbService

    def index() {
        //todo:put the try back
//        try {
            def res = request.JSON
            String commandString = res.command
            log.info("command recieved: $commandString")
            System.out.println("$commandString")
            String response = commandHandlerService.commandHandling(commandString)
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
