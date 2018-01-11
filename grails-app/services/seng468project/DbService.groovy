package seng468project

import grails.transaction.Transactional

@Transactional
class DbService {



    def serviceMethod() {

    }


    def addNewUserToUsersTable(String userId, Float balance){
        Users new_user = new Users()

        new_user.userid = userId
        new_user.shares = 0
        new_user.balance = balance
        new_user.save()
    }

    def isUsernameDuplicated(String userId){
        // get total number of rows in table
        def row_count = Users.createCriteria().get {
            projections {
                countDistinct("id")
            }
        }
        // get all rows in table
        def results = Users.createCriteria().list {
            and {
                ge("shares", 0)
            }
        }

        // check if id duplicates
        // TODO: use hash later for faster lookup
        def i
        for(i=0;i<row_count;i++){
            if(results[i].userid.equals(userId)){
                return true
            }
        }
        return false
    }


}
