/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

import java.net.Socket;
import java.util.EventObject;

public class ClientDisconnectedEvent extends EventObject {
    private Socket clientSocket;
    private Integer clientToken;

    public ClientDisconnectedEvent(Object source, Socket clientSocket, Integer clientToken) {
        super(source);
        this.clientSocket = clientSocket;
        this.clientToken = clientToken;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public Integer getClientToken() {
        return clientToken;
    }
}
