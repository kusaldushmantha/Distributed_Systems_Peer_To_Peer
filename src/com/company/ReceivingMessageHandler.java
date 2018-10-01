package com.company;

import java.net.DatagramPacket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import static com.company.Client.*;
import static com.company.Util.*;

public class ReceivingMessageHandler {

    public static void registrationOk(StringTokenizer st) {

        int no_of_nodes= Integer.parseInt(st.nextToken());

        switch (no_of_nodes){
            case 0:
                registered=true;
                print_Success("Registered");
                print_nng("No neighbours have registered in Bootstrap server yet");
                break;

            case 1:
                print_Success("Registered");
                registered=true;
                Node neighbour= new Node(st.nextToken(),st.nextToken(),"");
                routingTable.put(neighbour.getKey(),neighbour);
                print_nng("Added Neighbour to routing table <- "+neighbour.getKey());
                break;

            case 2:
                print_Success("Registered");
                registered=true;
                Node neighbour1= new Node(st.nextToken(),st.nextToken(),"");
                routingTable.put(neighbour1.getKey(),neighbour1);
                print_ng("Added Neighbour 1 to routing table <- "+neighbour1.getKey());

                Node neighbour2= new Node(st.nextToken(),st.nextToken(),"");
                routingTable.put(neighbour2.getKey(),neighbour2);
                print_nng("Added Neighbour 2 to routing table <- "+neighbour2.getKey());
                break;

            case 9999:
                print_Error_n("Code-"+no_of_nodes+": Error in the command ");
                break;

            case 9998:
                print_Error_n("Code-"+no_of_nodes+": Already registered, unregister first ");
                break;

            case 9997:
                print_Error_n("Code-"+no_of_nodes+": Registered to another user, try a different IP and port ");
                break;

            case 9996:
                print_Error_n("Code-"+no_of_nodes+": Can’t register. BS full ");
                break;
        }

    }

    public static void unregistrationOk(StringTokenizer st) {

        int value= Integer.parseInt(st.nextToken());

        switch (value){
            case 0:
                registered=false;
                bs_ip=null;
                print_Success_n("Unregistered");
                break;

            case 9999:
                print_Error_n("Code-"+value+": IP and port may not be in the registry or command is incorrect");
                break;
        }
    }

    public static void joinOk(StringTokenizer st, DatagramPacket incoming) {

        int value= Integer.parseInt(st.nextToken());

        String joinedNodeKey=incoming.getAddress().getHostAddress()+":"+incoming.getPort();

        switch (value){
            case 0:
                print_Success_n("Joined with "+joinedNodeKey);
                break;

            case 9999:
                print_Error_n("Code-"+value+": Error while adding to routing table of "+joinedNodeKey);
                break;
        }
    }

    public static void leaveOk(StringTokenizer st, DatagramPacket incoming) {
        int value= Integer.parseInt(st.nextToken());

        String joinedNodeKey=incoming.getAddress().getHostAddress()+":"+incoming.getPort();

        switch (value){
            case 0:
                print_Success_n("Left " +joinedNodeKey);
                break;

            case 9999:
                print_Error_n("Code-"+value+": Error while removing from routing table of "+ joinedNodeKey);
                break;
        }
    }

    public static void joiningOfNeighbour(StringTokenizer st, DatagramPacket incoming) {

        String node_ip=st.nextToken(); String node_port=st.nextToken();

        String msg;
        String msg_formatted;

        Node node=new Node(node_ip,node_port,"");
        if (routingTable.containsKey(node.getKey())){
            print_ng("Already added neighbour : "+node.getKey());

            msg="JOINOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted,"Join Ok");

        }else {

            try {

                routingTable.put(node.getKey(),node);
                print_ng("Added neighbour to routing table <- "+node.getKey());

            }catch (Exception e){
                msg="JOINOK "+9999;
                msg_formatted = formatMessage(msg);
                sendPacket(node.getIp(),node.getPort(),msg_formatted,"Join Ok");
            }

            msg="JOINOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted,"Join Ok");

        }
        print_n("");
    }

    public static void leavingOfNeighbour(StringTokenizer st, DatagramPacket incoming) {

        String node_ip=st.nextToken(); String node_port=st.nextToken();

        String msg;
        String msg_formatted;

        Node node=new Node(node_ip,node_port,"");
        if (routingTable.containsKey(node.getKey())){
            print_ng("Removing neighbour : "+node.getKey());

            try {

                routingTable.remove(node.getKey());

            }catch (Exception e){
                msg="LEAVEOK "+9999;
                msg_formatted = formatMessage(msg);
                sendPacket(node.getIp(),node.getPort(),msg_formatted,"Leave Ok");
            }

            msg="LEAVEOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted,"Leave Ok");

        }else {

            print_ng("Neighbour not exist in table to remove : "+node.getKey());

            msg="LEAVEOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted,"Leave Ok");

        }

        print_n("");
    }


    // TODO: 9/21/18 temp search result to collect until hops ==1
    // TODO: 9/21/18 map to save final result of a search

    public static void fileSearchOk(StringTokenizer st, String msg, DatagramPacket incoming) {

        int no_of_files= Integer.parseInt(st.nextToken());
        String ip_file_owner=st.nextToken();
        int port_file_owner= Integer.parseInt(st.nextToken());
        int hops= Integer.parseInt(st.nextToken());

        switch (no_of_files) {
            case 0:
                print_Success("Searching");
                print_nng("No matching result");
                break;
            case 9999:
                print_Error_n("Code-"+no_of_files+":  Failure due to node unreachable");
                break;
            case 9998:
                print_Error_n("Code-"+no_of_files+":  Other error");
                break;
            default:
                if (no_of_files>0) {
                    print_Success("File Search");


                    ArrayList<String> fileNames=new ArrayList<>();


                    for (String s: msg.split("'")){
                        fileNames.add(s);
                    }


                    //to have only file names
                    fileNames.remove(0); //remove first part of the msg
                    fileNames.remove(fileNames.size()-1); //remove last null item

                    String filesStr = "";

                    for (String file : fileNames) {
                        filesStr += file + ", ";
                    }
                    filesStr=filesStr.substring(0, filesStr.length() - 2); //remove last , and space

                    print_nng("Neighbour " + ip_file_owner + ":" + port_file_owner + " has : " + filesStr);

                    //collect data until hops==1

                }else {
                    print_nng("No a valid number of files "+no_of_files);
                }
        }
    }

    public static void searchFileForNeighbour(StringTokenizer st, DatagramPacket incoming, String incomeMessage) {

        String ip_file_needed=st.nextToken();
        int port_file_needed= Integer.parseInt(st.nextToken());
        String searchName=st.nextToken();
        int hops= Integer.parseInt(st.nextToken());

        //searching files locally
        ArrayList<String> foundFiles = searchFile(searchName);
        String filesStr="";

        for (String file: foundFiles){
            filesStr+="'"+file+ "' ";
        }

        String msg="SEROK "+ foundFiles.size() + " " + myIp + " " + myPort +" " + hops + " " + filesStr ;
        String msg_formatted = formatMessage(msg);
        sendPacket(ip_file_needed,port_file_needed,msg_formatted, "Search Ok");

        print_ng("File data sent to "+ip_file_needed+":"+port_file_needed);

        /*
        Manage hops
         */
        if (hops>1){
            hops=hops-1;
            String hopsMsg="SER " + ip_file_needed + " " + port_file_needed + " " + searchName + " " + hops;
            String msg_formattedForHops = formatMessage(hopsMsg);

            int forwardedHops=0;
            for (Map.Entry<String, Node> entry : routingTable.entrySet()) {

                if (!entry.getValue().isEqual(ip_file_needed,port_file_needed)){
                    sendPacket(entry.getValue().getIp(), entry.getValue().getPort(), msg_formattedForHops, "Search in hops");
                    print_ng("File search forwarded to "+ entry.getValue().getKey()+". more hops: "+hops);
                    forwardedHops++;
                }
            }
            if (forwardedHops==0){
                print_ng("No other neighbours to forward this search");
            }

        }
        print_n("");

    }
}
