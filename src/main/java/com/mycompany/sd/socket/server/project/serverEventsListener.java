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
import java.util.List;

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
            Paquete paquete = gson.fromJson(message, Paquete.class);
            String comando = paquete.getComando();
        
            Paquete paqueteResponse = new Paquete();
            
            if (null != comando) switch (comando) {
                case "login":{
                    Usuario user = gameServer.handleLogin(token, paquete);
                    if (user != null) {
                        paqueteResponse.setComando("LOGIN_SUCCESS");
                        paqueteResponse.setUsuario(user);
                    } else {
                        paqueteResponse.setComando("LOGIN_FAILURE");
                    }
                    String response = gson.toJson(paqueteResponse);
                    output.println(response);
                    break;
                    }
                case "register":{
                    Usuario user = gameServer.handleRegister(token, paquete);
                    if (user != null) {
                        paqueteResponse.setComando("REGISTER_SUCCESS");
                        paqueteResponse.setUsuario(user);
                    } else {
                        paqueteResponse.setComando("REGISTER_FAILURE");
                    }
                    String response = gson.toJson(paqueteResponse);
                    output.println(response);
                    break;
                    }
                case "create room":{
                    paqueteResponse.setComando("ROOM_CREATED");
                    paqueteResponse.setSala(gameServer.handleCreateRoom(paquete));
                    String response = gson.toJson(paqueteResponse);
                    output.println(response);
                    break;
                }
                case "request to join room":{
                    boolean success = gameServer.handleRequestJoinRoom(paquete);   
                    if(success){
                        paqueteResponse.setComando("REQUEST SENT");
                    }else
                        paqueteResponse.setComando("REQUEST FAILED");
                    
                    String response = gson.toJson(paqueteResponse);
                    output.println(response);
                    break;
                }
                case "join request accepted":{
                    gameServer.handleJoinRequestAccepted(paquete);
                    break;
                }
                case "join request rejected": {
                    gameServer.handleJoinRequestRejected(paquete);
                    break;
                }
                case "get rooms":{
                    paqueteResponse.setComando("SENDING ROOMS");
                    String rooms = gson.toJson(gameServer.getListRooms());
                    paqueteResponse.addParam(rooms);
                    
                    String response = gson.toJson(paqueteResponse);
                    output.println(response);
                    break;
                }
                case "exit room":{
                    gameServer.handleExitRoom(paquete);   
                    paqueteResponse.setComando("LEFT THE ROOM");
                    String response = gson.toJson(paqueteResponse);
                    output.println(response);
                    break;
                }
                case "delete room":{
                    gameServer.handleDeleteRoom(paquete);
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
