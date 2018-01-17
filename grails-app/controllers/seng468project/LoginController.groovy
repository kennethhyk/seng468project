package seng468project

class LoginController {

    def index() { }
//        params.password + params.username

    def login() {
        System.out.println(params.username)
        System.out.println(params.password)
        redirect()
    }


}
