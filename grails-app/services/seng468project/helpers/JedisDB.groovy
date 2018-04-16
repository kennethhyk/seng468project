package seng468project.helpers

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

/**
 * Created by kenneth on 2018-01-20.
 */
class JedisDB {
    Jedis jedis

    JedisDB(String host, Integer port) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig()
        jedisPoolConfig.setMaxTotal(50000)
        jedisPoolConfig.setMaxWaitMillis(1000)
        JedisPool pool = new JedisPool(jedisPoolConfig, host, port, 1000)
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
        jedis.close()
    }

    def retrieveValue(String key){
        String tmp = jedis.get(key)
        jedis.close()
        return tmp
    }
}