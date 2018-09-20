package com.company;

import java.util.Map.Entry;
import static com.company.Client.*;
import static com.company.Util.*;

public class SendingMessageHandler {

    /**
     * command: reg 127.0.0.1
     *
     * @param bs_ip
     */
    public static void registerToBS(String bs_ip) {

        /* length REG IP_address port_no username */
        String msg="REG "+myIp+" "+myPort+" "+myUserName;
        String msg_formatted = formatMessage(msg);

        Client.bs_ip=bs_ip;
        sendPacket(bs_ip,bs_port,msg_formatted);
    }


    /**
     *  command: unreg
     */
    public static void unregisterFromBS(){

        /* length UNREG IP_address port_no username */
        String msg="UNREG "+myIp+" "+myPort+" "+myUserName;
        String msg_formated = formatMessage(msg);

        sendPacket(bs_ip,bs_port,msg_formated);
    }

    /**
     *  command: join
     */
    public static void joinToSystem() {

        /* length JOIN IP_address port_no */
        String msg="JOIN "+myIp+" "+myPort;
        String msg_formated = formatMessage(msg);

        if (routingTable.size()==0){
            echoni("No neighbours to join");
        }else {

            for (Entry<String, Node> entry : routingTable.entrySet()) {
                sendPacket(entry.getValue().getIp(), entry.getValue().getPort(), msg_formated);
            }
        }
    }

    /**
     *  command: leave
     */
    public static void leaveTheSystem() {

        /* length LEAVE IP_address port_no */
        String msg="LEAVE "+myIp+" "+myPort;
        String msg_formated = formatMessage(msg);

        for (Entry<String,Node> entry: routingTable.entrySet()) {
            sendPacket(entry.getValue().getIp(),entry.getValue().getPort(),msg_formated);
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
