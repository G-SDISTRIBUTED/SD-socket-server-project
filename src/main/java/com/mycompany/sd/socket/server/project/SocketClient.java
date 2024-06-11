/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private Thread messagesThread;
    private EventsListener eventsListener;
    private String token = "";

    public SocketClient(Socket socket, EventsListener eventsListener) {
        this.socket = socket;
        this.eventsListener = eventsListener;
        this.messagesThread = new Thread(this::escucharCliente);
    }

    public Socket getSocket(){
        return socket;
    }
    
    public void cambiarToken(String token) {
        this.token = token;
    }

    public void startClient() {
        this.messagesThread.start();
    }

    public boolean isConnected() {
        return !socket.isClosed() && socket.isConnected();
    }
    
    public String getAddress() {
        return socket.getInetAddress().toString();
    }

    private void escucharCliente() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String text;
            while ((text = input.readLine()) != null) {
                eventsListener.handleMessageReceived(new MessageReceivedEvent(this, text, token));
            }
            if(text == null){
                System.out.println("El cliente se desconectó");
                eventsListener.handleClientDisconnected(new ClientDisconnectedEvent(this, socket, token));
            }
        } catch (IOException e) {
            System.out.println("El cliente se desconectó");
            eventsListener.handleClientDisconnected(new ClientDisconnectedEvent(this, socket, token));
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (messagesThread != null && messagesThread.isAlive()) {
                messagesThread.interrupt();
            }
            eventsListener.handleClientDisconnected(new ClientDisconnectedEvent(this, socket, token));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
