package clientFiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import static clientFiles.Client.*;
import static clientFiles.ResponseHandler.sendPulses;
import static clientFiles.Util.*;

public class MessegeHandler {

    public static void regNode(String bs_ip) {

        /* length REG IP_address port_no username */
        String msg="REG " + myIp + " " + defaultPort + " " + myName;
        String msg_formatted = formatMessage(msg);

        Client.bs_ip=bs_ip;
        sendPacket(bs_ip,bs_port,msg_formatted,"Register");
    }

    public static void unregNode(){

        String msg="UNREG " + myIp + " " + defaultPort + " " + myName;
        String msg_formatted = formatMessage(msg);

        sendPacket(bs_ip,bs_port,msg_formatted,"Unregister");
    }

    public static void joinNode() {

        if (getRoutingTable().size()==0) {
            System.out.println("Routing table is empty. No neighbours to connect!");
            return;
        }
        String msg="JOIN " + myIp + " " + defaultPort;
        String msg_formatted = formatMessage(msg);

        for (Entry<String, Node> entry : getRoutingTable().entrySet()) {
            sendPacket(entry.getValue().getIp(), entry.getValue().getPort(), msg_formatted,"Join");
        }

    }


    public static void nodeLeave() {

        if (getRoutingTable().size()==0) {
            System.out.println("Routing table is empty!");
            return;
        }
        String msg="LEAVE " + myIp + " " + defaultPort;
        String msg_formatted = formatMessage(msg);

        for (Entry<String,Node> entry: getRoutingTable().entrySet()) {
            sendPacket(entry.getValue().getIp(),entry.getValue().getPort(),msg_formatted,"Leave");
        }

    }


//    public static void searchFile_old(StringTokenizer st) {
//
//        if (getRoutingTable().isEmpty()){
//            System.out.println("Routing table is empty. No neighbours to search!");
//            return;
//        }
//
//        try {
//            String fileNameCreator="";
//            String hopsStr="";
//            while (st.hasMoreTokens()){
//                String part=st.nextToken();
//                fileNameCreator+=part+" ";
//                hopsStr=part;
//            }
//            String filename = fileNameCreator.substring(1, (fileNameCreator.length() - hopsStr.length() - 3));
//
//            searchFile(filename);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public static void searchFile(StringTokenizer st) {
//
//        length SER IP Port file_name hops search_key
//
//        search key = timestamp + ”_” + ip:port
//

        if (getRoutingTable().isEmpty()){
            System.out.println("No neighbours in routing table to search");
            return;
        }

        try {
            String fileNameCreator="";
            String hopsStr="";
            while (st.hasMoreTokens()){
                String part=st.nextToken();
                fileNameCreator+=part+" ";
                hopsStr=part;
            }

            String filename = fileNameCreator.substring(0, (fileNameCreator.length() - hopsStr.length() - 2));


            int hops=1;
            if(st.hasMoreTokens()) {
                try {
                    hops = Integer.parseInt(hopsStr);
                }catch (NumberFormatException e){
                    System.out.println("Wrong hops value");
                }
            }
            searchFile(filename,hops);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



//    public static void searchFile_old(String filename){
//        /* length SER IP port file_name hops */
//        String msg = "SER " + myIp + " " + defaultPort + " \"" + filename + "\" " + Client.hopsCount;
//        String msg_formatted = formatMessage(msg);
//
//        for (Entry<String, Node> entry : getRoutingTable().entrySet()) {
//            sendPacket(entry.getValue().getIp(), entry.getValue().getPort(), msg_formatted, "Search");
//        }
//    }

    public static void searchFile(String filename,int hops){
        /* length SER IP port file_name hops */
        ResponseHandler.searchedList.clear();
        String msg = "SER " + myIp + " " + defaultPort + " \"" + filename + "\" " + hops;
        String msg_formatted = formatMessage(msg);
        sendPacket(myIp, defaultPort, msg_formatted, "Search");


//        for (Entry<String, Node> entry : getRoutingTable().entrySet()) {
//            sendPacket(entry.getValue().getIp(), entry.getValue().getPort(), msg_formatted, "Search");
//        }
    }


    public static void exit() {
        unregNode();
        nodeLeave();
    }


    public static void gossipNodes() {
        sendPulses();

        long time1 = System.currentTimeMillis();
//        System.out.println("Sending pulses to neighbours...");
        while (System.currentTimeMillis() < time1 + 7000){
//            System.out.println(System.currentTimeMillis());
        }
//        printRoutingTable();
        if (getRoutingTable().size() > 1) {
            if (rgStatus.hasRoutingTableIncreasedComparedToGossipStatus()) {
                rgStatus.setGossipSendingStatusToRoutingTableStatus();

                ArrayList<Node> neighbourList = new ArrayList<>();
                neighbourList.addAll(getRoutingTable().values());

                for (Node node : neighbourList) {
                    String nextNeighbour = "";
                    int count = 0;
                    for (Node n : neighbourList) {
                        if (!node.isEqual(n.getIp(), n.getPort())) {
                            nextNeighbour += n.getIp() + " " + n.getPort() + " ";
                            count++;
                        } else {
                            continue;
                        }
                    }
                    nextNeighbour.substring(0, nextNeighbour.length() - 1);

                    String msg="GOSSIP "+myIp+" "+ defaultPort +" "+count+" "+nextNeighbour;
                    String msg_formatted=formatMessage(msg);
                    sendPacket(node.getIp(),node.getPort(),msg_formatted,"Gossip");
                }
            }
        }

        sendPulses();

//        setRoutingToActive();
    }

}
