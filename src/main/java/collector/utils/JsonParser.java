package collector.utils;

import java.util.*;

/**
 * @fileName: JsonParser
 * @author: h1
 * @date: 2018/4/24 13:28
 * @dscription: 解析json数据
 */
public class JsonParser {

    /**
     * 格式化Json数据
     *
     * @param jsonStr 原始json字符串
     * @return 格式化后的json字符串
     */
    public static String format(String jsonStr) {
        StringBuilder formatSB = new StringBuilder();

        List<String> startList = Arrays.asList("[", "{");
        String[] middleArr = {",", "\"", "\":"};
        List<String> endList = Arrays.asList("}", "]");
        String[] escapeArr = {"{}", "[]"};
        while (jsonStr.contains(endList.get(0)) || jsonStr.contains(endList.get(1))) {
            String firstChar = jsonStr.substring(0, 1);
            if (startList.contains(firstChar)) {
                formatSB.append("\n").append(firstChar);

                jsonStr = jsonStr.substring(1);
            } else if (firstChar.equals(middleArr[1])) {
                //找到key的结束位置
                int keyFlag = jsonStr.indexOf(middleArr[2]);
                String key = jsonStr.substring(1, keyFlag) + ":";

                //去掉"\"":\"
                jsonStr = jsonStr.substring(keyFlag + 2);
                int valueFlag = jsonStr.indexOf(middleArr[0]);
                int rightBraceFlag = jsonStr.indexOf(endList.get(0));
                //如果","的位置小于"}"位置且存在,使用","的位置,否则使用"}"的位置
                valueFlag = valueFlag < rightBraceFlag && valueFlag != -1 ? valueFlag : rightBraceFlag;
                String value = jsonStr.substring(0, valueFlag + 1);
                //如果有符号
                //如果value中存在开始或者结束符号,找到其位置,取出
                boolean temp = value.contains(startList.get(1)) ||
                        value.contains(endList.get(0));
                if (temp) {
                    int a = value.indexOf(startList.get(1));
                    int b = value.indexOf(endList.get(0));
                    Set<Integer> set = new LinkedHashSet<>(2);
                    if (a != -1) {
                        set.add(a);
                    }
                    if (b != -1) {
                        set.add(b);
                    }
                    Iterator<Integer> integerIterator = set.iterator();
                    if (integerIterator.hasNext()) {
                        valueFlag = integerIterator.next();
                    }
                }
                if (jsonStr.substring(valueFlag, valueFlag + 1).equals(middleArr[0])) {
                    valueFlag++;
                }
                //截取准确的值
                value = value.substring(0, valueFlag);
                formatSB.append("\n").append(key).append(value);
                jsonStr = jsonStr.substring(valueFlag);
            } else if (endList.contains(firstChar)) {
                int comma = jsonStr.indexOf(",");
                if (comma == 1) {
                    firstChar += ",";
                }
                formatSB.append("\n").append(firstChar);

                jsonStr = jsonStr.substring(1);
            } else {
                jsonStr = jsonStr.substring(1);
            }
        }
        StringBuilder retractSB = new StringBuilder();
        String fillStr = "  ";
        String[] tempArr = formatSB.toString().split("\n");
        formatSB.setLength(0);
        for (String temp : tempArr) {
            if (temp.length() == 0) {
                continue;
            }
            boolean isPlus = (!temp.contains(escapeArr[0]) && !temp.contains(escapeArr[1])
                    && (temp.contains(startList.get(0)) || temp.contains(startList.get(1))));
            boolean isSubtract = (!temp.contains(escapeArr[0]) && !temp.contains(escapeArr[1]))
                    && (temp.contains(endList.get(0)) || temp.contains(endList.get(1)));
            if (isPlus) {
                retractSB.append(fillStr);
            } else if (isSubtract) {
                retractSB.replace(0, fillStr.length(), "");
                int flag = formatSB.lastIndexOf("\n");
                formatSB.replace(flag + 1, flag + 3, "");
            }
            formatSB.append(temp).append("\n").append(retractSB);
        }

        return formatSB.toString();
    }
}
