package seng468project

import seng468project.beans.QuoteServerTypeBean
import seng468project.beans.TransactionHistory

class TestDbServiceController {
    def DbService
    def AuditService

    def index() {
        dbService.refreshDb()

        if(dbService.addNewUser('ws_test','pswd','999.00')){
            log.info('addNewUser test1 PASSED!')
        }else{
            log.error("addNewUser test1")
        }

        if(dbService.addNewUser('ws_test','d','99.00')){
            log.error("addNewUser test2")
        }else{
            log.info('addNewUser test2 PASSED!')
        }

        if(dbService.addAmount('ws_test','1')){
            log.info('addAmount  PASSED!')
        }else{
            log.error('addAmount')
        }

        if(dbService.getUserBalance('ws_test')[1] == '1000.00'){
            log.info('getUserBalance test1 PASSED!')
        }else{
            log.error('getUserBalance expected: 1000.00, got: ' + dbService.getUserBalance('ws_test')[1] )
        }

        if(dbService.removeAmount('ws_test','199.12')){
            log.info('removeAmount  PASSED!')
        }else{
            log.error('removeAmount')
        }

        if(dbService.getUserBalance('ws_test')[1] == '800.88'){
            log.info('getUserBalance test2 PASSED!')
        }else{
            log.error('getUserBalance expected: 800.88, got: ' + dbService.getUserBalance('ws_test')[1] )
        }

        if(dbService.updateUserBalance('ws_test','500')){
            log.info('updateUserBalance  PASSED!')
        }else{
            log.error('updateUserBalance')
        }

        if(dbService.getUserBalance('ws_test')[1] == '500'){
            log.info('getUserBalance test3 PASSED!')
        }else {
            log.error('getUserBalance expected: 500, got: ' + dbService.getUserBalance('ws_test')[1])
        }

        if(dbService.removeAmount('ws_test','100.01')){
            log.info('removeAmount  PASSED!')
        }else{
            log.error('removeAmount')
        }

        if(dbService.getUserBalance('ws_test')[1] == '399.99'){
            log.info('getUserBalance test4 PASSED!')
        }else{
            log.error('getUserBalance expected: 399.99, got: ' + dbService.getUserBalance('ws_test')[1] )
        }

        if(dbService.getUserStocks('ws_test','ABC')[1] == 0){
            log.info('getUserStocks test1 PASSED!')
        }else{
            log.error('getUserStocks expected: 0, got: ' + dbService.getUserStocks('ws_test','ABC')[1] )
        }

        if(dbService.addStockShares('ws_test','ABC',294)){
            log.info('addStockShares  PASSED!')
        }else{
            log.error('addStockShares')
        }

        if(dbService.getUserStocks('ws_test','ABC')[1] == 294){
            log.info('getUserStocks test2 PASSED!')
        }else{
            log.error('getUserStocks expected: 294, got: ' + dbService.getUserStocks('ws_test','ABC')[1] )
        }

        if(dbService.removeStockShares('ws_test','ABC',214)){
            log.info('removeStockShares  PASSED!')
        }else{
            log.error('removeStockShares')
        }

        if(dbService.getUserStocks('ws_test','ABC')[1] == 80){
            log.info('getUserStocks test3 PASSED!')
        }else{
            log.error('getUserStocks expected: 80, got: ' + dbService.getUserStocks('ws_test','ABC')[1] )
        }

        QuoteServerTypeBean obj = new QuoteServerTypeBean(15147648000001,"server1",1,"123.48","ABD","test_user",15147648000002,"500.00")

        auditService.auditQuoteServerRecord(obj)
    }
}
