package seng468project

import grails.transaction.Transactional
import java.security.*

@Transactional
class DbService {

    def refreshDb(){
        User.executeUpdate('delete from User')
    }

    def addAmount(String userId, String amount){
        def row = User.createCriteria().get{
            eq'userid',userId
        }

        if(!row){
            return 0
        }

        BigDecimal bd_amount = new BigDecimal(amount)
        row.balance = row.balance.add(bd_amount)
        row.save()
        return 1
    }

    def removeAmount(String userId, String amount){
        def row = User.createCriteria().get{
            eq'userid',userId
        }

        if(!row){
            return 0
        }

        BigDecimal bd_amount = new BigDecimal(amount)
        row.balance = row.balance.subtract(bd_amount)
        row.save()
        return 1
    }

    //TODO: change balance type
    def addNewUser(String userId, String password, String balance){
        if(userExists(userId)){
            return 0
        }else{
            password = digestPassword(password)

            BigDecimal bd_balance = new BigDecimal(balance)

            def new_user = new User(userid:userId,password:password,balance:bd_balance)

            new_user.stockSymbols = new HashMap<>()
            new_user.save()
            return 1
        }
    }

    //deprecated
    def digestPassword(String password){
        MessageDigest md = MessageDigest.getInstance("SHA")
        byte[] dataBytes = password.getBytes()
        md.update(dataBytes)
        byte[] digest = md.digest()
        String digestedPassword = Arrays.toString(digest)

        return digestedPassword
    }

    //deprecated
    def checkPassword(String userId, String password){

        def results = User.createCriteria().get{
            eq 'userid', userId
            and{
                eq 'password',password
            }
        }
        return results
    }

    // can be deprecated
    def userExists(String userId){
        // get all rows in table
        def results = User.createCriteria().get{
            eq 'userid', userId
        }

        if(results){
            return true
        }
        return false
    }

    // TODO: need to be updated once there are more than one company
    def getUserBalance(String userId){
        def row = User.createCriteria().get{
            eq'userid',userId
        }

        if(!row) {
            return [0,0]
        }else{
            return [1,row.balance.toString()]
        }
    }

    //TODO: typecheck
    def updateUserBalance(String userId, String balance){
        def row = User.createCriteria().get{
            eq'userid',userId
        }
        if(!row){
            return 0
        }

        row.balance = new BigDecimal(balance)
        row.save()

        return 1
    }


    def getUserStocks(String userId, String symbol){
        def row = User.createCriteria().get{
            eq'userid',userId
        }

        if(!row) {
            return [0,0]
        }else{
            if(row.stockSymbols[symbol]){
                return [1,Integer.parseInt(row.stockSymbols[symbol])]
            }else{
                return [1,0]
            }

        }
    }

    def addStockShares(String userId, String symbol, Integer shares){
        def row = User.createCriteria().get{
            eq'userid',userId
        }

        if(!row){
            return 0
        }

        // if not in list, add it in
        if(!row.stockSymbols[symbol]){
            row.stockSymbols[symbol] = Integer.toString(shares)
        }else{
            row.stockSymbols[symbol] = Integer.toString(row.stockSymbols[symbol].toInteger() + shares)
        }
        return 1
    }

    def removeStockShares(String userId, String symbol, Integer shares){
        def row = User.createCriteria().get{
            eq'userid',userId
        }

        if(!row){
            return 0
        }

        row.stockSymbols[symbol] = Integer.toString(row.stockSymbols[symbol].toInteger() - shares)

        return 1
    }
}
