package seng468project

import grails.transaction.Transactional

@Transactional
class DbService {

    def addAmount(User user, String amount){
        BigDecimal bd_amount = new BigDecimal(amount)
        user.balance = user.balance.add(bd_amount)
        user.save(flush: true)
        return amount
    }

    def removeAmount(User user, String amount){
        BigDecimal bd_amount = new BigDecimal(amount)
        user.balance = user.balance.subtract(bd_amount)
        user.save(flush: true)
        return amount
    }

    def addNewUser(String userId, String balance){
        if(userExists(userId)){
            return null
        }else{
            def new_user = new User(username:userId,balance:new BigDecimal(balance), reservedBalance: new BigDecimal("0"))
            new_user.save(flush: true)
            return new_user
        }
    }

    Integer getUserStocks(User user, String symbol){
        def t = StockShares.executeQuery("from StockShares where user_id = ? and stock_symbol = ?",
                [user.id,symbol])[0]
        if(t){
            return t.shares
        }else {
            return 0
        }
    }

    def addStockShares(User user, String symbol, Integer shares){
        StockShares t = StockShares.executeQuery("from StockShares where user_id = ? and stock_symbol = ?",
                [user.id,symbol])[0] as StockShares
        if(!t){
            new StockShares(user_id: user.id,stockSymbol: symbol,shares: shares).save(flush:true)
        }else{
            t.shares += shares
            t.save(flush:true)
        }
    }

    def removeStockShares(User user, String symbol, Integer shares){
        StockShares t = StockShares.executeQuery("from StockShares where user_id = ? and stock_symbol = ?",
                [user.id,symbol])[0] as StockShares
        if(!t){
            return 0
        }
        t.shares -= shares
        t.save(flush:true)
        return 1
    }

    def reserveMoney(User user, BigDecimal reserveAmount){
        user.balance -= reserveAmount
        user.reservedBalance += reserveAmount
        user.save(flush: true)
    }

    def releaseReservedMoney(User user, BigDecimal releaseAmount){
        user.balance = user.balance.add(releaseAmount)
        user.reservedBalance = user.reservedBalance.subtract(releaseAmount)
        user.save(flush: true)
    }

    def userExists(String userId){
        def results = User.findByUsername(userId)
        return results
    }
}
