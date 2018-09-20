package com.company;

import java.net.DatagramPacket;
import java.util.StringTokenizer;
import static com.company.Client.*;
import static com.company.Util.*;

public class ReceivingMessageHandler {

    public static void registrationOk(StringTokenizer st) {

        int no_nodes= Integer.parseInt(st.nextToken());
        echon("No of Nodes : "+no_nodes);

        switch (no_nodes){
            case 0:
                echoni("No neighbours have registered ");
                break;

            case 1:
                Node neighbour= new Node(st.nextToken(),st.nextToken(),"");
                routingTable.put(neighbour.getKey(),neighbour);
                echoni("Added to routing table <- "+neighbour.getKey());
                break;

            case 2:
                Node neighbour1= new Node(st.nextToken(),st.nextToken(),"");
                routingTable.put(neighbour1.getKey(),neighbour1);
                echon("Added to routing table <- "+neighbour1.getKey());

                Node neighbour2= new Node(st.nextToken(),st.nextToken(),"");
                routingTable.put(neighbour2.getKey(),neighbour2);
                echoni("Added to routing table <- "+neighbour2.getKey());
                break;

            case 9999:
                echoni("Error Code-"+no_nodes+": Error in the command ");
                break;

            case 9998:
                echoni("Error Code-"+no_nodes+": Already registered, unregister first ");
                break;

            case 9997:
                echoni("Error Code-"+no_nodes+": Registered to another user, try a different IP and port ");
                break;

            case 9996:
                echoni("Error Code-"+no_nodes+": Can’t register. BS full ");
                break;
        }

    }

    public static void unregistrationOk(StringTokenizer st) {

        int value= Integer.parseInt(st.nextToken());
        echon("Value : "+value);

        switch (value){
            case 0:
                echoni("Successfully unregistered");
                break;

            case 9999:
                echoni("IP and port may not be in the registry or command is incorrect");
                break;
        }
    }

    public static void joinOk(StringTokenizer st, DatagramPacket incoming) {

        int value= Integer.parseInt(st.nextToken());
        echon("Value : "+value);

        switch (value){
            case 0:
                echoni(incoming.getAddress()+":"+incoming.getPort()+" - Successfully Joined");
                break;

            case 9999:
                echoni(incoming.getAddress()+":"+incoming.getPort()+" - Error while adding to routing table");
                break;
        }
    }

    public static void leaveOk(StringTokenizer st, DatagramPacket incoming) {
        int value= Integer.parseInt(st.nextToken());
        echon("Value : "+value);

        switch (value){
            case 0:
                echoni(incoming.getAddress()+":"+incoming.getPort()+" - Successfully removed");
                break;

            case 9999:
                echoni(incoming.getAddress()+":"+incoming.getPort()+" - Error while removing from routing table");
                break;
        }
    }

    public static void joiningOfNeighbour(StringTokenizer st, DatagramPacket incoming) {

        String node_ip=st.nextToken(); String node_port=st.nextToken();

        String msg;
        String msg_formatted;

        Node node=new Node(node_ip,node_port,"");
        if (routingTable.containsKey(node.getKey())){
            echon("Already added neighbour : "+node.getKey());

            msg="JOINOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted);

        }else {

            try {

                routingTable.put(node.getKey(),node);
                echon("Added neighbour to routing table : "+node.getKey());

            }catch (Exception e){
                msg="JOINOK "+9999;
                msg_formatted = formatMessage(msg);
                sendPacket(node.getIp(),node.getPort(),msg_formatted);
            }

            msg="JOINOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted);

        }
        echoni("");
    }

    public static void leavingOfNeighbour(StringTokenizer st, DatagramPacket incoming) {

        String node_ip=st.nextToken(); String node_port=st.nextToken();

        String msg;
        String msg_formatted;

        Node node=new Node(node_ip,node_port,"");
        if (routingTable.containsKey(node.getKey())){
            echon("Removing neighbour : "+node.getKey());

            try {

                routingTable.remove(node.getKey());

            }catch (Exception e){
                msg="LEAVEOK "+9999;
                msg_formatted = formatMessage(msg);
                sendPacket(node.getIp(),node.getPort(),msg_formatted);
            }

            msg="LEAVEOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted);

        }else {

            echon("Neighbour not exist in table to remove : "+node.getKey());

            msg="LEAVEOK "+0;
            msg_formatted = formatMessage(msg);
            sendPacket(node.getIp(),node.getPort(),msg_formatted);

        }

        echoni("");
    }
}
