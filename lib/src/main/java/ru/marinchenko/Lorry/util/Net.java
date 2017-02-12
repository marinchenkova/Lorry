package ru.marinchenko.lorry.util;

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

        Pattern pattern = Pattern.compile("LV-[0-9]{8}");
        Matcher matcher = pattern.matcher(name);
        rec = matcher.matches();

        if(rec) password = KeyGen.generatePass(name);
    }

    public String getId(){ return id; }

    public String getPassword(){ return  password; }

    public boolean isRec(){ return rec; }
}
