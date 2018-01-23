package seng468project

import seng468project.helpers.CSocket
import seng468project.helpers.SSocket

class SocketTestController {
    SSocket server = new SSocket()
    CSocket client = new CSocket()
    int port = 5558

    def server() {
        log.debug("server started")
        if(server.start(port)) {
            log.debug("started on 4448")
            server.echo()
        }
        render ""
    }

    def client() {
        client.start("127.0.0.1", port)
        log.debug("client start sending message")
        render ""
    }

    def serverStop() {
        server.stop()
        render ""
    }

    def clientStop() {
        client.stop()
        render ""
    }

    def send() {
        log.info(client.sendMessage("1"))
        log.info(client.sendMessage("2"))
        log.info(client.sendMessage("3"))
        log.info(client.sendMessage("4"))
        log.info(client.sendMessage("5"))
        render ""
    }


}
