package spider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * @fileName: WeiBo
 * @author: h1
 * @date: 2018/4/24 16:48
 * @dscription:
 */
public class WeiBo {

    public static void main(String... args) {
        WeiBo spider = new WeiBo();
        String cookies = spider.getContent("https://weibo.com/u/5266578139/home?leftnav=1&pids=plc_main&ajaxpagelet=1&ajaxpagelet_v6=1&__ref=%2Fu%2F5266578139%2Fhome%3Fwvr%3D5&_t=FM_152456006885122");
        System.out.println(cookies);
    }

    public String getContent(String urlStr) {

        BufferedReader reader = null;
        StringBuilder contentSB = new StringBuilder();

        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL(urlStr).openConnection());
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Cookie", "SINAGLOBAL=5112133408816.364.1524556971930; login_sid_t=8354613b0f00df9332c99adff6266cdb; cross_origin_proto=SSL; YF-Ugrow-G0=169004153682ef91866609488943c77f; YF-V5-G0=46bd339a785d24c3e8d7cfb275d14258; _s_tentry=-; Apache=4548448018696.065.1524557908021; ULV=1524557908026:2:2:2:4548448018696.065.1524557908021:1524556971934; YF-Page-G0=c81c3ead2c8078295a6f198a334a5e82; WBtopGlobal_register_version=2835f82aba1b9774; un=18155907303; UOR=,,login.sina.com.cn; SCF=AniiN1tnHOXkKfWSrM-OYyfIgyAg75a-dEUg9UnLI1F5PmDV3LpX2EypY7tYi8EcAblzC_8SVtDvINtSg7vJ3nA.; SUB=_2A2532oCcDeRhGeNM7VQU9ybNyDWIHXVUkfVUrDV8PUNbmtBeLVitkW9NThI7hg8C-rGLEoR_kk3kDIIpWldlL_H0; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW9WCY3fKSVCRK0AfwOueAF5JpX5K2hUgL.Fo-ESoqfS0npe0.2dJLoIXnLxKqL1hnL1K2LxKnLBKqL1h2LxK-L12BL1h2LxKBLBonL1h5LxKML1hzLBo.LxK-L12BL122LxKML1-BLBK2LxKnLBKML1h.t; SUHB=0A0566bSl5RFN8; ALF=1525164867; SSOLoginState=1524560076; wvr=6");
            connection.setRequestProperty("Host", "weibo.com");
            connection.setRequestProperty("Referer", "https://weibo.com/u/5266578139/home?wvr=5");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

            /*String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line + "\n");
            }
            System.out.println(contentSB);*/
            int a;
            while ((a = reader.read()) != -1) {
                System.out.print(a + ",,");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(reader).close();
            } catch (Exception ignore) {
            }
        }

        return contentSB.toString();
    }

}
