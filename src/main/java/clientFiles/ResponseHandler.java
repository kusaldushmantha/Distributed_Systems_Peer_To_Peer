package clientFiles;

import sun.misc.Cleaner;

import java.net.DatagramPacket;
import java.util.*;

import static springboot.MainApplication.tomcatPort;
import static clientFiles.Client.*;
import static clientFiles.Util.*;

public class ResponseHandler {

    static List<String> searchedList = new ArrayList<>();

    public static void regOK(StringTokenizer st) {

        int nodeCount= Integer.parseInt(st.nextToken());

        switch (nodeCount){
            case 0:
                isRegistered =true;
                System.out.println("Successfully registered!");
                System.out.println("Only one node in server");
                break;

            case 1:
                isRegistered =true;
                System.out.println("Successfully registered!");
                Node neighbour= new Node(st.nextToken(),st.nextToken(),"");
                addToRoutingTable(neighbour,"server response");
                break;

            case 2:
                isRegistered =true;
                System.out.println("Successfully registered!");
                Node neighbour1= new Node(st.nextToken(),st.nextToken(),"");
                addToRoutingTable(neighbour1,"server response");

                Node neighbour2= new Node(st.nextToken(),st.nextToken(),"");
                addToRoutingTable(neighbour2,"server response");
                break;

            case 9999:
                System.out.println("ERROR ==> Code-"+nodeCount+": Invalid command!");
                break;

            case 9998:
                System.out.println("ERROR ==> Code-"+nodeCount+": already registered in the server!");
                break;

            case 9997:
                System.out.println("ERROR ==> Code-"+nodeCount+": The port is already in use by another user!");
                break;

            case 9996:
                System.out.println("ERROR ==> Code-"+nodeCount+": Server is full!");
                break;
        }

    }

    public static void unregOK(StringTokenizer st) {

        int value= Integer.parseInt(st.nextToken());

        switch (value){
            case 0:
                isRegistered =false;
                bs_ip=null;
                System.out.println("Unregistered successfully!");
                break;

            case 9999:
                System.out.println("Code-"+value+": Invalid command!");
                break;
        }
    }

    public static void joinOk(StringTokenizer st, DatagramPacket incoming) {

        int value= Integer.parseInt(st.nextToken());

        String joinedNode=incoming.getAddress().getHostAddress()+":"+incoming.getPort();

        switch (value){
            case 0:
                System.out.println("successfully joined with "+joinedNode);
                break;

            case 9999:
                System.out.println("Code-"+value+": Routing table update failed "+joinedNode);
                break;
        }
    }

    public static void leaveOk(StringTokenizer st, DatagramPacket incoming) {
        int value= Integer.parseInt(st.nextToken());

        String joinedNode=incoming.getAddress().getHostAddress()+":"+incoming.getPort();

        switch (value){
            case 0:
                System.out.println(joinedNode+ " Left");
                break;

            case 9999:
                System.out.println("Code-"+value+": routing table update failed "+ joinedNode+"!");
                break;
        }
    }

    public static void neighbourJoin(StringTokenizer st, DatagramPacket incoming) {

        String node_ip=st.nextToken(); String node_port=st.nextToken();

        String msg;
        String msg_formatted;

        Node node=new Node(node_ip,node_port,"");
        if (getRoutingTable().containsKey(node.getKey())){
            System.out.println("Neighbour is already joined!");

            msg="JOINOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted,"Join Ok");

        }else {
            try {
                addToRoutingTable(node,"Join request");
            }catch (Exception e){
                msg="JOINOK "+9999;
                msg_formatted = formatMessage(msg);
                sendPacket(node.getIp(),node.getPort(),msg_formatted,"Join Ok");
            }
            msg="JOINOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted,"Join Ok");
        }
        System.out.println("");
    }

    public static void neighbourLeave(StringTokenizer st, DatagramPacket incoming) {

        String node_ip=st.nextToken(); String node_port=st.nextToken();

        String msg;
        String msg_formatted;

        Node node=new Node(node_ip,node_port,"");
        if (getRoutingTable().containsKey(node.getKey())){
            System.out.println("Removing neighbour: "+ node.getKey());

            try {
                removeFromRoutingTable(node);

                msg="LEAVEOK "+0;
                msg_formatted = formatMessage(msg);
                sendPacket(node.getIp(),node.getPort(),msg_formatted,"Leave Ok");

            }catch (Exception e){
                msg="LEAVEOK "+9999;
                msg_formatted = formatMessage(msg);
                sendPacket(node.getIp(),node.getPort(),msg_formatted,"Leave Ok");
            }

        }else {
            System.out.println("Neighbour doest not exists"+ node.getKey());

            msg="LEAVEOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted,"Leave Ok");
        }
        System.out.println("");
    }


    public static void searchOK(StringTokenizer st, String msg, DatagramPacket incoming) {

        int fileCount= Integer.parseInt(st.nextToken());
        String ipFileOwner=st.nextToken();
        int portFileOwner= Integer.parseInt(st.nextToken());
        int hops= Integer.parseInt(st.nextToken());

        switch (fileCount) {
            case 0:
//                System.out.println("No results found!");
                break;
            case 9999:
                System.out.println("ERROR ==> Code-"+fileCount+": Unreachable node!");
                break;
            case 9998:
                System.out.println("ERROR ==> Code-"+fileCount+":  Error in search!");
                break;
            default:
                if (fileCount>0) {
                    System.out.println("Searching...");

                    ArrayList<String> fileList= (ArrayList<String>) divideHopsAndFiles(msg).get("files");

                    String filesStr = "";
                    for (String file : fileList) {
                        filesStr += file + ", ";
                    }
                    filesStr=filesStr.substring(0, filesStr.length() - 2);

                    String downloadLink = "download http://" + ipFileOwner + ":" + portFileOwner + "/download?name=\"" + filesStr+"\"";
                    System.out.println("Search successful: "+filesStr);
                    System.out.println("Node: "+ipFileOwner+" : "+portFileOwner);
                    System.out.println("download link: "+downloadLink);

                }else {
                    System.out.println("ERROR ==> Code-"+fileCount+": Invalid file Count!");
                }
        }
    }

//    public static void neighbourFileSearch_old(StringTokenizer st, DatagramPacket incoming, String incomeMessage) {
//
//        String requiredIpFile=st.nextToken();
//        int requiredPortFile= Integer.parseInt(st.nextToken());
//        HashMap<String, Object> filesAndHops = divideHopsAndFiles(incomeMessage);
//
//        String hopsStr = (String) filesAndHops.get("hops");
//        String searchName= ((ArrayList<String>) filesAndHops.get("files")).get(0);
//
//        //searching files locally
//        ArrayList<String> filesFound = searchFile(searchName);
//        String filesStr="";
//
//        for (String file: filesFound){
//            filesStr+="\""+file+ "\" ";
//        }
//
//        String msg="SEROK "+ filesFound.size() + " " + myIp + " " + tomcatPort +" " + Client.hopsCount + " " + filesStr.trim() ;
//        String msg_formatted = formatMessage(msg);
//        sendPacket(requiredIpFile,requiredPortFile,msg_formatted, "Search Ok");
//
////        System.out.println("File data sent to "+requiredIpFile+":"+requiredPortFile);
//
//        if (Client.hopsCount>1){
//            Client.hopsCount=Client.hopsCount-1;
//            String hopsMsg="SER " + requiredIpFile + " " + requiredPortFile + " \"" + searchName + "\" " + Client.hopsCount;
//            String msg_formattedForHops = formatMessage(hopsMsg);
//
//            int forwardedHops=0;
//            int cout = 1;
//            for (Map.Entry<String, Node> entry : getRoutingTable().entrySet()) {
//                System.out.println("aaaaaaaaaaaa "+cout);
//                cout++;
//                if (!entry.getValue().isEqual(requiredIpFile,requiredPortFile)){
//                    sendPacket(entry.getValue().getIp(), entry.getValue().getPort(), msg_formattedForHops, "Search in hops");
//                    System.out.println("Search in "+ entry.getValue().getKey()+". hops: "+Client.hopsCount);
//                    forwardedHops++;
//                }
//            }
//            if (forwardedHops==0){
//                System.out.println("Search completed in all nodes");
//            }
//
//        }
//
//    }

    public static void neighbourFileSearch(StringTokenizer st, DatagramPacket incoming, String incomeMessage) {

        String ip_file_needed=st.nextToken();
        int port_file_needed= Integer.parseInt(st.nextToken());

        HashMap<String, Object> filesAndHops = divideHopsAndFiles(incomeMessage);

        String hopsStr = (String) filesAndHops.get("hops");
        String searchName= ((ArrayList<String>) filesAndHops.get("files")).get(0);

        int hops=0;
        try {
            hops= Integer.parseInt(hopsStr);
        }catch (Exception e) {
            e.printStackTrace();
        }
        //reset the hops count
        if((myIp.equals(ip_file_needed))&&(defaultPort==port_file_needed)){
            hops = 0;
        }

        //searching files locally
        ArrayList<String> foundFiles = searchFile(searchName);
        String filesStr="";

        for (String file: foundFiles){
            filesStr+="\""+file+ "\" ";
        }

        String msg="SEROK "+ foundFiles.size() + " " + myIp + " " + tomcatPort +" " + hops + " " + filesStr.trim() ;
        String msg_formatted = formatMessage(msg);
        sendPacket(ip_file_needed,port_file_needed,msg_formatted, "Search Ok");

        //all hops searched identify
        boolean msgForward = true;
        if((myIp.equals(ip_file_needed))&&(defaultPort==port_file_needed)){
            if(foundFiles.size()==0){
                int counter = 0;
                for (Map.Entry<String,Node> entry: getActiveTable().entrySet()) {
                    for (String serached : searchedList){
                        if(entry.getValue().getIp().equals(serached)){
                            counter+=1;
                        }
                    }
                }
                if(counter==getActiveTable().size()){
                    msgForward = false;
                    System.out.println("Search completed. No results found!");
                }else msgForward = true;

            }
        }

        System.out.println("File data sent to "+ip_file_needed+":"+port_file_needed+" found files: "+foundFiles.size());
        if((foundFiles.size()==0)&&msgForward){
            Random random = new Random();
            List<String> keys = new ArrayList<String>(getActiveTable().keySet());

            String randomKey = keys.get( random.nextInt(keys.size()) );
            Node value = getActiveTable().get(randomKey);
            hops+=1;
            String hopsMsg="SER " + ip_file_needed + " " + port_file_needed + " \"" + searchName + "\" " + hops;
            String msg_format = formatMessage(hopsMsg);
            sendPacket(value.getIp(), value.getPort(), msg_format, "Search");
        }
        System.out.println("");
    }

    public static HashMap<String, Node> sendPulses(){
        HashMap<String, Node> copyRoutingTable = (HashMap<String, Node>)getRoutingTable().clone();
//        printRoutingTable();
        Client.nullActiveTable();
//        System.out.println("cleared");
//        printRoutingTable();
//        HashMap<String, Node> newTable = new HashMap<String, Node>();
        for (Map.Entry<String,Node> entry: copyRoutingTable.entrySet()) {
            String pulseMsg = "PULSE "+myIp+" "+ defaultPort;
            String msg_format = formatMessage(pulseMsg);
            sendPacket(entry.getValue().getIp(), entry.getValue().getPort(), msg_format, "Pulse");

        }

        return getRoutingTable();
    }


    public static void gossipHandler(StringTokenizer st, DatagramPacket incoming, String msg) {

        String senderIp=st.nextToken();
        String senderPort=st.nextToken();
        int recievedNodeCount=Integer.parseInt(st.nextToken());

        Node senderNode=new Node(senderIp,senderPort,"");

        if (!getRoutingTable().containsKey(senderNode.getKey())){
            addToRoutingTable(senderNode,"Gossiping");
        }

        for (int i=0;i<recievedNodeCount;i++){
            Node node=new Node(st.nextToken(),st.nextToken(),"");

            if (getRoutingTable().containsKey(node.getKey())){
                continue;
            }else {
                addToRoutingTable(node,"Gossiping");
            }
        }
    }

    public static void pulseHandler(StringTokenizer st, DatagramPacket incoming, String msg){
        String senderIp=st.nextToken();
        int senderPort=Integer.parseInt(st.nextToken());

        String pulseMsg = "PULSEOK " + myIp + " " + defaultPort;
        String msg_formatted = formatMessage(pulseMsg);
        sendPacket(senderIp, senderPort, msg_formatted, "PulseOk");
    }

    public static void updateActiveTable(StringTokenizer st, DatagramPacket incoming, String msg){
        String senderIp=st.nextToken();
        String senderPort=st.nextToken();

        Node senderNode=new Node(senderIp,senderPort,"");

        if (!getActiveTable().containsKey(senderNode.getKey())){
            addToActiveTable(senderNode,"Pulse");
        }

    }
}
