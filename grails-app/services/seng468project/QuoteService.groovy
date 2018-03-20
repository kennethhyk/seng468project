package seng468project

import grails.transaction.Transactional
import seng468project.beans.QuoteServerTypeBean
import seng468project.helpers.CSocket
import seng468project.helpers.JedisDB

import java.sql.Timestamp

@Transactional
class QuoteService {

    def auditService

    def getQuote(User user, String symbol, int transactionNum) {
        //todo:change to fit real quote response
        JedisDB jedis = new JedisDB()
        CSocket client = new CSocket()
        String ipaddress = "192.168.1.152"
        int port = 4447
        Boolean test = true
        String res

        QuoteServerTypeBean record
        if(!test){
            if(!jedis.lookupEntry(symbol)){
                client.start( ipaddress, port )
                res = client.sendMessage(symbol +"," + user.username)
                client.stop()
                jedis.addNewEntry(symbol, res)
                List<String> resList = res.split(",")
                record = new QuoteServerTypeBean(System.currentTimeMillis(), "quoteserve.seng:"+ (port as String), transactionNum, resList[0], resList[1], resList[2], resList[3] as Long, resList[4])
                auditService.dispatch( user.username, auditService.getQuoteServerString(record) )
            }else{
                res = jedis.retrieveValue(symbol)
                List<String> resList = res.split(",")
                record = new QuoteServerTypeBean(System.currentTimeMillis(), "quoteserve.seng:"+ (port as String), transactionNum, resList[0], resList[1], resList[2], resList[3] as Long, resList[4])
            }

        }else{
            record = new QuoteServerTypeBean(
                System.currentTimeMillis(),
                "quoteserve.seng:"+ (port as String),
                transactionNum,
                "21.4",
                symbol,
                user.username,
                123192 as Long,
                "this is the cryptokey"
            )
            String str = auditService.getQuoteServerString(record)
        }

        return record
    }
}
