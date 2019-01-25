package chatClient;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Crypt {

    private int _bit = 2;
    private byte[] keyBytes;

    public String crypt(String inf) {

        byte[] bytes = StandardCharsets.UTF_8.encode(inf).array();


        for (int i = 0; i < bytes.length; i++) System.out.printf("%8s[%d] ", Integer.toBinaryString(bytes[i]), i);
        System.out.println();

        String key = cryptMass(bytes, _bit, _bit);

        String crypted = new String(bytes, StandardCharsets.UTF_16LE) + key;

        System.out.println("\ncrypted: " + crypted + "\n");

        for (int i = 0; i < bytes.length; i++) System.out.printf("%8s[%d] ", Integer.toBinaryString(bytes[i]), i);
        System.out.println();

        return crypted;
    }


    private String cryptMass(byte[] bytes, int _byte, int _bit){
        BigInteger massBytes;
        String array, massBites = "";

        for (int i = 0; i < bytes.length; i++) if(i%_byte==0){
            if(bytes[i] == 0) {
                System.out.println("break: "+ i);
                break;
            }

            massBytes = BigInteger.valueOf(bytes[i]);

            array = massBytes.toString(2);
            massBites = array.charAt(array.length() - _bit - 1) + massBites;

            array = removeCharAt(array, array.length() - _bit - 1);
            massBytes = new BigInteger(array, 2);
            bytes[i] = massBytes.byteValueExact();
        }
        return createKey(massBites);
    }

    private String createKey(String massBites){
        BigInteger massBytes;
        String array;

        massBytes = new BigInteger(massBites, 2);
        array = massBytes.toString(16);

        keyBytes = new byte[array.length()];

        for (int i = 0; i < keyBytes.length; i++) {
            keyBytes[i] = (byte)array.charAt(i);
            keyBytes[i] = xorBites(keyBytes[i], _bit);
        }

        String key = new String(keyBytes, StandardCharsets.UTF_8);
        System.out.println("\nkey: " + key + "_" + key.length() + "\n");
        return key + "_" + key.length();

    }

    private byte xorBites(byte byte_i, int _bit) {
        if(_bit == 0) _bit = 1;
        if (!isPaired(byte_i)) byte_i ^= (byte) (1 << _bit);
        return byte_i;
    }

    private boolean isPaired(byte b) {
        if(b%2==0) return true;
        else return false;
    }


    private String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    private int sizeArray(String massBites){
        int i = massBites.length()/6;
        return massBites.length()%6 == 0? i : ++i;
    }

}
