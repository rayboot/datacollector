package collector.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @fileName: WordChecker
 * @author: h1
 * @date: 2018/4/24 14:23
 * @dscription:
 */
public class WordChecker {

    private static Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     */
    public static boolean containChinese(String str) {

        Matcher matcher = CHINESE_PATTERN.matcher(str);
        return matcher.find();
    }

    public static String gbEncoding(String gbString) {
        char[] utfBytes = gbString.toCharArray();
        StringBuilder unicodeBytes = new StringBuilder();
        for (char utfByte : utfBytes) {
            String hexB = Integer.toHexString(utfByte);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes.append("%u").append(hexB);
        }
        return unicodeBytes.toString();
    }
}
