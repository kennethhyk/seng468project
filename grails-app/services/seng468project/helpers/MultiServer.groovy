package seng468project.helpers

/**
 * Created by kenneth on 2018-01-20.
 */
class MultiServer {
    private ServerSocket serverSocket

    void start(int port) {
        serverSocket = new ServerSocket(port)
        while (true)
            new ClientHandler(serverSocket.accept()).start()
    }

     void stop() {
        serverSocket.close()
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket
        private PrintWriter outputStream
        private BufferedReader inputStream

         ClientHandler(Socket socket) {
            this.clientSocket = socket
        }

         void run() {
            outputStream = new PrintWriter(clientSocket.getOutputStream(), true)
            inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))

            while (true) {
                outputStream.println(inputStream.readLine())
            }

            inputStream.close()
            outputStream.close()
            clientSocket.close()
        }
    }
}
