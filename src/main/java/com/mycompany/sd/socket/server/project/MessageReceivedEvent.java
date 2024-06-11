/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sd.socket.server.project;

import java.util.EventObject;

/**
 *
 * @author Hp
 */
public class MessageReceivedEvent extends EventObject {
    private String message;
    private String token;

    public MessageReceivedEvent(Object source, String message, String token) {
        super(source);
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
