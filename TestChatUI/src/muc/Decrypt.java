package muc;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Decrypt {

    private int _bit = 2;

    public String  decrypt(String crypted){

        byte[] bytes = StandardCharsets.UTF_16LE.encode(crypted).array();
        byte[] keyBytes;
        String key = "";


        String encryped = new String(bytes, StandardCharsets.UTF_16LE);
        for (int i = encryped.length() - 1; i >= 0; i--) {
            if (encryped.charAt(i) == '_'){
                for (int j = i+1; j < encryped.length(); j++) key += Character.toString(encryped.charAt(j));
                try {
                    int lenth = Integer.parseInt(key);
                    System.out.println(lenth);
                    System.out.println(key);
                    key = "";
                    for (int j = i - lenth; j < i; j++) key += Character.toString(encryped.charAt(j));
                    int lenthS = encryped.length();
                    for (int j = i - lenth; j < lenthS; j++) encryped = removeCharAt(encryped, i - lenth);
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();

                }

                break;
            }
        }

        keyBytes = key.getBytes(StandardCharsets.UTF_8);
        bytes = StandardCharsets.UTF_16LE.encode(encryped).array();
        System.out.println("key: "+ key);
        System.out.println("encryped: "+ encryped);


        System.out.println("KETBYTES: "+new String(keyBytes));
        for (int i = 0; i < bytes.length; i++) System.out.printf("%8s[%d] ", Integer.toBinaryString(bytes[i]), i);
        System.out.println();

        decryptMass(bytes, keyBytes, _bit, _bit);

        String decrypted = new String(bytes, StandardCharsets.UTF_8);
        decrypted = removeCharAt(decrypted, decrypted.length() - 1);
        decrypted = removeCharAt(decrypted, decrypted.length() - 1);
        System.out.println("\ndecrypted: " + decrypted + "\n");

        for (int i = 0; i < bytes.length; i++) System.out.printf("%8s[%d] ", Integer.toBinaryString(bytes[i]), i);
        System.out.println();

        return decrypted;
    }

    private void decryptMass(byte[] bytes, byte[] keyBytes, int _byte, int _bit){
        BigInteger massBytes;
        String array, massBites = "", zero = "";

        for (int i = 0; i < keyBytes.length; i++) {
            keyBytes[i] = xorBites(keyBytes[i], _bit);
            massBites += (char)keyBytes[i];
        }

        massBytes = new BigInteger(massBites, 16);
        massBites = massBytes.toString(2);

        if(bitLenth(bytes.length, _byte) - massBites.length() > 0) {
            for (int i = 0; i < bitLenth(bytes.length, _byte) - massBites.length(); i++) zero += "0";
            massBites = zero + massBites;
        }

        for (int i = 0, b = massBites.length() - 1; i < bytes.length; i++) if(i%_byte==0){
            if(bytes[i] == 0) {
                System.out.println("break: " + i);
                break;
            }
            massBytes = BigInteger.valueOf(bytes[i]);

            array = massBytes.toString(2);
            array = insertCharAt(array, massBites.charAt(b), array.length() - _bit);

            massBytes = new BigInteger(array, 2);
            bytes[i] = massBytes.byteValueExact();
            b--;
        }
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
    static public String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    private String insertCharAt(String s, char symb, int pos){
        return s.substring(0, pos) + symb + s.substring(pos);
    }

    private int bitLenth(int len, int _byte){
        int i = len/_byte;
        return len%_byte == 0? i : ++i;
    }

}