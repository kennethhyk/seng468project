package seng468project

import grails.transaction.Transactional

import seng468project.beans.QuoteServerTypeBean

@Transactional
class AuditService {


    String header = "<?xml version=\"1.0\">\n\n "
    BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))

    def audit(){

    }

    def auditUserCommand(Long timestamp, String server, Integer transactionNum, String command, String username, String stockSymbol, String filename,String funds){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
        writer.write(header)
        writer.write("<UserCommandType>\n" +
                "   <timestamp>" + timestamp+ "</timestamp>\n" +
                "   <server>" + server+ "</server>\n" +
                "   <transactionNum>" + transactionNum+ "</transactionNum>\n" +
                "   <command>" + command+ "</command>\n" +
                "   <username>" + username+ "</username>\n" +
                "   <stockSymbol>" + stockSymbol+ "</stockSymbol>\n" +
                "   <filename>" + filename+ "</filename>\n" +
                "   <funds>" + funds+ "</funds>\n" +
        "</UserCommandType>")
        writer.close()
    }

    def auditQuoteServerRecord(QuoteServerTypeBean obj){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
        writer.write(header)
        writer.write("<QuoteServerType>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <price>" + obj.price+ "</price>\n" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <quoteServerTime>" + obj.quoteServerTime+ "</quoteServerTime>\n" +
                "   <cryptokey>" + obj.cryptoKey+ "</cryptokey>\n" +
                "</QuoteServerType>")
        writer.close()
    }

    def finishedLogging(){
        writer.close()
    }
}
