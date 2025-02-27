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
package de.tor.tribes.ui.windows;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.TroopFilterElement;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.renderer.UnitListCellRenderer;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.JOptionPaneHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author Torridity
 */
public class TroopFilterDialog extends javax.swing.JDialog {

    private static Logger logger = LogManager.getLogger("TroopFilter");
    private boolean doFilter = false;
    private HashMap<String, List<TroopFilterElement>> filterSets = new HashMap<>();

    /**
     * Creates new form TroopFilterDialog
     */
    public TroopFilterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        ActionListener listener = (ActionEvent e) -> {
            removeSelectedFilters();
        };
        capabilityInfoPanel3.addActionListener(listener);

        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        jFilterList.registerKeyboardAction(listener, "Delete", delete, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        jXCollapsiblePane1.setLayout(new BorderLayout());
        jXCollapsiblePane1.add(jSettingsPanel, BorderLayout.CENTER);
        reset();
    }

    private void removeSelectedFilters() {
        List selection = jFilterList.getSelectedValuesList();
        if (selection == null || selection.isEmpty()) {
            return;
        }
        List<TroopFilterElement> toRemove = new LinkedList<>();
        for (Object elem : selection) {
            toRemove.add((TroopFilterElement) elem);
        }
        DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();
        for (TroopFilterElement elem : toRemove) {
            filterModel.removeElement(elem);
        }
        jFilterList.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSettingsPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jExistingFilters = new javax.swing.JComboBox();
        jButton3 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jNewFilterName = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jStrictFilter = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jFilterUnitBox = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jMinValue = new javax.swing.JTextField();
        jMaxValue = new javax.swing.JTextField();
        jScrollPane14 = new javax.swing.JScrollPane();
        jFilterList = new javax.swing.JList();
        jXCollapsiblePane1 = new org.jdesktop.swingx.JXCollapsiblePane();
        jButton5 = new javax.swing.JButton();
        capabilityInfoPanel3 = new de.tor.tribes.ui.components.CapabilityInfoPanel();
        jApplyFiltersButton = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();

        jSettingsPanel.setLayout(new java.awt.GridBagLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtereinstellungen laden"));
        jPanel6.setPreferredSize(new java.awt.Dimension(247, 154));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Gespeicherte Filter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jExistingFilters, gridBagConstraints);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/export1.png"))); // NOI18N
        jButton3.setText("Laden");
        jButton3.setMaximumSize(new java.awt.Dimension(100, 23));
        jButton3.setMinimumSize(new java.awt.Dimension(100, 23));
        jButton3.setPreferredSize(new java.awt.Dimension(100, 23));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireLoadFilterSettingsEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jButton3, gridBagConstraints);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/red_x.png"))); // NOI18N
        jButton6.setText("Löschen");
        jButton6.setMaximumSize(new java.awt.Dimension(100, 23));
        jButton6.setMinimumSize(new java.awt.Dimension(100, 23));
        jButton6.setPreferredSize(new java.awt.Dimension(100, 23));
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireRemoveFilterEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jButton6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jSettingsPanel.add(jPanel6, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtereinstellungen speichern"));
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Name der Einstellung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jNewFilterName, gridBagConstraints);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/import1.png"))); // NOI18N
        jButton4.setText("Speichern");
        jButton4.setMaximumSize(new java.awt.Dimension(100, 23));
        jButton4.setMinimumSize(new java.awt.Dimension(100, 23));
        jButton4.setPreferredSize(new java.awt.Dimension(100, 23));
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireSaveFilterSettingsEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jButton4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jSettingsPanel.add(jPanel8, gridBagConstraints);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Sonstige Einstellungen"));
        jPanel9.setLayout(new java.awt.GridBagLayout());

        jStrictFilter.setSelected(true);
        jStrictFilter.setText("Strenge Filterung");
        jStrictFilter.setToolTipText("<html>Alle Filterbedingungen müssen erf&uuml;llt sein, damit ein Dorf zugelassen wird.<br/>\nIst dieses Feld deaktiviert reicht mindestens eine Bedingung.</html>");
        jStrictFilter.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jStrictFilter.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jStrictFilter.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(jStrictFilter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jSettingsPanel.add(jPanel9, gridBagConstraints);

        setTitle("Truppenfilter");
        setMinimumSize(new java.awt.Dimension(219, 400));
        setModal(true);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel4.setMinimumSize(new java.awt.Dimension(220, 200));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(239, 235, 223));
        jPanel1.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Neuer Filter"));
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jFilterUnitBox.setMaximumSize(new java.awt.Dimension(51, 25));
        jFilterUnitBox.setMinimumSize(new java.awt.Dimension(51, 25));
        jFilterUnitBox.setPreferredSize(new java.awt.Dimension(51, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jFilterUnitBox, gridBagConstraints);

        jLabel25.setText("Einheit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel25, gridBagConstraints);

        jLabel26.setText("Min");
        jLabel26.setMaximumSize(new java.awt.Dimension(20, 25));
        jLabel26.setMinimumSize(new java.awt.Dimension(20, 25));
        jLabel26.setPreferredSize(new java.awt.Dimension(20, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel26, gridBagConstraints);

        jLabel27.setText("Max");
        jLabel27.setMaximumSize(new java.awt.Dimension(20, 25));
        jLabel27.setMinimumSize(new java.awt.Dimension(20, 25));
        jLabel27.setPreferredSize(new java.awt.Dimension(20, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanel3.add(jLabel27, gridBagConstraints);

        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton17.setText("Hinzufügen");
        jButton17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddTroopFilterEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jButton17, gridBagConstraints);

        jMinValue.setMaximumSize(new java.awt.Dimension(51, 25));
        jMinValue.setMinimumSize(new java.awt.Dimension(51, 25));
        jMinValue.setPreferredSize(new java.awt.Dimension(51, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jMinValue, gridBagConstraints);

        jMaxValue.setMaximumSize(new java.awt.Dimension(51, 25));
        jMaxValue.setMinimumSize(new java.awt.Dimension(51, 25));
        jMaxValue.setPreferredSize(new java.awt.Dimension(51, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jMaxValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel3, gridBagConstraints);

        jScrollPane14.setBorder(javax.swing.BorderFactory.createTitledBorder("Verwendete Filter"));

        jScrollPane14.setViewportView(jFilterList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jScrollPane14, gridBagConstraints);

        jXCollapsiblePane1.setCollapsed(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jXCollapsiblePane1, gridBagConstraints);

        jButton5.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jButton5.setText("Einstellungen");
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireShowHideSettingsEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jButton5, gridBagConstraints);

        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel4.add(jPanel2, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel4, gridBagConstraints);

        capabilityInfoPanel3.setBbSupport(false);
        capabilityInfoPanel3.setCopyable(false);
        capabilityInfoPanel3.setPastable(false);
        capabilityInfoPanel3.setSearchable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        getContentPane().add(capabilityInfoPanel3, gridBagConstraints);

        jApplyFiltersButton.setText("Anwenden");
        jApplyFiltersButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireApplyTroopFiltersEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        getContentPane().add(jApplyFiltersButton, gridBagConstraints);

        jButton20.setText("Abbrechen");
        jButton20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireApplyTroopFiltersEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        getContentPane().add(jButton20, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fireAddTroopFilterEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddTroopFilterEvent
        UnitHolder unit = (UnitHolder) jFilterUnitBox.getSelectedItem();
        DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();
        int min = Integer.MIN_VALUE;
        int max = Integer.MAX_VALUE;
        try {
            min = Integer.parseInt(jMinValue.getText());
        } catch (Exception e) {
            min = Integer.MIN_VALUE;
        }
        try {
            max = Integer.parseInt(jMaxValue.getText());
        } catch (Exception e) {
            max = Integer.MIN_VALUE;
        }
        if (min > max && max > 0) {
            int tmp = min;
            min = max;
            max = tmp;
            jMinValue.setText("" + min);
            jMaxValue.setText("" + max);
        }

        if (min < 0 && max < 0) {
            jMinValue.setBackground(Color.RED);
            jMaxValue.setBackground(Color.RED);
            return;
        } else {
            jMinValue.setBackground(Color.WHITE);
            jMaxValue.setBackground(Color.WHITE);
        }

        for (int i = 0; i < filterModel.size(); i++) {
            TroopFilterElement listElem = (TroopFilterElement) filterModel.get(i);
            if (listElem.getUnit().equals(unit)) {
                //update min and max and return
                listElem.setMin(min);
                listElem.setMax(max);
                jFilterList.repaint();
                return;
            }
        }
        //no elem update --> add new elem
        filterModel.addElement(new TroopFilterElement(unit, min, max));

}//GEN-LAST:event_fireAddTroopFilterEvent

    private void fireApplyTroopFiltersEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireApplyTroopFiltersEvent
        if (evt.getSource() == jApplyFiltersButton) {
            doFilter = true;
        }
        setVisible(false);
}//GEN-LAST:event_fireApplyTroopFiltersEvent

    private void fireLoadFilterSettingsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireLoadFilterSettingsEvent
        String selection = (String) jExistingFilters.getSelectedItem();

        if (selection != null) {
            List<TroopFilterElement> elems = filterSets.get(selection);
            DefaultListModel model = new DefaultListModel();
            for (TroopFilterElement elem : elems) {
                model.addElement(new TroopFilterElement(elem.getUnit(), elem.getMin(), elem.getMax()));
            }
            jFilterList.setModel(model);
        }
    }//GEN-LAST:event_fireLoadFilterSettingsEvent

    private void fireSaveFilterSettingsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireSaveFilterSettingsEvent
        String setName = jNewFilterName.getText();
        DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();

        if (setName == null || setName.length() == 0) {
            JOptionPaneHelper.showInformationBox(this, "Bitte einen Namen für das neue Filterset angeben", "Information");
            return;
        }

        if (filterModel.getSize() == 0) {
            JOptionPaneHelper.showInformationBox(this, "Ein Filterset muss mindestens einen Eintrag enthalten", "Information");
            return;
        }

        if (filterSets.get(setName) != null) {
            if (JOptionPaneHelper.showQuestionConfirmBox(this, "Das Filterset '" + setName + "' existiert bereits.\nMöchtest du es überschreiben?", "Bestätigung", "Nein", "Ja") != JOptionPane.OK_OPTION) {
                return;
            }
        }

        StringBuilder b = new StringBuilder();
        b.append(setName).append(",");
        List<TroopFilterElement> elements = new LinkedList<>();
        for (int j = 0; j < filterModel.size(); j++) {
            TroopFilterElement elem = (TroopFilterElement) filterModel.get(j);
            elements.add(new TroopFilterElement(elem.getUnit(), elem.getMin(), elem.getMax()));
        }

        filterSets.put(setName, elements);
        updateFilterSetList();
        saveFilterSets();
    }//GEN-LAST:event_fireSaveFilterSettingsEvent

    private void fireShowHideSettingsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireShowHideSettingsEvent
        jXCollapsiblePane1.setCollapsed(!jXCollapsiblePane1.isCollapsed());
    }//GEN-LAST:event_fireShowHideSettingsEvent

    private void fireRemoveFilterEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveFilterEvent
        removeFilterSet();
    }//GEN-LAST:event_fireRemoveFilterEvent

    private void removeFilterSet() {
        String filter = (String) jExistingFilters.getSelectedItem();
        if (filter == null) {
            return;
        }
        if (JOptionPaneHelper.showQuestionConfirmBox(this, "Filter '" + filter + "' wirklich löschen?", "Filter löschen", "Nein", "Ja") == JOptionPane.YES_OPTION) {
            filterSets.remove(filter);
            saveFilterSets();
            updateFilterSetList();
        }
    }

    private void saveFilterSets() {
        String profileDir = GlobalOptions.getSelectedProfile().getProfileDirectory();
        File filterFile = new File(profileDir + "/filters.sav");

        StringBuilder b = new StringBuilder();
        for(String key: filterSets.keySet()) {
            b.append(key).append(",");
            List<TroopFilterElement> elements = filterSets.get(key);
            for (int i = 0; i < elements.size(); i++) {
                TroopFilterElement elem = elements.get(i);
                b.append(elem.getUnit().getPlainName()).append("/").append(elem.getMin()).append("/").append(elem.getMax());
                if (i < elements.size() - 1) {
                    b.append(",");
                }
            }
            b.append("\n");
        }

        FileWriter w = null;
        try {
            w = new FileWriter(filterFile);
            w.write(b.toString());
            w.flush();
        } catch (Exception e) {
            logger.error("Failed to write troop filters", e);
        } finally {
            try {
                w.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void loadFilterSets() {
        filterSets.clear();
        String profileDir = GlobalOptions.getSelectedProfile().getProfileDirectory();
        File filterFile = new File(profileDir + "/filters.sav");
        if (!filterFile.exists()) {
            return;
        }

        BufferedReader r = null;

        try {
            r = new BufferedReader(new FileReader(filterFile));
            String line = "";
            while ((line = r.readLine()) != null) {
                String[] split = line.split(",");
                String name = split[0];
                List<TroopFilterElement> elements = new LinkedList<>();
                for (int i = 1; i < split.length; i++) {
                    String[] elemSplit = split[i].split("/");
                    TroopFilterElement elem = new TroopFilterElement(DataHolder.getSingleton().getUnitByPlainName(elemSplit[0]), Integer.parseInt(elemSplit[1]), Integer.parseInt(elemSplit[2]));
                    elements.add(elem);
                }
                filterSets.put(name, elements);
            }
        } catch (Exception e) {
            logger.error("Failed to read troop filters", e);
        } finally {
            try {
                r.close();
            } catch (Exception ignored) {
            }
        }

        updateFilterSetList();
    }

    private void updateFilterSetList() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();

        for(String key: filterSets.keySet()) {
            model.addElement(key);
        }

        jExistingFilters.setModel(model);
    }

    public void reset() {
        jFilterList.setModel(new DefaultListModel());
    }

    public boolean showDialog() {
        jFilterUnitBox.setModel(new DefaultComboBoxModel(DataHolder.getSingleton().getUnits().toArray(new UnitHolder[]{})));
        jFilterUnitBox.setRenderer(new UnitListCellRenderer());
        //load filter sets
        loadFilterSets();
        doFilter = false;
        pack();
        setVisible(true);
        if (((DefaultListModel) jFilterList.getModel()).isEmpty()) {//do not filter with empty list
            doFilter = false;
        }
        return doFilter;
    }

    public void show(List<Village> pVillageToFilter) {
        if (showDialog()) {
            filter(pVillageToFilter);
        }
    }

    public void filter(List<Village> pVillageToFilter) {
        //update list if filter is enabled
        DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();

        for (Village v : pVillageToFilter.toArray(new Village[pVillageToFilter.size()])) {
            boolean villageAllowed = false;
            //go through all rows in attack table and get source village
            for (int j = 0; j < filterModel.size(); j++) {
                //check for all filters if villag is allowed
                if (!((TroopFilterElement) filterModel.get(j)).allowsVillage(v)) {
                    if (jStrictFilter.isSelected()) {
                        //village is not allowed, add to remove list if strict filtering is enabled
                        pVillageToFilter.remove(v);
                    }
                } else {
                    villageAllowed = true;
                    if (!jStrictFilter.isSelected()) {
                        break;
                    }
                }
            }
            if (!jStrictFilter.isSelected()) {
                //if strict filtering is disabled remove village only if it is not allowed
                if (!villageAllowed) {
                    pVillageToFilter.remove(v);
                }
            }
        }
    }

    public boolean canFilter() {
        return doFilter && !((DefaultListModel) jFilterList.getModel()).isEmpty();
    }

    public Village[] getIgnoredVillages(Village[] pVillageToFilter) {
        List<Village> ignored = new LinkedList<>();
        //update list if filter is enabled
        DefaultListModel filterModel = (DefaultListModel) jFilterList.getModel();

        for (Village v : pVillageToFilter) {
            boolean villageAllowed = false;
            //go through all rows in attack table and get source village
            for (int j = 0; j < filterModel.size(); j++) {
                //check for all filters if villag is allowed
                if (!((TroopFilterElement) filterModel.get(j)).allowsVillage(v)) {
                    if (jStrictFilter.isSelected()) {
                        //village is not allowed, add to remove list if strict filtering is enabled
                        ignored.add(v);
                    }
                } else {
                    villageAllowed = true;
                    if (!jStrictFilter.isSelected()) {
                        break;
                    }
                }
            }
            if (!jStrictFilter.isSelected()) {
                //if strict filtering is disabled remove village only if it is not allowed
                if (!villageAllowed) {
                    ignored.add(v);
                }
            }
        }
        return ignored.toArray(new Village[ignored.size()]);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.components.CapabilityInfoPanel capabilityInfoPanel3;
    private javax.swing.JButton jApplyFiltersButton;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox jExistingFilters;
    private javax.swing.JList jFilterList;
    private javax.swing.JComboBox jFilterUnitBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JTextField jMaxValue;
    private javax.swing.JTextField jMinValue;
    private javax.swing.JTextField jNewFilterName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JPanel jSettingsPanel;
    private javax.swing.JCheckBox jStrictFilter;
    private org.jdesktop.swingx.JXCollapsiblePane jXCollapsiblePane1;
    // End of variables declaration//GEN-END:variables
}
