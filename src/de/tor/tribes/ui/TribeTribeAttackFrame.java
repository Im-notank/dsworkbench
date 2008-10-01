/*
 * AllyAllyAttackFrame.java
 *
 * Created on 29. Juli 2008, 11:17
 */
package de.tor.tribes.ui;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.Ally;
import de.tor.tribes.types.Tribe;
import de.tor.tribes.types.Village;
import de.tor.tribes.ui.editors.DateSpinEditor;
import de.tor.tribes.ui.editors.VillageCellEditor;
import de.tor.tribes.ui.renderer.DateCellRenderer;
import de.tor.tribes.ui.editors.UnitCellEditor;
import de.tor.tribes.util.Constants;
import de.tor.tribes.util.DSCalculator;
import de.tor.tribes.util.attack.AttackManager;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.plaf.OptionPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author  Jejkal
 */
public class TribeTribeAttackFrame extends javax.swing.JFrame {

    private static Logger logger = Logger.getLogger(TribeTribeAttackFrame.class);

    /** Creates new form AllyAllyAttackFrame */
    public TribeTribeAttackFrame() {
        initComponents();
    //setup();
    }

    protected void setup() {
        DefaultTableModel attackModel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Herkunft", "Einheit", "Zeitrahmen"
                }) {

            Class[] types = new Class[]{
                Village.class, UnitHolder.class, String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        jAttacksTable.setModel(attackModel);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jAttacksTable.getModel());
        jAttacksTable.setRowSorter(sorter);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText("<html><b>" + r.getText() + "</b></html>");
                c.setBackground(Constants.DS_BACK);
                return c;
            }
        };

        for (int i = 0; i < jAttacksTable.getColumnCount(); i++) {
            jAttacksTable.getColumn(jAttacksTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
        }

        for (int i = 0; i < jResultsTable.getColumnCount(); i++) {
            jResultsTable.getColumn(jResultsTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
        }

        jScrollPane1.getViewport().setBackground(Constants.DS_BACK_LIGHT);
        jScrollPane2.getViewport().setBackground(Constants.DS_BACK_LIGHT);

        try {
            Enumeration<Integer> allyKeys = DataHolder.getSingleton().getAllies().keys();
            List<Ally> allies = new LinkedList();
            while (allyKeys.hasMoreElements()) {
                allies.add(DataHolder.getSingleton().getAllies().get(allyKeys.nextElement()));
            }

            Ally[] aAllies = allies.toArray(new Ally[]{});
            allies = null;
            Arrays.sort(aAllies, Ally.CASE_INSENSITIVE_ORDER);
            Village vCurrent = DSWorkbenchMainFrame.getSingleton().getCurrentUserVillage();
            if (vCurrent != null) {
                Tribe tCurrent = vCurrent.getTribe();
                if (tCurrent == null) {
                    logger.warn("Could not get current user village. Probably no active user is selected.");
                    return;
                } else {
                    jSourceVillageList.setModel(new DefaultComboBoxModel(tCurrent.getVillageList().toArray()));
                }
            }
            DefaultComboBoxModel targetAllyModel = new DefaultComboBoxModel(aAllies);
            jTargetAllyList.setModel(targetAllyModel);
            jTargetAllyList.setSelectedIndex(0);
            fireTargetAllyChangedEvent(null);
            jArriveTime.setValue(Calendar.getInstance().getTime());

            jAttacksTable.setDefaultRenderer(Date.class, new DateCellRenderer());
            jAttacksTable.setDefaultEditor(Date.class, new DateSpinEditor());
            jAttacksTable.setDefaultEditor(UnitHolder.class, new UnitCellEditor());
            jAttacksTable.setDefaultEditor(Village.class, new VillageCellEditor());

            DefaultComboBoxModel unitModel = new DefaultComboBoxModel(DataHolder.getSingleton().getUnits().toArray(new UnitHolder[]{}));
            jTroopsList.setModel(unitModel);

            jResultFrame.pack();
        } catch (Exception e) {
            logger.error("Failed to initialize TribeAttackFrame", e);
        }

        jResultsTable.setDefaultRenderer(Date.class, new DateCellRenderer());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jResultFrame = new javax.swing.JFrame();
        jScrollPane2 = new javax.swing.JScrollPane();
        jResultsTable = new javax.swing.JTable();
        jCloseResultsButton = new javax.swing.JButton();
        jCopyToClipboardAsBBButton = new javax.swing.JButton();
        jAddToAttacksButton = new javax.swing.JButton();
        jCopyToClipboardButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jAttacksTable = new javax.swing.JTable();
        jSourceVillageLabel = new javax.swing.JLabel();
        jSourceVillageList = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jTargetAllyList = new javax.swing.JComboBox();
        jTargetAllyLabel = new javax.swing.JLabel();
        jArriveTime = new javax.swing.JSpinner();
        jArriveTimeLabel = new javax.swing.JLabel();
        jMaxAttacksPerVillageLabel = new javax.swing.JLabel();
        jMaxAttacksPerVillage = new javax.swing.JComboBox();
        jTargetTribeLabel = new javax.swing.JLabel();
        jTargetPlayerList = new javax.swing.JComboBox();
        jSendTime = new javax.swing.JSpinner();
        jStartTimeLabel = new javax.swing.JLabel();
        jNightForbidden = new javax.swing.JCheckBox();
        jNoNightLabel = new javax.swing.JLabel();
        jRandomizeTribes = new javax.swing.JCheckBox();
        jRandomizeLabel = new javax.swing.JLabel();
        jCalculateButton = new javax.swing.JButton();
        jSourceUnitLabel = new javax.swing.JLabel();
        jTroopsList = new javax.swing.JComboBox();
        jButton3 = new javax.swing.JButton();
        jTimeFrameLabel = new javax.swing.JLabel();
        jTimeFrame = new javax.swing.JComboBox();
        jButton8 = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/tor/tribes/ui/Bundle"); // NOI18N
        jResultFrame.setTitle(bundle.getString("TribeTribeAttackFrame.jResultFrame.title")); // NOI18N

        jResultsTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jResultsTable.setOpaque(false);
        jScrollPane2.setViewportView(jResultsTable);

        jCloseResultsButton.setText(bundle.getString("TribeTribeAttackFrame.jCloseResultsButton.text")); // NOI18N
        jCloseResultsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireHideResultsEvent(evt);
            }
        });

        jCopyToClipboardAsBBButton.setText(bundle.getString("TribeTribeAttackFrame.jCopyToClipboardAsBBButton.text")); // NOI18N
        jCopyToClipboardAsBBButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jCopyToClipboardAsBBButton.toolTipText")); // NOI18N
        jCopyToClipboardAsBBButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAttacksToClipboardEvent(evt);
            }
        });

        jAddToAttacksButton.setText(bundle.getString("TribeTribeAttackFrame.jAddToAttacksButton.text")); // NOI18N
        jAddToAttacksButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jAddToAttacksButton.toolTipText")); // NOI18N
        jAddToAttacksButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireTransferToAttackPlanningEvent(evt);
            }
        });

        jCopyToClipboardButton.setText(bundle.getString("TribeTribeAttackFrame.jCopyToClipboardButton.text")); // NOI18N
        jCopyToClipboardButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jCopyToClipboardButton.toolTipText")); // NOI18N
        jCopyToClipboardButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireUnformattedAttacksToClipboardEvent(evt);
            }
        });

        javax.swing.GroupLayout jResultFrameLayout = new javax.swing.GroupLayout(jResultFrame.getContentPane());
        jResultFrame.getContentPane().setLayout(jResultFrameLayout);
        jResultFrameLayout.setHorizontalGroup(
            jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jResultFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addGroup(jResultFrameLayout.createSequentialGroup()
                        .addComponent(jAddToAttacksButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCopyToClipboardButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCopyToClipboardAsBBButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCloseResultsButton)))
                .addContainerGap())
        );
        jResultFrameLayout.setVerticalGroup(
            jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jResultFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jResultFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCloseResultsButton)
                    .addComponent(jCopyToClipboardAsBBButton)
                    .addComponent(jCopyToClipboardButton)
                    .addComponent(jAddToAttacksButton))
                .addContainerGap())
        );

        setTitle(bundle.getString("TribeTribeAttackFrame.title")); // NOI18N

        jAttacksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jAttacksTable.setOpaque(false);
        jAttacksTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(jAttacksTable);

        jSourceVillageLabel.setText(bundle.getString("TribeTribeAttackFrame.jSourceVillageLabel.text")); // NOI18N

        jSourceVillageList.setToolTipText(bundle.getString("TribeTribeAttackFrame.jSourceVillageList.toolTipText")); // NOI18N
        jSourceVillageList.setMaximumSize(new java.awt.Dimension(150, 20));
        jSourceVillageList.setMinimumSize(new java.awt.Dimension(150, 20));
        jSourceVillageList.setPreferredSize(new java.awt.Dimension(150, 20));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/add.gif"))); // NOI18N
        jButton1.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton1.toolTipText")); // NOI18N
        jButton1.setMaximumSize(new java.awt.Dimension(20, 20));
        jButton1.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton1.setPreferredSize(new java.awt.Dimension(20, 20));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddAttackEvent(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("TribeTribeAttackFrame.jPanel1.border.title"))); // NOI18N

        jTargetAllyList.setToolTipText(bundle.getString("TribeTribeAttackFrame.jTargetAllyList.toolTipText")); // NOI18N
        jTargetAllyList.setMaximumSize(new java.awt.Dimension(150, 20));
        jTargetAllyList.setMinimumSize(new java.awt.Dimension(150, 20));
        jTargetAllyList.setPreferredSize(new java.awt.Dimension(150, 20));
        jTargetAllyList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireTargetAllyChangedEvent(evt);
            }
        });

        jTargetAllyLabel.setText(bundle.getString("TribeTribeAttackFrame.jTargetAllyLabel.text")); // NOI18N

        jArriveTime.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), new java.util.Date(), null, java.util.Calendar.SECOND));
        jArriveTime.setToolTipText(bundle.getString("TribeTribeAttackFrame.jArriveTime.toolTipText")); // NOI18N
        jArriveTime.setMaximumSize(new java.awt.Dimension(150, 20));
        jArriveTime.setMinimumSize(new java.awt.Dimension(150, 20));
        jArriveTime.setPreferredSize(new java.awt.Dimension(150, 20));

        jArriveTimeLabel.setText(bundle.getString("TribeTribeAttackFrame.jArriveTimeLabel.text")); // NOI18N

        jMaxAttacksPerVillageLabel.setText(bundle.getString("TribeTribeAttackFrame.jMaxAttacksPerVillageLabel.text")); // NOI18N

        jMaxAttacksPerVillage.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        jMaxAttacksPerVillage.setToolTipText(bundle.getString("TribeTribeAttackFrame.jMaxAttacksPerVillage.toolTipText")); // NOI18N
        jMaxAttacksPerVillage.setMaximumSize(new java.awt.Dimension(150, 20));
        jMaxAttacksPerVillage.setMinimumSize(new java.awt.Dimension(150, 20));
        jMaxAttacksPerVillage.setPreferredSize(new java.awt.Dimension(150, 20));

        jTargetTribeLabel.setText(bundle.getString("TribeTribeAttackFrame.jTargetTribeLabel.text")); // NOI18N
        jTargetTribeLabel.setMaximumSize(new java.awt.Dimension(74, 14));
        jTargetTribeLabel.setMinimumSize(new java.awt.Dimension(74, 14));
        jTargetTribeLabel.setPreferredSize(new java.awt.Dimension(74, 14));

        jTargetPlayerList.setToolTipText(bundle.getString("TribeTribeAttackFrame.jTargetPlayerList.toolTipText")); // NOI18N
        jTargetPlayerList.setMaximumSize(new java.awt.Dimension(150, 20));
        jTargetPlayerList.setMinimumSize(new java.awt.Dimension(150, 20));
        jTargetPlayerList.setPreferredSize(new java.awt.Dimension(150, 20));

        jSendTime.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), new java.util.Date(), null, java.util.Calendar.SECOND));
        jSendTime.setToolTipText(bundle.getString("TribeTribeAttackFrame.jSendTime.toolTipText")); // NOI18N
        jSendTime.setMaximumSize(new java.awt.Dimension(150, 20));
        jSendTime.setMinimumSize(new java.awt.Dimension(150, 20));
        jSendTime.setPreferredSize(new java.awt.Dimension(150, 20));

        jStartTimeLabel.setText(bundle.getString("TribeTribeAttackFrame.jStartTimeLabel.text")); // NOI18N

        jNightForbidden.setToolTipText(bundle.getString("TribeTribeAttackFrame.jNightForbidden.toolTipText")); // NOI18N
        jNightForbidden.setOpaque(false);

        jNoNightLabel.setText(bundle.getString("TribeTribeAttackFrame.jNoNightLabel.text")); // NOI18N
        jNoNightLabel.setMaximumSize(new java.awt.Dimension(74, 14));
        jNoNightLabel.setMinimumSize(new java.awt.Dimension(74, 14));
        jNoNightLabel.setPreferredSize(new java.awt.Dimension(74, 14));

        jRandomizeTribes.setToolTipText(bundle.getString("TribeTribeAttackFrame.jRandomizeTribes.toolTipText")); // NOI18N
        jRandomizeTribes.setOpaque(false);

        jRandomizeLabel.setText(bundle.getString("TribeTribeAttackFrame.jRandomizeLabel.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRandomizeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jTargetAllyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jStartTimeLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jMaxAttacksPerVillageLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jMaxAttacksPerVillage, 0, 141, Short.MAX_VALUE)
                    .addComponent(jSendTime, javax.swing.GroupLayout.PREFERRED_SIZE, 141, Short.MAX_VALUE)
                    .addComponent(jTargetAllyList, 0, 0, Short.MAX_VALUE)
                    .addComponent(jRandomizeTribes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                .addGap(109, 109, 109)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTargetTribeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jNoNightLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jArriveTimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jNightForbidden, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(jArriveTime, javax.swing.GroupLayout.PREFERRED_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(jTargetPlayerList, 0, 140, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTargetAllyList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTargetAllyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jSendTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jStartTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jMaxAttacksPerVillage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMaxAttacksPerVillageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRandomizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRandomizeTribes)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTargetTribeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTargetPlayerList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jArriveTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jArriveTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jNoNightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1))
                            .addComponent(jNightForbidden))))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jCalculateButton.setText(bundle.getString("TribeTribeAttackFrame.jCalculateButton.text")); // NOI18N
        jCalculateButton.setToolTipText(bundle.getString("TribeTribeAttackFrame.jCalculateButton.toolTipText")); // NOI18N
        jCalculateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCalculateAttackEvent(evt);
            }
        });

        jSourceUnitLabel.setText(bundle.getString("TribeTribeAttackFrame.jSourceUnitLabel.text")); // NOI18N

        jTroopsList.setToolTipText(bundle.getString("TribeTribeAttackFrame.jTroopsList.toolTipText")); // NOI18N
        jTroopsList.setMaximumSize(new java.awt.Dimension(150, 20));
        jTroopsList.setMinimumSize(new java.awt.Dimension(150, 20));
        jTroopsList.setPreferredSize(new java.awt.Dimension(150, 20));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/remove.gif"))); // NOI18N
        jButton3.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton3.toolTipText")); // NOI18N
        jButton3.setMaximumSize(new java.awt.Dimension(20, 20));
        jButton3.setMinimumSize(new java.awt.Dimension(20, 20));
        jButton3.setPreferredSize(new java.awt.Dimension(20, 20));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRemoveAttackEvent(evt);
            }
        });

        jTimeFrameLabel.setText(bundle.getString("TribeTribeAttackFrame.jTimeFrameLabel.text")); // NOI18N

        jTimeFrame.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "egal", "Früh (6-8)", "Vormittag (8-12)", "Nachmittag (12-18)", "Abend (18-0)", "Nacht (0-6)" }));
        jTimeFrame.setToolTipText(bundle.getString("TribeTribeAttackFrame.jTimeFrame.toolTipText")); // NOI18N

        jButton8.setText(bundle.getString("TribeTribeAttackFrame.jButton8.text")); // NOI18N
        jButton8.setToolTipText(bundle.getString("TribeTribeAttackFrame.jButton8.toolTipText")); // NOI18N
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAddAllPlayerVillages(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSourceVillageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButton8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 526, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jSourceVillageList, 0, 204, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jSourceUnitLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTroopsList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jTimeFrameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTimeFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jCalculateButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTimeFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSourceVillageLabel)
                    .addComponent(jTimeFrameLabel)
                    .addComponent(jTroopsList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSourceUnitLabel)
                    .addComponent(jSourceVillageList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCalculateButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void fireAddAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddAttackEvent
    Village vSource = (Village) jSourceVillageList.getSelectedItem();
    UnitHolder uSource = (UnitHolder) jTroopsList.getSelectedItem();
    String timeFrame = (String) jTimeFrame.getSelectedItem();
    ((DefaultTableModel) jAttacksTable.getModel()).addRow(new Object[]{vSource, uSource, timeFrame});
}//GEN-LAST:event_fireAddAttackEvent

private void fireRemoveAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRemoveAttackEvent
    int[] rows = jAttacksTable.getSelectedRows();
    if ((rows != null) && (rows.length > 0)) {
        String message = "Angriff entfernen?";
        if (rows.length > 1) {
            message = rows.length + " Angriffe entfernen?";
        }
        int res = JOptionPane.showConfirmDialog(this, message, "Angriff entfernen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res != JOptionPane.YES_OPTION) {
            return;
        }
        for (int i = rows.length - 1; i >= 0; i--) {
            jAttacksTable.invalidate();
            int row = jAttacksTable.convertRowIndexToModel(rows[i]);
            ((DefaultTableModel) jAttacksTable.getModel()).removeRow(row);
            jAttacksTable.revalidate();
        }
    }
}//GEN-LAST:event_fireRemoveAttackEvent

private void fireCalculateAttackEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCalculateAttackEvent

    Tribe target = (Tribe) jTargetPlayerList.getSelectedItem();
    if (target == null) {
        JOptionPane.showMessageDialog(this, "Kein Ziel-Spieler ausgewählt", "Fehler", JOptionPane.ERROR_MESSAGE);
        return;
    }

    Hashtable<Village, Hashtable<Village, UnitHolder>> attacks = new Hashtable<Village, Hashtable<Village, UnitHolder>>();
    List<Village> notAssigned = new LinkedList<Village>();
    Hashtable<Tribe, Integer> attacksPerTribe = new Hashtable<Tribe, Integer>();
    for (int i = 0; i < jAttacksTable.getRowCount(); i++) {
        Village vSource = (Village) jAttacksTable.getValueAt(i, 0);
        UnitHolder uSource = (UnitHolder) jAttacksTable.getValueAt(i, 1);
        String sTimeFrame = (String) jAttacksTable.getValueAt(i, 2);
        //time when the fist attacks should begin
        long minSendTime = ((Date) jSendTime.getValue()).getTime();
        //time frame for sending troops
        int timeFrame = ((DefaultComboBoxModel) jTimeFrame.getModel()).getIndexOf(sTimeFrame);
        //time when the attacks should arrive
        long arrive = ((Date) jArriveTime.getValue()).getTime();
        //max. number of attacks per target village
        int maxAttacksPerVillage = jMaxAttacksPerVillage.getSelectedIndex() + 1;

        Village vTarget = null;

        //search all tribes and villages for targets
        for (Village v : target.getVillageList()) {
            double time = DSCalculator.calculateMoveTimeInSeconds(vSource, v, uSource.getSpeed());
            long sendTime = arrive - (long) time * 1000;
            //check if attack is somehow possible

            if (sendTime > minSendTime) {
                //check time frame
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(sendTime);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                int second = c.get(Calendar.SECOND);
                boolean inTimeFrame = false;
                switch (timeFrame) {
                    case 1: {
                        //6 to 8
                        if ((hour >= 6) && ((hour <= 7) && (minute <= 59) && (second <= 59))) {
                            inTimeFrame = true;
                        }
                        break;
                    }
                    case 2: {
                        //8 to 12
                        if ((hour >= 8) && ((hour <= 11) && (minute <= 59) && (second <= 59))) {
                            inTimeFrame = true;
                        }
                        break;
                    }
                    case 3: {
                        //12 to 18
                        if ((hour >= 12) && ((hour <= 17) && (minute <= 59) && (second <= 59))) {
                            inTimeFrame = true;
                        }
                        break;
                    }
                    case 4: {
                        //18 to 0
                        if ((hour >= 18) && ((hour <= 23) && (minute <= 59) && (second <= 59))) {
                            inTimeFrame = true;
                        }
                        break;
                    }
                    case 5: {
                        //0 to 6
                        if ((hour >= 0) && (hour < 6)) {
                            inTimeFrame = true;
                        }
                        break;
                    }
                    default: {
                        //doesn't matter
                        inTimeFrame = true;
                    }
                }

                //check sleep mode of still in time frame
                if (inTimeFrame) {
                    if (jNightForbidden.isSelected()) {
                        hour = c.get(Calendar.HOUR_OF_DAY);
                        int min = c.get(Calendar.MINUTE);
                        int sec = c.get(Calendar.SECOND);
                        if (((hour <= 23) && (min <= 59) && (sec <= 59)) && ((hour >= 8) && (min >= 0) && (sec >= 0))) {
                            //time between 23:59:59 and 08:00:00, outside night mode
                            } else {
                            inTimeFrame = false;
                        }
                    }
                }

                if (inTimeFrame) {
                    //only calculate if time is in time frame
                    //get list of source villages for current target
                    Hashtable<Village, UnitHolder> attacksForVillage = attacks.get(v);
                    if (attacksForVillage == null) {
                        //no attack found for this village
                        //get number of attacks on this tribe
                        Integer cnt = attacksPerTribe.get(v.getTribe());
                        if (cnt == null) {
                            //no attacks on this tribe yet
                            cnt = 0;
                        }
                        //create new table of attacks
                        attacksForVillage = new Hashtable<Village, UnitHolder>();
                        attacksForVillage.put(vSource, uSource);
                        attacks.put(v, attacksForVillage);
                        attacksPerTribe.put(v.getTribe(), cnt + 1);
                        vTarget = v;
                    } else {
                        //there are already attacks on this village
                        if (attacksForVillage.keySet().size() < maxAttacksPerVillage) {
                            //more attacks on this village are allowed
                            Integer cnt = attacksPerTribe.get(v.getTribe());
                            if (cnt == null) {
                                cnt = 0;
                            }
                            //max number of attacks neither for villages nor for player reached
                            attacksForVillage.put(vSource, uSource);
                            attacksPerTribe.put(v.getTribe(), cnt + 1);
                            vTarget = v;
                        } else {
                            //max number of attacks per village reached, continue search
                        }
                    }
                }
            }
            if (vTarget != null) {
                break;
            }
        }

        if (vTarget == null) {
            notAssigned.add(vSource);
        }
    }

    showResults(attacks);
    if (notAssigned.size() > 0) {
        String notAssignedMessage = "Für die folgenden Dörfer konnte kein passendes Ziel gefunden werden:\n";
        for (Village v : notAssigned) {
            notAssignedMessage += v + "\n";
        }
        JOptionPane.showMessageDialog(jResultFrame, notAssignedMessage, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}//GEN-LAST:event_fireCalculateAttackEvent

private void fireHideResultsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireHideResultsEvent
    jResultFrame.setVisible(false);
}//GEN-LAST:event_fireHideResultsEvent

private void fireTransferToAttackPlanningEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireTransferToAttackPlanningEvent
    DefaultTableModel resultModel = (DefaultTableModel) jResultsTable.getModel();
    for (int i = 0; i < resultModel.getRowCount(); i++) {
        Village source = (Village) resultModel.getValueAt(i, 0);
        UnitHolder unit = (UnitHolder) resultModel.getValueAt(i, 1);
        Village target = (Village) resultModel.getValueAt(i, 2);
        Date sendTime = (Date) resultModel.getValueAt(i, 3);
        long arriveTime = sendTime.getTime() + (long) (DSCalculator.calculateMoveTimeInSeconds(source, target, unit.getSpeed()) * 1000);
        AttackManager.getSingleton().addAttack(source, target, unit, new Date(arriveTime));
    }
}//GEN-LAST:event_fireTransferToAttackPlanningEvent

private void fireAttacksToClipboardEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAttacksToClipboardEvent
    try {
        DefaultTableModel resultModel = (DefaultTableModel) jResultsTable.getModel();
        String data = "";
        for (int i = 0; i < resultModel.getRowCount(); i++) {
            Village sVillage = (Village) resultModel.getValueAt(i, 0);
            UnitHolder sUnit = (UnitHolder) resultModel.getValueAt(i, 1);
            Village tVillage = (Village) resultModel.getValueAt(i, 2);
            Date dTime = (Date) resultModel.getValueAt(i, 3);
            String time = Constants.DATE_FORMAT.format(dTime);
            data += "Angriff aus " + sVillage.toBBCode() + " mit " + sUnit + " auf " + tVillage.getTribe().toBBCode() + " in " + tVillage.toBBCode() + " um " + time + "\n";
        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data), null);
        String result = "Daten in Zwischenablage kopiert.";
        JOptionPane.showMessageDialog(jResultFrame, result, "Information", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        logger.error("Failed to copy data to clipboard", e);
        String result = "Fehler beim Kopieren in die Zwischenablage.";
        JOptionPane.showMessageDialog(jResultFrame, result, "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}//GEN-LAST:event_fireAttacksToClipboardEvent

private void fireUnformattedAttacksToClipboardEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireUnformattedAttacksToClipboardEvent
    try {
        DefaultTableModel resultModel = (DefaultTableModel) jResultsTable.getModel();
        String data = "";
        for (int i = 0; i < resultModel.getRowCount(); i++) {
            Village sVillage = (Village) resultModel.getValueAt(i, 0);
            UnitHolder sUnit = (UnitHolder) resultModel.getValueAt(i, 1);
            Village tVillage = (Village) resultModel.getValueAt(i, 2);
            Date dTime = (Date) resultModel.getValueAt(i, 3);
            String time = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(dTime);
            data += sVillage + "\t" + sUnit + "\t" + tVillage.getTribe() + "\t" + tVillage + "\t" + time + "\n";
        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data), null);
        String result = "Daten in Zwischenablage kopiert.";
        JOptionPane.showMessageDialog(jResultFrame, result, "Information", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        logger.error("Failed to copy data to clipboard", e);
        String result = "Fehler beim Kopieren in die Zwischenablage.";
        JOptionPane.showMessageDialog(jResultFrame, result, "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}//GEN-LAST:event_fireUnformattedAttacksToClipboardEvent

private void fireAddAllPlayerVillages(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAddAllPlayerVillages
    Tribe sTribe = ((Village) jSourceVillageList.getItemAt(0)).getTribe();
    for (Village v : sTribe.getVillageList()) {
        jSourceVillageList.setSelectedItem(v);
        fireAddAttackEvent(null);
    }
}//GEN-LAST:event_fireAddAllPlayerVillages

private void fireTargetAllyChangedEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireTargetAllyChangedEvent
    Ally a = (Ally) jTargetAllyList.getSelectedItem();
    Tribe[] tribes = a.getTribes().toArray(new Tribe[]{});
    Arrays.sort(tribes, Tribe.CASE_INSENSITIVE_ORDER);
    jTargetPlayerList.setModel(new DefaultComboBoxModel(tribes));
}//GEN-LAST:event_fireTargetAllyChangedEvent

    private void showResults(Hashtable<Village, Hashtable<Village, UnitHolder>> pAttacks) {
        DefaultTableModel resultModel = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Herkunft", "Truppen", "Ziel", "Startzeit"
                }) {

            Class[] types = new Class[]{
                Village.class, UnitHolder.class, Village.class, Date.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };

        Enumeration<Village> targets = pAttacks.keys();
        while (targets.hasMoreElements()) {
            Village target = targets.nextElement();
            Hashtable<Village, UnitHolder> sources = pAttacks.get(target);
            Enumeration<Village> sourceEnum = sources.keys();
            while (sourceEnum.hasMoreElements()) {
                Village source = sourceEnum.nextElement();
                UnitHolder unit = sources.get(source);
                long targetTime = ((Date) jArriveTime.getValue()).getTime();
                long startTime = targetTime - (long) DSCalculator.calculateMoveTimeInSeconds(source, target, unit.getSpeed()) * 1000;
                resultModel.addRow(new Object[]{source, unit, target, new Date(startTime)});
            }
        }
        jResultsTable.setModel(resultModel);
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jResultsTable.getModel());
        jResultsTable.setRowSorter(sorter);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, hasFocus, hasFocus, row, row);
                String t = ((DefaultTableCellRenderer) c).getText();
                ((DefaultTableCellRenderer) c).setText(t);
                c.setBackground(Constants.DS_BACK);
                DefaultTableCellRenderer r = ((DefaultTableCellRenderer) c);
                r.setText("<html><b>" + r.getText() + "</b></html>");
                return c;
            }
        };

        for (int i = 0; i < jResultsTable.getColumnCount(); i++) {
            jResultsTable.getColumn(jResultsTable.getColumnName(i)).setHeaderRenderer(headerRenderer);
        }
        jResultFrame.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TribeTribeAttackFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jAddToAttacksButton;
    private javax.swing.JSpinner jArriveTime;
    private javax.swing.JLabel jArriveTimeLabel;
    private javax.swing.JTable jAttacksTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jCalculateButton;
    private javax.swing.JButton jCloseResultsButton;
    private javax.swing.JButton jCopyToClipboardAsBBButton;
    private javax.swing.JButton jCopyToClipboardButton;
    private javax.swing.JComboBox jMaxAttacksPerVillage;
    private javax.swing.JLabel jMaxAttacksPerVillageLabel;
    private javax.swing.JCheckBox jNightForbidden;
    private javax.swing.JLabel jNoNightLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jRandomizeLabel;
    private javax.swing.JCheckBox jRandomizeTribes;
    private javax.swing.JFrame jResultFrame;
    private javax.swing.JTable jResultsTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSendTime;
    private javax.swing.JLabel jSourceUnitLabel;
    private javax.swing.JLabel jSourceVillageLabel;
    private javax.swing.JComboBox jSourceVillageList;
    private javax.swing.JLabel jStartTimeLabel;
    private javax.swing.JLabel jTargetAllyLabel;
    private javax.swing.JComboBox jTargetAllyList;
    private javax.swing.JComboBox jTargetPlayerList;
    private javax.swing.JLabel jTargetTribeLabel;
    private javax.swing.JComboBox jTimeFrame;
    private javax.swing.JLabel jTimeFrameLabel;
    private javax.swing.JComboBox jTroopsList;
    // End of variables declaration//GEN-END:variables
}
