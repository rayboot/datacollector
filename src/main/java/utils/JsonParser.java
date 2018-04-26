package utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * @fileName: JsonParser
 * @author: h1
 * @date: 2018/4/24 13:28
 * @dscription: 解析json数据
 */
public class JsonParser {

    public static List<Map<String, String>> jsonStringToList(String jsonStr) throws Exception {
        JSONArray arr;
        try {
            arr = new JSONArray(jsonStr);
        } catch (JSONException e) {
            jsonStr = "[" + jsonStr + "]";
            arr = new JSONArray(jsonStr);
        }
        List<Map<String, String>> rsList = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jsonObject = arr.getJSONObject(i);
            Map<String, String> map = new HashMap<>(16);
            for (Iterator<?> iterator = jsonObject.keys(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                String value = jsonObject.get(key).toString();
                map.put(key, value);
            }
            rsList.add(map);
        }
        return rsList;
    }

    public static String format(String jsonStr) throws Exception{
        StringBuilder formatSB = new StringBuilder();

        List<String> startList = Arrays.asList("[","{");
        String[] middleArr = {",","\"","\":"};
        List<String> endList = Arrays.asList("}","]");
        String[] escapeArr = {"{}","[]"};
        while (jsonStr.contains(endList.get(0)) || jsonStr.contains(endList.get(1))){
            String firstChar = jsonStr.substring(0,1);
            if (startList.contains(firstChar)){
                formatSB.append("\n").append(firstChar);

                jsonStr = jsonStr.substring(1);
            }else if (firstChar.equals(middleArr[1])){
                //找到key的结束位置
                int keyFlag = jsonStr.indexOf(middleArr[2]);
                String key = jsonStr.substring(1,keyFlag) + ":";

                //去掉"\"":\"
                jsonStr = jsonStr.substring(keyFlag + 2);
                int valueFlag = jsonStr.indexOf(middleArr[0]);
                int rightBraceFlag = jsonStr.indexOf(endList.get(0));
                //如果","的位置小于"}"位置且存在,使用","的位置,否则使用"}"的位置
                valueFlag = valueFlag < rightBraceFlag && valueFlag != -1 ? valueFlag : rightBraceFlag;
                String value = jsonStr.substring(0,valueFlag + 1);
                //如果有符号
                //如果value中存在开始或者结束符号,找到其位置,取出
                boolean temp = value.contains(startList.get(1)) ||
                        value.contains(endList.get(0));
                if (temp){
                    int a = value.indexOf(startList.get(1));
                    int b = value.indexOf(endList.get(0));
                    Set<Integer> set = new LinkedHashSet<>(2);
                    if (a != -1){
                        set.add(a);
                    }
                    if (b != -1){
                        set.add(b);
                    }
                    Iterator<Integer> integerIterator = set.iterator();
                    if (integerIterator.hasNext()){
                        valueFlag = integerIterator.next();
                    }
                }
                if (jsonStr.substring(valueFlag,valueFlag + 1).equals(middleArr[0])){
                    valueFlag ++;
                }
                //截取准确的值
                value = value.substring(0,valueFlag);
                formatSB.append("\n").append(key).append(value);
                jsonStr = jsonStr.substring(valueFlag);
            }else if (endList.contains(firstChar)){
                int comma = jsonStr.indexOf(",");
                if (comma == 1){
                    firstChar += ",";
                }
                formatSB.append("\n").append(firstChar);

                jsonStr = jsonStr.substring(1);
            }else {
                jsonStr = jsonStr.substring(1);
            }
        }
        StringBuilder retractSB = new StringBuilder();
        String fillStr = "  ";
        String[] tempArr = formatSB.toString().split("\n");
        formatSB.setLength(0);
        for (String temp : tempArr){
            if (temp.length() == 0){
                continue;
            }
            boolean isPlus = (!temp.contains(escapeArr[0]) && !temp.contains(escapeArr[1])
                    && (temp.contains(startList.get(0)) || temp.contains(startList.get(1))));
            boolean isSubtract = (!temp.contains(escapeArr[0]) && !temp.contains(escapeArr[1]))
                    && (temp.contains(endList.get(0)) || temp.contains(endList.get(1)));
            if (isPlus){
                retractSB.append(fillStr);
            } else if (isSubtract){
                retractSB.replace(0,fillStr.length(),"");
                int flag = formatSB.lastIndexOf("\n");
                formatSB.replace(flag + 1,flag + 3,"");
            }
            formatSB.append(temp).append("\n").append(retractSB);
        }

        return formatSB.toString();
    }
    public static void main(String...args)throws Exception{
        format("[{\"id\":null,\"text\":\"芜湖明远集团\",\"icon\":\"glyphicon glyphicon-folder-open\",\"cen\":0,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[{\"id\":\"6277A056A00C61DFE053237A8A0A9C0C\",\"text\":\"芜湖明远集团公司电力工程分公司\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":1,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[{\"id\":\"62779D1406DD5E51E053237A8A0A0729\",\"text\":\"技术经济部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"6277A056A01461DFE053237A8A0A9C0C\",\"text\":\"电力工程--综合管理部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"6277A056A02061DFE053237A8A0A9C0C\",\"text\":\"工程技术部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"6277B0BAE86163C0E053237A8A0ABE6D\",\"text\":\"市场业务部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CA585B371D8D8E053237A8A0AEA5A\",\"text\":\"线路工程三队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CA585B379D8D8E053237A8A0AEA5A\",\"text\":\"线路工程四队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CA585B381D8D8E053237A8A0AEA5A\",\"text\":\"配网工程一队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CA585B389D8D8E053237A8A0AEA5A\",\"text\":\"配网工程二队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CA585B391D8D8E053237A8A0AEA5A\",\"text\":\"配网工程三队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CA6683CF2D9DCE053237A8A0AF5ED\",\"text\":\"变电工程四队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CA6683CFAD9DCE053237A8A0AF5ED\",\"text\":\"线路工程一队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CA6683D02D9DCE053237A8A0AF5ED\",\"text\":\"线路工程二队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CAE1EE36EDF2DE053237A8A0AF0E2\",\"text\":\"变电工程一队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CAE1EE376DF2DE053237A8A0AF0E2\",\"text\":\"变电工程二队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CAE1EE37EDF2DE053237A8A0AF0E2\",\"text\":\"变电工程三队\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]}]},{\"id\":\"6277A0569FD861DFE053237A8A0A9C0C\",\"text\":\"芜湖明远集团公司\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":1,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[{\"id\":\"62779D1406B95E51E053237A8A0A0729\",\"text\":\"综合管理部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"62779D1406C15E51E053237A8A0A0729\",\"text\":\"人力资源部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"6277B0BAE81B63C0E053237A8A0ABE6D\",\"text\":\"财务资产部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"6277B0BAE82763C0E053237A8A0ABE6D\",\"text\":\"安全监察质量部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"6277B0BAE83163C0E053237A8A0ABE6D\",\"text\":\"物资供应部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"6277B0BAE83B63C0E053237A8A0ABE6D\",\"text\":\"经营策划部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[{\"id\":\"6277A056A00461DFE053237A8A0A9C0C\",\"text\":\"信息通信室\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":3,\"state\":null,\"url\":null,\"open\":null,\"nodes\":null},{\"id\":\"6277B0BAE84963C0E053237A8A0ABE6D\",\"text\":\"经济运营室\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":3,\"state\":null,\"url\":null,\"open\":null,\"nodes\":null},{\"id\":\"6277B0BAE85163C0E053237A8A0ABE6D\",\"text\":\"建筑工程室\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":3,\"state\":null,\"url\":null,\"open\":null,\"nodes\":null}]}]},{\"id\":\"627CAE1EE3CEDF2DE053237A8A0AF0E2\",\"text\":\"芜湖明远物业管理有限公司宾馆分公司\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":1,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CA585B3A3D8D8E053237A8A0AEA5A\",\"text\":\"芜湖中安电力设计院有限公司\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":1,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[{\"id\":\"627CAE1EE398DF2DE053237A8A0AF0E2\",\"text\":\"电力设计院--综合管理部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CAE1EE3A0DF2DE053237A8A0AF0E2\",\"text\":\"电力设计院--技术经济部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CAE1EE3A6DF2DE053237A8A0AF0E2\",\"text\":\"变电设计部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CAE1EE3AEDF2DE053237A8A0AF0E2\",\"text\":\"系统设计部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CAE1EE3B6DF2DE053237A8A0AF0E2\",\"text\":\"电气设计部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CAE1EE3BEDF2DE053237A8A0AF0E2\",\"text\":\"土建设计部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"627CAE1EE3C6DF2DE053237A8A0AF0E2\",\"text\":\"线路设计部\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":2,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]}]},{\"id\":\"62F30C8BF3E50760E053237A8A0A386D\",\"text\":\"测试2\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":1,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"64858C5E01DF3020E050007F01005231\",\"text\":\"测试1\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":1,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"68791E18CAD8AF93E050007F010061DA\",\"text\":\"测试3\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":1,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]},{\"id\":\"68791E18CADBAF93E050007F010061DA\",\"text\":\"测试4\",\"icon\":\"glyphicon glyphicon-folder-close\",\"cen\":1,\"state\":null,\"url\":null,\"open\":null,\"nodes\":[]}]}]");
    }
}
