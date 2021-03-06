package seng468project

import grails.transaction.Transactional
import grails.util.Environment
import redis.clients.jedis.Jedis
import seng468project.beans.QuoteServerTypeBean
import seng468project.beans.AccountTransactionTypeBean
import seng468project.beans.UserCommandTypeBean

@Transactional
class AuditService {
    def redisService
//    static transactional = false
    String header = "<?xml version=\"1.0\"?>" +
            "<log>"
    String footer = "</log>"

    String dumpLog(String filename){
        redisService.withRedis { Jedis redis ->
            redis.bgsave()
        }
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
        return "DUMP LOG TO: $filename!"
    }

    String displaySummary(User usr){
        String str = "Balance: " + usr.balance.toString() +
                " Reserved balance:  " + usr.reservedBalance.toString()

        str += " Stocks owned{"

        def stocks = StockShares.createCriteria().list{
            eq 'user_id',usr.id
        } as List<StockShares>

        stocks.each{
            str +=  it.stockSymbol + ":" + it.shares + " "
        }
        str += "}"

        def transacs = StockTransaction.createCriteria().list{
            eq 'user',usr
        } as List<StockTransaction>

        str += " Transaction records{"
        transacs.each {
            str +=  "Time :" + it.dateCreated  +
                    " Transaction status: " + it.status  +
                    " Quoted price: " + it.quotedPrice  +
                    " Stock symbol: " + it.stockSymbol  +
                    " Amount: " + it.amount
        }
        str += "}"

        def triggers = TransactionTrigger.createCriteria().list{
            eq 'user',usr
        } as List<TransactionTrigger>

        str += " Trigger records{"
        triggers.each{
            str +=  "Buy/sell amount: " + it.buySellAmount  +
                    " Trigger status: " + it.status  +
                    " Trigger price: " + it.triggerPrice  +
                    " Stock symbol: " + it.stockSymbol
        }
        str += "}"

        return str
    }

    def auditUserCommand(UserCommandTypeBean obj){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
        writer.write(header)
        writer.write("<userCommandType>" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>" +
                "   <server>" + obj.server+ "</server>" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "   <command>" + obj.command+ "</command>" +
                "   <username>" + obj.username+ "</username>" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>" +
                "   <filename>" + obj.filename+ "</filename>" +
                "   <funds>" + obj.funds+ "</funds>" +
                "</UserCommandType>")
        writer.close()
    }

    String getUserCommandString(UserCommandTypeBean obj){
        return  " <userCommand>" +
                "<timestamp>" + obj.timestamp+ "</timestamp>" +
                "<server>" + obj.server+ "</server>" +
                "<transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "<command>" + obj.command+ "</command>" +
                "<username>" + obj.username+ "</username>" +
                "<stockSymbol>" + obj.stockSymbol+ "</stockSymbol>" +
                "<filename>" + obj.filename+ "</filename>" +
                "<funds>" + obj.funds+ "</funds>" +
                "</userCommand> "
    }

    def auditQuoteServerRecord(QuoteServerTypeBean obj){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
        writer.write(header)
        writer.write("<QuoteServerType>" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>" +
                "   <server>" + obj.server+ "</server>" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "   <price>" + obj.price+ "</price>" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>" +
                "   <username>" + obj.username+ "</username>" +
                "   <quoteServerTime>" + obj.quoteServerTime+ "</quoteServerTime>" +
                "   <cryptokey>" + obj.cryptoKey+ "</cryptokey>" +
                "</QuoteServerType>")
        writer.close()
    }

    String getQuoteServerString(QuoteServerTypeBean obj){
        return "<quoteServer>" +
                "<timestamp>" + obj.timestamp+ "</timestamp>" +
                "<server>" + obj.server+ "</server>" +
                "<transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "<price>" + obj.price+ "</price>" +
                "<stockSymbol>" + obj.stockSymbol+ "</stockSymbol>" +
                "<username>" + obj.username+ "</username>" +
                "<quoteServerTime>" + obj.quoteServerTime+ "</quoteServerTime>" +
                "<cryptokey>" + obj.cryptoKey+ "</cryptokey>" +
                "</quoteServer>"
    }

    def auditAccountTransaction(AccountTransactionTypeBean obj){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
        writer.write(header)
        writer.write("<AccountTransactionType>" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>" +
                "   <server>" + obj.server+ "</server>" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "   <username>" + obj.username+ "</username>" +
                "   <funds>" + obj.funds+ "</funds>" +
                "</AccountTransactionType>")
        writer.close()
    }

    String getAccountTransactionString(AccountTransactionTypeBean obj){
        return "    <accountTransaction>" +
                "       <timestamp>" + obj.timestamp+ "</timestamp>" +
                "       <server>" + obj.server+ "</server>" +
                "       <transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "       <action>" + obj.action+ "</action>" +
                "       <username>" + obj.username+ "</username>" +
                "       <funds>" + obj.funds+ "</funds>" +
                "   </accountTransaction>"
    }

    def auditSystemEvents(UserCommandTypeBean obj){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
        writer.write(header)
        writer.write("<SystemEventType>" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>" +
                "   <server>" + obj.server+ "</server>" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "   <command>" + obj.command+ "</command>" +
                "   <username>" + obj.username+ "</username>" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>" +
                "   <filename>" + obj.filename+ "</filename>" +
                "   <funds>" + obj.funds+ "</funds>" +
                "</SystemEventType>")
        writer.close()
    }

    String getSystemEventString(UserCommandTypeBean obj){
        return "    <systemEvent>" +
                "       <timestamp>" + obj.timestamp+ "</timestamp>" +
                "       <server>" + obj.server+ "</server>" +
                "       <transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "       <command>" + obj.command+ "</command>" +
                "       <username>" + obj.username+ "</username>" +
                "       <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>" +
                "       <filename>" + obj.filename+ "</filename>" +
                "       <funds>" + obj.funds+ "</funds>" +
                "   </systemEvent>"
    }

    String getErrorEventString(UserCommandTypeBean obj, String err_msg){
         String str = "<errorEvent>" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>" +
                "   <server>" + obj.server+ "</server>" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "   <command>" + obj.command+ "</command>" +
                "   <username>" + obj.username+ "</username>" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>" +
                "   <filename>" + obj.filename+ "</filename>" +
                "   <funds>" + "0.00" + "</funds>" +
                "   <errorMessage>" + err_msg+ "</errorMessage>" +
                "</errorEvent>"

//        new LogHistory(user,str).save()
    }

    def auditDebug(UserCommandTypeBean obj, String dbg_msg){
        BufferedWriter writer = new BufferedWriter(new FileWriter("./logFile.xml"))
        writer.write(header)
        writer.write("<DebugType>" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>" +
                "   <server>" + obj.server+ "</server>" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "   <command>" + obj.command+ "</command>" +
                "   <username>" + obj.username+ "</username>" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>" +
                "   <filename>" + obj.filename+ "</filename>" +
                "   <funds>" + obj.funds+ "</funds>" +
                "   <debugMessage>" + dbg_msg+ "</debugMessage>" +
                "</DebugType>")
        writer.close()
    }

    String getDebugString(UserCommandTypeBean obj, String dbg_msg){
        return "<DebugType>" +
                "   <timestamp>" + obj.timestamp+ "</timestamp>" +
                "   <server>" + obj.server+ "</server>" +
                "   <transactionNum>" + obj.transactionNum+ "</transactionNum>" +
                "   <command>" + obj.command+ "</command>" +
                "   <username>" + obj.username+ "</username>" +
                "   <stockSymbol>" + obj.stockSymbol+ "</stockSymbol>" +
                "   <filename>" + obj.filename+ "</filename>" +
                "   <funds>" + obj.funds+ "</funds>" +
                "   <debugMessage>" + dbg_msg+ "</debugMessage>" +
                "</DebugType>"
    }

    def dispatch(String user, String log_msg) {
//        new LogHistory(user,log_msg).save(flush: true)
//        def postBody = [user: user, log_msg: log_msg]
//        if(Environment.current == Environment.PRODUCTION) {
//            def http = new AsyncHTTPBuilder(
//                    poolSize : 1,
//                    uri : 'http://192.168.1.149:44445',
//                    contentType : JSON )
//            http.post(path: '/audit/save', body: postBody)
//        }
        if (Environment.current == Environment.PRODUCTION) {
            redisService.withRedis { Jedis redis ->
                redis.append(user,log_msg)
            }
        }
    }
}
