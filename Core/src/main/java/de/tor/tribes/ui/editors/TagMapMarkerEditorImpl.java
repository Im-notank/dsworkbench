/* 
 * Copyright 2015 Torridity.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tor.tribes.ui.editors;

import de.tor.tribes.ui.ImageManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import net.java.dev.colorchooser.ColorChooser;

/**
 *
 * @author Torridity
 */
public class TagMapMarkerEditorImpl extends javax.swing.JPanel {

    private ColorChooser c = null;

    /** Creates new form TagMapMarkerEditorImpl */
    public TagMapMarkerEditorImpl() {
        initComponents();
        c = new ColorChooser();
        add(c);

        //setup note symbol box
        jComboBox1.addItem(-1);
        //ICON_SNOB
        for (int i = 0; i <= ImageManager.MAX_NOTE_SYMBOL; i++) {
            jComboBox1.addItem(i);
        }

        final ImageIcon no_tag = new ImageIcon(TagMapMarkerEditorImpl.class.getResource("/res/remove.gif"));

        ListCellRenderer rSymbol = (ListCellRenderer) (JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) -> {
            Component c1 = new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            try {
                JLabel label = (JLabel) c1;
                label.setText("");
                int v = (Integer) value;
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (v != -1) {
                    BufferedImage symbol = ImageManager.getNoteSymbol(v);//ImageManager.getUnitImage(v, false);
                    label.setIcon(new ImageIcon(symbol.getScaledInstance(16, 16, BufferedImage.SCALE_FAST)));
                } else {
                    label.setIcon(no_tag);
                }
            }catch (Exception ignored) {
            }
            return c1;
        };
        jComboBox1.setRenderer(rSymbol);
    }

    public void setColor(Color pColor) {
        if (pColor != null) {
            c.setColor(pColor);
        }
    }

    public Color getColor() {
        return c.getColor();
    }

    public void setIcon(int pIcon) {
        jComboBox1.setSelectedItem(pIcon);
    }

    public int getIcon() {
        return (Integer)jComboBox1.getSelectedItem();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();

        setLayout(new java.awt.GridLayout(1, 2, 1, 0));

        add(jComboBox1);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    // End of variables declaration//GEN-END:variables
}
