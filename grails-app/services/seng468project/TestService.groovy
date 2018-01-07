package seng468project

import grails.transaction.Transactional

@Transactional
class TestService {

    def serviceMethod() {
        Test lz = new Test()
        lz.lizheng = "bubb"
        lz.save()
    }
}
