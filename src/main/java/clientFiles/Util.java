package clientFiles;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

import static clientFiles.Client.*;

public class Util {
//    private static File log = new File("out.txt");

    private static Random random= new Random();

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


    public static  void changePort(String port) {
        if (getRoutingTable().size()!=0){
            System.out.println("ERROR ==> Routing table is not empty. Can't change port!");
        }else {
            try {
                defaultPort = Integer.parseInt(port);
                isListen = false;
                socket = new DatagramSocket(defaultPort);
                listnerThread = new Thread(Client::msgListner);
                listnerThread.start();
                System.out.println("Port changed: "+port);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendPacket(String ip,int port,String msg,String commandType){
        try {
//            displayMsg(ip,port,msg,commandType);
            DatagramPacket dgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                    InetAddress.getByName(ip), port);
            socket.send(dgPacket);
        } catch (IOException e) {
            System.out.println("ERROR ==> Send failed");
        }
    }

    public static String getHash(File file){
        HashCode hc = null;
        try {
            hc = Files.asByteSource(file).hash(Hashing.md5());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hc.toString();
    }


    public static String formatMessage(String msg){
        String msg_formatted = String.format("%04d", msg.length() + 5) + " " + msg;
        return msg_formatted;
    }

    public static boolean getRandomBoolean() {
        return random.nextBoolean();
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

    public static void printResponse(String msg, DatagramPacket incoming) {
        String ip=incoming.getAddress().getHostAddress();
        int port=incoming.getPort();
        String sender=ip+":"+port;

        String bs="";
        if (ip.equals(bs_ip) && port==bs_port){
            bs=" [BS] ";
        }
        System.out.println(msg+" from "+sender+bs );
    }

    public static void printRoutingTable() {
        System.out.println("Routing table:\n");
        if (getRoutingTable().isEmpty()){
            System.out.println("No neighbours in the table!");
            return;
        }
        int i=0;

        for (Map.Entry<String,Node> entry:getRoutingTable().entrySet()){
            System.out.println((++i)+". "+entry.getKey()+"\t" +entry.getValue().toStringNeighbour());
        }
        System.out.println("");
    }

    public static void displayFiles() {
        System.out.println("Files: \n");
        selectedFiles.forEach(file-> System.out.println(file));
        System.out.println("");
    }

    public static void displayMsg(String ip, int port, String msg,String commandType) {
        String sender=ip+":"+port;
        System.out.println("\n"+commandType+" messege ==> "+msg+" to "+sender);
    }

//    public static void writeFile() throws IOException {
//        FileOutputStream fos = new FileOutputStream(log);
//
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
//
//        for (int i = 0; i < 10; i++) {
//            bw.write("something");
//            bw.newLine();
//        }
//
//        bw.close();
//    }

}
