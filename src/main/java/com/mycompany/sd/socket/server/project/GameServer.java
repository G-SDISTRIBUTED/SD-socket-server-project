/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

import com.mycompany.paquete.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Hp
 */
public class GameServer {
    List<Sala> listSalas= new ArrayList<>();
    List<Usuario> listUsuarios= new ArrayList<>();
    private DatabaseManager databaseManager = new DatabaseManager();
    private SocketServerForm serverForm;

    public GameServer(SocketServerForm serverForm) {
        this.serverForm = serverForm;
    }
    
    public void addSala(Sala sala){
        listSalas.add(sala);
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
    
    public List<Sala> getListSalas(){
        return listSalas;
    }
    
    public void mostrarTokenSalas(){
        String message = getListSalas().toString();
        serverForm.mostrarTokenSalas(message);
    }
    
    public String handleCreateSala(Paquete paquete){
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
        addSala(sala);
        mostrarTokenSalas();
        return token+" "+sala.getName();
    }
    
    public String handleJoinSala(Integer token, Paquete paquete){
        Integer tokenSala =  paquete.getSala().getToken();
        Usuario usuario = paquete.getUsuario();
        usuario.addSocketToken(token);
        for (Sala sala : listSalas) {
            if (sala.getToken().equals(tokenSala)) {
                sala.addJugador(usuario);
                mostrarTokenSalas();
                return tokenSala+" "+sala.getName();
            }
        }
        return "JOIN_FAILURE";
    }
    
    public void handleMessageSala(Paquete paquete) {
        
    }
}
