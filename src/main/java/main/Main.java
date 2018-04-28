package main;

import gui.MainFrame;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;

/**
 * @fileName: Main
 * @author: h1
 * @date: 2018/4/28 15:51
 * @dscription:
 */
public class Main {

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
}
