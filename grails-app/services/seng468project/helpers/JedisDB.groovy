package seng468project.helpers

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

/**
 * Created by kenneth on 2018-01-20.
 */
class JedisDB {
    Jedis jedis

    JedisDB() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig()
        jedisPoolConfig.setMaxTotal(1000)
        jedisPoolConfig.setMaxWaitMillis(1000)
        JedisPool pool = new JedisPool(jedisPoolConfig, "192.168.1.148", 6379, 1000)
        jedis = pool.getResource()
    }

    Boolean lookupEntry(String key){
        if(jedis.get(key) == null){
            return false
        }
        return true
    }

    def addNewEntry(String key, String quote_respond){
        jedis.set(key, quote_respond)
        jedis.expire(key, 60)
    }

    def retrieveValue(String key){
        return jedis.get(key)
    }
}