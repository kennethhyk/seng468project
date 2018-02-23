package seng468project

class RedisTestController {

    def quoteCacheService

    def index() {
        quoteCacheService.test()
        render "test redis"
    }
}
