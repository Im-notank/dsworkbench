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

import de.tor.tribes.control.GenericManagerListener;
import de.tor.tribes.types.ext.Tribe;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.editors.BuildingLevelCellEditor;
import de.tor.tribes.ui.models.ChurchTableModel;
import de.tor.tribes.ui.panels.GenericTestPanel;
import de.tor.tribes.ui.renderer.BuildingLevelCellRenderer;
import de.tor.tribes.ui.renderer.ColorCellRenderer;
import de.tor.tribes.ui.renderer.DefaultTableHeaderRenderer;
import de.tor.tribes.ui.windows.AbstractDSWorkbenchFrame;
import de.tor.tribes.ui.windows.DSWorkbenchMainFrame;
import de.tor.tribes.util.*;
import de.tor.tribes.util.mark.MarkerManager;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import de.tor.tribes.util.village.KnownVillage;
import de.tor.tribes.util.village.KnownVillageManager;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.HorizontalAlignment;
import org.jdesktop.swingx.painter.AbstractLayoutPainter.VerticalAlignment;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;

/**
 * @author Charon
 */
public class DSWorkbenchChurchFrame extends AbstractDSWorkbenchFrame implements GenericManagerListener, ListSelectionListener {

    @Override
    public void dataChangedEvent() {
        dataChangedEvent(null);
    }

    @Override
    public void dataChangedEvent(String pGroup) {
        ((ChurchTableModel) jChurchTable.getModel()).fireTableDataChanged();
    }
    private final static Logger logger = LogManager.getLogger("ChurchView");
    private static DSWorkbenchChurchFrame SINGLETON = null;
    private GenericTestPanel centerPanel = null;

    private static Translator trans = TranslationManager.getTranslator("ui.models.DSWorkbenchChurchFrame");
    
    public static synchronized DSWorkbenchChurchFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchChurchFrame();
        }
        return SINGLETON;
    }

    /**
     * Creates new form DSWorkbenchChurchFrame
     */
    DSWorkbenchChurchFrame() {
        initComponents();
        centerPanel = new GenericTestPanel();
        jChurchPanel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setChildComponent(jXPanel1);
        buildMenu();

        KeyStroke delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
        KeyStroke bbCopy = KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK, false);

        ActionListener listener = (ActionEvent e) -> {
            if ("Delete".equals(e.getActionCommand())) {
                deleteSelection();
            } else if ("BBCopy".equals(e.getActionCommand())) {
                bbCopySelection();
            }
        };
        capabilityInfoPanel1.addActionListener(listener);
        jChurchTable.registerKeyboardAction(listener, "Delete", delete, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        jChurchTable.registerKeyboardAction(listener, "BBCopy", bbCopy, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        jChurchFrameAlwaysOnTop.setSelected(GlobalOptions.getProperties().getBoolean("church.frame.alwaysOnTop"));
        setAlwaysOnTop(jChurchFrameAlwaysOnTop.isSelected());

        jChurchTable.setModel(new ChurchTableModel());
        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        if (!Constants.DEBUG) {
            GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.church_view", GlobalOptions.getHelpBroker().getHelpSet());
        }
        // </editor-fold>
        jChurchTable.getSelectionModel().addListSelectionListener(DSWorkbenchChurchFrame.this);
        pack();
    }

    @Override
    public void toBack() {
        jChurchFrameAlwaysOnTop.setSelected(false);
        fireChurchFrameOnTopEvent(null);
        super.toBack();
    }

    @Override
    public void storeCustomProperties(Configuration pConfig) {
        pConfig.setProperty(getPropertyPrefix() + ".menu.visible", centerPanel.isMenuVisible());
        pConfig.setProperty(getPropertyPrefix() + ".alwaysOnTop", jChurchFrameAlwaysOnTop.isSelected());

        PropertyHelper.storeTableProperties(jChurchTable, pConfig, getPropertyPrefix());

    }

    @Override
    public void restoreCustomProperties(Configuration pConfig) {
        centerPanel.setMenuVisible(pConfig.getBoolean(getPropertyPrefix() + ".menu.visible", true));

        try {
            jChurchFrameAlwaysOnTop.setSelected(pConfig.getBoolean(getPropertyPrefix() + ".alwaysOnTop"));
        } catch (Exception ignored) {
        }

        setAlwaysOnTop(jChurchFrameAlwaysOnTop.isSelected());

        PropertyHelper.restoreTableProperties(jChurchTable, pConfig, getPropertyPrefix());
    }

    @Override
    public String getPropertyPrefix() {
        return "church.view";
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

        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        infoPanel = new org.jdesktop.swingx.JXCollapsiblePane();
        jXLabel1 = new org.jdesktop.swingx.JXLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jChurchPanel = new org.jdesktop.swingx.JXPanel();
        jChurchFrameAlwaysOnTop = new javax.swing.JCheckBox();
        capabilityInfoPanel1 = new de.tor.tribes.ui.components.CapabilityInfoPanel();

        jXPanel1.setLayout(new java.awt.BorderLayout());

        infoPanel.setCollapsed(true);
        infoPanel.setInheritAlpha(false);

        jXLabel1.setText(trans.get("KeineMeldung"));
        jXLabel1.setOpaque(true);
        jXLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jXLabel1fireHideInfoEvent(evt);
            }
        });
        infoPanel.add(jXLabel1, java.awt.BorderLayout.CENTER);

        jXPanel1.add(infoPanel, java.awt.BorderLayout.SOUTH);

        jChurchTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jChurchTable);

        jXPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        setTitle(trans.get("Kirchen"));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jChurchPanel.setBackground(new java.awt.Color(239, 235, 223));
        jChurchPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 500;
        gridBagConstraints.ipady = 300;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jChurchPanel, gridBagConstraints);

        jChurchFrameAlwaysOnTop.setText(trans.get("ImmerimVordergrund"));
        jChurchFrameAlwaysOnTop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireChurchFrameOnTopEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jChurchFrameAlwaysOnTop, gridBagConstraints);

        capabilityInfoPanel1.setCopyable(false);
        capabilityInfoPanel1.setPastable(false);
        capabilityInfoPanel1.setSearchable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(capabilityInfoPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fireChurchFrameOnTopEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireChurchFrameOnTopEvent
        setAlwaysOnTop(!isAlwaysOnTop());
    }//GEN-LAST:event_fireChurchFrameOnTopEvent

    private void jXLabel1fireHideInfoEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jXLabel1fireHideInfoEvent
        infoPanel.setCollapsed(true);
}//GEN-LAST:event_jXLabel1fireHideInfoEvent

    private void buildMenu() {
        JXTaskPane transferPane = new JXTaskPane();
        transferPane.setTitle(trans.get("Uebertragen"));
        JXButton transferVillageList = new JXButton(new ImageIcon(DSWorkbenchChurchFrame.class.getResource("/res/ui/center_ingame.png")));
        transferVillageList.setToolTipText(trans.get("ZentriertdasKirchendorfimSpiel"));
        transferVillageList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                centerChurchInGame();
            }
        });
        transferPane.getContentPane().add(transferVillageList);

        if (!GlobalOptions.isMinimal()) {
            JXButton button = new JXButton(new ImageIcon(DSWorkbenchChurchFrame.class.getResource("/res/center_24x24.png")));
            button.setToolTipText(trans.get("ZentriertdasKirchendorfHauptkarte"));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    centerChurchVillage();
                }
            });

            transferPane.getContentPane().add(button);
        }
        centerPanel.setupTaskPane(transferPane);
    }

    private KnownVillage getSelectedCurch() {
        int row = jChurchTable.getSelectedRow();
        if (row >= 0) {
            try {
                return (KnownVillage) jChurchTable.getModel().getValueAt(jChurchTable.convertRowIndexToModel(row), 1);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private void centerChurchVillage() {
        KnownVillage v = getSelectedCurch();
        if (v != null) {
            DSWorkbenchMainFrame.getSingleton().centerVillage(v.getVillage());
        } else {
            showInfo(trans.get("KeineKirchegewaehlt"));
        }
    }

    private void centerChurchInGame() {
        KnownVillage v = getSelectedCurch();
        if (v != null) {
            BrowserInterface.centerVillage(v.getVillage());
        } else {
            showInfo(trans.get("KeineKirchegewaehlt"));
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            int selectionCount = jChurchTable.getSelectedRowCount();
            if (selectionCount != 0) {
                showInfo(selectionCount + ((selectionCount == 1) ? trans.get("Kirchegewaehlt") : trans.get("Kirchegewaehlt")));
            }
        }
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

    public void showSuccess(String pMessage) {
        infoPanel.setCollapsed(false);
        jXLabel1.setBackgroundPainter(new MattePainter(Color.GREEN));
        jXLabel1.setForeground(Color.BLACK);
        jXLabel1.setText(pMessage);
    }

    private void deleteSelection() {
        int[] rows = jChurchTable.getSelectedRows();
        if (rows.length == 0) {
            return;
        }
        String message = ((rows.length == 1) ? trans.get("Kirchendorf") : (rows.length + trans.get("Kirchendoerfer"))) + trans.get("wirklichloeschen");
        if (JOptionPaneHelper.showQuestionConfirmBox(this, message, trans.get("Loeschen"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.YES_OPTION) {
            //get markers to remove
            List<Village> toRemove = new LinkedList<>();
            jChurchTable.invalidate();
            for (int i = rows.length - 1; i >= 0; i--) {
                int row = jChurchTable.convertRowIndexToModel(rows[i]);
                int col = jChurchTable.convertColumnIndexToModel(1);
                Village v = ((KnownVillage) jChurchTable.getModel()
                        .getValueAt(row, col)).getVillage();
                toRemove.add(v);
            }
            jChurchTable.revalidate();
            //remove all selected markers and update the view once
            KnownVillageManager.getSingleton().removeChurches(toRemove.toArray(new Village[]{}), true);
            showSuccess(toRemove.size() + ((toRemove.size() == 1) ? trans.get("Kirchegeloescht") : trans.get("Kirchengeloescht")));
        }
    }

    private void bbCopySelection() {
        try {
            int[] rows = jChurchTable.getSelectedRows();
            if (rows.length == 0) {
                return;
            }

            boolean extended = (JOptionPaneHelper.showQuestionConfirmBox(this, trans.get("ErweiterterBBCodeverwendet"), trans.get("ErweiterterBBCode"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.YES_OPTION);

            StringBuilder buffer = new StringBuilder();
            if (extended) {
                buffer.append(trans.get("BBKirchendoerfer"));
            } else {
                buffer.append(trans.get("BBKirche"));
            }

            buffer.append("[table]\n");
            buffer.append(trans.get("BBPlayerVilRadi"));


            for (int row1 : rows) {
                int row = jChurchTable.convertRowIndexToModel(row1);
                int tribeCol = jChurchTable.convertColumnIndexToModel(0);
                int villageCol = jChurchTable.convertColumnIndexToModel(1);
                int rangeCol = jChurchTable.convertColumnIndexToModel(2);
                buffer.append("[*]").
                        append(((Tribe) jChurchTable.getModel().getValueAt(row, tribeCol)).toBBCode()).
                        append("[|]").
                        append(((Village) jChurchTable.getModel().getValueAt(row, villageCol)).toBBCode()).
                        append("[|]").
                        append(jChurchTable.getModel().getValueAt(row, rangeCol)).
                        append("\n");
            }

            buffer.append("[/table]");

            if (extended) {
                buffer.append(trans.get("Erstelltamsize"));
                buffer.append(TimeManager.getSimpleDateFormat("dd.MM.yy 'um' HH:mm:ss").format(new Date()));
                buffer.append(trans.get("mitDSWorkbench"));
                buffer.append(Constants.VERSION).append(Constants.VERSION_ADDITION + "[/size]\n");
            } else {
                buffer.append(trans.get("Erstelltam"));
                buffer.append(TimeManager.getSimpleDateFormat("dd.MM.yy 'um' HH:mm:ss").format(new Date()));
                buffer.append(trans.get("mitDSWorkbench"));
                buffer.append(Constants.VERSION).append(Constants.VERSION_ADDITION + "\n");
            }

            String b = buffer.toString();
            StringTokenizer t = new StringTokenizer(b, "[");
            int cnt = t.countTokens();
            if (cnt > 5000) {
                if (JOptionPaneHelper.showQuestionConfirmBox(this, trans.get("tausendeBBCode"), trans.get("ZuvieleBBCodes"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(b), null);
            String result = trans.get("DatenZwischenablage");
            showSuccess(result);
        } catch (Exception e) {
            logger.error("Failed to copy data to clipboard", e);
            String result = trans.get("FehlerbeimKopieren");
            showError(result);
        }
    }

    @Override
    public void resetView() {
        KnownVillageManager.getSingleton().addManagerListener(this);
        MarkerManager.getSingleton().addManagerListener(this);
        jChurchTable.getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());
        UIHelper.initTableColums(jChurchTable, 
                trans.getRaw("ui.models.ChurchTableModel.Stufe"), 
                trans.getRaw("ui.models.ChurchTableModel.Farbe"));

        ((ChurchTableModel) jChurchTable.getModel()).fireTableDataChanged();
    }

    @Override
    public void fireVillagesDraggedEvent(List<Village> pVillages, Point pDropLocation) {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.components.CapabilityInfoPanel capabilityInfoPanel1;
    private org.jdesktop.swingx.JXCollapsiblePane infoPanel;
    private javax.swing.JCheckBox jChurchFrameAlwaysOnTop;
    private org.jdesktop.swingx.JXPanel jChurchPanel;
    private static final org.jdesktop.swingx.JXTable jChurchTable = new org.jdesktop.swingx.JXTable();
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXLabel jXLabel1;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    // End of variables declaration//GEN-END:variables

    static {
        HighlightPredicate.ColumnHighlightPredicate colu = new HighlightPredicate.ColumnHighlightPredicate(0, 1, 2);
        jChurchTable.setHighlighters(new CompoundHighlighter(colu, HighlighterFactory.createAlternateStriping(Constants.DS_ROW_A, Constants.DS_ROW_B)));

        jChurchTable.setColumnControlVisible(true);
        jChurchTable.setDefaultRenderer(Color.class, new ColorCellRenderer());
        jChurchTable.setDefaultRenderer(Integer.class, new BuildingLevelCellRenderer());
        jChurchTable.setDefaultEditor(Integer.class, new BuildingLevelCellEditor());
        
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
        jChurchTable.addHighlighter(new PainterHighlighter(HighlightPredicate.EDITABLE, new ImagePainter(back, HorizontalAlignment.RIGHT, VerticalAlignment.TOP)));
    }
}
