package Distributed_Systems_Peer_To_Peer;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client{

    private String clientID;
    private String ipAddress;
    private String port;
    private String username;
    private byte[] responseFromServer;
    private DatagramSocket clientSocket;
    private DatagramPacket packet;
    private ResponseHandler responseHandler;
    private Thread responseHandlerThread;
    public volatile Map<String, String> routingTable;
    private List<String> fileList = new ArrayList<>();

    private Client(String ipAddress,
                   String port,
                   String username){
        this.ipAddress = ipAddress;
        this.port = port;
        this.username = username;
        this.responseFromServer = new byte[1024];
        genarateFileList();
        
        try {
            routingTable = new HashMap<>();
            
            this.clientSocket = new DatagramSocket(Integer.parseInt(this.port));
            responseHandler = new ResponseHandler(this.clientSocket, routingTable, this.username, this.fileList);
            responseHandlerThread = new Thread(responseHandler);
            responseHandlerThread.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private String serverCommand(String serverCmd) {
        try {
            byte[] bytesToSend = serverCmd.getBytes();
            InetAddress inetAddress = InetAddress.getByName("localhost");
            this.packet = new DatagramPacket(bytesToSend, bytesToSend.length, inetAddress, 55555);
            this.clientSocket.send(this.packet);

            while (!this.responseHandler.isValueSet){}
            this.responseHandler.isValueSet = false;
            return this.responseHandler.response;
        }catch (Exception e){
            System.out.println(e);
            return "";
        }
    }

    private String nodeCommand(String nodeCmd) {
        try {
            String toConnectNodeIP = nodeCmd.split(" ")[2].trim();
            int toConnectNodePort = Integer.parseInt(nodeCmd.split(" ")[3].trim());

            byte[] bytesToSend = nodeCmd.getBytes();
            InetAddress inetAddress = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(bytesToSend, bytesToSend.length, inetAddress, toConnectNodePort);
            this.clientSocket.send(packet);
            System.out.println("node command");
            while (!this.responseHandler.isValueSet){}
            System.out.println("node command next");
            this.responseHandler.isValueSet = false;
            return this.responseHandler.response;
        }catch (Exception e){
            System.out.println(e);
            return "";
        }
    }

    private void registerOnServer(){
        String regCmd = "REG " + this.ipAddress + " " + this.port + " " + this.username;
        String serverResponseStatus = serverCommand(formatString(regCmd));
        if(serverResponseStatus.isEmpty()){
            System.out.println("Error while sending server command to register");
        }else {
            int activeNodeCount = Integer.parseInt(serverResponseStatus.split(" ")[2].trim());
            if(activeNodeCount == 0){
                System.out.println("This is the first node in the network");
            }else if(activeNodeCount == 9999){
                System.out.println("Failed, There is some error in the command");
            }else if(activeNodeCount == 9998){
                System.out.println("Failed, Client already registered, Unregister first");
            }else if(activeNodeCount == 9997){
                System.out.println("Failed, Registered to another user, try a different IP and port");
            }else if(activeNodeCount == 9996) {
                System.out.println("Failed, Cannot Register, BS Full");
            }else{
                System.out.println("More than 2 Nodes in the Network");
                String[] serverResponse = serverResponseStatus.split(" ");
                for(int i=0; i<=activeNodeCount; i+=2){
                    String nodeIpaddress = serverResponse[3+i].trim();
                    String nodePort = serverResponse[4+i].trim();
                    joinToNode(nodeIpaddress, nodePort);
                }
            }
        }
    }


    private void unregisterFromServer(){
        String unregCmd = "UNREG " + this.ipAddress + " " + this.port + " " + this.username;
        String serverResponse = serverCommand(formatString(unregCmd));
        if(serverResponse.split(" ")[2].trim().equals("0")){
            System.out.println("Successfully unregistered");
        }else{
            System.out.println("Error while unregistering. FAILED");
        }
    }

    private void joinToNode(String ipAddress, String port){
        this.responseHandler.response = "";
        String joinString = "JOIN " + ipAddress + " " + port + " " + this.username;
        joinString = formatString(joinString);
        String joinResponse = nodeCommand(joinString);
        if(joinResponse.split(" ")[2].trim().equals("0")) {
            System.out.println("Join Network Successful");
            this.responseHandler.isValueSet = false;
        }else{
            System.out.println("Error while joining network");
            this.responseHandler.isValueSet = false;
        }
    }
    private void sendMSG(String msg){
        String joinString;
        try {
            joinString = "SER " +InetAddress.getLocalHost().getHostAddress()+ " " + this.port + " " + msg+" "+0;
            joinString = formatString(joinString);
            String joinResponse = nodeCommand(joinString);
        } catch (UnknownHostException ex) {
           return;
        }
//        String regCmd = "SER " + this.ipAddress + " " + this.port + " " + ipAddress+" "+port;
//        String serverResponseStatus = nodeCommand(formatString(regCmd));
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
    private void genarateFileList(){
        for(int x=0 ; x<3; x++){
           this.fileList.add(this.username+x);
        }
    }
    private void printRoutingTable(){
        System.out.println(this.routingTable);
    }
    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);

        System.out.print("Client IP Address: ");
        String ipAddress = scanner.next();
        System.out.print("Client PORT: ");
        String port = scanner.next();
        System.out.print("Client Username: ");
        String username = scanner.next();

        Client client = new Client(ipAddress,port,username);

        while (true){
            System.out.println("Client Command: ");
            String clientCmd = scanner.next();
            System.out.println();

            if(clientCmd.equals("REG")){
                client.registerOnServer();
                System.out.println("File set : "+client.fileList);
            }else if(clientCmd.equals("UNREG")){
                client.unregisterFromServer();
            }else if(clientCmd.equals("MSG")){
                System.out.print("Type your message : ");
                String message = scanner.next();
                client.sendMSG(message);
            }
        }
    }
    

}
