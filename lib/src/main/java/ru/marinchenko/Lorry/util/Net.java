package ru.marinchenko.lorry.util;

import java.util.regex.*;

public class Net {

    private boolean reg;
    private String id;
    private String password = "0";

    public static void main(String[] args) {
        Net net = new Net("LV-363673");
        System.out.println("Wi-Fi Net ID: " + net.getId());
        System.out.println("KeyGen:  " + net.getPassword());
        System.out.println("Registrator:  " + net.isReg());
    }

    public Net(String name){
        id = name;

        Pattern pattern = Pattern.compile("LV-[0-9]{6}");
        Matcher matcher = pattern.matcher(name);
        reg = matcher.matches();

        if(reg) password = KeyGen.generatePass(name);
    }

    public String getId(){ return id; }

    public String getPassword(){ return  password; }

    public boolean isReg(){ return reg; }
}
