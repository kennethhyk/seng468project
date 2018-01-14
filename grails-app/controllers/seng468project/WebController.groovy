package seng468project

class WebController {
    def dbService
    def index() {

    }

    def addNewUser(String userId, Float balance) {
        //TODO: sanitize input
        if(dbService.addNewUser(userId,balance)){
            return [msg:"User '" + userId + "' successfully created"]
        }else{
            return [msg:"ERROR: User '" + userId + "' already exits"]
        }
    }

    def addAmount(String userId, Float amount){
        if(dbService.addAmount(userId, amount)){
            return [msg: "added balance: " + amount + " to user : " + userId]
        }else{
            return [msg:"ERROR: no records found for user '" + userId + "'"]
        }
    }

    //NOTE: negative values allowed
    def removeAmount(String userId, Float amount){
        if(dbService.removeAmount(userId, amount)){
            return [msg: "removed balance: " + amount + " to user : " + userId]
        }else{
            return [msg:"ERROR: no records found for user '" + userId + "'"]
        }
    }

    def getUserBalance(String userId){
        def (status, balance) = dbService.getUserBalance(userId)
        if(status){
            return [msg: "User has balance: " + balance + " and shares: " + shares]
        }else{
            return [msg:"ERROR: no records found for user '" + userId + "'"]
        }
    }

    // will be archived
    def updateUserInfo(String userId, Float balance, Integer shares){
        if(dbService.updateUserInfo(userId,balance,shares)){
            return [msg: "User information updated"]
        }else{
            return [msg:"ERROR: no records found for user '" + userId + "'"]
        }
    }

    def getUserStocks(String userId, String symbol){
        def (status, stocks) = dbService.getUserStocks(userId,symbol)
        if(status){
            return [msg:  symbol+ ": " + stocks]
        }else{
            return [msg:"ERROR: no records found for user '" + userId + "'"]
        }
    }

    def addStockShares(String userId, String symbol, Integer shares){
        if(dbService.addStockShares(userId,symbol,shares)){
            return [msg:  "added " + shares + " shares to stockSymbol: " +symbol ]
        }else{
            return [msg:"ERROR: no records found for user '" + userId + "'"]
        }
    }
}
