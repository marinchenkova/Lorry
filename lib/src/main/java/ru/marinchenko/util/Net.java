package ru.marinchenko.util;

import java.util.regex.*;

public class Net {

    private boolean reg;
    private String id;
    private String password;

    public static void main(String[] args) {
        Net net = new Net("LdV-363673");
        System.out.println("Wi-Fi Net ID: " + net.getId());
        System.out.println("PasswordGen:     " + net.getPassword());
        System.out.println("Registrator:  " + net.isRegistator());
    }

    public Net(String name){
        id = name;

        Pattern pattern = Pattern.compile("LV-[0-9]{6}");
        Matcher matcher = pattern.matcher(name);
        reg = matcher.matches();

        password = PasswordGen.generate(name);
    }

    public String getId(){ return id; }

    public String getPassword(){ return  password; }

    public boolean isRegistator(){ return reg; }
}
