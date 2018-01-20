package seng468project.helpers

/**
 * Created by kenneth on 2018-01-20.
 */
class CSocket {
    private Socket clientSocket
    private PrintWriter outputStream
    private BufferedReader inputStream

    void start(String ip, int port) {
        int retry = 10
        for (i in (1..retry)) {
            try{
                clientSocket = new Socket(ip, port)
                outputStream = new PrintWriter(clientSocket.getOutputStream(), true)
                inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                break
            }
            catch(e) {
                System.out.println("Cannot start client, ip/port given is not open to connect, please check server, retrying connection...")
                if(i == retry) {
                    System.out.println("Connection Timeout, stop retrying")
                }
            }
            sleep(3000)
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
    }
}