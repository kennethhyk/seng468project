package seng468project

class CommandHandlerController {

    def commandHandlerService

    def index() {
        //todo:put the try back
//        try {
            def res = request.JSON
            String commandString = res.command
            log.info("command recieved: $commandString")
            String response = commandHandlerService.commandHandling(commandString)
            render contentType: "application/json", text: "[{'content':'command recieved $commandString', 'res': '$response'}]"
//        }
//        catch(Exception e) {
//            log.error("$e")
//            render "$e"
//        }
    }
}
