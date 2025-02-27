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

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.DataHolderListener;
import de.tor.tribes.io.ServerManager;
import de.tor.tribes.io.TroopAmountFixed;
import de.tor.tribes.io.UnitHolder;
import de.tor.tribes.types.UserProfile;
import de.tor.tribes.types.ext.InvalidTribe;
import de.tor.tribes.types.ext.Tribe;
import de.tor.tribes.types.ext.Village;
import de.tor.tribes.ui.editors.ColorChooserCellEditor;
import de.tor.tribes.ui.panels.MapPanel;
import de.tor.tribes.ui.panels.MinimapPanel;
import de.tor.tribes.ui.panels.TroopSelectionPanel;
import de.tor.tribes.ui.renderer.ColorCellRenderer;
import de.tor.tribes.ui.renderer.DefaultTableHeaderRenderer;
import de.tor.tribes.ui.windows.DSWorkbenchMainFrame;
import de.tor.tribes.ui.wiz.red.ResourceDistributorWizard;
import de.tor.tribes.ui.wiz.tap.TacticsPlanerWizard;
import de.tor.tribes.util.*;
import de.tor.tribes.util.html.AttackPlanHTMLExporter;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author Torridity
 */
public class DSWorkbenchSettingsDialog extends javax.swing.JDialog implements
        DataHolderListener {
    private static Logger logger = LogManager.getLogger("SettingsDialog");
    private static Translator trans = TranslationManager.getTranslator("ui.views.DSWorkbenchSettingsDialog");

    private static DSWorkbenchSettingsDialog SINGLETON = null;
    private Proxy webProxy;
    private boolean INITIALIZED = false;
    private boolean isBlocked = false;
    private javax.swing.DefaultComboBoxModel jVillageSortTypeChooserModel;
    private javax.swing.DefaultComboBoxModel jNotifyDurationBoxModel;

    public static synchronized DSWorkbenchSettingsDialog getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchSettingsDialog();
        }

        return SINGLETON;
    }

    /**
     * Creates new form TribesPlannerStartFrame
     */
    DSWorkbenchSettingsDialog() {
        jVillageSortTypeChooserModel = new javax.swing.DefaultComboBoxModel(new String[] {
            trans.get("Alphabetisch"), trans.get("NachKoordinaten")
        });
        jNotifyDurationBoxModel = new javax.swing.DefaultComboBoxModel(new String[] {
            trans.get("Unbegrenzt"), trans.get("10Sekunden"), trans.get("20Sekunden"), trans.get("30Sekunden")
        });
        initComponents();
        troopDensitySelection.setup(new String[]{"spear", "sword", "archer", "heavy"},
                TroopSelectionPanel.alignType.VERTICAL, -1);
        sosAttackerSelection.setupOffense(TroopSelectionPanel.alignType.VERTICAL, -1);
        sosDefenderSelection.setupDefense(TroopSelectionPanel.alignType.VERTICAL, -1);

        jLanguageChooser.setModel(new javax.swing.DefaultComboBoxModel(TranslationManager.getLanguages()));
        
        GlobalOptions.addDataHolderListener(DSWorkbenchSettingsDialog.this);

        // <editor-fold defaultstate="collapsed" desc=" General Layout ">
        jTroopDensitySelectionDialog.pack();
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Network Setup">
        boolean useProxy = GlobalOptions.getProperties().getBoolean("proxySet");

        jDirectConnectOption.setSelected(!useProxy);
        jProxyConnectOption.setSelected(useProxy);

        //System.setProperty("proxyHost", GlobalOptions.getProperty("proxyHost"));
        jProxyHost.setText(GlobalOptions.getProperty("proxyHost"));

        //System.setProperty("proxyPort", GlobalOptions.getProperty("proxyPort"));
        jProxyPort.setText(GlobalOptions.getProperty("proxyPort"));

        // System.setProperty("proxyHost", GlobalOptions.getProperty("proxyHost"));
        jProxyTypeChooser.setSelectedIndex(GlobalOptions.getProperties().getInt("proxyType"));

        //System.setProperty("proxyPort", GlobalOptions.getProperty("proxyPort"));
        jProxyUser.setText(GlobalOptions.getProperty("proxyUser"));

        //System.setProperty("proxyPort", GlobalOptions.getProperty("proxyPort"));
        jProxyPassword.setText(GlobalOptions.getProperty("proxyPassword"));

        if (jProxyConnectOption.isSelected()) {
            SocketAddress addr = new InetSocketAddress(jProxyHost.getText(), Integer.parseInt(jProxyPort.getText()));
            switch (jProxyTypeChooser.getSelectedIndex()) {
                case 1: {
                    webProxy = new Proxy(Proxy.Type.SOCKS, addr);
                    break;
                }
                default: {
                    webProxy = new Proxy(Proxy.Type.HTTP, addr);
                    break;
                }
            }
            if ((jProxyUser.getText().length() >= 1) && (jProxyPassword.getPassword().length > 1)) {
                Authenticator.setDefault(new Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(jProxyUser.getText(), jProxyPassword.getPassword());
                    }
                });
            }
        } else {
            webProxy = Proxy.NO_PROXY;
        }

        //</editor-fold>
        // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
        if (!Constants.DEBUG) {
            GlobalOptions.getHelpBroker().enableHelp(jPlayerServerSettings, "pages.player_server_settings", GlobalOptions.getHelpBroker().getHelpSet());
            GlobalOptions.getHelpBroker().enableHelp(jMapSettings, "pages.map_settings", GlobalOptions.getHelpBroker().getHelpSet());
            GlobalOptions.getHelpBroker().enableHelp(jAttackSettings, "pages.attack_settings", GlobalOptions.getHelpBroker().getHelpSet());
            GlobalOptions.getHelpBroker().enableHelp(jNetworkSettings, "pages.network_settings", GlobalOptions.getHelpBroker().getHelpSet());
            GlobalOptions.getHelpBroker().enableHelp(jTemplateSettings, "pages.template_settings", GlobalOptions.getHelpBroker().getHelpSet());
            GlobalOptions.getHelpBroker().enableHelp(jMiscSettings, "pages.misc_settings", GlobalOptions.getHelpBroker().getHelpSet());
            GlobalOptions.getHelpBroker().enableHelpKey(getRootPane(), "pages.settings", GlobalOptions.getHelpBroker().getHelpSet());
        }
        // </editor-fold>
    }

    public void restoreProperties() {
        //show popup moral
        jShowPopupMoral.setSelected(GlobalOptions.getProperties().getBoolean("show.popup.moral"));
        jShowPopupConquers.setSelected(GlobalOptions.getProperties().getBoolean("show.popup.conquers"));
        jShowPopupRanks.setSelected(GlobalOptions.getProperties().getBoolean("show.popup.ranks"));
        jShowPopupFarmSpace.setSelected(GlobalOptions.getProperties().getBoolean("show.popup.farm.space"));
        jMaxFarmSpace.setText(GlobalOptions.getProperty("max.farm.space"));
        jPopupFarmUseRealValues.setSelected(GlobalOptions.getProperties().getBoolean("farm.popup.use.real"));
        updateFarmSpaceUI();
        jShowContinents.setSelected(GlobalOptions.getProperties().getBoolean("map.showcontinents"));
        jShowSectors.setSelected(GlobalOptions.getProperties().getBoolean("show.sectors"));
        jShowBarbarian.setSelected(GlobalOptions.getProperties().getBoolean("show.barbarian"));
        jMarkerTransparency.setValue(GlobalOptions.getProperties().getInt("map.marker.transparency"));
        jShowAttackMovementBox.setSelected(GlobalOptions.getProperties().getBoolean("attack.movement"));
        jDrawAttacksByDefaultBox.setSelected(GlobalOptions.getProperties().getBoolean("draw.attacks.by.default"));

        jShowLiveCountdown.setSelected(GlobalOptions.getProperties().getBoolean("show.live.countdown"));

        jExtendedAttackLineDrawing.setSelected(GlobalOptions.getProperties().getBoolean("extended.attack.vectors"));
        jHeaderPath.setText(GlobalOptions.getProperty("attack.template.header"));
        jBlockPath.setText(GlobalOptions.getProperty("attack.template.block"));
        jFooterPath.setText(GlobalOptions.getProperty("attack.template.footer"));
        //reload templates
        AttackPlanHTMLExporter.loadCustomTemplate();
        jMarkOwnVillagesOnMinimap.setSelected(GlobalOptions.getProperties().getBoolean("mark.villages.on.minimap"));
        jDefaultMark.setSelectedIndex(GlobalOptions.getProperties().getInt("default.mark"));
        jCheckForUpdatesBox.setSelected(GlobalOptions.getProperties().getBoolean("check.updates.on.startup"));
        int villageOrder = GlobalOptions.getProperties().getInt("village.order");
        Village.setOrderType(villageOrder);
        jVillageSortTypeChooser.setSelectedIndex(villageOrder);
        jNotifyDurationBox.setSelectedIndex(GlobalOptions.getProperties().getInt("notify.duration"));
        jInformOnUpdates.setSelected(GlobalOptions.getProperties().getBoolean("inform.on.updates"));
        jMaxTroopDensity.setText(GlobalOptions.getProperty("max.density.troops"));
        jClipboardSound.setSelected(GlobalOptions.getProperties().getBoolean("clipboard.notification"));
        jLabel24.setEnabled(SystrayHelper.isSystraySupported());
        jEnableSystray.setEnabled(SystrayHelper.isSystraySupported());
        jEnableSystray.setSelected(GlobalOptions.getProperties().getBoolean("systray.enabled"));
        jDeleteFarmReportsOnExit.setSelected(GlobalOptions.getProperties().getBoolean("delete.farm.reports.on.exit"));
        jBrowserPath.setText(GlobalOptions.getProperty("default.browser"));
        jUseStandardBrowser.setSelected(jBrowserPath.getText().length() < 1);
        
        //TODO workaround for now to avoid wrong loading (using data Holder Listener...)
        setOffense(new TroopAmountFixed(0).loadFromProperty(GlobalOptions.getProperty("standard.off")));
        setDefense(new TroopAmountFixed(0).loadFromProperty(GlobalOptions.getProperty("standard.defense.split")));
        jMaxSimRounds.setText(GlobalOptions.getProperty("max.sim.rounds"));
        jTolerance.setText(GlobalOptions.getProperty("support.tolerance"));
        jMaxLossRatio.setText(GlobalOptions.getProperty("max.loss.ratio"));
        jReportServerPort.setText(GlobalOptions.getProperty("report.server.port"));
        jObstServer.setText(GlobalOptions.getProperty("obst.server"));
        
        jMenueSize.setValue(menueSizeToSlider(GlobalOptions.getProperties().getDouble("ribbon.size")));
        jMenueSizeStateChanged(null);
        
        jSliderCmdSleepTime.setValue(GlobalOptions.getProperties().getInt("command.sleep.time"));
        jSliderCmdSleepTimeStateChanged(null);
        
        jLanguageChooser.setSelectedIndex(TranslationManager.findLanguageIndex(GlobalOptions.getProperty("ui.language")));
    }

    private void setDefense(TroopAmountFixed pDefense) {
        sosDefenderSelection.setAmounts(pDefense);
    }

    public TroopAmountFixed getDefense() {
        return sosDefenderSelection.getAmounts();
    }

    private void setOffense(TroopAmountFixed pOffense) {
        sosAttackerSelection.setAmounts(pOffense);
    }

    public TroopAmountFixed getOffense() {
        return sosAttackerSelection.getAmounts();
    }

    public Proxy getWebProxy() {
        if (webProxy == null) {
            return Proxy.NO_PROXY;
        }
        return webProxy;
    }

    public void setupAttackColorTable() {
        jAttackColorTable.invalidate();
        DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    trans.get("Einheit"), trans.get("Farbe")
                }) {

            Class[] types = new Class[]{
                String.class, Color.class
            };
            boolean[] canEdit = new boolean[]{
                false, true
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        jAttackColorTable.setDefaultRenderer(Color.class, new ColorCellRenderer());

        jAttackColorTable.setModel(model);
        jAttackColorTable.getColumnModel().getColumn(1).setMaxWidth(75);
        for (UnitHolder unit : DataHolder.getSingleton().getUnits()) {
            String hexColor = GlobalOptions.getProperty(unit.getName() + ".color");
            try {
                Color col = Color.decode(hexColor);
                model.addRow(new Object[]{unit, col});
            } catch (Exception e) {
                logger.warn("Failed to decode color " + hexColor + ". Switch to default");
                hexColor = Integer.toHexString(Color.RED.getRGB());
                hexColor = "#" + hexColor.substring(2, hexColor.length());
                GlobalOptions.addProperty(unit.getName() + ".color", hexColor);
                model.addRow(new Object[]{unit, Color.RED});
            }
        }

        jAttackColorTable.getTableHeader().setDefaultRenderer(new DefaultTableHeaderRenderer());
        jAttackColorTable.revalidate();
    }

    @Override
    public void setVisible(boolean pValue) {
        if (!INITIALIZED) {
            if (!DataHolder.getSingleton().getUnits().isEmpty()) {
                setupAttackColorTable();
                INITIALIZED = true;
            } else {
                //units not loaded yet
            }
        }
        try {
            super.setVisible(pValue);
        } catch (Exception e) {
            logger.debug("IGNORE: Exception while changing visibility", e);
        }
    }

    public void setBlocking(boolean pValue) {
        isBlocked = pValue;

        if (pValue) {
            setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            jCancelButton.setEnabled(false);
        } else {
            setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
            jCancelButton.setEnabled(true);
        }
    }

    public boolean checkSettings() {
        logger.debug("Checking settings");
        checkConnectivity();
        if (!updateServerList()) {
            //remote update failed and no local servers found
            String message = trans.get("Serverliste_text");
            JOptionPaneHelper.showWarningBox(this, message, trans.get("Warnung"));
            return false;
        }

        return checkTribesAccountSettings();
    }

    //check server and player settings
    private boolean checkServerPlayerSettings() {
        boolean result = false;
        if (!GlobalOptions.getProperties().exists("default.player")
                || !GlobalOptions.getProperties().exists("default.server")) {
            UserProfile selection = null;
            try {
                selection = (UserProfile) jProfileBox.getSelectedItem();
                result = true;
            } catch (Exception e) {
                logger.error("Failed to get selected profile", e);
            }
            //check if selection is valid
            if (selection != null) {
                //set default user for server
                GlobalOptions.addProperty("default.player", Long.toString(selection.getProfileId()));
                GlobalOptions.setSelectedServer(selection.getServerId());
                GlobalOptions.addProperty("default.server", selection.getServerId());
                result = true;
            } else {
                //no default user selected
                logger.error("No profile selected");
            }
        } else {
            String defaultUser = GlobalOptions.getProperty("default.player");
            //check if profile is valid
            UserProfile[] profiles = ProfileManager.getSingleton()
                    .getProfiles(GlobalOptions.getProperty("default.server"));
            for (UserProfile profile : profiles) {
                try {
                    if (profile.getProfileId() == Long.parseLong(defaultUser)) {
                        result = true;
                        break;
                    }
                } catch (NumberFormatException nfe) {
                    logger.error("Failed to get profile for id '" + defaultUser + "'");
                }
            }

            if (!result) {
                try {
                    //profile was probably removed. Get selected entry
                    UserProfile selection = (UserProfile) jProfileBox.getSelectedItem();
                    if (selection != null) {
                        //set default user for server
                        GlobalOptions.addProperty("default.player", Long.toString(selection.getProfileId()));
                        result = true;
                    }
                } catch (Exception e) {
                    logger.error("Failed to get selected profile", e);
                }
            }
        }
        return result;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        connectionTypeGroup = new javax.swing.ButtonGroup();
        tagMarkerGroup = new javax.swing.ButtonGroup();
        jTroopDensitySelectionDialog = new javax.swing.JDialog();
        jDeffStrengthOKButton = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        troopDensitySelection = new de.tor.tribes.ui.panels.TroopSelectionPanelFixed();
        jSettingsTabbedPane = new javax.swing.JTabbedPane();
        jPlayerServerSettings = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jDownloadLiveDataButton = new javax.swing.JButton();
        jCheckForUpdatesBox = new javax.swing.JCheckBox();
        jLabelServer = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jProfileBox = new javax.swing.JComboBox();
        jNewProfileButton = new javax.swing.JButton();
        jModifyProfileButton = new javax.swing.JButton();
        jDeleteProfileButton = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jStatusArea = new javax.swing.JTextArea();
        jMapSettings = new javax.swing.JPanel();
        jShowContinents = new javax.swing.JCheckBox();
        jShowSectors = new javax.swing.JCheckBox();
        jMarkOwnVillagesOnMinimap = new javax.swing.JCheckBox();
        jShowBarbarian = new javax.swing.JCheckBox();
        jMarkerTransparency = new javax.swing.JSlider();
        jShowContinentsLabel = new javax.swing.JLabel();
        jShowSectorsLabel = new javax.swing.JLabel();
        jMarkOwnVillagesOnMinimapLabel = new javax.swing.JLabel();
        jShowBarbarianLabel = new javax.swing.JLabel();
        jMarkerTransparencyLabel = new javax.swing.JLabel();
        jDefaultMarkLabel = new javax.swing.JLabel();
        jDefaultMark = new javax.swing.JComboBox();
        jPanelPopupinfos = new javax.swing.JPanel();
        jMaxFarmSpace = new javax.swing.JTextField();
        jShowPopupConquers = new javax.swing.JCheckBox();
        jShowPopupFarmSpace = new javax.swing.JCheckBox();
        jShowPopupMoral = new javax.swing.JCheckBox();
        jMaxFarmSpacelabel = new javax.swing.JLabel();
        jShowPopupRanks = new javax.swing.JCheckBox();
        jPopupFarmUseRealValues = new javax.swing.JCheckBox();
        jAttackSettings = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jAttackMovementLabel = new javax.swing.JLabel();
        jShowAttackMovementBox = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jAttackColorTable = new javax.swing.JTable();
        jAttackMovementLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jDrawAttacksByDefaultBox = new javax.swing.JCheckBox();
        jLabel21 = new javax.swing.JLabel();
        jShowLiveCountdown = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        jExtendedAttackLineDrawing = new javax.swing.JCheckBox();
        jDefenseSettings = new javax.swing.JPanel();
        jSingleSupportPanel = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        sosDefenderSelection = new de.tor.tribes.ui.panels.TroopSelectionPanelFixed();
        jPanel15 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jMaxSimRounds = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jMaxLossRatio = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jTolerance = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jStandardAttackerPanel = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        sosAttackerSelection = new de.tor.tribes.ui.panels.TroopSelectionPanelFixed();
        jNetworkSettings = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jUseStandardBrowser = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jBrowserPath = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jXLabel3 = new org.jdesktop.swingx.JXLabel();
        jPanel1 = new javax.swing.JPanel();
        jSliderCmdSleepTime = new javax.swing.JSlider();
        jLabelCmdSleepTimePreview = new javax.swing.JLabel();
        jLabelCmdSleepTime = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jDirectConnectOption = new javax.swing.JRadioButton();
        jProxyConnectOption = new javax.swing.JRadioButton();
        jProxyAdressLabel = new javax.swing.JLabel();
        jProxyHost = new javax.swing.JTextField();
        jProxyPortLabel = new javax.swing.JLabel();
        jProxyPort = new javax.swing.JTextField();
        jRefeshNetworkButton = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jProxyTypeChooser = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jProxyUser = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jProxyPassword = new javax.swing.JPasswordField();
        jTemplateSettings = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jHeaderPath = new javax.swing.JTextField();
        jBlockPath = new javax.swing.JTextField();
        jFooterPath = new javax.swing.JTextField();
        jSelectHeaderButton = new javax.swing.JButton();
        jSelectBlockButton = new javax.swing.JButton();
        jSelectFooterButton = new javax.swing.JButton();
        jRestoreHeaderButton = new javax.swing.JButton();
        jRestoreBlockButton = new javax.swing.JButton();
        jRestoreFooterButton = new javax.swing.JButton();
        jMiscSettings = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jVillageSortTypeChooser = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jNotifyDurationBox = new javax.swing.JComboBox();
        jInformOnUpdates = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jMaxTroopDensity = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jClipboardSound = new javax.swing.JCheckBox();
        jLabel20 = new javax.swing.JLabel();
        jDeleteFarmReportsOnExit = new javax.swing.JCheckBox();
        jLabel24 = new javax.swing.JLabel();
        jEnableSystray = new javax.swing.JCheckBox();
        jPanelMenueSize = new javax.swing.JPanel();
        jLabelMenueSize = new javax.swing.JLabel();
        jMenueSize = new javax.swing.JSlider();
        jLabelLanguage = new javax.swing.JLabel();
        jLanguageChooser = new javax.swing.JComboBox();
        jPanel13 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jReportServerPort = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jObstServer = new javax.swing.JTextField();
        jOKButton = new javax.swing.JButton();
        jCancelButton = new javax.swing.JButton();

        jTroopDensitySelectionDialog.setTitle(trans.get("deffanzahl"));
        jTroopDensitySelectionDialog.setModal(true);
        jTroopDensitySelectionDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        jDeffStrengthOKButton.setText(trans.get("OK"));
        jDeffStrengthOKButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAcceptDeffStrengthEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jTroopDensitySelectionDialog.getContentPane().add(jDeffStrengthOKButton, gridBagConstraints);

        jButton12.setText(trans.get("Abbrechen"));
        jButton12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireAcceptDeffStrengthEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jTroopDensitySelectionDialog.getContentPane().add(jButton12, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        jTroopDensitySelectionDialog.getContentPane().add(troopDensitySelection, gridBagConstraints);

        setTitle(trans.get("Einstellungen"));
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                fireClosingEvent(evt);
            }
        });

        jSettingsTabbedPane.setBackground(new java.awt.Color(239, 235, 223));
        jSettingsTabbedPane.setPreferredSize(new java.awt.Dimension(620, 400));

        jPlayerServerSettings.setBackground(new java.awt.Color(239, 235, 223));
        jPlayerServerSettings.setPreferredSize(new java.awt.Dimension(620, 400));

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("Server")));
        jPanel9.setOpaque(false);

        jDownloadLiveDataButton.setBackground(new java.awt.Color(239, 235, 223));
        jDownloadLiveDataButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/download_tw.png"))); // NOI18N
        jDownloadLiveDataButton.setToolTipText(trans.get("Datenmarkierten"));
        jDownloadLiveDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireDownloadLiveDataEvent(evt);
            }
        });

        jCheckForUpdatesBox.setText(trans.get("StartUpdate"));
        jCheckForUpdatesBox.setToolTipText(trans.get("StartUpdate_Text"));
        jCheckForUpdatesBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireCheckForUpdatesEvent(evt);
            }
        });

        jLabelServer.setText(trans.get("de"));
        jLabelServer.setToolTipText(trans.get("gewaehlterserver"));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelServer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDownloadLiveDataButton)
                            .addComponent(jCheckForUpdatesBox))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelServer)
                .addGap(18, 18, 18)
                .addComponent(jDownloadLiveDataButton)
                .addGap(18, 18, 18)
                .addComponent(jCheckForUpdatesBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("Profil")));
        jPanel10.setOpaque(false);

        jProfileBox.setMinimumSize(new java.awt.Dimension(23, 25));
        jProfileBox.setPreferredSize(new java.awt.Dimension(28, 25));
        jProfileBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireSelectProfile(evt);
            }
        });

        jNewProfileButton.setBackground(new java.awt.Color(239, 235, 223));
        jNewProfileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/id_card_new.png"))); // NOI18N
        jNewProfileButton.setToolTipText(trans.get("new_profile"));
        jNewProfileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireProfileActionEvent(evt);
            }
        });

        jModifyProfileButton.setBackground(new java.awt.Color(239, 235, 223));
        jModifyProfileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/id_card_edit.png"))); // NOI18N
        jModifyProfileButton.setToolTipText(trans.get("gewaehltes_profil"));
        jModifyProfileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireProfileActionEvent(evt);
            }
        });

        jDeleteProfileButton.setBackground(new java.awt.Color(239, 235, 223));
        jDeleteProfileButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/id_card_delete.png"))); // NOI18N
        jDeleteProfileButton.setToolTipText(trans.get("gewaehltes_profil_delete"));
        jDeleteProfileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireProfileActionEvent(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProfileBox, 0, 439, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jNewProfileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jModifyProfileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jDeleteProfileButton)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jProfileBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jNewProfileButton)
                    .addComponent(jModifyProfileButton)
                    .addComponent(jDeleteProfileButton))
                .addContainerGap(52, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("Informationen")));
        jPanel11.setOpaque(false);

        jStatusArea.setEditable(false);
        jStatusArea.setColumns(20);
        jStatusArea.setRows(5);
        jScrollPane1.setViewportView(jStatusArea);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPlayerServerSettingsLayout = new javax.swing.GroupLayout(jPlayerServerSettings);
        jPlayerServerSettings.setLayout(jPlayerServerSettingsLayout);
        jPlayerServerSettingsLayout.setHorizontalGroup(
            jPlayerServerSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPlayerServerSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPlayerServerSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPlayerServerSettingsLayout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPlayerServerSettingsLayout.setVerticalGroup(
            jPlayerServerSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPlayerServerSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPlayerServerSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSettingsTabbedPane.addTab(trans.get("SpielerServer_titel"), new javax.swing.ImageIcon(getClass().getResource("/res/face.png")), jPlayerServerSettings); // NOI18N

        jMapSettings.setBackground(new java.awt.Color(239, 235, 223));
        jMapSettings.setPreferredSize(new java.awt.Dimension(620, 400));
        jMapSettings.setLayout(new java.awt.GridBagLayout());

        jShowContinents.setToolTipText(trans.get("Anzeiger_Minimap"));
        jShowContinents.setContentAreaFilled(false);
        jShowContinents.setMaximumSize(new java.awt.Dimension(25, 25));
        jShowContinents.setMinimumSize(new java.awt.Dimension(25, 25));
        jShowContinents.setPreferredSize(new java.awt.Dimension(25, 25));
        jShowContinents.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireChangeContinentsOnMinimapEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jMapSettings.add(jShowContinents, gridBagConstraints);

        jShowSectors.setToolTipText(trans.get("Sektoren_Hauptkarte"));
        jShowSectors.setMaximumSize(new java.awt.Dimension(25, 25));
        jShowSectors.setMinimumSize(new java.awt.Dimension(25, 25));
        jShowSectors.setPreferredSize(new java.awt.Dimension(25, 25));
        jShowSectors.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fireChangeShowSectorsEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jMapSettings.add(jShowSectors, gridBagConstraints);

        jMarkOwnVillagesOnMinimap.setToolTipText(trans.get("Markierte_Doerfer_Minimap"));
        jMarkOwnVillagesOnMinimap.setMaximumSize(new java.awt.Dimension(25, 25));
        jMarkOwnVillagesOnMinimap.setMinimumSize(new java.awt.Dimension(25, 25));
        jMarkOwnVillagesOnMinimap.setPreferredSize(new java.awt.Dimension(25, 25));
        jMarkOwnVillagesOnMinimap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireChangeMarkOwnVillagesOnMinimapEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jMapSettings.add(jMarkOwnVillagesOnMinimap, gridBagConstraints);

        jShowBarbarian.setToolTipText(trans.get("Anzeige_Barbarendoerfer"));
        jShowBarbarian.setMaximumSize(new java.awt.Dimension(25, 25));
        jShowBarbarian.setMinimumSize(new java.awt.Dimension(25, 25));
        jShowBarbarian.setPreferredSize(new java.awt.Dimension(25, 25));
        jShowBarbarian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireShowBarbarianChangedEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jMapSettings.add(jShowBarbarian, gridBagConstraints);

        jMarkerTransparency.setMajorTickSpacing(10);
        jMarkerTransparency.setMinimum(10);
        jMarkerTransparency.setMinorTickSpacing(1);
        jMarkerTransparency.setPaintLabels(true);
        jMarkerTransparency.setPaintTicks(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        jMapSettings.add(jMarkerTransparency, gridBagConstraints);

        jShowContinentsLabel.setText(trans.get("Kontinenteanzeigen"));
        jShowContinentsLabel.setMaximumSize(new java.awt.Dimension(250, 25));
        jShowContinentsLabel.setMinimumSize(new java.awt.Dimension(250, 25));
        jShowContinentsLabel.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jMapSettings.add(jShowContinentsLabel, gridBagConstraints);

        jShowSectorsLabel.setText(trans.get("Sektorenanzeigen"));
        jShowSectorsLabel.setMaximumSize(new java.awt.Dimension(250, 25));
        jShowSectorsLabel.setMinimumSize(new java.awt.Dimension(250, 25));
        jShowSectorsLabel.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jMapSettings.add(jShowSectorsLabel, gridBagConstraints);

        jMarkOwnVillagesOnMinimapLabel.setText(trans.get("EigeneDoerfer_Minimap"));
        jMarkOwnVillagesOnMinimapLabel.setMaximumSize(new java.awt.Dimension(250, 25));
        jMarkOwnVillagesOnMinimapLabel.setMinimumSize(new java.awt.Dimension(250, 25));
        jMarkOwnVillagesOnMinimapLabel.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jMapSettings.add(jMarkOwnVillagesOnMinimapLabel, gridBagConstraints);

        jShowBarbarianLabel.setText(trans.get("Barbarendoerferanzeigen"));
        jShowBarbarianLabel.setToolTipText("");
        jShowBarbarianLabel.setMaximumSize(new java.awt.Dimension(250, 25));
        jShowBarbarianLabel.setMinimumSize(new java.awt.Dimension(250, 25));
        jShowBarbarianLabel.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jMapSettings.add(jShowBarbarianLabel, gridBagConstraints);

        jMarkerTransparencyLabel.setText(trans.get("Deckkraft"));
        jMarkerTransparencyLabel.setMaximumSize(new java.awt.Dimension(250, 25));
        jMarkerTransparencyLabel.setMinimumSize(new java.awt.Dimension(250, 25));
        jMarkerTransparencyLabel.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jMapSettings.add(jMarkerTransparencyLabel, gridBagConstraints);

        jDefaultMarkLabel.setText(trans.get("Standardmarkierung"));
        jDefaultMarkLabel.setMaximumSize(new java.awt.Dimension(250, 25));
        jDefaultMarkLabel.setMinimumSize(new java.awt.Dimension(250, 25));
        jDefaultMarkLabel.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jMapSettings.add(jDefaultMarkLabel, gridBagConstraints);

        jDefaultMark.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "DS 6.0", "Rot", "Weiß" }));
        jDefaultMark.setToolTipText(trans.get("StandardfarbeDorfmarkierungen"));
        jDefaultMark.setMinimumSize(new java.awt.Dimension(52, 25));
        jDefaultMark.setPreferredSize(new java.awt.Dimension(57, 25));
        jDefaultMark.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireStandardMarkChangedEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        jMapSettings.add(jDefaultMark, gridBagConstraints);

        jPanelPopupinfos.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("PopupInfos")));
        jPanelPopupinfos.setLayout(new java.awt.GridBagLayout());

        jMaxFarmSpace.setText("20000");
        jMaxFarmSpace.setToolTipText(trans.get("AnzahlTruppen"));
        jMaxFarmSpace.setMinimumSize(new java.awt.Dimension(50, 25));
        jMaxFarmSpace.setPreferredSize(new java.awt.Dimension(100, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        jPanelPopupinfos.add(jMaxFarmSpace, gridBagConstraints);

        jShowPopupConquers.setText(trans.get("BesiegteGegneranzeigen"));
        jShowPopupConquers.setToolTipText(trans.get("BesiegteGegneranzeigen_text"));
        jShowPopupConquers.setMaximumSize(new java.awt.Dimension(193, 25));
        jShowPopupConquers.setMinimumSize(new java.awt.Dimension(250, 25));
        jShowPopupConquers.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        jPanelPopupinfos.add(jShowPopupConquers, gridBagConstraints);

        jShowPopupFarmSpace.setText(trans.get("Bauernhof"));
        jShowPopupFarmSpace.setToolTipText(trans.get("Bauernhof_text"));
        jShowPopupFarmSpace.setMaximumSize(new java.awt.Dimension(193, 25));
        jShowPopupFarmSpace.setMinimumSize(new java.awt.Dimension(250, 25));
        jShowPopupFarmSpace.setPreferredSize(new java.awt.Dimension(250, 25));
        jShowPopupFarmSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPopupFarmActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        jPanelPopupinfos.add(jShowPopupFarmSpace, gridBagConstraints);

        jShowPopupMoral.setText(trans.get("Moralanzeigen"));
        jShowPopupMoral.setToolTipText(trans.get("Moralanzeigen"));
        jShowPopupMoral.setMaximumSize(new java.awt.Dimension(193, 25));
        jShowPopupMoral.setMinimumSize(new java.awt.Dimension(250, 25));
        jShowPopupMoral.setPreferredSize(new java.awt.Dimension(250, 25));
        jShowPopupMoral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jShowPopupMoralActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        jPanelPopupinfos.add(jShowPopupMoral, gridBagConstraints);

        jMaxFarmSpacelabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jMaxFarmSpacelabel.setText(trans.get("Max_Bauerhofplaetze"));
        jMaxFarmSpacelabel.setMinimumSize(new java.awt.Dimension(180, 25));
        jMaxFarmSpacelabel.setPreferredSize(new java.awt.Dimension(180, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        jPanelPopupinfos.add(jMaxFarmSpacelabel, gridBagConstraints);

        jShowPopupRanks.setText(trans.get("ErweiterteInformation"));
        jShowPopupRanks.setToolTipText(trans.get("ErweiterteInformation_text"));
        jShowPopupRanks.setMaximumSize(new java.awt.Dimension(193, 25));
        jShowPopupRanks.setMinimumSize(new java.awt.Dimension(250, 25));
        jShowPopupRanks.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1.0;
        jPanelPopupinfos.add(jShowPopupRanks, gridBagConstraints);

        jPopupFarmUseRealValues.setText(trans.get("EchteWertebenutzen"));
        jPopupFarmUseRealValues.setToolTipText(trans.get("EchteWertebenutzen_text"));
        jPopupFarmUseRealValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPopupFarmActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        jPanelPopupinfos.add(jPopupFarmUseRealValues, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jMapSettings.add(jPanelPopupinfos, gridBagConstraints);

        jSettingsTabbedPane.addTab(trans.get("Karten"), new javax.swing.ImageIcon(getClass().getResource("/res/ui/map.gif")), jMapSettings); // NOI18N

        jAttackSettings.setBackground(new java.awt.Color(239, 235, 223));
        jAttackSettings.setPreferredSize(new java.awt.Dimension(620, 400));

        jPanel12.setMinimumSize(new java.awt.Dimension(600, 300));
        jPanel12.setOpaque(false);
        jPanel12.setPreferredSize(new java.awt.Dimension(500, 300));
        jPanel12.setLayout(new java.awt.GridBagLayout());

        jAttackMovementLabel.setText(trans.get("Truppenbewegunganzeigen"));
        jAttackMovementLabel.setMaximumSize(new java.awt.Dimension(260, 25));
        jAttackMovementLabel.setMinimumSize(new java.awt.Dimension(260, 25));
        jAttackMovementLabel.setPreferredSize(new java.awt.Dimension(260, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jAttackMovementLabel, gridBagConstraints);

        jShowAttackMovementBox.setToolTipText(trans.get("AnzeigeTruppenbewegungen"));
        jShowAttackMovementBox.setMaximumSize(new java.awt.Dimension(21, 25));
        jShowAttackMovementBox.setMinimumSize(new java.awt.Dimension(21, 25));
        jShowAttackMovementBox.setPreferredSize(new java.awt.Dimension(21, 25));
        jShowAttackMovementBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireChangeShowAttackMovementEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jShowAttackMovementBox, gridBagConstraints);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(300, 200));
        jScrollPane2.setOpaque(false);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(300, 200));

        jAttackColorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jAttackColorTable.setOpaque(false);
        jScrollPane2.setViewportView(jAttackColorTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jScrollPane2, gridBagConstraints);

        jAttackMovementLabel3.setText(trans.get("FaerbungderBefehlverktoren"));
        jAttackMovementLabel3.setMaximumSize(new java.awt.Dimension(260, 25));
        jAttackMovementLabel3.setMinimumSize(new java.awt.Dimension(260, 25));
        jAttackMovementLabel3.setPreferredSize(new java.awt.Dimension(260, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jAttackMovementLabel3, gridBagConstraints);

        jLabel9.setText(trans.get("NeueBefehle"));
        jLabel9.setMaximumSize(new java.awt.Dimension(280, 25));
        jLabel9.setMinimumSize(new java.awt.Dimension(280, 25));
        jLabel9.setPreferredSize(new java.awt.Dimension(280, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel9, gridBagConstraints);

        jDrawAttacksByDefaultBox.setToolTipText(trans.get("NeueerstellenBefehle"));
        jDrawAttacksByDefaultBox.setMaximumSize(new java.awt.Dimension(21, 25));
        jDrawAttacksByDefaultBox.setMinimumSize(new java.awt.Dimension(21, 25));
        jDrawAttacksByDefaultBox.setPreferredSize(new java.awt.Dimension(21, 25));
        jDrawAttacksByDefaultBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireDrawAttacksByDefaultChangedEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jDrawAttacksByDefaultBox, gridBagConstraints);

        jLabel21.setText(trans.get("Countdown"));
        jLabel21.setMaximumSize(new java.awt.Dimension(260, 25));
        jLabel21.setMinimumSize(new java.awt.Dimension(260, 25));
        jLabel21.setPreferredSize(new java.awt.Dimension(260, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel21, gridBagConstraints);

        jShowLiveCountdown.setToolTipText(trans.get("HTML_Countdown"));
        jShowLiveCountdown.setMaximumSize(new java.awt.Dimension(21, 25));
        jShowLiveCountdown.setMinimumSize(new java.awt.Dimension(21, 25));
        jShowLiveCountdown.setPreferredSize(new java.awt.Dimension(21, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jShowLiveCountdown, gridBagConstraints);

        jLabel23.setText(trans.get("Laufrichtung"));
        jLabel23.setMaximumSize(new java.awt.Dimension(260, 25));
        jLabel23.setMinimumSize(new java.awt.Dimension(260, 25));
        jLabel23.setPreferredSize(new java.awt.Dimension(260, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel23, gridBagConstraints);

        jExtendedAttackLineDrawing.setToolTipText(trans.get("ZeigeLaufrichtung_Text"));
        jExtendedAttackLineDrawing.setMaximumSize(new java.awt.Dimension(21, 25));
        jExtendedAttackLineDrawing.setMinimumSize(new java.awt.Dimension(21, 25));
        jExtendedAttackLineDrawing.setPreferredSize(new java.awt.Dimension(21, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jExtendedAttackLineDrawing, gridBagConstraints);

        javax.swing.GroupLayout jAttackSettingsLayout = new javax.swing.GroupLayout(jAttackSettings);
        jAttackSettings.setLayout(jAttackSettingsLayout);
        jAttackSettingsLayout.setHorizontalGroup(
            jAttackSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAttackSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                .addContainerGap())
        );
        jAttackSettingsLayout.setVerticalGroup(
            jAttackSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jAttackSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSettingsTabbedPane.addTab(trans.get("Angriffe"), new javax.swing.ImageIcon(getClass().getResource("/res/barracks.png")), jAttackSettings); // NOI18N

        jDefenseSettings.setBackground(new java.awt.Color(239, 235, 223));
        jDefenseSettings.setLayout(new java.awt.GridBagLayout());

        jSingleSupportPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("Einzelunterstuetzung")));
        jSingleSupportPanel.setOpaque(false);
        jSingleSupportPanel.setLayout(new java.awt.GridBagLayout());

        jButton4.setText(trans.get("uebernehmen"));
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireChangeDefEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jSingleSupportPanel.add(jButton4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jSingleSupportPanel.add(sosDefenderSelection, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jDefenseSettings.add(jSingleSupportPanel, gridBagConstraints);

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("SonstigeEinstellungen")));
        jPanel15.setOpaque(false);
        jPanel15.setLayout(new java.awt.GridBagLayout());

        jLabel25.setText(trans.get("Max_Simulationsrunden"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(jLabel25, gridBagConstraints);

        jMaxSimRounds.setText("500");
        jMaxSimRounds.setToolTipText(trans.get("Simulationsrunden_text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(jMaxSimRounds, gridBagConstraints);

        jLabel26.setText(trans.get("Max_Verlustrate"));
        jLabel26.setMaximumSize(new java.awt.Dimension(114, 14));
        jLabel26.setMinimumSize(new java.awt.Dimension(114, 14));
        jLabel26.setPreferredSize(new java.awt.Dimension(114, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(jLabel26, gridBagConstraints);

        jMaxLossRatio.setText("50");
        jMaxLossRatio.setToolTipText(trans.get("Verlustrate_text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(jMaxLossRatio, gridBagConstraints);

        jLabel27.setText("%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel15.add(jLabel27, gridBagConstraints);

        jLabel28.setText(trans.get("Toleranz"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(jLabel28, gridBagConstraints);

        jTolerance.setText("10");
        jTolerance.setToolTipText(trans.get("Toleranz_text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(jTolerance, gridBagConstraints);

        jLabel29.setText("%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel15.add(jLabel29, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jDefenseSettings.add(jPanel15, gridBagConstraints);

        jStandardAttackerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("AngreiferimVerteidigungsplaner")));
        jStandardAttackerPanel.setOpaque(false);
        jStandardAttackerPanel.setLayout(new java.awt.GridBagLayout());

        jButton3.setText(trans.get("uebernehmen"));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireChangeOffEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jStandardAttackerPanel.add(jButton3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jStandardAttackerPanel.add(sosAttackerSelection, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jDefenseSettings.add(jStandardAttackerPanel, gridBagConstraints);

        jSettingsTabbedPane.addTab(trans.get("Verteidigung"), new javax.swing.ImageIcon(getClass().getResource("/res/ally.png")), jDefenseSettings); // NOI18N

        jNetworkSettings.setBackground(new java.awt.Color(239, 235, 223));
        jNetworkSettings.setPreferredSize(new java.awt.Dimension(620, 400));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("Browser")));
        jPanel4.setMaximumSize(new java.awt.Dimension(400, 126));
        jPanel4.setMinimumSize(new java.awt.Dimension(400, 126));
        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(400, 126));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jUseStandardBrowser.setSelected(true);
        jUseStandardBrowser.setText(trans.get("Standardbrowser"));
        jUseStandardBrowser.setToolTipText(trans.get("Standardbrowser_text"));
        jUseStandardBrowser.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireChangeDefaultBrowserEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jUseStandardBrowser, gridBagConstraints);

        jLabel5.setText(trans.get("AlternativerBrowser"));
        jLabel5.setEnabled(false);
        jLabel5.setMaximumSize(new java.awt.Dimension(120, 23));
        jLabel5.setMinimumSize(new java.awt.Dimension(120, 23));
        jLabel5.setPreferredSize(new java.awt.Dimension(120, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jLabel5, gridBagConstraints);

        jBrowserPath.setEnabled(false);
        jBrowserPath.setMinimumSize(new java.awt.Dimension(6, 23));
        jBrowserPath.setPreferredSize(new java.awt.Dimension(6, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jBrowserPath, gridBagConstraints);

        jButton1.setBackground(new java.awt.Color(239, 235, 223));
        jButton1.setText("...");
        jButton1.setEnabled(false);
        jButton1.setMaximumSize(new java.awt.Dimension(23, 23));
        jButton1.setMinimumSize(new java.awt.Dimension(23, 23));
        jButton1.setPreferredSize(new java.awt.Dimension(23, 23));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSelectBrowserEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jButton1, gridBagConstraints);

        jXLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jXLabel3.setText(trans.get("AlternativerBrowser_text"));
        jXLabel3.setToolTipText("");
        jXLabel3.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jXLabel3.setLineWrap(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jXLabel3, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jSliderCmdSleepTime.setMaximum(2000);
        jSliderCmdSleepTime.setMinimum(100);
        jSliderCmdSleepTime.setSnapToTicks(true);
        jSliderCmdSleepTime.setValue(150);
        jSliderCmdSleepTime.setMinimumSize(new java.awt.Dimension(150, 46));
        jSliderCmdSleepTime.setPreferredSize(new java.awt.Dimension(300, 46));
        jSliderCmdSleepTime.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderCmdSleepTimeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel1.add(jSliderCmdSleepTime, gridBagConstraints);

        jLabelCmdSleepTimePreview.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCmdSleepTimePreview.setText("20");
        jLabelCmdSleepTimePreview.setMaximumSize(new java.awt.Dimension(50, 18));
        jLabelCmdSleepTimePreview.setMinimumSize(new java.awt.Dimension(50, 18));
        jLabelCmdSleepTimePreview.setPreferredSize(new java.awt.Dimension(50, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 16, 0, 0);
        jPanel1.add(jLabelCmdSleepTimePreview, gridBagConstraints);

        jLabelCmdSleepTime.setText(trans.get("WartezeitzwischendenTabs"));
        jLabelCmdSleepTime.setMaximumSize(new java.awt.Dimension(400, 18));
        jLabelCmdSleepTime.setMinimumSize(new java.awt.Dimension(220, 18));
        jLabelCmdSleepTime.setPreferredSize(new java.awt.Dimension(220, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(jLabelCmdSleepTime, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel4.add(jPanel1, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.GridBagLayout());

        connectionTypeGroup.add(jDirectConnectOption);
        jDirectConnectOption.setSelected(true);
        jDirectConnectOption.setText(trans.get("Internetverbunden"));
        jDirectConnectOption.setMaximumSize(new java.awt.Dimension(259, 23));
        jDirectConnectOption.setMinimumSize(new java.awt.Dimension(259, 23));
        jDirectConnectOption.setPreferredSize(new java.awt.Dimension(259, 23));
        jDirectConnectOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireChangeConnectTypeEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jDirectConnectOption, gridBagConstraints);

        connectionTypeGroup.add(jProxyConnectOption);
        jProxyConnectOption.setText(trans.get("Proxy_internet"));
        jProxyConnectOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fireChangeConnectTypeEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyConnectOption, gridBagConstraints);

        jProxyAdressLabel.setText(trans.get("ProxyAdresse"));
        jProxyAdressLabel.setMaximumSize(new java.awt.Dimension(100, 23));
        jProxyAdressLabel.setMinimumSize(new java.awt.Dimension(100, 23));
        jProxyAdressLabel.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyAdressLabel, gridBagConstraints);

        jProxyHost.setToolTipText(trans.get("AdressedesProxyServers"));
        jProxyHost.setEnabled(false);
        jProxyHost.setMinimumSize(new java.awt.Dimension(6, 23));
        jProxyHost.setPreferredSize(new java.awt.Dimension(6, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyHost, gridBagConstraints);

        jProxyPortLabel.setText(trans.get("ProxyPort"));
        jProxyPortLabel.setMaximumSize(new java.awt.Dimension(70, 23));
        jProxyPortLabel.setMinimumSize(new java.awt.Dimension(70, 23));
        jProxyPortLabel.setPreferredSize(new java.awt.Dimension(70, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyPortLabel, gridBagConstraints);

        jProxyPort.setToolTipText(trans.get("PortdesProxyServers"));
        jProxyPort.setEnabled(false);
        jProxyPort.setMaximumSize(new java.awt.Dimension(40, 23));
        jProxyPort.setMinimumSize(new java.awt.Dimension(40, 23));
        jProxyPort.setPreferredSize(new java.awt.Dimension(40, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyPort, gridBagConstraints);

        jRefeshNetworkButton.setBackground(new java.awt.Color(239, 235, 223));
        jRefeshNetworkButton.setText(trans.get("Aktualisieren"));
        jRefeshNetworkButton.setToolTipText(trans.get("Aktualisieren_text"));
        jRefeshNetworkButton.setMaximumSize(new java.awt.Dimension(120, 23));
        jRefeshNetworkButton.setMinimumSize(new java.awt.Dimension(120, 23));
        jRefeshNetworkButton.setPreferredSize(new java.awt.Dimension(120, 23));
        jRefeshNetworkButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireUpdateProxySettingsEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jRefeshNetworkButton, gridBagConstraints);

        jLabel10.setText(trans.get("ProxyTyp"));
        jLabel10.setMaximumSize(new java.awt.Dimension(100, 23));
        jLabel10.setMinimumSize(new java.awt.Dimension(100, 23));
        jLabel10.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jLabel10, gridBagConstraints);

        jProxyTypeChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "HTTP", "SOCKS" }));
        jProxyTypeChooser.setToolTipText(trans.get("ArtdesProxyServers"));
        jProxyTypeChooser.setEnabled(false);
        jProxyTypeChooser.setMinimumSize(new java.awt.Dimension(100, 23));
        jProxyTypeChooser.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyTypeChooser, gridBagConstraints);

        jLabel11.setText(trans.get("Benutzername"));
        jLabel11.setMaximumSize(new java.awt.Dimension(100, 23));
        jLabel11.setMinimumSize(new java.awt.Dimension(100, 23));
        jLabel11.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jLabel11, gridBagConstraints);

        jProxyUser.setToolTipText(trans.get("Benutzername_text"));
        jProxyUser.setEnabled(false);
        jProxyUser.setMaximumSize(new java.awt.Dimension(150, 23));
        jProxyUser.setMinimumSize(new java.awt.Dimension(150, 23));
        jProxyUser.setPreferredSize(new java.awt.Dimension(150, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyUser, gridBagConstraints);

        jLabel12.setText(trans.get("Passwort"));
        jLabel12.setMaximumSize(new java.awt.Dimension(100, 23));
        jLabel12.setMinimumSize(new java.awt.Dimension(100, 23));
        jLabel12.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jLabel12, gridBagConstraints);

        jProxyPassword.setToolTipText(trans.get("PasswortAuthentifizierungProxyServer"));
        jProxyPassword.setEnabled(false);
        jProxyPassword.setMaximumSize(new java.awt.Dimension(150, 23));
        jProxyPassword.setMinimumSize(new java.awt.Dimension(150, 23));
        jProxyPassword.setPreferredSize(new java.awt.Dimension(150, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyPassword, gridBagConstraints);

        javax.swing.GroupLayout jNetworkSettingsLayout = new javax.swing.GroupLayout(jNetworkSettings);
        jNetworkSettings.setLayout(jNetworkSettingsLayout);
        jNetworkSettingsLayout.setHorizontalGroup(
            jNetworkSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jNetworkSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jNetworkSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE))
                .addContainerGap())
        );
        jNetworkSettingsLayout.setVerticalGroup(
            jNetworkSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jNetworkSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSettingsTabbedPane.addTab(trans.get("Netzwerk"), new javax.swing.ImageIcon(getClass().getResource("/res/proxy.png")), jNetworkSettings); // NOI18N

        jTemplateSettings.setBackground(new java.awt.Color(239, 235, 223));
        jTemplateSettings.setPreferredSize(new java.awt.Dimension(620, 400));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(trans.get("HTMLTemplates")));
        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel6.setText("Header");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jLabel6, gridBagConstraints);

        jLabel18.setText(trans.get("Angriffsblock"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jLabel18, gridBagConstraints);

        jLabel19.setText(trans.get("Footer"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jLabel19, gridBagConstraints);

        jHeaderPath.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jHeaderPath, gridBagConstraints);

        jBlockPath.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jBlockPath, gridBagConstraints);

        jFooterPath.setText("<Standard>");
        jFooterPath.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jFooterPath, gridBagConstraints);

        jSelectHeaderButton.setBackground(new java.awt.Color(239, 235, 223));
        jSelectHeaderButton.setText("...");
        jSelectHeaderButton.setToolTipText(trans.get("Templatewaehlen"));
        jSelectHeaderButton.setMaximumSize(new java.awt.Dimension(25, 23));
        jSelectHeaderButton.setMinimumSize(new java.awt.Dimension(25, 23));
        jSelectHeaderButton.setPreferredSize(new java.awt.Dimension(25, 23));
        jSelectHeaderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSelectTemplateEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jSelectHeaderButton, gridBagConstraints);

        jSelectBlockButton.setBackground(new java.awt.Color(239, 235, 223));
        jSelectBlockButton.setText("...");
        jSelectBlockButton.setToolTipText(trans.get("Templatewaehlen"));
        jSelectBlockButton.setMaximumSize(new java.awt.Dimension(25, 23));
        jSelectBlockButton.setMinimumSize(new java.awt.Dimension(25, 23));
        jSelectBlockButton.setPreferredSize(new java.awt.Dimension(25, 23));
        jSelectBlockButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSelectTemplateEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jSelectBlockButton, gridBagConstraints);

        jSelectFooterButton.setBackground(new java.awt.Color(239, 235, 223));
        jSelectFooterButton.setText("...");
        jSelectFooterButton.setToolTipText(trans.get("Templatewaehlen"));
        jSelectFooterButton.setMaximumSize(new java.awt.Dimension(25, 23));
        jSelectFooterButton.setMinimumSize(new java.awt.Dimension(25, 23));
        jSelectFooterButton.setPreferredSize(new java.awt.Dimension(25, 23));
        jSelectFooterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSelectTemplateEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jSelectFooterButton, gridBagConstraints);

        jRestoreHeaderButton.setBackground(new java.awt.Color(239, 235, 223));
        jRestoreHeaderButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/refresh.png"))); // NOI18N
        jRestoreHeaderButton.setToolTipText(trans.get("Standardwiederherstellen"));
        jRestoreHeaderButton.setAlignmentY(0.0F);
        jRestoreHeaderButton.setMaximumSize(new java.awt.Dimension(25, 23));
        jRestoreHeaderButton.setMinimumSize(new java.awt.Dimension(25, 23));
        jRestoreHeaderButton.setPreferredSize(new java.awt.Dimension(25, 23));
        jRestoreHeaderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRestoreTemplateEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jRestoreHeaderButton, gridBagConstraints);

        jRestoreBlockButton.setBackground(new java.awt.Color(239, 235, 223));
        jRestoreBlockButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/refresh.png"))); // NOI18N
        jRestoreBlockButton.setToolTipText(trans.get("Standardwiederherstellen"));
        jRestoreBlockButton.setMaximumSize(new java.awt.Dimension(25, 23));
        jRestoreBlockButton.setMinimumSize(new java.awt.Dimension(25, 23));
        jRestoreBlockButton.setPreferredSize(new java.awt.Dimension(25, 23));
        jRestoreBlockButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRestoreTemplateEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jRestoreBlockButton, gridBagConstraints);

        jRestoreFooterButton.setBackground(new java.awt.Color(239, 235, 223));
        jRestoreFooterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/refresh.png"))); // NOI18N
        jRestoreFooterButton.setToolTipText(trans.get("Standardwiederherstellen"));
        jRestoreFooterButton.setMaximumSize(new java.awt.Dimension(25, 23));
        jRestoreFooterButton.setMinimumSize(new java.awt.Dimension(25, 23));
        jRestoreFooterButton.setPreferredSize(new java.awt.Dimension(25, 23));
        jRestoreFooterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireRestoreTemplateEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(jRestoreFooterButton, gridBagConstraints);

        javax.swing.GroupLayout jTemplateSettingsLayout = new javax.swing.GroupLayout(jTemplateSettings);
        jTemplateSettings.setLayout(jTemplateSettingsLayout);
        jTemplateSettingsLayout.setHorizontalGroup(
            jTemplateSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTemplateSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
                .addContainerGap())
        );
        jTemplateSettingsLayout.setVerticalGroup(
            jTemplateSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jTemplateSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(374, Short.MAX_VALUE))
        );

        jSettingsTabbedPane.addTab(trans.get("Templates"), new javax.swing.ImageIcon(getClass().getResource("/res/ui/component.png")), jTemplateSettings); // NOI18N

        jMiscSettings.setBackground(new java.awt.Color(239, 235, 223));
        jMiscSettings.setPreferredSize(new java.awt.Dimension(620, 400));

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jVillageSortTypeChooser.setModel(jVillageSortTypeChooserModel);
        jVillageSortTypeChooser.setToolTipText("Art der Dorfsortierung in DS Workbench");
        jVillageSortTypeChooser.setMaximumSize(new java.awt.Dimension(105, 18));
        jVillageSortTypeChooser.setPreferredSize(new java.awt.Dimension(105, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jVillageSortTypeChooser, gridBagConstraints);

        jLabel13.setText(trans.get("Dorfsortierung"));
        jLabel13.setMaximumSize(new java.awt.Dimension(138, 18));
        jLabel13.setMinimumSize(new java.awt.Dimension(138, 18));
        jLabel13.setPreferredSize(new java.awt.Dimension(138, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel13, gridBagConstraints);

        jLabel14.setText(trans.get("Anzeigedauer_hinweisen"));
        jLabel14.setMaximumSize(new java.awt.Dimension(138, 18));
        jLabel14.setMinimumSize(new java.awt.Dimension(138, 18));
        jLabel14.setPreferredSize(new java.awt.Dimension(138, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel14, gridBagConstraints);

        jNotifyDurationBox.setModel(jNotifyDurationBoxModel);
        jNotifyDurationBox.setToolTipText(trans.get("Zeitdauer_hinweis"));
        jNotifyDurationBox.setMaximumSize(new java.awt.Dimension(105, 18));
        jNotifyDurationBox.setMinimumSize(new java.awt.Dimension(105, 18));
        jNotifyDurationBox.setPreferredSize(new java.awt.Dimension(105, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jNotifyDurationBox, gridBagConstraints);

        jInformOnUpdates.setSelected(true);
        jInformOnUpdates.setToolTipText(trans.get("Pruefungauf_DSWorkbench"));
        jInformOnUpdates.setMaximumSize(new java.awt.Dimension(105, 18));
        jInformOnUpdates.setMinimumSize(new java.awt.Dimension(105, 18));
        jInformOnUpdates.setPreferredSize(new java.awt.Dimension(105, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jInformOnUpdates, gridBagConstraints);

        jLabel15.setText(trans.get("Updatesinformieren"));
        jLabel15.setMaximumSize(new java.awt.Dimension(138, 18));
        jLabel15.setMinimumSize(new java.awt.Dimension(138, 18));
        jLabel15.setPreferredSize(new java.awt.Dimension(138, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel15, gridBagConstraints);

        jLabel16.setText(trans.get("BerechnungTruppendichte"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel16, gridBagConstraints);

        jMaxTroopDensity.setText("650000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jMaxTroopDensity, gridBagConstraints);

        jButton8.setBackground(new java.awt.Color(239, 235, 223));
        jButton8.setText(trans.get("Auswaehlen"));
        jButton8.setToolTipText(trans.get("Truppenstaerke_text"));
        jButton8.setMaximumSize(new java.awt.Dimension(90, 23));
        jButton8.setMinimumSize(new java.awt.Dimension(90, 23));
        jButton8.setPreferredSize(new java.awt.Dimension(90, 23));
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSelectTroopsDensityEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jButton8, gridBagConstraints);

        jLabel2.setText(trans.get("Hauptmenuegroesse"));
        jLabel2.setMaximumSize(new java.awt.Dimension(34, 18));
        jLabel2.setMinimumSize(new java.awt.Dimension(34, 18));
        jLabel2.setPreferredSize(new java.awt.Dimension(34, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel2, gridBagConstraints);

        jLabel8.setText(trans.get("Clipboard_Hinweis"));
        jLabel8.setMaximumSize(new java.awt.Dimension(34, 18));
        jLabel8.setMinimumSize(new java.awt.Dimension(34, 18));
        jLabel8.setPreferredSize(new java.awt.Dimension(34, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel8, gridBagConstraints);

        jClipboardSound.setSelected(true);
        jClipboardSound.setToolTipText(trans.get("SpielteinenTon"));
        jClipboardSound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireEnableClipboardNotificationEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jClipboardSound, gridBagConstraints);

        jLabel20.setText(trans.get("Farmberichte"));
        jLabel20.setMaximumSize(new java.awt.Dimension(34, 18));
        jLabel20.setMinimumSize(new java.awt.Dimension(34, 18));
        jLabel20.setPreferredSize(new java.awt.Dimension(34, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel20, gridBagConstraints);

        jDeleteFarmReportsOnExit.setSelected(true);
        jDeleteFarmReportsOnExit.setToolTipText(trans.get("Farmberichte_Beenden"));
        jDeleteFarmReportsOnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireDeleteFarmReportsOnExitEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jDeleteFarmReportsOnExit, gridBagConstraints);

        jLabel24.setText(trans.get("Systray"));
        jLabel24.setMaximumSize(new java.awt.Dimension(34, 18));
        jLabel24.setMinimumSize(new java.awt.Dimension(34, 18));
        jLabel24.setPreferredSize(new java.awt.Dimension(34, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel24, gridBagConstraints);

        jEnableSystray.setSelected(true);
        jEnableSystray.setToolTipText(trans.get("Systray_open"));
        jEnableSystray.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fireEnableSystrayEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jEnableSystray, gridBagConstraints);

        jPanelMenueSize.setMinimumSize(new java.awt.Dimension(206, 46));
        jPanelMenueSize.setOpaque(false);
        jPanelMenueSize.setPreferredSize(new java.awt.Dimension(206, 46));
        jPanelMenueSize.setLayout(new java.awt.GridBagLayout());

        jLabelMenueSize.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelMenueSize.setText("20");
        jLabelMenueSize.setMaximumSize(new java.awt.Dimension(10, 18));
        jLabelMenueSize.setMinimumSize(new java.awt.Dimension(10, 18));
        jLabelMenueSize.setPreferredSize(new java.awt.Dimension(10, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanelMenueSize.add(jLabelMenueSize, gridBagConstraints);

        jMenueSize.setMaximum(20);
        jMenueSize.setSnapToTicks(true);
        jMenueSize.setPreferredSize(new java.awt.Dimension(50, 46));
        jMenueSize.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jMenueSizeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 271;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelMenueSize.add(jMenueSize, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel6.add(jPanelMenueSize, gridBagConstraints);

        jLabelLanguage.setText(trans.get("Language"));
        jLabelLanguage.setMaximumSize(new java.awt.Dimension(138, 18));
        jLabelLanguage.setMinimumSize(new java.awt.Dimension(138, 18));
        jLabelLanguage.setPreferredSize(new java.awt.Dimension(138, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabelLanguage, gridBagConstraints);

        jLanguageChooser.setToolTipText(trans.get("LanguageHelp"));
        jLanguageChooser.setMaximumSize(new java.awt.Dimension(105, 18));
        jLanguageChooser.setPreferredSize(new java.awt.Dimension(105, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLanguageChooser, gridBagConstraints);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Berichtserver"));
        jPanel13.setOpaque(false);
        jPanel13.setPreferredSize(new java.awt.Dimension(72, 50));
        jPanel13.setLayout(new java.awt.GridBagLayout());

        jLabel31.setText("Port");
        jLabel31.setMaximumSize(new java.awt.Dimension(50, 14));
        jLabel31.setMinimumSize(new java.awt.Dimension(50, 14));
        jLabel31.setPreferredSize(new java.awt.Dimension(50, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(jLabel31, gridBagConstraints);

        jReportServerPort.setText("8080");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(jReportServerPort, gridBagConstraints);

        jButton5.setText(trans.get("Neustart"));
        jButton5.setToolTipText(trans.get("Berichtserver_startet"));
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fireRestartReportServerEvent(evt);
            }
        });
        jPanel13.add(jButton5, new java.awt.GridBagConstraints());

        jLabel32.setText("OBST Server");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(jLabel32, gridBagConstraints);

        jObstServer.setToolTipText(trans.get("OBST_text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel13.add(jObstServer, gridBagConstraints);

        javax.swing.GroupLayout jMiscSettingsLayout = new javax.swing.GroupLayout(jMiscSettings);
        jMiscSettings.setLayout(jMiscSettingsLayout);
        jMiscSettingsLayout.setHorizontalGroup(
            jMiscSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jMiscSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jMiscSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE))
                .addContainerGap())
        );
        jMiscSettingsLayout.setVerticalGroup(
            jMiscSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMiscSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSettingsTabbedPane.addTab(trans.get("Sonstiges"), new javax.swing.ImageIcon(getClass().getResource("/res/checkbox.png")), jMiscSettings); // NOI18N

        jOKButton.setBackground(new java.awt.Color(239, 235, 223));
        jOKButton.setText(trans.get("OK"));
        jOKButton.setToolTipText(trans.get("Einstellungenubernehmen"));
        jOKButton.setMaximumSize(new java.awt.Dimension(90, 25));
        jOKButton.setMinimumSize(new java.awt.Dimension(90, 25));
        jOKButton.setPreferredSize(new java.awt.Dimension(90, 25));
        jOKButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireOkEvent(evt);
            }
        });

        jCancelButton.setBackground(new java.awt.Color(239, 235, 223));
        jCancelButton.setText(trans.get("Abbrechen"));
        jCancelButton.setToolTipText(trans.get("Einstellungenverwerfen"));
        jCancelButton.setMaximumSize(new java.awt.Dimension(100, 25));
        jCancelButton.setMinimumSize(new java.awt.Dimension(100, 25));
        jCancelButton.setPreferredSize(new java.awt.Dimension(100, 25));
        jCancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireCloseEvent(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jOKButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSettingsTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSettingsTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jOKButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSettingsTabbedPane.getAccessibleContext().setAccessibleName("Karten");

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void fireUpdateProxySettingsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireUpdateProxySettingsEvent

        if (jProxyConnectOption.isSelected()) {
            //store properties
            GlobalOptions.addProperty("proxySet", Boolean.toString(true));
            GlobalOptions.addProperty("proxyHost", jProxyHost.getText());
            GlobalOptions.addProperty("proxyPort", jProxyPort.getText());
            GlobalOptions.addProperty("proxyType", Integer.toBinaryString(jProxyTypeChooser.getSelectedIndex()));
            GlobalOptions.addProperty("proxyUser", jProxyUser.getText());
            GlobalOptions.addProperty("proxyPassword", new String(jProxyPassword.getPassword()));
            //create proxy object
            SocketAddress addr = new InetSocketAddress(jProxyHost.getText(), Integer.parseInt(jProxyPort.getText()));
            switch (jProxyTypeChooser.getSelectedIndex()) {
                case 1: {
                    webProxy = new Proxy(Proxy.Type.SOCKS, addr);
                    break;
                }
                default: {
                    webProxy = new Proxy(Proxy.Type.HTTP, addr);
                    break;
                }
            }
            if ((jProxyUser.getText().length() >= 1) && (jProxyPassword.getPassword().length > 1)) {
                Authenticator.setDefault(new Authenticator() {

                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(jProxyUser.getText(), jProxyPassword.getPassword());
                    }
                });
            }
        } else {
            //store properties
            GlobalOptions.addProperty("proxySet", Boolean.toString(false));
            GlobalOptions.removeProperty("proxyHost");
            GlobalOptions.removeProperty("proxyPort");
            GlobalOptions.removeProperty("proxyType");
            GlobalOptions.removeProperty("proxyUser");
            GlobalOptions.removeProperty("proxyPassword");
            //set no proxy and no authentification
            Authenticator.setDefault(null);
            webProxy = Proxy.NO_PROXY;
        }

        GlobalOptions.saveProperties();

        checkConnectivity();

        boolean offlineBefore = GlobalOptions.isOfflineMode();

        if (!updateServerList()) {
            //fully failed --> remote update failed and no local servers found
            String message = trans.get("Serverliste_text_google");
            JOptionPaneHelper.showWarningBox(this, message, trans.get("Warnung"));
        } else {
            String message = null;
            String title = trans.get("Fehler");
            int type = JOptionPane.ERROR_MESSAGE;
            if (offlineBefore) {
                //was offline before checking serverlist
                message = trans.get("Verbindung_text");
            } else if (GlobalOptions.isOfflineMode()) {
                //get offline while checking serverlist
                message = trans.get("Pruefung_Verbindung_text");
            } else {
                //success
                message = trans.get("VerbindungErfolgreich");
                title = trans.get("Information");
                type = JOptionPane.INFORMATION_MESSAGE;
            }

            //show box
            if (type == JOptionPane.INFORMATION_MESSAGE) {
                JOptionPaneHelper.showInformationBox(this, message, title);
            } else {
                JOptionPaneHelper.showErrorBox(this, message, title);
            }

        }

        DSWorkbenchMainFrame.getSingleton().onlineStateChanged();
    }//GEN-LAST:event_fireUpdateProxySettingsEvent

    private void fireChangeConnectTypeEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireChangeConnectTypeEvent
        jProxyHost.setEnabled(jProxyConnectOption.isSelected());
        jProxyPort.setEnabled(jProxyConnectOption.isSelected());
        jProxyUser.setEnabled(jProxyConnectOption.isSelected());
        jProxyPassword.setEnabled(jProxyConnectOption.isSelected());
        jProxyTypeChooser.setEnabled(jProxyConnectOption.isSelected());
    }//GEN-LAST:event_fireChangeConnectTypeEvent

    private void fireCloseEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireCloseEvent
        if (!jCancelButton.isEnabled()) {
            return;
        }

        if (!checkTribesAccountSettings()) {
            return;
        }

        DSWorkbenchMainFrame.getSingleton().serverSettingsChangedEvent();
        setVisible(false);
    }//GEN-LAST:event_fireCloseEvent

    private void fireOkEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireOkEvent
        if (!jOKButton.isEnabled()) {
            return;
        }
        try {
            /*
              Validate player settings
             */
            UserProfile selectedProfile = null;
            try {
                selectedProfile = (UserProfile) jProfileBox.getSelectedItem();
            } catch (Exception ignored) {
            }
            if (selectedProfile != null) {
                if (selectedProfile.getTribe() == null) {
                    //probably data is not loaded yet as there was an error during initialization...just return
                    setBlocking(false);
                    setVisible(false);
                    return;
                }
                if (selectedProfile.getTribe().equals(InvalidTribe.getSingleton())) {
                    JOptionPaneHelper.showWarningBox(this, trans.get("Spieler_existiert_nicht"), trans.get("Warnung"));
                    return;
                }

                logger.debug("Setting default profile for server '" + GlobalOptions.getSelectedServer() + "' to " + selectedProfile.getTribeName());
                UserProfile formerProfile = GlobalOptions.getSelectedProfile();

                if (formerProfile.getProfileId() != selectedProfile.getProfileId()) {
                    logger.info("Writing user data for former profile");
                    TacticsPlanerWizard.storeProperties();
                    ResourceDistributorWizard.storeProperties();
                    GlobalOptions.saveUserData();
                    GlobalOptions.addProperty("selected.profile", Long.toString(selectedProfile.getProfileId()));
                    formerProfile.updateProperties();
                    formerProfile.storeProfileData();
                    GlobalOptions.setSelectedProfile(selectedProfile);
                    logger.info("Loading user data for selected profile");
                    GlobalOptions.loadUserData();
                } else {
                    GlobalOptions.addProperty("selected.profile", Long.toString(selectedProfile.getProfileId()));
                    GlobalOptions.setSelectedProfile(selectedProfile);
                }
            } else if (GlobalOptions.getSelectedProfile() == null) {
                JOptionPaneHelper.showWarningBox(DSWorkbenchSettingsDialog.this, trans.get("Profil_auswahl"), trans.get("Warnung"));
                return;
            }

            /*
              Update attack vector colors
             */
            DefaultTableModel model = ((DefaultTableModel) jAttackColorTable.getModel());
            for (int i = 0; i < model.getRowCount(); i++) {
                String unit = ((UnitHolder) model.getValueAt(i, 0)).getName();
                Color color = (Color) model.getValueAt(i, 1);
                String hexCol = Integer.toHexString(color.getRGB());
                hexCol = "#" + hexCol.substring(2, hexCol.length());
                GlobalOptions.addProperty(unit + ".color", hexCol);
            }

            /*
              Validate misc properties
             */
            int sortType = jVillageSortTypeChooser.getSelectedIndex();
            Village.setOrderType(sortType);
            GlobalOptions.addProperty("village.order", Integer.toString(sortType));
            GlobalOptions.addProperty("notify.duration", Integer.toString(jNotifyDurationBox.getSelectedIndex()));
            GlobalOptions.addProperty("inform.on.updates", Boolean.toString(jInformOnUpdates.isSelected()));
            GlobalOptions.addProperty("show.popup.moral", Boolean.toString(jShowPopupMoral.isSelected()));
            GlobalOptions.addProperty("show.popup.conquers", Boolean.toString(jShowPopupConquers.isSelected()));
            GlobalOptions.addProperty("show.popup.ranks", Boolean.toString(jShowPopupRanks.isSelected()));
            GlobalOptions.addProperty("show.popup.farm.space", Boolean.toString(jShowPopupFarmSpace.isSelected()));
            GlobalOptions.addProperty("max.farm.space", jMaxFarmSpace.getText());
            GlobalOptions.addProperty("farm.popup.use.real", Boolean.toString(jPopupFarmUseRealValues.isSelected()));
            GlobalOptions.addProperty("max.density.troops", jMaxTroopDensity.getText());
            GlobalOptions.addProperty("show.live.countdown", Boolean.toString(jShowLiveCountdown.isSelected()));
            GlobalOptions.addProperty("extended.attack.vectors", Boolean.toString(jExtendedAttackLineDrawing.isSelected()));
            GlobalOptions.addProperty("max.sim.rounds", jMaxSimRounds.getText());
            GlobalOptions.addProperty("support.tolerance", jTolerance.getText());
            GlobalOptions.addProperty("max.loss.ratio", jMaxLossRatio.getText());
            GlobalOptions.addProperty("map.marker.transparency", Integer.toString(jMarkerTransparency.getValue()));
            GlobalOptions.addProperty("obst.server", jObstServer.getText());
            GlobalOptions.addProperty("command.sleep.time", Integer.toString(jSliderCmdSleepTime.getValue()));
            if (!checkSettings()) {
                logger.error("Failed to check server settings");
                return;
            }
            setBlocking(false);
            setVisible(false);
            DSWorkbenchMainFrame.getSingleton().serverSettingsChangedEvent();
            MapPanel.getSingleton().getMapRenderer().initiateRedraw(null);
            MinimapPanel.getSingleton().redraw();
            
            if(Math.abs(GlobalOptions.getProperties().getDouble("ribbon.size") - menueSizeFromSlider()) > 0.1) {
                GlobalOptions.addProperty("ribbon.size", Double.toString(menueSizeFromSlider()));
                JOptionPaneHelper.showInformationBox(DSWorkbenchSettingsDialog.this, trans.get("Groessenaenderung_Hauptmenues"), trans.get("Neustarterforderlich"));
            }
            
            if(! GlobalOptions.getProperty("ui.language").equals(jLanguageChooser.getSelectedItem().toString())) {
                TranslationManager.getSingleton().setLanguage(jLanguageChooser.getSelectedItem().toString());
                String languageChange = trans.get("Sprachaenderung");
                String restart = trans.get("Neustarterforderlich");
                TranslationManager.getSingleton().setLanguage(GlobalOptions.getProperty("ui.language"));
                
                GlobalOptions.addProperty("ui.language", jLanguageChooser.getSelectedItem().toString());
                
                JOptionPaneHelper.showInformationBox(DSWorkbenchSettingsDialog.this, languageChange, restart);
            }
            GlobalOptions.saveProperties();
        } catch (Throwable t) {
            logger.error("Failed to close settings dialog", t);
        }
    }//GEN-LAST:event_fireOkEvent

private void fireClosingEvent(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_fireClosingEvent
    fireCloseEvent(null);
}//GEN-LAST:event_fireClosingEvent

    // </editor-fold>
private void fireChangeContinentsOnMinimapEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireChangeContinentsOnMinimapEvent
    GlobalOptions.addProperty("map.showcontinents", Boolean.toString(jShowContinents.isSelected()));
    MinimapPanel.getSingleton().resetBuffer();
    MinimapPanel.getSingleton().redraw();
}//GEN-LAST:event_fireChangeContinentsOnMinimapEvent

    // <editor-fold defaultstate="collapsed" desc=" EventListeners for settings ">
private void fireChangeShowAttackMovementEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireChangeShowAttackMovementEvent
    GlobalOptions.addProperty("attack.movement", Boolean.toString(jShowAttackMovementBox.isSelected()));
}//GEN-LAST:event_fireChangeShowAttackMovementEvent

private void fireChangeMarkOwnVillagesOnMinimapEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireChangeMarkOwnVillagesOnMinimapEvent
    GlobalOptions.addProperty("mark.villages.on.minimap", Boolean.toString(jMarkOwnVillagesOnMinimap.isSelected()));
    MinimapPanel.getSingleton().resetBuffer();
    MinimapPanel.getSingleton().redraw();
}//GEN-LAST:event_fireChangeMarkOwnVillagesOnMinimapEvent

private void fireStandardMarkChangedEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireStandardMarkChangedEvent
    if (evt.getStateChange() == ItemEvent.SELECTED) {
        int idx = jDefaultMark.getSelectedIndex();
        if (idx < 0) {
            idx = 0;
        }
        GlobalOptions.addProperty("default.mark", Integer.toString(idx));
    }
}//GEN-LAST:event_fireStandardMarkChangedEvent

private void fireDrawAttacksByDefaultChangedEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireDrawAttacksByDefaultChangedEvent
    GlobalOptions.addProperty("draw.attacks.by.default", Boolean.toString(jDrawAttacksByDefaultBox.isSelected()));
}//GEN-LAST:event_fireDrawAttacksByDefaultChangedEvent

private void fireCheckForUpdatesEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireCheckForUpdatesEvent
    GlobalOptions.addProperty("check.updates.on.startup", Boolean.toString(jCheckForUpdatesBox.isSelected()));
}//GEN-LAST:event_fireCheckForUpdatesEvent

private void fireChangeShowSectorsEvent(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fireChangeShowSectorsEvent
    GlobalOptions.addProperty("show.sectors", Boolean.toString(jShowSectors.isSelected()));
}//GEN-LAST:event_fireChangeShowSectorsEvent

private void fireShowBarbarianChangedEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireShowBarbarianChangedEvent
    GlobalOptions.addProperty("show.barbarian", Boolean.toString(jShowBarbarian.isSelected()));
}//GEN-LAST:event_fireShowBarbarianChangedEvent

private void fireAcceptDeffStrengthEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireAcceptDeffStrengthEvent
    if (evt.getSource() == jDeffStrengthOKButton) {
        try {
            TroopAmountFixed troops = troopDensitySelection.getAmounts();
            String result = Integer.toString((int) troops.getDefInfantryValue());
            GlobalOptions.addProperty("max.density.troops", result);
            jMaxTroopDensity.setText(result);
        } catch (Exception e) {
            JOptionPaneHelper.showErrorBox(jTroopDensitySelectionDialog, trans.get("ueberpruefeEingabe"), trans.get("Fehler"));
            return;
        }
    }

    jTroopDensitySelectionDialog.setVisible(false);
}//GEN-LAST:event_fireAcceptDeffStrengthEvent

private void fireSelectTroopsDensityEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireSelectTroopsDensityEvent
    jTroopDensitySelectionDialog.setLocationRelativeTo(this);
    jTroopDensitySelectionDialog.setVisible(true);
}//GEN-LAST:event_fireSelectTroopsDensityEvent

private void fireSelectBrowserEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireSelectBrowserEvent
    if (!jButton1.isEnabled()) {
        return;
    }
    String dir = GlobalOptions.getProperty("screen.dir");

    JFileChooser chooser = null;
    try {
        chooser = new JFileChooser(dir);
    } catch (Exception e) {
        JOptionPaneHelper.showErrorBox(this, trans.get("Dateiauswahldialog"), trans.get("Fehler"));
        return;
    }
    chooser.setDialogTitle(trans.get("Browserauswaehlen"));

    chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

        @Override
        public boolean accept(File f) {
            return true;
        }

        @Override
        public String getDescription() {
            return "*.*";
        }
    });
    int ret = chooser.showSaveDialog(this);
    if (ret == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();

        if (f != null && f.canExecute()) {
            try {
                jBrowserPath.setText(f.getCanonicalPath());
            } catch (Exception e) {
                jBrowserPath.setText(f.getPath());
            }
            GlobalOptions.addProperty("default.browser", jBrowserPath.getText());
            if (JOptionPaneHelper.showQuestionConfirmBox(this, trans.get("AktivierBrowser"), trans.get("Erfolg"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.YES_OPTION) {
                if (!BrowserInterface.openPage("http://www.google.com")) {
                    JOptionPaneHelper.showErrorBox(this, trans.get("Browser_no_open"), trans.get("Fehler"));
                }
            }
        } else {
            JOptionPaneHelper.showErrorBox(this, trans.get("no_programm"), trans.get("Fehler"));
        }
    }

}//GEN-LAST:event_fireSelectBrowserEvent

private void fireChangeDefaultBrowserEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fireChangeDefaultBrowserEvent
    boolean value = jUseStandardBrowser.isSelected();
    GlobalOptions.addProperty("default.browser", (value) ? "" : jBrowserPath.getText());
    jLabel5.setEnabled(!value);
    jBrowserPath.setEnabled(!value);
    jButton1.setEnabled(!value);
}//GEN-LAST:event_fireChangeDefaultBrowserEvent

private void fireSelectTemplateEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireSelectTemplateEvent
    String dir = null;
    int templateID = -1;
    if (evt.getSource() == jSelectHeaderButton) {
        dir = GlobalOptions.getProperty("attack.template.header");
        templateID = 0;
    } else if (evt.getSource() == jSelectBlockButton) {
        dir = GlobalOptions.getProperty("attack.template.block");
        templateID = 1;
    } else if (evt.getSource() == jSelectFooterButton) {
        dir = GlobalOptions.getProperty("attack.template.footer");
        templateID = 2;
    }
    if (templateID < 0) {
        //unknown event source
        return;
    }
    if (dir == null) {
        dir = ".";
    }

    JFileChooser chooser = null;
    try {
        chooser = new JFileChooser(dir);
    } catch (Exception e) {
        JOptionPaneHelper.showErrorBox(this, trans.get("Dateiauswahldialog"), trans.get("Fehler"));
        return;
    }
    chooser.setDialogTitle(trans.get("Templateauswaehlen"));

    chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

        @Override
        public boolean accept(File f) {
            return true;
        }

        @Override
        public String getDescription() {
            return "*.tmpl";
        }
    });
    int ret = chooser.showOpenDialog(this);
    if (ret == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();
        if (f != null && f.isFile() && f.exists()) {
            switch (templateID) {
                case 0: {
                    GlobalOptions.addProperty("attack.template.header", f.getPath());
                    jHeaderPath.setText(f.getPath());
                    break;
                }
                case 1: {
                    GlobalOptions.addProperty("attack.template.block", f.getPath());
                    jBlockPath.setText(f.getPath());
                    break;
                }
                default: {
                    GlobalOptions.addProperty("attack.template.footer", f.getPath());
                    jFooterPath.setText(f.getPath());
                    break;
                }
            }
        } else {
            JOptionPaneHelper.showErrorBox(this, trans.get("Dateinichtgueltig"), trans.get("Fehler"));
        }
        AttackPlanHTMLExporter.loadCustomTemplate();
    }
}//GEN-LAST:event_fireSelectTemplateEvent

private void fireRestoreTemplateEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRestoreTemplateEvent

    if (evt.getSource() == jRestoreHeaderButton) {
        jHeaderPath.setText(trans.get("Standard"));
        GlobalOptions.removeProperty("attack.template.header");
    } else if (evt.getSource() == jRestoreBlockButton) {
        jBlockPath.setText(trans.get("Standard"));
        GlobalOptions.removeProperty("attack.template.block");
    } else if (evt.getSource() == jRestoreFooterButton) {
        jFooterPath.setText(trans.get("Standard"));
        GlobalOptions.removeProperty("attack.template.footer");
    }
    AttackPlanHTMLExporter.loadCustomTemplate();
}//GEN-LAST:event_fireRestoreTemplateEvent

private void fireDownloadLiveDataEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireDownloadLiveDataEvent
    if (jProfileBox.getSelectedItem() == null) {
        return;
    }
    // <editor-fold defaultstate="collapsed" desc=" Offline Mode ? ">

    if (GlobalOptions.isOfflineMode()) {
        JOptionPaneHelper.showWarningBox(this, trans.get("Offline_Modus"),
                trans.get("Warnung"));
        return;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Account valid, data outdated ? ">
    String selectedServer = ((UserProfile) jProfileBox.getSelectedItem()).getServerId();
    // </editor-fold>

    //save current user data for current server
    GlobalOptions.saveUserData();
    GlobalOptions.setSelectedServer(selectedServer);
    GlobalOptions.addProperty("default.server", selectedServer);
    GlobalOptions.saveProperties();

    jOKButton.setEnabled(false);
    jCancelButton.setEnabled(false);
    jDownloadLiveDataButton.setEnabled(false);
    jNewProfileButton.setEnabled(false);
    jModifyProfileButton.setEnabled(false);
    jDeleteProfileButton.setEnabled(false);
    jStatusArea.setText("");
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    Thread t = new Thread(() -> {
        try {
            logger.debug("Start downloading data from tribal wars servers");
            boolean ret = DataHolder.getSingleton().loadLiveData();
            logger.debug("Update finished " + ((ret) ? "successfully" : "with errors"));
        } catch (Exception e) {
            logger.error("Failed to load data", e);
        }
    });

    logger.debug("Starting update thread");
    t.setDaemon(true);
    t.start();
}//GEN-LAST:event_fireDownloadLiveDataEvent

private void fireProfileActionEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireProfileActionEvent
    if (evt.getSource() == jNewProfileButton) {
        DSWorkbenchProfileDialog.getSingleton().setLocationRelativeTo(this);
        DSWorkbenchProfileDialog.getSingleton().showAddProfileDialog();
    } else if (evt.getSource() == jModifyProfileButton) {
        DSWorkbenchProfileDialog.getSingleton().setLocationRelativeTo(this);
        UserProfile profile = (UserProfile) jProfileBox.getSelectedItem();
        if (profile == null) {
            return;
        }
        DSWorkbenchProfileDialog.getSingleton().showModifyDialog(profile);
    } else if (evt.getSource() == jDeleteProfileButton) {
        UserProfile profile = (UserProfile) jProfileBox.getSelectedItem();
        boolean success = false;
        if (JOptionPaneHelper.showWarningConfirmBox(this, trans.get("Profil_attack") + profile + trans.get("wirklichloeschen"), trans.get("Warnung"), trans.get("Nein"), trans.get("Ja")) == JOptionPane.OK_OPTION) {
            success = profile.delete();
            if (!success) {
                JOptionPaneHelper.showWarningBox(this, trans.get("Profil_canot_delete"), trans.get("loeschenfehlgeschlagen"));
            }
        } else {
            //delete canceled
            return;
        }
    }
    updateProfileList();
}//GEN-LAST:event_fireProfileActionEvent

    private void fireEnableClipboardNotificationEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireEnableClipboardNotificationEvent
        GlobalOptions.addProperty("clipboard.notification", Boolean.toString(jClipboardSound.isSelected()));
    }//GEN-LAST:event_fireEnableClipboardNotificationEvent

    private void fireEnableSystrayEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireEnableSystrayEvent
        GlobalOptions.addProperty("systray.enabled", Boolean.toString(jEnableSystray.isSelected()));
    }//GEN-LAST:event_fireEnableSystrayEvent

    private void fireDeleteFarmReportsOnExitEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireDeleteFarmReportsOnExitEvent
        GlobalOptions.addProperty("delete.farm.reports.on.exit", Boolean.toString(jDeleteFarmReportsOnExit.isSelected()));
    }//GEN-LAST:event_fireDeleteFarmReportsOnExitEvent

    private void fireChangeOffEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireChangeOffEvent
        GlobalOptions.addProperty("standard.off", getOffense().toProperty());
    }//GEN-LAST:event_fireChangeOffEvent

    private void fireChangeDefEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireChangeDefEvent
        GlobalOptions.addProperty("standard.defense.split", getDefense().toProperty());
    }//GEN-LAST:event_fireChangeDefEvent

    private void fireRestartReportServerEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fireRestartReportServerEvent
        int portBefore = GlobalOptions.getProperties().getInt("report.server.port");
        int port = UIHelper.parseIntFromField(jReportServerPort, 8080);

        if (port != portBefore) {
            GlobalOptions.addProperty("report.server.port", Integer.toString(port));
            ReportServer.getSingleton().stop();
            try {
                ReportServer.getSingleton().start(port);
                JOptionPaneHelper.showInformationBox(this, trans.get("DerBerichtserver") + port, trans.get("Information"));
            } catch (BindException be) {
                JOptionPaneHelper.showErrorBox(this, trans.get("Port") + port + trans.get("wirdbereitsverwendet"), trans.get("Fehler"));
            } catch (Exception e) {
                JOptionPaneHelper.showErrorBox(this, trans.get("error_port") + port, trans.get("Fehler"));
            }
        } else {
            JOptionPaneHelper.showInformationBox(this, trans.get("report_port") + port, trans.get("Information"));
        }
    }//GEN-LAST:event_fireRestartReportServerEvent

    private void jShowPopupMoralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jShowPopupMoralActionPerformed
        //TODO remove this when moral is working correctly
        if (jShowPopupMoral.isSelected()
                && (ServerSettings.getSingleton().getMoralType() == ServerSettings.TIMEBASED_MORAL
                || ServerSettings.getSingleton().getMoralType() == ServerSettings.TIME_LIMITED_POINTBASED_MORAL)) {
            String warning = trans.get("aufWelten");
            if (ServerSettings.getSingleton().getMoralType() == ServerSettings.TIMEBASED_MORAL) {
                warning += trans.get("Zeitbasierter");
            } else if (ServerSettings.getSingleton().getMoralType() == ServerSettings.TIME_LIMITED_POINTBASED_MORAL) {
                warning += trans.get("zeitlichMoral");
            }
            warning += trans.get("zeitlichMoral_text");
            if (JOptionPaneHelper.showWarningConfirmBox(this, warning, trans.get("Warnung"), trans.get("Nein"),
                    trans.get("Ja")) != JOptionPane.OK_OPTION) {
                jShowPopupMoral.setSelected(false);
            }
        }
    }//GEN-LAST:event_jShowPopupMoralActionPerformed

    private void fireSelectProfile(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fireSelectProfile
        if (jProfileBox.getSelectedItem() == null) {
            return;
        }
        String selectedServer = ((UserProfile) jProfileBox.getSelectedItem()).getServerId();
        GlobalOptions.addProperty("default.player", Long.toString(((UserProfile) jProfileBox.getSelectedItem()).getProfileId()));

        if (!GlobalOptions.getProperty("default.server").equals(selectedServer)) {
            //save user data for current server
            GlobalOptions.saveUserData();
            GlobalOptions.addProperty("default.server", selectedServer);
            GlobalOptions.saveProperties();

            GlobalOptions.setSelectedServer(selectedServer);
            jOKButton.setEnabled(false);
            jCancelButton.setEnabled(false);
            jDownloadLiveDataButton.setEnabled(false);
            jNewProfileButton.setEnabled(false);
            jModifyProfileButton.setEnabled(false);
            jDeleteProfileButton.setEnabled(false);

            jStatusArea.setText("");
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            try {
                logger.debug("Start loading from hard disk");
                boolean ret = DataHolder.getSingleton().loadData(false);
                jLabelServer.setText(selectedServer);
                logger.debug("Data loaded " + ((ret) ? "successfully" : "with errors"));
            } catch (Exception e) {
                logger.error("Failed loading data", e);
            }
        }
    }//GEN-LAST:event_fireSelectProfile

    private void jPopupFarmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPopupFarmActionPerformed
        updateFarmSpaceUI();
    }//GEN-LAST:event_jPopupFarmActionPerformed

    private void jMenueSizeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jMenueSizeStateChanged
        jLabelMenueSize.setText(Double.toString(menueSizeFromSlider()));
    }//GEN-LAST:event_jMenueSizeStateChanged

    private void jSliderCmdSleepTimeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderCmdSleepTimeStateChanged
        jLabelCmdSleepTimePreview.setText(Integer.toString(jSliderCmdSleepTime.getValue()));
    }//GEN-LAST:event_jSliderCmdSleepTimeStateChanged

    private double menueSizeFromSlider() {
        int sliderVal = jMenueSize.getValue(), sliderMin = jMenueSize.getMinimum(),
                sliderMax = jMenueSize.getMaximum();
        double min = 0.2f, max = 5f;
        
        double percentage = ((double) sliderVal - sliderMin) / (sliderMax - sliderMin);
        double minLog = Math.log(min), maxLog = Math.log(max);
        double size = Math.exp(minLog + (maxLog - minLog) * percentage);
        
        return new BigDecimal(size).round(new MathContext(2)).doubleValue();
    }
    
    private int menueSizeToSlider(double value) {
        double min = 0.2f, max = 5f;
        
        double sizeLog = Math.log(value), minLog = Math.log(min), maxLog = Math.log(max);
        double percentage = (sizeLog - minLog) / (maxLog - minLog);
        
        int sliderMin = jMenueSize.getMinimum(), sliderMax = jMenueSize.getMaximum();
        int sliderVal = (int) Math.round(sliderMin + (sliderMax - sliderMin) * percentage);
        return sliderVal;
    }
    
    private void updateFarmSpaceUI() {
        if(jShowPopupFarmSpace.isSelected()) {
            if(jPopupFarmUseRealValues.isSelected()) {
                jMaxFarmSpace.setEnabled(false);
                jMaxFarmSpacelabel.setEnabled(false);
            } else {
                jMaxFarmSpace.setEnabled(true);
                jMaxFarmSpacelabel.setEnabled(true);
            }
            jPopupFarmUseRealValues.setEnabled(true);
        } else {
            jPopupFarmUseRealValues.setEnabled(false);
            jMaxFarmSpace.setEnabled(false);
            jMaxFarmSpacelabel.setEnabled(false);
        }
    }
    // </editor-fold>
    /**
     * Update the server list
     */
    private boolean updateServerList() {
        String[] servers;
        //if connection not failed before, get server list
        if (!GlobalOptions.isOfflineMode()) {
            try {
                ServerManager.loadServerList(getWebProxy());
                servers = ServerManager.getServerIDs();
                if (servers == null) {
                    throw new Exception("No server received");
                }
            } catch (Exception e) {
                logger.error("Failed to load server list", e);
                GlobalOptions.setOfflineMode(true);
                servers = ServerManager.getLocalServers();
            }
        } else {
            //get local list in offline mode
            servers = ServerManager.getLocalServers();
        }

        if (servers.length < 1) {
            logger.error("Failed to get server list and no locally stored server found");
            jProfileBox.setModel(new DefaultComboBoxModel(new Object[]{trans.get("no_server_found")}));
            jLabelServer.setText("no_server");
            return false;
        }
        
        updateProfileList();
        
        return true;
    }

    private void updateProfileList() {
        DefaultComboBoxModel model;
        UserProfile[] profiles = ProfileManager.getSingleton().getProfiles();
        if (profiles != null && profiles.length > 0) {
            model = new DefaultComboBoxModel(profiles);
        } else {
            model = new DefaultComboBoxModel(new UserProfile[]{});
            logger.fatal("no Profile");
        }

        long profileId = -1;
        if (GlobalOptions.getSelectedProfile() != null) {
            profileId = GlobalOptions.getSelectedProfile().getProfileId();
        } else {
            try {
                profileId = GlobalOptions.getProperties().getLong("default.player");
            } catch (Exception ignored) {
            }
        }
        jProfileBox.setModel(model);

        if (profileId != -1) {
            for (UserProfile profile : profiles) {
                if (profile.getProfileId() == profileId) {
                    jProfileBox.setSelectedItem(profile);
                    break;
                }
            }
        }
        if(jProfileBox.getSelectedItem() != null) {
            jLabelServer.setText(((UserProfile) jProfileBox.getSelectedItem()).getServerId());
        } else {
            jLabelServer.setText(trans.get("no_profil"));
        }

        fireSelectProfile(null);
    }

    /**
     * Check the connectivity to google.com
     */
    private void checkConnectivity() {
        logger.debug("Checking general connectivity");
        try {
            URLConnection c = new URL("http://www.google.com").openConnection(getWebProxy());
            //   c.setConnectTimeout(10000);
            String header = c.getHeaderField(0);
            if (header != null) {
                logger.debug("Connection established");
                GlobalOptions.setOfflineMode(false);
            } else {
                logger.warn("Could not establish connection");
                GlobalOptions.setOfflineMode(true);
            }
        } catch (Exception in) {
            logger.error("Exception while opening connection", in);
            GlobalOptions.setOfflineMode(true);
        }
    }

    /**
     * Check the tribes server and account
     */
    private boolean checkTribesAccountSettings() {
        if (!checkServerPlayerSettings()) {
            String message = trans.get("Serversettings_check");
            message += trans.get("no_server_noPlayer");
            message += trans.get("Settings");

            if (JOptionPaneHelper.showQuestionConfirmBox(this, message, trans.get("Warnung"), trans.get("Beenden"), trans.get("Korrigieren")) == JOptionPane.NO_OPTION) {
                logger.error("Player/Server settings incorrect. User requested application to terminate");
                System.exit(1);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void fireDataHolderEvent(String eventMessage) {
        SimpleDateFormat f = TimeManager.getSimpleDateFormat(trans.get("dateFormat"));
        jStatusArea.insert("(" + f.format(new Date(System.currentTimeMillis())) + ") " + eventMessage + "\n", jStatusArea.getText().length());
        UIHelper.applyCorrectViewPosition(jStatusArea, jScrollPane1);
    }

    @Override
    public void fireDataLoadedEvent(boolean pSuccess) {
        if (pSuccess) {
            try {
                Collection<Tribe> tribes = DataHolder.getSingleton().getTribes().values();
                Tribe[] ta = tribes.toArray(new Tribe[]{});
                Arrays.sort(ta, Tribe.CASE_INSENSITIVE_ORDER);
                DefaultComboBoxModel model;
                UserProfile[] profiles = ProfileManager.getSingleton().getProfiles();
                UserProfile active = null;
                if (profiles != null && profiles.length > 0) {
                    model = new DefaultComboBoxModel(profiles);

                    jProfileBox.setModel(model);
                    long profileId = -1;
                    try {
                        profileId = GlobalOptions.getProperties().getLong("default.player");
                    } catch (Exception ignored) {
                    }
                    if (profileId != -1) {
                        for (UserProfile profile : profiles) {
                            if (profile.getProfileId() == profileId) {
                                jProfileBox.setSelectedItem(profile);
                                active = profile;
                                break;
                            }
                        }
                    } else {
                        jProfileBox.setSelectedIndex(0);
                        GlobalOptions.addProperty("default.player", Long.toString(profiles[0].getProfileId()));

                        String server = ((UserProfile) jProfileBox.getSelectedItem()).getServerId();
                        jLabelServer.setText(server);
                        GlobalOptions.addProperty("default.server", server);
                    }
                    if (active != null) {
                        GlobalOptions.setSelectedProfile(active);
                    } else {
                        GlobalOptions.setSelectedProfile(profiles[0]);
                    }
                } else {
                    model = new DefaultComboBoxModel(new Object[]{trans.get("no_profil")});
                    jProfileBox.setModel(model);
                    GlobalOptions.setSelectedProfile(null);
                    jLabelServer.setText(trans.get("no_server"));
                }
                
                if (GlobalOptions.isStarted()) {
                    DSWorkbenchMainFrame.getSingleton().serverSettingsChangedEvent();
                }
                
                //TODO workaround for now to avoid wrong loading
                setOffense(new TroopAmountFixed(0).loadFromProperty(GlobalOptions.getProperty("standard.off")));
                setDefense(new TroopAmountFixed(0).loadFromProperty(GlobalOptions.getProperty("standard.defense.split")));
            } catch (Exception e) {
                logger.error("Failed to setup tribe list", e);
            }
            logger.info("Loading user data");
            GlobalOptions.loadUserData();
        }

        jOKButton.setEnabled(true);
        if (!isBlocked) {
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            jCancelButton.setEnabled(true);
        }
        jDownloadLiveDataButton.setEnabled(true);
        jNewProfileButton.setEnabled(true);
        jModifyProfileButton.setEnabled(true);
        jDeleteProfileButton.setEnabled(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup connectionTypeGroup;
    private javax.swing.JTable jAttackColorTable;
    private javax.swing.JLabel jAttackMovementLabel;
    private javax.swing.JLabel jAttackMovementLabel3;
    private javax.swing.JPanel jAttackSettings;
    private javax.swing.JTextField jBlockPath;
    private javax.swing.JTextField jBrowserPath;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jCancelButton;
    private javax.swing.JCheckBox jCheckForUpdatesBox;
    private javax.swing.JCheckBox jClipboardSound;
    private javax.swing.JComboBox jDefaultMark;
    private javax.swing.JLabel jDefaultMarkLabel;
    private javax.swing.JPanel jDefenseSettings;
    private javax.swing.JButton jDeffStrengthOKButton;
    private javax.swing.JCheckBox jDeleteFarmReportsOnExit;
    private javax.swing.JButton jDeleteProfileButton;
    private javax.swing.JRadioButton jDirectConnectOption;
    private javax.swing.JButton jDownloadLiveDataButton;
    private javax.swing.JCheckBox jDrawAttacksByDefaultBox;
    private javax.swing.JCheckBox jEnableSystray;
    private javax.swing.JCheckBox jExtendedAttackLineDrawing;
    private javax.swing.JTextField jFooterPath;
    private javax.swing.JTextField jHeaderPath;
    private javax.swing.JCheckBox jInformOnUpdates;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCmdSleepTime;
    private javax.swing.JLabel jLabelCmdSleepTimePreview;
    private javax.swing.JLabel jLabelLanguage;
    private javax.swing.JLabel jLabelMenueSize;
    private javax.swing.JLabel jLabelServer;
    private javax.swing.JComboBox jLanguageChooser;
    private javax.swing.JPanel jMapSettings;
    private javax.swing.JCheckBox jMarkOwnVillagesOnMinimap;
    private javax.swing.JLabel jMarkOwnVillagesOnMinimapLabel;
    private javax.swing.JSlider jMarkerTransparency;
    private javax.swing.JLabel jMarkerTransparencyLabel;
    private javax.swing.JTextField jMaxFarmSpace;
    private javax.swing.JLabel jMaxFarmSpacelabel;
    private javax.swing.JTextField jMaxLossRatio;
    private javax.swing.JTextField jMaxSimRounds;
    private javax.swing.JTextField jMaxTroopDensity;
    private javax.swing.JSlider jMenueSize;
    private javax.swing.JPanel jMiscSettings;
    private javax.swing.JButton jModifyProfileButton;
    private javax.swing.JPanel jNetworkSettings;
    private javax.swing.JButton jNewProfileButton;
    private javax.swing.JComboBox jNotifyDurationBox;
    private javax.swing.JButton jOKButton;
    private javax.swing.JTextField jObstServer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelMenueSize;
    private javax.swing.JPanel jPanelPopupinfos;
    private javax.swing.JPanel jPlayerServerSettings;
    private javax.swing.JCheckBox jPopupFarmUseRealValues;
    private javax.swing.JComboBox jProfileBox;
    private javax.swing.JLabel jProxyAdressLabel;
    private javax.swing.JRadioButton jProxyConnectOption;
    private javax.swing.JTextField jProxyHost;
    private javax.swing.JPasswordField jProxyPassword;
    private javax.swing.JTextField jProxyPort;
    private javax.swing.JLabel jProxyPortLabel;
    private javax.swing.JComboBox jProxyTypeChooser;
    private javax.swing.JTextField jProxyUser;
    private javax.swing.JButton jRefeshNetworkButton;
    private javax.swing.JTextField jReportServerPort;
    private javax.swing.JButton jRestoreBlockButton;
    private javax.swing.JButton jRestoreFooterButton;
    private javax.swing.JButton jRestoreHeaderButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton jSelectBlockButton;
    private javax.swing.JButton jSelectFooterButton;
    private javax.swing.JButton jSelectHeaderButton;
    private javax.swing.JTabbedPane jSettingsTabbedPane;
    private javax.swing.JCheckBox jShowAttackMovementBox;
    private javax.swing.JCheckBox jShowBarbarian;
    private javax.swing.JLabel jShowBarbarianLabel;
    private javax.swing.JCheckBox jShowContinents;
    private javax.swing.JLabel jShowContinentsLabel;
    private javax.swing.JCheckBox jShowLiveCountdown;
    private javax.swing.JCheckBox jShowPopupConquers;
    private javax.swing.JCheckBox jShowPopupFarmSpace;
    private javax.swing.JCheckBox jShowPopupMoral;
    private javax.swing.JCheckBox jShowPopupRanks;
    private javax.swing.JCheckBox jShowSectors;
    private javax.swing.JLabel jShowSectorsLabel;
    private javax.swing.JPanel jSingleSupportPanel;
    private javax.swing.JSlider jSliderCmdSleepTime;
    private javax.swing.JPanel jStandardAttackerPanel;
    private javax.swing.JTextArea jStatusArea;
    private javax.swing.JPanel jTemplateSettings;
    private javax.swing.JTextField jTolerance;
    private javax.swing.JDialog jTroopDensitySelectionDialog;
    private javax.swing.JCheckBox jUseStandardBrowser;
    private javax.swing.JComboBox jVillageSortTypeChooser;
    private org.jdesktop.swingx.JXLabel jXLabel3;
    private de.tor.tribes.ui.panels.TroopSelectionPanelFixed sosAttackerSelection;
    private de.tor.tribes.ui.panels.TroopSelectionPanelFixed sosDefenderSelection;
    private javax.swing.ButtonGroup tagMarkerGroup;
    private de.tor.tribes.ui.panels.TroopSelectionPanelFixed troopDensitySelection;
    // End of variables declaration//GEN-END:variables
}
