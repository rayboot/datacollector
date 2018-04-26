package gui;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import spider.Spider;
import utils.JsonParser;

import javax.swing.*;
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
    private JTextArea dataArea;
    private JButton dataBtn;
    private JTextArea cookiesRequestHeadArea;
    private JTextArea dataRequestHeadArea;
    private JButton formatBtn;
    private String rowJsonData;

    private MainFrame() {

        //设置获取cookies组件
        setCookiesComponent();

        //设置获取data组件
        setDataComponent();

        add(mainPane);
        setTitle("数据采集测试");
        setBounds(30, 50, 800, 650);
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
        String[] btnTextArr = {"格式化","原始数据"};
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
                    dataArea.setText(backData[1]);
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
                    dataArea.setText(rowJsonData);
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
                        dataArea.setText(formatData);
                        formatBtn.setText(btnTextArr[1]);
                    } catch (Exception ex) {
                        popErrorInfo(ex, "格式化JSON数据失败");
                    }
                } else if (btnTextArr[1].equals(btnName)){
                    dataArea.setText(rowJsonData);
                    formatBtn.setText(btnTextArr[0]);
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
}
