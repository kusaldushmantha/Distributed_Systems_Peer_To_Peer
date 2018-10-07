package udpclient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Stream;

import static udpclient.Client.*;
import static udpclient.Printer.*;


public class Util {


    public static ArrayList<String> searchFile(String searchName){

        ArrayList<String> foundFiles=new ArrayList<>();

        for (String fileName:selectedFiles){
            for (String word:fileName.split(" ")){
                if (word.equalsIgnoreCase(searchName)){
                    foundFiles.add(fileName);
                    break;
                }
            }
        }

        return foundFiles;
    }

    public static String getMyIp() {
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMyHostname() {
        try{
            String hostName = InetAddress.getLocalHost().getHostName();
            return hostName;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static  void changeMyPort(String port) {
        if (getRoutingTable().size()!=0){
            print_n("Port changing denied. Route table not empty ");
        }else {
            try {
                myPort = Integer.parseInt(port);
                okToListen = false;
                socket = new DatagramSocket(myPort);
                listeningThread = new Thread(Client::lookForMessages);
                listeningThread.start();
                print_nng("Port changed to " + port);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendPacket(String ip,int port,String msg,String commandType){
        try {
            print_Sending(ip,port,msg,commandType);
            DatagramPacket dgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(ip), port);
            socket.send(dgPacket);
        } catch (IOException e) {
            print_Error_n("Sending failed");
        }
    }


    public static void readAndGetRandomFiles(String filepath) {
        ArrayList<String> nameList=new ArrayList<>();
        try (Stream<String> lines = Files.lines(Paths.get(filepath), StandardCharsets.UTF_8)) {
            lines.forEachOrdered(line -> nameList.add(line));

            Collections.shuffle(nameList);

            Random random = new Random();
            int range=random.nextInt((5 - 3) + 1) + 3;
            for (int i=0; i<range; i++) {
                selectedFiles.add(nameList.get(i));
            }

            printSelectedFiles();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String formatMessage(String msg){
        String msg_formatted = String.format("%04d", msg.length() + 5) + " " + msg;
        return msg_formatted;
    }


    public static String getHelpText(){
        StringBuilder sb=new StringBuilder();
        sb.append("\t").append("reg server_ip").append("\t\t\t\t - ").append("register to Bootstrap server").append("\n");
        sb.append("\t").append("regl").append("\t\t\t\t\t - ").append("register to Bootstrap server running on same ip").append("\n");
        sb.append("\t").append("unreg").append("\t\t\t\t\t - ").append("unregister from Bootstrap server").append("\n");
        sb.append("\t").append("join").append("\t\t\t\t\t - ").append("join to neighbours in routing table").append("\n");
        sb.append("\t").append("leave").append("\t\t\t\t\t - ").append("leave from neighbours").append("\n");
        sb.append("\t").append("table").append("\t\t\t\t\t - ").append("show routing table").append("\n");
        sb.append("\t").append("files").append("\t\t\t\t\t - ").append("show selected files").append("\n");
        sb.append("\t").append("search file_name hops[optional]").append("\t\t - ").append("search files in network by name").append("\n");
        sb.append("\t").append("exit").append("\t\t\t\t\t - ").append("exit from application followed by 'unreg' and 'leave' ").append("\n");
        sb.append("\t  ____\n\n");
        sb.append("\t").append("help").append("\t\t - ").append("app commands (this)").append("\n");
        sb.append("\t").append("setport port").append("\t - ").append("change port if registration failed").append("\n");
        return sb.toString();
    }


}
