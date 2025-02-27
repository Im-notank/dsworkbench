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
package de.tor.tribes.ui.panels;

import de.tor.tribes.control.ManageableType;
import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.TroopAmountElement;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.TimeSpan;
import de.tor.tribes.types.UserProfile;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.ImageManager;
import de.tor.tribes.ui.editors.*;
import de.tor.tribes.ui.models.AttackTableModel;
import de.tor.tribes.ui.renderer.*;
import de.tor.tribes.ui.views.DSWorkbenchAttackFrame;
import de.tor.tribes.ui.views.DSWorkbenchSelectionFrame;
import de.tor.tribes.ui.windows.AttacksToTextExportDialog;
import de.tor.tribes.ui.windows.ClockFrame;
import de.tor.tribes.ui.wiz.ret.RetimerDataPanel;
import de.tor.tribes.ui.wiz.tap.TacticsPlanerWizard;
import de.tor.tribes.util.*;
import de.tor.tribes.util.attack.AttackManager;
import de.tor.tribes.util.attack.StandardAttackManager;
import de.tor.tribes.util.bb.AttackListFormatter;
import de.tor.tribes.util.html.AttackPlanHTMLExporter;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.VerticalAlignment;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.table.TableColumnExt;

/**
 *
 * @author Torridity
 */
public class AttackTableTab extends javax.swing.JPanel implements ListSelectionListener {

    private static Translator trans = TranslationManager.getTranslator("ui.panels.AttackTableTab");
    private static Logger logger = LogManager.getLogger("AttackTableTab");

    public enum TRANSFER_TYPE {
        CLIPBOARD_PLAIN, CLIPBOARD_BB, FILE_HTML, FILE_TEXT, DSWB_RETIME, SELECTION_TOOL, BROWSER_LINK, CUT_TO_INTERNAL_CLIPBOARD, COPY_TO_INTERNAL_CLIPBOARD, FROM_INTERNAL_CLIPBOARD
    }
    private String sAttackPlan = null;
    private final static JXTable jxAttackTable = new JXTable();
    private static AttackTableModel attackModel = null;
    private static boolean KEY_LISTENER_ADDED = false;
    private static PainterHighlighter highlighter = null;
    private ActionListener actionListener = null;
    private static List<Highlighter> highlighters = new ArrayList<>();
    private static boolean useSortColoring = false;

    static {
        jxAttackTable.setRowHeight(24);
        jxAttackTable.setHighlighters(new CompoundHighlighter(HighlighterFactory.createSimpleStriping(), HighlighterFactory.createAlternateStriping(Constants.DS_ROW_A, Constants.DS_ROW_B)));
        jxAttackTable.setColumnControlVisible(true);
        jxAttackTable.getTableHeader().setDefaultRenderer(new UnitTableHeaderRenderer());
        jxAttackTable.setDefaultEditor(Village.class, new VillageCellEditor());
        jxAttackTable.setDefaultEditor(UnitHolder.class, new UnitCellEditor());
        jxAttackTable.setDefaultRenderer(UnitHolder.class, new UnitCellRenderer());
        jxAttackTable.setDefaultRenderer(Long.class, new ColoredCoutdownCellRenderer());
        jxAttackTable.setDefaultRenderer(Date.class, new ColoredDateCellRenderer());
        jxAttackTable.setDefaultEditor(Date.class, new DateSpinEditor());
        jxAttackTable.setDefaultRenderer(Integer.class, new NoteIconCellRenderer(NoteIconCellRenderer.ICON_TYPE.NOTE));
        jxAttackTable.setDefaultEditor(Integer.class, new NoteIconCellEditor(NoteIconCellEditor.ICON_TYPE.NOTE));
        jxAttackTable.setDefaultEditor(TroopAmountElement.class, new StandardAttackElementEditor());

        attackModel = new AttackTableModel(AttackManager.DEFAULT_GROUP);

        jxAttackTable.setModel(attackModel);
        TableColumnExt drawCol = jxAttackTable.getColumnExt(trans.getRaw("ui.models.AttackTableModel.show_on_map"));
        drawCol.setCellRenderer(new CustomBooleanRenderer(CustomBooleanRenderer.LayoutStyle.DRAW_NOTDRAW));
        drawCol.setCellEditor(new CustomCheckBoxEditor(CustomBooleanRenderer.LayoutStyle.DRAW_NOTDRAW));
        TableColumnExt transferCol = jxAttackTable.getColumnExt(trans.getRaw("ui.models.AttackTableModel.transfer"));
        transferCol.setCellRenderer(new CustomBooleanRenderer(CustomBooleanRenderer.LayoutStyle.SENT_NOTSENT));
        transferCol.setCellEditor(new CustomCheckBoxEditor(CustomBooleanRenderer.LayoutStyle.SENT_NOTSENT));
        TableColumnExt runtimeCol = jxAttackTable.getColumnExt(trans.getRaw("ui.models.AttackTableModel.runtime"));
        runtimeCol.setVisible(false);
        
        BufferedImage back = ImageUtils.createCompatibleBufferedImage(5, 5, BufferedImage.BITMASK);
        Graphics2D g = back.createGraphics();
        GeneralPath p = new GeneralPath();
        p.moveTo(0, 0);
        p.lineTo(5, 0);
        p.lineTo(5, 5);
        p.closePath();
        g.setColor(Color.GREEN.darker());
        g.fill(p);
        g.dispose();
        
        jxAttackTable.addHighlighter(new PainterHighlighter(HighlightPredicate.EDITABLE, new ImagePainter(back, HorizontalAlignment.RIGHT, VerticalAlignment.TOP)));
    }

    /**
     * Creates new form AttackTablePanel
     *
     * @param pAttackPlan
     * @param pActionListener
     */
    public AttackTableTab(String pAttackPlan, final ActionListener pActionListener) {
        actionListener = pActionListener;
        sAttackPlan = pAttackPlan;
        initComponents();
        jScrollPane1.setViewportView(jxAttackTable);
        jUnitBox.setRenderer(new UnitListCellRenderer());

        jTypeComboBox.setRenderer(new NoteIconListCellRenderer(NoteIconCellEditor.ICON_TYPE.NOTE));
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i = -1; i <= ImageManager.MAX_NOTE_SYMBOL; i++) {
            model.addElement(i);
        }
        jTypeComboBox.setModel(model);
        jUnconfiguredTypeWarning.setVisible(false);
        jTypeComboBox.setSelectedIndex(0);
        if (!KEY_LISTENER_ADDED) {
            KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
            KeyStroke bbCopy = KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK, false);
            KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
            KeyStroke cut = KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK, false);
            KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
            jxAttackTable.registerKeyboardAction(pActionListener, "Copy", copy, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            jxAttackTable.registerKeyboardAction(pActionListener, "Cut", cut, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            jxAttackTable.registerKeyboardAction(pActionListener, "Paste", paste, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            jxAttackTable.registerKeyboardAction(pActionListener, "Delete", delete, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            jxAttackTable.registerKeyboardAction(pActionListener, "BBCopy", bbCopy, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            jxAttackTable.getActionMap().put("find", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pActionListener.actionPerformed(new ActionEvent(jxAttackTable, 0, "Find"));
                }
            });

            jxAttackTable.getRowSorter().addRowSorterListener(new RowSorterListener() {
                @Override
                public void sorterChanged(RowSorterEvent e) {
                    actionListener.actionPerformed(new ActionEvent(this, 0, "Recolor"));
                }
            });

            KEY_LISTENER_ADDED = true;
        }
        jxAttackTable.getSelectionModel().addListSelectionListener(AttackTableTab.this);
        jDateField.setDate(new Date());
        jTimeChangeDialog.pack();
        jChangeAttackTypeDialog.pack();
    }

    public void deregister() {
        jxAttackTable.getSelectionModel().removeListSelectionListener(this);
    }

    public void setUseSortColoring() {
        useSortColoring = !useSortColoring;
        if (!useSortColoring) {
            for (Highlighter h : highlighters) {
                jxAttackTable.removeHighlighter(h);
            }
            highlighters.clear();
        } else {
            updateSortHighlighter();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            int selectionCount = jxAttackTable.getSelectedRowCount();
            if (selectionCount != 0) {
                showInfo(selectionCount + ((selectionCount == 1) ? trans.get("Befehlgewaehlt") : trans.get("Befehlegewaehlt")));
            }
        }
    }

    public void showSuccess(String pMessage) {
        infoPanel.setCollapsed(false);
        jXLabel1.setBackgroundPainter(new MattePainter(Color.GREEN));
        jXLabel1.setForeground(Color.BLACK);
        jXLabel1.setText(pMessage);
    }

    public void showInfo(String pMessage) {
        infoPanel.setCollapsed(false);
        jXLabel1.setBackgroundPainter(new MattePainter(getBackground()));
        jXLabel1.setForeground(Color.BLACK);
        jXLabel1.setText(pMessage);
    }

    public void showError(String pMessage) {
        infoPanel.setCollapsed(false);
        jXLabel1.setBackgroundPainter(new MattePainter(Color.RED));
        jXLabel1.setForeground(Color.WHITE);
        jXLabel1.setText(pMessage);
    }

    public String getAttackPlan() {
        return sAttackPlan;
    }

    public JXTable getAttackTable() {
        return jxAttackTable;
    }

    public void addAttackTimer() {
        List<Attack> selection = getSelectedAttacks();
        if (selection.isEmpty()) {
            showInfo(trans.get("KeinBefehlgewaehlt"));
        } else {
            //remove attacks from past
            for (Attack a : selection.toArray(new Attack[]{})) {
                if (a.getSendTime().getTime() < System.currentTimeMillis()) {
                    selection.remove(a);
                }
            }

            String result = JOptionPane.showInputDialog(this, trans.get("WievieleSekunden"), 30);
            if (result != null) {
                try {
                    int time = Integer.parseInt(result);
                    for (Attack a : selection) {
                        ClockFrame.getSingleton().addTimer(a, time);
                    }
                    showInfo(selection.size() + trans.get("Timererstellt"));
                } catch (Exception ex) {
                    showInfo(trans.get("UngueltigeSekundenangabe"));
                }
            }
        }
    }

    public void updateCountdown() {
        TableColumnExt col = jxAttackTable.getColumnExt(trans.getRaw("ui.models.AttackTableModel.remaining"));
        if (col.isVisible()) {
            int startX = 0;
            for (int i = 0; i < jxAttackTable.getColumnCount(); i++) {
                if (jxAttackTable.getColumnExt(i).equals(col)) {
                    break;
                }
                startX += (jxAttackTable.getColumnExt(i).isVisible()) ? jxAttackTable.getColumnExt(i).getWidth() : 0;
            }
            jxAttackTable.repaint(startX, (int) jxAttackTable.getVisibleRect().getY(), startX + col.getWidth(), (int) jxAttackTable.getVisibleRect().getHeight());
        }
    }

    public void updateTime() {
        TableColumnExt col = jxAttackTable.getColumnExt(trans.getRaw("ui.models.AttackTableModel.send_time"));
        if (col.isVisible()) {
            int startX = 0;
            for (int i = 0; i < jxAttackTable.getColumnCount(); i++) {
                if (jxAttackTable.getColumnExt(i).equals(col)) {
                    break;
                }
                startX += (jxAttackTable.getColumnExt(i).isVisible()) ? jxAttackTable.getColumnExt(i).getWidth() : 0;
            }
            jxAttackTable.repaint(startX, 0, startX + col.getWidth(), jxAttackTable.getHeight());
        }
    }

    public void updatePlan() {
        attackModel.setPlan(sAttackPlan);
        UIHelper.initTableColums(
                jxAttackTable,
                trans.getRaw("ui.models.AttackTableModel.unit"),
                trans.getRaw("ui.models.AttackTableModel.type"),
                trans.getRaw("ui.models.AttackTableModel.transfer"),
                trans.getRaw("ui.models.AttackTableModel.show_on_map")
        );

        jScrollPane1.setViewportView(jxAttackTable);
        updateSortHighlighter();
    }

    public void updateSortHighlighter() {
        if (useSortColoring) {
            TableHelper.applyTableColoring(jxAttackTable, sAttackPlan, highlighters);
        }
    }

    public void updateFilter(final String pValue, final List<String> columns, final boolean pCaseSensitive, final boolean pFilterRows) {
        if (highlighter != null) {
            jxAttackTable.removeHighlighter(highlighter);
        }
        if (!pFilterRows) {
            jxAttackTable.setRowFilter(null);
            final List<Integer> relevantCols = new LinkedList<>();
            List<TableColumn> cols = jxAttackTable.getColumns(true);
            for (int i = 0; i < jxAttackTable.getColumnCount(); i++) {
                TableColumnExt col = jxAttackTable.getColumnExt(i);
                if (col.isVisible() && columns.contains(col.getTitle())) {
                    relevantCols.add(cols.indexOf(col));
                }
            }
            for (Integer col : relevantCols) {
                PatternPredicate patternPredicate0 = new PatternPredicate((pCaseSensitive ? "" : "(?i)") + Matcher.quoteReplacement(pValue), col);
                MattePainter mp = new MattePainter(new Color(0, 0, 0, 120));
                highlighter = new PainterHighlighter(new HighlightPredicate.NotHighlightPredicate(patternPredicate0), mp);
                jxAttackTable.addHighlighter(highlighter);
            }
        } else {
            jxAttackTable.setRowFilter(new RowFilter<TableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                    final List<Integer> relevantCols = new LinkedList<>();
                    List<TableColumn> cols = jxAttackTable.getColumns(true);
                    for (int i = 0; i < jxAttackTable.getColumnCount(); i++) {
                        TableColumnExt col = jxAttackTable.getColumnExt(i);
                        if (col.isVisible() && columns.contains(col.getTitle())) {
                            relevantCols.add(cols.indexOf(col));
                        }
                    }

                    for (Integer col : relevantCols) {
                        if (pCaseSensitive) {
                            if (entry.getStringValue(col).contains(pValue)) {
                                return true;
                            }
                        } else {
                            if (entry.getStringValue(col).toLowerCase().contains(pValue.toLowerCase())) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
        }
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

        jTimeChangeDialog = new javax.swing.JDialog();
        jOKButton = new javax.swing.JButton();
        jCancelButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jDayField = new javax.swing.JSpinner();
        jMinuteField = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jHourField = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jSecondsField = new javax.swing.JSpinner();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jDateField = new de.tor.tribes.ui.components.DateTimeField();
        jAdaptTypeBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jModifyArrivalOption = new javax.swing.JRadioButton();
        jMoveTimeOption = new javax.swing.JRadioButton();
        jRandomizeOption = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jRandomField = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        jNotRandomToNightBonus = new javax.swing.JCheckBox();
        jModifySendOption = new javax.swing.JRadioButton();
        jChangeAttackTypeDialog = new javax.swing.JDialog();
        jAcceptChangeUnitTypeButton = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jAdeptTypeBox = new javax.swing.JCheckBox();
        jAdeptUnitBox = new javax.swing.JCheckBox();
        jTypeComboBox = new javax.swing.JComboBox();
        jUnitBox = new javax.swing.JComboBox();
        jUnconfiguredTypeWarning = new org.jdesktop.swingx.JXLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        infoPanel = new org.jdesktop.swingx.JXCollapsiblePane();
        jXLabel1 = new org.jdesktop.swingx.JXLabel();

        jTimeChangeDialog.setTitle("Zeiten ändern");

        jOKButton.setText("OK");
        jOKButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireCloseTimeChangeDialogEvent(evt);
            }
        });

        jCancelButton.setText("Abbrechen");
        jCancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireCloseTimeChangeDialogEvent(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText("Tage");
        jLabel7.setMaximumSize(new java.awt.Dimension(80, 25));
        jLabel7.setMinimumSize(new java.awt.Dimension(80, 25));
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel7, gridBagConstraints);

        jLabel5.setText("Minuten");
        jLabel5.setMaximumSize(new java.awt.Dimension(80, 25));
        jLabel5.setMinimumSize(new java.awt.Dimension(80, 25));
        jLabel5.setPreferredSize(new java.awt.Dimension(80, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel5, gridBagConstraints);

        jDayField.setMinimumSize(new java.awt.Dimension(60, 25));
        jDayField.setPreferredSize(new java.awt.Dimension(60, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jDayField, gridBagConstraints);

        jMinuteField.setMinimumSize(new java.awt.Dimension(60, 25));
        jMinuteField.setPreferredSize(new java.awt.Dimension(60, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jMinuteField, gridBagConstraints);

        jLabel6.setText("Stunden");
        jLabel6.setMaximumSize(new java.awt.Dimension(80, 25));
        jLabel6.setMinimumSize(new java.awt.Dimension(80, 25));
        jLabel6.setPreferredSize(new java.awt.Dimension(80, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel6, gridBagConstraints);

        jHourField.setMinimumSize(new java.awt.Dimension(60, 25));
        jHourField.setPreferredSize(new java.awt.Dimension(60, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jHourField, gridBagConstraints);

        jLabel14.setText("Sekunden");
        jLabel14.setMaximumSize(new java.awt.Dimension(80, 25));
        jLabel14.setMinimumSize(new java.awt.Dimension(80, 25));
        jLabel14.setPreferredSize(new java.awt.Dimension(80, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jLabel14, gridBagConstraints);

        jSecondsField.setMinimumSize(new java.awt.Dimension(60, 20));
        jSecondsField.setPreferredSize(new java.awt.Dimension(60, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jSecondsField, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel8.setText("Neue Zeit");
        jLabel8.setToolTipText("");
        jLabel8.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jLabel8, gridBagConstraints);

        jDateField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jDateField, gridBagConstraints);

        jAdaptTypeBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Datum und Zeit", "Nur Datum", "Nur Zeit" }));
        jAdaptTypeBox.setEnabled(false);
        jAdaptTypeBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireChangeAdeptTypeEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jAdaptTypeBox, gridBagConstraints);

        jLabel1.setText("Angleichung");
        jLabel1.setEnabled(false);
        jLabel1.setMaximumSize(new java.awt.Dimension(80, 20));
        jLabel1.setMinimumSize(new java.awt.Dimension(80, 20));
        jLabel1.setPreferredSize(new java.awt.Dimension(80, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jLabel1, gridBagConstraints);

        buttonGroup2.add(jModifyArrivalOption);
        jModifyArrivalOption.setText("Ankunftzeit angleichen");
        jModifyArrivalOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireModifyTimeEvent(evt);
            }
        });

        buttonGroup2.add(jMoveTimeOption);
        jMoveTimeOption.setSelected(true);
        jMoveTimeOption.setText("Verschieben");
        jMoveTimeOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireModifyTimeEvent(evt);
            }
        });

        buttonGroup2.add(jRandomizeOption);
        jRandomizeOption.setText("Zufällig verschieben");
        jRandomizeOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireModifyTimeEvent(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel17.setText("Zeitfenster");
        jLabel17.setEnabled(false);
        jLabel17.setMaximumSize(new java.awt.Dimension(100, 25));
        jLabel17.setMinimumSize(new java.awt.Dimension(100, 25));
        jLabel17.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel5.add(jLabel17, gridBagConstraints);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel18.setText("+/-");
        jLabel18.setEnabled(false);
        jLabel18.setMaximumSize(new java.awt.Dimension(30, 25));
        jLabel18.setMinimumSize(new java.awt.Dimension(30, 25));
        jLabel18.setName(""); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(30, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel5.add(jLabel18, gridBagConstraints);

        jRandomField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jRandomField.setToolTipText("<html>Zeitfenster in Minuten<br/>Wird hier 2 eingegeben, so werden alle Befehle um einen zufälligen Wert<br/>\n in einem Bereich von -2 bis +2 Minuten,<br/> ausgehend von ihrer aktuellen Zeit, verschoben.</html>");
        jRandomField.setEnabled(false);
        jRandomField.setMinimumSize(new java.awt.Dimension(6, 25));
        jRandomField.setPreferredSize(new java.awt.Dimension(6, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(jRandomField, gridBagConstraints);

        jLabel19.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        jPanel5.add(jLabel19, gridBagConstraints);

        jNotRandomToNightBonus.setSelected(true);
        jNotRandomToNightBonus.setText("Nicht in Nachtbonus verschieben");
        jNotRandomToNightBonus.setToolTipText("DS Workbench sorgt dafür, dass Befehle nicht im Nachtbonus landen");
        jNotRandomToNightBonus.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(jNotRandomToNightBonus, gridBagConstraints);

        buttonGroup2.add(jModifySendOption);
        jModifySendOption.setText("Abschickzeit angleichen");
        jModifySendOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireModifyTimeEvent(evt);
            }
        });

        javax.swing.GroupLayout jTimeChangeDialogLayout = new javax.swing.GroupLayout(jTimeChangeDialog.getContentPane());
        jTimeChangeDialog.getContentPane().setLayout(jTimeChangeDialogLayout);
        jTimeChangeDialogLayout.setHorizontalGroup(
            jTimeChangeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTimeChangeDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jTimeChangeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRandomizeOption)
                    .addGroup(jTimeChangeDialogLayout.createSequentialGroup()
                        .addComponent(jModifyArrivalOption)
                        .addGap(18, 18, 18)
                        .addComponent(jModifySendOption))
                    .addComponent(jMoveTimeOption)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTimeChangeDialogLayout.createSequentialGroup()
                        .addComponent(jCancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jOKButton))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jTimeChangeDialogLayout.setVerticalGroup(
            jTimeChangeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jTimeChangeDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jMoveTimeOption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jTimeChangeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jModifyArrivalOption)
                    .addComponent(jModifySendOption))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRandomizeOption)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jTimeChangeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jOKButton)
                    .addComponent(jCancelButton))
                .addContainerGap())
        );

        jChangeAttackTypeDialog.setTitle("Befehlstyp anpassen");
        jChangeAttackTypeDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        jAcceptChangeUnitTypeButton.setText("Übernehmen");
        jAcceptChangeUnitTypeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireChangeUnitTypeEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        jChangeAttackTypeDialog.getContentPane().add(jAcceptChangeUnitTypeButton, gridBagConstraints);

        jButton15.setText("Abbrechen");
        jButton15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireChangeUnitTypeEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        jChangeAttackTypeDialog.getContentPane().add(jButton15, gridBagConstraints);

        jAdeptTypeBox.setSelected(true);
        jAdeptTypeBox.setText("Typ angleichen");
        jAdeptTypeBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jAdeptTypeBoxfireEnableDisableAdeptTypeEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jChangeAttackTypeDialog.getContentPane().add(jAdeptTypeBox, gridBagConstraints);

        jAdeptUnitBox.setText("Einheit angleichen");
        jAdeptUnitBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jAdeptUnitBoxfireEnableDisableChangeUnitEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jChangeAttackTypeDialog.getContentPane().add(jAdeptUnitBox, gridBagConstraints);

        jTypeComboBox.setMinimumSize(new java.awt.Dimension(51, 25));
        jTypeComboBox.setPreferredSize(new java.awt.Dimension(56, 25));
        jTypeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireTypeSelectionChangedEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jChangeAttackTypeDialog.getContentPane().add(jTypeComboBox, gridBagConstraints);

        jUnitBox.setEnabled(false);
        jUnitBox.setMinimumSize(new java.awt.Dimension(23, 25));
        jUnitBox.setPreferredSize(new java.awt.Dimension(28, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jChangeAttackTypeDialog.getContentPane().add(jUnitBox, gridBagConstraints);

        jUnconfiguredTypeWarning.setForeground(new java.awt.Color(255, 0, 0));
        jUnconfiguredTypeWarning.setText("Achtung: Der gewählte Befehlstyp ist nicht konfiguriert!");
        jUnconfiguredTypeWarning.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        jUnconfiguredTypeWarning.setLineWrap(true);
        jUnconfiguredTypeWarning.setMaximumSize(new java.awt.Dimension(40, 25));
        jUnconfiguredTypeWarning.setMinimumSize(new java.awt.Dimension(40, 25));
        jUnconfiguredTypeWarning.setPreferredSize(new java.awt.Dimension(40, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jChangeAttackTypeDialog.getContentPane().add(jUnconfiguredTypeWarning, gridBagConstraints);

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
        jScrollPane2.setViewportView(jXTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addContainerGap())
        );

        setBackground(new java.awt.Color(255, 255, 255));
        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setForeground(new java.awt.Color(240, 240, 240));
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        infoPanel.setCollapsed(true);
        infoPanel.setInheritAlpha(false);

        jXLabel1.setText("Keine Meldung");
        jXLabel1.setOpaque(true);
        jXLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireHideInfoEvent(evt);
            }
        });
        infoPanel.add(jXLabel1, java.awt.BorderLayout.CENTER);

        add(infoPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void fireCloseTimeChangeDialogEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCloseTimeChangeDialogEvent
        if (evt.getSource() == jOKButton) {
            actionListener.actionPerformed(new ActionEvent(this, 0, "TimeChange"));
        }
        jTimeChangeDialog.setVisible(false);
}//GEN-LAST:event_fireCloseTimeChangeDialogEvent

    private void fireModifyTimeEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireModifyTimeEvent
        boolean moveMode = false;
        boolean arriveMode = false;
        boolean randomMode = false;
        if (evt.getSource() == jMoveTimeOption) {
            moveMode = true;
        } else if (evt.getSource() == jModifyArrivalOption || evt.getSource() == jModifySendOption) {
            arriveMode = true;
        } else if (evt.getSource() == jRandomizeOption) {
            randomMode = true;
        }
        jLabel5.setEnabled(moveMode);
        jLabel6.setEnabled(moveMode);
        jLabel7.setEnabled(moveMode);
        jSecondsField.setEnabled(moveMode);
        jMinuteField.setEnabled(moveMode);
        jHourField.setEnabled(moveMode);
        jDayField.setEnabled(moveMode);
        //set arrive options
        jLabel8.setEnabled(arriveMode);
        jDateField.setEnabled(arriveMode);
        jLabel1.setEnabled(arriveMode);
        jAdaptTypeBox.setEnabled(arriveMode);
        //random options
        jLabel17.setEnabled(randomMode);
        jLabel18.setEnabled(randomMode);
        jLabel19.setEnabled(randomMode);
        jRandomField.setEnabled(randomMode);
        jNotRandomToNightBonus.setEnabled(randomMode);
}//GEN-LAST:event_fireModifyTimeEvent

    private void fireChangeUnitTypeEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireChangeUnitTypeEvent
        if (evt.getSource() == jAcceptChangeUnitTypeButton) {
            actionListener.actionPerformed(new ActionEvent(this, 0, "UnitChange"));
        }
        jChangeAttackTypeDialog.setVisible(false);
}//GEN-LAST:event_fireChangeUnitTypeEvent

    private void jAdeptTypeBoxfireEnableDisableAdeptTypeEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jAdeptTypeBoxfireEnableDisableAdeptTypeEvent
        jTypeComboBox.setEnabled(jAdeptTypeBox.isSelected());
}//GEN-LAST:event_jAdeptTypeBoxfireEnableDisableAdeptTypeEvent

    private void jAdeptUnitBoxfireEnableDisableChangeUnitEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jAdeptUnitBoxfireEnableDisableChangeUnitEvent
        jUnitBox.setEnabled(jAdeptUnitBox.isSelected());
}//GEN-LAST:event_jAdeptUnitBoxfireEnableDisableChangeUnitEvent

    private void fireHideInfoEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireHideInfoEvent
        infoPanel.setCollapsed(true);
    }//GEN-LAST:event_fireHideInfoEvent

    private void fireChangeAdeptTypeEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireChangeAdeptTypeEvent
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (jAdaptTypeBox.getSelectedIndex() == 0) {//adapt both
                jDateField.setTimeEnabled(true);
                jDateField.setDateEnabled(true);
            } else if (jAdaptTypeBox.getSelectedIndex() == 1) {//adapt date
                jDateField.setTimeEnabled(false);
                jDateField.setDateEnabled(true);
            } else if (jAdaptTypeBox.getSelectedIndex() == 2) {//adapt time
                jDateField.setTimeEnabled(true);
                jDateField.setDateEnabled(false);
            }
        }
    }//GEN-LAST:event_fireChangeAdeptTypeEvent

    private void fireTypeSelectionChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireTypeSelectionChangedEvent
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            Integer v = (Integer) jTypeComboBox.getSelectedItem();
            jUnconfiguredTypeWarning.setVisible(StandardAttackManager.getSingleton().getElementByIcon(v) == null);
        }
        jChangeAttackTypeDialog.pack();
    }//GEN-LAST:event_fireTypeSelectionChangedEvent
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private org.jdesktop.swingx.JXCollapsiblePane infoPanel;
    private static javax.swing.JButton jAcceptChangeUnitTypeButton;
    private static javax.swing.JComboBox jAdaptTypeBox;
    private static javax.swing.JCheckBox jAdeptTypeBox;
    private static javax.swing.JCheckBox jAdeptUnitBox;
    private static javax.swing.JButton jButton15;
    private static javax.swing.JButton jCancelButton;
    private static javax.swing.JDialog jChangeAttackTypeDialog;
    private static de.tor.tribes.ui.components.DateTimeField jDateField;
    private static javax.swing.JSpinner jDayField;
    private static javax.swing.JSpinner jHourField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private static javax.swing.JSpinner jMinuteField;
    private static javax.swing.JRadioButton jModifyArrivalOption;
    private static javax.swing.JRadioButton jModifySendOption;
    private static javax.swing.JRadioButton jMoveTimeOption;
    private static javax.swing.JCheckBox jNotRandomToNightBonus;
    private static javax.swing.JButton jOKButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private static javax.swing.JFormattedTextField jRandomField;
    private static javax.swing.JRadioButton jRandomizeOption;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private static javax.swing.JSpinner jSecondsField;
    private static javax.swing.JDialog jTimeChangeDialog;
    private static javax.swing.JComboBox jTypeComboBox;
    private org.jdesktop.swingx.JXLabel jUnconfiguredTypeWarning;
    private static javax.swing.JComboBox jUnitBox;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXTable jXTable1;
    // End of variables declaration//GEN-END:variables

    public void fireChangeTimeEvent() {
        List<Attack> attacksToModify = getSelectedAttacks();
        if (jMoveTimeOption.isSelected()) {
            Integer sec = (Integer) jSecondsField.getValue();
            Integer min = (Integer) jMinuteField.getValue();
            Integer hour = (Integer) jHourField.getValue();
            Integer day = (Integer) jDayField.getValue();

            for (Attack attack : attacksToModify) {
                long arrive = attack.getArriveTime().getTime();
                long diff = sec * 1000 + min * 60000 + hour * 3600000 + day * 86400000;
                //later if first index is selected
                //if later, add diff to arrival, else remove diff from arrival
                arrive += diff;
                attack.setArriveTime(new Date(arrive));
            }

        } else if (jModifyArrivalOption.isSelected()) {
            Calendar arrive = Calendar.getInstance();
            arrive.setTime(jDateField.getSelectedDate());
            if (jAdaptTypeBox.getSelectedIndex() == 0) {
                for (Attack attack : attacksToModify) {
                    attack.setArriveTime(arrive.getTime());
                }
            } else if (jAdaptTypeBox.getSelectedIndex() == 1) {//adapt date
                for (Attack attack : attacksToModify) {
                    Date d = attack.getArriveTime();
                    Calendar dc = Calendar.getInstance();
                    dc.setTime(d);
                    dc.set(Calendar.DAY_OF_MONTH, arrive.get(Calendar.DAY_OF_MONTH));
                    dc.set(Calendar.MONTH, arrive.get(Calendar.MONTH));
                    dc.set(Calendar.YEAR, arrive.get(Calendar.YEAR));
                    attack.setArriveTime(dc.getTime());
                }
            } else if (jAdaptTypeBox.getSelectedIndex() == 2) {//adapt time
                for (Attack attack : attacksToModify) {
                    Date d = attack.getArriveTime();
                    Calendar dc = Calendar.getInstance();
                    dc.setTime(d);
                    dc.set(Calendar.HOUR_OF_DAY, arrive.get(Calendar.HOUR_OF_DAY));
                    dc.set(Calendar.MINUTE, arrive.get(Calendar.MINUTE));
                    dc.set(Calendar.SECOND, arrive.get(Calendar.SECOND));
                    dc.set(Calendar.MILLISECOND, arrive.get(Calendar.MILLISECOND));
                    attack.setArriveTime(dc.getTime());
                }
            }
        } else if (jModifySendOption.isSelected()) {
            Calendar send = Calendar.getInstance();
            send.setTime(jDateField.getSelectedDate());
            if (jAdaptTypeBox.getSelectedIndex() == 0) {
                for (Attack attack : attacksToModify) {
                    attack.setSendTime(send.getTime());
                }
            } else if (jAdaptTypeBox.getSelectedIndex() == 1) {//adapt date
                for (Attack attack : attacksToModify) {
                    Date d = attack.getSendTime();
                    Calendar dc = Calendar.getInstance();
                    dc.setTime(d);
                    dc.set(Calendar.DAY_OF_MONTH, send.get(Calendar.DAY_OF_MONTH));
                    dc.set(Calendar.MONTH, send.get(Calendar.MONTH));
                    dc.set(Calendar.YEAR, send.get(Calendar.YEAR));
                    attack.setSendTime(dc.getTime());
                }
            } else if (jAdaptTypeBox.getSelectedIndex() == 2) {//adapt time
                for (Attack attack : attacksToModify) {
                    Date d = attack.getSendTime();
                    Calendar dc = Calendar.getInstance();
                    dc.setTime(d);
                    dc.set(Calendar.HOUR_OF_DAY, send.get(Calendar.HOUR_OF_DAY));
                    dc.set(Calendar.MINUTE, send.get(Calendar.MINUTE));
                    dc.set(Calendar.SECOND, send.get(Calendar.SECOND));
                    dc.set(Calendar.MILLISECOND, send.get(Calendar.MILLISECOND));
                    attack.setSendTime(dc.getTime());
                }
            }
        } else if (jRandomizeOption.isSelected()) {
            long rand = Long.parseLong(jRandomField.getText()) * 60 * 1000;
            
            TimeSpan span;
            for (Attack attack : attacksToModify) {
                boolean invalid = true;
                long arrive = attack.getArriveTime().getTime();
                long newArrive = 0;
                int tries = 0;
                while (invalid) {
                    //random until valid value was found
                    newArrive = (long) (arrive + (Math.random()*2*rand - rand));
                    span = new TimeSpan(new Date(newArrive));
                    
                    invalid = jNotRandomToNightBonus.isSelected() && span.intersectsWithNightBonus();
                    if(invalid) {
                        //check if this could possibly fit
                        span = new TimeSpan(Range.between(arrive-rand, arrive+rand), false);
                        logger.debug("Span: {}/{}", span, span.partlyOutOfNightBonus());
                        if(!span.partlyOutOfNightBonus()) {
                            //this span cannot be outside night bonus -> just ignore lazy user input
                            logger.warn("Ignoring Attack {} when changing arrive time since impossible", attack);
                            invalid = false;
                            newArrive = arrive;
                        }
                    }
                    tries++;
                    if(tries > 100) {
                        //to hard to find / a bug
                        logger.error("Unable to find new position for {} with {} in time Night: {}\n{}", arrive, rand,
                                jNotRandomToNightBonus.isSelected(), attack);
                        invalid = false;
                    }
                }
                attack.setArriveTime(new Date(newArrive));
            }
        }
        attackModel.fireTableDataChanged();
    }

    public void fireChangeUnitEvent() {
        int newType = -2;
        if (jAdeptTypeBox.isSelected()) {
            newType = (Integer) jTypeComboBox.getSelectedItem();
        }
        UnitHolder newUnit = null;
        if (jAdeptUnitBox.isSelected()) {
            newUnit = (UnitHolder) jUnitBox.getSelectedItem();
        }

        for (Attack attack : getSelectedAttacks()) {
            if (newType != -2) {
                attack.setType(newType);
                attack.setTroopsByType();
            }
            if (newUnit != null) {
                attack.setUnit(newUnit);
            }
        }
        attackModel.fireTableDataChanged();
    }

    public void cleanup() {
        List<Attack> elements = AttackManager.getSingleton().getAllElements(sAttackPlan);
        List<Attack> toRemove = new LinkedList<>();
        for (Attack a : elements) {
            if (a.getSendTime().getTime() < System.currentTimeMillis()) {
                toRemove.add(a);
            }
        }
        if (toRemove.isEmpty()) {
            return;
        }
        String message = (toRemove.size() == 1) ? trans.get("One_report_delete") : toRemove.size() + trans.get("Befehleentfernen");

        if (JOptionPaneHelper.showQuestionConfirmBox(this, message, trans.get("AbgelaufeneBefehleentfernen"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.NO_OPTION) {
            return;
        }

        logger.debug("Cleaning up " + toRemove.size() + " attacks");

        AttackManager.getSingleton().removeElements(sAttackPlan, toRemove);
        attackModel.fireTableDataChanged();
        showSuccess(toRemove.size() + trans.get("Befehleentfernt"));
    }

    public boolean deleteSelection(boolean pAsk) {
        List<Attack> selectedAttacks = getSelectedAttacks();

        if (pAsk) {
            String message = ((selectedAttacks.size() == 1) ? trans.get("Befehl") : (selectedAttacks.size() + trans.get("Befehle"))) + trans.get("wirklichloeschen");
            if (selectedAttacks.isEmpty() || JOptionPaneHelper.showQuestionConfirmBox(this, message, trans.get("Befehleloeschen"), trans.get("Nein"), trans.get("Ja")) != JOptionPane.YES_OPTION) {
                return false;
            }
        }

        jxAttackTable.editingCanceled(new ChangeEvent(this));
        AttackManager.getSingleton().removeElements(sAttackPlan, selectedAttacks);
        attackModel.fireTableDataChanged();
        showSuccess(selectedAttacks.size() + trans.get("Befehlegeloescht"));
        return true;
    }

    public void deleteSelection() {
        deleteSelection(true);
    }

    public void changeSelectionTime() {
        if (!getSelectedAttacks().isEmpty()) {
            jTimeChangeDialog.setVisible(true);
        } else {
            showInfo(trans.get("KeinBefehlgewaehlt"));
        }
    }

    public void changeSelectionType() {
        if (!getSelectedAttacks().isEmpty()) {

            jUnitBox.setModel(new DefaultComboBoxModel(DataHolder.getSingleton().getUnits().toArray(new UnitHolder[]{})));

            jChangeAttackTypeDialog.setLocationRelativeTo(this);
            jChangeAttackTypeDialog.pack();
            jChangeAttackTypeDialog.setVisible(true);
        } else {
            showInfo(trans.get("KeinBefehlgewaehlt"));
        }
    }

    public void setSelectionUnsent() {
        if (!getSelectedAttacks().isEmpty()) {
            for (Attack a : getSelectedAttacks()) {
                a.setTransferredToBrowser(false);
            }
            attackModel.fireTableDataChanged();
        } else {
            showInfo(trans.get("KeinBefehlgewaehlt"));
        }
    }

    public void changeSelectionDrawState() {
        if (!getSelectedAttacks().isEmpty()) {
            for (Attack a : getSelectedAttacks()) {
                a.setShowOnMap(!a.isShowOnMap());
            }
            attackModel.fireTableDataChanged();
        } else {
            showInfo(trans.get("KeinBefehlgewaehlt"));
        }
    }

    private void transferToSelectionTool() {
        List<Attack> selection = getSelectedAttacks();
        if (selection.isEmpty()) {
            showInfo(trans.get("KeinBefehlgewaehlt"));
            return;
        }
        List<Village> villages = new ArrayList<>();
        int result = JOptionPaneHelper.showQuestionThreeChoicesBox(this, 
                trans.get("HerkunftZieldoerfer"), 
                trans.get("Uebertragen"), 
                trans.get("Herkunft"), 
                trans.get("Ziele"), 
                trans.get("Abbrechen"));
        if (result == JOptionPane.YES_OPTION) {
            //target   
            for (Attack a : selection) {
                if (!villages.contains(a.getTarget())) {
                    villages.add(a.getTarget());
                }
            }
        } else if (result == JOptionPane.NO_OPTION) {
            //source
            for (Attack a : selection) {
                if (!villages.contains(a.getSource())) {
                    villages.add(a.getSource());
                }
            }
        } else {
            //none
            return;
        }
        DSWorkbenchSelectionFrame.getSingleton().updateSelection(villages);
        showInfo(villages.size() + ((villages.size() == 1) ? trans.get("Dorf") : trans.get("Doerfer")) + trans.get("In_die_auswahlueberischt"));
    }

    public void transferSelection(TRANSFER_TYPE pType) {
        switch (pType) {
            case COPY_TO_INTERNAL_CLIPBOARD:
                copyToInternalClipboard();
                break;
            case CUT_TO_INTERNAL_CLIPBOARD:
                cutToInternalClipboard();
                break;
            case FROM_INTERNAL_CLIPBOARD:
                copyFromInternalClipboard();
                break;
            case CLIPBOARD_PLAIN:
                copyPlainToExternalClipboardEvent();
                break;
            case CLIPBOARD_BB:
                copyBBToExternalClipboardEvent();
                break;
            case BROWSER_LINK:
                //use own thread against blocking of render thread
                new Thread(this::sendAttacksToBrowser).start();
                break;
            case FILE_HTML:
                copyHTMLToFileEvent();
                break;
            case FILE_TEXT:
                copyTextToFileEvent();
                break;
            case DSWB_RETIME:
                sendAttackToRetimeFrame();
                break;
            case SELECTION_TOOL:
                transferToSelectionTool();
                break;
        }
    }

    private void copyPlainToExternalClipboardEvent() {
        try {
            List<Attack> attacks = getSelectedAttacks();
            if (attacks.isEmpty()) {
                showInfo(trans.get("KeineBefehleausgewaehlt"));
                return;
            }
            StringBuilder buffer = new StringBuilder();
            for (Attack a : getSelectedAttacks()) {
                buffer.append(AttackToPlainTextFormatter.formatAttack(a)).append("\n");
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(buffer.toString()), null);
            String result = trans.get("DateninZwischenablagekopiert");
            showSuccess(result);
        } catch (Exception e) {
            logger.error("Failed to copy data to clipboard", e);
            String result = trans.get("FehlerbeimKopierenindieZwischenablage");
            // JOptionPaneHelper.showErrorBox(this, result, "Fehler");
            showError(result);
        }
    }

    private void copyBBToExternalClipboardEvent() {
        try {
            List<Attack> attacks = getSelectedAttacks();
            if (attacks.isEmpty()) {
                showInfo(trans.get("KeineBefehleausgewaehlt"));
                return;
            }
            
            String b = AttackListFormatter.AttackListToBBCodes(this, attacks, trans.get("Angriffsplan"));
            StringTokenizer t = new StringTokenizer(b, "[");
            int cnt = t.countTokens();
            if (cnt > 5000) {
                if (JOptionPaneHelper.showQuestionConfirmBox(this, trans.get("Befehletausendcodes"), trans.get("ZuvieleBBCodes"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(b), null);
            String result = trans.get("DateninZwischenablagekopiert");
            showSuccess(result);
        } catch (Exception e) {
            logger.error("Failed to copy data to clipboard", e);
            String result = trans.get("FehlerbeimKopierenindieZwischenablage");
            showError(result);
        }
    }

    private void copyHTMLToFileEvent() {
        List<Attack> toExport = getSelectedAttacks();

        if (toExport.isEmpty()) {
            showInfo(trans.get("KeineBefehleausgewaehlt"));
            return;
        }

        String dir = GlobalOptions.getProperty("screen.dir");
        if (dir == null) {
            dir = ".";
        }
        String selectedPlan = sAttackPlan;
        JFileChooser chooser = null;
        try {
            chooser = new JFileChooser(dir);
        } catch (Exception e) {
            JOptionPaneHelper.showErrorBox(this, trans.get("Dateiauswahldialog"), trans.get("Fehler"));
            return;
        }

        chooser.setDialogTitle(trans.get("Dateiauswaehlen"));

        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return (f != null) && (f.isDirectory() || f.getName().endsWith(".html"));
            }

            @Override
            public String getDescription() {
                return "*.html";
            }
        });
        chooser.setSelectedFile(new File(dir + "/" + selectedPlan + ".html"));
        int ret = chooser.showSaveDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {

            File f = null;
            try {
                f = chooser.getSelectedFile();
                String file = f.getCanonicalPath();
                if (!file.endsWith(".html")) {
                    file += ".html";
                }

                File target = new File(file);
                if (target.exists()) {
                    if (JOptionPaneHelper.showQuestionConfirmBox(this, trans.get("BestehendeDatei"), trans.get("Ueberschreiben"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.NO_OPTION) {
                        //do not overwrite
                        return;
                    }
                }

                AttackPlanHTMLExporter.doExport(target, selectedPlan, toExport);
                //store current directory
                GlobalOptions.addProperty("screen.dir", target.getParent());
                showSuccess(trans.get("Befehleerfolgreichgespeichert"));
                if (JOptionPaneHelper.showQuestionConfirmBox(this, trans.get("DateiBrowserbetrachten"), trans.get("Information"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.YES_OPTION) {
                    BrowserInterface.openPage(target.toURI().toURL().toString());
                }
            } catch (Exception e) {
                if (f != null) {
                    logger.error("Failed to write attacks to HTML file " + f.getPath(), e);
                } else {
                    logger.error("Failed to write attacks to HTML file <INVALID>", e);
                }
                showError(trans.get("FehlerSpeichernHTMLDatei"));
            }
        }
    }

    private void copyTextToFileEvent() {
        List<Attack> toExport = getSelectedAttacks();

        if (toExport.isEmpty()) {
            showInfo(trans.get("KeineBefehleausgewaehlt"));
            return;
        }

        AttacksToTextExportDialog dia = new AttacksToTextExportDialog(DSWorkbenchAttackFrame.getSingleton(), true);
        dia.setLocationRelativeTo(this);
        if (dia.setupAndShow(toExport)) {
            showSuccess(trans.get("Befehleerfolgreichgespeichert"));
        } else {
            showInfo(trans.get("AbbruchSpeichern"));
        }
    }

    private void sendAttacksToBrowser() {
        List<Attack> attacks = getSelectedAttacks();
        if (attacks.isEmpty()) {
            showInfo(trans.get("KeineBefehleausgewaehlt"));
            return;
        }
        int sentAttacks = 0;
        int ignoredAttacks = 0;
        int errors = 0;
        UserProfile profile = DSWorkbenchAttackFrame.getSingleton().getQuickProfile();

        for (Attack a : attacks) {
            try {
                if (!a.isTransferredToBrowser()) {
                    for(int i = 0; i < a.getMultiplier(); i++) {
                        if (BrowserInterface.sendAttack(a, profile)) {
                            a.setTransferredToBrowser(true);
                            sentAttacks++;
                        }
                    }
                } else {
                    ignoredAttacks++;
                }
            } catch(Exception e) {
                logger.error("Unhandled exception while sending attacks\n{}", a.toInternalRepresentation(), e);
                errors++;
            }
        }
        
        if (sentAttacks == 1) {
            jxAttackTable.getSelectionModel().setSelectionInterval(jxAttackTable.getSelectedRow() + 1, jxAttackTable.getSelectedRow() + 1);
        } else {
            jxAttackTable.getSelectionModel().setSelectionInterval(jxAttackTable.getSelectedRow() + sentAttacks, jxAttackTable.getSelectedRow() + sentAttacks);
        }

        String usedProfile = "";
        if (profile != null) {
            usedProfile = trans.get("als") + profile.toString();
        }
        String message = "<html>" + sentAttacks + trans.get("von") + attacks.size() + trans.get("Befehlen_HTML") + usedProfile + trans.get("Browseruebertragen_HTML");
        if(errors != 0) {
            message += "<br/>" + errors + trans.get("BefehleinternenFehler");
        }
        if (ignoredAttacks != 0) {
            message += "<br/>" + ignoredAttacks + trans.get("Befehleignoriert");
        }

        message += "</html>";
        showInfo(message);
    }

    private boolean copyToInternalClipboard() {
        List<Attack> selection = getSelectedAttacks();
        if (selection.isEmpty()) {
            showInfo(trans.get("KeinBefehlgewaehlt"));
            return false;
        }
        StringBuilder b = new StringBuilder();
        int cnt = 0;
        for (Attack a : selection) {
            b.append(a.toInternalRepresentation()).append("\n");
            cnt++;
        }
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(b.toString()), null);
            showSuccess(cnt + ((cnt == 1) ? trans.get("Befehlkopiert") : trans.get("Befehlekopiert")));
            return true;
        } catch (HeadlessException hex) {
            showError(trans.get("FehlerKopierenBefehle"));
            return false;
        }
    }

    private void cutToInternalClipboard() {
        int size = getSelectedAttacks().size();
        if (size == 0) {
            showInfo(trans.get("KeinBefehlgewaehlt"));
            return;
        }
        if (copyToInternalClipboard() && deleteSelection(false)) {
            showSuccess(size + ((size == 1) ? trans.get("Befehlausgeschnitten") : trans.get("Befehleausgeschnitten")));
        } else {
            showError(trans.get("FehlerAusschneidenBefehle"));
        }
    }

    private void copyFromInternalClipboard() {
        try {
            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);

            String[] lines = data.split("\n");
            int cnt = 0;
            for (String line : lines) {
                Attack a = Attack.fromInternalRepresentation(line);
                if (a != null) {
                    AttackManager.getSingleton().addManagedElement(sAttackPlan, a);
                    cnt++;
                }
            }
            showSuccess(cnt + ((cnt == 1) ? trans.get("Befehleingefuegt") : trans.get("Befehleeingefuegt")));
        } catch (UnsupportedFlavorException | IOException ufe) {
            logger.error("Failed to copy attacks from internal clipboard", ufe);
            showError(trans.get("FehlerEinfuegenBefehle"));
        }
        attackModel.fireTableDataChanged();
    }

    private void sendAttackToRetimeFrame() {
        if (getSelectedAttacks().isEmpty()) {
            showInfo(trans.get("KeinBefehlegewaehlt"));
            return;
        }
        Attack attack = getSelectedAttacks().get(0);

        StringBuilder b = new StringBuilder();
        b.append(trans.get("Herkunft_")).append(attack.getSource().toString()).append("\n");
        b.append(trans.get("Ziel_")).append(attack.getTarget().toString()).append("\n");
        SimpleDateFormat f = null;
        if (ServerSettings.getSingleton().isMillisArrival()) {
            f = TimeManager.getSimpleDateFormat("dd.MM.yy HH:mm:ss:SSS");
        } else {
            f = TimeManager.getSimpleDateFormat("dd.MM.yy HH:mm:ss");
        }
        b.append(trans.get("Ankunft:")).append(f.format(attack.getArriveTime())).append("\n");


        if (RetimerDataPanel.getSingleton().readAttackFromString(b.toString())) {
            showSuccess(trans.get("BefehlRetimeruebertragen"));
            TacticsPlanerWizard.show();
        } else {
            showError(trans.get("KeinAngriffsbefehlgefunden"));
        }
    }

    private List<Attack> getSelectedAttacks() {
        final List<Attack> selectedAttacks = new LinkedList<>();
        int[] selectedRows = jxAttackTable.getSelectedRows();
        if (selectedRows != null && selectedRows.length < 1) {
            return selectedAttacks;
        }
        for (Integer selectedRow : selectedRows) {
            Attack a = (Attack) AttackManager.getSingleton().getAllElements(sAttackPlan).get(jxAttackTable.convertRowIndexToModel(selectedRow));
            if (a != null) {
                selectedAttacks.add(a);
            }
        }
        return selectedAttacks;
    }

    public void reloadAttacksFromStd() {
        List<Attack> selectedAtts = getSelectedAttacks();
        
        for(Attack a: selectedAtts) {
            a.setTroopsByType();
        }
        
        attackModel.fireTableDataChanged();
    }
}
