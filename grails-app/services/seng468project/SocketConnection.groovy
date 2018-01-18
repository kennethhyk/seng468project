package seng468project
/**
 * Created by kenneth on 2018-01-16.
 */
class SocketConnection {
    ServerSocket serverSocket
    Socket clientSocket

    SocketConnection(ServerSocket server, Socket client) {
        serverSocket = server
        clientSocket = client
    }
}
