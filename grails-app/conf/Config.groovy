grails {
    redis {
        poolConfig {
            // jedis pool specific tweaks here, see jedis docs & src
            // ex: testWhileIdle = true
        }
        timeout = 2000 //default in milliseconds
        password = "" //defaults to no password

        // requires either host & port combo, or a sentinels and masterName combo

        // use a single redis server (use only if nore using sentinel cluster)
        port = 6379
        host = "localhost"
        database = 5 // set default database to 5

        // use redis-sentinel cluster as opposed to a single redis server (use only if not use host/port)
        sentinels = [ "host1:6379", "host2:6379", "host3:6379" ] // list of sentinel instance host/ports
        masterName = "mymaster" // the name of a master the sentinel cluster is configured to monitor
    }

}