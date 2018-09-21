package com.company;

import java.util.Map.Entry;
import java.util.StringTokenizer;

import static com.company.Client.*;
import static com.company.Util.*;

public class SendingMessageHandler {

    /**
     * command: reg bs_ip_address
     *
     * @param bs_ip
     */
    public static void registerToBS(String bs_ip) {

        /* length REG IP_address port_no username */
        String msg="REG " + myIp + " " + myPort + " " + myUserName;
        String msg_formatted = formatMessage(msg);

        Client.bs_ip=bs_ip;
        sendPacket(bs_ip,bs_port,msg_formatted,"Register");
    }

    /**
     * (testing purpose)
     *
     * command regl
     *
     */
    public static void registerToBSonSameIp() {

        /* length REG IP_address port_no username */
        String msg="REG " + myIp + " " + myPort + " " + myUserName;
        String msg_formatted = formatMessage(msg);

        Client.bs_ip=myIp;
        sendPacket(bs_ip,bs_port,msg_formatted,"Register");
    }


    /**
     *  command: unreg
     */
    public static void unregisterFromBS(){

        /* length UNREG IP_address port_no username */
        String msg="UNREG " + myIp + " " + myPort + " " + myUserName;
        String msg_formated = formatMessage(msg);

        sendPacket(bs_ip,bs_port,msg_formated,"Unregister");
    }

    /**
     *  command: join
     */
    public static void joinToSystem() {

        if (routingTable.size()==0) {
            print_nng("No neighbours in routing table to join");
            return;
        }

        /* length JOIN IP_address port_no */
        String msg="JOIN " + myIp + " " + myPort;
        String msg_formated = formatMessage(msg);

        for (Entry<String, Node> entry : routingTable.entrySet()) {
            sendPacket(entry.getValue().getIp(), entry.getValue().getPort(), msg_formated,"Join");
        }

    }


    /**
     *  command: leave
     */
    public static void leaveTheSystem() {

        if (routingTable.size()==0) {
            print_nng("No neighbours in routing table to leave");
            return;
        }

        /* length LEAVE IP_address port_no */
        String msg="LEAVE " + myIp + " " + myPort;
        String msg_formated = formatMessage(msg);

        for (Entry<String,Node> entry: routingTable.entrySet()) {
            sendPacket(entry.getValue().getIp(),entry.getValue().getPort(),msg_formated,"Leave");
        }

    }


    /**
     *  command: search file_name
     *
     * @param st
     */
    public static void searchFile(StringTokenizer st) {

        if (routingTable.isEmpty()){
            print_nng("No neighbours in routing table to search");
            return;
        }

        try {
            String filename = st.nextToken();
            int hops=1;
            if(st.hasMoreTokens()) {
                try {
                    hops = Integer.parseInt(st.nextToken());
                }catch (NumberFormatException e){
                    print_nng("Wrong hops value");
                }
            }
            /* length SER IP port file_name hops */
            String msg = "SER " + myIp + " " + myPort + " " + filename + " " + hops;
            String msg_formated = formatMessage(msg);

            for (Entry<String, Node> entry : routingTable.entrySet()) {
                sendPacket(entry.getValue().getIp(), entry.getValue().getPort(), msg_formated, "Search");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * command: exit
     */
    public static void exit() {

        unregisterFromBS();

        leaveTheSystem();

    }



}
