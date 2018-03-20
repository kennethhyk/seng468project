package seng468project

import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.JSON
import groovyx.net.http.AsyncHTTPBuilder

class AuditController {

    def save() {
        String user = request.getParameter("user")
        String log_msg = request.getParameter("log_msg")
        LogHistory lh = new LogHistory(user, log_msg)
        lh.save()
        render status:200
    }

    def hi() {
        String user = UUID.randomUUID().toString()
        String log_msg = UUID.randomUUID().toString()
        def postBody = [user: user, log_msg: log_msg]
        def http = new AsyncHTTPBuilder(
                poolSize : 1,
                uri : 'http://localhost:8080',
                contentType : JSON )
        http.post(path: '/audit/save', body: postBody)
        render status: 200
    }
}