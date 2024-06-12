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
    private Integer token;

    public MessageReceivedEvent(Object source, String message, Integer token) {
        super(source);
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public Integer getToken() {
        return token;
    }
}
