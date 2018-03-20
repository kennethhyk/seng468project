package seng468project

class AuditController {

    def save() {
        def res = request.JSON
        LogHistory lh = new LogHistory(res.user as String, res.log as String)
        lh.save()
        render status:200
    }
}
