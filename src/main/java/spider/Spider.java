package spider;

import utils.JsonParser;
import utils.WordChecker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @fileName: Spider
 * @author: h1
 * @date: 2018/4/24 9:38
 * @dscription: 简单系统数据采集
 */
public class Spider {

    public static void main(String... args) {
        Spider spider = new Spider();
        String cookies = spider.getCookies("http://192.168.1.223:8081/rwgk/landManage/userLogin", "sysadmin",
                "1q2w3e4r!");
        System.out.println(cookies);
        System.out.println(spider.getData("http://192.168.1.223:8081/rwgk/taskLaunchManage/findTaskByParam?pageindex=1" +
                "&pageSize=20&sortName=taskCreatetime&sortOrder=desc&taskName=&taskStatus=0", cookies));
    }

    private String getCookies(String urlStr, String userName, String password) {

        String cookies = "";
        BufferedReader reader = null;
        urlStr += "?userName=" + userName + "&password=1e9e9f6fef3369cdc763284d80ae5feb";

        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL(urlStr).openConnection());
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Content-Length", "85");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            cookies = connection.getHeaderField("set-Cookie") + ";";

            StringBuilder loginInfoSB = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                loginInfoSB.append(line).append("\n");
            }

            List<Map<String, String>> jsonList = JsonParser.jsonStringToList(loginInfoSB.toString());
            loginInfoSB.setLength(0);
            for (Map<String, String> map : jsonList) {
                Set<String> keySet = map.keySet();
                for (String keyStr : keySet) {
                    String value = map.get(keyStr);
                    if (WordChecker.containChinese(value)) {
                        value = WordChecker.gbEncoding(value);
                    }
                    loginInfoSB.append(keyStr).append("=").append(value).append(";");
                }
            }
            cookies += loginInfoSB.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(reader).close();
            } catch (Exception ignore) {
            }
        }

        return cookies;
    }

    private String getData(String urlStr, String cookies) {

        StringBuilder response = new StringBuilder();
        BufferedReader reader = null;

        try {
            URLConnection urlConnection = new URL(urlStr).openConnection();
            urlConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            urlConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Length", "85");
            urlConnection.setRequestProperty("Cookie", cookies);
            urlConnection.setRequestProperty("Host", "localhost:28080");
            urlConnection.setRequestProperty("Origin", "http://localhost:28080");
            urlConnection.setRequestProperty("Referer", "http://localhost:28080/rwgk/taskLaunchManage");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
            urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(reader).close();
            } catch (Exception ignore) {
            }
        }

        return response.toString();
    }
}
