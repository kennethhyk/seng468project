package seng468project

class WebController {
    def dbService
    def index() {

    }

    def addNewUserToUsersTable(String userId, Float balance) {
        //TODO: sanitize input
        if(dbService.isUsernameDuplicated(userId)){
            return [msg:"ERROR: User '" + userId + "' already exits"]
        }else{
            dbService.addNewUserToUsersTable(userId,balance)
            return [msg:"User '" + userId + "' successfully created"]
        }

    }

}
