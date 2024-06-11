/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hp
 */
public class serverEventsListener implements EventsListener{
    private ConcurrentHashMap<String, SocketClient> clients;
    private SocketServerForm serverForm;
    private DatabaseManager databaseManager;

    public serverEventsListener(ConcurrentHashMap<String, SocketClient> clients, SocketServerForm serverForm) {
        this.clients = clients;
        this.serverForm = serverForm;
        this.databaseManager = new DatabaseManager();
    }
    
    @Override
    public void handleClientConnected(ClientConnectedEvent event) {
        SocketClient socketClient = new SocketClient(event.getClientSocket(), this);
        String sessionID = event.getClientSocket().getInetAddress().getHostAddress();
        socketClient.cambiarToken(sessionID);
        clients.put(sessionID, socketClient);
        startClient(sessionID);
        serverForm.addMessage("Cliente conectado: " + sessionID);
    }
    
    private void startClient(String key) {
        SocketClient socketClient = this.clients.get(key);
        socketClient.startClient();
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
        String token = event.getToken();

        try {
            PrintWriter output = new PrintWriter(clients.get(token).getSocket().getOutputStream(), true);
            
            if (message.startsWith("LOGIN:")) {
                String[] parts = message.split(":");
                String username = parts[1];
                String password = parts[2];
                boolean success = databaseManager.loginUser(username, password);

                if (success) {
                    output.println("LOGIN_SUCCESS");
                } else {
                    output.println("LOGIN_FAILURE");
                }
            } else if (message.startsWith("REGISTER:")) {
                String[] parts = message.split(":");
                String username = parts[1];
                String password = parts[2];
                boolean success = databaseManager.registerUser(username, password);
                if (success) {
                    output.println("REGISTER_SUCCESS");
                } else {
                    output.println("REGISTER_FAILURE");
                }
            }
        } catch (IOException ex) {
                Logger.getLogger(serverEventsListener.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
}
