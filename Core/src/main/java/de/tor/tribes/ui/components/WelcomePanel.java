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
package de.tor.tribes.ui.components;

import de.tor.tribes.ui.windows.DSWorkbenchMainFrame;
import de.tor.tribes.util.BrowserInterface;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.imageio.ImageIO;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.MattePainter;

/**
 *
 * @author Torridity
 */
public class WelcomePanel extends JXPanel {

    
    private Translator trans = TranslationManager.getTranslator("ui.components.WelcomePanel");
    
    private HashMap<JXLabel, String> welcomeTooltipMap = new HashMap<>();
    private BufferedImage back = null;

    /** Creates new form WelcomePanel */
    public WelcomePanel() {
        initComponents();
        setOpaque(true);
        welcomeTooltipMap.put(jxHelpLabel, trans.get("help_help"));
        welcomeTooltipMap.put(jxCommunityLabel, trans.get("community_help"));
        welcomeTooltipMap.put(jxIdeaLabel, trans.get("idea_help"));
        welcomeTooltipMap.put(jxFacebookLabel, trans.get("facebook_help"));
        welcomeTooltipMap.put(jContentLabel, trans.get("WillkommenbeiDSWorkbench"));
        try {
            back = ImageIO.read(WelcomePanel.class.getResource("/images/c.gif"));
        } catch (Exception ignored) {
        }
        if (back != null) {
            setBackgroundPainter(new MattePainter(new TexturePaint(back, new Rectangle2D.Float(0, 0, 200, 20))));
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

        jWelcomePane = new javax.swing.JPanel();
        jContentLabel = new org.jdesktop.swingx.JXLabel();
        jxHelpLabel = new org.jdesktop.swingx.JXLabel();
        jxCommunityLabel = new org.jdesktop.swingx.JXLabel();
        jxIdeaLabel = new org.jdesktop.swingx.JXLabel();
        jxFacebookLabel = new org.jdesktop.swingx.JXLabel();
        jDisableWelcome = new javax.swing.JCheckBox();
        jxCloseLabel = new org.jdesktop.swingx.JXLabel();

        setLayout(new java.awt.BorderLayout());

        jWelcomePane.setOpaque(false);
        jWelcomePane.setLayout(new java.awt.GridBagLayout());

        jContentLabel.setText(trans.get("WillkommenbeiDSWorkbench"));
        jContentLabel.setMinimumSize(new java.awt.Dimension(300, 300));
        jContentLabel.setPreferredSize(new java.awt.Dimension(300, 300));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jWelcomePane.add(jContentLabel, gridBagConstraints);

        jxHelpLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/128x128/help.png"))); // NOI18N
        jxHelpLabel.setEnabled(false);
        jxHelpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                fireMouseEnterLinkEvent(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                fireMouseExitLinkEvent(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                firePerformWelcomeActionEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jWelcomePane.add(jxHelpLabel, gridBagConstraints);

        jxCommunityLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/128x128/forum.png"))); // NOI18N
        jxCommunityLabel.setEnabled(false);
        jxCommunityLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                fireMouseEnterLinkEvent(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                fireMouseExitLinkEvent(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                firePerformWelcomeActionEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        jWelcomePane.add(jxCommunityLabel, gridBagConstraints);

        jxIdeaLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/128x128/idea.png"))); // NOI18N
        jxIdeaLabel.setEnabled(false);
        jxIdeaLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                fireMouseEnterLinkEvent(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                fireMouseExitLinkEvent(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                firePerformWelcomeActionEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        jWelcomePane.add(jxIdeaLabel, gridBagConstraints);

        jxFacebookLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/128x128/facebook.png"))); // NOI18N
        jxFacebookLabel.setEnabled(false);
        jxFacebookLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                fireMouseEnterLinkEvent(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                fireMouseExitLinkEvent(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                firePerformWelcomeActionEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        jWelcomePane.add(jxFacebookLabel, gridBagConstraints);

        jDisableWelcome.setText(trans.get("disable_next")
        );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jWelcomePane.add(jDisableWelcome, gridBagConstraints);

        jxCloseLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jxCloseLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jxCloseLabel.setText(trans.get("DankeDSWorkbenchverwenden"));
        jxCloseLabel.setEnabled(false);
        jxCloseLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jxCloseLabel.setMaximumSize(new java.awt.Dimension(319, 40));
        jxCloseLabel.setMinimumSize(new java.awt.Dimension(319, 40));
        jxCloseLabel.setPreferredSize(new java.awt.Dimension(319, 40));
        jxCloseLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                fireMouseEnterLinkEvent(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                fireMouseExitLinkEvent(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                firePerformWelcomeActionEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jWelcomePane.add(jxCloseLabel, gridBagConstraints);

        add(jWelcomePane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void fireMouseEnterLinkEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireMouseEnterLinkEvent
        JXLabel source = ((JXLabel) evt.getSource());
        source.setEnabled(true);
        String text = welcomeTooltipMap.get(source);
        if (text == null) {
            text = welcomeTooltipMap.get(jContentLabel);
        }

        jContentLabel.setText(text);
        repaint();
    }//GEN-LAST:event_fireMouseEnterLinkEvent

    private void fireMouseExitLinkEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireMouseExitLinkEvent
        ((JXLabel) evt.getSource()).setEnabled(false);
        jContentLabel.setText(welcomeTooltipMap.get(jContentLabel));
        repaint();
    }//GEN-LAST:event_fireMouseExitLinkEvent

    private void firePerformWelcomeActionEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_firePerformWelcomeActionEvent
        if (evt.getSource() == jxHelpLabel) {
            GlobalOptions.getHelpBroker().setDisplayed(true);
        } else if (evt.getSource() == jxCommunityLabel) {
            BrowserInterface.openPage("https://forum.die-staemme.de/index.php?threads/ds-workbench.80831/");
        } else if (evt.getSource() == jxIdeaLabel) {
            BrowserInterface.openPage("https://forum.die-staemme.de/index.php?threads/ds-workbench.80831/");
        } else if (evt.getSource() == jxFacebookLabel) {
            BrowserInterface.openPage("http://www.facebook.com/pages/DS-Workbench/182068775185568");
        } else if (evt.getSource() == jxCloseLabel) {
            //hide welcome page

            GlobalOptions.addProperty("no.welcome", Boolean.toString(jDisableWelcome.isSelected()));
            DSWorkbenchMainFrame.getSingleton().hideWelcomePage();
        }
    }//GEN-LAST:event_firePerformWelcomeActionEvent
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXLabel jContentLabel;
    private javax.swing.JCheckBox jDisableWelcome;
    private javax.swing.JPanel jWelcomePane;
    private org.jdesktop.swingx.JXLabel jxCloseLabel;
    private org.jdesktop.swingx.JXLabel jxCommunityLabel;
    private org.jdesktop.swingx.JXLabel jxFacebookLabel;
    private org.jdesktop.swingx.JXLabel jxHelpLabel;
    private org.jdesktop.swingx.JXLabel jxIdeaLabel;
    // End of variables declaration//GEN-END:variables
}
