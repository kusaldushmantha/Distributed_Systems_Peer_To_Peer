package com.company;

import java.io.IOException;
import java.util.*;
import java.net.*;

import static com.company.Util.*;

public class Client {

    public static int myPort;
    public static String myIp;
    public static String myUserName;

    private static Scanner scanner;

    public static DatagramSocket socket;

    public static int bs_port=55555;
    public static String bs_ip;

    public static HashMap<String,Node> routingTable= new HashMap<String,Node>();

    public static boolean okToListen=false;

    static Thread listeningThread;
    static Thread cliThread;

    public static void main(String[] args) throws SocketException {

        scanner =new Scanner(System.in);

        myIp=getMyIp();

        while (myPort==0){
            try {
                echo("Enter port : ");
                myPort= Integer.parseInt(scanner.next());
                socket = new DatagramSocket(myPort);
            }catch (BindException e){
                echon("Permission denied. Use a different port\n");
                myPort=0;
            }catch (NumberFormatException e){
                echon("Wrong input for port\n");
                myPort=0;
            }
        }

        echo("Enter username : ");
        myUserName= scanner.next();
        scanner.nextLine();

        listeningThread = new Thread(Client::lookForMessages);
        listeningThread.start();

        echoni("\033[1;30m"+"IP address : " + myIp + " \tPort : " +myPort + " \tUsername : " +myUserName+"\033[0m\n");

        cliThread = new Thread(Client:: handleInterfaceInput);
        cliThread.start();

    }

    private static void handleInterfaceInput() {

        while (true){
            try {
                String input = scanner.nextLine();
                StringTokenizer st = new StringTokenizer(input, " ");
                switch (st.nextToken()) {
                    case "exit":
                        SendingMessageHandler.exit();
                        System.exit(0);
                        break;
                    case "setport":
                        changeMyPort(st.nextToken());
                        break;
                    case "table":
                        showRoutingTable();
                        break;
                    case "reg":
                        echon("Register command");
                        SendingMessageHandler.registerToBS(st.nextToken());
                        break;
                    case "unreg":
                        echon("Unregister command");
                        SendingMessageHandler.unregisterFromBS();
                        break;
                    case "join":
                        echon("Joining command");
                        SendingMessageHandler.joinToSystem();
                        break;
                    case "leave":
                        echon("Leaving command");
                        SendingMessageHandler.leaveTheSystem();
                        break;
                    case "search":
                        echon("File search command");
                        SendingMessageHandler.searchFile(st.nextToken());
                        break;
                }
            }catch (NoSuchElementException e){
                echoni("Wrong command");
            }

        }
    }


    public static void lookForMessages() {
        okToListen=true;
        while (okToListen){
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            try {

                socket.receive(incoming);
                byte[] data = incoming.getData();
                String msg = new String(data, 0, incoming.getLength());
                echon("\033[0;32m"+msg+"\033[0m");
                StringTokenizer st = new StringTokenizer(msg, " ");
                String length= st.nextToken();
                try {
                    switch (st.nextToken()) {

                    // messages from BootstrapServer
                        case "REGOK":
                            ReceivingMessageHandler.registrationOk(st);
                            break;

                        case "UNROK":
                            ReceivingMessageHandler.unregistrationOk(st);
                            break;
                    // messages from neighbours
                        case "JOINOK":
                            ReceivingMessageHandler.joinOk(st, incoming);
                            break;

                        case "LEAVEOK":
                            ReceivingMessageHandler.leaveOk(st, incoming);
                            break;

                        case "SEROK":
                            ReceivingMessageHandler.fileSearchResult(st, incoming);
                            break;

                        case "JOIN":
                            ReceivingMessageHandler.joiningOfNeighbour(st, incoming);
                            break;

                        case "LEAVE":
                            ReceivingMessageHandler.leavingOfNeighbour(st, incoming);
                            break;

                        case "SER":
                            ReceivingMessageHandler.searchFileForNeighbour(st, incoming);


                    }
                }catch (NoSuchElementException e){
                    echon("Wrong message");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
