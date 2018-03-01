package seng468project

import grails.transaction.Transactional

@Transactional
class SingletonService {

    def share = UUID.randomUUID().toString()

    def create() {
        share = UUID.randomUUID().toString()
        return share
    }

    def check() {
        return share
    }
}
