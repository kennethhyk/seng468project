package seng468project

class MultiController {

    static scope = "prototype"

    def multiService

    def create() {
        String str = multiService.create()
        render str
    }

    def check() {
        String str = multiService.check()
        render str
    }
}
