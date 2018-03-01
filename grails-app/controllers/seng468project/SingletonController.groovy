package seng468project

class SingletonController {

    static scope = "singleton"

    def singletonService

    def create() {
        String str = singletonService.create()
        render str
    }

    def check() {
        String str = singletonService.check()
        render str
    }
}
