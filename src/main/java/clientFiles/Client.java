package clientFiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static clientFiles.Util.*;

public class Client {

    public static int defaultPort =1111;
    public static String myIp;
    public static String myName;

    public static DatagramSocket socket;

    public static int bs_port=55555;
    public static String bs_ip;
    public static boolean isRegistered =false;

    public static int gossipInterval =5000;
    public static int fileCount = 5;

    private static HashMap<String,Node> routingTable= new HashMap<String,Node>();
    private static HashMap<String,Node> activeNodes= new HashMap<String,Node>();

    public static ArrayList<String> selectedFiles=new ArrayList<>();

    public static String filepath= "Movie Files.txt";

    public static int hopsCount = 3;  //hops count in search

    public static int maxNeighbourcount = 2;

    public static boolean isListen =false;

    public static Thread listnerThread;
    private static Thread clientThread;
    private static Thread gossipThread;

    private static Scanner scanner;

    public static StatusHandler rgStatus=new StatusHandler();
    public static Random r = new Random();


    public static void main(String[] args)  {
        startClient(true);

    }

    public static void allocatePort(){
        int defaultPort= Client.defaultPort;
        while (!isPortAvailable(defaultPort)){
            defaultPort= Client.defaultPort++;
        }

        while (true) {
            System.out.print("Enter UDP port (default: " + defaultPort + "): ");
            String inPort=scanner.nextLine();
            if (inPort.equals("")) {
                try {
                    Client.defaultPort =defaultPort;
                    socket = new DatagramSocket(defaultPort);
                    break;
                }catch (BindException e){
                    System.out.println("ERROR ==> Permission denied. Use a different port!");
                    defaultPort++;
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    Client.defaultPort = Integer.parseInt(inPort);
                    socket = new DatagramSocket(Client.defaultPort);
                    break;
                }catch (BindException e){
                    System.out.println("ERROR ==> Permission denied. Use a different port!");
                }catch (NumberFormatException e){
                    System.out.println("ERROR ==> Please enter a valid port number");
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void startClient(boolean inputEnabled){
        scanner =new Scanner(System.in);
        myIp=getMyIp();

        allocatePort();

        myName =getMyHostname();

        System.out.print("Enter username (defualt: "+ myName +"): ");
        String inName=scanner.nextLine();
        if (!inName.equals("")) {
            myName =inName;
        }

        listnerThread = new Thread(Client::msgListner);
        listnerThread.start();

        addFilesToClient(filepath);

        if(inputEnabled){
            clientThread = new Thread(Client::readCommands);
            clientThread.start();
        }

        gossipThread=new Thread(Client::startGossiping);
        gossipThread.start();
    }

    private static void readCommands() {

        while (true){
            try {
                String input = scanner.nextLine();
                if (input.equals(""))continue;
                StringTokenizer st = new StringTokenizer(input, " ");
                switch (st.nextToken()) {
                    case "reg":
                        MessegeHandler.regNode(st.nextToken());
                        break;
                    case "unreg":
                        MessegeHandler.unregNode();
                        break;
                    case "table":
                        printRoutingTable();
                        break;
                    case "join":
                        MessegeHandler.joinNode();
                        break;
                    case "leave":
                        MessegeHandler.nodeLeave();
                        break;
                    case "search":
                        MessegeHandler.searchFile(st);
                        break;
                    case "files":
                        displayFiles();
                        break;
                    case "setport":
                        changePort(st.nextToken());
                        break;
                    case "exit":
                        MessegeHandler.exit();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("ERROR ==> Invalid Command!");
                        break;
                }
            }catch (NoSuchElementException e){
                System.out.println("Invalid command!");
            }

        }
    }


    public static void msgListner() {
        isListen =true;
        while (isListen){
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(incoming);
                byte[] data = incoming.getData();
                String msg = new String(data, 0, incoming.getLength());

                StringTokenizer st = new StringTokenizer(msg, " ");
                String length= st.nextToken();
//                System.out.println(msg);
                

                try {
                    switch (st.nextToken()) {

                    // messages from BootstrapServer
                        case "REGOK":
//                            printResponse(msg,incoming);
                            ResponseHandler.regOK(st);
                            break;

                        case "UNROK":
                            ResponseHandler.unregOK(st);
//                            printResponse(msg,incoming);
                            break;

                    // messages from neighbours
                        case "JOINOK":
                            ResponseHandler.joinOk(st, incoming);
//                            printResponse(msg,incoming);
                            break;

                        case "LEAVEOK":
                            ResponseHandler.leaveOk(st, incoming);
//                            printResponse(msg,incoming);
                            break;

                        case "SEROK":
                            ResponseHandler.searchOK(st,msg, incoming);
//                            printResponse(msg,incoming);
                            break;

                        case "JOIN":
                            ResponseHandler.neighbourJoin(st, incoming);
//                            printResponse(msg,incoming);
                            break;

                        case "LEAVE":
                            ResponseHandler.neighbourLeave(st, incoming);
//                            printResponse(msg,incoming);
                            break;

                        case "SER":
                            ResponseHandler.neighbourFileSearch(st, incoming,msg);
//                            printResponse(msg,incoming);
                            break;
                        case "GOSSIP":
                            ResponseHandler.gossipHandler(st,incoming,msg);
                            break;
                        case "PULSE":
                            ResponseHandler.pulseHandler(st,incoming,msg);
                            break;
                        case "PULSEOK":
                            ResponseHandler.updateActiveTable(st,incoming,msg);
                            break;

                    }
                }catch (NoSuchElementException e){
                    System.out.println("Invalid Message!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addFilesToClient(String filepath) {

        InputStream inputStream = Util.class.getClassLoader().getResourceAsStream(filepath);

        List<String> files =
                new BufferedReader(new InputStreamReader(inputStream,
                        StandardCharsets.UTF_8)).lines().collect(Collectors.toList());

        Collections.shuffle(files);

        Random random = new Random();
        int start=random.nextInt(files.size()-fileCount);
        for (int i=0; i<fileCount; i++) {
            start += i;
            selectedFiles.add(files.get(start));
        }
    }


    public static void startGossiping(){

        Timer t=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                MessegeHandler.gossipNodes();
            }
        };
        t.schedule(task,gossipInterval, gossipInterval);
    }

//    public static void addToRoutingTable(Node node,String addedBy){
//        if (routingTable.containsKey(node.getKey())){
//            return;
//        }else {
//            if (routingTable.size() >= maxNeighbourcount){
//                boolean isAdding = Util.getRandomBoolean();
//                if (isAdding){
//                    List<String> keysAsArray = new ArrayList<String>(routingTable.keySet());
//                    routingTable.remove(keysAsArray.get(r.nextInt(1)));
//                    routingTable.put(node.getKey(), node);
//                    rgStatus.routingTableStatus_plus1();
//                }
//            }else {
//                routingTable.put(node.getKey(), node);
//                rgStatus.routingTableStatus_plus1();
////            System.out.println(node.getKey()+" added to routing table by "+addedBy);
//            }
//        }
//    }

    public static void addToRoutingTable(Node node,String addedBy){
        if (routingTable.containsKey(node.getKey())){
            return;
        }else {
            routingTable.put(node.getKey(),node);
            rgStatus.routingTableStatus_plus1();
//            System.out.println("Routing table <-added "+node.getKey()+" by "+addedBy);
        }
    }

    public static void addToActiveTable(Node node,String addedBy){
        if (activeNodes.containsKey(node.getKey())){
            return;
        }else {
            activeNodes.put(node.getKey(),node);
            rgStatus.routingTableStatus_plus1();
//            System.out.println("Routing table <-added "+node.getKey()+" by "+addedBy);
        }
    }

    public static void removeFromRoutingTable(Node node){
        if (routingTable.containsKey(node.getKey()))    {
            routingTable.remove(node.getKey());
            System.out.println(node.getKey()+" removed from routing table");
        }
    }

    public static HashMap<String, Node> getRoutingTable(){return routingTable;
    }

    public static HashMap<String, Node> getActiveTable(){return activeNodes;
    }

    public static void setRoutingTable(HashMap<String, Node> table){
        routingTable = table;
    }

    public static void setActiveTable(HashMap<String, Node> table){
        activeNodes = table;
    }

    public static void nullRoutingTable(){
        routingTable = null;
        routingTable = new HashMap<String, Node>();
    }

    public static void nullActiveTable(){
        activeNodes = new HashMap<String, Node>();
    }

    public static void setRoutingToActive(){
        routingTable = activeNodes;
    }



}
