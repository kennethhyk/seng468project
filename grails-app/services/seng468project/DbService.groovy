package seng468project

import grails.transaction.Transactional

@Transactional
class DbService {

    def addAmount(User user, String amount){
        BigDecimal bd_amount = new BigDecimal(amount)
        user.balance = user.balance.add(bd_amount)
        user.save()
        return amount
    }

    def removeAmount(User user, String amount){
        BigDecimal bd_amount = new BigDecimal(amount)
        user.balance = user.balance.subtract(bd_amount)
        user.save()
        return amount
    }

    def addNewUser(String userId, String balance){
        if(userExists(userId)){
            return null
        }else{
            def new_user = new User(username:userId,balance:new BigDecimal(balance), reservedBalance: new BigDecimal("0"))

            new_user.stockShareMap = new HashMap<>()
            new_user.save()
            return new_user
        }
    }

    def getUserStocks(User user, String symbol){
        if(user.stockShareMap[symbol]){
            return Integer.parseInt(user.stockShareMap[symbol] as String)
        }else {
            user.stockShareMap[symbol] = "0"
            return null
        }
    }

    def addStockShares(User user, String symbol, Integer shares){
        if(!user.stockShareMap[symbol]){
            user.stockShareMap[symbol] = Integer.toString(shares)
        }else{
            user.stockShareMap[symbol] = Integer.toString(user.stockShareMap[symbol].toInteger() + shares)
        }
        user.save()
    }

    def removeStockShares(User user, String symbol, Integer shares){
        user.stockShareMap[symbol] = Integer.toString(user.stockShareMap[symbol].toInteger() - shares)
        user.save()
    }

    def reserveMoney(User user, BigDecimal reserveAmount){
        user.balance -= reserveAmount
        user.reservedBalance += reserveAmount
        user.save()
    }

    def releaseReservedMoney(User user, BigDecimal releaseAmount){
        user.balance = user.balance.add(releaseAmount)
        user.reservedBalance = user.reservedBalance.subtract(releaseAmount)
        user.save()
    }

    def userExists(String userId){
        def results = User.findByUsername(userId)
        return results
    }
}
