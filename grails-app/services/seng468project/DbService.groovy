package seng468project

import grails.transaction.Transactional
import java.security.*

@Transactional
class DbService {



    def serviceMethod() {

    }

    def addAmount(String userId, Float amount){
        def row = Users.createCriteria().get{
            eq'userid',userId
        }

        if(!row){
            return 0
        }

        row.balance += amount
        row.save()
        return 1
    }

    def removeAmount(String userId, Float amount){
        def row = Users.createCriteria().get{
            eq'userid',userId
        }

        if(!row){
            return 0
        }

        row.balance -= amount
        row.save()
        return 1
    }

    //TODO: change balance type
    def addNewUser(String userId, String password, Float balance){
        if(userExists(userId)){
            return 0
        }else{
            password = digestPassword(password)
            def new_user = new Users(userid:userId,password:password,balance:balance)
            new_user.stockSymbols = Collections.emptyMap()
            new_user.save()
            return 1
        }
    }

    def digestPassword(String password){
        MessageDigest md = MessageDigest.getInstance("SHA")
        byte[] dataBytes = password.getBytes()
        md.update(dataBytes)
        byte[] digest = md.digest()
        String digestedPassword = Arrays.toString(digest)

        return digestedPassword
    }

    def checkPassword(String userId, String password){

        def results = Users.createCriteria().get{
            eq 'userid', userId
            and{
                eq 'password',password
            }
        }
        return results
    }

    def userExists(String userId){
        // get all rows in table
        def results = Users.createCriteria().get{
            eq 'userid', userId
        }

        if(results){
            return true
        }
        return false
    }

    // TODO: need to be updated once there are more than one company
    def getUserBalance(String userId){
        def row = Users.createCriteria().get{
            eq'userid',userId
        }

        if(!row) {
            return [0,0]
        }else{
            return [1,row.balance]
        }
    }

    //TODO: typecheck
    def updateUserInfo(String userId, Float balance, Integer shares){
        def row = Users.createCriteria().get{
            eq'userid',userId
        }

        if(!row){
            return 0
        }
        println("original balance: " + row.balance)
        row.balance = balance
        row.shares  = shares
        row.save()

        return 1
    }


    def getUserStocks(String userId, String symbol){
        def row = Users.createCriteria().get{
            eq'userid',userId
        }

        if(!row) {
            return [0,0]
        }else{
            if(row.stockSymbols[symbol]){
                return [1,row.stockSymbols[symbol]]
            }else{
                return [1,0]
            }

        }
    }

    def addStockShares(String userId, String symbol, Integer shares){
        def row = Users.createCriteria().get{
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
        def row = Users.createCriteria().get{
            eq'userid',userId
        }

        if(!row){
            return 0
        }

        row.stockSymbols[symbol] = Integer.toString(row.stockSymbols[symbol].toInteger() - shares)

        return 1
    }
}
