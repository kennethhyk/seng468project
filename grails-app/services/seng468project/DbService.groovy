package seng468project

import grails.transaction.Transactional
import seng468project.beans.AccountTransactionTypeBean

import java.security.*

@Transactional
class DbService {

    def AuditService

    def refreshDb(){
        TransactionTrigger.executeUpdate('delete from TransactionTrigger')
        User.executeUpdate('delete from User')
    }

    def addAmount(String userId, String amount, int transactionNum){
        def row = User.createCriteria().get{
            eq'username',userId
        } as User

        if(!row){
            return 0
        }

        BigDecimal bd_amount = new BigDecimal(amount)
        row.balance = row.balance.add(bd_amount)
        row.save()

        AccountTransactionTypeBean obj = new AccountTransactionTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "ADD",
                userId,
                amount
        )
        String str = auditService.getAccountTransactionString(obj)
        new LogHistory(row,str).save()
        return 1
    }

    // TODO: change dbservice to use user isntead of id? talk to Kenneth
    def addAmount(User user, String amount, int transactionNum){
        user.balance += new BigDecimal(amount)
        user.save()

        AccountTransactionTypeBean obj = new AccountTransactionTypeBean(
                System.currentTimeMillis(),
                "TRANSACTION SERVER: ZaaS",
                transactionNum,
                "ADD",
                user.username,
                amount
        )
        String str = auditService.getAccountTransactionString(obj)
        new LogHistory(user,str).save()

        return 1
    }

    def removeAmount(String userId, String amount){
        def row = User.createCriteria().get{
            eq'username',userId
        } as User

        if(!row){
            return 0
        }

        BigDecimal bd_amount = new BigDecimal(amount)
        row.balance = row.balance.subtract(bd_amount)
        row.save()
        return 1
    }

    //TODO: change balance type
    def addNewUser(String userId, String balance){
        if(userExists(userId)){
            return 0
        }else{
            BigDecimal bd_balance = new BigDecimal(balance)
            BigDecimal zero       = new BigDecimal("0")

            def new_user = new User(username:userId,balance:bd_balance,reservedBalance:zero)

            new_user.stockShareMap = new HashMap<>()
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
            eq 'username', userId
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
            eq 'username', userId
        } as User

        if(results){
            return true
        }
        return false
    }

    // TODO: need to be updated once there are more than one company
    def getUserBalance(String userId){
        def row = User.createCriteria().get{
            eq'username',userId
        } as User

        if(!row) {
            return [0,0]
        }else{
            return [1,row.balance.setScale(2).toString()]
        }
    }

    //TODO: typecheck
    def updateUserBalance(String userId, String balance){
        def row = User.createCriteria().get{
            eq'username',userId
        } as User
        if(!row){
            return 0
        }

        row.balance = new BigDecimal(balance)
        row.save()

        return 1
    }


    def getUserStocks(String userId, String symbol){
        def row = User.createCriteria().get{
            eq'username',userId
        } as User

        if(!row) {
            return [0,0]
        }else{
            if(row.stockShareMap[symbol]){
                return [1,Integer.parseInt(row.stockShareMap[symbol] as String)]
            }else{
                return [1,0]
            }

        }
    }

    def addStockShares(String userId, String symbol, Integer shares){
        def row = User.createCriteria().get{
            eq'username',userId
        } as User

        if(!row){
            return 0
        }

        // if not in list, add it in
        if(!row.stockShareMap[symbol]){
            row.stockShareMap[symbol] = Integer.toString(shares)
        }else{
            row.stockShareMap[symbol] = Integer.toString(row.stockShareMap[symbol].toInteger() + shares)
        }
        row.save()
        return 1
    }

    def removeStockShares(String userId, String symbol, Integer shares){
        def row = User.createCriteria().get{
            eq'username',userId
        } as User

        if(!row){
            return 0
        }

        row.stockShareMap[symbol] = Integer.toString(row.stockShareMap[symbol].toInteger() - shares)

        row.save()
        return 1
    }

    def reserveMoney(String userId, BigDecimal reserveAmount){
        def row = User.createCriteria().get{
            eq'username',userId
        } as User

        if(!row){
            return 0
        }

        row.balance -= reserveAmount
        row.reservedBalance += reserveAmount
        row.save()
        return 1
    }

    def releaseReservedMoney(String userId, BigDecimal releaseAmount){
        def row = User.createCriteria().get{
            eq'username',userId
        } as User

        if(!row){
            return 0
        }

        row.balance = row.balance.add(releaseAmount)
        row.reservedBalance = row.reservedBalance.subtract(releaseAmount)
        row.save()
        return 1
    }
}
