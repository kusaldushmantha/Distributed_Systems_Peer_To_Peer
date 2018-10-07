package udpclient;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

import static udpclient.Printer.*;
import static udpclient.Util.*;

public class Client {

    public static int myPort=5556;
    public static String myIp;
    public static String myUserName;

    public static DatagramSocket socket;

    public static int bs_port=55555;
    public static String bs_ip;
    public static boolean registered=false;

    public static int gossipThreadStartingDelay=1000; //10s
    public static int gossipPeriod =10000; //10s

    private static HashMap<String,Node> routingTable= new HashMap<String,Node>();

    public static ArrayList<String> selectedFiles=new ArrayList<>();

    public static String filepath= "Resources/File Names.txt";

    public static boolean okToListen=false;

    public static Thread listeningThread;
    private static Thread cliThread;
    private static Thread gossipThread;

    private static Scanner scanner;

    public static Status rgStatus=new Status();

    public static void main(String[] args)  {

        scanner =new Scanner(System.in);
        myIp=getMyIp();

        printName("Distributed System Client Application");

        while (true) {
            print("\nEnter port \t[" + myPort + "]\t: ");
            String inPort=scanner.nextLine();
            if (inPort.equals("")) {
                try {
                    socket = new DatagramSocket(myPort);
                    break;
                }catch (BindException e){
                    print_ng("Permission denied. Use a different port");
                    myPort++;
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    myPort= Integer.parseInt(inPort);
                    socket = new DatagramSocket(myPort);
                    break;
                }catch (BindException e){
                    print_ng("Permission denied. Use a different port");
                }catch (NumberFormatException e){
                    print_ng("Wrong input for port");
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }

        myUserName=getMyHostname();
        print("Enter username \t["+myUserName+"]\t: ");
        String inName=scanner.nextLine();
        if (!inName.equals("")) {
            myUserName=inName;
        }


        listeningThread = new Thread(Client::lookForMessages);
        listeningThread.start();

        print_nn("\nIP address : " + myIp + " \tPort : " +myPort + " \tUsername : " +myUserName, "\033[0;1m");


        readAndGetRandomFiles(filepath); // get five files from File Names.txt

        cliThread = new Thread(Client:: handleInterfaceInput);
        cliThread.start();

        gossipThread=new Thread(Client::sendGossips);
        gossipThread.start();

    }

    private static void handleInterfaceInput() {

        while (true){
            try {
                String input = scanner.nextLine();
                if (input.equals(""))continue;
                StringTokenizer st = new StringTokenizer(input, " ");
                switch (st.nextToken()) {
                    case "reg":
                        SendingMessageHandler.registerToBS(st.nextToken());
                        break;
                    case "unreg":
                        SendingMessageHandler.unregisterFromBS();
                        break;
                    case "table":
                        printRoutingTable();
                        break;
                    case "join":
                        SendingMessageHandler.joinToSystem();
                        break;
                    case "leave":
                        SendingMessageHandler.leaveTheSystem();
                        break;
                    case "search":
                        SendingMessageHandler.searchFile(st);
                        break;
                    case "files":
                        printSelectedFiles();
                        break;

                    case "regl":
                        SendingMessageHandler.registerToBSonSameIp();
                        break;
                    case "help":
                        printHelp(getHelpText());
                        break;
                    case "setport":
                        changeMyPort(st.nextToken());
                        break;
                    case "exit":
                        SendingMessageHandler.exit();
                        System.exit(0);
                        break;
                    default:
                        print_nng("Not a valid command");
                        break;
                }
            }catch (NoSuchElementException e){
                print_nng("Error in command");
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
                print_Receiving(msg,incoming);

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
                            ReceivingMessageHandler.fileSearchOk(st,msg, incoming);
                            break;

                        case "JOIN":
                            ReceivingMessageHandler.joiningOfNeighbour(st, incoming);
                            break;

                        case "LEAVE":
                            ReceivingMessageHandler.leavingOfNeighbour(st, incoming);
                            break;

                        case "SER":
                            ReceivingMessageHandler.searchFileForNeighbour(st, incoming,msg);
                            break;
                        case "GOSSIP":
                            ReceivingMessageHandler.handleGossip(st,incoming,msg);

                    }
                }catch (NoSuchElementException e){
                    print_nng("Wrong message");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public static void sendGossips(){

        Timer timer=new Timer();

        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                SendingMessageHandler.sendGossips();
            }
        };

        timer.schedule(task,gossipThreadStartingDelay, gossipPeriod);
    }

    public static void addToRoutingTable(Node node,String addedBy){
        if (routingTable.containsKey(node.getKey())){
            return;
        }else {
            routingTable.put(node.getKey(),node);
            rgStatus.routingTableStatus_plus1();
            print_ng("Added Neighbour to routing table <- "+node.getKey()+" by "+addedBy);
        }
    }
    public static void removeFromRoutingTable(Node node){
        if (routingTable.containsKey(node.getKey())){
            routingTable.remove(node.getKey());
            print_ng("Removing Neighbour from routing table : "+node.getKey());
        }
    }

    public static HashMap<String, Node> getRoutingTable(){
        return routingTable;
    }

}
