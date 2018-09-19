package com.company;

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
     *
     */
    public static void unregisterFromBS(){

        /* length UNREG IP_address port_no username */
        String msg="UNREG "+myIp+" "+myPort+" "+myUserName;
        String msg_formated = formatMessage(msg);

        sendPacket(bs_ip,bs_port,msg_formated);
    }



}
