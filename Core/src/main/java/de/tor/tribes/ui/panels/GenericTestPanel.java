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

import de.tor.tribes.ui.components.CollapseExpandTrigger;
import org.jdesktop.swingx.JXTaskPaneContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Torridity
 */
public class GenericTestPanel extends javax.swing.JPanel {

    private JComponent centerComponent = null;
    private boolean menuEnabled = true;
    private JXTaskPaneContainer taskContainer = null;

    /** Creates new form GenericTestPanel */
    public GenericTestPanel(boolean menuEnabled) {
        this.menuEnabled = menuEnabled;
        initComponents();
        if (menuEnabled) {
            CollapseExpandTrigger trigger = new CollapseExpandTrigger();
            trigger.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent e) {
                    menuPanel.setCollapsed(!menuPanel.isCollapsed());
                }
            });
            menuCollapsePanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
            menuCollapsePanel.add(trigger, BorderLayout.CENTER);
        } else {
            remove(menuPanel);
            centerPanel.remove(menuCollapsePanel);
        }

    }

    /** Creates new form GenericTestPanel */
    public GenericTestPanel() {
        this(true);
    }

    public void setMenuVisible(boolean pValue) {
        menuPanel.setCollapsed(!pValue);
    }

    public boolean isMenuVisible() {
        return !menuPanel.isCollapsed();
    }

    public void setupTaskPane(JComponent... pTaskPane) {
        taskContainer = new JXTaskPaneContainer();
        for (JComponent aPTaskPane : pTaskPane) {
            taskContainer.add(aPTaskPane);
        }
        menuPanel.remove(jXTaskPaneContainer1);
        JScrollPane s = new JScrollPane(taskContainer);
        s.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        menuPanel.add(s, BorderLayout.CENTER);
        taskContainer.setBackground(getBackground());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuPanel = new org.jdesktop.swingx.JXCollapsiblePane();
        jXTaskPaneContainer1 = new org.jdesktop.swingx.JXTaskPaneContainer();
        centerPanel = new org.jdesktop.swingx.JXPanel();
        menuCollapsePanel = new org.jdesktop.swingx.JXPanel();

        setPreferredSize(new java.awt.Dimension(190, 100));
        setLayout(new java.awt.BorderLayout());

        menuPanel.setAnimated(false);
        menuPanel.setDirection(org.jdesktop.swingx.JXCollapsiblePane.Direction.RIGHT);
        menuPanel.setInheritAlpha(false);

        jXTaskPaneContainer1.setBackground(new java.awt.Color(240, 240, 240));
        jXTaskPaneContainer1.setMinimumSize(new java.awt.Dimension(170, 10));
        jXTaskPaneContainer1.setPreferredSize(new java.awt.Dimension(170, 10));
        menuPanel.add(jXTaskPaneContainer1, java.awt.BorderLayout.CENTER);

        add(menuPanel, java.awt.BorderLayout.EAST);

        centerPanel.setPreferredSize(new java.awt.Dimension(20, 20));
        centerPanel.setLayout(new java.awt.BorderLayout());

        menuCollapsePanel.setBackground(new java.awt.Color(204, 204, 204));
        menuCollapsePanel.setPreferredSize(new java.awt.Dimension(20, 473));
        menuCollapsePanel.setLayout(new java.awt.BorderLayout());
        centerPanel.add(menuCollapsePanel, java.awt.BorderLayout.EAST);

        add(centerPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXPanel centerPanel;
    private org.jdesktop.swingx.JXTaskPaneContainer jXTaskPaneContainer1;
    private org.jdesktop.swingx.JXPanel menuCollapsePanel;
    private org.jdesktop.swingx.JXCollapsiblePane menuPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the childPanel
     */
    public JComponent getCenterComponent() {
        return centerComponent;
    }

    /**
     * @param childComponent the childComponent to set
     */
    public void setChildComponent(JComponent childComponent) {
        this.centerComponent = childComponent;
        centerPanel.removeAll();
        if (menuEnabled) {
            centerPanel.add(menuCollapsePanel, java.awt.BorderLayout.EAST);
        }
        if (childComponent != null) {
            centerPanel.add(childComponent, BorderLayout.CENTER);
        }
    }
}