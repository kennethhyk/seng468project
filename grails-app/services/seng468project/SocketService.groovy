package seng468project

import grails.transaction.Transactional
import java.net.*
import java.io.*

@Transactional
class SocketService {
    private BufferedReader inputStream

    ServerSocket createServerSocket(String ipAddress,int port) {
        ServerSocket serverSocket = new ServerSocket()
        serverSocket.bind(new InetSocketAddress(ipAddress, port))
        log.info("Socket opened at "+serverSocket.getInetAddress()+":"+(serverSocket.getLocalPort() as String))
        return serverSocket
    }

    Socket awaitingClientToConnectToServer(ServerSocket server) {
        log.info("Awaiting Client to connect at "+server.getInetAddress()+":"+(server.getLocalPort() as String))
        Socket clientSocket = server.accept()
        return clientSocket
    }

    SocketConnection startSocketConnection(String ipAddress,int port) {
        ServerSocket server = createServerSocket(ipAddress, port)
        Socket client = awaitingClientToConnectToServer(server)
        if(client) {
            log.info("Client($client) connected to Server($server)")
        }
        SocketConnection connection = new SocketConnection(server, client)
        return connection
    }

    def socketSendStringTo(Socket receiver, String msg) {
        PrintWriter outputStream = new PrintWriter(receiver.getOutputStream(), true)
        outputStream.print(msg)
    }

    def startListeningTo(Socket socket) {
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        log.debug('listening')
        while (inputStream.readLine() != null) {
            log.info(inputStream.readLine())
            //TODO: throw commands to a command handler
        }
    }
}
