package ru.marinchenko.lorry.util;

import java.util.ArrayList;

public class NetList {
    private ArrayList<Net> list = new ArrayList<>();
    private String present = "0";

    public NetList(ArrayList<Net> newList){
        list = newList;
    }


    public ArrayList<Net> getList(){
        return list;
    }

    public String getPresent(){
        return present;
    }

    public ArrayList<String> getStringList(){
        ArrayList<String> str = new ArrayList<>();
        for(Net n : list){
            str.add(n.getSsid());
        }
        return str;
    }

    public int getSize(){
        return list.size();
    }

    public void setPresent(String newNet){
        present = newNet;
    }

}
