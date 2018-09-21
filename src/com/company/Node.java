package com.company;

import java.util.StringTokenizer;

public class Node {

    private String ip;
    private int port;
    private String userName;

    public Node(String ip, String port, String userName) {
        this.ip = ip;
        this.port = Integer.parseInt(port);
        this.userName = userName;
    }

    public String getKey(){
        return ip+":"+port;
    }

    public boolean isEqual(String ip,int port){
        if(port==this.port && ip.equals(this.ip)){
            return true;
        }return false;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String details() {
        return "Neighbour[ " +
                "ip=" + ip +
                ", port=" + port +
//                ", userName='" + userName + '\'' +
                " ]";
    }

    @Override
    public String toString() {
        return "Node[ " +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", userName='" + userName + '\'' +
                " ]";
    }
}
