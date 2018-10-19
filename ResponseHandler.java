package Distributed_Systems_Peer_To_Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResponseHandler implements Runnable {

    public volatile String response;
    public volatile boolean isValueSet = false;
    public volatile Map<String, String> routingTable;

    DatagramSocket socket;
    String username;
    List<String> fileList;
    ResponseHandler(DatagramSocket socket,
                    Map<String, String> routingTable,
                    String username , List<String> fileList){
        this.socket = socket;
        this.routingTable = routingTable;
        this.username = username;
        this.fileList = fileList;
    }
    @Override
    public void run() {
        while (true) { 
            //this.isValueSet = false;
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
                this.isValueSet = true;

            }else if(command.equals("JOINOK")){
                String requesterIp = packet.getAddress().toString();
                int requesterPort = packet.getPort();
                String username = requestOnNode[3].trim();
                String ipAndPort = requesterIp + " " + requesterPort;
                addToRoutingTable(username, ipAndPort);
                System.out.println("Routing Table Size : " + Integer.toString(this.routingTable.size()));
                System.out.println(this.routingTable);
                System.out.println();
                this.isValueSet = true;

            }else if(command.equals("SER")){
                String result = searchForFile(requestOnNode[4].trim());
                
                String ipaddress = requestOnNode[2].trim();
                String port = requestOnNode[3].trim();
                    
                //initialize the hopes count
                int hops = Integer.parseInt(requestOnNode[5].trim());
                if(requestOnNode[3].trim().equals(this.socket.getLocalPort()+"")){
                        
                       hops = 0;
                }else{
                       hops++;
                }
                if(hops>20){
                    sendSearchStatus(ipaddress,Integer.parseInt(port),result,hops,9999);
                }
                else if(!result.equals("")){
                    sendSearchStatus(ipaddress,Integer.parseInt(port),result,hops,0);
                }else{
                    
                    // Address also need to equal
                   
                    Random random = new Random();
                    List<String> keys = new ArrayList<String>(this.routingTable.keySet());
                    String randomKey = keys.get( random.nextInt(keys.size()) );
                    String value = this.routingTable.get(randomKey);
                    String data = requestOnNode[1].trim()+" "+requestOnNode[2].trim()+" "+requestOnNode[3].trim()+" "+requestOnNode[4].trim()+" "+hops;
                    sendMSG(formatString(data),value.split(" ")[0].substring(1).trim(),value.split(" ")[1].trim());
                }
            }else if(command.equals("SEROK")){
                this.isValueSet = true;
                System.out.println("Client Command: ");
            }
            else{
                this.isValueSet = true;
                while (this.isValueSet){ }
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
        System.out.println("send join status");
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
    
    private void sendSearchStatus(String ipaddress, int senderPort, String results, int hops,int status){
        System.out.println("send serach status");
        String joinResponse = "";
        try {
        if(status == 0){
            joinResponse = "SEROK " +InetAddress.getLocalHost().getHostAddress()+" "+this.socket.getLocalPort()+" "+hops+" "+results;
        }else{
            joinResponse = "SEROK 9999 " + this.username;
        }
            InetAddress senderAddress = InetAddress.getByName("localhost");
            byte[] joinStatus = formatString(joinResponse).getBytes();
            DatagramPacket sendPacket = new DatagramPacket(joinStatus, joinStatus.length, senderAddress, senderPort);
            this.socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            this.isValueSet = false;
            System.out.println("Client Command: ");
        }
    }
    private void sendMSG(String nodeCmd,String nodeIP, String nodePort){
        try{
            String toConnectNodeIP = nodeIP;
            int toConnectNodePort = Integer.parseInt(nodePort);
            byte[] bytesToSend = nodeCmd.getBytes();
            InetAddress inetAddress = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(bytesToSend, bytesToSend.length, inetAddress, toConnectNodePort);
            this.socket.send(packet);
        }catch (Exception e){
            System.out.println(e);
        }finally {
            
            this.isValueSet = false;
            System.out.println("Client Command: ");
        }
    }
    private String searchForFile(String file){
        String result = "";
        for(int r =0; r<this.fileList.size();r++){
            if(this.fileList.get(r).length()>=file.length()){
                for(int x=0;x<this.fileList.get(r).length()-file.length();x++){
                    if(file.equals(this.fileList.get(r).subSequence(x, x+file.length()))){
                        result+=this.fileList.get(r) + " ";
                    }
                }
            }else{
                continue;
            }
        }
        return result;
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