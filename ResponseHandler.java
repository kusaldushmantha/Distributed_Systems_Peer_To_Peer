package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

public class ResponseHandler implements Runnable {

    public volatile String response;
    public volatile boolean isValueSet = false;
    public volatile Map<Integer, Map<String, Integer>> routingTable;

    DatagramSocket socket;

    ResponseHandler(DatagramSocket socket,
                    Map<Integer, Map<String, Integer>> routingTable){
        this.socket = socket;
        this.routingTable = routingTable;
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
            String command = this.response.split(" ")[1].trim();
            if(command.equals("JOIN")){
                sendJoinStatus(packet, 0);
            }else{
                isValueSet = true;
                while (isValueSet){ }
            }
        }
    }

    private void sendJoinStatus(DatagramPacket packet, int status){
        String joinResponse = "";
        InetAddress senderAddress = packet.getAddress();
        int senderPort = packet.getPort();
        if(status == 0){
            joinResponse = "JOINOK " + Integer.toString(status);
        }else{
            joinResponse = "JOINOK 9999";
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
