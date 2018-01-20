package seng468project.helpers

/**
 * Created by kenneth on 2018-01-20.
 */
class SSocket {
    private ServerSocket serverSocket
    private Socket clientSocket
    private PrintWriter outputStream
    private BufferedReader inputStream

    Boolean start(int port) {
        serverSocket = new ServerSocket(port)
        clientSocket = serverSocket.accept()
        outputStream = new PrintWriter(clientSocket.getOutputStream(), true)
        inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        return true
    }

    def echo() {
        String inputLine
        while (true) {
            //inputLine recieved, do something
            //reply
            outputStream.println(inputStream.readLine())
        }
    }

    String sendMessage(String msg) {
        outputStream.println(msg)
        String resp = inputStream.readLine()
        return resp
    }

    void stop() {
        inputStream.close()
        outputStream.close()
        clientSocket.close()
        serverSocket.close()
    }
}