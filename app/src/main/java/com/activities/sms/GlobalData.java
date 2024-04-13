package com.activities.sms;

import static java.nio.charset.StandardCharsets.UTF_8;

import android.util.Base64;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class GlobalData {
    public static String KEY = "c30b0cfb8f90358fd87f33d18e207978";

    public static String encrypt(String strToEncrypt) {
        try {
            byte[] salt = {31, -13, 45, 122, 5, 63, 89, -6, 34, 12, 48, -103, 14, -87, 107, 110};
            byte[] iv = {-116, 33, -103, -17, -15, 64, -112, -82, -55, -82, -98, 83, 60, 26, 83, 53};
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKey aesKeyFromPassword = getAESKeyFromPassword(KEY.toCharArray(), salt, 999);
            Cipher cipher = Cipher.getInstance("AES_256/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, ivSpec);
            byte[] data = cipher.doFinal(strToEncrypt.getBytes(UTF_8));
            String cipherText = Base64.encodeToString(data, Base64.DEFAULT);
            return "#" + cipherText + "#";
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e);
            return "";
        }
    }

    public static String decrypt(String strToDecrypt) {
        if (strToDecrypt.startsWith("#") && strToDecrypt.endsWith("#")) {
            strToDecrypt = strToDecrypt.substring(1, strToDecrypt.length() - 1);
            try {
                byte[] salt = {31, -13, 45, 122, 5, 63, 89, -6, 34, 12, 48, -103, 14, -87, 107, 110};
                byte[] iv = {-116, 33, -103, -17, -15, 64, -112, -82, -55, -82, -98, 83, 60, 26, 83, 53};
                int iteration = 999;
                byte[] cipherData = Base64.decode(strToDecrypt, Base64.DEFAULT);
                IvParameterSpec ivSpec = new IvParameterSpec(iv);
                SecretKey aesKeyFromPassword = getAESKeyFromPassword(KEY.toCharArray(), salt, iteration);
                Cipher cipher = Cipher.getInstance("AES_256/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, ivSpec);
                return new String(cipher.doFinal(cipherData), UTF_8).trim();
            } catch (Exception e) {
                Log.e("Ranjith", e.getLocalizedMessage());
                return strToDecrypt;
            }
        } else {
            return strToDecrypt;
        }
    }

    public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt, int iteration) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        KeySpec spec = new PBEKeySpec(password, salt, iteration, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }
}
