package ru.marinchenko.lorry;


import ru.marinchenko.lorry.API.LorryAPI;
import ru.marinchenko.lorry.util.KeyGen;



public class Lorry implements LorryAPI{

    private Settings settings = new Settings();
    private NetAgent netAgent = new NetAgent();

    public Lorry(){}

    @Override
    public void showInstruction(){

    }

    @Override
    public Settings getSettings(){
        return settings;
    }

    @Override
    public NetAgent getNetAgent() {
        return netAgent;
    }
}
