package udpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static udpclient.Client.*;
import static udpclient.Printer.*;


public class Util {


    public static ArrayList<String> searchFile(String searchName){

        ArrayList<String> foundFiles=new ArrayList<>();

        for (String fileName:selectedFiles){
            for (String word:fileName.split(" ")){ //for space separated words in selected files
                if (word.equalsIgnoreCase(searchName)){
                    foundFiles.add(fileName);
                    break;
                }
            }
            if (fileName.equalsIgnoreCase(searchName)){ //chek for hall file name in selected files
                foundFiles.add(fileName);
                break;
            }
        }

        return foundFiles;
    }

    public static boolean isPortAvailable(int port) {

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }
        return false;
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

        InputStream inputStream = Util.class.getClassLoader().getResourceAsStream(filepath);

        List<String> nameList =
                new BufferedReader(new InputStreamReader(inputStream,
                        StandardCharsets.UTF_8)).lines().collect(Collectors.toList());

        Collections.shuffle(nameList);

        Random random = new Random();
        int range=random.nextInt((5 - 3) + 1) + 3;
        for (int i=0; i<range; i++) {
            selectedFiles.add(nameList.get(i));
        }

    }


    public static String formatMessage(String msg){
        String msg_formatted = String.format("%04d", msg.length() + 5) + " " + msg;
        return msg_formatted;
    }


    public static HashMap<String,Object> divideHopsAndFiles(String s){

        int firstQuotationIndex=s.indexOf('\"');
        int lastQuotationIndex=0;
        for (int i=0;i<s.length();i++){
            if (s.charAt(i)=='\"'){
                lastQuotationIndex=i;
            }
        }

        String hopsStr = s.substring(lastQuotationIndex+1,s.length()).trim();
        String[] filesStr= s.substring(firstQuotationIndex,lastQuotationIndex+1).split("\"");

        ArrayList<String> fileList=new ArrayList<String>();
        for (String str:filesStr){
            fileList.add(str);
        }
        fileList.remove("");
        fileList.remove(" ");

        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("files",fileList);
        map.put("hops",hopsStr);

        return map;

    }

    public static String getHelpText(){
        StringBuilder sb=new StringBuilder();
        sb.append("\t").append("reg server_ip").append("\t\t\t - ").append("register to Bootstrap server").append("\n");
        sb.append("\t").append("regl").append("\t\t\t\t\t - ").append("register to Bootstrap server running on same ip").append("\n");
        sb.append("\t").append("unreg").append("\t\t\t\t\t - ").append("unregister from Bootstrap server").append("\n");
        sb.append("\t").append("join").append("\t\t\t\t\t - ").append("join to neighbours in routing table").append("\n");
        sb.append("\t").append("leave").append("\t\t\t\t\t - ").append("leave from neighbours").append("\n");
        sb.append("\t").append("table").append("\t\t\t\t\t - ").append("show routing table").append("\n");
        sb.append("\t").append("files").append("\t\t\t\t\t - ").append("show selected files").append("\n");
        sb.append("\t").append("search file_name hops(optional)").append("\t - ").append("search files in network by name").append("\n");
        sb.append("\t").append("exit").append("\t\t\t\t\t - ").append("exit from application followed by 'unreg' and 'leave' ").append("\n");
        sb.append("\t  ____\n\n");
        sb.append("\t").append("help").append("\t\t\t\t\t - ").append("app commands (this)").append("\n");
        sb.append("\t").append("setport port").append("\t\t\t - ").append("change port if registration failed").append("\n");
        return sb.toString();
    }


}
