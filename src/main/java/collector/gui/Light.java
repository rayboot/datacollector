package collector.gui;

import javax.swing.*;
import java.awt.*;

/**
 * @fileName: Light
 * @author: h1
 * @date: 2018/4/28 17:07
 * @dscription:
 */
public class Light extends JPanel {

    private Color color;

    void setColor(Color color) {
        this.color = color;
        updateUI();
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        updateUI();
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillOval(0, 0, 10, 10);
    }
}
