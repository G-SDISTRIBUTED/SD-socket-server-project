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
    private ConcurrentHashMap<String, SocketClient> clients;
    private ConnectionListener connectionListener;
    private SocketServerForm serverForm;
    private ConnectionsChecker connectionsChecker;

    public TCPSocketServer(int port, SocketServerForm serverForm) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.serverForm = serverForm;
            initializeConnectionListener();
            this.clients = new ConcurrentHashMap<>();
            System.out.println("Servidor escuchando en el puerto " + port);
            this.connectionsChecker = new ConnectionsChecker(this);
            this.connectionsChecker.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeConnectionListener() {
        try {
            EventsListener eventsListener = createEventsListener();
            this.connectionListener = new ConnectionListener(this.serverSocket, eventsListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private EventsListener createEventsListener() {
        return new EventsListener() {
            @Override
            public void handleClientConnected(ClientConnectedEvent event) {
                SocketClient socketClient = new SocketClient(event.getClientSocket(), this);
                String sessionID = event.getClientSocket().getInetAddress().getHostAddress();
                socketClient.cambiarToken(sessionID);
                clients.put(sessionID, socketClient);
                startClient(sessionID);
                serverForm.addMessage("Cliente conectado: " + sessionID);
            }

            @Override
            public void handleClientDisconnected(ClientDisconnectedEvent event) {
                String clientToken = event.getClientToken();
                clients.remove(clientToken);
                serverForm.addMessage("Cliente desconectado: " + clientToken);
            }

            @Override
            public void handleMessageReceived(MessageReceivedEvent event) {
                String message = event.getMessage();
                serverForm.addMessage("Mensaje de cliente: " + message);
            }
        };
    }

    private void startClient(String key) {
        SocketClient socketClient = this.clients.get(key);
        socketClient.startClient();
    }

    public void startConnectionListener() {
        this.connectionListener.start();
    }

    public ConcurrentHashMap<String, SocketClient> getClients() {
        return clients;
    }

    public SocketServerForm getServerForm() {
        return serverForm;
    }
}
