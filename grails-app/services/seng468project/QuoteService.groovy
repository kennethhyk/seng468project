package seng468project

import grails.transaction.Transactional
import seng468project.beans.QuoteServerTypeBean
import seng468project.helpers.CSocket

@Transactional
class QuoteService {

    def auditService

//    CSocket client = new CSocket()
//    int port = 4447

    def getQuote() {
        //todo:change to fit real quote response
//        client.start("quoteserve.seng", port)
//        String res = client.sendMessage("ABC, ZaaS")
//        List<String> resList = res.split(",")

//        QuoteServerTypeBean record = new QuoteServerTypeBean(
//                timestamp: 234234,
//                server: "Zaas",
//                transactionNum: 123,
//                price: resList[1],
//                stockSymbol: resList[0],
//                username: "GG wetwet der",
//                quoteServerTime: 984789479817,
//                cryptoKey: resList[2])
        QuoteServerTypeBean record = new QuoteServerTypeBean(
                 234234,
                 "Zaas",
                 123,
                 "123",
                 "what",
                 "GG wetwet der",
                 98479817,
                 "hey")
//        auditService.auditQuoteServerRecord(record)
        return record
    }
}
