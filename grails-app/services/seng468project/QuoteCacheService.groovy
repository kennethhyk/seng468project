package seng468project

import grails.transaction.Transactional
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig


@Transactional
class QuoteCacheService {

    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig()
    JedisPool pool = new JedisPool(jedisPoolConfig, "192.168.1.148", 6379, 1000)
    Jedis jedis = pool.getResource()
    def seconds = 60

    Boolean lookupEntry(String key){
    if(jedis.get(key) == null){
        return false
    }
        return true
    }

    def addNewEntry(String key, String quote_respond){
        jedis.set(key, quote_respond)
        jedis.expire(key, seconds)
    }

    def retrieveValue(String key){
        return jedis.get(key)
    }
}
