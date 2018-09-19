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

    public static String formatMessage(String msg){
        String msg_formatted = String.format("%04d", msg.length() + 5) + " " + msg;
        return msg_formatted;
    }


    public static void echon(String msg){
        System.out.println(msg);
    }

    public static void echo(String msg){
        System.out.print(msg);
    }
}
