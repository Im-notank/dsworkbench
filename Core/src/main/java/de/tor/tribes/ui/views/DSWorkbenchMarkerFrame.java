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
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.panels.GenericTestPanel;
import de.tor.tribes.ui.panels.MarkerTableTab;
import de.tor.tribes.ui.windows.AbstractDSWorkbenchFrame;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.PropertyHelper;
import de.tor.tribes.util.mark.MarkerManager;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTaskPane;

/**
 * @author  Charon
 */
public class DSWorkbenchMarkerFrame extends AbstractDSWorkbenchFrame implements GenericManagerListener, ActionListener {

    private static Translator trans = TranslationManager.getTranslator("ui.views.DSWorkbenchMarkerFrame");
    
    private static Logger logger = LogManager.getLogger("MarkerView");
    private static DSWorkbenchMarkerFrame SINGLETON = null;
    private GenericTestPanel centerPanel = null;

    @Override
    public void actionPerformed(ActionEvent e) {
        MarkerTableTab activeTab = getActiveTab();
        if (e.getActionCommand() != null && activeTab != null) {
            switch (e.getActionCommand()) {
                case "BBCopy":
                    activeTab.transferSelection(MarkerTableTab.TRANSFER_TYPE.CLIPBOARD_BB);
                    break;
                case "Cut":
                    activeTab.transferSelection(MarkerTableTab.TRANSFER_TYPE.CUT_TO_INTERNAL_CLIPBOARD);
                    break;
                case "Copy":
                    activeTab.transferSelection(MarkerTableTab.TRANSFER_TYPE.COPY_TO_INTERNAL_CLIPBOARD);
                    break;
                case "Paste":
                    activeTab.transferSelection(MarkerTableTab.TRANSFER_TYPE.FROM_INTERNAL_CLIPBOARD);
                    break;
                case "Delete":
                    activeTab.deleteSelection(true);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void dataChangedEvent() {
        generateMarkerTabs();
    }

    @Override
    public void dataChangedEvent(String pGroup) {
        MarkerTableTab tab = getActiveTab();
        if (tab != null) {
            tab.updateSet();
        }
    }

    /** Creates new form DSWorkbenchMarkerFrame */
    DSWorkbenchMarkerFrame() {
        initComponents();
        if (!GlobalOptions.isMinimal()) {
            centerPanel = new GenericTestPanel(true);
        } else {
            centerPanel = new GenericTestPanel(false);
        }
        jMarkersPanel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setChildComponent(jXMarkerPanel);
        if (!GlobalOptions.isMinimal()) {
            buildMenu();
        }
        capabilityInfoPanel1.addActionListener(this);

        jMarkerTabPane.getModel().addChangeListener((ChangeEvent e) -> {
            MarkerTableTab activeTab = getActiveTab();
            if (activeTab != null) {
                activeTab.updateSet();
            }
        });

        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        if (!Constants.DEBUG) {
            GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.markers_view", GlobalOptions.getHelpBroker().getHelpSet());
        }
// </editor-fold>
        pack();
    }

    private void buildMenu() {
        JXTaskPane editPane = new JXTaskPane();
        editPane.setTitle(trans.get("Bearbeiten"));
        JXButton showButton = new JXButton(new ImageIcon(DSWorkbenchTagFrame.class.getResource("/res/ui/eye_large.png")));

        showButton.setToolTipText(trans.get("BlendetHauptkarteein"));
        showButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                MarkerTableTab tab = getActiveTab();
                if (tab != null) {
                    tab.changeVisibility(true);
                }
            }
        });
        editPane.getContentPane().add(showButton);
        JXButton hideButton = new JXButton(new ImageIcon(DSWorkbenchTagFrame.class.getResource("/res/ui/eye_forbidden_large.png")));

        hideButton.setToolTipText(trans.get("BlendetHauptkarteaus"));
        hideButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                MarkerTableTab tab = getActiveTab();
                if (tab != null) {
                    tab.changeVisibility(false);
                }
            }
        });
        editPane.getContentPane().add(hideButton);


        centerPanel.setupTaskPane(editPane);
    }

    @Override
    public void toBack() {
        jMarkerFrameAlwaysOnTop.setSelected(false);
        fireMarkerFrameOnTopEvent(null);
        super.toBack();
    }

    @Override
    public void storeCustomProperties(Configuration pConfig) {
        pConfig.setProperty(getPropertyPrefix() + ".menu.visible", centerPanel.isMenuVisible());
        pConfig.setProperty(getPropertyPrefix() + ".alwaysOnTop", jMarkerFrameAlwaysOnTop.isSelected());

        int selectedIndex = jMarkerTabPane.getModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            pConfig.setProperty(getPropertyPrefix() + ".tab.selection", selectedIndex);
        }

        MarkerTableTab tab = ((MarkerTableTab) jMarkerTabPane.getComponentAt(0));
        PropertyHelper.storeTableProperties(tab.getMarkerTable(), pConfig, getPropertyPrefix());
    }

    @Override
    public void restoreCustomProperties(Configuration pConfig) {
        centerPanel.setMenuVisible(pConfig.getBoolean(getPropertyPrefix() + ".menu.visible", true));
        try {
            jMarkerTabPane.setSelectedIndex(pConfig.getInteger(getPropertyPrefix() + ".tab.selection", 0));
        } catch (Exception ignored) {
        }
        try {
            jMarkerFrameAlwaysOnTop.setSelected(pConfig.getBoolean(getPropertyPrefix() + ".alwaysOnTop"));
        } catch (Exception ignored) {
        }

        setAlwaysOnTop(jMarkerFrameAlwaysOnTop.isSelected());

        MarkerTableTab tab = ((MarkerTableTab) jMarkerTabPane.getComponentAt(0));
        PropertyHelper.restoreTableProperties(tab.getMarkerTable(), pConfig, getPropertyPrefix());
    }

    @Override
    public String getPropertyPrefix() {
        return "marker.view";
    }

    public static synchronized DSWorkbenchMarkerFrame getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchMarkerFrame();
        }
        return SINGLETON;
    }

    /**Get the currently selected tab*/
    private MarkerTableTab getActiveTab() {
        try {
            if (jMarkerTabPane.getModel().getSelectedIndex() < 0) {
                return null;
            }
            return ((MarkerTableTab) jMarkerTabPane.getComponentAt(jMarkerTabPane.getModel().getSelectedIndex()));
        } catch (ClassCastException cce) {
            return null;
        }
    }

    /**Initialize and add one tab for each marker set to jTabbedPane1*/
    public void generateMarkerTabs() {
        while (jMarkerTabPane.getTabCount() > 0) {
            MarkerTableTab tab = (MarkerTableTab) jMarkerTabPane.getComponentAt(0);
            tab.deregister();
            jMarkerTabPane.removeTabAt(0);
        }

        LabelUIResource lr = new LabelUIResource();
        lr.setLayout(new BorderLayout());
        lr.add(jNewPlanPanel, BorderLayout.CENTER);
        String[] plans = MarkerManager.getSingleton().getGroups();

        //insert default tab to first place
        for (String plan : plans) {
            MarkerTableTab tab = new MarkerTableTab(plan, this);
            jMarkerTabPane.addTab(plan, tab);
        }

        jMarkerTabPane.setSelectedIndex(0);
        MarkerTableTab tab = getActiveTab();
        if (tab != null) {
            tab.updateSet();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jXMarkerPanel = new org.jdesktop.swingx.JXPanel();
        jMarkerTabPane = new javax.swing.JTabbedPane();
        jNewPlanPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jMarkerFrameAlwaysOnTop = new javax.swing.JCheckBox();
        jMarkersPanel = new javax.swing.JPanel();
        capabilityInfoPanel1 = new de.tor.tribes.ui.components.CapabilityInfoPanel();

        jXMarkerPanel.setLayout(new java.awt.BorderLayout());
        jXMarkerPanel.add(jMarkerTabPane, java.awt.BorderLayout.CENTER);

        jNewPlanPanel.setOpaque(false);
        jNewPlanPanel.setLayout(new java.awt.BorderLayout());

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/document_new_24x24.png"))); // NOI18N
        jLabel3.setToolTipText(trans.get("LeeresMarkierungsseterstellen"));
        jLabel3.setEnabled(false);
        jLabel3.setMaximumSize(new java.awt.Dimension(40, 40));
        jLabel3.setMinimumSize(new java.awt.Dimension(40, 40));
        jLabel3.setPreferredSize(new java.awt.Dimension(40, 40));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                fireEnterEvent(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                fireMouseExitEvent(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireCreateMarkerSetEvent(evt);
            }
        });
        jNewPlanPanel.add(jLabel3, java.awt.BorderLayout.CENTER);

        setTitle(trans.get("Markierungen"));
        setMinimumSize(new java.awt.Dimension(400, 300));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jMarkerFrameAlwaysOnTop.setText(trans.get("ImmerimVordergrund"));
        jMarkerFrameAlwaysOnTop.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireMarkerFrameOnTopEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jMarkerFrameAlwaysOnTop, gridBagConstraints);

        jMarkersPanel.setBackground(new java.awt.Color(239, 235, 223));
        jMarkersPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jMarkersPanel, gridBagConstraints);

        capabilityInfoPanel1.setSearchable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(capabilityInfoPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireMarkerFrameOnTopEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireMarkerFrameOnTopEvent
    setAlwaysOnTop(!isAlwaysOnTop());
}//GEN-LAST:event_fireMarkerFrameOnTopEvent

private void fireEnterEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireEnterEvent
    jLabel3.setEnabled(true);
}//GEN-LAST:event_fireEnterEvent

private void fireMouseExitEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireMouseExitEvent
    jLabel3.setEnabled(false);
}//GEN-LAST:event_fireMouseExitEvent

private void fireCreateMarkerSetEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCreateMarkerSetEvent
    int unusedId = 1;
    while (unusedId < 1000) {
        if (MarkerManager.getSingleton().addGroup(trans.get("NeuesSet") + unusedId)) {
            break;
        }
        unusedId++;
    }
    if (unusedId == 1000) {
        JOptionPaneHelper.showErrorBox(DSWorkbenchMarkerFrame.this, trans.get("tausendeMarkierungssets"), trans.get("Fehler"));
    }
}//GEN-LAST:event_fireCreateMarkerSetEvent

    /**Setup marker panel*/
    @Override
    public void resetView() {
        MarkerManager.getSingleton().addManagerListener(this);
        generateMarkerTabs();
    }

    @Override
    public void fireVillagesDraggedEvent(List<Village> pVillages, Point pDropLocation) {
    }
    
    // <editor-fold defaultstate="collapsed" desc="Gesture handling">

    @Override
    public void fireNextPageGestureEvent() {
        int current = jMarkerTabPane.getSelectedIndex();
        int size = jMarkerTabPane.getTabCount();
        if (current + 1 > size - 1) {
            current = 0;
        } else {
            current += 1;
        }
        jMarkerTabPane.setSelectedIndex(current);
    }

    @Override
    public void firePreviousPageGestureEvent() {
        int current = jMarkerTabPane.getSelectedIndex();
        int size = jMarkerTabPane.getTabCount();
        if (current - 1 < 0) {
            current = size - 1;
        } else {
            current -= 1;
        }
        jMarkerTabPane.setSelectedIndex(current);
    }

    @Override
    public void fireRenameGestureEvent() {
        int idx = jMarkerTabPane.getSelectedIndex();
        if (idx != 0) {
            jMarkerTabPane.setSelectedIndex(idx);
        }
    }
// </editor-fold>
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.tor.tribes.ui.components.CapabilityInfoPanel capabilityInfoPanel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JCheckBox jMarkerFrameAlwaysOnTop;
    private javax.swing.JTabbedPane jMarkerTabPane;
    private javax.swing.JPanel jMarkersPanel;
    private javax.swing.JPanel jNewPlanPanel;
    private org.jdesktop.swingx.JXPanel jXMarkerPanel;
    // End of variables declaration//GEN-END:variables
}
