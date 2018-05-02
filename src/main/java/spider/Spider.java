package spider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
     * @param urlStr 目标url
     * @param arg 参数
     * @param requestHead 请求头
     * @param charset 编码方式
     * @param requestMethod 请求方法
     * @param cookies cookies
     * @param isRedirect 是否重定向
     * @return 1.获取的cookies
     *          2.目标url返回的数据
     *          3.目标url响应状态
     * @throws Exception 向上抛异常
     */
    public static String[] getData(String urlStr, String arg, String requestHead, String charset, String
            requestMethod,String cookies,boolean isRedirect)
            throws Exception {

        System.out.println("URL:" + urlStr + "\n参数:" + arg + "\n请求头:" + requestHead + "\n字符编码:" + charset + "\n请求方法:"
                + requestMethod + "\ncookies:" + cookies + "\n是否重定向:" + isRedirect + "\n");
        String[] requestMethodArr = {"GET", "POST"};
        String[] backData = new String[3];
        BufferedReader reader = null;
        OutputStream outputStream = null;

        if (requestMethodArr[0].equals(requestMethod)) {
            urlStr = urlStr + "?" + arg;
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL(urlStr).openConnection());
            connection.setConnectTimeout(1000);
            //设置请求头
            String[] requestHeadArr = requestHead.split("\n");
            for (String requestProperty : requestHeadArr) {
                String[] arr = requestProperty.split(": ");
                if (arr.length == 2) {
                    connection.setRequestProperty(arr[0], arr[1]);
                }
            }
            connection.setRequestProperty("Cookie", cookies);
            connection.setInstanceFollowRedirects(isRedirect);
            if (requestMethodArr[1].equals(requestMethod)) {
                connection.setDoOutput(true);
                outputStream = connection.getOutputStream();
                outputStream.write(arg.getBytes());
                outputStream.flush();
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
            //获取cookies
            backData[0] = connection.getHeaderField("set-Cookie");
            //获取状态码
            backData[2] = connection.getResponseCode() + "";

            StringBuilder loginInfoSB = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                loginInfoSB.append(line).append("\n");
            }
            //获取登录返回数据
            backData[1] = loginInfoSB.toString();

        } finally {
            try {
                Objects.requireNonNull(outputStream).close();
            } catch (Exception ignore) {
            }
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
    public static String getData(String urlStr, String cookies, String requestHead, String charset) throws Exception {

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

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), charset));
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
