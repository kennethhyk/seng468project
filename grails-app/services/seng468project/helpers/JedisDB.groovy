package seng468project.helpers

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

/**
 * Created by kenneth on 2018-01-20.
 */
class JedisDB {
    JedisPool pool

    JedisDB() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig()
        jedisPoolConfig.setMaxWaitMillis(1000)
        jedisPoolConfig.setMaxTotal(5000)
        jedisPoolConfig.setMaxIdle(128)
        jedisPoolConfig.setMinIdle(16)
        jedisPoolConfig.setTestOnBorrow(true)
        jedisPoolConfig.setTestOnReturn(true)
        jedisPoolConfig.setTestWhileIdle(true)
        jedisPoolConfig.setMinEvictableIdleTimeMillis(60000)
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000)
        jedisPoolConfig.setNumTestsPerEvictionRun(3)
        jedisPoolConfig.setBlockWhenExhausted(true)
        pool = new JedisPool(jedisPoolConfig, "192.168.1.148", 6379, 1000)
    }

    Boolean lookupEntry(String key){
        Jedis jedis = pool.getResource()
        if(!jedis.get(key)){
            jedis.close()
            return false
        }
        jedis.close()
        return true
    }

    def addNewEntry(String key, String quote_respond){
        Jedis jedis = pool.getResource()
        jedis.set(key, quote_respond)
        jedis.expire(key, 60)
        jedis.close()
    }

    def retrieveValue(String key){
        Jedis jedis = pool.getResource()
        String val = jedis.get(key)
        jedis.close()
        return val

    }
}