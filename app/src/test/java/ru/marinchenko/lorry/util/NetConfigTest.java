package ru.marinchenko.lorry.util;

import org.junit.Test;

import static org.junit.Assert.*;


public class NetConfigTest {
    @Test
    public void ifRec() throws Exception {
        String name = "LV-12345678";
        assertTrue(NetConfig.ifRec(name));

        name = "LV-87654321";
        assertTrue(NetConfig.ifRec(name));

        name = "LV_87654321";
        assertFalse(NetConfig.ifRec(name));

        name = "LD-87654321";
        assertFalse(NetConfig.ifRec(name));

        name = "LV-1234567";
        assertFalse(NetConfig.ifRec(name));
    }

    @Test
    public void generateRandomSSID() throws Exception {
        String name;
        for(int i = 0; i < 1000; i++) {
            name = NetConfig.generateRandomSSID();
            assertTrue(NetConfig.ifRec(name));
        }
    }

    @Test
    public void generatePass() throws Exception {
        String name = "LV-12345678";
        String pass = NetConfig.generatePass(name);
        assertEquals(pass, "gChaZXUQLFo");
    }

}