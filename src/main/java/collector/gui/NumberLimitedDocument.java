package collector.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

/**
 * @fileName: NumberLimitedDocument
 * @author: h1
 * @date: 2018/5/3 11:23
 * @dscription:
 */
public class NumberLimitedDocument extends PlainDocument {

    @Override
    public void insertString(int offset, String str, AttributeSet attr)
            throws javax.swing.text.BadLocationException {
        if (str == null) {
            return;
        }

        char[] s = str.toCharArray();
        int length = 0;
        // 过滤非数字
        for (int i = 0; i < s.length; i++) {
            if ((s[i] >= '0') && (s[i] <= '9')) {
                s[length++] = s[i];
            }
            // 插入内容
            super.insertString(offset, new String(s, 0, length), attr);
        }
    }
}
