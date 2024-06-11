/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Hp
 */
public class ConnectionsThread extends Thread {
    private ServerSocket serverSocket;
    private EventsListener eventsListener;

    public ConnectionsThread(ServerSocket serverSocket, EventsListener eventsListener) throws IOException {
        this.serverSocket = serverSocket;
        this.eventsListener = eventsListener;
    }

    @Override
    public void run() {
        try {
            System.out.println("Escuchando conexiones en el puerto: " + serverSocket.getLocalPort());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado desde: " + clientSocket.getInetAddress());
                eventsListener.handleClientConnected(new ClientConnectedEvent(this, clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
