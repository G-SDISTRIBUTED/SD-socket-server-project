/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

/**
 *
 * @author Hp
 */
interface EventsListener {
    void handleClientConnected(ClientConnectedEvent event);
    void handleClientDisconnected(ClientDisconnectedEvent event);
    void handleMessageReceived(MessageReceivedEvent event);
}
