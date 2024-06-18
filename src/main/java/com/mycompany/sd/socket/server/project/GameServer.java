/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

import com.google.gson.Gson;
import com.mycompany.paquete.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hp
 */
public class GameServer {
    List<Sala> listRooms= new ArrayList<>();
    List<Usuario> listUsuarios= new ArrayList<>();
    private DatabaseManager databaseManager = new DatabaseManager();
    private SocketServerForm serverForm;

    public GameServer(SocketServerForm serverForm) {
        this.serverForm = serverForm;
    }
    
    public void addRoom(Sala sala){
        listRooms.add(sala);
    }
    public void addUsuario(Usuario usuario){
        listUsuarios.add(usuario);
    }
    
    public boolean handleLogin(Integer token, Paquete paquete){
        Usuario usuario = paquete.getUsuario();
        String username = usuario.getUsername();
        String password = usuario.getPassword();
        boolean success = databaseManager.loginUser(username, password);
        
        if(success){
            //MODIFICAR ESTA PARTE
            usuario.addSocketToken(token);
            addUsuario(usuario);
            mostrarTokenSalas();
//            for (Usuario user : listUsuarios) {
//                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
//                    user.addSocketToken(token);
//                    return true;
//                }
//            }
            mostrarTokenSalas();
        }
        return success;
    }
    
    public boolean handleRegister(Integer token, Paquete paquete){
        Usuario usuario = paquete.getUsuario();
        String username = usuario.getUsername();
        String password = usuario.getPassword();
        boolean success = databaseManager.registerUser(username, password);
        if(success){
            usuario.addSocketToken(token);
            addUsuario(usuario);
            mostrarTokenSalas();
        }
        return success;
    }
    
    public List<Sala> getListRooms(){
        return listRooms;
    }
    
    public void mostrarTokenSalas(){
        List<Sala> list = getListRooms();
        String s="";
        for(Sala sala: list){
            s+="Sala: "+sala.getToken()+"\n";
        }
        serverForm.mostrarTokenSalas(s);
    }
    
    public Sala handleCreateRoom(Paquete paquete){
        Sala sala = paquete.getSala();
        Usuario creador = sala.getCreador();
        Integer token = Objects.hash(sala);
        sala.setToken(token);
        for (Usuario user : listUsuarios) {
            if (user.getUsername().equals(creador.getUsername()) && user.getPassword().equals(creador.getPassword())) {
                sala.setCreador(user);
                break;
            }
        }        
        addRoom(sala);
        mostrarTokenSalas();
        return sala;
    }
    
    public boolean handleRequestJoinRoom(Integer token, Paquete paquete){
        Integer tokenSala =  Integer.valueOf((String)paquete.getParams().getFirst());
        Usuario usuario = (Usuario) paquete.getUsuario();
        usuario.addSocketToken(token);
        for (Sala sala : listRooms) {
            if (sala.getToken().equals(tokenSala)) {
                PrintWriter out = null;
                try {
                    Usuario creador = sala.getCreador();
                    Integer creadorIdSocket = creador.getSocketTokens().getLast();
                    Socket creadorSocket = TCPSocketServer.getClient(creadorIdSocket).getSocket();
                    out = new PrintWriter(creadorSocket.getOutputStream(), true);
                    Paquete paqueteRequest = new Paquete();
                    paqueteRequest.setComando("RECIVING REQUEST TO JOIN ROOM");
                    paqueteRequest.setUsuario(usuario);
                    Gson gson = new Gson();
                    String response = gson.toJson(paqueteRequest);
                    out.println(response);
                    return true;
                } catch (IOException ex) {
                    Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }
    
    public void handleJoinRequestAccepted(Paquete paquete){
        Integer tokenRoom =  Integer.valueOf((String)paquete.getParams().getFirst());
        Usuario jugador = (Usuario) paquete.getUsuario();
        for (Sala sala : listRooms) {
            if (sala.getToken().equals(tokenRoom)) {
                sala.addJugador(jugador);
                
                PrintWriter out = null;
                try {
                    Integer jugadorIdSocket = jugador.getSocketTokens().getLast();
                    Socket jugadorSocket = TCPSocketServer.getClient(jugadorIdSocket).getSocket();
                    out = new PrintWriter(jugadorSocket.getOutputStream(), true);
                    Paquete paqueteRequest = new Paquete();
                    paqueteRequest.setComando("JOIN ROOM ACCEPTED");
                    paqueteRequest.setSala(sala);
                    Gson gson = new Gson();
                    String response = gson.toJson(paqueteRequest);
                    out.println(response);
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    out.close();
                }
            }
        }
    }
    
    public void handleJoinRoom(Paquete paquete){
        List<Object> params = paquete.getParams();
        Integer tokenRoom = (Integer) params.getFirst();
        Usuario jugador = (Usuario) params.getLast();
        for (Sala sala : listRooms) {
            if (sala.getToken().equals(tokenRoom)) {
                sala.addJugador(jugador);
                //Enviarle un mensaje al jugador de exito para que entre a la sala
            }
        }
    }
    
    public void handleMessageRoom(Paquete paquete) {
        
    }
}
