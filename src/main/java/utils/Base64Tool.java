package utils;

import java.util.Base64;

/**
 * @fileName: Base64Tool
 * @author: h1
 * @date: 2018/4/28 11:25
 * @dscription:
 */
public class Base64Tool {

    /**
     * Base64加密
     *
     * @param rowStr  加密前字符串
     * @param charset 编码方式
     * @return 加密后字符串
     * @throws Exception 抛出异常
     */
    public static String encode(String rowStr, String charset) throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] rowBytes = rowStr.getBytes(charset);
        return encoder.encodeToString(rowBytes);
    }

    /**
     * Base64解码
     *
     * @param rowStr  解码前字符串
     * @param charset 编码方式
     * @return 解码后字符串
     * @throws Exception 抛出异常
     */
    public static String decode(String rowStr, String charset) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(rowStr), charset);
    }
}
