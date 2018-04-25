package utils;

import java.security.MessageDigest;

/**
 * @fileName: Security
 * @author: h1
 * @date: 2018/4/25 10:47
 * @dscription:
 */
public class Security {

    private final static char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    public static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {

        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            ret.append(HEX_DIGITS[(aByte >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[aByte & 0x0f]);
        }
        return ret.toString();
    }
}
