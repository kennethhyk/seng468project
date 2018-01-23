package seng468project

import grails.transaction.Transactional

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import seng468project.beans.QuoteServerTypeBean
import seng468project.beans.AccountTransactionTypeBean
import seng468project.beans.UserCommandTypeBean

@Transactional
class AuditService {


    String header = "<?xml version=\"1.0\">\n\n "
    Path out_path = Paths.get("./logFile.xml")
    BufferedWriter writer = Files.newBufferedWriter(out_path)


    def serviceMethod() {

    }

    def audit(){

    }

    def auditUserCommand(UserCommandTypeBean obj){
        BufferedWriter writer = Files.newBufferedWriter(out_path)
        writer.write(header)
        writer.write("<UserCommandType>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <command>" + obj.command+ "</command>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "   <filename>" + obj.filename+ "</filename>\n" +
                "   <funds>" + obj.funds+ "</funds>\n" +
                "</UserCommandType>")
        writer.close()
    }

    def auditQuoteServerRecord(QuoteServerTypeBean obj){
        BufferedWriter writer = Files.newBufferedWriter(out_path)
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

    def auditAccountTransaction(AccountTransactionTypeBean obj){
        BufferedWriter writer = Files.newBufferedWriter(out_path)
        writer.write(header)
        writer.write("<AccountTransactionType>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <funds>" + obj.funds+ "</funds>\n" +
                "</AccountTransactionType>")
        writer.close()
    }

    def auditSystemEvents(UserCommandTypeBean obj){
        BufferedWriter writer = Files.newBufferedWriter(out_path)
        writer.write(header)
        writer.write("<SystemEventType>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <command>" + obj.command+ "</command>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "   <filename>" + obj.filename+ "</filename>\n" +
                "   <funds>" + obj.funds+ "</funds>\n" +
                "</SystemEventType>")
        writer.close()
    }

    def auditErrorEvents(UserCommandTypeBean obj, String err_msg){
        BufferedWriter writer = Files.newBufferedWriter(out_path)
        writer.write(header)
        writer.write("<ErrorEventType>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <command>" + obj.command+ "</command>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "   <filename>" + obj.filename+ "</filename>\n" +
                "   <funds>" + obj.funds+ "</funds>\n" +
                "   <errorMessage>" + err_msg+ "</errorMessage>" +
                "</ErrorEventType>")
        writer.close()
    }

    def auditDebug(UserCommandTypeBean obj, String dbg_msg){
        BufferedWriter writer = Files.newBufferedWriter(out_path)
        writer.write(header)
        writer.write("<DebugType>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <command>" + obj.command+ "</command>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "   <filename>" + obj.filename+ "</filename>\n" +
                "   <funds>" + obj.funds+ "</funds>\n" +
                "   <debugMessage>" + dbg_msg+ "</debugMessage>" +
                "</DebugType>")
        writer.close()
    }

    def finishedLogging(){
        writer.close()
    }
}
