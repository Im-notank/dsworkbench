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

import de.tor.tribes.types.UserProfile;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.components.VillageOverviewMapPanel;
import de.tor.tribes.ui.components.VillageSelectionPanel;
import de.tor.tribes.ui.models.REFSourceTableModel;
import de.tor.tribes.ui.renderer.DefaultTableHeaderRenderer;
import de.tor.tribes.ui.wiz.ref.types.REFSourceElement;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.PluginManager;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.spi.wizard.*;

/**
 *
 * @author Torridity
 */
public class SupportRefillSourcePanel extends WizardPage {

    private static Translator trans = TranslationManager.getTranslator("ui.wiz.ref.SupportRefillSourcePanel");
    
    private static final String GENERAL_INFO = trans.get("Herkunftsdorf_INFO");
    private static SupportRefillSourcePanel singleton = null;
    private VillageSelectionPanel villageSelectionPanel = null;
    private VillageOverviewMapPanel overviewPanel = null;

    public static synchronized SupportRefillSourcePanel getSingleton() {
        if (singleton == null) {
            singleton = new SupportRefillSourcePanel();
        }
        return singleton;
    }

    /**
     * Creates new form AttackSourcePanel
     */
    SupportRefillSourcePanel() {
        initComponents();
        jVillageTable.setModel(new REFSourceTableModel());
        jXCollapsiblePane1.setLayout(new BorderLayout());
        jXCollapsiblePane1.add(jInfoScrollPane, BorderLayout.CENTER);
        villageSelectionPanel = new VillageSelectionPanel(this::addVillages);

        jVillageTable.setHighlighters(HighlighterFactory.createAlternateStriping(Constants.DS_ROW_A, Constants.DS_ROW_B));
        jVillageTable.getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());

        villageSelectionPanel.enableSelectionElement(VillageSelectionPanel.SELECTION_ELEMENT.ALLY, false);
        villageSelectionPanel.enableSelectionElement(VillageSelectionPanel.SELECTION_ELEMENT.TRIBE, false);
        villageSelectionPanel.setUnitSelectionEnabled(false);
        villageSelectionPanel.setFakeSelectionEnabled(false);
        jDataPanel.add(villageSelectionPanel, BorderLayout.CENTER);
        
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        ActionListener panelListener = (ActionEvent e) -> {
            if (e.getActionCommand().equals("Paste")) {
                pasteFromClipboard();
            } else if (e.getActionCommand().equals("Delete")) {
                deleteSelection();
            }
        };
        jVillageTable.registerKeyboardAction(panelListener, "Paste", paste, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        jVillageTable.registerKeyboardAction(panelListener, "Delete", delete, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        capabilityInfoPanel1.addActionListener(panelListener);

        jVillageTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            int selectedRows = jVillageTable.getSelectedRowCount();
            if (selectedRows != 0) {
                jStatusLabel.setText(selectedRows + trans.get("Dorfgeweahlt"));
            }
        });


        jInfoTextPane.setText(GENERAL_INFO);
        overviewPanel = new VillageOverviewMapPanel();
        jPanel2.add(overviewPanel, BorderLayout.CENTER);
    }

    public static String getDescription() {
        return trans.get("Herkunft");
    }

    public static String getStep() {
        return "id-ref-source";
    }

    public void storeProperties() {
        UserProfile profile = GlobalOptions.getSelectedProfile();
        profile.addProperty("ref.source.expert", villageSelectionPanel.isExpertSelection());
    }

    public void restoreProperties() {
        getModel().clear();
        UserProfile profile = GlobalOptions.getSelectedProfile();
        villageSelectionPanel.setExpertSelection(Boolean.parseBoolean(profile.getProperty("ref.source.expert")));
        villageSelectionPanel.setup();
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
        jDataPanel = new javax.swing.JPanel();
        jVillageTablePanel = new javax.swing.JPanel();
        jTableScrollPane = new javax.swing.JScrollPane();
        jVillageTable = new org.jdesktop.swingx.JXTable();
        jPanel2 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jStatusLabel = new javax.swing.JLabel();
        capabilityInfoPanel1 = new de.tor.tribes.ui.components.CapabilityInfoPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

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

        jDataPanel.setMinimumSize(new java.awt.Dimension(500, 200));
        jDataPanel.setPreferredSize(new java.awt.Dimension(500, 200));
        jDataPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        add(jDataPanel, gridBagConstraints);

        jVillageTablePanel.setMinimumSize(new java.awt.Dimension(500, 232));
        jVillageTablePanel.setPreferredSize(new java.awt.Dimension(500, 232));
        jVillageTablePanel.setLayout(new java.awt.GridBagLayout());

        jTableScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("VerwendeteHerkunftsdoerfer")));
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

        capabilityInfoPanel1.setBbSupport(false);
        capabilityInfoPanel1.setCopyable(false);
        capabilityInfoPanel1.setSearchable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jVillageTablePanel.add(capabilityInfoPanel1, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/support_ignorel.png"))); // NOI18N
        jButton1.setToolTipText(trans.get("Doerferentfernen"));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireRemoveSupportTargetsEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jButton1, gridBagConstraints);

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

    private void fireRemoveSupportTargetsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveSupportTargetsEvent
        int remCount = 0;
        for (Village v : SupportRefillTargetPanel.getSingleton().getAllElements()) {
            if (getModel().removeRow(v, false)) {
                overviewPanel.removeVillage(v);
                remCount++;
            }
        }
        getModel().fireTableDataChanged();
        overviewPanel.repaint();
        jStatusLabel.setText(remCount + trans.get("Dorfentfernt"));
    }//GEN-LAST:event_fireRemoveSupportTargetsEvent

    private REFSourceTableModel getModel() {
        return (REFSourceTableModel) jVillageTable.getModel();
    }

    public void addVillages(Village[] pVillages) {
        REFSourceTableModel model = getModel();
        for (Village v : pVillages) {
            model.addRow(v);
        }
        if (model.getRowCount() > 0) {
            setProblem(null);
        }
        jStatusLabel.setText(pVillages.length + trans.get("Dorfeingefuegt"));
        updateOverview(false);
        update();
    }

    private void pasteFromClipboard() {
        try {
            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
            List<Village> villages = PluginManager.getSingleton().executeVillageParser(data);
            if (!villages.isEmpty()) {
                addVillages(villages.toArray(new Village[villages.size()]));
            }
        } catch (HeadlessException | IOException | UnsupportedFlavorException ignored) {
        }
    }

    private void deleteSelection() {
        int[] selection = jVillageTable.getSelectedRows();
        if (selection.length > 0) {
            List<Integer> rows = new LinkedList<>();
            for (int i : selection) {
                rows.add(jVillageTable.convertRowIndexToModel(i));
            }
            Collections.sort(rows);
            for (int i = rows.size() - 1; i >= 0; i--) {
                getModel().removeRow(rows.get(i));
            }
            jStatusLabel.setText(selection.length + trans.get("Dorfentfernt"));
            updateOverview(true);
            if (getModel().getRowCount() == 0) {
                setProblem(trans.get("KeineDoerfergewaehlt"));
            }
        }
    }

    public void update() {
        int supportTolerance = GlobalOptions.getProperties().getInt("support.tolerance");

        for (REFSourceElement element : getAllElements()) {
            element.updateAvailableSupports(SupportRefillSettingsPanel.getSingleton().getSplit(), supportTolerance);
        }
        getModel().fireTableDataChanged();
    }

    private void updateOverview(boolean pReset) {
        if (pReset) {
            overviewPanel.reset();
        }
        for (REFSourceElement element : getAllElements()) {
            overviewPanel.addVillage(new Point(element.getVillage().getX(), element.getVillage().getY()), Color.yellow);
        }
        overviewPanel.repaint();
    }

    public REFSourceElement[] getAllElements() {
        List<REFSourceElement> result = new LinkedList<>();
        REFSourceTableModel model = getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            result.add(model.getRow(jVillageTable.convertRowIndexToModel(i)));
        }
        return result.toArray(new REFSourceElement[result.size()]);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.components.CapabilityInfoPanel capabilityInfoPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jDataPanel;
    private javax.swing.JScrollPane jInfoScrollPane;
    private javax.swing.JTextPane jInfoTextPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jStatusLabel;
    private javax.swing.JScrollPane jTableScrollPane;
    private javax.swing.JToggleButton jToggleButton1;
    private org.jdesktop.swingx.JXTable jVillageTable;
    private javax.swing.JPanel jVillageTablePanel;
    private org.jdesktop.swingx.JXCollapsiblePane jXCollapsiblePane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public WizardPanelNavResult allowNext(String string, Map map, Wizard wizard) {
        if (getAllElements().length == 0) {
            setProblem(trans.get("KeineDoerfergewaehlt"));
            return WizardPanelNavResult.PROCEED;
        }
        SupportRefillCalculationPanel.getSingleton().updateStatus();
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
