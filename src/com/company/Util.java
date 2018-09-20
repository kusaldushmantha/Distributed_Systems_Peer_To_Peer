package com.company;

import java.io.IOException;
import java.net.*;
import static com.company.Client.*;

public class Util {

    public static String getMyIp() {
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static  void changeMyPort(String port) {
        if (routingTable.size()!=0){
            echoni("Port changing denied. Route table not empty ");
        }else {
            try {
                myPort = Integer.parseInt(port);
                okToListen = false;
                socket = new DatagramSocket(myPort);
                listeningThread = new Thread(Client::lookForMessages);
                listeningThread.start();
                echoni("Changed port to " + port);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendPacket(String ip,int port,String msg){
        try {
            echon("Sending: "+"\033[0;34m"+msg+"\033[0m");
            DatagramPacket dgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(ip), port);
            socket.send(dgPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showRoutingTable() {
        routingTable.forEach((key, value) -> echon(key + "   |   " + value));
        echoni("");
    }

    public static String formatMessage(String msg){
        String msg_formatted = String.format("%04d", msg.length() + 5) + " " + msg;
        return msg_formatted;
    }

    public static void echo(String msg){
        System.out.print(msg);
    }

    public static void echon(String msg){
        System.out.println(msg);
    }

    public static void echoni(String msg){
        System.out.print(msg+"\n\n> ");
    }
}
