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

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public class ConnectionsChecker extends Thread {
    private TCPSocketServer tcpSocketServer;
    
    public ConnectionsChecker(TCPSocketServer tcpSocketServer) {
        this.tcpSocketServer = tcpSocketServer;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);
                checkClientsConnectivity();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void checkClientsConnectivity() {
        Map<String, SocketClient> clients = tcpSocketServer.getClients();
        clients.entrySet().removeIf(entry -> {
            String clientAddress = entry.getKey().replace("/", "");
            SocketClient client = entry.getValue();
            try {
                InetAddress inetAddress = InetAddress.getByName(clientAddress);
                if (!inetAddress.isReachable(3000)) {
                    client.close();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                client.close();
                return true;
            }
            return false;
        });
    }
}
