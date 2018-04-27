package gui;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import spider.Spider;
import utils.JsonParser;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @fileName: MainFrame
 * @author: h1
 * @date: 2018/4/25 11:25
 * @dscription:
 */
public class MainFrame extends JFrame implements ActionListener {
    private JPanel mainPane;
    private JTextField cookiesAddrField;
    private JTextField cookiesArgField;
    private JTextField cookiesNameField;
    private JPasswordField cookiesPwdField;
    private JButton cookiesBtn;
    private JTextField dataAddrField;
    private JTextField dataArgField;
    private JTextField cookiesField;
    private JTextPane dataArea;
    private JButton dataBtn;
    private JTextArea cookiesRequestHeadArea;
    private JTextArea dataRequestHeadArea;
    private JButton formatBtn;
    private String rowJsonData;
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

    private MainFrame() {

        //设置获取cookies组件
        setCookiesComponent();

        //设置获取data组件
        setDataComponent();

        add(mainPane);
        setTitle("数据采集测试");
        setBounds(33, 50, 800, 650);
        //setBounds(200, 50, 1500, 927);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String... args) {
        try {
            //设置皮肤
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
            UIManager.put("RootPane.setupButtonVisible", false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            new MainFrame();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String[] btnTextArr = {"格式化", "原始数据"};
        //获取动作命令
        String cmdStr = e.getActionCommand();
        //根据动作命令做操作
        switch (cmdStr) {
            case "cookies":
                String cookiesAdd = cookiesAddrField.getText();
                String cookiesArg = cookiesArgField.getText();
                cookiesArg = "".equals(cookiesArg) ? "" : "&" + cookiesArg;
                String userName = cookiesNameField.getText();
                String password = new String(cookiesPwdField.getPassword());
                String cookiesRequestHeadStr = cookiesRequestHeadArea.getText();

                String cookiesUrl = cookiesAdd + "?userName=" + userName + "&password=" + password + cookiesArg;
                String[] backData;
                try {
                    //获取到数据后,放置数据
                    backData = Spider.login(cookiesUrl, cookiesRequestHeadStr);
                    cookiesField.setText(backData[0]);
                    setRowJsonDataToDataArea(backData[1]);
                    rowJsonData = backData[1];
                    formatBtn.setText(btnTextArr[0]);
                } catch (Exception ex) {
                    //出错后弹出提示
                    popErrorInfo(ex, "获取cookies异常");
                }
                break;
            case "data":
                String dataAdd = dataAddrField.getText();
                String dataArg = dataArgField.getText();
                String cookies = cookiesField.getText();
                String dataUrl = "".equals(dataAdd) ? dataAdd : dataAdd + "?" + dataArg;
                String dataRequestHeadStr = dataRequestHeadArea.getText();

                try {
                    rowJsonData = Spider.getData(dataUrl, cookies, dataRequestHeadStr);
                    setRowJsonDataToDataArea(rowJsonData);
                    formatBtn.setText(btnTextArr[0]);
                } catch (Exception ex) {
                    //出错后弹出提示
                    popErrorInfo(ex, "获取数据异常");
                }
                break;
            case "format":
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
                    }catch (Exception ex){
                        popErrorInfo(ex,"重置原始数据失败");
                    }
                }
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
    private void setCookiesComponent() {
        //设置动作命令
        cookiesBtn.setActionCommand("cookies");
        cookiesAddrField.setActionCommand("cookies");
        cookiesArgField.setActionCommand("cookies");
        cookiesNameField.setActionCommand("cookies");
        cookiesPwdField.setActionCommand("cookies");
        //添加响应事件
        cookiesBtn.addActionListener(this);
        cookiesAddrField.addActionListener(this);
        cookiesArgField.addActionListener(this);
        cookiesNameField.addActionListener(this);
        cookiesPwdField.addActionListener(this);
    }

    /**
     * 设置获取数据组件
     */
    private void setDataComponent() {
        //设置动作命令
        dataAddrField.setActionCommand("data");
        dataArgField.setActionCommand("data");
        dataBtn.setActionCommand("data");
        formatBtn.setActionCommand("format");
        //添加响应事件
        dataAddrField.addActionListener(this);
        dataArgField.addActionListener(this);
        dataBtn.addActionListener(this);
        formatBtn.addActionListener(this);
    }

    /**
     * 使用给定字符串插入JTextPane,并根据不同的内容使用不同的颜色
     * @param object 字符串
     * @throws Exception 异常
     */
    private void insertObjectToDataArea(String object) throws Exception{
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
        dataArea.setText(null);
        //获取文档风格对象
        StyledDocument document = dataArea.getStyledDocument();
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

        dataArea.setCaretPosition(0);
    }

    /**
     * 设置默认黑色
     * @param rowStr 字符串
     * @throws Exception 异常
     */
    private void setRowJsonDataToDataArea(String rowStr) throws Exception{
        //获取文档风格对象
        StyledDocument document = dataArea.getStyledDocument();
        //实例化属性集
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, colorArr[8]);
        dataArea.setText(null);
        document.insertString(document.getLength(), rowStr, attributeSet);

        dataArea.setCaretPosition(0);
    }
}
