package org.lintx.plugin.webauth.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Utils {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static UUID newUUID(){
        UUID uuid = UUID.randomUUID();
        long most = uuid.getMostSignificantBits();
        most = most << 32 >>> 32;
        return new UUID(most,uuid.getLeastSignificantBits());
    }

    public static String newToken(){
        //14*4=56,4位字节会换成一位字符串
        UUID uuid = UUID.randomUUID();
        long hi = 1L << 56;
        long val = uuid.getLeastSignificantBits();
        String str = Long.toHexString(hi | (val & (hi - 1))).substring(1);
        return "=" + str + "=";
    }

    public static String sha1(String string){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(string.getBytes());
            byte[] bytes =  messageDigest.digest();

            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars);
        } catch (NoSuchAlgorithmException ignored) {

        }
        return "";
    }

    public static LocalDateTime str2DateTime(String str){
        try {
            return LocalDateTime.parse(str,dateTimeFormatter);
        }
        catch (Exception e){
            return null;
        }
    }

    public static String dateTime2String(LocalDateTime date){
        return dateTimeFormatter.format(date);
    }
}
