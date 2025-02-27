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
package de.tor.tribes.ui.wiz.ref;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.TroopAmountFixed;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.php.UnitTableInterface;
import de.tor.tribes.types.UserProfile;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.components.VillageOverviewMapPanel;
import de.tor.tribes.ui.models.REFSettingsTableModel;
import de.tor.tribes.ui.panels.TroopSelectionPanel;
import de.tor.tribes.ui.panels.TroopSelectionPanelFixed;
import de.tor.tribes.ui.renderer.DefaultTableHeaderRenderer;
import de.tor.tribes.ui.util.ColorGradientHelper;
import de.tor.tribes.ui.wiz.ref.types.REFTargetElement;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.TimeManager;
import de.tor.tribes.util.TroopHelper;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.spi.wizard.*;

/**
 *
 * @author Torridity
 */
public class SupportRefillSettingsPanel extends WizardPage implements ActionListener {

    private static Translator trans = TranslationManager.getTranslator("ui.wiz.ref.SupportRefillSettingsPanel");
    
    private static final String GENERAL_INFO = trans.get("Ziel_INFO");
    private static SupportRefillSettingsPanel singleton = null;
    private VillageOverviewMapPanel overviewPanel = null;
    private TroopSelectionPanelFixed targetAmountPanel = null;
    private TroopSelectionPanelFixed splitAmountPanel = null;

    public static synchronized SupportRefillSettingsPanel getSingleton() {
        if (singleton == null) {
            singleton = new SupportRefillSettingsPanel();
        }
        return singleton;
    }

    /**
     * Creates new form AttackSourcePanel
     */
    SupportRefillSettingsPanel() {
        initComponents();
        jVillageTable.setModel(new REFSettingsTableModel());
        jXCollapsiblePane1.setLayout(new BorderLayout());
        jXCollapsiblePane1.add(jInfoScrollPane, BorderLayout.CENTER);
        jVillageTable.setHighlighters(HighlighterFactory.createAlternateStriping(Constants.DS_ROW_A, Constants.DS_ROW_B));
        jVillageTable.getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());

        KeyStroke bbCopy = KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK, false);
        jVillageTable.registerKeyboardAction(SupportRefillSettingsPanel.this, "BBCopy", bbCopy, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        capabilityInfoPanel1.addActionListener(SupportRefillSettingsPanel.this);
        
        jVillageTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            int selectedRows = jVillageTable.getSelectedRowCount();
            if (selectedRows != 0) {
                jStatusLabel.setText(selectedRows + trans.get("Dorfgeweahlt"));
            }
        });

        targetAmountPanel = new TroopSelectionPanelFixed();
        targetAmountPanel.setupDefense(TroopSelectionPanel.alignType.GROUPED, -1);
        jTargetAmountsPanel.add(targetAmountPanel, BorderLayout.CENTER);
        splitAmountPanel = new TroopSelectionPanelFixed();
        splitAmountPanel.setupDefense(TroopSelectionPanel.alignType.GROUPED, -1);
        jSplitSizePanel.add(splitAmountPanel, BorderLayout.CENTER);
        jInfoTextPane.setText(GENERAL_INFO);
        overviewPanel = new VillageOverviewMapPanel();
        jPanel2.add(overviewPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("BBCopy")) {
            copyDefRequests();
        }
    }

    private void copyDefRequests() {
        REFTargetElement[] selection = getSelectedElements();

        if (selection.length == 0) {
            jStatusLabel.setText(trans.get("KeineEintraege"));
            return;
        }
        boolean extended = (JOptionPaneHelper.showQuestionConfirmBox(this, 
                trans.get("ErweiterteBBCodes"), 
                trans.get("ErweiterterBBCode"), 
                trans.get("Nein"), 
                trans.get("Ja")) == JOptionPane.YES_OPTION);

        SimpleDateFormat df = TimeManager.getSimpleDateFormat("dd.MM.yy HH:mm:ss");
        StringBuilder b = new StringBuilder();
        b.append(trans.get("AufgelistetenVergleichbare"));

        TroopAmountFixed split = splitAmountPanel.getAmounts();

        for (REFTargetElement defense : selection) {
            Village target = defense.getVillage();
            int needed = defense.getNeededSupports();
            TroopAmountFixed need = new TroopAmountFixed();
            for (UnitHolder unit: DataHolder.getSingleton().getUnits()) {
                need.setAmountForUnit(unit, needed * split.getAmountForUnit(unit));
            }

            if (extended) {
                b.append("[table]\n");
                b.append("[**]").append(target.toBBCode()).append("[|]");
                b.append("[img]").append(UnitTableInterface.createDefenderUnitTableLink(need)).append("[/img][/**]\n");
                b.append("[/table]\n");
            } else {
                b.append(buildSimpleRequestTable(target, need, defense));
            }
        }
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(b.toString()), null);
            jStatusLabel.setText(trans.get("Unterstuetunganfragen"));
        } catch (HeadlessException hex) {
            jStatusLabel.setText(trans.get("FehlerbeimKopieren"));
        }
    }

    private String buildSimpleRequestTable(Village pTarget, TroopAmountFixed pNeed, REFTargetElement pDefense) {
        StringBuilder b = new StringBuilder();
        b.append("[table]\n");
        b.append("[**]").append(pTarget.toBBCode());
        int colCount = 0;

        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            int value = pNeed.getAmountForUnit(unit);
            if (value > 0) {
                b.append("[|]").append("[unit]").append(unit.getPlainName()).append("[/unit]");
                colCount++;
            }
        }
        b.append("[/**]\n");

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(0);
        b.append("[*]");
        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            int value = pNeed.getAmountForUnit(unit);
            if (value > 0) {
                b.append("[|]").append(nf.format(value));
            }
        }

        for (int i = 0; i < colCount; i++) {
            b.append("[|]");
        }
        b.append("\n");

        for (int i = 0; i < colCount; i++) {
            b.append("[|]");
        }
        b.append("\n");
        b.append("[/table]\n");

        return b.toString();
    }

    public static String getDescription() {
        return trans.get("Einstellungen");
    }

    public static String getStep() {
        return "id-ref-settings";
    }

    public void storeProperties() {
        UserProfile profile = GlobalOptions.getSelectedProfile();
        profile.addProperty("ref.filter.amount", targetAmountPanel.getAmounts().toProperty());
        profile.addProperty("ref.filter.split", splitAmountPanel.getAmounts().toProperty());
        profile.addProperty("ref.allow.similar.amount", jAllowSimilarTroops.isSelected());
    }

    public void restoreProperties() {
        UserProfile profile = GlobalOptions.getSelectedProfile();
        targetAmountPanel.setAmounts(new TroopAmountFixed(0).loadFromProperty(profile.getProperty("ref.filter.amount")));
        splitAmountPanel.setAmounts(new TroopAmountFixed(0).loadFromProperty(profile.getProperty("ref.filter.split")));
        String val = profile.getProperty("ref.allow.similar.amount");
        if (val == null) {
            jAllowSimilarTroops.setSelected(true);
        } else {
            jAllowSimilarTroops.setSelected(Boolean.parseBoolean(val));
        }
    }

    public TroopAmountFixed getSplit() {
        return splitAmountPanel.getAmounts();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jInfoScrollPane = new javax.swing.JScrollPane();
        jInfoTextPane = new javax.swing.JTextPane();
        jXCollapsiblePane1 = new org.jdesktop.swingx.JXCollapsiblePane();
        jLabel1 = new javax.swing.JLabel();
        jDataPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jTargetAmountsPanel = new javax.swing.JPanel();
        jAllowSimilarTroops = new javax.swing.JCheckBox();
        jSplitSizePanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jVillageTablePanel = new javax.swing.JPanel();
        jTableScrollPane = new javax.swing.JScrollPane();
        jVillageTable = new org.jdesktop.swingx.JXTable();
        jPanel2 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jStatusLabel = new javax.swing.JLabel();
        capabilityInfoPanel1 = new de.tor.tribes.ui.components.CapabilityInfoPanel();
        jPanel3 = new javax.swing.JPanel();
        jAddAttackButton = new javax.swing.JButton();
        jRemoveAttackButton = new javax.swing.JButton();

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
        gridBagConstraints.weightx = 1.0;
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
        gridBagConstraints.weightx = 1.0;
        add(jLabel1, gridBagConstraints);

        jDataPanel.setMinimumSize(new java.awt.Dimension(600, 200));
        jDataPanel.setPreferredSize(new java.awt.Dimension(600, 300));
        jDataPanel.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jTargetAmountsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("Truppenstaerke")));
        jTargetAmountsPanel.setLayout(new java.awt.BorderLayout());

        jAllowSimilarTroops.setSelected(true);
        jAllowSimilarTroops.setText(trans.get("Truppenstaerke_Text"));
        jAllowSimilarTroops.setToolTipText(trans.get("Truppenstaerke_Option"));
        jAllowSimilarTroops.setMaximumSize(new java.awt.Dimension(150, 23));
        jAllowSimilarTroops.setMinimumSize(new java.awt.Dimension(150, 23));
        jAllowSimilarTroops.setPreferredSize(new java.awt.Dimension(150, 23));
        jTargetAmountsPanel.add(jAllowSimilarTroops, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jTargetAmountsPanel, gridBagConstraints);

        jSplitSizePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("Einzelunterstuetzung_border")));
        jSplitSizePanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jSplitSizePanel, gridBagConstraints);

        jButton1.setText(trans.get("HTML_Unterstuetzung"));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireCalculateNeededSupportsEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jButton1, gridBagConstraints);

        jButton2.setText(trans.get("HTML_Notwendige"));
        jButton2.setToolTipText(trans.get("HTML_Notwendige_Tooltip"));
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireCalculateAndExportRequiredTroopsEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jDataPanel.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        add(jDataPanel, gridBagConstraints);

        jVillageTablePanel.setPreferredSize(new java.awt.Dimension(400, 232));
        jVillageTablePanel.setLayout(new java.awt.GridBagLayout());

        jTableScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("Beruecksichtigte")));
        jTableScrollPane.setMinimumSize(new java.awt.Dimension(23, 100));
        jTableScrollPane.setPreferredSize(new java.awt.Dimension(23, 100));

        jVillageTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jTableScrollPane.setViewportView(jVillageTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jVillageTablePanel.add(jTableScrollPane, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel2.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 5, 5);
        jVillageTablePanel.add(jPanel2, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jVillageTablePanel.add(jToggleButton1, gridBagConstraints);

        jStatusLabel.setMaximumSize(new java.awt.Dimension(0, 16));
        jStatusLabel.setMinimumSize(new java.awt.Dimension(0, 16));
        jStatusLabel.setPreferredSize(new java.awt.Dimension(0, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jVillageTablePanel.add(jStatusLabel, gridBagConstraints);

        capabilityInfoPanel1.setCopyable(false);
        capabilityInfoPanel1.setDeletable(false);
        capabilityInfoPanel1.setPastable(false);
        capabilityInfoPanel1.setSearchable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jVillageTablePanel.add(capabilityInfoPanel1, gridBagConstraints);

        jAddAttackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/add_attack.png"))); // NOI18N
        jAddAttackButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireChangeSupportCountEvent(evt);
            }
        });

        jRemoveAttackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/remove_attack.png"))); // NOI18N
        jRemoveAttackButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireChangeSupportCountEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jAddAttackButton)
                .addGap(4, 4, 4)
                .addComponent(jRemoveAttackButton)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRemoveAttackButton)
                    .addComponent(jAddAttackButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jVillageTablePanel.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        add(jVillageTablePanel, gridBagConstraints);
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
            overviewPanel.setOptimalSize();
            jTableScrollPane.setViewportView(overviewPanel);
            jPanel2.remove(overviewPanel);
        } else {
            jTableScrollPane.setViewportView(jVillageTable);
            jPanel2.add(overviewPanel, BorderLayout.CENTER);
            SwingUtilities.invokeLater(jPanel2::updateUI);
        }
    }//GEN-LAST:event_fireViewStateChangeEvent

    private void fireCalculateNeededSupportsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCalculateNeededSupportsEvent
        TroopAmountFixed target = targetAmountPanel.getAmounts();
        TroopAmountFixed split = splitAmountPanel.getAmounts();

        if (target.getTroopPopCount() == 0) {
            jStatusLabel.setText(trans.get("KeineTruppenstaerke"));
            return;
        }
        if (split.getTroopPopCount() == 0) {
            jStatusLabel.setText(trans.get("Einzelunterstuetzung"));
            return;
        }

        int max = 0;
        for (int i = 0; i < getModel().getRowCount(); i++) {
            REFTargetElement elem = getModel().getRow(jVillageTable.convertRowIndexToModel(i));
            elem.setNeededSupports(TroopHelper.getNeededSupports(elem.getVillage(), target, split, jAllowSimilarTroops.isSelected()));
            max = Math.max(elem.getNeededSupports(), max);
        }
        getModel().fireTableDataChanged();

        for (REFTargetElement element : getAllElements()) {
            overviewPanel.addVillage(new Point(element.getVillage().getX(), element.getVillage().getY()),
                    ColorGradientHelper.getGradientColor(100.0f * (float) element.getNeededSupports() / (float) max, Color.RED, Color.GREEN));
        }
        overviewPanel.repaint();
        setProblem(null);
    }//GEN-LAST:event_fireCalculateNeededSupportsEvent

    private void fireCalculateAndExportRequiredTroopsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCalculateAndExportRequiredTroopsEvent
        TroopAmountFixed target = targetAmountPanel.getAmounts();
        if (target.getTroopPopCount() == 0) {
            jStatusLabel.setText(trans.get("KeineTruppenstaerke"));
            return;
        }
        StringBuilder b = new StringBuilder();
        b.append(trans.get("AufgelistetenVergleichbare"));

        for (int i = 0; i < getModel().getRowCount(); i++) {
            REFTargetElement elem = getModel().getRow(jVillageTable.convertRowIndexToModel(i));
            Village v = elem.getVillage();
            TroopAmountFixed requiredTroops = TroopHelper.getRequiredTroops(v, target);
            b.append("[table]\n");
            b.append("[**]").append(v.toBBCode()).append("[|]");
            b.append("[img]").append(UnitTableInterface.createDefenderUnitTableLink(requiredTroops)).append("[/img][/**]\n");
            b.append("[/table]\n");
        }

        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(b.toString()), null);
            jStatusLabel.setText(trans.get("Unterstuetunganfragenzungs"));
        } catch (HeadlessException hex) {
            jStatusLabel.setText(trans.get("FehlerbeimKopieren"));
        }

    }//GEN-LAST:event_fireCalculateAndExportRequiredTroopsEvent

    private void fireChangeSupportCountEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireChangeSupportCountEvent
        boolean increase = false;
        if (evt.getSource() == jAddAttackButton) {
            increase = true;
        } else if (evt.getSource() == jRemoveAttackButton) {
            increase = false;
        }

        int[] selection = jVillageTable.getSelectedRows();
        if (selection.length > 0) {
            int modificationCount = 0;
            for (int i : selection) {
                REFTargetElement elem = getModel().getRow(jVillageTable.convertRowIndexToModel(i));
                if (increase) {
                    elem.addSupport();
                    modificationCount++;
                } else {
                    if (elem.removeSupport()) {
                        modificationCount++;
                    }
                }
            }

            jStatusLabel.setText(modificationCount + trans.get("Unterstuetzungen") + ((increase) ? trans.get("hinzugefuegt") : trans.get("entfernt")));
            if (modificationCount > 0) {
                // getModel().fireTableDataChanged();
                jVillageTable.repaint();
            }
        } else {
            jStatusLabel.setText(trans.get("KeinZiel"));
        }
        overviewPanel.repaint();
        setProblem(null);
    }//GEN-LAST:event_fireChangeSupportCountEvent

    private REFSettingsTableModel getModel() {
        return (REFSettingsTableModel) jVillageTable.getModel();
    }

    public void update() {
        REFSettingsTableModel model = getModel();
        model.clear();
        for (Village v : SupportRefillTargetPanel.getSingleton().getAllElements()) {
            model.addRow(v);
        }
        updateOverview(true);
    }

    private void updateOverview(boolean pReset) {
        if (pReset) {
            overviewPanel.reset();
        }
        for (REFTargetElement element : getAllElements()) {
            overviewPanel.addVillage(new Point(element.getVillage().getX(), element.getVillage().getY()), Color.yellow);
        }
        overviewPanel.repaint();
    }

    public REFTargetElement[] getAllElements() {
        List<REFTargetElement> result = new LinkedList<>();
        REFSettingsTableModel model = getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            result.add(model.getRow(jVillageTable.convertRowIndexToModel(i)));
        }
        return result.toArray(new REFTargetElement[result.size()]);
    }

    public REFTargetElement[] getSelectedElements() {
        List<REFTargetElement> result = new LinkedList<>();
        REFSettingsTableModel model = getModel();

        for (int i : jVillageTable.getSelectedRows()) {
            result.add(model.getRow(jVillageTable.convertRowIndexToModel(i)));
        }
        return result.toArray(new REFTargetElement[result.size()]);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.components.CapabilityInfoPanel capabilityInfoPanel1;
    private javax.swing.JButton jAddAttackButton;
    private javax.swing.JCheckBox jAllowSimilarTroops;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jDataPanel;
    private javax.swing.JScrollPane jInfoScrollPane;
    private javax.swing.JTextPane jInfoTextPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton jRemoveAttackButton;
    private javax.swing.JPanel jSplitSizePanel;
    private javax.swing.JLabel jStatusLabel;
    private javax.swing.JScrollPane jTableScrollPane;
    private javax.swing.JPanel jTargetAmountsPanel;
    private javax.swing.JToggleButton jToggleButton1;
    private org.jdesktop.swingx.JXTable jVillageTable;
    private javax.swing.JPanel jVillageTablePanel;
    private org.jdesktop.swingx.JXCollapsiblePane jXCollapsiblePane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public WizardPanelNavResult allowNext(String string, Map map, Wizard wizard) {
        if (getAllElements().length == 0) {
            setProblem(trans.get("KeineDoerfergewaehlt"));
            return WizardPanelNavResult.REMAIN_ON_PAGE;
        }
        int need = 0;
        for (REFTargetElement elem : getAllElements()) {
            need += elem.getNeededSupports();
        }

        if (need == 0) {
            setProblem(trans.get("Keinenotwendigen"));
            return WizardPanelNavResult.REMAIN_ON_PAGE;
        }

        SupportRefillSourcePanel.getSingleton().update();
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
