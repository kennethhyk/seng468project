package seng468project

class LogHistory {

    String user
    String xmlBlock

    static mapping = {
        xmlBlock type: 'text'
    }

    static constraints = {
        xmlBlock(size:0..65535)
    }

    LogHistory(String user, String xmlBlock) {
        this.user = user
        this.xmlBlock = xmlBlock
    }
}

