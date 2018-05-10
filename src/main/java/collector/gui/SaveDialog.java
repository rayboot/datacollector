package collector.gui;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @fileName: SaveDialog
 * @author: h1
 * @date: 2018-5-8 10:26:01
 * @dscription:
 */
public class SaveDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField titleField;
    private JTextField pathField;
    private JButton pathBtn;
    private Map<String, String> map;
    private String path;

    SaveDialog(Map<String, String> map) {

        pathField.setText(MainFrame.getSavePath());

        this.map = map;

        setTitle("保存数据");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        pathBtn.addActionListener(e -> path());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void path() {
        JFileChooser fileChooser = new JFileChooser(pathField.getText());
        //设置标题
        fileChooser.setDialogTitle("选择目录");
        //设置只允许选择目录
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //打开弹框
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            path = file.getAbsolutePath();
            pathField.setText(path);
        }
    }

    private void onOK() {
        // add your code here
        String title = titleField.getText();
        path = pathField.getText();
        if (title.isEmpty() || path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "标题或者路径为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        File pathFile = new File(path);
        boolean isPathExist = pathFile.exists();
        if (!isPathExist) {
            JOptionPane.showMessageDialog(this, pathFile + "不存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        MainFrame.setSavePath(path);
        File file = new File(path + "/" + title + ".txt");
        boolean isFileExist = file.exists();
        if (isFileExist) {
            JOptionPane.showMessageDialog(this, file + "已存在", "错误", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        BufferedWriter writer = null;
        boolean isSuccess = false;
        try {
            boolean isCreated = file.createNewFile();
            if (!isCreated) {
                throw new Exception("创建文件失败:");
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                String value = map.get(key);
                String keyValue = key + ":" + value;
                writer.write(keyValue);
                writer.newLine();
            }
            writer.flush();
            //成功标志
            isSuccess = true;
        } catch (Exception e) {
            MainFrame.popErrorInfo(this, e, "写入文件失败");
        } finally {
            try {
                Objects.requireNonNull(writer).close();
            } catch (Exception ignore) {
            }
        }
        JOptionPane.showMessageDialog(this, isSuccess ? "成功保存" : "保存失败");
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
