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
package de.tor.tribes.ui.components;

import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;

/**
 *
 * @author Torridity
 */
public class ClickAccountPanel extends javax.swing.JPanel {

    private Translator trans = TranslationManager.getTranslator("ui.compoents.ClickAccountPanel");
    
    private int clickAccount = 0;

    /** Creates new form ClickAccountPanel */
    public ClickAccountPanel() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jClickAccountLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jClickAccountLabel.setBackground(new java.awt.Color(255, 255, 255));
        jClickAccountLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/LeftClick.png"))); // NOI18N
        jClickAccountLabel.setText(String.format(trans.get("Klick_Konto"), 0));
        jClickAccountLabel.setToolTipText(trans.get("KeineKlicks"));
        jClickAccountLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        jClickAccountLabel.setMaximumSize(new java.awt.Dimension(140, 40));
        jClickAccountLabel.setMinimumSize(new java.awt.Dimension(140, 40));
        jClickAccountLabel.setOpaque(true);
        jClickAccountLabel.setPreferredSize(new java.awt.Dimension(140, 40));
        jClickAccountLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireUpdateClickAccountEvent(evt);
            }
        });
        add(jClickAccountLabel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void fireUpdateClickAccountEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireUpdateClickAccountEvent
        clickAccount++;
        updateClickAccount();
    }//GEN-LAST:event_fireUpdateClickAccountEvent

    private void updateClickAccount() {
        jClickAccountLabel.setToolTipText(clickAccount + trans.get("Klickaufgeladen"));
        jClickAccountLabel.setText(String.format(trans.get("Klick_Konto"), clickAccount));
    }

    public boolean useClick() {
        if (clickAccount != 0) {
            clickAccount--;
            updateClickAccount();
            return true;
        } else {
            return false;
        }
    }

    public void giveClickBack() {
        clickAccount++;
        updateClickAccount();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jClickAccountLabel;
    // End of variables declaration//GEN-END:variables
}
