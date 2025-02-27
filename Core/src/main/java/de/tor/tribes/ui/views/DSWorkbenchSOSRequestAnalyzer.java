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
package de.tor.tribes.ui.views;

import de.tor.tribes.control.ManageableType;
import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.TroopAmountFixed;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.php.UnitTableInterface;
import de.tor.tribes.types.*;
import de.tor.tribes.types.ext.Tribe;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.components.ProfileQuickChangePanel;
import de.tor.tribes.ui.models.DefenseToolModel;
import de.tor.tribes.ui.models.SupportsModel;
import de.tor.tribes.ui.panels.GenericTestPanel;
import de.tor.tribes.ui.renderer.*;
import de.tor.tribes.ui.windows.AbstractDSWorkbenchFrame;
import de.tor.tribes.ui.windows.VillageSupportFrame;
import de.tor.tribes.ui.wiz.dep.DefenseAnalysePanel;
import de.tor.tribes.ui.wiz.ret.RetimerDataPanel;
import de.tor.tribes.ui.wiz.tap.TacticsPlanerWizard;
import de.tor.tribes.util.*;
import de.tor.tribes.util.bb.SosListFormatter;
import de.tor.tribes.util.sos.SOSManager;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.table.TableColumnExt;

/**
 *
 * @author Torridity
 * @author extremeCrazyCoder
 */
public class DSWorkbenchSOSRequestAnalyzer extends AbstractDSWorkbenchFrame implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("BBCopy")) {
            copySelectionToClipboardAsBBCode();
        } else if (e.getActionCommand().equals("Delete")) {
            removeSelection();
        }
    }
    
    private static Translator trans = TranslationManager.getTranslator("ui.views.DSWorkbenchSOSRequestAnalyzer");
    
    private static Logger logger = LogManager.getLogger("SOSRequestAnalyzer");
    private static DSWorkbenchSOSRequestAnalyzer SINGLETON = null;
    private GenericTestPanel centerPanel = null;
    private DefenseAnalyzer a = null;
    private ProfileQuickChangePanel profileQuickchangePanel = null;

    public static synchronized DSWorkbenchSOSRequestAnalyzer getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchSOSRequestAnalyzer();
        }
        return SINGLETON;
    }

    @Override
    public void resetView() {
        updateView();
        updateSupportTable();
    }

    /**
     * Creates new form DSWorkbenchSOSRequestAnalyzer
     */
    DSWorkbenchSOSRequestAnalyzer() {
        initComponents();
        centerPanel = new GenericTestPanel(true);
        jSOSPanel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setChildComponent(jSOSInputPanel);
        buildMenu();
        jButton1.setIcon(new ImageIcon("./graphics/big/find.png"));
        capabilityInfoPanel1.addActionListener(DSWorkbenchSOSRequestAnalyzer.this);
        //  KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
        KeyStroke bbCopy = KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK, false);
        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        //KeyStroke cut = KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK, false);
        // jAttacksTable.registerKeyboardAction(DSWorkbenchSOSRequestAnalyzer.this, "Copy", copy, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        jAttacksTable.registerKeyboardAction(DSWorkbenchSOSRequestAnalyzer.this, "BBCopy", bbCopy, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        jAttacksTable.registerKeyboardAction(DSWorkbenchSOSRequestAnalyzer.this, "Delete", delete, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        // jAttacksTable.registerKeyboardAction(DSWorkbenchSOSRequestAnalyzer.this, "Cut", cut, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        jAttacksTable.getActionMap().put("find", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //ignore find
            }
        });

        jAttacksTable.setModel(new DefenseToolModel());
        jAttacksTable.setHighlighters(HighlighterFactory.createAlternateStriping(Constants.DS_ROW_A, Constants.DS_ROW_B));
        jAttacksTable.getColumnExt(trans.getRaw("ui.models.DefenseToolModel.Tendenz")).setCellRenderer(new TendencyTableCellRenderer());
        jAttacksTable.getColumnExt(trans.getRaw("ui.models.DefenseToolModel.Status")).setCellRenderer(new DefenseStatusTableCellRenderer());
        //jAttacksTable.getColumnExt("Wall").setCellRenderer(new WallLevellCellRenderer());
        jAttacksTable.getColumnExt(trans.getRaw("ui.models.DefenseToolModel.Verlustrate")).setCellRenderer(new LossRatioTableCellRenderer());
        jAttacksTable.setDefaultRenderer(Date.class, new DateCellRenderer());
        jAttacksTable.setColumnControlVisible(false);
        jAttacksTable.setDefaultRenderer(Date.class, new DateCellRenderer());
        jAttacksTable.getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());
        jAttacksTable.requestFocus();

        jSupportsTable.setModel(new SupportsModel());
        jSupportsTable.setHighlighters(HighlighterFactory.createAlternateStriping(Constants.DS_ROW_A, Constants.DS_ROW_B));
        jSupportsTable.getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());
        jSupportsTable.setDefaultRenderer(Date.class, new ColoredDateCellRenderer());
        jSupportsTable.setDefaultRenderer(Boolean.class, new CustomBooleanRenderer(CustomBooleanRenderer.LayoutStyle.SENT_NOTSENT));

        new SupportCountdownThread().start();
        new SupportColorUpdateThread().start();
        jXInfoLabel.setLineWrap(true);

        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        if (!Constants.DEBUG) {
            GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.sos_analyzer", GlobalOptions.getHelpBroker().getHelpSet());
        }
        // </editor-fold>
    }

    @Override
    public void toBack() {
        jAlwaysOnTopBox.setSelected(false);
        fireAlwaysOnTopEvent(null);
        super.toBack();
    }

    @Override
    public void storeCustomProperties(Configuration pConfig) {
        pConfig.setProperty(getPropertyPrefix() + ".menu.visible", centerPanel.isMenuVisible());
        pConfig.setProperty(getPropertyPrefix() + ".alwaysOnTop", jAlwaysOnTopBox.isSelected());
        PropertyHelper.storeTableProperties(jAttacksTable, pConfig, getPropertyPrefix());
    }

    @Override
    public void restoreCustomProperties(Configuration pConfig) {
        centerPanel.setMenuVisible(pConfig.getBoolean(getPropertyPrefix() + ".menu.visible", true));

        try {
            jAlwaysOnTopBox.setSelected(pConfig.getBoolean(getPropertyPrefix() + ".alwaysOnTop"));
        } catch (Exception ignored) {
        }

        setAlwaysOnTop(jAlwaysOnTopBox.isSelected());
        PropertyHelper.restoreTableProperties(jAttacksTable, pConfig, getPropertyPrefix());
    }

    @Override
    public String getPropertyPrefix() {
        return "sos.view";
    }

    private void buildMenu() {
        JXTaskPane viewPane = new JXTaskPane();
        viewPane.setTitle(trans.get("Ansicht"));
        JXButton toSosView = new JXButton(new ImageIcon(DSWorkbenchTagFrame.class.getResource("/res/ui/axe24.png")));
        toSosView.setToolTipText(trans.get("EingeleseneSOS"));
        toSosView.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                jScrollPane6.setViewportView(jAttacksTable);
            }
        });
        viewPane.getContentPane().add(toSosView);

        JXButton toSupportView = new JXButton(new ImageIcon(DSWorkbenchTagFrame.class.getResource("/res/ui/sword24.png")));
        toSupportView.setToolTipText(trans.get("ErrechneteUnterstuetzungen"));
        toSupportView.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                jScrollPane6.setViewportView(jSupportsTable);
            }
        });

        viewPane.getContentPane().add(toSupportView);

        JXTaskPane transferPane = new JXTaskPane();
        transferPane.setTitle(trans.get("Uebertragen"));
        JXButton toSupport = new JXButton(new ImageIcon(DSWorkbenchTagFrame.class.getResource("/res/ui/support_tool.png")));
        toSupport.setToolTipText(trans.get("UnterstuetzungWerkzeug"));
        toSupport.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                transferToSupportTool();
            }
        });

        transferPane.getContentPane().add(toSupport);

        JXButton toRetime = new JXButton(new ImageIcon(DSWorkbenchTagFrame.class.getResource("/res/ui/re-time.png")));
        toRetime.setToolTipText(trans.get("Retimer"));
        toRetime.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                transferToRetimeTool();
            }
        });

        transferPane.getContentPane().add(toRetime);

        JXButton toBrowser = new JXButton(new ImageIcon(DSWorkbenchTagFrame.class.getResource("/res/ui/att_browser.png")));
        toBrowser.setToolTipText(trans.get("Browser"));
        toBrowser.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                transferToBrowser();
            }
        });

        transferPane.getContentPane().add(toBrowser);

        transferPane.getContentPane().add(new JXButton(new AbstractAction(null, new ImageIcon(DSWorkbenchTagFrame.class.getResource("/res/ui/sos_clipboard.png"))) {

            @Override
            public Object getValue(String key) {
                if (key.equals(Action.SHORT_DESCRIPTION)) {
                    return trans.get("UnterstuetzungText");
                }
                return super.getValue(key);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                copyDefRequests();
            }
        }));


        JXTaskPane miscPane = new JXTaskPane();
        miscPane.setTitle(trans.get("Sonstiges"));
        JXButton reAnalyzeButton = new JXButton(new ImageIcon(DSWorkbenchTagFrame.class.getResource("/res/ui/reanalyze.png")));
        reAnalyzeButton.setToolTipText(trans.get("Analysiert"));
        reAnalyzeButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                analyzeData(true);
            }
        });
        miscPane.getContentPane().add(reAnalyzeButton);
        JXButton setSelectionSecured = new JXButton(new ImageIcon(DSWorkbenchTagFrame.class.getResource("/res/ui/save.png")));
        setSelectionSecured.setToolTipText(trans.get("Manuell_Entrys"));
        setSelectionSecured.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                setSelectionSecured();
            }
        });
        miscPane.getContentPane().add(setSelectionSecured);
        profileQuickchangePanel = new ProfileQuickChangePanel();
        centerPanel.setupTaskPane(profileQuickchangePanel, viewPane, transferPane, miscPane);
    }

    public boolean sendDataToDefensePlaner() {
        List<DefenseInformation> infos = new LinkedList<>();
        if (getModel().getRowCount() == 0) {
            return false;
        } else {
            for (int row = 0; row < jAttacksTable.getRowCount(); row++) {
                infos.add(getModel().getRows()[jAttacksTable.convertRowIndexToModel(row)]);
            }
        }
        DefenseAnalysePanel.getSingleton().setData(infos);
        return true;
    }

    public DefenseToolModel getModel() {
        return TableHelper.getTableModel(jAttacksTable);
    }

    private void showInfo(String pMessage) {
        infoPanel.setCollapsed(false);
        jXInfoLabel.setBackgroundPainter(new MattePainter(getBackground()));
        jXInfoLabel.setForeground(Color.BLACK);
        jXInfoLabel.setText(pMessage);
    }

    private void showSuccess(String pMessage) {
        infoPanel.setCollapsed(false);
        jXInfoLabel.setBackgroundPainter(new MattePainter(Color.GREEN));
        jXInfoLabel.setForeground(Color.BLACK);
        jXInfoLabel.setText(pMessage);
    }

    private void showError(String pMessage) {
        infoPanel.setCollapsed(false);
        jXInfoLabel.setBackgroundPainter(new MattePainter(Color.RED));
        jXInfoLabel.setForeground(Color.WHITE);
        jXInfoLabel.setText(pMessage);
    }

    private void transferToSupportTool() {
        List<DefenseInformation> attacks = getSelectedRows();
        if (attacks.isEmpty()) {
            return;
        }
        VillageSupportFrame.getSingleton().showSupportFrame(attacks.get(0).getTarget(), attacks.get(0).getFirstAttack().getTime());
    }

    private void transferToBrowser() {
        if (!jScrollPane6.getViewport().getView().equals(jSupportsTable)) {
            showInfo(trans.get("UnterstuetzungFunktion"));
            return;
        }
        int[] rows = jSupportsTable.getSelectedRows();
        if (rows == null || rows.length == 0) {
            showInfo(trans.get("KeineUnterstuezung"));
            return;
        }
        SupportsModel model = TableHelper.getTableModel(jSupportsTable);
        for (int i : rows) {
            int row = jSupportsTable.convertRowIndexToModel(i);
            Defense defense = model.getRows()[row];
            if (!defense.isTransferredToBrowser()) {
                Village source = defense.getSupporter();
                Village target = defense.getTarget();
                TroopAmountFixed units = DSWorkbenchSettingsDialog.getSingleton().getDefense();
                if (units == null || !units.hasUnits()) {
                    showError(trans.get("FehlerhafteEinstellungen"));
                    break;
                }
                if (!BrowserInterface.sendTroops(source, target, units, profileQuickchangePanel.getSelectedProfile())) {
                    showError(trans.get("Browsers"));
                    break;
                } else {
                    jSupportsTable.getSelectionModel().removeSelectionInterval(i, i);
                    defense.setTransferredToBrowser(true);
                }
            } else {//already transferred
                jSupportsTable.getSelectionModel().removeSelectionInterval(i, i);
            }
        }
    }

    private void copyDefRequests() {
        List<DefenseInformation> selection = getSelectedRows();

        if (selection.isEmpty()) {
            showInfo(trans.get("KeineEintraege"));
            return;
        }
        boolean extended = (JOptionPaneHelper.showQuestionConfirmBox(this, trans.get("ErweiterterBBCode_Text"), trans.get("ErweiterterBBCode"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.YES_OPTION);

        SimpleDateFormat df = TimeManager.getSimpleDateFormat(trans.get("dateFormat"));
        StringBuilder b = new StringBuilder();
        b.append(trans.get("vergleichbareUnterstutzungen"));

        for (DefenseInformation defense : selection) {
            if (!defense.isSave()) {
                Village target = defense.getTarget();
                int needed = defense.getNeededSupports();
                int available = defense.getSupports().length;
                TroopAmountFixed split = DSWorkbenchSettingsDialog.getSingleton().getDefense();
                TroopAmountFixed need = new TroopAmountFixed();
                for (UnitHolder unit: DataHolder.getSingleton().getUnits()) {
                    need.setAmountForUnit(unit, (needed - available) * split.getAmountForUnit(unit));
                }

                if (extended) {
                    b.append("[table]\n");
                    b.append("[**]").append(target.toBBCode()).append("[|]");
                    b.append("[img]").append(UnitTableInterface.createDefenderUnitTableLink(need)).append("[/img][/**]\n");
                    b.append(trans.get("AngriffeFakes")).append(defense.getAttackCount()).append("/").append(defense.getFakeCount()).append("\n");
                    b.append(trans.get("Zeitraum")).append(df.format(defense.getFirstAttack())).append(trans.get("bis")).append(df.format(defense.getLastAttack())).append("\n");
                    b.append("[/table]\n");
                } else {
                    b.append(buildSimpleRequestTable(target, need, defense));
                }
            }
        }
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(b.toString()), null);
            showSuccess(trans.get("Unterstuetunganfrage"));
        } catch (HeadlessException hex) {
            showError(trans.get("Failed_clipboard"));
        }
    }

    private String buildSimpleRequestTable(Village pTarget, TroopAmountFixed pNeed, DefenseInformation pDefense) {
        StringBuilder b = new StringBuilder();
        SimpleDateFormat df = TimeManager.getSimpleDateFormat(trans.get("dateFormat"));
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

        b.append(trans.get("BBAngriffeFakes")).append(pDefense.getAttackCount()).append("/").append(pDefense.getFakeCount());
        for (int i = 0; i < colCount; i++) {
            b.append("[|]");
        }
        b.append("\n");
        b.append(trans.get("BBZeitraum")).append(df.format(pDefense.getFirstAttack())).append(trans.get("bis")).append(df.format(pDefense.getLastAttack()));
        for (int i = 0; i < colCount; i++) {
            b.append("[|]");
        }
        b.append("\n");
        b.append("[/table]\n");

        return b.toString();
    }

    private void transferToRetimeTool() {
        List<DefenseInformation> attacks = getSelectedRows();
        if (attacks.isEmpty()) {
            return;
        }
        SimpleDateFormat f;
        if (!ServerSettings.getSingleton().isMillisArrival()) {
            f = TimeManager.getSimpleDateFormat(PluginManager.getSingleton().getVariableValue("sos.date.format"));
        } else {
            f = TimeManager.getSimpleDateFormat(PluginManager.getSingleton().getVariableValue("sos.date.format.ms"));
        }
        StringBuilder b = new StringBuilder();

        b.append(PluginManager.getSingleton().getVariableValue("sos.source")).append(" ").
                append(attacks.get(0).getTargetInformation().getFirstTimedAttack().getSource().toString()).append("\n");
        b.append(trans.get("Ziel")).append(attacks.get(0).getTarget().toString()).append("\n");
        b.append(PluginManager.getSingleton().getVariableValue("attack.arrive.time")).append(" ").
                append(f.format(attacks.get(0).getFirstAttack())).append("\n");
        if (RetimerDataPanel.getSingleton().readAttackFromString(b.toString())) {
            TacticsPlanerWizard.show();
        } else {
            showError("Retimer_Error");
        }
    }

    public de.tor.tribes.io.UnitHolder getSlowestUnit() {
        return DSWorkbenchSettingsDialog.getSingleton().getDefense().getSlowestUnit();
    }

    private void removeSelection() {
        removeSelection(true);
        updateSupportTable();
    }

    private void copySelectionToClipboardAsBBCode() {
        HashMap<Tribe, SOSRequest> selectedRequests = new HashMap<>();
        List<DefenseInformation> selection = getSelectedRows();
        if (selection.isEmpty()) {
            showInfo(trans.get("No_SOS"));
            return;
        }

        for (DefenseInformation info : selection) {
            Tribe defender = info.getTarget().getTribe();
            SOSRequest request = selectedRequests.get(defender);
            if (request == null) {
                request = new SOSRequest(defender);
                selectedRequests.put(defender, request);
            }
            TargetInformation targetInfo = request.addTarget(info.getTarget());
            targetInfo.merge(info.getTargetInformation());
        }

        try {
            boolean extended = (JOptionPaneHelper.showQuestionConfirmBox(this, trans.get("ErweiterterBBCode_Text"), trans.get("ErweiterterBBCode"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.YES_OPTION);

            StringBuilder buffer = new StringBuilder();
            if (extended) {
                buffer.append(trans.get("BB_SOS_Anfrage"));
            } else {
                buffer.append(trans.get("SOSAnfrageBB"));
            }

            List<SOSRequest> requests = new LinkedList<>();
            CollectionUtils.addAll(requests, selectedRequests.values());
            buffer.append(new SosListFormatter().formatElements(requests,
                    extended));

            if (extended) {
                buffer.append(trans.get("Erstelltamsize"));
                buffer.append(TimeManager.getSimpleDateFormat(trans.get("DateFormat_um")).format(new Date()));
                buffer.append(trans.get("mitDSWorkbench"));
                buffer.append(Constants.VERSION).append(Constants.VERSION_ADDITION + "[/size]\n");
            } else {
                buffer.append(trans.get("Erstelltam"));
                buffer.append(TimeManager.getSimpleDateFormat(trans.get("DateFormat_um")).format(new Date()));
                buffer.append(trans.get("mitDSWorkbench"));
                buffer.append(Constants.VERSION).append(Constants.VERSION_ADDITION + "\n");
            }

            String b = buffer.toString();
            StringTokenizer t = new StringTokenizer(b, "[");
            int cnt = t.countTokens();
            if (cnt > 5000) {
                if (JOptionPaneHelper.showQuestionConfirmBox(this, trans.get("Anfrage_Text"), trans.get("mutch_BB"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(b), null);
            showSuccess(trans.get("Zwischenablagekopiert"));
        } catch (Exception e) {
            logger.error("Failed to copy data to clipboard", e);
            showError(trans.get("failed_copy_clipboard"));
        }

    }

    private void removeSelection(boolean pAsk) {
        int[] selectedRows = jAttacksTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length < 1) {
            showInfo(trans.get("no_attacks"));
            return;
        }

        if (!pAsk || JOptionPaneHelper.showQuestionConfirmBox(this, trans.get("WillstDu") + ((selectedRows.length == 1) ? trans.get("gewaehltenAngriff") : trans.get("gewaehlteAngriffe")) + trans.get("really_delete"), trans.get("Loeschen"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.YES_OPTION) {

            DefenseToolModel model = TableHelper.getTableModel(jAttacksTable);
            int numRows = selectedRows.length;
            List<DefenseInformation> toRemove = new LinkedList<>();
            for (int row : selectedRows) {
                toRemove.add(model.getRows()[jAttacksTable.convertRowIndexToModel(row)]);
            }

            //remove model rows
            for (DefenseInformation defense : toRemove) {
                model.removeRow(defense);
            }

            //update SOS requests
            for (ManageableType e : SOSManager.getSingleton().getAllElements()) {
                SOSRequest r = (SOSRequest) e;
                for (DefenseInformation defense : toRemove) {
                    r.removeTarget(defense.getTarget());
                }
            }

            model.fireTableDataChanged();
            showSuccess(((numRows == 1) ? trans.get("Angriff") : numRows + trans.get("Angriffe")) + trans.get("geloescht"));
        }
    }

    private List<DefenseInformation> getSelectedRows() {
        List<DefenseInformation> infos = new LinkedList<>();
        if (getModel().getRowCount() == 0) {
            showInfo(trans.get("noSOS"));
        } else {
            int[] rows = jAttacksTable.getSelectedRows();
            if (rows == null || rows.length == 0) {
                showInfo(trans.get("noattack"));
                return infos;
            } else {
                for (int row : rows) {
                    infos.add(getModel().getRows()[jAttacksTable.convertRowIndexToModel(row)]);
                }
            }
        }
        return infos;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jSOSInputPanel = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();
        jResultPanel = new org.jdesktop.swingx.JXPanel();
        infoPanel = new org.jdesktop.swingx.JXCollapsiblePane();
        jXInfoLabel = new org.jdesktop.swingx.JXLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jAttacksTable = new org.jdesktop.swingx.JXTable();
        jSupportsTable = new org.jdesktop.swingx.JXTable();
        jSOSPanel = new javax.swing.JPanel();
        capabilityInfoPanel1 = new de.tor.tribes.ui.components.CapabilityInfoPanel();
        jAlwaysOnTopBox = new javax.swing.JCheckBox();

        jSOSInputPanel.setMinimumSize(new java.awt.Dimension(500, 400));
        jSOSInputPanel.setPreferredSize(new java.awt.Dimension(500, 400));
        jSOSInputPanel.setLayout(new java.awt.GridBagLayout());

        jProgressBar1.setMinimumSize(new java.awt.Dimension(100, 20));
        jProgressBar1.setPreferredSize(new java.awt.Dimension(100, 20));
        jProgressBar1.setString("Bereit");
        jProgressBar1.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jSOSInputPanel.add(jProgressBar1, gridBagConstraints);

        jButton1.setMaximumSize(new java.awt.Dimension(73, 60));
        jButton1.setMinimumSize(new java.awt.Dimension(73, 60));
        jButton1.setPreferredSize(new java.awt.Dimension(73, 60));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireReadDataFromClipboardEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jSOSInputPanel.add(jButton1, gridBagConstraints);

        jResultPanel.setPreferredSize(new java.awt.Dimension(360, 300));
        jResultPanel.setLayout(new java.awt.BorderLayout());

        infoPanel.setCollapsed(true);
        infoPanel.setInheritAlpha(false);

        jXInfoLabel.setText(trans.get("KeineMeldung"));
        jXInfoLabel.setOpaque(true);
        jXInfoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jXInfoLabelfireHideInfoEvent(evt);
            }
        });
        infoPanel.add(jXInfoLabel, java.awt.BorderLayout.CENTER);

        jResultPanel.add(infoPanel, java.awt.BorderLayout.SOUTH);

        jAttacksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(jAttacksTable);

        jResultPanel.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 10.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jSOSInputPanel.add(jResultPanel, gridBagConstraints);

        jSupportsTable.setModel(new javax.swing.table.DefaultTableModel(
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

        setTitle(trans.get("SOSAnalyzer"));
        setMinimumSize(new java.awt.Dimension(500, 400));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jSOSPanel.setBackground(new java.awt.Color(239, 235, 223));
        jSOSPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        jSOSPanel.setPreferredSize(new java.awt.Dimension(500, 300));
        jSOSPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jSOSPanel, gridBagConstraints);

        capabilityInfoPanel1.setCopyable(false);
        capabilityInfoPanel1.setPastable(false);
        capabilityInfoPanel1.setSearchable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(capabilityInfoPanel1, gridBagConstraints);

        jAlwaysOnTopBox.setText(trans.get("ImmerimVordergrund"));
        jAlwaysOnTopBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireAlwaysOnTopEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jAlwaysOnTopBox, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jXInfoLabelfireHideInfoEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jXInfoLabelfireHideInfoEvent
        infoPanel.setCollapsed(true);
}//GEN-LAST:event_jXInfoLabelfireHideInfoEvent

private void fireAlwaysOnTopEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireAlwaysOnTopEvent
    setAlwaysOnTop(!isAlwaysOnTop());
}//GEN-LAST:event_fireAlwaysOnTopEvent

    private void fireReadDataFromClipboardEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireReadDataFromClipboardEvent
        if (jButton1.isEnabled()) {
            readRequestFromClipboard();
        }
    }//GEN-LAST:event_fireReadDataFromClipboardEvent

    public void updateExternally() {
        getModel().fireTableDataChanged();
        updateSupportTable();
    }

    private void updateSupportTable() {
        SupportsModel model = TableHelper.getTableModel(jSupportsTable);
        model.clear();
        for (ManageableType t : SOSManager.getSingleton().getAllElements()) {
            SOSRequest r = (SOSRequest) t;
            for(Village target: r.getTargets()) {
                DefenseInformation d = r.getDefenseInformation(target);
                for (Defense i : d.getSupports()) {
                    model.addRow(i);
                }
            }
        }
        model.fireTableDataChanged();
    }

    private void readRequestFromClipboard() {
        try {
            Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            String data = (String) t.getTransferData(DataFlavor.stringFlavor);

            List<SOSRequest> requests = PluginManager.getSingleton().executeSOSParser(data);
            if (requests != null && !requests.isEmpty()) {
                for (SOSRequest request : requests) {
                    SOSManager.getSingleton().addRequest(request);
                }
                findFakes();
                if (!ServerSettings.getSingleton().isMillisArrival()) {
                    showInfo(trans.get("aktuelleServer_Text"));
                }
            } else {
                showInfo(trans.get("noSOS_found"));
            }
            updateView();
            analyzeData();
        } catch (HeadlessException | IOException | UnsupportedFlavorException he) {
            showInfo(trans.get("error_clipboard"));
        }
    }

    private void analyzeData() {
        analyzeData(false);
    }

    private void analyzeData(boolean pReAnalyze) {
        if (a == null || !a.isRunning()) {
            jProgressBar1.setValue(0);
            jProgressBar1.setString(trans.get("Analysiere"));
            jButton1.setEnabled(false);
            
            TroopAmountFixed stdOffense = DSWorkbenchSettingsDialog.getSingleton().getOffense();
            TroopAmountFixed stdDefense = DSWorkbenchSettingsDialog.getSingleton().getDefense();
            if(stdOffense.getTroopPopCount() == 0 || stdDefense.getTroopPopCount() == 0) {
                showInfo(trans.get("no_analysieren"));
                jProgressBar1.setString(trans.get("Bereit"));
                jButton1.setEnabled(true);
                updateSupportTable();
                return;
            }
            
            a = new DefenseAnalyzer(new DefenseAnalyzer.DefenseAnalyzerListener() {

                @Override
                public void fireProceedEvent(double pStatus) {
                    int status = (int) Math.rint(pStatus * 100.0);
                    jProgressBar1.setValue((int) Math.rint(pStatus * 100.0));
                    if (status % 10 == 0) {
                        jAttacksTable.repaint();
                    }
                }

                @Override
                public void fireFinishedEvent() {
                    jButton1.setEnabled(true);
                    jProgressBar1.setString(trans.get("Bereit"));
                    updateSupportTable();
                }
            }, stdOffense, stdDefense,
                    GlobalOptions.getProperties().getInt("max.sim.rounds"),
                    GlobalOptions.getProperties().getInt("max.loss.ratio"),
                    pReAnalyze);
            a.start();
        }
    }

    private void setSelectionSecured() {
        for (DefenseInformation info : getSelectedRows()) {
            info.setDefenseStatus(DefenseInformation.DEFENSE_STATUS.SAVE);
        }
        repaint();
    }

    private void updateView() {
        DefenseToolModel model = getModel();
        model.clear();

        for (ManageableType e : SOSManager.getSingleton().getAllElements()) {
            SOSRequest r = (SOSRequest) e;
            for(Village target: r.getTargets()) {
                model.addRow(r.getDefenseInformation(target));
            }
        }
        model.fireTableDataChanged();
    }

    private void findFakes() {
        Boolean fakesOrAGsFound = false;
        
        if(GlobalOptions.getProperties().getBoolean("sos.mark.all.duplicates.as.fake")) {
            List<Village> sourcesOnce = new ArrayList<>();
            List<Village> sourcesMoreThanOnce = new ArrayList<>();
            
            for (ManageableType manTyp: SOSManager.getSingleton().getAllElements()) {
                SOSRequest pRequest = (SOSRequest) manTyp;
                for(Village target: pRequest.getTargets()) {
                    TargetInformation targetInfo = pRequest.getTargetInformation(target);
                    
                    for(Village targetSource: targetInfo.getSources()) {
                        if(sourcesOnce.contains(targetSource) &&
                                !sourcesMoreThanOnce.contains(targetSource)) {
                            sourcesMoreThanOnce.add(targetSource);
                        }
                        else {
                            sourcesOnce.add(targetSource);
                        }
                    }
                }
            }
            
            for (ManageableType manTyp: SOSManager.getSingleton().getAllElements()) {
                SOSRequest pRequest = (SOSRequest) manTyp;
                
                for(Village target: pRequest.getTargets()) {
                    TargetInformation targetInfo = pRequest.getTargetInformation(target);

                    Enumeration<TimedAttack> attacks = Collections.enumeration(targetInfo.getAttacks());
                    while (attacks.hasMoreElements()) {
                        TimedAttack att = attacks.nextElement();

                        //check for snobs
                        long sendTime = att.getlArriveTime() - (long) (
                                DSCalculator.calculateMoveTimeInSeconds(att.getSource(), target,
                                DataHolder.getSingleton().getUnitByPlainName("ram").getSpeed()) * 1000);
                        if (sendTime >= System.currentTimeMillis()) {
                            att.setPossibleSnob(true);
                            att.setPossibleFake(false);
                            fakesOrAGsFound = true;
                        }

                        //check for multiple attacks from same source
                        if(sourcesMoreThanOnce.contains(att.getSource())) {
                            if (!att.isPossibleSnob()) {//ignore snobs
                                att.setPossibleSnob(false);
                                att.setPossibleFake(true);
                                fakesOrAGsFound = true;
                            }
                        }
                    }
                }
            }
        }
        else {
            for (ManageableType manTyp: SOSManager.getSingleton().getAllElements()) {
                SOSRequest pRequest = (SOSRequest) manTyp;
                
                for(Village target: pRequest.getTargets()) {
                    TargetInformation targetInfo = pRequest.getTargetInformation(target);

                    //check for multiple attacks from same source to same target
                    for(Village source: targetInfo.getSources()) {
                        if (targetInfo.getAttackCountFromSource(source) > 1) {
                            for (TimedAttack att : targetInfo.getAttacksFromSource(source)) {
                                if (!att.isPossibleFake() && !att.isPossibleSnob()) {//check only once
                                    long sendTime = att.getlArriveTime() - (long) (DSCalculator.calculateMoveTimeInSeconds(source, target, DataHolder.getSingleton().getUnitByPlainName("ram").getSpeed()) * 1000);
                                    if (sendTime < System.currentTimeMillis()) {
                                        att.setPossibleSnob(false);
                                        att.setPossibleFake(true);
                                    } else {
                                        att.setPossibleSnob(true);
                                        att.setPossibleFake(false);
                                    }
                                    fakesOrAGsFound = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if(fakesOrAGsFound) {
            //if we have found something inside the Atts we have to rebuild the Defence requests
            logger.debug("refreshing deff requests");
            
            for (ManageableType manTyp: SOSManager.getSingleton().getAllElements()) {
                SOSRequest pRequest = (SOSRequest) manTyp;
                
                for(Village target: pRequest.getTargets()) {
                    pRequest.getDefenseInformation(target).updateAttackInfo();
                }
            }
        }
    }

    private static void createSampleRequests() {
        int wallLevel = 20;
        int supportCount = 20;
        int maxAttackCount = 10;
        int maxFakeCount = 0;

        Village[] villages = GlobalOptions.getSelectedProfile().getTribe().getVillageList();
        Village[] attackerVillages = DataHolder.getSingleton().getTribeByName("Alexander25").getVillageList();

        for (int i = 0; i < supportCount; i++) {
            int id = (int) Math.rint(Math.random() * (villages.length - 1));
            Village target = villages[id];
            SOSRequest r = new SOSRequest(target.getTribe());
            TargetInformation info = r.addTarget(target);
            info.setWallLevel(wallLevel);

            TroopAmountFixed troops = new TroopAmountFixed();
            troops.setAmountForUnit("spear", (int) Math.rint(Math.random() * 14000));
            troops.setAmountForUnit("sword", (int) Math.rint(Math.random() * 14000));
            troops.setAmountForUnit("heavy", (int) Math.rint(Math.random() * 5000));
            info.setTroops(troops);
            
            int cnt = (int) Math.rint(maxAttackCount * Math.random());
            for (int j = 0; j < cnt; j++) {
                int idx = (int) Math.rint(Math.random() * (attackerVillages.length - 2));
                Village v = attackerVillages[idx];
                info.addAttack(v, new Date(System.currentTimeMillis() + Math.round(DateUtils.MILLIS_PER_DAY * 7 * Math.random())));
                for (int k = 0; k < (int) Math.rint(maxFakeCount * Math.random()); k++) {
                    idx = (int) Math.rint(Math.random() * (attackerVillages.length - 2));
                    v = attackerVillages[idx];
                    info.addAttack(v, new Date(System.currentTimeMillis() + Math.round(3600 * Math.random())));
                }
            }
            SOSManager.getSingleton().addRequest(r);
        }
    }

    public void updateCountdown() {
        if (!jSupportsTable.isVisible()) {
            return;
        }
        TableColumnExt col = jSupportsTable.getColumnExt("Countdown");
        if (col.isVisible()) {
            int startX = 0;
            for (int i = 0; i < jSupportsTable.getColumnCount(); i++) {
                if (jSupportsTable.getColumnExt(i).equals(col)) {
                    break;
                }
                startX += (jSupportsTable.getColumnExt(i).isVisible()) ? jSupportsTable.getColumnExt(i).getWidth() : 0;
            }
            jSupportsTable.repaint(startX, (int) jSupportsTable.getVisibleRect().getY(), startX + col.getWidth(), (int) jSupportsTable.getVisibleRect().getHeight());
        }
    }

    public void updateTime() {
        TableColumnExt firstCol = jSupportsTable.getColumnExt(trans.get("FruehesteAbschickzeit"));
        TableColumnExt lastCol = jSupportsTable.getColumnExt(trans.get("SpaetesteAbschickzeit"));
        if (firstCol.isVisible() && lastCol.isVisible()) {
            int startX = 0;
            for (int i = 0; i < jSupportsTable.getColumnCount(); i++) {
                if (jSupportsTable.getColumnExt(i).equals(firstCol)) {
                    break;
                }
                startX += (jSupportsTable.getColumnExt(i).isVisible()) ? jSupportsTable.getColumnExt(i).getWidth() : 0;
            }
            jSupportsTable.repaint(startX, 0, startX + firstCol.getWidth() + lastCol.getWidth(), jSupportsTable.getHeight());
        }
    }

    @Override
    public void fireVillagesDraggedEvent(List<Village> pVillages, Point pDropLocation) {
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.components.CapabilityInfoPanel capabilityInfoPanel1;
    private org.jdesktop.swingx.JXCollapsiblePane infoPanel;
    private javax.swing.JCheckBox jAlwaysOnTopBox;
    private org.jdesktop.swingx.JXTable jAttacksTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JProgressBar jProgressBar1;
    private org.jdesktop.swingx.JXPanel jResultPanel;
    private javax.swing.JPanel jSOSInputPanel;
    private javax.swing.JPanel jSOSPanel;
    private javax.swing.JScrollPane jScrollPane6;
    private org.jdesktop.swingx.JXTable jSupportsTable;
    private org.jdesktop.swingx.JXLabel jXInfoLabel;
    // End of variables declaration//GEN-END:variables
}

class SupportColorUpdateThread extends Thread {

    public SupportColorUpdateThread() {
        setName("SupportColorUpdater");
        setPriority(MIN_PRIORITY);
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                DSWorkbenchSOSRequestAnalyzer.getSingleton().updateTime();
                try {
                    Thread.sleep(10000);
                } catch (Exception ignored) {
                }
            } catch (Throwable ignored) {
            }
        }
    }
}

class SupportCountdownThread extends Thread {

    private boolean showCountdown = true;

    public SupportCountdownThread() {
        setName("SupportTableCountdownUpdater");
        setPriority(MIN_PRIORITY);
        setDaemon(true);
    }

    public void updateSettings() {
        showCountdown = Boolean.parseBoolean(GlobalOptions.getProperty("show.live.countdown"));
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (showCountdown && DSWorkbenchSOSRequestAnalyzer.getSingleton().isVisible()) {
                    DSWorkbenchSOSRequestAnalyzer.getSingleton().updateCountdown();
                    //yield();
                    sleep(100);
                } else {
                    // yield();
                    sleep(1000);
                }
            } catch (Exception ignored) {
            }
        }
    }
}
