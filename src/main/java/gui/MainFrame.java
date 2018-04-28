package gui;

import spider.Spider;
import utils.Base64Tool;
import utils.JsonParser;
import utils.Security;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.HttpURLConnection;

/**
 * @fileName: MainFrame
 * @author: h1
 * @date: 2018/4/25 11:25
 * @dscription:
 */
public class MainFrame extends JFrame implements ActionListener {
    private JPanel mainPane;
    private JTextField urlField;
    private JTextField argField;
    private JTextField nameField;
    private JPasswordField pwdField;
    private JButton verifyBtn;
    private JTextField cookiesField;
    private JTextPane dataTextPane;
    private JTextArea requestHeadArea;
    private JButton formatBtn;
    private JComboBox pwdComboBox;
    private JComboBox charsetComboBox;
    private JComboBox nameComboBox;
    private JComboBox requestMethodComboBox;
    private Light light;
    private JPanel lightPane;
    private JLabel responseCodeLabel;
    /**
     * 原始获得未格式化的json数据
     */
    private String rowJsonData;
    /**
     * 自配颜色
     */
    private Color[] colorArr = {
            new Color(133, 174, 233),
            new Color(32, 74, 135),
            new Color(71, 71, 71),
            new Color(78, 154, 6),
            new Color(0, 0, 255),
            new Color(198, 200, 195),
            new Color(136, 19, 145),
            new Color(195, 160, 0),
            new Color(60, 60, 60)
    };
    /**
     * 格式化按钮文本
     */
    private String[] btnTextArr = {"格式化", "原始数据"};
    /**
     * 编码方式
     */
    private String charset;
    /**
     * 请求方法
     */
    private String requestMethod;
    /**
     * 未加密的用户名
     */
    private String rowName;
    /**
     * 在用的用户名
     */
    private String inUseName;
    /**
     * 加密前的密码
     */
    private String rowPwd;
    /**
     * 在用的密码
     */
    private String inUsePwd;

    public MainFrame() {

        charset = charsetComboBox.getItemAt(0).toString();
        requestMethod = requestMethodComboBox.getItemAt(0).toString();

        //设置配置组件
        setConfComponent();

        //设置获取data组件
        setDataComponent();

        add(mainPane);
        setTitle("数据采集测试");
        setBounds(33, 50, 800, 650);
        //setBounds(200, 50, 1500, 927);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * 加密名称
     */
    private void encodeName() {
        int nameIndex = nameComboBox.getSelectedIndex();
        switch (nameIndex) {
            case 1:
                //加密用户名失败使用原始用户名
                try {
                    inUseName = Base64Tool.encode(rowName, "UTF-8");
                } catch (Exception ex) {
                    popErrorInfo(ex, "加密用户名失败");
                }
                break;
            case 2:
                try {
                    inUseName = Security.md5(rowName);
                } catch (Exception ex) {
                    popErrorInfo(ex, "加密用户名失败");
                }
                break;
            case 0:
            default:
                inUseName = rowName;
                break;
        }

    }

    /**
     * 加密密码
     */
    private void encodePassword() {

        int pwdIndex = pwdComboBox.getSelectedIndex();
        switch (pwdIndex) {
            case 1:
                //加密密码名失败使用原始密码
                try {
                    inUsePwd = Base64Tool.encode(rowPwd, "UTF-8");
                } catch (Exception ex) {
                    popErrorInfo(ex, "加密密码失败");
                }
                break;
            case 2:
                try {
                    inUsePwd = Security.md5(rowPwd);
                } catch (Exception ex) {
                    popErrorInfo(ex, "加密密码失败");
                }
                break;
            case 0:
            default:
                inUsePwd = rowPwd;
                break;
        }
    }

    /**
     * 模拟访问
     */
    private void visit() {

        String urlStr = urlField.getText();
        String arg = argField.getText();
        //替换用户名
        arg = arg.replace("$(USERNAME)", inUseName);
        //替换密码
        arg = arg.replace("$(PASSWORD)", inUsePwd);
        String requestHeadStr = requestHeadArea.getText();

        String[] backData = {"", "", "404"};
        try {
            //获取到数据后,放置数据
            backData = Spider.getData(urlStr, arg, requestHeadStr, charset, requestMethod);
            cookiesField.setText(backData[0]);
            setRowJsonDataToDataArea(backData[1]);
            rowJsonData = backData[1];
            formatBtn.setText(btnTextArr[0]);
        } catch (Exception ex) {
            //出错后弹出提示
            popErrorInfo(ex, "获取数据异常");
        }
        int responseCode = Integer.parseInt(backData[2]);
        lightPane.setVisible(true);
        responseCodeLabel.setText(responseCode + "");
        if (responseCode >= HttpURLConnection.HTTP_OK && responseCode <= HttpURLConnection.HTTP_PARTIAL) {
            light.setColor(Color.green);
        } else {
            light.setColor(Color.red);
        }
    }

    /**
     * 格式化数据区json数据
     */
    private void format() {

        String btnName = formatBtn.getText();
        if (btnTextArr[0].equals(btnName)) {
            try {
                String formatData = JsonParser.format(rowJsonData);
                insertObjectToDataArea(formatData);
                formatBtn.setText(btnTextArr[1]);
            } catch (Exception ex) {
                popErrorInfo(ex, "格式化JSON数据失败");
            }
        } else if (btnTextArr[1].equals(btnName)) {
            try {
                setRowJsonDataToDataArea(rowJsonData);
                formatBtn.setText(btnTextArr[0]);
            } catch (Exception ex) {
                popErrorInfo(ex, "重置原始数据失败");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        encodeName();
        encodePassword();
        //获取动作命令
        String cmdStr = e.getActionCommand();
        //根据动作命令做操作
        switch (cmdStr) {
            case "nameEncode":
                encodeName();
                break;
            case "pwdEncode":
                encodePassword();
                break;
            case "charset":
                int charsetIndex = charsetComboBox.getSelectedIndex();
                charset = charsetComboBox.getItemAt(charsetIndex).toString();
                break;
            case "requestMethod":
                int requestMethodIndex = requestMethodComboBox.getSelectedIndex();
                requestMethod = requestMethodComboBox.getItemAt(requestMethodIndex).toString();
                break;
            case "data":
                visit();
                break;
            case "format":
                format();
                break;
            default:
                break;
        }
    }

    /**
     * 弹出异常
     *
     * @param e 异常对象
     */
    private void popErrorInfo(Exception e, String title) {
        StringBuilder msg = new StringBuilder(e.toString());
        //获得栈元素
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        //挨个添加
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            msg.append("\n").append(stackTraceElement.toString());
        }
        //实例化文本域并设置
        JTextArea textArea = new JTextArea(msg.toString());
        textArea.setLineWrap(false);
        textArea.setEditable(false);
        //实例化滚动面板并设置
        JScrollPane scrollPane = new JScrollPane(textArea);
        //固定住大小
        scrollPane.setPreferredSize(new Dimension(400, 300));
        //弹出异常面板
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 设置获取cookies组件
     */
    private void setConfComponent() {
        //设置动作命令
        urlField.setActionCommand("data");
        argField.setActionCommand("data");
        nameField.setActionCommand("data");
        pwdField.setActionCommand("data");
        verifyBtn.setActionCommand("data");
        nameComboBox.setActionCommand("nameEncode");
        pwdComboBox.setActionCommand("pwdEncode");
        charsetComboBox.setActionCommand("charset");
        requestMethodComboBox.setActionCommand("requestMethod");
        //添加响应事件
        verifyBtn.addActionListener(this);
        urlField.addActionListener(this);
        argField.addActionListener(this);
        nameField.addActionListener(this);
        pwdField.addActionListener(this);
        nameComboBox.addActionListener(this);
        pwdComboBox.addActionListener(this);
        charsetComboBox.addActionListener(this);
        requestMethodComboBox.addActionListener(this);

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                rowName = nameField.getText();
            }
        });

        pwdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                rowPwd = new String(pwdField.getPassword());
            }
        });
    }

    /**
     * 设置数据区组件属性
     */
    private void setDataComponent() {
        //设置动作命令
        formatBtn.setActionCommand("format");
        //添加响应事件
        formatBtn.addActionListener(this);
    }

    /**
     * 使用给定字符串插入JTextPane,并根据不同的内容使用不同的颜色
     *
     * @param object 字符串
     * @throws Exception 异常
     */
    private void insertObjectToDataArea(String object) throws Exception {
        String[] symbolArr = {
                ":",
                "\"",
                ",",
                "true",
                "false",
                "TRUE",
                "FALSE",
                "null",
                "NULL",
                "{",
                "[",
                "}",
                "]",
                " "
        };
        //根据换行符分割字符串
        String[] strArr = object.split("\n");
        dataTextPane.setText(null);
        //获取文档风格对象
        StyledDocument document = dataTextPane.getStyledDocument();
        //实例化属性集
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        for (String str : strArr) {
            //获取第一个不为" "的字符
            String firstChar = str.substring(0, 1);
            int i = 1;
            while (firstChar.contains(symbolArr[13])) {
                firstChar = str.substring(i, i + 1);
                i++;
            }
            //检测第一个字符是否是"[","{","}","]",如果是,改变颜色,进入下一次循环
            if (firstChar.equals(symbolArr[9]) || firstChar.equals(symbolArr[10]) || firstChar.equals
                    (symbolArr[11]) || firstChar.equals(symbolArr[12])) {
                StyleConstants.setForeground(attributeSet, colorArr[0]);
                document.insertString(document.getLength(), str + "\n", attributeSet);
                continue;
            }
            //改变key颜色
            int colonFlag = str.indexOf(symbolArr[0]);
            String key = str.substring(0, colonFlag);
            StyleConstants.setForeground(attributeSet, colorArr[6]);
            document.insertString(document.getLength(), key, attributeSet);

            //改变":"颜色
            StyleConstants.setForeground(attributeSet, colorArr[2]);
            document.insertString(document.getLength(), ":", attributeSet);

            //改变value颜色
            String value = str.substring(colonFlag + 1);
            boolean hasComma = value.endsWith(symbolArr[2]);
            if (hasComma) {
                value = value.substring(0, value.length() - 1);
            }
            //是否是字符串
            boolean isString = value.substring(0, 1).equals(symbolArr[1]) && value.substring(value.length() - 1).equals
                    (symbolArr[1]);
            //是否是布尔变量
            boolean isBoolean = value.equals(symbolArr[3]) || value.equals(symbolArr[4]) || value.equals(symbolArr[5])
                    || value.equals(symbolArr[6]);
            //是否是空
            boolean isNull = value.equals(symbolArr[7]) || value.equals(symbolArr[8]);
            if (isString) {
                StyleConstants.setForeground(attributeSet, colorArr[3]);
            } else if (isBoolean) {
                StyleConstants.setForeground(attributeSet, colorArr[7]);
            } else if (isNull) {
                StyleConstants.setForeground(attributeSet, colorArr[5]);
            } else {
                StyleConstants.setForeground(attributeSet, colorArr[4]);
            }
            document.insertString(document.getLength(), value, attributeSet);

            //添加结尾字符串,如果包含","则添上
            String line = "\n";
            if (hasComma) {
                StyleConstants.setForeground(attributeSet, colorArr[2]);
                line = ",\n";
            }
            document.insertString(document.getLength(), line, attributeSet);
        }

        dataTextPane.setCaretPosition(0);
    }

    /**
     * 设置默认黑色
     *
     * @param rowStr 字符串
     * @throws Exception 异常
     */
    private void setRowJsonDataToDataArea(String rowStr) throws Exception {
        //获取文档风格对象
        StyledDocument document = dataTextPane.getStyledDocument();
        //实例化属性集
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, colorArr[8]);
        dataTextPane.setText(null);
        document.insertString(document.getLength(), rowStr, attributeSet);

        dataTextPane.setCaretPosition(0);
    }
}
