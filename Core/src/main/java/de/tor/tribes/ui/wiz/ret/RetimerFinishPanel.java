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
package de.tor.tribes.ui.wiz.ret;

import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.UserProfile;
import de.tor.tribes.ui.components.VillageOverviewMapPanel;
import de.tor.tribes.ui.models.RETResultTableModel;
import de.tor.tribes.ui.renderer.*;
import de.tor.tribes.ui.windows.AttackTransferDialog;
import de.tor.tribes.ui.wiz.ret.types.RETSourceElement;
import de.tor.tribes.ui.wiz.tap.*;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.util.*;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPanelNavResult;

/**
 *
 * @author Torridity
 */
public class RetimerFinishPanel extends WizardPage {

    private static Translator trans = TranslationManager.getTranslator("ui.wiz.ret.RetimerFinishPanel");
    
    private static final String GENERAL_INFO = trans.get("Berechnung_INFO");
    private static RetimerFinishPanel singleton = null;
    private VillageOverviewMapPanel overviewPanel = null;

    public static synchronized RetimerFinishPanel getSingleton() {
        if (singleton == null) {
            singleton = new RetimerFinishPanel();
        }
        return singleton;
    }

    public static String getDescription() {
        return trans.get("Fertig");
    }

    public static String getStep() {
        return "id-ret-finish";
    }

    /**
     * Creates new form AttackSourcePanel
     */
    RetimerFinishPanel() {
        initComponents();
        jXCollapsiblePane1.setLayout(new BorderLayout());
        jXCollapsiblePane1.add(jInfoScrollPane, BorderLayout.CENTER);
        jInfoTextPane.setText(GENERAL_INFO);
        jxResultsTable.setModel(new RETResultTableModel());
        jxResultsTable.setHighlighters(HighlighterFactory.createAlternateStriping(Constants.DS_ROW_A, Constants.DS_ROW_B));
        jxResultsTable.getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());
        jxResultsTable.setDefaultRenderer(Date.class, new ColoredDateCellRenderer());
        jxResultsTable.setDefaultRenderer(UnitHolder.class, new UnitCellRenderer());
        overviewPanel = new VillageOverviewMapPanel();
        jPanel5.add(overviewPanel, BorderLayout.CENTER);
    }

    public void storeProperties() {
        UserProfile profile = GlobalOptions.getSelectedProfile();
    }

    public void restoreProperties() {
        getModel().clear();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jInfoScrollPane = new javax.swing.JScrollPane();
        jInfoTextPane = new javax.swing.JTextPane();
        jXCollapsiblePane1 = new org.jdesktop.swingx.JXCollapsiblePane();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jxResultsTable = new org.jdesktop.swingx.JXTable();
        jPanel3 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();

        jInfoScrollPane.setMinimumSize(new java.awt.Dimension(19, 180));
        jInfoScrollPane.setPreferredSize(new java.awt.Dimension(19, 180));

        jInfoTextPane.setEditable(false);
        jInfoTextPane.setContentType("text/html"); // NOI18N
        jInfoTextPane.setText(trans.get("Angriffsmodus_Text"));
        jInfoScrollPane.setViewportView(jInfoTextPane);

        setLayout(new java.awt.GridBagLayout());

        jXCollapsiblePane1.setCollapsed(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jXCollapsiblePane1, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(trans.get("Informationeneinblenden"));
        jLabel1.setToolTipText(trans.get("Datenquellen"));
        jLabel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireHideInfoEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jLabel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("AngegriffeneZiele")));

        jxResultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jxResultsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("AbschliessendeAktionen")));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/48x48/selection_axe_clipboard.png"))); // NOI18N
        jButton4.setToolTipText(trans.get("AbschliessendeAktionen_Info"));
        jButton4.setMaximumSize(new java.awt.Dimension(70, 70));
        jButton4.setMinimumSize(new java.awt.Dimension(70, 70));
        jButton4.setPreferredSize(new java.awt.Dimension(70, 70));
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSelectedToAttackPlanEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 20);
        jPanel3.add(jButton4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jPanel3, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel5.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel5.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        jPanel4.add(jPanel5, gridBagConstraints);

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/search.png"))); // NOI18N
        jToggleButton1.setToolTipText(trans.get("Informationskarte"));
        jToggleButton1.setMaximumSize(new java.awt.Dimension(100, 23));
        jToggleButton1.setMinimumSize(new java.awt.Dimension(100, 23));
        jToggleButton1.setPreferredSize(new java.awt.Dimension(100, 23));
        jToggleButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireViewStateChangeEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(jToggleButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void fireHideInfoEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireHideInfoEvent
        if (jXCollapsiblePane1.isCollapsed()) {
            jXCollapsiblePane1.setCollapsed(false);
            jLabel1.setText(trans.get("Informationenausblenden"));
        } else {
            jXCollapsiblePane1.setCollapsed(true);
            jLabel1.setText(trans.get("Informationeneinblenden"));
        }
    }//GEN-LAST:event_fireHideInfoEvent

    private void fireViewStateChangeEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireViewStateChangeEvent
        if (jToggleButton1.isSelected()) {
            overviewPanel.setOptimalSize(2);
            jScrollPane1.setViewportView(overviewPanel);
            jPanel2.remove(overviewPanel);
        } else {
            jPanel5.add(overviewPanel, BorderLayout.CENTER);
            jScrollPane1.setViewportView(jxResultsTable);
            SwingUtilities.invokeLater(jPanel5::updateUI);
        }
    }//GEN-LAST:event_fireViewStateChangeEvent

    private void fireSelectedToAttackPlanEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireSelectedToAttackPlanEvent
        int[] selection = jxResultsTable.getSelectedRows();
        List<Attack> attacks = new LinkedList<>();
        for (int row : selection) {
            int modelRow = jxResultsTable.convertRowIndexToModel(row);
            Attack a = getModel().getRow(modelRow);
            attacks.add(a);
        }
        transferToAttackView(attacks);
    }//GEN-LAST:event_fireSelectedToAttackPlanEvent

    private void transferToAttackView(List<Attack> pToTransfer) {
        if (pToTransfer.isEmpty()) {
            JOptionPaneHelper.showInformationBox(this, trans.get("KeineAngriffegewaehlt"), trans.get("Information"));
            return;
        }
        new AttackTransferDialog(TacticsPlanerWizard.getFrame(), true).setupAndShow(pToTransfer.toArray(new Attack[pToTransfer.size()]));
    }

    private RETResultTableModel getModel() {
        return (RETResultTableModel) jxResultsTable.getModel();
    }

    public void update() {
        Attack[] results = RetimerCalculationPanel.getSingleton().getResults();
        RETResultTableModel model = getModel();
        model.clear();
        overviewPanel.reset();

        for (RETSourceElement elem : RetimerSourceFilterPanel.getSingleton().getFilteredElements()) {
            overviewPanel.addVillage(new Point(elem.getVillage().getX(), elem.getVillage().getY()), Color.LIGHT_GRAY);
        }

        for (Attack elem : RetimerDataPanel.getSingleton().getAllElements()) {
            overviewPanel.addVillage(new Point(elem.getSource().getX(), elem.getSource().getY()), Color.LIGHT_GRAY);
        }

        for (Attack a : results) {
            overviewPanel.addVillage(a.getTarget(), Color.RED);
            overviewPanel.addVillage(a.getSource(), Color.YELLOW);
            model.addRow(a);
        }
        jxResultsTable.setModel(model);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton4;
    private javax.swing.JScrollPane jInfoScrollPane;
    private javax.swing.JTextPane jInfoTextPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private org.jdesktop.swingx.JXCollapsiblePane jXCollapsiblePane1;
    private org.jdesktop.swingx.JXTable jxResultsTable;
    // End of variables declaration//GEN-END:variables

    @Override
    public WizardPanelNavResult allowNext(String string, Map map, Wizard wizard) {
        return WizardPanelNavResult.PROCEED;
    }

    @Override
    public WizardPanelNavResult allowBack(String string, Map map, Wizard wizard) {
        return WizardPanelNavResult.PROCEED;

    }

    @Override
    public WizardPanelNavResult allowFinish(String string, Map map, Wizard wizard) {
        return WizardPanelNavResult.PROCEED;
    }
}
