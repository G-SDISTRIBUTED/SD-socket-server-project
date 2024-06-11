/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

import java.net.Socket;
import java.util.EventObject;

/**
 *
 * @author Hp
 */
public class ClientConnectedEvent  extends EventObject {
    private Socket clientSocket;

    public ClientConnectedEvent(Object source, Socket clientSocket) {
        super(source);
        this.clientSocket = clientSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
