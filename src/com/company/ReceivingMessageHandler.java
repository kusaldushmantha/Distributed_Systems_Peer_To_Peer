package com.company;

import java.util.StringTokenizer;
import static com.company.Util.*;

public class ReceivingMessageHandler {

    public static void registrationOk(StringTokenizer st) {

        int no_nodes= Integer.parseInt(st.nextToken());
        echo("No of Nodes : "+no_nodes+"\n\n> ");
    }

    public static void unregistrationOk(StringTokenizer st) {

        int value= Integer.parseInt(st.nextToken());
        echo("Value : "+value +"\n\n> ");
    }
}
