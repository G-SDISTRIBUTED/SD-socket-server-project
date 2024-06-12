/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mycompany.paquete.Paquete;
import com.mycompany.paquete.*;

/**
 *
 * @author Hp
 */
public class serverEventsListener implements EventsListener{
    private ConcurrentHashMap<Integer, SocketClient> clients;
    private SocketServerForm serverForm;
    private DatabaseManager databaseManager;
    private GameServer gameServer;

    public serverEventsListener(ConcurrentHashMap<Integer, SocketClient> clients, SocketServerForm serverForm) {
        this.clients = clients;
        this.serverForm = serverForm;
        this.databaseManager = new DatabaseManager();
        this.gameServer = new GameServer(this.serverForm);
    }
    
    @Override
    public void handleClientConnected(ClientConnectedEvent event) {
        SocketClient socketClient = new SocketClient(event.getClientSocket(), this);
        Integer sessionID = event.getClientSocket().hashCode();
        socketClient.cambiarToken(sessionID);
        clients.put(sessionID, socketClient);
        startClient(sessionID);
        serverForm.addMessage("Cliente conectado: " + sessionID);
    }
    
    private void startClient(Integer key) {
        SocketClient socketClient = this.clients.get(key);
        socketClient.startClient();
    }

    @Override
    public void handleClientDisconnected(ClientDisconnectedEvent event) {
        Integer clientToken = event.getClientToken();
        clients.remove(clientToken);
        serverForm.addMessage("Cliente desconectado: " + clientToken);
    }

    @Override
    public void handleMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage();
        Integer token = event.getToken();
        if (message.startsWith("PING hacia el servidor...")){
            System.out.println("PING del cliente");
            return;
        }
        try {
            
            PrintWriter output = new PrintWriter(clients.get(token).getSocket().getOutputStream(), true);
            
            Gson gson = new Gson();
            StringBuilder jsonString = new StringBuilder();
            jsonString.append(message);
            System.out.println("json: "+jsonString.toString());
            System.out.println("message: "+ message);
            Paquete paquete = gson.fromJson(message, Paquete.class);
            String comando = paquete.getComando();
        
            if (null != comando) switch (comando) {
                case "login":{
                    boolean success = gameServer.handleLogin(token, paquete);
                    if (success) {
                        output.println("LOGIN_SUCCESS");
                    } else {
                        output.println("LOGIN_FAILURE");
                    }       break;
                    }
                case "register":{
                    boolean success = gameServer.handleRegister(token, paquete);
                    if (success) {
                        output.println("REGISTER_SUCCESS");
                    } else {
                        output.println("REGISTER_FAILURE");
                    }       break;
                    }
                case "create sala":{
                    String salaDatos = gameServer.handleCreateSala(paquete);    
                    output.println("SALA_CREATED " + salaDatos);
                    break;
                }
                case "join sala":{
                    String salaDatos = gameServer.handleJoinSala(token, paquete);   
                    if(!"JOIN_FAILURE".equals(salaDatos))
                        output.println("SALA_JOINED "+ salaDatos);
                    else
                        output.println(salaDatos);
                    break;
                }
                case "send message sala":{
                    gameServer.handleMessageSala(paquete);    
                    output.println("");
                    break;
                }
                default:
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(serverEventsListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
