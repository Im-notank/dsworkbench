/*//GEN-LINE:variables
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
package de.tor.tribes.ui.windows;

import de.tor.tribes.io.DataHolder;
import de.tor.tribes.io.DataHolderListener;
import de.tor.tribes.io.ServerManager;
import de.tor.tribes.types.UserProfile;
import de.tor.tribes.ui.renderer.ProfileTreeNodeRenderer;
import de.tor.tribes.ui.views.DSWorkbenchSettingsDialog;
import de.tor.tribes.ui.wiz.FirstStartWizard;
import de.tor.tribes.util.*;
import de.tor.tribes.util.GithubVersionCheck.UpdateInfo;
import de.tor.tribes.util.ThreadDeadlockDetector.DefaultDeadlockListener;
import de.tor.tribes.util.translation.TranslationManager;
import de.tor.tribes.util.translation.Translator;
import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPanelProvider;

/**
 * @author Torridity
 */
public class DSWorkbenchSplashScreen extends javax.swing.JFrame implements DataHolderListener {

    protected enum HIDE_RESULT {
        SUCCESS, RESTART_NEEDED, ERROR
    }

    private static Logger logger = LogManager.getLogger("Launcher");
    private final DSWorkbenchSplashScreen self = this;
    private final SplashRepaintThread t;
    private static DSWorkbenchSplashScreen SINGLETON = null;
    private ThreadDeadlockDetector deadlockDetector = null;

    private static Translator trans = TranslationManager.getTranslator("ui.windows.DSWorkbenchSplashScreen");
    
    public static synchronized DSWorkbenchSplashScreen getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new DSWorkbenchSplashScreen();
        }
        return SINGLETON;
    }

    /**
     * Creates new form DSWorkbenchSplashScreen
     */
    DSWorkbenchSplashScreen() {
        initComponents();
        if (GlobalOptions.isMinimal()) {
            jLabel1.setIcon(new ImageIcon("./graphics/splash_mini.gif"));
        } else {
            jLabel1.setIcon(new ImageIcon("./graphics/splash.gif"));
        }

        setTitle("DS Workbench " + Constants.VERSION + Constants.VERSION_ADDITION);
        jProfileDialog.getContentPane().setBackground(Constants.DS_BACK_LIGHT);
        jProfileDialog.pack();
        jProfileDialog.setLocationRelativeTo(DSWorkbenchSplashScreen.this);
        t = new SplashRepaintThread();
        t.start();
        new Timer("StartupTimer", true).schedule(new HideSplashTask(), 1000);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jProfileDialog = new javax.swing.JDialog(this);
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jStatusOutput = new javax.swing.JProgressBar();

        jProfileDialog.setTitle(trans.get("Profile"));
        jProfileDialog.setModal(true);
        jProfileDialog.setUndecorated(true);

        jScrollPane2.setViewportView(jTree1);

        jButton1.setBackground(new java.awt.Color(239, 235, 223));
        jButton1.setText(trans.get("Profilauswaehlen"));
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fireSelectAccountEvent(evt);
            }
        });

        javax.swing.GroupLayout jProfileDialogLayout = new javax.swing.GroupLayout(jProfileDialog.getContentPane());
        jProfileDialog.getContentPane().setLayout(jProfileDialogLayout);
        jProfileDialogLayout.setHorizontalGroup(
                jProfileDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jProfileDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jProfileDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addContainerGap())
        );
        jProfileDialogLayout.setVerticalGroup(
                jProfileDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jProfileDialogLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jLabel1.setMaximumSize(new java.awt.Dimension(516, 250));
        jLabel1.setMinimumSize(new java.awt.Dimension(516, 250));
        jLabel1.setPreferredSize(new java.awt.Dimension(516, 250));
        getContentPane().add(jLabel1, java.awt.BorderLayout.CENTER);

        jStatusOutput.setIndeterminate(true);
        jStatusOutput.setMinimumSize(new java.awt.Dimension(10, 20));
        jStatusOutput.setPreferredSize(new java.awt.Dimension(146, 20));
        jStatusOutput.setString(trans.get("LadeEinstellungen"));
        jStatusOutput.setStringPainted(true);
        getContentPane().add(jStatusOutput, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>                        

    private void fireSelectAccountEvent(java.awt.event.MouseEvent evt) {
        Object[] path = jTree1.getSelectionPath().getPath();
        UserProfile profile = null;
        try {
            profile = (UserProfile) ((DefaultMutableTreeNode) path[2]).getUserObject();
        } catch (Exception ignored) {
        }
        if (profile == null) {
            JOptionPaneHelper.showWarningBox(jProfileDialog, trans.get("BitteeineProfilauswaehlen"), trans.get("Bittewaehlen"));
        } else {
            String server = profile.getServerId();
            if (ServerManager.getServerURL(server) == null) {
                //no world data update any longer
                JOptionPaneHelper.showWarningBox(jProfileDialog, trans.get("DerServerdesgewahlten"), trans.get("Servernichtverfuegbar"));
            }
            GlobalOptions.setSelectedServer(server);
            GlobalOptions.setSelectedProfile(profile);
            GlobalOptions.addProperty("default.server", server);
            GlobalOptions.addProperty("selected.profile", Long.toString(profile.getProfileId()));
            GlobalOptions.addProperty("default.player", Long.toString(profile.getProfileId()));
            jProfileDialog.setVisible(false);
        }
    }

    protected HIDE_RESULT hideSplash() {
        try {
            if (!new File(".").canWrite()) {
                try {
                    throw new IOException("Failed to access installation directory " + new File(".").getAbsolutePath());
                } catch (Exception e) {
                    showFatalError(e);
                    return HIDE_RESULT.ERROR;
                }
            }
            File f = new File("./servers");
            if (!f.exists() && !f.mkdir()) {
                try {
                    throw new IOException("Failed to create server directory at location " + new File(".").getAbsolutePath());
                } catch (Exception e) {
                    showFatalError(e);
                    return HIDE_RESULT.ERROR;
                }
            }

            ProfileManager.getSingleton().loadProfiles();
            if (ProfileManager.getSingleton().getProfiles().length == 0) {
                logger.debug("Starting first start wizard");

                //first start wizard
                if (!new File("./hfsw").exists()) {
                    logger.debug("Starting language selection");
                    LanguageSelection langSelect = new LanguageSelection(this, true);
                    langSelect.setLocationRelativeTo(this);
                    langSelect.setVisible(true);
                    logger.debug("Awaiting language selection");
                    GlobalOptions.addProperty("ui.language", langSelect.getSelected());
                    logger.debug("Selected Language: {}", langSelect.getSelected());
                    
                    logger.debug(" - Initializing first start wizard");
                    Map result = new HashMap<>();

                    try {
                        WizardPanelProvider provider = new FirstStartWizard();
                        Wizard wizard = provider.createWizard();
                        logger.debug(" - Showing wizard");
                        result = (Map) WizardDisplayer.showWizard(wizard);
                        logger.debug("Wizard finished with result " + result);
                    } catch (Throwable t) {
                        logger.error("Wizard exception", t);
                        result = null;
                    }
                    logger.debug(" - Wizard has finished");
                    if (result == null) {
                        logger.warn(" - Wizard returned no result. Startup will fail.");
                        JOptionPaneHelper.showWarningBox(self, trans.get("grundlegendenEinstellungen"), trans.get("Abbruch"));
                        return HIDE_RESULT.ERROR;
                    } else {
                        logger.debug("Wizard result: " + result);
                    }
                    logger.debug("- First start wizard finished");
                    GlobalOptions.addProperty("proxySet", result.get("proxySet").toString());
                    GlobalOptions.addProperty("proxyHost", result.get("proxyHost").toString());
                    GlobalOptions.addProperty("proxyPort", result.get("proxyPort").toString());
                    GlobalOptions.addProperty("proxyType", result.get("proxyType").toString());
                    GlobalOptions.addProperty("proxyUser", result.get("proxyUser").toString());
                    GlobalOptions.addProperty("proxyPassword", result.get("proxyPassword").toString());
                    GlobalOptions.addProperty("default.server", result.get("server").toString());
                    GlobalOptions.addProperty("default.player", result.get("tribe.id").toString());
                    logger.debug("Creating initial profile");
                    UserProfile p = UserProfile.create(GlobalOptions.getProperty("default.server"),
                            result.get("tribe.name").toString());
                    GlobalOptions.setSelectedProfile(p);
                    GlobalOptions.addProperty("selected.profile", Long.toString(p.getProfileId()));
                    logger.debug(" - Disabling first start wizard");
                    FileUtils.touch(new File("./hfsw"));
                    GlobalOptions.saveProperties();
                }
            }

            //load properties, cursors, skins, world decoration
            logger.debug("Adding startup listeners");
            DataHolder.getSingleton().addDataHolderListener(this);
            DataHolder.getSingleton().addDataHolderListener(DSWorkbenchSettingsDialog.getSingleton());
            GlobalOptions.addDataHolderListener(this);
        } catch (Exception e) {
            logger.error("Failed to initialize global options", e);
            showFatalError(e);
            return HIDE_RESULT.ERROR;
        }

        logger.debug("Starting profile selection");
        boolean settingsRestored = false;
        
        ServerManager.loadServerList(DSWorkbenchSettingsDialog.getSingleton().getWebProxy());
        try {
            //open profile selection
            if (ProfileManager.getSingleton().getProfiles().length == 0) {
                logger.debug("No profile exists, SettingsDialog will handle this");
                //no profile found...this is handles by the settings validation
            } else if (ProfileManager.getSingleton().getProfiles().length == 1) {
                logger.debug("One profile exists. Using it...");
                //only one single profile was found, use it
                UserProfile profile = ProfileManager.getSingleton().getProfiles()[0];
                String server = profile.getServerId();
                GlobalOptions.setSelectedServer(server);
                GlobalOptions.setSelectedProfile(profile);
                GlobalOptions.addProperty("default.server", server);
                GlobalOptions.addProperty("selected.profile", Long.toString(profile.getProfileId()));
            } else {
                logger.debug("More than one profiles exist. Showing selection dialog");
                File f = new File("./servers");
                List<String> servers = new LinkedList<>();
                for (File server : f.listFiles()) {
                    servers.add(server.getName());
                }
                //sort server names
                Collections.sort(servers, (String o1, String o2) -> {
                    if (o1.length() < o2.length()) {
                        return -1;
                    } else if (o1.length() > o2.length()) {
                        return 1;
                    }
                    return o1.compareTo(o2);
                });
                List<Object> path = new LinkedList<>();
                DefaultMutableTreeNode root = new DefaultMutableTreeNode(trans.get("Profile"));
                long selectedProfile = GlobalOptions.getProperties().getLong("selected.profile");
                path.add(root);
                for (String server : servers) {
                    DefaultMutableTreeNode serverNode = new DefaultMutableTreeNode(server);
                    boolean profileAdded = false;
                    for (UserProfile profile : ProfileManager.getSingleton().getProfiles(server)) {
                        DefaultMutableTreeNode profileNode = new DefaultMutableTreeNode(profile);
                        if (profile.getProfileId() == selectedProfile) {
                            path.add(serverNode);
                            path.add(profileNode);
                        }
                        serverNode.add(profileNode);
                        profileAdded = true;
                    }
                    if (profileAdded) {
                        root.add(serverNode);
                    }
                }

                jTree1.setModel(new DefaultTreeModel(root));
                jTree1.setSelectionPath(new TreePath(path.toArray()));
                jTree1.scrollPathToVisible(new TreePath(path.toArray()));
                jTree1.setCellRenderer(new ProfileTreeNodeRenderer());
                jProfileDialog.setVisible(true);
            }
            logger.debug("Profile selection finished");
            //check settings
            DSWorkbenchSettingsDialog.getSingleton().restoreProperties();
            settingsRestored = true;
            if (!DSWorkbenchSettingsDialog.getSingleton().checkSettings()) {
                logger.debug("Settings check in settings dialog failed");
                logger.info("Reading user settings returned error(s)");
                DSWorkbenchSettingsDialog.getSingleton().setBlocking(true);
                DSWorkbenchSettingsDialog.getSingleton().setVisible(true);
            }
        } catch (Exception e) {
            logger.warn("Failed to open profile manager", e);
        }

        if (!settingsRestored) {
            DSWorkbenchSettingsDialog.getSingleton().restoreProperties();
        }

        // <editor-fold defaultstate="collapsed" desc=" Check for data updates ">
        logger.debug("Checking for application updates");
        boolean checkForUpdates = GlobalOptions.getProperties().getBoolean("check.updates.on.startup");

        try {
            if (!DataHolder.getSingleton().loadData(checkForUpdates)) {
                throw new Exception("loadData() returned 'false'. See log for more details.");
            }
        } catch (Exception e) {
            logger.error("Failed to load server data", e);
            showFatalError(e);
            return HIDE_RESULT.ERROR;
        }
        // </editor-fold>
        try {
            logger.debug("Initializing application window");
            DSWorkbenchMainFrame.getSingleton().init();
            logger.info("Showing application window");

            DSWorkbenchMainFrame.getSingleton().setVisible(true);

            //check for version updates
            logger.info("Checking for DS Workbench update.");
            UpdateInfo info = GithubVersionCheck.getUpdateInformation();
            switch (info.getStatus()) {
                case UPDATE_AVAILABLE:
                    NotifierFrame.doNotification(trans.get("DSWorkbenchVersion"), NotifierFrame.NOTIFY_UPDATE);
                default:
                    logger.info("No update available or update check failed.");
            }

            try {
                ReportServer.getSingleton().start(GlobalOptions.getProperties().getInt("report.server.port"));
            } catch (Exception e) {
                logger.error("Failed to start report server", e);
            }
            
            // <editor-fold defaultstate="collapsed" desc=" Init HelpSystem ">
            if (!Constants.DEBUG) {
                GlobalOptions.getHelpBroker().enableHelpKey(DSWorkbenchMainFrame.getSingleton().getRootPane(), "index", GlobalOptions.getHelpBroker().getHelpSet());
            }
            // </editor-fold>
            
            t.stopRunning();
            setVisible(false);
            GlobalOptions.removeDataHolderListener(this);

            return HIDE_RESULT.SUCCESS;
        } catch (Throwable th) {
            logger.fatal("Fatal error while running DS Workbench", th);
            showFatalError(th);
            return HIDE_RESULT.ERROR;
        }
    }

    private void showFatalError(Throwable t) {
        FatalErrorDialog errorDialog = new FatalErrorDialog(self, true);
        errorDialog.setLocationRelativeTo(self);
        errorDialog.show(t);
    }

    public static class ExceptionHandler
            implements Thread.UncaughtExceptionHandler {

        public void handle(Throwable thrown) {
            // for EDT exceptions
            handleException(Thread.currentThread().getName(), thrown);
        }

        @Override
        public void uncaughtException(Thread thread, Throwable thrown) {
            // for other uncaught exceptions
            handleException(thread.getName(), thrown);
        }

        protected void handleException(String tname, Throwable thrown) {
            logger.warn("Unhandled exception in thread '" + tname + "'", thrown);
        }
    }
    private boolean SPECIAL_DEBUG_MODE = false;

    public void initializeSuperSpecialDebugFeatures() {
        if (SPECIAL_DEBUG_MODE) {
            return;
        }
        SystrayHelper.showInfoMessage("Switching to debug mode");
        deadlockDetector = new ThreadDeadlockDetector();
        deadlockDetector.addListener(new DefaultDeadlockListener());
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);
        logger.debug("==========================");
        logger.debug("==DEBUG MODE ESTABLISHED==");
        logger.debug("==========================");

        logger.debug("---------System Information---------");
        logger.debug("Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.version") + "/" + System.getProperty("os.arch") + ")");
        logger.debug("Java: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version") + " (" + System.getProperty("java.home") + ")");
        logger.debug("Working Dir: " + System.getProperty("user.dir"));
        logger.debug("------------------------------------");
        SPECIAL_DEBUG_MODE = true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        TranslationManager.setWorkbenchBoot(true);
        File runningIndicator = new File("runningFile");
        if (runningIndicator.exists()) {
            try {
                GlobalOptions.loadProperties(true);
                GlobalDefaults.initialize();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(DSWorkbenchSplashScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            int answer = JOptionPaneHelper.showQuestionConfirmBox(null, trans.get("DSWorkbenchnochlaufenwuerde"), trans.get("Absturz"), trans.get("Nein"), trans.get("Ja"));

            if(answer == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        }
        
        int mode = -1;
        int minimal = 0;
        boolean ssd = false;
        if (args != null) {
            for (String arg : args) {
                switch (arg) {
                    case "-d":
                    case "--debug":
                        //debug mode
                        mode = 0;
                        SystrayHelper.showInfoMessage("Running in debug mode");
                        break;
                    case "-m":
                        minimal = 1;
                        break;
                    case "-ssd":
                        ssd = true;
                        break;
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());

        if (mode == 0) {
            Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);
        }
        GlobalOptions.setMinimalVersion(minimal == 1);

        try {
            GlobalOptions.initialize();
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.error("Failed to setup LnF", e);
        }

        final boolean useSSD = ssd;

        SwingUtilities.invokeLater(() -> {
            try {
                DSWorkbenchSplashScreen.getSingleton().setLocationRelativeTo(null);
                if (useSSD) {
                    DSWorkbenchSplashScreen.getSingleton().initializeSuperSpecialDebugFeatures();
                }
                DSWorkbenchSplashScreen.getSingleton().setVisible(true);
            } catch (Exception e) {
                logger.error("Fatal application error", e);
            }
        });
    }
    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JDialog jProfileDialog;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JProgressBar jStatusOutput;
    private javax.swing.JTree jTree1;
    // End of variables declaration                   

    @Override
    public void fireDataHolderEvent(String eventMessage) {
        jStatusOutput.setString(eventMessage);
    }

    public void updateStatus() {
        jStatusOutput.repaint();
    }

    @Override
    public void fireDataLoadedEvent(boolean pSuccess) {
        if (pSuccess) {
            jStatusOutput.setString(trans.get("Datengeladen"));
        } else {
            jStatusOutput.setString(trans.get("Downloadfehlgeschlagen"));
        }
    }
}

class HideSplashTask extends TimerTask {

    public HideSplashTask() {
        super();
    }

    @Override
    public void run() {
        try {
            switch (DSWorkbenchSplashScreen.getSingleton().hideSplash()) {
                case ERROR: {
                    System.exit(1);
                    break;
                }
                case RESTART_NEEDED: {
                    System.exit(0);
                    break;
                }
                default: {
                    //finally, add the shutdown hook to guarantee a proper termination
                    Runtime.getRuntime().addShutdownHook(MainShutdownHook.getSingleton());
                    GlobalOptions.setStarted();
                    break;
                }
            }
        } catch (Throwable t) {
            System.exit(1);
        }
    }
}

class SplashRepaintThread extends Thread {

    private boolean running = true;

    public SplashRepaintThread() {
        setName("SplashHideThread");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            DSWorkbenchSplashScreen.getSingleton().updateStatus();
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
}
