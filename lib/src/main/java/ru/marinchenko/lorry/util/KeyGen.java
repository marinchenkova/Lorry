package ru.marinchenko.lorry.util;


/**
 * Символы для пароля в кодировке UTF-8: 0 - 9 (48-57), A - Z (65-90), a - z (97-122)
 */
public class KeyGen {

    public static void main(String[] args) {
        /*String id = generateID();
        System.out.println("Wi-Fi name:  " + id);
        System.out.println("KeyGen:      " + generatePass(id));*/
    }

    public static String generateID(){
        int s = (int) (10000000 + Math.random() * 89999999);
        return "LV-" + String.valueOf(s);
    }

    public static String generatePass(String id){
        int l = id.length() - 1;
        char[] array = id.toCharArray();

        for(int s = 0; s < l; s++) {
            for (int j = s; j < l; j++) {
                array[s] = utfUp(array[s], array[j + 1]);
            }
        }
        array[l] = utfUp(array[l], array[0]);

        return new String(array);
    }

    private static char utfUp(char a, char b){
        int c = toRelCode((int) a) + toRelCode((int) b);
        if(c > 61){ c -= 62; }
        return fromRelCode(c);
    }

    private static int toRelCode(int a){
        if(a > 47 & a < 58){
            return a - 48 == 0 ? 9 : a - 48;
        } else if(a > 64 & a < 91){
            return a - 55;
        } else if(a > 96 & a < 123){
            return a - 61;
        } else {
            return 7;
        }
    }

    private static char fromRelCode(int c){
        if(c > -1 & c < 10){
            return (char) (c + 48);
        } else if(c > 9 & c < 36){
            return (char) (c + 55);
        } else if(c > 35 & c < 62){
            return (char) (c + 61);
        } else {
            return (char) 56;
        }
    }
}
