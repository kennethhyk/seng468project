package seng468project

class BootStrap {

    def init = { servletContext ->
        User zheng = new User(username:"zheng", balance: new BigDecimal("100000"), reservedBalance: new BigDecimal("0") ).save()
        User claudia = new User(username:"claudia", balance: new BigDecimal("100000"), reservedBalance: new BigDecimal("0") ).save()
    }
    def destroy = {
    }
}
