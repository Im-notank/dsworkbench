/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DSWorkbenchFormFrame.java
 *
 * Created on 03.01.2009, 23:50:25
 */
package de.tor.tribes.ui;

import de.tor.tribes.types.AbstractForm;
import de.tor.tribes.util.map.FormManager;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;

/**
 *
 * @author Charon
 */
public class DSWorkbenchFormFrame extends AbstractDSWorkbenchFrame {

    private static DSWorkbenchFormFrame SINGLETON = null;

    public static synchronized DSWorkbenchFormFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchFormFrame();
        }
        return SINGLETON;
    }

    /** Creates new form DSWorkbenchFormFrame */
    DSWorkbenchFormFrame() {
        initComponents();
    }

    public void updateFormList() {
        AbstractForm[] forms = null;
        if (jToggleVisibleOnlyButton.isSelected()) {
            forms = FormManager.getSingleton().getForms().toArray(new AbstractForm[]{});
        } else {
            forms = FormManager.getSingleton().getVisibleForms().toArray(new AbstractForm[]{});
        }
        DefaultListModel model = new DefaultListModel();
        for (AbstractForm f : forms) {
            model.addElement(f);
        }
        jFormsList.setModel(model);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jFormsList = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jToggleVisibleOnlyButton = new javax.swing.JToggleButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        setTitle("Formen");

        jPanel1.setBackground(new java.awt.Color(239, 235, 223));

        jLabel1.setText("Formen");

        jFormsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jFormsList);

        jButton1.setBackground(new java.awt.Color(239, 235, 223));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/remove.gif"))); // NOI18N
        jButton1.setToolTipText("Gewählte Form löschen");
        jButton1.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton1.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton1.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveFormEvent(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(239, 235, 223));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/search.png"))); // NOI18N
        jButton2.setToolTipText("Form anzeigen");
        jButton2.setMaximumSize(new java.awt.Dimension(25, 25));
        jButton2.setMinimumSize(new java.awt.Dimension(25, 25));
        jButton2.setPreferredSize(new java.awt.Dimension(25, 25));
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireShowFormEvent(evt);
            }
        });

        jToggleVisibleOnlyButton.setBackground(new java.awt.Color(239, 235, 223));
        jToggleVisibleOnlyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/eye.png"))); // NOI18N
        jToggleVisibleOnlyButton.setMaximumSize(new java.awt.Dimension(25, 25));
        jToggleVisibleOnlyButton.setMinimumSize(new java.awt.Dimension(25, 25));
        jToggleVisibleOnlyButton.setPreferredSize(new java.awt.Dimension(25, 25));
        jToggleVisibleOnlyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireToggleVisibleOnlyEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(13, 13, 13))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jToggleVisibleOnlyButton, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 162, Short.MAX_VALUE)
                        .addComponent(jToggleVisibleOnlyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap())
        );

        jCheckBox1.setText("Immer im Vordergrund");
        jCheckBox1.setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jCheckBox1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fireRemoveFormEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveFormEvent
        // TODO add your handling code here:
    }//GEN-LAST:event_fireRemoveFormEvent

    private void fireShowFormEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireShowFormEvent
        // TODO add your handling code here:
    }//GEN-LAST:event_fireShowFormEvent

    private void fireToggleVisibleOnlyEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireToggleVisibleOnlyEvent
        if (jToggleVisibleOnlyButton.isSelected()) {
            jToggleVisibleOnlyButton.setIcon(new ImageIcon(this.getClass().getResource("/res/ui/eye_forbidden.png")));
        } else {
            jToggleVisibleOnlyButton.setIcon(new ImageIcon(this.getClass().getResource("/res/ui/eye.png")));
        }
    }//GEN-LAST:event_fireToggleVisibleOnlyEvent
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JList jFormsList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleVisibleOnlyButton;
    // End of variables declaration//GEN-END:variables
}
