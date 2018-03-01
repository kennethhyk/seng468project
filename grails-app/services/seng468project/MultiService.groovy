package seng468project

import grails.transaction.Transactional

@Transactional
class MultiService {

    static scope = "prototype"

    def share = UUID.randomUUID().toString()

    def create() {
        share = UUID.randomUUID().toString()
        return share
    }

    def check() {
        return share
    }
}
