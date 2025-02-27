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
package de.tor.tribes.ui.wiz.tap;

import de.tor.tribes.types.ext.Tribe;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.algo.SettingsChangedListener;
import de.tor.tribes.ui.components.VillageOverviewMapPanel;
import de.tor.tribes.ui.renderer.DefaultTableHeaderRenderer;
import de.tor.tribes.ui.renderer.PercentCellRenderer;
import de.tor.tribes.ui.util.ColorGradientHelper;
import de.tor.tribes.ui.wiz.tap.types.TAPAttackSourceElement;
import de.tor.tribes.ui.wiz.tap.types.TAPAttackTargetElement;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.algo.types.TimeFrame;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPanelNavResult;

/**
 *
 * @author Torridity
 */
public class ValidationPanel extends WizardPage implements SettingsChangedListener {
    
    private static Translator trans = TranslationManager.getTranslator("ui.wiz.tap.ValidationPanel");
    
    Logger logger = LogManager.getLogger("ValidationPanel");
    @Override
    public void fireTimeFrameChangedEvent() {
    }
    private static final String GENERAL_INFO = trans.get("Info_Ergebnis");
    private static ValidationPanel singleton = null;
    private VillageOverviewMapPanel sourceOverviewPanel = null;
    private VillageOverviewMapPanel targetOverviewPanel = null;

    public static synchronized ValidationPanel getSingleton() {
        if (singleton == null) {
            singleton = new ValidationPanel();
        }
        return singleton;
    }

    /**
     * Creates new form AttackSourcePanel
     */
    ValidationPanel() {
        initComponents();
        jXCollapsiblePane1.setLayout(new BorderLayout());
        jXCollapsiblePane1.add(jInfoScrollPane, BorderLayout.CENTER);
        jInfoTextPane.setText(GENERAL_INFO);
        
        sourceOverviewPanel = new VillageOverviewMapPanel();
        jPanel2.add(sourceOverviewPanel, BorderLayout.CENTER);
        targetOverviewPanel = new VillageOverviewMapPanel();
        jPanel3.add(targetOverviewPanel, BorderLayout.CENTER);
        jXTable1.setModel(new VillageUsageTableModel());
        jXTable2.setModel(new VillageUsageTableModel());
        jXTable1.setHighlighters(HighlighterFactory.createAlternateStriping(Constants.DS_ROW_A, Constants.DS_ROW_B));
        jXTable1.getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());
        jXTable2.setHighlighters(HighlighterFactory.createAlternateStriping(Constants.DS_ROW_A, Constants.DS_ROW_B));
        jXTable2.getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());
        jXTable1.setDefaultRenderer(Float.class, new PercentCellRenderer());
        jXTable2.setDefaultRenderer(Float.class, new PercentCellRenderer());
    }

    public static String getDescription() {
        return trans.get("Ueberpruefung");
    }

    public static String getStep() {
        return "id-attack-check";
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
        jSourceValidationPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        jPanel2 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jTargetValidationPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jXTable2 = new org.jdesktop.swingx.JXTable();
        jPanel3 = new javax.swing.JPanel();
        jToggleButton2 = new javax.swing.JToggleButton();

        jInfoScrollPane.setMinimumSize(new java.awt.Dimension(19, 180));
        jInfoScrollPane.setPreferredSize(new java.awt.Dimension(19, 180));

        jInfoTextPane.setContentType("text/html"); // NOI18N
        jInfoTextPane.setEditable(false);
        jInfoTextPane.setText("<html>Du befindest dich im <b>Angriffsmodus</b>. Hier kannst du die Herkunftsd&ouml;rfer ausw&auml;hlen, die f&uuml;r Angriffe verwendet werden d&uuml;rfen. Hierf&uuml;r hast die folgenden M&ouml;glichkeiten:\n<ul>\n<li>Einf&uuml;gen von Dorfkoordinaten aus der Zwischenablage per STRG+V</li>\n<li>Einf&uuml;gen der Herkunftsd&ouml;rfer aus der Gruppen&uuml;bersicht</li>\n<li>Einf&uuml;gen der Herkunftsd&ouml;rfer aus dem SOS-Analyzer</li>\n<li>Einf&uuml;gen der Herkunftsd&ouml;rfer aus Berichten</li>\n<li>Einf&uuml;gen aus der Auswahlübersicht</li>\n<li>Manuelle Eingabe</li>\n</ul>\n</html>\n");
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
        jLabel1.setToolTipText(trans.get("BlendetInfo"));
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

        jSourceValidationPanel.setMinimumSize(new java.awt.Dimension(400, 200));
        jSourceValidationPanel.setPreferredSize(new java.awt.Dimension(400, 200));
        jSourceValidationPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("MoeglicheVerwendungen")));

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jXTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jSourceValidationPanel.add(jScrollPane1, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel2.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jSourceValidationPanel.add(jPanel2, gridBagConstraints);

        jToggleButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/search.png"))); // NOI18N
        jToggleButton1.setToolTipText(trans.get("Informationskartevergroessern"));
        jToggleButton1.setMaximumSize(new java.awt.Dimension(100, 23));
        jToggleButton1.setMinimumSize(new java.awt.Dimension(100, 23));
        jToggleButton1.setPreferredSize(new java.awt.Dimension(100, 23));
        jToggleButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireChangeViewEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jSourceValidationPanel.add(jToggleButton1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        add(jSourceValidationPanel, gridBagConstraints);

        jTargetValidationPanel.setMinimumSize(new java.awt.Dimension(400, 200));
        jTargetValidationPanel.setPreferredSize(new java.awt.Dimension(400, 200));
        jTargetValidationPanel.setLayout(new java.awt.GridBagLayout());

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("MoeglicheZieldoerfer")));

        jXTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jXTable2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jTargetValidationPanel.add(jScrollPane2, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel3.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jTargetValidationPanel.add(jPanel3, gridBagConstraints);

        jToggleButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/search.png"))); // NOI18N
        jToggleButton2.setToolTipText(trans.get("Informationskartevergroessern"));
        jToggleButton2.setMaximumSize(new java.awt.Dimension(100, 23));
        jToggleButton2.setMinimumSize(new java.awt.Dimension(100, 23));
        jToggleButton2.setPreferredSize(new java.awt.Dimension(100, 23));
        jToggleButton2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireChangeViewEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jTargetValidationPanel.add(jToggleButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        add(jTargetValidationPanel, gridBagConstraints);
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

    private void fireChangeViewEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireChangeViewEvent
        if (evt.getSource().equals(jToggleButton1)) {
            if (jToggleButton1.isSelected()) {
                sourceOverviewPanel.setOptimalSize();
                jScrollPane1.setViewportView(sourceOverviewPanel);
                jPanel2.remove(sourceOverviewPanel);
            } else {
                jScrollPane1.setViewportView(jXTable1);
                jPanel2.add(sourceOverviewPanel, BorderLayout.CENTER);
                SwingUtilities.invokeLater(jPanel2::updateUI);
            }
        } else {
            if (jToggleButton2.isSelected()) {
                targetOverviewPanel.setOptimalSize();
                jScrollPane2.setViewportView(targetOverviewPanel);
                jPanel3.remove(targetOverviewPanel);
            } else {
                jScrollPane2.setViewportView(jXTable2);
                jPanel3.add(targetOverviewPanel, BorderLayout.CENTER);
                SwingUtilities.invokeLater(jPanel3::updateUI);
            }
        }
    }//GEN-LAST:event_fireChangeViewEvent

    protected void setup() {
        logger.debug("Setting up Validation Panel");
        sourceOverviewPanel.reset();
        targetOverviewPanel.reset();
        TAPAttackSourceElement[] sourceElements = AttackSourceFilterPanel.getSingleton().getFilteredElements();
        List<TAPAttackTargetElement> targetElements = AttackTargetPanel.getSingleton().getAllElements();

        HashMap<Village, Integer> validSources = new HashMap<>();
        HashMap<Village, Integer> validTargets = new HashMap<>();

        TimeFrame f = TimeSettingsPanel.getSingleton().getTimeFrame();
        logger.debug("Got timeFrame" + f.toString());
        for (TAPAttackSourceElement sourceElement : sourceElements) {
            logger.debug("running through sources");
            for (TAPAttackTargetElement targetElement : targetElements) {
                logger.debug("running through targets");
                long runtime = DSCalculator.calculateMoveTimeInMillis(sourceElement.getVillage(), targetElement.getVillage(), sourceElement.getUnit().getSpeed());
                if (f.isMovementPossible(runtime)) {
                    Integer sourceVal = validSources.get(sourceElement.getVillage());
                    if (sourceVal == null) {
                        sourceVal = 0;
                    }
                    validSources.put(sourceElement.getVillage(), sourceVal + 1);
                    Integer targetVal = validTargets.get(targetElement.getVillage());
                    if (targetVal == null) {
                        targetVal = 0;
                    }
                    validTargets.put(targetElement.getVillage(), targetVal + 1);
                } else {
                    Integer sourceVal = validSources.get(sourceElement.getVillage());
                    if (sourceVal == null) {
                        validSources.put(sourceElement.getVillage(), 0);
                    }
                    Integer targetVal = validTargets.get(targetElement.getVillage());
                    if (targetVal == null) {
                        validTargets.put(targetElement.getVillage(), 0);
                    }
                }
            }
        }

        VillageUsageTableModel model = (VillageUsageTableModel) jXTable1.getModel();
        model.clear();
        for(Entry<Village, Integer> entry: validSources.entrySet()) {
            float possibilities = 100f * (float) entry.getValue() / (float) targetElements.size();
            possibilities = Math.min(possibilities, 100.0f);
            sourceOverviewPanel.addVillage(entry.getKey(), ColorGradientHelper.getGradientColor(possibilities, Color.RED, Color.YELLOW));
            model.addRow(entry.getKey(), possibilities / 100.0f);
        }
        model.fireTableDataChanged();

        model = (VillageUsageTableModel) jXTable2.getModel();
        model.clear();
        for(Entry<Village, Integer> entry: validTargets.entrySet()) {
            float possibilities = 100f * (float) entry.getValue() / (float) sourceElements.length;
            targetOverviewPanel.addVillage(entry.getKey(), ColorGradientHelper.getGradientColor(possibilities, Color.RED, Color.YELLOW));
            model.addRow(entry.getKey(), possibilities / 100.0f);
        }
        model.fireTableDataChanged();
        sourceOverviewPanel.repaint();
        targetOverviewPanel.repaint();

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jInfoScrollPane;
    private javax.swing.JTextPane jInfoTextPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel jSourceValidationPanel;
    private javax.swing.JPanel jTargetValidationPanel;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private org.jdesktop.swingx.JXCollapsiblePane jXCollapsiblePane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private org.jdesktop.swingx.JXTable jXTable2;
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

class VillageUsageTableModel extends AbstractTableModel {

    private Translator trans = TranslationManager.getTranslator("ui.wiz.tap.ValidationPanel");
    
    private String[] columnNames = new String[]{
        trans.get("Spieler"), 
        trans.get("Dorf"), 
        trans.get("Verwendungen")
    };
    private Class[] types = new Class[]{
        Tribe.class, Village.class, Float.class
    };
    private final List<VillageUsageElement> elements = new LinkedList<>();

    public VillageUsageTableModel() {
        super();
    }

    public void clear() {
        elements.clear();
        fireTableDataChanged();
    }

    public void addRow(final Village pVillage, float pUsage) {
        elements.add(new VillageUsageElement(pVillage, pUsage));
    }

    @Override
    public int getRowCount() {
        if (elements == null) {
            return 0;
        }
        return elements.size();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void removeRow(int row) {
        elements.remove(row);
        fireTableDataChanged();
    }

    public VillageUsageElement getRow(int row) {
        return elements.get(row);
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (elements == null || elements.size() - 1 < row) {
            return null;
        }
        VillageUsageElement element = elements.get(row);
        switch (column) {
            case 0:
                return element.getVillage().getTribe();
            case 1:
                return element.getVillage();
            default:
                return element.getUsage();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
}

class VillageUsageElement {

    private Village village = null;
    private float usage = 0f;

    public VillageUsageElement(Village pVillage, float pUsage) {
        village = pVillage;
        usage = pUsage;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TAPAttackSourceElement && ((TAPAttackSourceElement) obj).getVillage().equals(village);
    }

    public Village getVillage() {
        return village;
    }

    public float getUsage() {
        return usage;
    }

    public void setUsage(float usage) {
        this.usage = usage;
    }
}