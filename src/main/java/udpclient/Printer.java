package udpclient;



import java.net.DatagramPacket;
import java.util.Map;

import static udpclient.Client.*;

public class Printer {

    private static String generalColor= "\033[0;37m";
    private static String boldBlack= "\033[1;30m";
    private static String highIntensityWhite = "\033[0;97m";
    private static String underlinedHighIntensityWhite = "\033[4;97m";
    private static String boldGreen="\033[1;32m";
    private static String boldRed="\033[1;31m";
    private static String boldBlue="\033[1;34m";
    private static String boldYellow="\033[1;33m";
    private static String bold="\033[0;1m";

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static String colorText(String msg, String colorCode){
        //windows command prompt doesn't support these colors
        if (isWindows()){
            return msg;
        }
        return colorCode+msg+generalColor;
    }

    public static void print(String msg){
        System.out.print(msg);
    }

    public static void print(String msg, String colorCode){
        System.out.print(colorText(msg,colorCode));
    }

    public static void print_n(String msg){
        System.out.println(msg);
    }

    public static void print_n(String msg, String colorcode){
        System.out.println(colorText(msg,colorcode));
    }

    public static void print_nn(String msg, String colorcode){
        print_n(msg+"\n",colorcode);
    }

    public static void print_ng(String msg) {
        String msg_=colorText(msg,highIntensityWhite);
        print_n(msg_);
    }

    public static void print_nng(String msg) {
        print_ng(msg+"\n");
    }

    public static void printName(String name) {
        print_n("\n");
        print_n(" "+name,boldBlue);
        print_n("");
    }

    public static void print_Receiving(String msg, DatagramPacket incoming) {
        String ip=incoming.getAddress().getHostAddress();
        int port=incoming.getPort();
        String sender=ip+":"+port;

        String bs="";
        if (ip.equals(bs_ip) && port==bs_port){
            bs=" [BS] ";
        }
        print_n( colorText(" <= ",boldYellow)+colorText(msg,boldYellow)+colorText(" from "+sender+bs, generalColor));
    }

    public static void print_Sending(String ip, int port, String msg,String commandType) {
        String sender=ip+":"+port;
        String bs="";
        if (bs_ip!=null){
            if (ip.equals(bs_ip) && port==bs_port){
                bs=" [BS] ";
            }
        }
        print_n( colorText(" => ",boldBlue) +colorText(msg,boldBlue)+colorText(" to "+sender+bs+ colorText(" ["+ commandType+" command]",bold), generalColor) );
    }


    public static void print_Success(String msg){
        String msg_=colorText("Success: ",boldGreen)+colorText(msg, generalColor);
        print_n(msg_);
    }

    public static void print_Success_n(String msg){
        String msg_=colorText("Success: ",boldGreen)+colorText(msg, generalColor);
        print_n(msg_+"\n");
    }

    public static void print_Error_n(String msg){
        String msg_=colorText("Error: ",boldRed)+colorText(msg, generalColor);
        print_n(msg_+"\n");
    }


    public static void printHelp(String help) {
        print_n(colorText("Application commands\n",underlinedHighIntensityWhite));
        print_nn(help,highIntensityWhite);
    }

    public static void printRoutingTable() {
        print_n(colorText("Routing table\n",underlinedHighIntensityWhite));
        if (getRoutingTable().isEmpty()){
            print_nn("\tTable is empty",highIntensityWhite);
            return;
        }
        int i=0;

        for (Map.Entry<String,Node> entry:getRoutingTable().entrySet()){
            print_n("["+(++i)+"]\t"+"key="+entry.getKey()+"\t\t" +entry.getValue().details(),highIntensityWhite);
        }
        print_n("");
    }

    public static void printSelectedFiles() {

        print_n(colorText(" > ",bold)+colorText("Chosen files\n",underlinedHighIntensityWhite));
        selectedFiles.forEach(file->print_n("\t"+file,highIntensityWhite));
        print_n("");
    }


    public static void printName(){
        StringBuffer buf = new StringBuffer();
        buf.append("\n");
        buf.append("===============================================================================")
                .append("\n");
        buf.append("|                           Distributed Client Cli                            |")
                .append("\n");
        buf.append("===============================================================================")
                .append("\n");
        print_n( buf.toString(),boldGreen);
    }


}
