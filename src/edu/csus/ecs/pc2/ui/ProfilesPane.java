package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.ibm.webrunner.j2mclb.util.HeapSorter;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.ContestTimeComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IProfileListener;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileEvent;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.report.ProfileCloneSettingsReport;

/**
 * Profile administration pane.
 * 
 * Provides a front end to all profile functions, like rename, change, clone, etc.
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfilesPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 9075523788534575300L;

    private ProfileSaveFrame profileSaveFrame = null;

    private JLabel profileNameLabel = null;

    private JButton switchButton = null;

    private JButton setButton = null;

    private JTextField profileTextField = null;

    private JPanel centerPane = null;

    private JPanel buttonPane = null;

    private JButton newButton = null;

    private JButton exportButton = null;

    private JButton cloneButton = null;

    private JButton resetContestButton = null;

    private ResetContestFrame resetContestFrame = null;

    private JTextField profileDescriptionTextField = null;

    private JLabel profileDescriptionLabel = null;

    private JPanel topPanel = null;

    private MCLB profilesListBox = null;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    private JButton reportButton = null;

    /**
     * This method initializes
     * 
     */
    public ProfilesPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        profileNameLabel = new JLabel();
        profileNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        profileNameLabel.setBounds(new Rectangle(51, 29, 134, 23));
        profileNameLabel.setText("Profile Name");
        profileNameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() > 1 && e.isControlDown() && e.isShiftDown()) {
                    Profile profile = getContest().getProfile();
                    String message = "Contest Id " + profile.getContestId();
                    JOptionPane.showMessageDialog(null, message);
                }
            }
        });
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(729, 319));
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);

        this.add(getTopPanel(), BorderLayout.NORTH);
        FrameUtilities.centerFrame(this);

    }

    public String getPluginTitle() {
        return "Profiles Pane";
    }

    /**
     * This method initializes Set
     * 
     * @return javax.swing.JButton
     */
    private JButton getSwitchButton() {
        if (switchButton == null) {
            switchButton = new JButton();
            switchButton.setEnabled(true);
            switchButton.setMnemonic(java.awt.event.KeyEvent.VK_W);
            switchButton.setPreferredSize(new java.awt.Dimension(100, 26));
            switchButton.setSelected(false);
            switchButton.setText("Switch");
            switchButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    switchSelectedProfile();
                }
            });
        }
        return switchButton;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSetButton() {
        if (setButton == null) {
            setButton = new JButton();
            setButton.setEnabled(false);
            setButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
            setButton.setBounds(new Rectangle(556, 54, 100, 26));
            setButton.setText("Set");
            setButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    renameProfile();
                }
            });
        }
        return setButton;
    }

    /**
     * This method initializes profileTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProfileTextField() {
        if (profileTextField == null) {
            profileTextField = new JTextField();
            profileTextField.setBounds(new Rectangle(202, 25, 303, 30));
            profileTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableSetButton();
                }
            });
        }
        return profileTextField;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            profileDescriptionLabel = new JLabel();
            profileDescriptionLabel.setText("Description");
            profileDescriptionLabel.setBounds(new Rectangle(51, 78, 134, 23));
            profileDescriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getProfilesListBox(), BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            buttonPane.add(getNewButton(), null);
            buttonPane.add(getResetContestButton(), null);
            buttonPane.add(getCloneButton(), null);
            buttonPane.add(getSwitchButton(), null);
            buttonPane.add(getExportButton(), null);
            buttonPane.add(getReportButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes newButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getNewButton() {
        if (newButton == null) {
            newButton = new JButton();
            newButton.setText("New");
            newButton.setMnemonic(java.awt.event.KeyEvent.VK_N);
            newButton.setEnabled(true);
            newButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newProfile();
                }
            });
        }
        return newButton;
    }

    /**
     * This method initializes jButton2
     * 
     * @return javax.swing.JButton
     */
    private JButton getExportButton() {
        if (exportButton == null) {
            exportButton = new JButton();
            exportButton.setText("Export");
            exportButton.setMnemonic(java.awt.event.KeyEvent.VK_X);
            exportButton.setEnabled(false);
            exportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    exportProfile();
                }
            });
        }
        return exportButton;
    }

    /**
     * This method initializes cloneButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloneButton() {
        if (cloneButton == null) {
            cloneButton = new JButton();
            cloneButton.setText("Clone");
            cloneButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cloneButton.setEnabled(true);
            cloneButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    cloneProfile();
                }
            });
        }
        return cloneButton;
    }

    protected void cloneProfile() {
        getProfileSaveFrame().setTitle("Clone/Copy existing profile: " + getProfileName());
        getProfileSaveFrame().setSaveButtonName(ProfileSavePane.CLONE_BUTTON_NAME);
        getProfileSaveFrame().setVisible(true);
    }

    private String getProfileName() {
        return getContest().getProfile().getName();
    }

    protected void newProfile() {
        getProfileSaveFrame().setTitle("Create new profile");
        getProfileSaveFrame().setSaveButtonName(ProfileSavePane.NEW_BUTTON_NAME);
        getProfileSaveFrame().setVisible(true);
    }

    protected void exportProfile() {
        getProfileSaveFrame().setTitle("Export settings " + getProfileName());
        getProfileSaveFrame().setSaveButtonName(ProfileSavePane.EXPORT_BUTTON_NAME);
        getProfileSaveFrame().setVisible(true);
    }

    protected void switchSelectedProfile() {
        int selectedIndex = getProfilesListBox().getSelectedIndex();
        if (selectedIndex == -1) {
            showMessage("No profile selected");
            return;
        }

        ElementId elementId = (ElementId) getProfilesListBox().getKeys()[selectedIndex];
        Profile selectedProfile = getProfile(elementId);

        if (selectedProfile.equals(getContest().getProfile())) {
            showMessage("Currently using profile '" + selectedProfile.getName() + "' (no need to switch)");
            return;
        }
        
        int result = FrameUtilities.yesNoCancelDialog(null, "Are you sure you want to switch to profile "+selectedProfile.getName()+" ?", "Switch Profile");

        if (result == JOptionPane.YES_OPTION) {
            getController().switchProfile(getContest().getProfile(), selectedProfile);
        }
        
    }

    private Profile getProfile(ElementId elementId) {

        for (Profile profile : getContest().getProfiles()) {
            if (profile.getElementId().equals(elementId)) {
                return profile;
            }
        }

        return null;
    }

    protected void renameProfile() {

        if (getProfileTextField() == null || getProfileTextField().getText().trim().length() < 1) {
            showMessage("No profile name specified");
            return;
        }

        String newProfileName = getProfileTextField().getText().trim();

        String description = getProfileDescriptionTextField().getText().trim() + "";

        Profile profile = getContest().getProfile();
        profile.setName(newProfileName);
        profile.setDescription(description);
        getController().updateProfile(profile);
    }

    private void showMessage(String string) {
        JOptionPane.showMessageDialog(this, string);
    }

    public ProfileSaveFrame getProfileSaveFrame() {
        if (profileSaveFrame == null) {
            profileSaveFrame = new ProfileSaveFrame();
        }
        return profileSaveFrame;
    }

    /**
     * This method initializes resetContestButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getResetContestButton() {
        if (resetContestButton == null) {
            resetContestButton = new JButton();
            resetContestButton.setText("Reset");
            resetContestButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    resetContest();
                }
            });
        }
        return resetContestButton;
    }

    protected void resetContest() {

        String siteContestClockRunning = "";

        ContestTime[] contestTimes = getContest().getContestTimes();
        Arrays.sort(contestTimes, new ContestTimeComparator());

        int numberSites = 0; // number of sites with contest clock running/started

        for (ContestTime contestTime : contestTimes) {
            if (contestTime.isContestRunning() && siteConnected(contestTime.getSiteNumber())) {
                Site site = getContest().getSite(contestTime.getSiteNumber());
                siteContestClockRunning = siteContestClockRunning + ", " + site.format(Site.LONG_NAME_PATTERN);
                numberSites++;
            }
        }

        if (numberSites != 0) {
            siteContestClockRunning = siteContestClockRunning.substring(2); // remove ", " off front of string to make
            String sitePhrase = "a site";
            if (numberSites > 1) {
                sitePhrase = numberSites + " sites";
            }
            JOptionPane.showMessageDialog(this, "Contest Clock not stopped at " + sitePhrase + " (" + siteContestClockRunning + ")\n All contest clocks must be stopped", "Unable to reset",
                    JOptionPane.ERROR_MESSAGE);
        } else {

            showResetDialog();

        }
    }

    private boolean siteConnected(int siteNumber) {
        ClientId serverId = new ClientId(siteNumber, Type.SERVER, 0);
        return getContest().isLocalLoggedIn(serverId) || isThisSite(siteNumber);
    }

    private boolean isThisSite(int siteNumber) {
        return getContest().getSiteNumber() == siteNumber;
    }

    private void showResetDialog() {

        if (resetContestFrame == null) {
            resetContestFrame = new ResetContestFrame();
            resetContestFrame.setContestAndController(getContest(), getController());
        }

        resetContestFrame.setVisible(true);
    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        getProfileSaveFrame().setContestAndController(inContest, inController);

        Profile profile = getContest().getProfile();
        updateProfileInformation(profile);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                refreshProfilesList();
            }

        });

        inContest.addProfileListener(new ProfileListenerImplementation());
    }

    protected void refreshProfilesList() {

        try {
            Profile[] profiles = getContest().getProfiles();

            getProfilesListBox().removeAllRows();

            getSwitchButton().setEnabled(false);
            if (profiles.length > 0) {

                for (Profile profile : profiles) {
                    Object[] objects = buildProfileRow(profile);
                    getProfilesListBox().addRow(objects, profile.getElementId());
                }
                getProfilesListBox().autoSizeAllColumns();
                getSwitchButton().setEnabled(true);
            }

            updateProfileInformation(getContest().getProfile());

        } catch (Exception e) {
            getController().getLog().log(Log.DEBUG, "Exception refreshing profile list", e);
        }
    }

    private Object[] buildProfileRow(Profile profile) {

        // Object[] cols = { "Name", "Description", "Create Date" }

        int numberColumns = profilesListBox.getColumnCount();
        Object[] c = new String[numberColumns];

        c[0] = profile.getName();
        if (profile.equals(getContest().getProfile())) {
            c[0] = "(Current) " + profile.getName();
        }
        c[1] = profile.getDescription();
        c[2] = formatter.format(profile.getCreateDate());

        return c;
    }

    private void updateProfileInformation(final Profile profile) {

        if (profile != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getProfileTextField().setText(profile.getName());
                    profileNameLabel.setToolTipText("Contest Profile Name " + profile.getContestId());
                    getProfileDescriptionTextField().setText(profile.getDescription());
                    enableSetButton();
                }
            });
        }
    }

    /**
     * Profile Listener Implementation
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class ProfileListenerImplementation implements IProfileListener {

        public void profileAdded(ProfileEvent event) {
            updateProfileRow(event.getProfile());
        }

        public void profileChanged(ProfileEvent event) {
            updateProfileRow(event.getProfile());
            Profile profile = getContest().getProfile();
            updateProfileInformation(profile);
        }

        public void profileRemoved(ProfileEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    refreshProfilesList();
                }
            });
        }

        public void profileRefreshAll(ProfileEvent profileEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    refreshProfilesList();
                }
            });
        }
    }

    /**
     * This method initializes profileDescriptionTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProfileDescriptionTextField() {
        if (profileDescriptionTextField == null) {
            profileDescriptionTextField = new JTextField();
            profileDescriptionTextField.setBounds(new Rectangle(202, 75, 303, 29));
            profileDescriptionTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableSetButton();
                }
            });
        }
        return profileDescriptionTextField;
    }

    protected void enableSetButton() {

        Profile profile = getContest().getProfile();

        String name = getProfileTextField().getText();
        String description = getProfileDescriptionTextField().getText();

        boolean enable = (!profile.getName().equals(name)) || (!profile.getDescription().equals(description));

        getSetButton().setEnabled(enable);
    }

    /**
     * This method initializes topPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel();
            topPanel.setLayout(null);
            topPanel.setPreferredSize(new Dimension(120, 120));
            topPanel.add(profileNameLabel, null);
            topPanel.add(profileDescriptionLabel, null);
            topPanel.add(getProfileTextField(), null);
            topPanel.add(getProfileDescriptionTextField(), null);
            topPanel.add(getSetButton(), null);
        }
        return topPanel;
    }

    /**
     * This method initializes profilesListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getProfilesListBox() {
        if (profilesListBox == null) {
            profilesListBox = new MCLB();

            Object[] cols = { "Name", "Description", "Create Date" };
            profilesListBox.addColumns(cols);

            HeapSorter sorter = new HeapSorter();

            int idx = 0;

            profilesListBox.setColumnSorter(idx++, sorter, 1); // Name
            profilesListBox.setColumnSorter(idx++, sorter, 2); // Description
            profilesListBox.setColumnSorter(idx++, sorter, 3); // Create Date

        }
        return profilesListBox;
    }

    public void updateProfileRow(final Profile profile) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Object[] objects = buildProfileRow(profile);
                int rowNumber = profilesListBox.getIndexByKey(profile.getElementId());
                if (rowNumber == -1) {
                    profilesListBox.addRow(objects, profile.getElementId());
                } else {
                    profilesListBox.replaceRow(objects, rowNumber);
                }
                profilesListBox.autoSizeAllColumns();
                profilesListBox.sort();
            }
        });
    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Report");
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    createReport(new ProfileCloneSettingsReport());
                }
            });
        }
        return reportButton;
    }

    protected void createReport(IReport report) {
        report.setContestAndController(getContest(), getController());

        try {
            createAndViewReportFile(report, new Filter(), getController().getLog());
        } catch (IOException e) {
            StaticLog.log("Exception creating report", e);
            JOptionPane.showMessageDialog(this, "Exception in report " + e.getLocalizedMessage());
        }
    }

} // @jve:decl-index=0:visual-constraint="25,9"
