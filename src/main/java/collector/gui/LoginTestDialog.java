package collector.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @fileName: LoginTestDialog
 * @author: h1
 * @date: 2018-5-10 10:11:05
 * @dscription:
 */
public class LoginTestDialog extends JDialog {
    private JPanel contentPane;
    private JTextField firstURLField;
    private JTextField sessionIdField;
    private JTextField ltField;
    private JTextField executionField;
    private JTextField eventIdField;
    private JButton firstHtmlBtn;
    private JButton firstBtn;
    private JButton firstResponseBtn;
    private JTextField cookieField;
    private JTextArea firstRequestHeaderArea;
    private JTextField secondURLField;
    private JTextField secondParameterField;
    private JTextArea secondRequestHeaderArea;
    private JTextField secondLocationField;
    private JButton secondResponseBtn;
    private JButton secondHtmlBtn;
    private JButton secondBtn;
    private JTextField thirdURLField;
    private JTextArea thirdRequestHeaderArea;
    private StringBuilder firstHtmlSB = new StringBuilder();
    private StringBuilder firstResponseSB = new StringBuilder();
    private StringBuilder secondHtmlSB = new StringBuilder();
    private StringBuilder secondResponseSB = new StringBuilder();

    public LoginTestDialog() {
        setContentPane(contentPane);
        setModal(true);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        firstBtn.addActionListener(e -> firstLogin());

        secondBtn.addActionListener(e -> secondLogin());
    }

    public static void main(String[] args) {
        LoginTestDialog dialog = new LoginTestDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void messageDialog(JTextArea textArea){
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500,300));
        JOptionPane.showMessageDialog(null,scrollPane,"HTML",JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 初次访问网站获取SessionId,Cookie3,lt,execution,_eventId
     */
    private void firstLogin(){
        String urlStr = firstURLField.getText();

        BufferedReader reader = null;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(urlStr).openConnection());
            //设置请求头
            String requestHeader = firstRequestHeaderArea.getText();
            String[] requestHeaders = requestHeader.split("\n");
            for (String requestProperty : requestHeaders) {
                String[] arr = requestProperty.split(": ");
                if (arr.length == 2) {
                    httpURLConnection.setRequestProperty(arr[0], arr[1]);
                }
            }
            //获取响应头中的Set-Cookie
            Map<String,List<String>> responseMap = httpURLConnection.getHeaderFields();
            List<String> cookies = responseMap.get("Set-Cookie");
            try {
                sessionIdField.setText(cookies.get(0));
                cookieField.setText(cookies.get(1));
            }catch (Exception ignore){}
            Set<String> keySet = responseMap.keySet();
            for (String key : keySet){
                List<String> valueList = responseMap.get(key);
                for (String value : valueList) {
                    firstResponseSB.append(key).append(":").append(value).append("\n");
                }
            }
            firstResponseBtn.addActionListener(e -> {
                JTextArea textArea = new JTextArea(firstResponseSB.toString());
                messageDialog(textArea);
            });

            reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                firstHtmlSB.append(line).append("\n");
                int valueIndex;
                String value;
                if (line.contains("name=\"lt\"")){
                    valueIndex = line.indexOf("value=\"");
                    value = line.substring(valueIndex + 7,line.length() - 4);
                    ltField.setText(value);
                }else if (line.contains("name=\"execution\"")){
                    valueIndex = line.indexOf("value=\"");
                    value = line.substring(valueIndex + 7,line.length() - 4);
                    executionField.setText(value);
                }else if (line.contains("name=\"_eventId\"")){
                    valueIndex = line.indexOf("value=\"");
                    value = line.substring(valueIndex + 7,line.length() - 4);
                    eventIdField.setText(value);
                }
            }
            firstHtmlBtn.addActionListener(e -> {
                JTextArea textArea = new JTextArea(firstHtmlSB.toString());
                messageDialog(textArea);
            });

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                Objects.requireNonNull(reader).close();
            }catch (Exception ignore){}
        }
    }

    private void secondLogin(){
        String sessionId = sessionIdField.getText();
        try {
            sessionId = sessionId.substring(0, sessionId.indexOf(";"));
        }catch (Exception e){
            e.printStackTrace();
        }
        String cookie3 = cookieField.getText();
        try {
            cookie3 = cookie3.substring(0, cookie3.indexOf(";"));
        }catch (Exception e){
            e.printStackTrace();
        }
        String urlStr = secondURLField.getText();
        int index = urlStr.indexOf("?");
        String hostStr = urlStr.substring(0,index);
        String serviceStr = urlStr.substring(index);
        urlStr = hostStr + ";" + sessionId + serviceStr;

        BufferedReader reader = null;
        OutputStream outputStream = null;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(urlStr).openConnection());
            //设置请求头
            String requestHeader = secondRequestHeaderArea.getText();
            String[] requestHeaders = requestHeader.split("\n");
            for (String requestProperty : requestHeaders) {
                String[] arr = requestProperty.split(": ");
                if (arr.length == 2) {
                    httpURLConnection.setRequestProperty(arr[0], arr[1]);
                }
            }
            String cookieStr = sessionId + ";" + cookie3;
            httpURLConnection.setRequestProperty("Cookie",cookieStr);
            httpURLConnection.setDoOutput(true);
            outputStream = httpURLConnection.getOutputStream();
            String parameterStr = secondParameterField.getText();
            parameterStr = parameterStr.replace("$(lt)",ltField.getText())
                    .replace("$(execution)",executionField.getText())
                    .replace("$(_eventId)",eventIdField.getText());
            outputStream.write(parameterStr.getBytes());
            outputStream.flush();

            //获取响应头中的Location
            Map<String,List<String>> responseMap = httpURLConnection.getHeaderFields();
            List<String> locationList = responseMap.get("Location");
            try {
                secondLocationField.setText(locationList.get(0));
            }catch (Exception ignore){}
            Set<String> keySet = responseMap.keySet();
            for (String key : keySet){
                List<String> valueList = responseMap.get(key);
                for (String value : valueList) {
                    secondResponseSB.append(key).append(":").append(value).append("\n");
                }
            }
            secondResponseBtn.addActionListener(e -> {
                JTextArea textArea = new JTextArea(secondResponseSB.toString());
                messageDialog(textArea);
            });

            reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                secondHtmlSB.append(line).append("\n");
            }
            secondHtmlBtn.addActionListener(e -> {
                JTextArea textArea = new JTextArea(secondHtmlSB.toString());
                messageDialog(textArea);
            });
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                Objects.requireNonNull(outputStream).close();
            }catch (Exception ignore){}
            try{
                Objects.requireNonNull(reader).close();
            }catch (Exception ignore){}
        }
    }
}
