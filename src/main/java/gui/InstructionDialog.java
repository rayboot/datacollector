package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @fileName: InstructionDialog
 * @author: h1
 * @date: 2018/4/25 11:25
 * @dscription:
 */
public class InstructionDialog extends JDialog {
    private JPanel contentPane;
    private JButton okBtn;

    InstructionDialog() {
        setContentPane(contentPane);
        setTitle("关于");
        setModal(true);
        JRootPane rootPane = getRootPane();
        rootPane.setDefaultButton(okBtn);

        okBtn.addActionListener(e -> dispose());

        //按ESC退出框体
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0);
        rootPane.registerKeyboardAction(e -> {
            dispose();
        },stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public static void main(String...args){

        InstructionDialog dialog = new InstructionDialog();
        dialog.pack();
        dialog.setVisible(true);
    }
}
