package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

public class ResponseHandler implements Runnable {

    public volatile String response;
    public volatile boolean isValueSet = false;
    public volatile Map<String, String> routingTable;

    DatagramSocket socket;
    String username;

    ResponseHandler(DatagramSocket socket,
                    Map<String, String> routingTable,
                    String username){
        this.socket = socket;
        this.routingTable = routingTable;
        this.username = username;
    }

    @Override
    public void run() {
        while (true) {
            isValueSet = false;
            byte[] responseFromServer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(responseFromServer, responseFromServer.length);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.response = new String(packet.getData());
            System.out.println("Response handler: " + this.response);
            System.out.println();
            String[] requestOnNode = this.response.split(" ");
            String command = requestOnNode[1].trim();

            if(command.equals("JOIN")){
                String requesterIp = packet.getAddress().toString();
                int requesterPort = packet.getPort();
                String username = requestOnNode[4].trim();
                String ipAndPort = requesterIp + " " + requesterPort;
                int status = addToRoutingTable(username, ipAndPort);
                sendJoinStatus(packet, status);
                System.out.println("Routing Table Size : " + Integer.toString(this.routingTable.size()));
                System.out.println(this.routingTable);
                System.out.println();
                isValueSet = true;

            }else if(command.equals("JOINOK")){
                String requesterIp = packet.getAddress().toString();
                int requesterPort = packet.getPort();
                String username = requestOnNode[3].trim();
                String ipAndPort = requesterIp + " " + requesterPort;
                addToRoutingTable(username, ipAndPort);
                System.out.println("Routing Table Size : " + Integer.toString(this.routingTable.size()));
                System.out.println(this.routingTable);
                System.out.println();
                isValueSet = true;

            }else{
                isValueSet = true;
                while (isValueSet){ }
            }
        }
    }

    private int addToRoutingTable(String nodeID, String ipAndPort) {
        this.routingTable.put(nodeID , ipAndPort);
        return 0;
    }

    private int removeFromRoutingTable(String nodeID){
        this.routingTable.remove(nodeID);
        return 0;
    }

    private void sendJoinStatus(DatagramPacket packet, int status){
        String joinResponse = "";
        InetAddress senderAddress = packet.getAddress();
        int senderPort = packet.getPort();
        if(status == 0){
            joinResponse = "JOINOK " + Integer.toString(status)+" "+this.username;
        }else{
            joinResponse = "JOINOK 9999 " + this.username;
        }
        byte[] joinStatus = formatString(joinResponse).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(joinStatus, joinStatus.length, senderAddress, senderPort);
        try {
            this.socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            this.isValueSet = false;
            System.out.println("Client Command: ");
        }
    }

    private String formatString(String currentString){
        int charactersInCurrentString = Integer.toString(currentString.length()).length();
        if(charactersInCurrentString < 4){
            int missingCharacters = 4 - charactersInCurrentString;
            String lengthString = "";
            for(int i=0; i<missingCharacters; i++){
                lengthString = lengthString.concat("0");
            }
            lengthString = lengthString.concat(Integer.toString(currentString.length()+5));
            return lengthString + " "+ currentString;
        }else{
            return Integer.toString(currentString.length()+5) + " " +currentString;
        }
    }
}
