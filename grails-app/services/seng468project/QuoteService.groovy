package seng468project

import grails.transaction.Transactional
import seng468project.beans.QuoteServerTypeBean
import seng468project.helpers.CSocket

import java.sql.Timestamp

@Transactional
class QuoteService {

    def auditService

    CSocket client = new CSocket()
    String ipaddress = "192.168.1.152"
    int port = 4445
    Boolean test = false

    def getQuote(User user, String symbol) {
        //todo:change to fit real quote response
        QuoteServerTypeBean record
        if(!test){
            client.start( ipaddress, port )
            String res = client.sendMessage(symbol +"," + user.username)
            client.stop()
            List<String> resList = res.split(",")
            record = new QuoteServerTypeBean(System.currentTimeMillis(), "Zaas", 1, resList[0], resList[1], resList[2], resList[3] as Long, resList[4])

            String str = auditService.getQuoteServerString(record)
            new LogHistory(user,str).save()
        }else{
            record = new QuoteServerTypeBean(
                System.currentTimeMillis(),
                "test_server",
                1,
                "21.4",
                symbol,
                user.username,
                123192 as Long,
                "jslif)FDJ*e8f this is the cryptokey"
            )
            String str = auditService.getQuoteServerString(record)
            new LogHistory(user,str).save()
        }

        return record
    }
}
