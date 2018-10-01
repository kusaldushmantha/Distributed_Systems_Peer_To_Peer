package com.company;

public class Status {
    private int routingTableStatus=0;

    private int gossipSendingStatus=0;


    public void routingTableStatus_plus1(){
        routingTableStatus++;
    }

    public void setGossipSendingStatusToRoutingTableStatus(){
        gossipSendingStatus=routingTableStatus;
    }


    public boolean hasRoutingTableIncreasedComparedToGossipStatus(){
        if (routingTableStatus>gossipSendingStatus){
            return true;
        }else{
            return false;
        }
    }

}
