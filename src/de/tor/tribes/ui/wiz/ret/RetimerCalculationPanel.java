/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AttackSourcePanel.java
 *
 * Created on Oct 15, 2011, 9:54:36 AM
 */
package de.tor.tribes.ui.wiz.ret;

import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Attack;
import de.tor.tribes.types.UserProfile;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.wiz.ret.types.RETSourceElement;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.GlobalOptions;
import de.tor.tribes.util.JOptionPaneHelper;
import de.tor.tribes.util.TroopHelper;
import de.tor.tribes.util.troops.TroopsManager;
import de.tor.tribes.util.troops.VillageTroopsHolder;
import java.awt.BorderLayout;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.netbeans.spi.wizard.*;

/**
 *
 * @author Torridity
 */
public class RetimerCalculationPanel extends WizardPage {

    private static Logger logger = Logger.getLogger("Retimer-Calculation");
    private static final String GENERAL_INFO = "In diesem Schritt können alle verfügbaren Retimes berechnet werden. Dabei wird DS Workbench versuchen,<br/>"
            + "die angegebenen Herkunftsdörfer so zu timen, dass sie die angreifenden Dörfer erreichen sobald die Truppen aus diesen Dörfern zurückkehren.<br/>"
            + "Da ein Angriff auch mal schiefgehen kann, berechnet DS Workbench für jedes Herkunfts- und Zieldorf verschiedene Kombinationen, die von der verwendeten<br/>"
            + "Einheit und somit der Laufzeit abhängen. Am Ende kann natürlich nur ein Angriff tatsächlich durchgeführt werden. Die Entscheidung womit du retimen<br/>"
            + "möchtest liegt allein bei dir.";
    private static RetimerCalculationPanel singleton = null;
    private List<Attack> retimes = null;
    private SimpleDateFormat dateFormat = null;

    public static synchronized RetimerCalculationPanel getSingleton() {
        if (singleton == null) {
            singleton = new RetimerCalculationPanel();
        }
        return singleton;
    }

    /**
     * Creates new form AttackSourcePanel
     */
    RetimerCalculationPanel() {
        initComponents();
        jXCollapsiblePane1.setLayout(new BorderLayout());
        jXCollapsiblePane1.add(jInfoScrollPane, BorderLayout.CENTER);
        jInfoTextPane.setText(GENERAL_INFO);
        StyledDocument doc = (StyledDocument) jTextPane1.getDocument();
        Style defaultStyle = doc.addStyle("Default", null);
        StyleConstants.setItalic(defaultStyle, true);
        StyleConstants.setFontFamily(defaultStyle, "SansSerif");
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        retimes = new LinkedList<Attack>();
    }

    public static String getDescription() {
        return "Berechnung";
    }

    public static String getStep() {
        return "id-ret-calculation";
    }

    public void storeProperties() {
        UserProfile profile = GlobalOptions.getSelectedProfile();
    }

    public void restoreProperties() {
        UserProfile profile = GlobalOptions.getSelectedProfile();
        retimes.clear();
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
        buttonGroup1 = new javax.swing.ButtonGroup();
        jXCollapsiblePane1 = new org.jdesktop.swingx.JXCollapsiblePane();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jCalculateButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jAttacks = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jRetimeSources = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();

        jInfoScrollPane.setMinimumSize(new java.awt.Dimension(19, 180));
        jInfoScrollPane.setPreferredSize(new java.awt.Dimension(19, 180));

        jInfoTextPane.setContentType("text/html");
        jInfoTextPane.setEditable(false);
        jInfoTextPane.setText("<html>Du befindest dich im <b>Angriffsmodus</b>. Hier kannst du die Herkunftsd&ouml;rfer ausw&auml;hlen, die f&uuml;r Angriffe verwendet werden d&uuml;rfen. Hierf&uuml;r hast die folgenden M&ouml;glichkeiten:\n<ul>\n<li>Einf&uuml;gen von Dorfkoordinaten aus der Zwischenablage per STRG+V</li>\n<li>Einf&uuml;gen der Herkunftsd&ouml;rfer aus der Gruppen&uuml;bersicht</li>\n<li>Einf&uuml;gen der Herkunftsd&ouml;rfer aus dem SOS-Analyzer</li>\n<li>Einf&uuml;gen der Herkunftsd&ouml;rfer aus Berichten</li>\n<li>Einf&uuml;gen aus der Auswahlübersicht</li>\n<li>Manuelle Eingabe</li>\n</ul>\n</html>\n");
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
        jLabel1.setText("Informationen einblenden");
        jLabel1.setToolTipText("Blendet Informationen zu dieser Ansicht und zu den Datenquellen ein/aus");
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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Informationen zur Berechnung"));
        jScrollPane1.setViewportView(jTextPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        jCalculateButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jCalculateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ui/select.png"))); // NOI18N
        jCalculateButton.setText("Retimes berechnen");
        jCalculateButton.setMaximumSize(new java.awt.Dimension(190, 40));
        jCalculateButton.setMinimumSize(new java.awt.Dimension(190, 40));
        jCalculateButton.setPreferredSize(new java.awt.Dimension(190, 40));
        jCalculateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireCalculateAttacksEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jCalculateButton, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Zusammenfassung"));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Angriffe");
        jLabel2.setPreferredSize(new java.awt.Dimension(200, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel2, gridBagConstraints);

        jAttacks.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jAttacks, gridBagConstraints);

        jLabel10.setText("Herkunftsdörfer für Retimes");
        jLabel10.setPreferredSize(new java.awt.Dimension(200, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel10, gridBagConstraints);

        jRetimeSources.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jRetimeSources, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jPanel1, gridBagConstraints);

        jProgressBar1.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jProgressBar1, gridBagConstraints);

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
            jLabel1.setText("Informationen ausblenden");
        } else {
            jXCollapsiblePane1.setCollapsed(true);
            jLabel1.setText("Informationen einblenden");
        }
    }//GEN-LAST:event_fireHideInfoEvent

    private void fireCalculateAttacksEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCalculateAttacksEvent
        /*
         * if (calculator == null) {//not used yet initializeCalculation(); } else {//in use or finished if (calculator.isRunning()) {//in
         * use...abort calculator.abort(); return; } else {//not in use...recalculate if (calculator.hasResults() &&
         * JOptionPaneHelper.showQuestionConfirmBox(this, "Vorherige Berechnung verwerfen?", "Berechnung verwerfen", "Nein", "Ja") ==
         * JOptionPane.NO_OPTION) { //not recalculate return; } else { //recalculate initializeCalculation(); } } }
         *
         * jCalculateButton.setText("Abbrechen"); calculator.start(); setBusy(true); //wait until calculation is running try {
         * Thread.sleep(20); } catch (Exception e) { }
         */
        if (!retimes.isEmpty()) {
            if (JOptionPaneHelper.showQuestionConfirmBox(this, "Vorherige Berechnung verwerfen?", "Berechnung verwerfen", "Nein", "Ja") == JOptionPane.NO_OPTION) {
                return;

            }
        }
        doCalculation();
    }//GEN-LAST:event_fireCalculateAttacksEvent

    private void doCalculation() {
        if (!jCalculateButton.isEnabled()) {
            setProblem("Berechnung läuft bereits");
            return;
        }
        jCalculateButton.setText("Berechne...");
        jCalculateButton.setEnabled(false);

        setBusy(true);
        new Thread(new Runnable() {

            public void run() {
                try {
                    RETSourceElement[] filtered = RetimerSourceFilterPanel.getSingleton().getFilteredElements();
                    Attack[] attacks = RetimerDataPanel.getSingleton().getAttacks();
                    for (Attack a : attacks) {
                        notifyStatusUpdate("Berechne Retimes für Angriff " + a.getSource().getFullName() + " -> " + a.getTarget().getFullName());
                        for (RETSourceElement element : filtered) {
                            if (!element.isIgnored()) {
                                notifyStatusUpdate(" - Teste Herkunftsdorf " + element.getVillage().getFullName());
                                VillageTroopsHolder holder = TroopsManager.getSingleton().getTroopsForVillage(element.getVillage(), TroopsManager.TROOP_TYPE.OWN);
                                if (holder == null && Constants.DEBUG) {
                                    holder = TroopHelper.getRandomOffVillageTroops(element.getVillage());
                                }
                                if (holder != null) {
                                    List<Attack> retimesForVillage = getRetimesForVillage(a, holder);
                                    CollectionUtils.addAll(retimes, retimesForVillage.toArray(new Attack[retimesForVillage.size()]));
                                } else {
                                    notifyStatusUpdate("Keine Truppen für Dorf " + element.getVillage() + " gefunden");
                                }
                            } else {
                                notifyStatusUpdate("Dorf " + element.getVillage() + " wird ignoriert");
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Failed to calculate retimes", e);
                    notifyStatusUpdate("Interner Fehler bei der Berechnung!");
                } finally {
                    notifyCalculationFinished();
                }
            }
        }).start();
    }

    private List<Attack> getRetimesForVillage(Attack pAttack, VillageTroopsHolder pHolder) {
        List<Attack> results = new LinkedList<Attack>();
        Village target = pAttack.getSource();
        Village source = pHolder.getVillage();
        long returnTime = pAttack.getReturnTime().getTime();
        Hashtable<UnitHolder, Integer> amounts = pHolder.getTroops();
        List<UnitHolder> units = TroopHelper.getContainedUnits(amounts);
        Collections.sort(units, UnitHolder.RUNTIME_COMPARATOR);
        for (int i = units.size() - 1; i >= 0; i--) {
            UnitHolder unit = units.get(i);
            Integer amount = amounts.get(unit);
            if (unit.isRetimeUnit() && amount != null && amount > 0) {
                notifyStatusUpdate(" - Teste Einheit '" + unit.getName() + "'");
                long sendTime = returnTime - DSCalculator.calculateMoveTimeInMillis(source, target, unit.getSpeed());
                if (sendTime > System.currentTimeMillis() + DateUtils.MILLIS_PER_MINUTE && sendTime > pAttack.getArriveTime().getTime() + DateUtils.MILLIS_PER_MINUTE) {
                    notifyStatusUpdate(" - Einheit für Retime geeignet");
                    Attack a = new Attack();
                    a.setSource(source);
                    a.setTarget(target);
                    a.setUnit(unit);
                    a.setSendTime(new Date(sendTime));
                    a.setType(Attack.CLEAN_TYPE);
                    results.add(a);
                }
            }
        }

        return results;
    }

    public void updateStatus() {
        jAttacks.setText(Integer.toString(RetimerDataPanel.getSingleton().getAttacks().length));
        jRetimeSources.setText(Integer.toString(RetimerSourceFilterPanel.getSingleton().getFilteredElements().length));
    }

    public void notifyCalculationFinished() {
        setBusy(false);
        if (!retimes.isEmpty()) {
            setProblem(null);
        } else {
            setProblem("Berechnung erzielte keine Ergebnisse");
        }
        notifyStatusUpdate("Berechnung abgeschlossen");
        jCalculateButton.setText("Retimes berechnen");
        jCalculateButton.setEnabled(true);
    }

    public void notifyStatusUpdate(String pMessage) {
        try {
            StyledDocument doc = jTextPane1.getStyledDocument();
            doc.insertString(doc.getLength(), "(" + dateFormat.format(new Date(System.currentTimeMillis())) + ") " + pMessage + "\n", doc.getStyle("Info"));
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    scroll();
                }
            });
        } catch (BadLocationException ble) {
        }
    }

    private void scroll() {
        Point point = new Point(0, (int) (jTextPane1.getSize().getHeight()));
        JViewport vp = jScrollPane1.getViewport();
        if ((vp == null) || (point == null)) {
            return;
        }
        vp.setViewPosition(point);
    }

    public Attack[] getResults() {
        return retimes.toArray(new Attack[retimes.size()]);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jAttacks;
    private javax.swing.JButton jCalculateButton;
    private javax.swing.JScrollPane jInfoScrollPane;
    private javax.swing.JTextPane jInfoTextPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel jRetimeSources;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private org.jdesktop.swingx.JXCollapsiblePane jXCollapsiblePane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public WizardPanelNavResult allowNext(String string, Map map, Wizard wizard) {
        if (retimes.isEmpty()) {
            setProblem("Keine Ergebnisse vorhanden");
            return WizardPanelNavResult.REMAIN_ON_PAGE;
        }

        RetimerFinishPanel.getSingleton().update();
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
