package seng468project

import grails.transaction.Transactional


@Transactional
class QuoteCacheService {

     def redisService
     def seconds = 60

     Boolean lookupEntry(String key){

         if(redisService.get(key) == null){
             return false
         }
         return true
     }

    def addNewEntry(String key, String quote_respond){
        redisService.sadd(key, quote_respond)
        redisService.expire(key, seconds)
    }

    def retrieveValue(String key){
        return redisService.get(key)

    }


}
