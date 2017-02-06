package ru.marinchenko.util;

/**
 * Символы для пароля в кодировке UTF-8: 0 - 9 (48-57), A - Z (65-90), a - z (97-122)
 */
public class PasswordGen {

    public static void main(String[] args) {
        String pass = "a";
        System.out.println("Wi-Fi name: " + pass);
        System.out.println("PasswordGen: " + generate(pass));
    }

    public static String generate(String id){
        String t = id;
        int l = t.length() - 1;
        int i = 0;

        if(t.length() == 1){
            t = t.concat(String.valueOf(utfUp(t.charAt(0), 'I')));
        }
        while(t.length() < 8){
            t = t.concat(String.valueOf(utfUp(t.charAt(i), t.charAt(i + 1))));
            i++;
        }

        char[] chs = t.toCharArray();

        for(int s = 0; s < l; s++) {
            for (int j = s; j < l; j++) {
                chs[s] = PasswordGen.utfUp(chs[s], chs[j + 1]);
            }
        }
        chs[l] = PasswordGen.utfUp(chs[l], chs[0]);

        return new String(chs);
    }

    private static char utfUp(char a, char b){
        int c = toRelCode((int) a) + toRelCode((int) b);
        if(c > 61){ c -= 62; }
        return (char) fromRelCode(c);
    }

    private static int toRelCode(int a){
        if(a > 47 & a < 58){
            return a - 48;
        } else if(a > 64 & a < 91){
            return a - 55;
        } else if(a > 96 & a < 123){
            return a - 61;
        } else {
            return 25;
        }
    }

    private static int fromRelCode(int c){
        if(c > -1 & c < 10){
            return c + 48;
        } else if(c > 9 & c < 36){
            return c + 55;
        } else if(c > 35 & c < 62){
            return c + 61;
        } else {
            return 85;
        }
    }
}
