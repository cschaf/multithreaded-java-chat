package de.hsbremen.chat.server;

import de.hsbremen.chat.core.IDisposable;
import de.hsbremen.chat.network.transferableObjects.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by cschaf on 28.03.2015.
 * Server is multithreaded chat server. It accepts multiple clients
 * simultaneously and serves them. Clients can send messages to the server.
 * When some client send a message to the server, this message is dispatched
 * to all the clients connected to the server.
 */
public class Server implements IDisposable{
    public int port;
    private ServerSocket  serverSocket = null;
    private ServerDispatcher serverDispatcher = null;
    private ClientAccepter clientAccepter = null;

    public Server(int port){
        this.port = port;
    }
    public void start(){
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.err.println("Can not start listening on port " + port);
            e.printStackTrace();
            System.exit(1);
        }
        // Start ServerDispatcher thread
        this.serverDispatcher = new ServerDispatcher();
        this.serverDispatcher.start();
        // Accept and handle client connections
        this.clientAccepter = new ClientAccepter(serverSocket, serverDispatcher);
        this.clientAccepter.start();
    }

    public void stop(){
        this.clientAccepter.dispose();
        this.serverDispatcher.dispose();
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
