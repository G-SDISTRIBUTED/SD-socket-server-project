/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

import com.mycompany.paquete.Paquete;
import com.mycompany.paquete.Usuario;
import com.mycompany.paquete.Sala;
import java.time.LocalDateTime;
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
    
    public boolean handleLogin(String token, Paquete paquete){
        Usuario usuario = paquete.getUsuario();
        String username = usuario.getUsername();
        String password = usuario.getPassword();
        boolean success = databaseManager.loginUser(username, password);
        
        if(success){
            for (Usuario user : listUsuarios) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    user.addSocketToken(token);
                    return true;
                }
            }
            mostrarTokenSalas();
        }
        return success;
    }
    
    public boolean handleRegister(String token, Paquete paquete){
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
    
    public void handleCreateSala(Paquete paquete){
        Sala sala = paquete.getSala();
        sala.setToken(Objects.hash(sala));
        addSala(sala);
        mostrarTokenSalas();
    }
}
