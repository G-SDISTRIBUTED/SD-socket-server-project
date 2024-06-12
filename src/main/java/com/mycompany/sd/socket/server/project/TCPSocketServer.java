/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sd.socket.server.project;

/**
 *
 * @author Pc
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class TCPSocketServer {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<Integer, SocketClient> clients;
    private ConnectionsThread connectionsThread;
    private SocketServerForm serverForm;
    private ConnectionsChecker connectionsChecker;

    public TCPSocketServer(int port, SocketServerForm serverForm) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.serverForm = serverForm;
            this.clients = new ConcurrentHashMap<>();
            System.out.println("Servidor escuchando en el puerto " + port);
            initializeConnectionListener();
            this.connectionsChecker = new ConnectionsChecker(this);
            this.connectionsChecker.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeConnectionListener() {
        try {
            EventsListener eventsListener = new serverEventsListener(clients, serverForm);
            this.connectionsThread = new ConnectionsThread(this.serverSocket, eventsListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void startConnectionListener() {
        this.connectionsThread.start();
    }

    public ConcurrentHashMap<Integer, SocketClient> getClients() {
        return clients;
    }

    public SocketServerForm getServerForm() {
        return serverForm;
    }
}
