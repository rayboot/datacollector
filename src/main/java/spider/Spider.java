package spider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

/**
 * @fileName: Spider
 * @author: h1
 * @date: 2018/4/24 9:38
 * @dscription: 简单系统数据采集
 */
public class Spider {

    /**
     * 使用指定地址登录
     *
     * @param urlStr 目标地址
     * @return 1.cookies;
     * 2.返回数据
     * @throws Exception 向上抛异常
     */
    public static String[] login(String urlStr, String requestHead) throws Exception {

        String[] backData = new String[2];
        BufferedReader reader = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL(urlStr).openConnection());
            //设置请求头
            String[] requestHeadArr = requestHead.split("\n");
            for (String requestProperty : requestHeadArr) {
                String[] arr = requestProperty.split(": ");
                if (arr.length == 2) {
                    connection.setRequestProperty(arr[0], arr[1]);
                }
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            //获取cookies
            backData[0] = connection.getHeaderField("set-Cookie");

            StringBuilder loginInfoSB = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                loginInfoSB.append(line).append("\n");
            }
            //获取登录返回数据
            backData[1] = loginInfoSB.toString();

        } finally {
            try {
                Objects.requireNonNull(reader).close();
            } catch (Exception ignore) {
            }
        }

        return backData;
    }

    /**
     * 获取数据
     *
     * @param urlStr  目标url
     * @param cookies cookies
     * @return 返回数据
     */
    public static String getData(String urlStr, String cookies, String requestHead) throws Exception {

        StringBuilder response = new StringBuilder();
        BufferedReader reader = null;

        try {
            URLConnection urlConnection = new URL(urlStr).openConnection();
            //设置请求头
            String[] requestHeadArr = requestHead.split("\n");
            for (String requestProperty : requestHeadArr) {
                String[] arr = requestProperty.split(": ");
                if (arr.length == 2) {
                    urlConnection.setRequestProperty(arr[0], arr[1]);
                }
            }
            urlConnection.setRequestProperty("Cookie", cookies);

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
        } finally {
            try {
                Objects.requireNonNull(reader).close();
            } catch (Exception ignore) {
            }
        }

        return response.toString();
    }
}
