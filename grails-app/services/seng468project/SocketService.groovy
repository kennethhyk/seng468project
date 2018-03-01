package seng468project

import grails.transaction.Transactional
import seng468project.helpers.CSocket

@Transactional
class SocketService {

    CSocket client = new CSocket()
    String ipaddress = "192.168.1.152"
    int port = 4447

    def quoteGetQueue(msg) {
        String res
        client.start( ipaddress, port )
        res = client.sendMessage(msg)
        client.stop()

        return res
    }
}
