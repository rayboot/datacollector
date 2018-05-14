package collector.spider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * @fileName: MeetingData
 * @author: h1
 * @date: 2018/5/14 9:10
 * @dscription: 模拟登陆, 获取cookies;访问特定页面,获取token;获取url返回数据
 */
public class MeetingData {

    /**
     * 主机地址
     */
    private String hostStr = "http://iscsso.sgcc.com.cn/isc_sso/login";
    /**
     * 服务地址
     */
    private String serviceStr = "service=http%3A%2F%2F10.1.180.3%3A80%2Fhst%2Findex%2Fmain%2Findex.jsp";
    /**
     * 初始化登录页面的参数
     */
    private String sessionId;
    private String lt;
    private String execution;
    private String eventId;
    /**
     * 重定向的验证URL
     */
    private String redirectedTokenUrlStr;

    public static void main(String... args) throws Exception {
        MeetingData meetingData = new MeetingData();
        //获取cookie
        String cookie = meetingData.getLoginCookie("panl2034", "0551*panling");
        //获取token
        String token = meetingData.getToken("http://10.1.180.3/hst/meetingmanager/querymeeting/jsp/queryMetting.jsp",
                cookie);
        //获取数据
        String data = meetingData.getData("http://10.1.180.3/hst/meetingmanager/rest/meetingManager/queryMeeting",
                cookie, "{\"items\":[{\"searchMeetingDeptLevel\":\"1\",\"searchMeetingTabType\":\"1\"," +
                        "\"searchMeetingType\":\"-1\",\"searchMeetingCategory\":\"-1\",\"searchMeetingStatus\":\"0," +
                        "1\",\"searchDeptName\":\"\",\"searchMeetingName\":\"\",\"searchMeetingStartTime\":\"2018-05-11\"," +
                        "\"searchMeetingEndTime\":\"2018-05-11\",\"searchOrgNo\":\"-1\",\"searchResource1\":\"-1\"," +
                        "\"searchResource2\":\"-1\",\"deptOption\":\"-1\",\"pageNo\":1,\"pageSize\":5,\"token\":\"" +
                        token + "\"}]}", 1);

        System.out.println(cookie + "\n" + token + "\n" + data);
    }

    /**
     * Base64加密
     *
     * @param rowStr 加密前字符串
     * @return 加密后字符串
     * @throws Exception 抛出异常
     */
    private String encode(String rowStr) throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] rowBytes = rowStr.getBytes("UTF-8");
        return encoder.encodeToString(rowBytes);
    }

    /**
     * 打开登录界面,获取服务器返回的Session ID,lt,execution,eventId
     *
     * @throws Exception 捕获异常
     */
    private void getLoginPageParameter() throws Exception {
        //打开页面
        HttpURLConnection httpURLConnection =
                (HttpURLConnection) (new URL(hostStr + "?" + serviceStr).openConnection());
        //获取头字段map
        Map<String, List<String>> headerFieldsMap = httpURLConnection.getHeaderFields();
        //获取sessionId
        sessionId = headerFieldsMap.get("Set-Cookie").get(0);
        sessionId = sessionId.substring(0, sessionId.indexOf(";"));

        //获取输入流
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //在字符串中查找各个参数
                int valueIndex;
                if (line.contains("name=\"lt\"")) {
                    valueIndex = line.indexOf("value=\"");
                    lt = line.substring(valueIndex + 7, line.length() - 4);
                } else if (line.contains("name=\"execution\"")) {
                    valueIndex = line.indexOf("value=\"");
                    execution = line.substring(valueIndex + 7, line.length() - 4);
                } else if (line.contains("name=\"_eventId\"")) {
                    valueIndex = line.indexOf("value=\"");
                    eventId = line.substring(valueIndex + 7, line.length() - 4);
                }
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }

    /**
     * 连接登录url,输入凭证,获取重定向的验证链接
     *
     * @throws Exception 抛出异常
     */
    private void getLoginTokenRedirect(String loginName, String password) throws Exception {
        //拼接输入帐号密码的链接
        String loginUrl = hostStr + ";" + sessionId + "?" + serviceStr;
        //请求头参数
        String[] requestProperties = {
                "Host：iscsso.sgcc.com.cn",
                "Connection：keep-alive",
                "Content-Length：133",
                "Cache-Control：max-age=0",
                "Accept：text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                "Origin：http://iscsso.sgcc.com.cn",
                "Upgrade-Insecure-Requests：1",
                "User-Agent：Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36",
                "Content-Type：application/x-www-form-urlencoded",
                "Referer：" + loginUrl,
                "Accept-Encoding：gzip,deflate,sdch",
                "Accept-Language：zh-CN,zh;q=0.8",
                "Accept：zh-text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
        };
        //打开连接
        HttpURLConnection httpURLConnection =
                (HttpURLConnection) (new URL(loginUrl).openConnection());
        //禁止自动执行重定向
        httpURLConnection.setInstanceFollowRedirects(false);
        //打开使用URL连接输出
        httpURLConnection.setDoOutput(true);
        //设置请求属性
        for (String requestPropertyStr : requestProperties) {
            String[] requestProperty = requestPropertyStr.split("：");
            httpURLConnection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        //写入凭证
        String evidenceStr = "wangsheng=ah&username=" + loginName + "&password=" + password + "&lt=" + lt
                + "&execution=" + execution + "&_eventId=" + eventId;
        try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
            outputStream.write(evidenceStr.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            throw new Exception();
        }
        //获取重定向的验证链接
        Map<String, List<String>> headerFieldsMap = httpURLConnection.getHeaderFields();
        redirectedTokenUrlStr = headerFieldsMap.get("Location").get(0);
    }

    /**
     * 打开重定向验证链接,获取cookie
     *
     * @return cookie
     * @throws Exception 抛出异常
     */
    private String getCookie() throws Exception {
        //请求头参数
        String[] requestProperties = {
                "Host：10.1.180.3",
                "Connection：keep-alive",
                "Upgrade-Insecure-Requests：1",
                "User-Agent：Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36",
                "Accept：zh-text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
                "Referer：http://10.1.180.3/hst/meetingmanager/querymeeting/index.jsp",
                "Accept-Encoding：gzip, deflate",
                "Accept-Language：zh-CN,zh;q=0.9",
        };
        //打开重定向链接
        HttpURLConnection httpURLConnection =
                (HttpURLConnection) (new URL(redirectedTokenUrlStr).openConnection());
        //禁止自动重定向
        httpURLConnection.setInstanceFollowRedirects(false);
        //设置请求属性
        for (String requestPropertyStr : requestProperties) {
            String[] requestProperty = requestPropertyStr.split("：");
            httpURLConnection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        //获取登录cookie
        Map<String, List<String>> headerFieldsMap = httpURLConnection.getHeaderFields();
        return headerFieldsMap.get("Set-Cookie").get(0);
    }

    /**
     * 获取登录cookie
     *
     * @param loginName 登录名
     * @param password  登录密码
     * @return 登录cookie
     * @throws Exception 抛出异常
     */
    public String getLoginCookie(String loginName, String password) throws Exception {
        try {
            password = encode(password);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("密码加密失败,采用未加密密码");
        }
        System.out.println(password);
        try {
            getLoginPageParameter();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("获取登录页面参数失败");
        }
        try {
            getLoginTokenRedirect(loginName, password);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("获取重定向验证链接失败");
        }
        try {
            return getCookie();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("打开验证链接获取cookie失败");
        }
    }

    /**
     * 获取页面的token
     *
     * @param urlStr 访问链接
     * @param cookie 登录cookie
     * @return 页面token
     * @throws Exception 抛出异常
     */
    public String getToken(String urlStr, String cookie) throws Exception {
        String token = "";
        //请求头参数
        String[] requestProperties = {
                "Host：10.1.180.3",
                "Connection：keep-alive",
                "Upgrade-Insecure-Requests：1",
                "User-Agent：Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36",
                "Accept：zh-text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
                "Referer：http://10.1.180.3/hst/meetingmanager/querymeeting/index.jsp",
                "Accept-Encoding：gzip, deflate",
                "Accept-Language：zh-CN,zh;q=0.9",
                "Cookie：" + cookie
        };
        //打开连接
        HttpURLConnection httpURLConnection =
                (HttpURLConnection) (new URL(urlStr).openConnection());
        //设置请求属性
        for (String requestPropertyStr : requestProperties) {
            String[] requestProperty = requestPropertyStr.split("：");
            httpURLConnection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
            String line;
            boolean isFlag = false;
            while ((line = reader.readLine()) != null) {
                if (isFlag) {
                    int start = line.indexOf("\"") + 1;
                    int end = line.lastIndexOf("\"");
                    token = line.substring(start, end);
                }
                if (line.contains("function getToken(){")) {
                    isFlag = true;
                }
            }
        }

        return token;
    }

    /**
     * 使用参数获取指定url返回的数据
     *
     * @param urlStr        目标url
     * @param cookies       登录cookies
     * @param parameter     参数
     * @param requestMethod 请求方法:GET:0,POST:1
     * @return 目标url返回的数据
     * @throws Exception 抛出异常
     */
    public String getData(String urlStr, String cookies, String parameter, int requestMethod) throws Exception {
        //请求头参数
        String[] requestProperties = {
                "Host：10.1.180.3",
                "Origin：http://10.1.180.3",
                "Connection：keep-alive",
                "Content-Type：application/json",
                "User-Agent：Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.170 Safari/537.36",
                "Accept：application/json, text/javascript, */*; q=0.01",
                "Accept-Encoding：gzip, deflate",
                "Accept-Language：zh-CN,zh;q=0.9",
                "X-Requested-With:XMLHttpRequest"
        };
        //请求方式为GET
        if (requestMethod == 0) {
            urlStr += "?" + parameter;
        }
        //打开连接
        HttpURLConnection httpURLConnection =
                (HttpURLConnection) (new URL(urlStr).openConnection());
        //设置请求属性
        for (String requestPropertyStr : requestProperties) {
            String[] requestProperty = requestPropertyStr.split("：");
            httpURLConnection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        //设置cookie
        httpURLConnection.setRequestProperty("Cookie", cookies);
        if (requestMethod == 0) {
            //请求方式为GET
            httpURLConnection.setRequestMethod("GET");
        } else if (requestMethod == 1) {
            //请求方式为POST
            httpURLConnection.setRequestMethod("POST");
            //打开URL连接输出
            httpURLConnection.setDoOutput(true);
            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                outputStream.write(parameter.getBytes());
                outputStream.flush();
            } catch (Exception e) {
                throw new Exception();
            }
        }
        StringBuilder dataSB = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),
                "utf-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dataSB.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new Exception();
        }

        return dataSB.toString();
    }
}