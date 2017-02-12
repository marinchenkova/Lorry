package ru.marinchenko.lorry.API;

import ru.marinchenko.lorry.NetAgent;
import ru.marinchenko.lorry.Settings;

public interface LorryAPI {

    //TODO instruction
    void showInstruction();

    Settings getSettings();

    NetAgent getNetAgent();
}
