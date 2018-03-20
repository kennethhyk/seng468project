package seng468project

import grails.converters.JSON
import grails.transaction.Transactional
import groovyx.net.http.AsyncHTTPBuilder

//import java.nio.file.Files
//import java.nio.file.Path
//import java.nio.file.Paths
import seng468project.beans.QuoteServerTypeBean
import seng468project.beans.AccountTransactionTypeBean
import seng468project.beans.UserCommandTypeBean

@Transactional
class AuditService {


    String header = "<?xml version=\"1.0\"?>\n" +
            "<log>\n"
    String footer = "\n</log>"

    String dumpLog(String filename){
//        String username = null
//        System.out.println("dumplog function")
//        def records
//        String fileContent =""
//        if(username){
//            def user = User.createCriteria().get{
//                eq 'username',username
//            } as User
//
//            records = LogHistory.createCriteria().list {
//                eq 'user',user
//            } as ArrayList<LogHistory>
//        }else{
//            records = LogHistory.createCriteria().list {} as ArrayList<LogHistory>
//        }
//        BufferedWriter writer = new BufferedWriter(new FileWriter(filename))
//        //writer.write(header)
//        fileContent += header
//        records.each{
////            writer.write(it.xmlBlock)
//            fileContent += it.xmlBlock
//        }
//        //writer.write(footer)
//        fileContent += footer
//        writer.write(fileContent)
//        writer.close()
//        return fileContent
        return ""
    }

    String displaySummary(User usr){
        String str = "Balance: " + usr.balance.toString() + '\n' +
                "Reserved balance: " + usr.reservedBalance.toString() + '\n'

        str += "Stocks owned: {\n"
        for (stock in usr.stockShareMap){
            str += " - " + stock.key + " : " + stock.value + "\n"
        }
        str += "}\n\n"

        str += "Transaction records: {\n"
        usr.transactionList.each {
            str += "\t[\n" +
                    "\t\tTime: " + it.dateCreated + "\n" +
                    "\t\tTransaction status: " + it.status + "\n" +
                    "\t\tQuoted price: " + it.quotedPrice + "\n" +
                    "\t\tStock symbol: " + it.stockSymbol + "\n" +
                    "\t\tAmount:" + it.amount + "\n" +
                    "\t]\n"
        }
        str += "}\n\n"

        def triggers = TransactionTrigger.createCriteria().list{
            eq 'user',usr
        } as List<TransactionTrigger>

        str += "Trigger records: {\n"
        triggers.each{
            str += "\t[\n" +
                    "\t\tBuy/sell amount: " + it.buySellAmount + "\n" +
                    "\t\tTrigger status: " + it.status + "\n" +
                    "\t\tTrigger price: " + it.triggerPrice + "\n" +
                    "\t\tStock symbol: " + it.stockSymbol + "\n" +
                    "\t]\n"
        }
        str += "}\n\n"

//        log.info("IAM AIOFJWIFJOWIFJWE+_dfj)ej(f)ej()fjw)(fej()w")
//        log.info(str)
        return str
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

    String saveErrorEvent(User user, UserCommandTypeBean obj, String err_msg){
         String str = "<errorEvent>\n" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>\n" +
                "   <server>" + obj.server+ "</server>\n" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>\n" +
                "   <command>" + obj.command+ "</command>\n" +
                "   <username>" + obj.username+ "</username>\n" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>\n" +
                "   <filename>" + obj.filename+ "</filename>\n" +
                "   <funds>" + "0.00" + "</funds>\n" +
                "   <errorMessage>" + err_msg+ "</errorMessage>\n" +
                "</errorEvent>\n"

        new LogHistory(user,str).save()
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

    def dispatch(String user, String log_msg) {
        def postBody = [user: user, log_msg: log_msg]
        def http = new AsyncHTTPBuilder(
                poolSize : 1,
                uri : 'http://localhost:8080',
                contentType : JSON )
        http.post(path: '/audit/save', body: postBody)
    }
}
