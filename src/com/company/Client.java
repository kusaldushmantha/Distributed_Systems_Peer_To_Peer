package com.company;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;

import static com.company.Util.*;

public class Client {

    public static int myPort;
    public static String myIp;
    public static String myUserName;

    private static Scanner scanner;

    public static DatagramSocket socket;

    public static int bs_port=55555;
    public static String bs_ip;


    public static void main(String[] args) throws SocketException {

        scanner =new Scanner(System.in);

        echo("enter port : "); myPort= scanner.nextInt();
        echo("enter username : "); myUserName= scanner.next();
        scanner.nextLine();
        socket = new DatagramSocket(myPort);
        myIp=getMyIp(); echon("\033[1;30m"+"ip address : " + myIp + " \tport : " +myPort + " \tusername : " +myUserName+"\033[0m\n");

        Thread thread = new Thread(Client::lookForMessages);
        thread.start();

        Thread thread2 = new Thread(Client:: handleInterfaceInput);
        thread2.start();

    }

    private static void handleInterfaceInput() {

        while (true){
            echo("> ");String input = scanner.nextLine();
            StringTokenizer st = new StringTokenizer(input, " ");
            try {
                switch (st.nextToken()) {
                    case "reg":
                        echon("Register command...");
                        SendingMessageHandler.registerToBS(st.nextToken());
                        break;
                    case "unreg":
                        echon("Unregister command");
                        SendingMessageHandler.unregisterFromBS();
                        break;
                }
            }catch (NoSuchElementException e){
                echon("Wrong command");
            }

        }
    }

    private static void lookForMessages() {
        while (true){
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(incoming);
                byte[] data = incoming.getData();
                String msg = new String(data, 0, incoming.getLength());
                echo("\033[0;32m"+msg+"\033[0m"+"\n\n> ");
                StringTokenizer st = new StringTokenizer(msg, " ");
                String length= st.nextToken();
                try {
                    switch (st.nextToken()) {
                        case "REGOK":
                            ReceivingMessageHandler.registrationOk(st);
                            break;

                        case "UNROK":
                            ReceivingMessageHandler.unregistrationOk(st);
                            break;
                    }
                }catch (NoSuchElementException e){
                    echon("Wrong command");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
