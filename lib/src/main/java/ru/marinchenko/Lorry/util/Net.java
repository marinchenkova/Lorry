package ru.marinchenko.lorry.util;

import java.awt.Image;
import java.util.regex.*;

public class Net {

    private boolean rec;
    private String id;
    private String password = "0";

    public static void main(String[] args) {
        Net net = new Net(KeyGen.generateID());
        System.out.println("Wi-Fi Net ID: " + net.getId());
        System.out.println("KeyGen:       " + net.getPassword());
        System.out.println("Registrator:  " + net.isRec());
    }

    public Net(String name){
        id = name;
        rec = ifRec(id);
    }

    public static boolean ifRec(String name){
        Pattern pattern = Pattern.compile("LV-[0-9]{8}");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public String getId(){ return id; }

    public String getPassword(){ return  password; }

    public boolean isRec(){ return rec; }
}
