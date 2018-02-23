//grails {
//    redis {
//        poolConfig {
//            // jedis pool specific tweaks here, see jedis docs & src
//            // ex: testWhileIdle = true
//        }
//        timeout = 2000 //default in milliseconds
//        password = "" //defaults to no password
//
//        // requires either host & port combo, or a sentinels and masterName combo
//
//        // use a single redis server (use only if nore using sentinel cluster)
//        port = 6379
//        host = "192.168.1.148"
//        database = 5 // set default database to 5
//        masterName = "mymaster" // the name of a master the sentinel cluster is configured to monitor
//    }
//
//}