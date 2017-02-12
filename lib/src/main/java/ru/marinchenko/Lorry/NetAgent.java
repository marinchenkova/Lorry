package ru.marinchenko.lorry;

import java.util.ArrayList;
import ru.marinchenko.lorry.util.Net;

/**
 * С помощью класса {@link NetAgent} осуществляется взаимодействие с доступными сетями
 * {@link Net}. Выносятся в отдельный список сети, прошедшие проверку соответствия имени сети:
 * {@link NetAgent#regs}.
 */
public class NetAgent {

    private ArrayList<Net> nets = new ArrayList<>();
    private ArrayList<Net> regs = new ArrayList<>();
    private ArrayList<Net> newNets = new ArrayList<>();

    public NetAgent(){}

    /**
     * Добавление новых сетей в уже существующий список {@link NetAgent#nets}. Новые сети
     * добавляются в отдельный список {@link NetAgent#newNets}. Обновляется список сетей,
     * прошедших проверку на соответствие {@link NetAgent#regs}.
     * @param ids массив имен сетей
     */
    public void addNets(String[] ids){
        newNets = new ArrayList<>();

        for (String id : ids) {
            newNets.add(new Net(id));
            nets.add(new Net(id));
        }

        for(Net n: newNets){
            if(n.isRec()) regs.add(n);
        }
    }

    /**
     * Обновление списков сетей {@link NetAgent#nets} и {@link NetAgent#regs}. Все новые сети так
     * же добавляются в список {@link NetAgent#newNets}.
     * @param ids массив имен сетей
     */
    public void updateNets(String[] ids){
        nets = new ArrayList<>();
        regs = new ArrayList<>();
        newNets = new ArrayList<>();

        for (String id : ids) {
            nets.add(new Net(id));
            newNets = new ArrayList<>();
        }

        for(Net n: nets){
            if(n.isRec()) regs.add(n);
        }
    }

    /**
     * Возврат сети из списка сетей, прошедших проверку на соответствие {@link NetAgent#regs}.
     * @param i номер сети из списка {@link NetAgent#regs}
     * @return сеть по номеру из списка {@link NetAgent#regs}
     */
    public Net autoConnect(int i){
        return regs.get(i);
    }

    public ArrayList<Net> getNets(){ return nets; }

    public ArrayList<Net> getRegs(){ return regs; }

    public ArrayList<Net> getRecentNets(){ return newNets; }
}
