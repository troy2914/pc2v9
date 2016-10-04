package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Add/Edit ContestTime Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditScheduledStartTimePane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;
    
    private boolean populatingGUI = true;

    private JPanel centerPane = null;

    private JLabel scheduledStartTimeLabel;
    private JTextField scheduledStartTimeTextBox;

    private ContestInformation contestInfo;
    private JPanel scheduledStartTimePanel;
    private JPanel startTimeButtonPanel;
    private JButton clearStartTimeButton;
    private JButton setStartToNowButton;
    private JComboBox<Integer> incrementTimeComboBox;
    private JButton incrementTimeButton;
    private JButton decrementTimeButton;


    /**
     * This constructor creates an EditScheduledStartTimePane containing a message pane,
     * a button pane with Update and Cancel buttons, and a center pane allowing editing of the Scheduled Start Time. 
     * 
     */
    public EditScheduledStartTimePane() {
        super();
        setPreferredSize(new Dimension(530, 350));
        initialize();
    }

    /**
     * This method initializes the EditScheduledStartTimePane with a message pane,
     * a button pane and a center pane allowing editing of the Scheduled Start Time.
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(530, 350));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
    }

    /**
     * It is the responsibility of classes using this EditScheduledStartTimePane to call this method and
     * provide a valid {@link IInternalContest} (model) and {@link IInternalController} before using the pane.
     */
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        addWindowCloserListener();
    }

    private void addWindowCloserListener() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCancelButton();
                        }
                    });
                } 
            }
        });
    }

    public String getPluginTitle() {
        return "Edit ScheduledStartTime Pane";
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messagePane = new JPanel();
            messagePane.setMinimumSize(new Dimension(10, 30));
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new Dimension(25, 30));
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * This method initializes buttonPane with Update and Cancel buttons
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(45);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * Enable the Update button.  This method should be invoked by any method which changes the state of
     * any GUI component (such as editing the Scheduled Start Time textbox, or pressing buttons which 
     * indirectly update the textbox).
     * 
     */
    private void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        setButtonStatesAndLabels(true);
    }


    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setToolTipText("Apply the specified Scheduled Start Time and (if not \"undefined\") set the contest to automatically start at the specified time)");
            updateButton.setText("Update");
            updateButton.setEnabled(false);
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleUpdate();
                }
            });
        }
        return updateButton;
    }

    /**
     * This method is called when the Update button is pressed.  It verifies that that contents of the
     * Scheduled Start Time textbox represent a valid time (including "<undefined>" or null or the empty string);
     * if so it stores the specified Scheduled Start Time in the Contest model.
     * 
     */
    protected void handleUpdate() {


        //check ScheduledStartTime in the GUI textbox
        if (!validateScheduledStartTimeField()) {
            //new scheduled start time is invalid; just return (message issued by validateScheduledStartTimeField())
            return;
        }
        
        
        //get the existing ContestInfo from the contest, insert new scheduled start time into it
        ContestInformation contestInfo = getContest().getContestInformation();
        contestInfo.setScheduledStartTime(getScheduledStartTimeFromGUI());

        //put the updated ContestInfo back into the Controller
        getController().updateContestInformation(contestInfo);

        cancelButton.setText("Close");
        updateButton.setEnabled(false);

        //we're done; hide the GUI
        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }
    
    /**
     * Extracts the Scheduled Start Time from the GUI and returns it as a GregorianCalendar (Date).
     * 
     * @return the GregorianCalendar representing the Scheduled Start Time in the GUI, or null if the GUI string does
     *      not represent a valid Date
     */
    private GregorianCalendar getScheduledStartTimeFromGUI() {
        //Pull the string out of the GUI, check for null, empty, or "undefined" and if so return null;
        // otherwise, parse the time string and return a new GregorianCalendar (return null if the parse fails).
        //get the Scheduled Start Time field from the GUI
        String guiString = getScheduledStartTimeTextBox().getText();
        
        //check for values that represent "no Scheduled Start Time"
        if (guiString==null || guiString.length()==0 || guiString.trim().equals("") || guiString.equalsIgnoreCase("<undefined>") ) {
            return null;
        } else {
            
            //try parsing the GUI string into a GregorianCalendar
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //TODO: add support for yyyy/MM/dd being optional
            Date date;
            try {
                date = df.parse(guiString);
            } catch (ParseException e1) {
                //the input text does not parse as a date matching the format (pattern)
                showMessage("<html>Invalid Scheduled Start Time (must match yyyy-mm-dd hh:mm<br> or the string \"&lt;undefined&gt;\" or the empty string)");
                return null ;
            }
            
            //date parses properly; put it into a Calendar object
            //TODO: should put the calendar in non-lenient mode and catch exceptions due to, e.g. month=13
            GregorianCalendar parsedDate = (GregorianCalendar) Calendar.getInstance();
            parsedDate.setTime(date);
            
            return parsedDate;
        }
    }
    
    /**
     * Verify that the Scheduled Start Time entry is valid. Valid start times are
     * strings of the form "yyyy-mm-dd hh:mm" or "<undefined>" or an empty string.
     * 
     * @return true if the ScheduledStartTimeTextbox field contains either (1) a valid
     *     start date/time (in the future and in the proper format) or (2) the string "<undefined>"
     *     or (3) the empty string;
     *     false otherwise.
     */
    private boolean validateScheduledStartTimeField() {
        
        String textBoxStartTime = getScheduledStartTimeTextBox().getText() ;
        GregorianCalendar tempScheduledStartTime ;
        
        if (textBoxStartTime.equalsIgnoreCase("<undefined>") || textBoxStartTime.equals("") || textBoxStartTime.equals("<already started>") ) {
            return true;
        } else {
            
            //parse the scheduled start time to be sure it's valid
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //TODO: add support for yyyy/MM/dd being optional
            Date date;
            try {
                date = df.parse(textBoxStartTime);
            } catch (ParseException e1) {
                //the input text does not parse as a date matching the format (pattern)
                showMessage("<html>Invalid Scheduled Start Time (must match yyyy-mm-dd hh:mm<br> or the string \"&lt;undefined&gt;\" or the empty string)");
                return false ;
            }
            
            //date parses properly; put it into a Calendar object
            //TODO: should put the calendar in non-lenient mode and catch exceptions due to, e.g. month=13
            tempScheduledStartTime = (GregorianCalendar) Calendar.getInstance();
            tempScheduledStartTime.setTime(date);
            
            //verify the scheduled start time is in the future
            GregorianCalendar now = (GregorianCalendar) Calendar.getInstance();
            if (!tempScheduledStartTime.after(now)) {
                showMessage("Scheduled start date/time must be in the future");
                return false;
            }
        }
                   
        //if we get here, the date/time in the text box must have been valid
        return true ;
    }
    

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    protected void handleCancelButton() {

        if (getUpdateButton().isEnabled()) {
            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Scheduled Start Time has been modified;"
                    + "\n do you want to save the changes?\n", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                handleUpdate();

                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            } else if (result == JOptionPane.NO_OPTION) {
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }
        } else {
            // Close
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    public void setContestInfo(ContestInformation inContestInfo) {
        this.contestInfo = inContestInfo;
        if (contestInfo!=null) {
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    populateGUI(contestInfo);
                    setButtonStatesAndLabels(false);    //false = "Update" button should NOT be enabled
                    showMessage("");
                }
            });
            
        } else {
            //we got passed a null ContestInfo; not good
            if (getController()!=null) {
                Log log = getController().getLog();
                if (log!=null) {
                    log.warning("EditScheduledStartTimePane: received NULL ContestInformation; cannot continue");
                } else {
                    System.err.println ("EditScheduledStartTimePane: received NULL ContestInformation and cannot get Log from controller!");
                }
            } else {
                System.err.println ("EditScheduledStartTimePane: received NULL ContestInformation and cannot get Controller (so, no Log)");
            }
        }
    }
    
    private void populateGUI(ContestInformation contestInfo) {

        populatingGUI = true;
        
        //put the ScheduledStartTime into the GUI
        GregorianCalendar scheduledStartTime = contestInfo.getScheduledStartTime();
        String displayStartTime = "";
        if (scheduledStartTime == null) {
            displayStartTime = "<undefined>";
        } else {
            displayStartTime = getGregorianTimeAsString(scheduledStartTime);
        }
        
        IInternalContest contest = getContest();
        if (contest==null) {
            System.out.println("EditScheduledStartTimePane: getContest() returned null");
        } else {
            ContestTime time = contest.getContestTime() ;
            if (time==null) {
                System.out.println("EditScheduledStartTimePane: getContestTime() returned null");
            }
        }
        if (getContest().getContestTime().isContestStarted()) {
            getScheduledStartTimeTextBox().setText("<already started>"); 
            getScheduledStartTimeTextBox().setToolTipText("Scheduled start time cannot be set when the contest has already started.");
            getScheduledStartTimeTextBox().setEditable(false);
        } else {
            getScheduledStartTimeTextBox().setText(displayStartTime);
            getScheduledStartTimeTextBox().setToolTipText(
                    "<html>\r\nEnter the future date/time when the contest is scheduled to start, in format yyyy-mm-dd hh:mm;"
                    + "\r\n<br>\r\nor enter \"&lt;undefined&gt;\" or an empty string to clear any scheduled start time."
                    + "\r\n<br>\r\nNote that hh:mm must be in \"24-hour\" time (e.g. 1pm = 13:00)\r\n</html>");
            getScheduledStartTimeTextBox().setEditable(true);
        }
        
        getUpdateButton().setVisible(true);
        setButtonStatesAndLabels(false);

        populatingGUI = false;
    }

    protected void setButtonStatesAndLabels(boolean fieldsChanged) {
        if (fieldsChanged) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        updateButton.setEnabled(fieldsChanged);
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText(message);
            }
        });
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setMinimumSize(new Dimension(10, 500));
            centerPane.setLayout(new BorderLayout(0, 0));
            centerPane.add(getScheduledStartTimePanel(), BorderLayout.NORTH);
            centerPane.add(getStartTimeButtonPanel(), BorderLayout.CENTER);
        }
        return centerPane;
    }
    
    /**
     * Convert a GregorianCalendar date/time to a displayable string in yyyy-mm-dd hh:mm form.
     */
    private String getGregorianTimeAsString(GregorianCalendar cal) {
        
        String retString = "<undefined>";
        if (cal != null) {
            //extract fields from input and build string
            //TODO:  need to deal with the difference between displaying LOCAL time and storing UTC

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            fmt.setCalendar(cal);
            retString = fmt.format(cal.getTime());

        }

        return retString;
    }

    /**
     * Convert String to second. Expects input in form: ss or mm:ss or hh:mm:ss
     * 
     * @param s
     *            string to be converted to seconds
     * @return -1 if invalid time string, 0 or >0 if valid
     */
    public long stringToLongSecs(String s) {

        if (s == null || s.trim().length() == 0) {
            return -1;
        }

        String[] fields = s.split(":");
        long hh = 0;
        long mm = 0;
        long ss = 0;
        
        switch (fields.length ) {
            case 3:
                hh = stringToLong(fields[0]);
                mm = stringToLong(fields[1]);
                ss = stringToLong(fields[2]);
                break;
            case 2:
                mm = stringToLong(fields[0]);
                ss = stringToLong(fields[1]);
                break;
            case 1:
                ss = stringToLong(fields[0]);
                break;

            default:
                break;
        }

        // System.out.println(" values "+hh+":"+mm+":"+ss);

        long totsecs = 0;
        if (hh != -1) {
            totsecs = hh;
        }
        if (mm != -1) {
            totsecs = (totsecs * 60) + mm;
        }
        if (ss != -1) {
            totsecs = (totsecs * 60) + ss;
        }

        // System.out.println(" values "+hh+":"+mm+":"+ss+" secs="+totsecs);

        if (hh == -1 || mm == -1 || ss == -1) {
            return -1;
        }

        return totsecs;
    }

    /**
     * Parse and return positive long.
     * 
     * @param s1
     * @return -1 if non-long string, else long value
     */
    private long stringToLong(String s1) {
        if (s1 == null) {
            return -1;
        }
        try {
            return Long.parseLong(s1);
        } catch (Exception e) {
            return -1;
        }
    }

    private JLabel getScheduledStartTimeLabel() {
        if (scheduledStartTimeLabel == null) {
            scheduledStartTimeLabel = new JLabel("Scheduled Start Time");
            scheduledStartTimeLabel.setToolTipText("The date/time when the contest is scheduled to start; must be a time in the future.");
        }
        return scheduledStartTimeLabel;
    }

    private JTextField getScheduledStartTimeTextBox() {
        if (scheduledStartTimeTextBox == null) {
            scheduledStartTimeTextBox = new JTextField();
            scheduledStartTimeTextBox.setPreferredSize(new Dimension(200, 20));
            scheduledStartTimeTextBox.setMinimumSize(new Dimension(50, 20));
            scheduledStartTimeTextBox
                    .setToolTipText("<html>\r\nEnter the future date/time when the contest is scheduled to start, in format yyyy-mm-dd hh:mm;\r\n<br>\r\nor enter \"&lt;undefined&gt;\" or an empty string to clear any scheduled start time.\r\n<br>\r\nNote that hh:mm must be in \"24-hour\" time (e.g. 1pm = 13:00)\r\n</html>");
            scheduledStartTimeTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return scheduledStartTimeTextBox;
    }

    private JPanel getScheduledStartTimePanel() {
        if (scheduledStartTimePanel == null) {
        	scheduledStartTimePanel = new JPanel();
        	scheduledStartTimePanel.add(getScheduledStartTimeLabel());
        	scheduledStartTimePanel.add(getScheduledStartTimeTextBox());
        }
        return scheduledStartTimePanel;
    }
    private JPanel getStartTimeButtonPanel() {
        if (startTimeButtonPanel == null) {
        	startTimeButtonPanel = new JPanel();
        	startTimeButtonPanel.add(getClearStartTimeButton());
        	startTimeButtonPanel.add(getSetStartToNowButton());
        	startTimeButtonPanel.add(getIncrementTimeComboBox());
        	startTimeButtonPanel.add(getIncrementTimeButton());
        	startTimeButtonPanel.add(getDecrementTimeButton());
        }
        return startTimeButtonPanel;
    }
    private JButton getClearStartTimeButton() {
        if (clearStartTimeButton == null) {
        	clearStartTimeButton = new JButton("Clear");
        	clearStartTimeButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        setStartTimeToUndefined();
        	    }
        	});
        	clearStartTimeButton.setToolTipText("Resets the Scheduled Start Time to \"undefined\" (which in turn means the contest will not start automatically)");
        }
        return clearStartTimeButton;
    }
    
    /**
     * Sets the Scheduled Start Time GUI textbox to "<undefined>", and enables the "Update" button.
     */
    protected void setStartTimeToUndefined() {
        getScheduledStartTimeTextBox().setText("<undefined>");
        enableUpdateButton();
        showMessage("");
    }

    private JButton getSetStartToNowButton() {
        if (setStartToNowButton == null) {
        	setStartToNowButton = new JButton("Set to Now");
        	setStartToNowButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        setStartTimeToNow();
        	    }
        	});
        	setStartToNowButton.setToolTipText("Sets the Scheduled Start Time to the next whole minute which is at least 30 seconds from now (and schedules the contest to automatically start at that time)");
        }
        return setStartToNowButton;
    }
    
    /**
     * Sets the Scheduled Start Time GUI textbox to the string representing the time Now + 30 seconds, 
     * rounded up to the next whole minute (to comply with the CLICS specification that says the Start Time should not be
     * able to be set to less than 30 seconds in the future; this specification refers to the Web /starttime interface but
     * it makes sense to keep the GUI consistent with that spec).
     */
    protected void setStartTimeToNow() {
        
        GregorianCalendar now = new GregorianCalendar();
        
        //add 30 seconds to the current time
        now.add(Calendar.SECOND, 30);
        
        //bump the minute up by one and clear lower fields to zero
        now.roll(Calendar.MINUTE, true);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        
        getScheduledStartTimeTextBox().setText(getGregorianTimeAsString(now));
                
        enableUpdateButton();
        showMessage("");
    }

    private JComboBox<Integer> getIncrementTimeComboBox() {
        if (incrementTimeComboBox == null) {
        	incrementTimeComboBox = new JComboBox<Integer>();
        	incrementTimeComboBox.setMaximumRowCount(9);
        	incrementTimeComboBox.setModel(new DefaultComboBoxModel<Integer>());
        	String [] incrementValues = new String[] {"0", "1", "2", "5", "10", "20", "30", "45", "60"};
        	for (String str : incrementValues) {
        	    incrementTimeComboBox.addItem(Integer.parseInt(str));
        	}
        	incrementTimeComboBox.setToolTipText("Select the amount (in minutes) to be added to the Scheduled Start Time, then press \"Increment\"");
        	incrementTimeComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (getIncrementTimeComboBox().getSelectedIndex()>0) {
                        getIncrementTimeButton().setEnabled(true);
                        getDecrementTimeButton().setEnabled(true);
                    } else {
                        getIncrementTimeButton().setEnabled(false);
                        getDecrementTimeButton().setEnabled(false);
                    }
                    showMessage("");
                }
        	});
        }
        return incrementTimeComboBox;
    }
    
    private JButton getIncrementTimeButton() {
        if (incrementTimeButton == null) {
        	incrementTimeButton = new JButton("Increment");
        	incrementTimeButton.setEnabled(false);
        	incrementTimeButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        incrementGUIStartTime();
        	    }
        	});
        	incrementTimeButton.setToolTipText("Increments the Scheduled Start Time by the amount selected in the drop-down list");
        }
        return incrementTimeButton;
    }
    
    private JButton getDecrementTimeButton() {
        if (decrementTimeButton == null) {
            decrementTimeButton = new JButton("Decrement");
            decrementTimeButton.setEnabled(false);
            decrementTimeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    decrementGUIStartTime();
                }
            });
            decrementTimeButton.setToolTipText("Decrements the Scheduled Start Time by the amount selected in the drop-down list");
        }
        return decrementTimeButton;
    }

    /**
     * Increments the Scheduled Start Time displayed in the GUI by the amount selected in the incrementTimeComboBox.
     */
    protected void incrementGUIStartTime() {
        
        GregorianCalendar currentTextboxStartTime = getScheduledStartTimeFromGUI();
        
        if (currentTextboxStartTime==null) {
            showMessage("Invalid time in Scheduled Start Time textbox; cannot increment");
        } else {
            currentTextboxStartTime.add(Calendar.MINUTE, ((Integer)(getIncrementTimeComboBox().getSelectedItem())));
            getScheduledStartTimeTextBox().setText(getGregorianTimeAsString(currentTextboxStartTime));
            enableUpdateButton();  
            showMessage("");
        }
    }

    /**
     * Decrements the Scheduled Start Time displayed in the GUI by the amount selected in the incrementTimeComboBox.
     */
    protected void decrementGUIStartTime() {
        
        GregorianCalendar currentTextboxStartTime = getScheduledStartTimeFromGUI();
        
        if (currentTextboxStartTime==null) {
            showMessage("Invalid time in Scheduled Start Time textbox; cannot decrement");
        } else {
            currentTextboxStartTime.add(Calendar.MINUTE, -(((Integer)(getIncrementTimeComboBox().getSelectedItem()))));
            getScheduledStartTimeTextBox().setText(getGregorianTimeAsString(currentTextboxStartTime));
            enableUpdateButton();  
            showMessage("");
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"