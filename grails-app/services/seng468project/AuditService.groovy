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


    String header = "<?xml version=\"1.0\"?>\n" +
            "<log>\n"
    String footer = "\n</log>"

    String dumpLog(String username=null, String filename){
        def records
        if(username){
            def user = User.createCriteria().get{
                eq 'username',username
            } as User

            records = LogHistory.createCriteria().list {
                eq 'user',user
            } as ArrayList<LogHistory>
        }else{
            records = LogHistory.createCriteria().list {} as ArrayList<LogHistory>
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename))
        writer.write(header)

        records.each{
            writer.write(it.xmlBlock)
        }
        writer.write(footer)
        writer.close()
        return "done"
    }

    def auditUserCommand(UserCommandTypeBean obj){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
        writer.write(header)
        writer.write("<userCommandType>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <command>" + obj.command+ "</command>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "   <filename>" + obj.filename+ "</filename>\n" +
                "   <funds>" + obj.funds+ "</funds>\n" +
                "</UserCommandType>\n")
        writer.close()
    }

    String getUserCommandString(UserCommandTypeBean obj){
        return "    <userCommand>\n" +
                "       <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "       <server>" + obj.server+ "</server>\n" +
                "       <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "       <command>" + obj.command+ "</command>\n" +
                "       <username>" + obj.username+ "</username>\n" +
                "       <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "       <filename>" + obj.filename+ "</filename>\n" +
                "       <funds>" + obj.funds+ "</funds>\n" +
                "   </userCommand>\n"
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
                "</QuoteServerType>\n")
        writer.close()
    }

    String getQuoteServerString(QuoteServerTypeBean obj){
        return "    <quoteServer>\n" +
                "       <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "       <server>" + obj.server+ "</server>\n" +
                "       <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "       <price>" + obj.price+ "</price>\n" +
                "       <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "       <username>" + obj.username+ "</username>\n" +
                "       <quoteServerTime>" + obj.quoteServerTime+ "</quoteServerTime>\n" +
                "       <cryptokey>" + obj.cryptoKey+ "</cryptokey>\n" +
                "   </quoteServer>\n"
    }

    def auditAccountTransaction(AccountTransactionTypeBean obj){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
        writer.write(header)
        writer.write("<AccountTransactionType>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <funds>" + obj.funds+ "</funds>\n" +
                "</AccountTransactionType>\n")
        writer.close()
    }

    String getAccountTransactionString(AccountTransactionTypeBean obj){
        return "    <accountTransaction>\n" +
                "       <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "       <server>" + obj.server+ "</server>\n" +
                "       <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "       <action>" + obj.action+ "</action>\n" +
                "       <username>" + obj.username+ "</username>\n" +
                "       <funds>" + obj.funds+ "</funds>\n" +
                "   </accountTransaction>\n"
    }

    def auditSystemEvents(UserCommandTypeBean obj){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
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
                "</SystemEventType>\n")
        writer.close()
    }

    String getSystemEventString(UserCommandTypeBean obj){
        return "    <systemEvent>\n" +
                "       <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "       <server>" + obj.server+ "</server>\n" +
                "       <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "       <command>" + obj.command+ "</command>\n" +
                "       <username>" + obj.username+ "</username>\n" +
                "       <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "       <filename>" + obj.filename+ "</filename>\n" +
                "       <funds>" + obj.funds+ "</funds>\n" +
                "   </systemEvent>\n"
    }

    def auditErrorEvents(UserCommandTypeBean obj, String err_msg){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
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
                "</ErrorEventType>\n")
        writer.close()
    }

    String getErrorEventString(UserCommandTypeBean obj, String err_msg){
        return "<ErrorEventType>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <command>" + obj.command+ "</command>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "   <filename>" + obj.filename+ "</filename>\n" +
                "   <funds>" + obj.funds+ "</funds>\n" +
                "   <errorMessage>" + err_msg+ "</errorMessage>" +
                "</ErrorEventType>\n"
    }

    def auditDebug(UserCommandTypeBean obj, String dbg_msg){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
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
                "</DebugType>\n")
        writer.close()
    }

    String getDebugString(UserCommandTypeBean obj, String dbg_msg){
        return "<DebugType>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <command>" + obj.command+ "</command>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "   <filename>" + obj.filename+ "</filename>\n" +
                "   <funds>" + obj.funds+ "</funds>\n" +
                "   <debugMessage>" + dbg_msg+ "</debugMessage>" +
                "</DebugType>\n"
    }

    def finishedLogging(){
        writer.write(footer)
        writer.close()
    }
}
