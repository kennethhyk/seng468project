package seng468project

import seng468project.beans.QuoteServerTypeBean
import seng468project.enums.TriggerStatusEnum

class TestDbServiceController {
    def DbService
    def AuditService
    def TransactionService

    def index() {
        dbService.refreshDb()

        if(dbService.addNewUser('ws_test','999.00')){
            log.info('addNewUser test1 PASSED!')
        }else{
            log.error("addNewUser test1")
        }

        if(dbService.addNewUser('ws_test','99.00')){
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

        if(dbService.getUserBalance('ws_test')[1] == '500.00'){
            log.info('getUserBalance test3 PASSED!')
        }else {
            log.error('getUserBalance expected: 500.00, got: ' + dbService.getUserBalance('ws_test')[1])
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

        /*************************************************************
         *      TRIGGER SECTION
         **********************************************************/

        def user = User.createCriteria().get{
            eq'username','ws_test'
        } as User

        String expected0 = "you son't have enough money"
        if(transactionService.setBuyAmount(user,'ABC',new BigDecimal("1194.29")).equals(expected0)){
            log.info("setBuyAmount not enough money test PASSED!")
        }else{
            log.error('setBuyAmount not enough money test')
        }

        String expected = " Amount: '194.29' is set for stockSymbol: 'ABC', please also set trigger"
        if(transactionService.setBuyAmount(user,'ABC',new BigDecimal("194.29")).equals(expected)){
            log.info("setBuyAmount test1 PASSED!")
        }else{
            log.error('setBuyAmount')
        }

        if(user.balance.toString() != "205.70" || user.reservedBalance.toString() != "194.29") log.error("set_buy calculation error")

        String expected1 = "TransactionTrigger for ABC already exists"
        if(transactionService.setBuyAmount(user,'ABC',new BigDecimal("1111.11")).equals(expected1)){
            log.info("setBuyAmount duplicate test PASSED!")
        }else{
            log.error('setBuyAmount duplicate test')
        }


        String expected2 = "no trigger record found"
        if(transactionService.cancelSetBuy(user,'A').equals(expected2)){
            log.info("cancelSetBuy no record test PASSED!")
        }else{
            log.error('cancelSetBuy no record test')
        }

        String expected3 = "set_buy canceled"
        if(transactionService.cancelSetBuy(user,'ABC').equals(expected3)){
            if(transactionService.triggerExists(user,'ABC',"BUY")){
                log.error('cancelSetBuy test')
            }else{
                log.info("cancelSetBuy test1 PASSED!")
            }
        }else{
            log.error('cancelSetBuy test')
        }

        // make sure money is relseased
        if(user.balance.toString() != "399.99" || user.reservedBalance.toString() != "0.00") log.error("cancel_set_buy calculation error")

        String expected4 = "no record found, please set buy amount first"
        if(transactionService.setBuyTrigger(user,'ABC',new BigDecimal("10")).equals(expected4)){
            log.info('setBuyTrigger no set buy test PASSED!')
        }else{
            log.error('setBuyTrigger no set buy test')
        }


        String expected41 = " Amount: '194.29' is set for stockSymbol: 'ABC', please also set trigger"
        String res = transactionService.setBuyAmount(user,'ABC',new BigDecimal("194.29"))
        if(res.equals(expected41)){
            log.info("setBuyAmount test2 PASSED!")
        }else{
            //log.info(res)
            log.error('setBuyAmount')
        }


        String expected5 = "buy trigger set"
        if(transactionService.setBuyTrigger(user,'ABC',new BigDecimal("10")).equals(expected5)){
            log.info('setBuyTrigger test PASSED!')
        }else{
            log.error('setBuyTrigger test')
        }

        def trig = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol','ABC'
            or {
                eq 'status', TriggerStatusEnum.SET_BUY_TRIGGER
                eq 'status', TriggerStatusEnum.SET_SELL_TRIGGER
                eq 'status', TriggerStatusEnum.SET_SELL
                eq 'status', TriggerStatusEnum.SET_BUY
            }
        } as TransactionTrigger

        if(!trig) log.error("record not found")

        if(trig.status != TriggerStatusEnum.SET_BUY_TRIGGER){
            log.error("setBuyTrigger set wrong status")
        }

        if(transactionService.cancelSetBuy(user,'ABC').equals(expected3)){
            if(transactionService.triggerExists(user,'ABC',"BUY")){
                log.error('cancelSetBuy test cancel trigger')
            }else{
                log.info("cancelSetBuy test cancel trigger PASSED!")
            }
        }else{
            log.error('cancelSetBuy test cancel trigger')
        }

        // make sure money is relseased
        if(user.balance.toString() != "399.99" || user.reservedBalance.toString() != "0.00") log.error("cancel_set_buy calculation error")

        /*************************************************************
         *     SELL TRIGGER SECTION
         **********************************************************/

        String expected6 = " Amount: '194.29' is set for stockSymbol: 'ABC', please also set trigger"
        if(transactionService.setSellAmount(user,'ABC',new BigDecimal("194.29")).equals(expected6)){
            log.info("setSellAmount test1 PASSED!")
        }else{
            log.error('setSellAmount')
        }

        String expected7 = "TransactionTrigger for ABC already exists"
        String res12 = transactionService.setSellAmount(user,'ABC',new BigDecimal("1111.11"))
        if((res12).equals(expected7)){
            log.info("setBuyAmount duplicate test PASSED!")
        }else{
            log.error('setBuyAmount duplicate test')
        }

        String expected8 = "no trigger record found"
        if(transactionService.cancelSetSell(user,'A').equals(expected8)){
            log.info("cancelSetSell no record test PASSED!")
        }else{
            log.error('cancelSetSell no record test')
        }

        String expected9 = "set_sell canceled"
        if(transactionService.cancelSetSell(user,'ABC').equals(expected9)){
            if(transactionService.triggerExists(user,'ABC',"SELL")){
                log.error('cancelSetBuy test, didnt delete record')
            }else{
                log.info("cancelSetBuy test1 PASSED!")
            }
        }else{
            log.error('cancelSetBuy test')
        }

        String expected10 = "no record found, please set sell amount first"
        String res13 = transactionService.setSellTrigger(user,'ABC',new BigDecimal("10"))
        if(res13.equals(expected10)){
            log.info('setSellTrigger no set buy test PASSED!')
        }else{
            log.error('setSellTrigger no set buy test')
        }

        String expected11 = " Amount: '194.29' is set for stockSymbol: 'ABC', please also set trigger"
        if(transactionService.setSellAmount(user,'ABC',new BigDecimal("194.29")).equals(expected11)){
            log.info("setSellAmount test1 PASSED!")
        }else{
            log.error('setSellAmount')
        }

        String expected20 = "not enough shares to sell"
        String res14 = transactionService.setSellTrigger(user,'ABC',new BigDecimal("1"))
        if(res14.equals(expected20)){
            log.info('setSellTrigger not enough share test PASSED!')
        }else{
            log.error('setSellTrigger not enough share test')
        }

        String expected12 = "sell trigger set"
        if(transactionService.setSellTrigger(user,'ABC',new BigDecimal("10")).equals(expected12)){
            log.info('setSellTrigger test PASSED!')
        }else{
            log.error('setSellTrigger test')
        }

        if(user.stockShareMap['ABC'] != "61") log.error("share reservation error")

        def trig_2 = TransactionTrigger.createCriteria().get{
            eq'user',user
            eq 'stockSymbol','ABC'
            or {
                eq 'status', TriggerStatusEnum.SET_BUY_TRIGGER
                eq 'status', TriggerStatusEnum.SET_SELL_TRIGGER
                eq 'status', TriggerStatusEnum.SET_SELL
                eq 'status', TriggerStatusEnum.SET_BUY
            }
        } as TransactionTrigger

        if(!trig_2) log.error("record not found")

        if(trig_2.status != TriggerStatusEnum.SET_SELL_TRIGGER){
            log.error("setSellTrigger set wrong status")
        }

        String expected15 = "set_sell canceled"
        if(transactionService.cancelSetSell(user,'ABC').equals(expected15)){
            if(transactionService.triggerExists(user,'ABC',"SELL")){
                log.error('cancelSetBuy test, didnt delete record')
            }else{
                log.info("cancelSetBuy test1 PASSED!")
            }
        }else{
            log.error('cancelSetBuy test')
        }

        if(user.stockShareMap['ABC'] != "80") log.error("share reservation error")
    }
}
