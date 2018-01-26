package seng468project

import grails.transaction.Transactional
import seng468project.beans.QuoteServerTypeBean
import seng468project.helpers.CSocket

@Transactional
class QuoteService {

    def auditService

    CSocket client = new CSocket()
    String ipaddress = "192.168.1.152"
    int port = 4445

    def getQuote(User user, String symbol) {
        //todo:change to fit real quote response
        client.start( ipaddress, port )
        String res = client.sendMessage(symbol +"," + user.username)
        client.stop()
        List<String> resList = res.split(",")
        QuoteServerTypeBean record = new QuoteServerTypeBean(234234 as Long, "Zaas", 1, resList[0], resList[1], resList[2], resList[3] as Long, resList[4])
        auditService.auditQuoteServerRecord(record)
        return record
    }
}
