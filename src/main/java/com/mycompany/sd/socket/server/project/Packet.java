/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sd.socket.server.project;

/**
 *
 * @author Pc
 */

import java.sql.Timestamp;
        
public class Packet {
    private String command;
    private String params;
    private int clientId;
    private Timestamp fecha;
    
  public Packet() {}

  public Packet(String command, String params, int clientId, Timestamp fecha) {
    this.command = command;
    this.params = params;
    this.clientId = clientId;
    this.fecha = fecha;
  }

  public String getCommand() {
    return command;
  }

  public String getParams() {
    return params;
  }

  public int getClientId() {
    return clientId;
  }

  public Timestamp getFecha() {
    return fecha;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public void setFecha(Timestamp fecha) {
    this.fecha = fecha;
  }
}
