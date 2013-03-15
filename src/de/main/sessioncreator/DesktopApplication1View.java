/*
 * Copyright 2011-2013 Sven Finsterwalder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.main.sessioncreator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Document;
import org.apache.commons.io.FileUtils;
import javax.swing.JLayeredPane;

/**
 * The application's main frame.
 * @author Sven Finsterwalder
 */
public class DesktopApplication1View extends FrameView {

    FileHelper fileHelper = new FileHelper();
    TextHelper textHelper = new TextHelper();
    SwingHelper swingHelper = new SwingHelper();
    ReportingHelper rh = new ReportingHelper();
    DefaultTableModel model;
    public String ChoosenCharter;
    public String UserShortName;
    public String ChoosenAreas;
    SwingWorker<String, Void> worker;
    public String returnvalue;
    private String Filename;
    private String TesterRealname;
    private String submittedFolder;
    private String realFilename;
    private boolean running = false;
    private boolean wizardshown = true;
    public boolean edited = false;
    public long HS = 0;
    public long sek = 0;
    public long min = 0;
    public int h = 0;
    HashMap<JLabel, String> percent = new HashMap<JLabel, String>();
    ArrayList<JLabel> listofLabels = new ArrayList<JLabel>();
    MouseListener popupListener2;

    public DesktopApplication1View(SingleFrameApplication app) {
        super(app);
        java.net.URL imgURL = getClass().getResource("resources/sc.gif");
        ImageIcon image = new ImageIcon(imgURL, "session creator");
        this.getFrame().setIconImage(image.getImage());
        this.getFrame().pack();
        Dimension dimension = new Dimension(1030, 650);
        this.getFrame().setMinimumSize(dimension);
        this.getFrame().setTitle("SessionCreator - Wizard");

        initComponents();

        swingHelper.setConfigPaths(wizardTfPathTodo);
        swingHelper.setConfigPaths(wizardTfPathSubmitted);
        swingHelper.setConfigPaths(wizardtfCoverageini);
        progressBar.setVisible(false);
        MouseListener popupListener = activateActionListener();
        wizardtaBugs.addMouseListener(popupListener);
        wizardtaIssues.addMouseListener(popupListener);
        swingHelper.setTesterCombox(wizardCmbxTester);
        wizardtabp.setSelectedIndex(0);
        swingHelper.setTab1EnableAt(wizardtabp, 0);
        wizardbtnStart.setVisible(false);
        wizardbtntopStart.setEnabled(false);
        wizardbtnStop.setVisible(false);
        wizardbtntopStop.setEnabled(false);
        wizardbtnSave.setVisible(false);
        wizardbtntopSave.setEnabled(false);
        wizardbtnNew.setVisible(false);
        wizardbtntopNew.setEnabled(false);
        saveMenuItem.setEnabled(false);
        wizardCmbxMoreTester.setEnabled(false);
        if (wizardshown) {
            wizardbtnAddAreas.addActionListener(new AddListener());
            sessionWizardMenuItem.setEnabled(false);
            reviewVieMenuItem.setEnabled(true);
            sessionReportMenuItem.setEnabled(true);
            viewReviewsPanel.setVisible(false);
            reportPanel.setVisible(false);
        }
    }

	private MouseListener activateActionListener() {
		wizardChckBxSecondTester.addActionListener(new chckBxSecondTesterListener());
        JMenuItem menuItem = new JMenuItem("Add text '#Bug'");
        menuItem.addActionListener(new alBugsIssue());
        wizardPopUpBugTab.add(menuItem);
        JMenuItem menuItem2 = new JMenuItem("Add text '#Issue'");
        menuItem2.addActionListener(new alBugsIssue());
        wizardPopUpIssueTab.add(menuItem2);
        MouseListener popupListener = new PopupListener(wizardPopUpIssueTab);
		return popupListener;
	}

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = DesktopApplication1.getApplication().getMainFrame();
            aboutBox = new DesktopApplication1AboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        DesktopApplication1.getApplication().show(aboutBox);
    }

    @Action
    public void showHelpPdf() {
        File f = new File(System.getProperty("user.dir") + "/SessionCreatorHelp.pdf");
        try {
            Desktop.getDesktop().open(f);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error during open the Help:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showSessionWizard() {
        sessionWizardMenuItem.setEnabled(false);
        reviewVieMenuItem.setEnabled(true);
        sessionReportMenuItem.setEnabled(true);
        viewReviewsPanel.setVisible(false);
        mainPanel.validate();
        reportPanel.setVisible(false);
        mainPanel.validate();
        wizardPanel.setVisible(true);
        mainPanel.validate();
        this.getFrame().setTitle("SessionCreator - Wizard");
    }

    private void showReportPanel() {
        sessionWizardMenuItem.setEnabled(true);
        reviewVieMenuItem.setEnabled(true);
        sessionReportMenuItem.setEnabled(false);
        viewReviewsPanel.setVisible(false);
        mainPanel.validate();
        wizardPanel.setVisible(false);
        mainPanel.validate();
        reportOverviewTable.setVisible(false);
        reportPanel.setVisible(true);
        mainPanel.validate();
        this.getFrame().setTitle("SessionCreator - Report");
        //Backgroundprozess to fill data to the reportPanel
        class ReportData extends SwingWorker<DefaultTableModel, Void> {

            @Override
            protected DefaultTableModel doInBackground() throws Exception {
                rh.addReport(reportChartPanel);
                reportScrollPOverviewTabel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                reportScrollPOverviewTabel.setToolTipText("Please wait...");
                reportlblSum.setText("Sums of Charters done: <counting>");
                progressBar.setIndeterminate(true);
                progressBar.setVisible(true);
                model = new DefaultTableModel();
                reportOverviewTable.removeAll();
                model = rh.getTableModel();
                reportOverviewTable.setModel(model);
                reportOverviewTable.setVisible(true);

                String count = rh.getAllSessionCount(model);
                reportlblSum.setText("Sums of Charters done: " + count);
                String[] text = rh.getAllBugsAndIssues();
                reportlblBug.setText(text[0]);
                reportlblIssue.setText(text[1]);

                return model;
            }

            @Override
            protected void done() {
                try {
                    reportScrollPOverviewTabel.setCursor(Cursor.getDefaultCursor());
                    reportScrollPOverviewTabel.setToolTipText("");
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                    reportScrollPOverviewTabel.setEnabled(true);
                    reportOverviewTable.setFillsViewportHeight(true);
                    reportOverviewTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                    TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<DefaultTableModel>(model);
                    reportOverviewTable.setRowSorter(rowSorter);
                    rowSorter.setComparator(1, new Comparator<Integer>() {

                        @Override
                        public int compare(Integer int1, Integer int2) {
                            return int1.intValue() - int2.intValue();
                        }
                    });
                    TableColumn column = null;

                    for (int i = 0; i < 2; i++) {
                        column = reportOverviewTable.getColumnModel().getColumn(i);
                        if (i == 1) {
                            column.setPreferredWidth(10); //third column is bigger
                            DefaultTableCellRenderer myRenderer = new DefaultTableCellRenderer();
                            //Textalignment in second column right
                            myRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
                            column.setCellRenderer(myRenderer);
                        } else {
                            column.setPreferredWidth(490);
                        }
                    }

                } catch ( /* InterruptedException, ExecutionException */Exception e) {
                }
            }
        }
        new ReportData().execute();
    }

    public void showReviewPanel() {
        reviewtaReview.getDocument().addDocumentListener(new reviewTaReviewListener());
        sessionWizardMenuItem.setEnabled(true);
        reviewVieMenuItem.setEnabled(false);
        sessionReportMenuItem.setEnabled(true);
        wizardPanel.setVisible(false);
        mainPanel.validate();
        reportPanel.setVisible(false);
        mainPanel.validate();
        viewReviewsPanel.setVisible(true);
        mainPanel.validate();
        this.getFrame().setTitle("SessionCreator - Review");
        swingHelper.setTab1EnableAt(reviewSessionsTabp, 0);
        reviewbtnNext.setEnabled(true);
        reviewbtnMove.setEnabled(false);
        reviewbtntopMove.setEnabled(false);
        reviewbtnSave.setVisible(false);
        final File directory = new File(wizardTfPathSubmitted.getText());
        reviewCmbxSessiontoReview.removeAllItems();
        reviewCmbxSessiontoReview.addItem("Please select a session");
        fileHelper.charterList.clear();
        if (fileHelper.getFilesWithCharter(directory, false)) {
            for (String toReview : fileHelper.charterList) {
                reviewCmbxSessiontoReview.addItem(toReview);
            }

            fileHelper.getSessionList(wizardTfPathSubmitted.getText());
            reviewtoReviewPanel.removeAll();
            ArrayList<String> Tester = new ArrayList<String>();
            Tester = fileHelper.getTester();
            int[] counter = new int[Tester.size()];
            for (String s : fileHelper.sessionList) {
                for (String n : Tester) {
                    if (s.contains(n.substring(0, 3))) {
                        counter[Tester.indexOf(n)]++;
                        break;
                    }
                }
            }
            for (String n : Tester) {
                reviewtoReviewPanel.add(new JLabel(n.substring(4) + ": " + counter[Tester.indexOf(n)]));
                reviewtoReviewPanel.revalidate();
                listofLabels.add(new JLabel(n.substring(4) + ": " + counter[Tester.indexOf(n)]));
                reviewtoReviewPanel.repaint();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Directory '" + directory + "' does not exsits!\nPlease edit in the config.txt the 'Submitted'-Path and try again.", "Error", JOptionPane.ERROR_MESSAGE);
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

        mainPanel = new JLayeredPane();
        UIManager.put("TabbedPane.selected", Color.green);
        wizardPanel = new javax.swing.JPanel();
        mainPanel.setLayer(wizardPanel, 3);
        WizardToolbar = new javax.swing.JToolBar();
        wizardbtntopNew = new javax.swing.JButton();
        wizardbtntopSave = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        wizardbtntopBack = new javax.swing.JButton();
        wizardbtntopNext = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        wizardbtntopStart = new javax.swing.JButton();
        wizardbtntopStop = new javax.swing.JButton();
        wizardtabp = new javax.swing.JTabbedPane();
        welcomePanel = new javax.swing.JPanel();
        wizardLblWelcome = new javax.swing.JLabel();
        wizardLblChooseName = new javax.swing.JLabel();
        wizardLblpathTodo = new javax.swing.JLabel();
        wizardTfPathTodo = new javax.swing.JTextField();
        wizardhCkBxSubfolder = new javax.swing.JCheckBox();
        wizardLblPathSubmitted = new javax.swing.JLabel();
        wizardTfPathSubmitted = new javax.swing.JTextField();
        wizardCmbxTester = new javax.swing.JComboBox();
        wizardbtnRefreshTester = new javax.swing.JButton();
        wizardChckBxSecondTester = new javax.swing.JCheckBox();
        wizardCmbxMoreTester = new javax.swing.JComboBox();
        charterPanel = new javax.swing.JPanel();
        wizardLblChooseCharter = new javax.swing.JLabel();
        wizardCmbxCharter = new javax.swing.JComboBox();
        wizardScrollPCharterInfo = new javax.swing.JScrollPane();
        wizardtaPreviewCharter = new javax.swing.JTextArea();
        wizardChckBxNewCharter = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        wizardtaNewCharter = new javax.swing.JTextArea();
        areasPanel = new javax.swing.JPanel();
        wizardtabpAreas = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        wizardjScrollPaneAreas = new javax.swing.JScrollPane();
        wizardtaChoosenAreas = new javax.swing.JTextArea();
        wizardLblchooseArea = new javax.swing.JLabel();
        wizardLblpathToCoverageini = new javax.swing.JLabel();
        wizardtfCoverageini = new javax.swing.JTextField();
        wizardbtnRefreshAreas = new javax.swing.JButton();
        wizardbtnRemoveArea = new javax.swing.JButton();
        wizardbtnAddAreas = new javax.swing.JButton();
        wizardbtnSaveTodo = new javax.swing.JButton();
        testsessionPanel = new javax.swing.JPanel();
        wizardLblstartTime = new javax.swing.JLabel();
        wizardPanelTaskBreakd = new javax.swing.JPanel();
        wizardLblDuration = new javax.swing.JLabel();
        wizardLblDesignExecution = new javax.swing.JLabel();
        wizardLblSetup = new javax.swing.JLabel();
        wizardLblChartervs = new javax.swing.JLabel();
        wizardtfDuration = new javax.swing.JTextField();
        wizardtfSessionSetup = new javax.swing.JTextField();
        wizardLblBugReporting = new javax.swing.JLabel();
        wizardtfBugInvestigation = new javax.swing.JTextField();
        wizardtfCharter = new javax.swing.JTextField();
        wizardLblSlash = new javax.swing.JLabel();
        wizardtfOpportunity = new javax.swing.JTextField();
        wizardLblStopfield = new javax.swing.JLabel();
        wizardtfTestDesignExecution = new javax.swing.JTextField();
        wizardLblvs = new javax.swing.JLabel();
        wizardLblOpportunity = new javax.swing.JLabel();
        wizardPanelTestsessionAreas = new javax.swing.JPanel();
        wizardScrollPaneAreas = new javax.swing.JScrollPane();
        wizardtaTestsessionAreas = new javax.swing.JTextArea();
        wizardLblcharterHeadline = new javax.swing.JLabel();
        wizardLblStart = new javax.swing.JLabel();
        wizardLblTester = new javax.swing.JLabel();
        wizardTabpDetailsDTBIR = new javax.swing.JTabbedPane();
        wizardScrollPaneDataFiles = new javax.swing.JScrollPane();
        wizardtaDataFiles = new javax.swing.JTextArea();
        wizardScrollPaneTestNotes = new javax.swing.JScrollPane();
        wizardtaTestNotes = new javax.swing.JTextArea();
        wizardScrollPaneBugs = new javax.swing.JScrollPane();
        wizardtaBugs = new javax.swing.JTextArea();
        wizardScrollPaneIssues = new javax.swing.JScrollPane();
        wizardtaIssues = new javax.swing.JTextArea();
        wizardScrollPaneReview = new javax.swing.JScrollPane();
        wizardtaReview = new javax.swing.JTextArea();
        wizardtfNameOfTester = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        wizardtaCharterdynamic = new javax.swing.JTextArea();
        wizardbtnBack = new javax.swing.JButton();
        wizardbtnStart = new javax.swing.JButton();
        wizardbtnStop = new javax.swing.JButton();
        wizardbtnSave = new javax.swing.JButton();
        wizardbtnNext = new javax.swing.JButton();
        wizardbtnNew = new javax.swing.JButton();
        viewReviewsPanel = new javax.swing.JPanel();
        mainPanel.setLayer(viewReviewsPanel, 1);
        ReviewToolbar = new javax.swing.JToolBar();
        reviewbtntopSave = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        reviewbtntopBack = new javax.swing.JButton();
        reviewbtntopNext = new javax.swing.JButton();
        reviewbtntopMove = new javax.swing.JButton();
        reviewViewlabel = new javax.swing.JLabel();
        reviewSessionsTabp = new javax.swing.JTabbedPane();
        reviewToreviewSessionsPanel = new javax.swing.JPanel();
        reviewCmbxSessiontoReview = new javax.swing.JComboBox();
        reviewtoReviewPanel = new javax.swing.JPanel();
        reviewViewPanel = new javax.swing.JPanel();
        reviewLblStartTime = new javax.swing.JLabel();
        reviewPaneTaskbreakdown = new javax.swing.JPanel();
        reviewLblDuration = new javax.swing.JLabel();
        reviewLblTestExecution = new javax.swing.JLabel();
        reviewLblSetup = new javax.swing.JLabel();
        reviewLblChartervs = new javax.swing.JLabel();
        reviewtfDuration = new javax.swing.JTextField();
        reviewtfSessionSetup = new javax.swing.JTextField();
        reviewLblBugReporting = new javax.swing.JLabel();
        reviewtfBugInvestigation = new javax.swing.JTextField();
        reviewtfCharter = new javax.swing.JTextField();
        reviewtfTestDesignExecution = new javax.swing.JTextField();
        reviewLblvs = new javax.swing.JLabel();
        reviewLblOpportunity = new javax.swing.JLabel();
        reviewPanelTestsessionAreas = new javax.swing.JPanel();
        reviewScrollPaneArea = new javax.swing.JScrollPane();
        reviewtaTestsessionAreasReview = new javax.swing.JTextArea();
        reviewCharterHeadlineLabel = new javax.swing.JLabel();
        reviewLblStart = new javax.swing.JLabel();
        reviewLblTester = new javax.swing.JLabel();
        reviewTabpDTBIR = new javax.swing.JTabbedPane();
        reviewScrollPaneDatafile = new javax.swing.JScrollPane();
        reviewtaDataFiles = new javax.swing.JTextArea();
        reviewScrollPaneTestNotes = new javax.swing.JScrollPane();
        reviewtaTestNotes = new javax.swing.JTextArea();
        reviewScrollPaneBugs = new javax.swing.JScrollPane();
        reviewedipaneBugs = new javax.swing.JEditorPane();
        reviewScrollPaneIssues = new javax.swing.JScrollPane();
        reviewtaIssues = new javax.swing.JTextArea();
        reviewScrollPaneReview = new javax.swing.JScrollPane();
        reviewtaReview = new javax.swing.JTextArea();
        reviewtfNameOfTester = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        reviewtaCharterdynamic = new javax.swing.JTextArea();
        reviewbtnNext = new javax.swing.JButton();
        reviewbtnBack = new javax.swing.JButton();
        reviewbtnSave = new javax.swing.JButton();
        reviewbtnMove = new javax.swing.JButton();
        reportPanel = new javax.swing.JPanel();
        mainPanel.setLayer(reportPanel, 2);
        reportChartPanel = new javax.swing.JPanel();
        reportScrollPOverviewTabel = new javax.swing.JScrollPane();
        reportOverviewTable = new javax.swing.JTable();
        reportlblSum = new javax.swing.JLabel();
        reportlblBug = new javax.swing.JLabel();
        reportlblIssue = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        saveMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        sessionMenu = new javax.swing.JMenu();
        sessionWizardMenuItem = new javax.swing.JMenuItem();
        reviewVieMenuItem = new javax.swing.JMenuItem();
        sessionReportMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        helpMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        wizardPopUpBugTab = new javax.swing.JPopupMenu();
        wizardPopUpIssueTab = new javax.swing.JPopupMenu();

        mainPanel.setMaximumSize(new java.awt.Dimension(990, 900));
        mainPanel.setMinimumSize(new java.awt.Dimension(900, 500));
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(990, 500));

        wizardPanel.setMaximumSize(new java.awt.Dimension(990, 900));
        wizardPanel.setMinimumSize(new java.awt.Dimension(990, 500));
        wizardPanel.setName("wizardPanel"); // NOI18N
        wizardPanel.setPreferredSize(new java.awt.Dimension(990, 500));

        WizardToolbar.setFloatable(false);
        WizardToolbar.setRollover(true);
        WizardToolbar.setName("WizardToolbar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(de.main.sessioncreator.DesktopApplication1.class).getContext().getResourceMap(DesktopApplication1View.class);
        wizardbtntopNew.setIcon(resourceMap.getIcon("wizardbtntopNew.icon")); // NOI18N
        wizardbtntopNew.setToolTipText(resourceMap.getString("wizardbtntopNew.toolTipText")); // NOI18N
        wizardbtntopNew.setDisabledIcon(resourceMap.getIcon("wizardbtntopNew.disabledIcon")); // NOI18N
        wizardbtntopNew.setFocusable(false);
        wizardbtntopNew.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        wizardbtntopNew.setName("wizardbtntopNew"); // NOI18N
        wizardbtntopNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        wizardbtntopNew.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                wizardbtntopNewMouseClicked(evt);
            }
        });
        WizardToolbar.add(wizardbtntopNew);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(de.main.sessioncreator.DesktopApplication1.class).getContext().getActionMap(DesktopApplication1View.class, this);
        wizardbtntopSave.setAction(actionMap.get("saveTestsession")); // NOI18N
        wizardbtntopSave.setIcon(resourceMap.getIcon("wizardbtntopSave.icon")); // NOI18N
        wizardbtntopSave.setToolTipText(resourceMap.getString("wizardbtntopSave.toolTipText")); // NOI18N
        wizardbtntopSave.setDisabledIcon(resourceMap.getIcon("wizardbtntopSave.disabledIcon")); // NOI18N
        wizardbtntopSave.setFocusable(false);
        wizardbtntopSave.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        wizardbtntopSave.setName("wizardbtntopSave"); // NOI18N
        wizardbtntopSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        wizardbtntopSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                wizardbtntopSaveMouseClicked(evt);
            }
        });
        WizardToolbar.add(wizardbtntopSave);

        jSeparator2.setName("jSeparator2"); // NOI18N
        WizardToolbar.add(jSeparator2);

        wizardbtntopBack.setIcon(resourceMap.getIcon("wizardbtntopBack.icon")); // NOI18N
        wizardbtntopBack.setToolTipText(resourceMap.getString("wizardbtntopBack.toolTipText")); // NOI18N
        wizardbtntopBack.setDisabledIcon(resourceMap.getIcon("wizardbtntopBack.disabledIcon")); // NOI18N
        wizardbtntopBack.setEnabled(false);
        wizardbtntopBack.setFocusable(false);
        wizardbtntopBack.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        wizardbtntopBack.setName("wizardbtntopBack"); // NOI18N
        wizardbtntopBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        wizardbtntopBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wizardbtntopBackActionPerformed(evt);
            }
        });
        WizardToolbar.add(wizardbtntopBack);

        wizardbtntopNext.setIcon(resourceMap.getIcon("wizardbtntopNext.icon")); // NOI18N
        wizardbtntopNext.setToolTipText(resourceMap.getString("wizardbtntopNext.toolTipText")); // NOI18N
        wizardbtntopNext.setDisabledIcon(resourceMap.getIcon("wizardbtntopNext.disabledIcon")); // NOI18N
        wizardbtntopNext.setFocusable(false);
        wizardbtntopNext.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        wizardbtntopNext.setMaximumSize(new java.awt.Dimension(23, 23));
        wizardbtntopNext.setMinimumSize(new java.awt.Dimension(23, 23));
        wizardbtntopNext.setName("wizardbtntopNext"); // NOI18N
        wizardbtntopNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wizardbtntopNextActionPerformed(evt);
            }
        });
        WizardToolbar.add(wizardbtntopNext);

        jSeparator1.setName("jSeparator1"); // NOI18N
        WizardToolbar.add(jSeparator1);

        wizardbtntopStart.setIcon(resourceMap.getIcon("wizardbtntopStart.icon")); // NOI18N
        wizardbtntopStart.setToolTipText(resourceMap.getString("wizardbtntopStart.toolTipText")); // NOI18N
        wizardbtntopStart.setDisabledIcon(resourceMap.getIcon("wizardbtntopStart.disabledIcon")); // NOI18N
        wizardbtntopStart.setEnabled(false);
        wizardbtntopStart.setFocusable(false);
        wizardbtntopStart.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        wizardbtntopStart.setName("wizardbtntopStart"); // NOI18N
        wizardbtntopStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        WizardToolbar.add(wizardbtntopStart);

        wizardbtntopStop.setIcon(resourceMap.getIcon("wizardbtntopStop.icon")); // NOI18N
        wizardbtntopStop.setToolTipText(resourceMap.getString("wizardbtntopStop.toolTipText")); // NOI18N
        wizardbtntopStop.setDisabledIcon(resourceMap.getIcon("wizardbtntopStop.disabledIcon")); // NOI18N
        wizardbtntopStop.setEnabled(false);
        wizardbtntopStop.setFocusable(false);
        wizardbtntopStop.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        wizardbtntopStop.setName("wizardbtntopStop"); // NOI18N
        wizardbtntopStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        WizardToolbar.add(wizardbtntopStop);

        wizardtabp.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        wizardtabp.setName("jTabSessionWizard"); // NOI18N
        wizardtabp.setPreferredSize(new java.awt.Dimension(990, 552));

        welcomePanel.setMaximumSize(new java.awt.Dimension(1000, 1000));
        welcomePanel.setName("jPanelWelcome"); // NOI18N

        wizardLblWelcome.setFont(resourceMap.getFont("wizardLblWelcome.font")); // NOI18N
        wizardLblWelcome.setText(resourceMap.getString("wizardLblWelcome.text")); // NOI18N
        wizardLblWelcome.setName("wizardLblWelcome"); // NOI18N

        wizardLblChooseName.setText(resourceMap.getString("wizardLblChooseName.text")); // NOI18N
        wizardLblChooseName.setName("wizardLblChooseName"); // NOI18N

        wizardLblpathTodo.setText(resourceMap.getString("wizardLblpathTodo.text")); // NOI18N
        wizardLblpathTodo.setName("wizardLblpathTodo"); // NOI18N

        wizardTfPathTodo.setText(resourceMap.getString("jTxtFieldTodoSession.text")); // NOI18N
        wizardTfPathTodo.setName("jTxtFieldTodoSession"); // NOI18N

        wizardhCkBxSubfolder.setText(resourceMap.getString("jChckBxUnterordner.text")); // NOI18N
        wizardhCkBxSubfolder.setName("jChckBxUnterordner"); // NOI18N

        wizardLblPathSubmitted.setText(resourceMap.getString("wizardLblPathSubmitted.text")); // NOI18N
        wizardLblPathSubmitted.setName("wizardLblPathSubmitted"); // NOI18N

        wizardTfPathSubmitted.setText(resourceMap.getString("jTxtFieldSubmitted.text")); // NOI18N
        wizardTfPathSubmitted.setName("jTxtFieldSubmitted"); // NOI18N

        wizardCmbxTester.setName("wizardCmbxTester"); // NOI18N

        wizardbtnRefreshTester.setText(resourceMap.getString("wizardbtnRefreshTester.text")); // NOI18N
        wizardbtnRefreshTester.setName("wizardbtnRefreshTester"); // NOI18N
        wizardbtnRefreshTester.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonRefreshTester(evt);
            }
        });

        wizardChckBxSecondTester.setText(resourceMap.getString("wizardChckBxSecondTester.text")); // NOI18N
        wizardChckBxSecondTester.setName("wizardChckBxSecondTester"); // NOI18N

        wizardCmbxMoreTester.setName("wizardCmbxMoreTester"); // NOI18N

        javax.swing.GroupLayout welcomePanelLayout = new javax.swing.GroupLayout(welcomePanel);
        welcomePanel.setLayout(welcomePanelLayout);
        welcomePanelLayout.setHorizontalGroup(
            welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wizardLblWelcome)
                    .addGroup(welcomePanelLayout.createSequentialGroup()
                        .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(wizardLblpathTodo)
                            .addComponent(wizardLblPathSubmitted))
                        .addGap(18, 18, 18)
                        .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(wizardTfPathTodo, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                            .addComponent(wizardhCkBxSubfolder)
                            .addComponent(wizardTfPathSubmitted)))
                    .addGroup(welcomePanelLayout.createSequentialGroup()
                        .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(wizardLblChooseName)
                            .addComponent(wizardChckBxSecondTester))
                        .addGap(24, 24, 24)
                        .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(wizardCmbxMoreTester, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(wizardCmbxTester, 0, 300, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wizardbtnRefreshTester)))
                .addContainerGap(274, Short.MAX_VALUE))
        );
        welcomePanelLayout.setVerticalGroup(
            welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(welcomePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wizardLblWelcome)
                .addGap(11, 11, 11)
                .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wizardLblChooseName)
                    .addComponent(wizardCmbxTester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wizardbtnRefreshTester))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wizardChckBxSecondTester)
                    .addComponent(wizardCmbxMoreTester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wizardLblpathTodo)
                    .addComponent(wizardTfPathTodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(wizardhCkBxSubfolder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(welcomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wizardLblPathSubmitted)
                    .addComponent(wizardTfPathSubmitted, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(398, Short.MAX_VALUE))
        );

        wizardtabp.addTab(resourceMap.getString("jPanelWelcome.TabConstraints.tabTitle"), welcomePanel); // NOI18N

        charterPanel.setName("jPanelCharter"); // NOI18N

        wizardLblChooseCharter.setText(resourceMap.getString("wizardLblChooseCharter.text")); // NOI18N
        wizardLblChooseCharter.setName("wizardLblChooseCharter"); // NOI18N

        wizardCmbxCharter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select one" }));
        wizardCmbxCharter.setName("jComboBxCharter"); // NOI18N
        wizardCmbxCharter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChooseCharterjCombobox1(evt);
            }
        });

        wizardScrollPCharterInfo.setName("wizardScrollPCharterInfo"); // NOI18N

        wizardtaPreviewCharter.setBackground(resourceMap.getColor("wizardtaPreviewCharter.background")); // NOI18N
        wizardtaPreviewCharter.setColumns(20);
        wizardtaPreviewCharter.setLineWrap(true);
        wizardtaPreviewCharter.setRows(5);
        wizardtaPreviewCharter.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        wizardtaPreviewCharter.setEnabled(false);
        wizardtaPreviewCharter.setName("wizardtaPreviewCharter"); // NOI18N
        wizardScrollPCharterInfo.setViewportView(wizardtaPreviewCharter);

        wizardChckBxNewCharter.setText(resourceMap.getString("wizardChckBxNewCharter.text")); // NOI18N
        wizardChckBxNewCharter.setToolTipText(resourceMap.getString("wizardChckBxNewCharter.toolTipText")); // NOI18N
        wizardChckBxNewCharter.setName("wizardChckBxNewCharter"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        wizardtaNewCharter.setColumns(20);
        wizardtaNewCharter.setEditable(false);
        wizardtaNewCharter.setLineWrap(true);
        wizardtaNewCharter.setRows(5);
        wizardtaNewCharter.setWrapStyleWord(true);
        wizardtaNewCharter.setEnabled(false);
        wizardtaNewCharter.setName("wizardtaNewCharter"); // NOI18N
        jScrollPane1.setViewportView(wizardtaNewCharter);

        javax.swing.GroupLayout charterPanelLayout = new javax.swing.GroupLayout(charterPanel);
        charterPanel.setLayout(charterPanelLayout);
        charterPanelLayout.setHorizontalGroup(
            charterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(charterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(charterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wizardChckBxNewCharter)
                    .addComponent(wizardLblChooseCharter)
                    .addComponent(wizardCmbxCharter, 0, 975, Short.MAX_VALUE)
                    .addComponent(wizardScrollPCharterInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 975, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 975, Short.MAX_VALUE))
                .addContainerGap())
        );
        charterPanelLayout.setVerticalGroup(
            charterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(charterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wizardLblChooseCharter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wizardCmbxCharter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wizardScrollPCharterInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(wizardChckBxNewCharter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(304, Short.MAX_VALUE))
        );

        wizardtabp.addTab(resourceMap.getString("jPanelCharter.TabConstraints.tabTitle"), charterPanel); // NOI18N

        areasPanel.setName("areasPanel"); // NOI18N

        wizardtabpAreas.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        wizardtabpAreas.setToolTipText(resourceMap.getString("wizardtabpAreas.toolTipText")); // NOI18N
        wizardtabpAreas.setName("wizardtabpAreas"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        jList2.setName("jList2"); // NOI18N
        jScrollPane4.setViewportView(jList2);

        wizardtabpAreas.addTab(resourceMap.getString("jScrollPane4.TabConstraints.tabTitle"), jScrollPane4); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        jList3.setName("jList3"); // NOI18N
        jScrollPane5.setViewportView(jList3);

        wizardtabpAreas.addTab(resourceMap.getString("jScrollPane5.TabConstraints.tabTitle"), jScrollPane5); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jList1.setName("jList1"); // NOI18N
        jScrollPane2.setViewportView(jList1);

        wizardtabpAreas.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        wizardjScrollPaneAreas.setName("wizardjScrollPaneAreas"); // NOI18N

        wizardtaChoosenAreas.setColumns(20);
        wizardtaChoosenAreas.setRows(5);
        wizardtaChoosenAreas.setName("wizardtaChoosenAreas"); // NOI18N
        wizardjScrollPaneAreas.setViewportView(wizardtaChoosenAreas);

        wizardLblchooseArea.setFont(resourceMap.getFont("wizardLblchooseArea.font")); // NOI18N
        wizardLblchooseArea.setText(resourceMap.getString("wizardLblchooseArea.text")); // NOI18N
        wizardLblchooseArea.setName("wizardLblchooseArea"); // NOI18N

        wizardLblpathToCoverageini.setText(resourceMap.getString("wizardLblpathToCoverageini.text")); // NOI18N
        wizardLblpathToCoverageini.setName("wizardLblpathToCoverageini"); // NOI18N

        wizardtfCoverageini.setText(resourceMap.getString("wizardtfCoverageini.text")); // NOI18N
        wizardtfCoverageini.setName("wizardtfCoverageini"); // NOI18N

        wizardbtnRefreshAreas.setText(resourceMap.getString("wizardbtnRefreshAreas.text")); // NOI18N
        wizardbtnRefreshAreas.setName("wizardbtnRefreshAreas"); // NOI18N
        wizardbtnRefreshAreas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                wizardbtnRefreshAreasMouseClicked(evt);
            }
        });

        wizardbtnRemoveArea.setText(resourceMap.getString("wizardbtnRemoveArea.text")); // NOI18N
        wizardbtnRemoveArea.setToolTipText(resourceMap.getString("wizardbtnRemoveArea.toolTipText")); // NOI18N
        wizardbtnRemoveArea.setMaximumSize(new java.awt.Dimension(41, 23));
        wizardbtnRemoveArea.setMinimumSize(new java.awt.Dimension(41, 23));
        wizardbtnRemoveArea.setName("wizardbtnRemoveArea"); // NOI18N
        wizardbtnRemoveArea.setPreferredSize(new java.awt.Dimension(41, 23));
        wizardbtnRemoveArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                wizardbtnRemoveAreaMouseClicked(evt);
            }
        });

        wizardbtnAddAreas.setText(resourceMap.getString("wizardbtnAddAreas.text")); // NOI18N
        wizardbtnAddAreas.setToolTipText(resourceMap.getString("wizardbtnAddAreas.toolTipText")); // NOI18N
        wizardbtnAddAreas.setName("wizardbtnAddAreas"); // NOI18N

        wizardbtnSaveTodo.setText(resourceMap.getString("wizardbtnSaveTodo.text")); // NOI18N
        wizardbtnSaveTodo.setToolTipText(resourceMap.getString("wizardbtnSaveTodo.toolTipText")); // NOI18N
        wizardbtnSaveTodo.setEnabled(false);
        wizardbtnSaveTodo.setName("wizardbtnSaveTodo"); // NOI18N
        wizardbtnSaveTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wizardbtnSaveTodoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout areasPanelLayout = new javax.swing.GroupLayout(areasPanel);
        areasPanel.setLayout(areasPanelLayout);
        areasPanelLayout.setHorizontalGroup(
            areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(areasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wizardtabpAreas, javax.swing.GroupLayout.DEFAULT_SIZE, 975, Short.MAX_VALUE)
                    .addGroup(areasPanelLayout.createSequentialGroup()
                        .addComponent(wizardLblchooseArea)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 746, Short.MAX_VALUE)
                        .addComponent(wizardbtnSaveTodo))
                    .addGroup(areasPanelLayout.createSequentialGroup()
                        .addComponent(wizardLblpathToCoverageini)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wizardtfCoverageini, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wizardbtnRefreshAreas))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, areasPanelLayout.createSequentialGroup()
                        .addComponent(wizardjScrollPaneAreas, javax.swing.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(wizardbtnAddAreas)
                            .addComponent(wizardbtnRemoveArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        areasPanelLayout.setVerticalGroup(
            areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(areasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wizardLblchooseArea)
                    .addComponent(wizardbtnSaveTodo))
                .addGap(18, 18, 18)
                .addGroup(areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wizardLblpathToCoverageini)
                    .addComponent(wizardtfCoverageini, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wizardbtnRefreshAreas))
                .addGap(11, 11, 11)
                .addComponent(wizardtabpAreas, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(areasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, areasPanelLayout.createSequentialGroup()
                        .addComponent(wizardbtnAddAreas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(wizardbtnRemoveArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(wizardjScrollPaneAreas, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))
                .addContainerGap())
        );

        wizardtabpAreas.getAccessibleContext().setAccessibleParent(welcomePanel);

        wizardtabp.addTab(resourceMap.getString("areasPanel.TabConstraints.tabTitle"), areasPanel); // NOI18N

        testsessionPanel.setFocusable(false);
        testsessionPanel.setName("testsessionPanel"); // NOI18N

        wizardLblstartTime.setText(resourceMap.getString("wizardLblstartTime.text")); // NOI18N
        wizardLblstartTime.setName("wizardLblstartTime"); // NOI18N

        wizardPanelTaskBreakd.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("wizardPanelTaskBreakd.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("wizardPanelTaskBreakd.border.titleFont"))); // NOI18N
        wizardPanelTaskBreakd.setName("wizardPanelTaskBreakd"); // NOI18N

        wizardLblDuration.setText(resourceMap.getString("wizardLblDuration.text")); // NOI18N
        wizardLblDuration.setName("wizardLblDuration"); // NOI18N

        wizardLblDesignExecution.setText(resourceMap.getString("wizardLblDesignExecution.text")); // NOI18N
        wizardLblDesignExecution.setName("wizardLblDesignExecution"); // NOI18N

        wizardLblSetup.setText(resourceMap.getString("wizardLblSetup.text")); // NOI18N
        wizardLblSetup.setName("wizardLblSetup"); // NOI18N

        wizardLblChartervs.setText(resourceMap.getString("wizardLblChartervs.text")); // NOI18N
        wizardLblChartervs.setName("wizardLblChartervs"); // NOI18N

        wizardtfDuration.setEditable(false);
        wizardtfDuration.setText(resourceMap.getString("wizardtfDuration.text")); // NOI18N
        wizardtfDuration.setName("wizardtfDuration"); // NOI18N

        wizardtfSessionSetup.setText(resourceMap.getString("wizardtfSessionSetup.text")); // NOI18N
        wizardtfSessionSetup.setName("wizardtfSessionSetup"); // NOI18N

        wizardLblBugReporting.setText(resourceMap.getString("wizardLblBugReporting.text")); // NOI18N
        wizardLblBugReporting.setName("wizardLblBugReporting"); // NOI18N

        wizardtfBugInvestigation.setText(resourceMap.getString("wizardtfBugInvestigation.text")); // NOI18N
        wizardtfBugInvestigation.setName("wizardtfBugInvestigation"); // NOI18N

        wizardtfCharter.setText(resourceMap.getString("wizardtfCharter.text")); // NOI18N
        wizardtfCharter.setName("wizardtfCharter"); // NOI18N

        wizardLblSlash.setText(resourceMap.getString("wizardLblSlash.text")); // NOI18N
        wizardLblSlash.setName("wizardLblSlash"); // NOI18N

        wizardtfOpportunity.setText(resourceMap.getString("wizardtfOpportunity.text")); // NOI18N
        wizardtfOpportunity.setName("wizardtfOpportunity"); // NOI18N

        wizardLblStopfield.setFont(resourceMap.getFont("wizardLblStopfield.font")); // NOI18N
        wizardLblStopfield.setForeground(resourceMap.getColor("wizardLblStopfield.foreground")); // NOI18N
        wizardLblStopfield.setText(resourceMap.getString("wizardLblStopfield.text")); // NOI18N
        wizardLblStopfield.setName("wizardLblStopfield"); // NOI18N

        wizardtfTestDesignExecution.setText(resourceMap.getString("wizardtfTestDesignExecution.text")); // NOI18N
        wizardtfTestDesignExecution.setName("wizardtfTestDesignExecution"); // NOI18N

        wizardLblvs.setText(resourceMap.getString("wizardLblvs.text")); // NOI18N
        wizardLblvs.setName("wizardLblvs"); // NOI18N

        wizardLblOpportunity.setText(resourceMap.getString("wizardLblOpportunity.text")); // NOI18N
        wizardLblOpportunity.setName("wizardLblOpportunity"); // NOI18N

        javax.swing.GroupLayout wizardPanelTaskBreakdLayout = new javax.swing.GroupLayout(wizardPanelTaskBreakd);
        wizardPanelTaskBreakd.setLayout(wizardPanelTaskBreakdLayout);
        wizardPanelTaskBreakdLayout.setHorizontalGroup(
            wizardPanelTaskBreakdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wizardPanelTaskBreakdLayout.createSequentialGroup()
                .addGroup(wizardPanelTaskBreakdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(wizardPanelTaskBreakdLayout.createSequentialGroup()
                        .addComponent(wizardLblChartervs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wizardLblvs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wizardLblOpportunity))
                    .addComponent(wizardtfBugInvestigation, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(wizardPanelTaskBreakdLayout.createSequentialGroup()
                        .addComponent(wizardtfCharter, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wizardLblSlash)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wizardtfOpportunity, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(wizardPanelTaskBreakdLayout.createSequentialGroup()
                        .addComponent(wizardLblDuration)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wizardLblStopfield))
                    .addComponent(wizardtfDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wizardLblDesignExecution)
                    .addComponent(wizardLblSetup)
                    .addComponent(wizardtfSessionSetup, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wizardLblBugReporting)
                    .addComponent(wizardtfTestDesignExecution, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(172, Short.MAX_VALUE))
        );
        wizardPanelTaskBreakdLayout.setVerticalGroup(
            wizardPanelTaskBreakdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wizardPanelTaskBreakdLayout.createSequentialGroup()
                .addGroup(wizardPanelTaskBreakdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wizardLblDuration)
                    .addComponent(wizardLblStopfield))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wizardtfDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(wizardLblDesignExecution)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wizardtfTestDesignExecution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wizardLblSetup)
                .addGap(9, 9, 9)
                .addComponent(wizardtfSessionSetup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wizardLblBugReporting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wizardtfBugInvestigation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(wizardPanelTaskBreakdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wizardLblChartervs)
                    .addComponent(wizardLblvs)
                    .addComponent(wizardLblOpportunity))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(wizardPanelTaskBreakdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wizardtfCharter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wizardLblSlash)
                    .addComponent(wizardtfOpportunity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        wizardPanelTestsessionAreas.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("wizardPanelTestsessionAreas.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("wizardPanelTestsessionAreas.border.titleFont"))); // NOI18N
        wizardPanelTestsessionAreas.setName("wizardPanelTestsessionAreas"); // NOI18N

        wizardScrollPaneAreas.setName("wizardScrollPaneAreas"); // NOI18N

        wizardtaTestsessionAreas.setColumns(20);
        wizardtaTestsessionAreas.setRows(5);
        wizardtaTestsessionAreas.setName("wizardtaTestsessionAreas"); // NOI18N
        wizardScrollPaneAreas.setViewportView(wizardtaTestsessionAreas);

        javax.swing.GroupLayout wizardPanelTestsessionAreasLayout = new javax.swing.GroupLayout(wizardPanelTestsessionAreas);
        wizardPanelTestsessionAreas.setLayout(wizardPanelTestsessionAreasLayout);
        wizardPanelTestsessionAreasLayout.setHorizontalGroup(
            wizardPanelTestsessionAreasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wizardPanelTestsessionAreasLayout.createSequentialGroup()
                .addComponent(wizardScrollPaneAreas, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addContainerGap())
        );
        wizardPanelTestsessionAreasLayout.setVerticalGroup(
            wizardPanelTestsessionAreasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wizardPanelTestsessionAreasLayout.createSequentialGroup()
                .addComponent(wizardScrollPaneAreas, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addContainerGap())
        );

        wizardLblcharterHeadline.setFont(resourceMap.getFont("wizardLblcharterHeadline.font")); // NOI18N
        wizardLblcharterHeadline.setText(resourceMap.getString("wizardLblcharterHeadline.text")); // NOI18N
        wizardLblcharterHeadline.setName("wizardLblcharterHeadline"); // NOI18N

        wizardLblStart.setFont(resourceMap.getFont("wizardLblStart.font")); // NOI18N
        wizardLblStart.setText(resourceMap.getString("wizardLblStart.text")); // NOI18N
        wizardLblStart.setName("wizardLblStart"); // NOI18N

        wizardLblTester.setFont(resourceMap.getFont("wizardLblTester.font")); // NOI18N
        wizardLblTester.setText(resourceMap.getString("wizardLblTester.text")); // NOI18N
        wizardLblTester.setName("wizardLblTester"); // NOI18N

        wizardTabpDetailsDTBIR.setName("wizardTabpDetailsDTBIR"); // NOI18N

        wizardScrollPaneDataFiles.setName("wizardScrollPaneDataFiles"); // NOI18N

        wizardtaDataFiles.setColumns(20);
        wizardtaDataFiles.setLineWrap(true);
        wizardtaDataFiles.setRows(5);
        wizardtaDataFiles.setName("wizardtaDataFiles"); // NOI18N
        wizardScrollPaneDataFiles.setViewportView(wizardtaDataFiles);

        wizardTabpDetailsDTBIR.addTab(resourceMap.getString("wizardScrollPaneDataFiles.TabConstraints.tabTitle"), wizardScrollPaneDataFiles); // NOI18N

        wizardScrollPaneTestNotes.setName("wizardScrollPaneTestNotes"); // NOI18N

        wizardtaTestNotes.setColumns(20);
        wizardtaTestNotes.setFont(resourceMap.getFont("wizardtaTestNotes.font")); // NOI18N
        wizardtaTestNotes.setLineWrap(true);
        wizardtaTestNotes.setRows(5);
        wizardtaTestNotes.setWrapStyleWord(true);
        wizardtaTestNotes.setName("wizardtaTestNotes"); // NOI18N
        wizardScrollPaneTestNotes.setViewportView(wizardtaTestNotes);

        wizardTabpDetailsDTBIR.addTab(resourceMap.getString("wizardScrollPaneTestNotes.TabConstraints.tabTitle"), wizardScrollPaneTestNotes); // NOI18N

        wizardScrollPaneBugs.setName("wizardScrollPaneBugs"); // NOI18N

        wizardtaBugs.setColumns(20);
        wizardtaBugs.setFont(resourceMap.getFont("wizardtaBugs.font")); // NOI18N
        wizardtaBugs.setLineWrap(true);
        wizardtaBugs.setRows(5);
        wizardtaBugs.setWrapStyleWord(true);
        wizardtaBugs.setComponentPopupMenu(wizardPopUpBugTab);
        wizardtaBugs.setName("wizardtaBugs"); // NOI18N
        wizardScrollPaneBugs.setViewportView(wizardtaBugs);

        wizardTabpDetailsDTBIR.addTab(resourceMap.getString("wizardScrollPaneBugs.TabConstraints.tabTitle"), wizardScrollPaneBugs); // NOI18N

        wizardScrollPaneIssues.setName("wizardScrollPaneIssues"); // NOI18N

        wizardtaIssues.setColumns(20);
        wizardtaIssues.setFont(resourceMap.getFont("wizardtaIssues.font")); // NOI18N
        wizardtaIssues.setLineWrap(true);
        wizardtaIssues.setRows(5);
        wizardtaIssues.setWrapStyleWord(true);
        wizardtaIssues.setName("wizardtaIssues"); // NOI18N
        wizardScrollPaneIssues.setViewportView(wizardtaIssues);

        wizardTabpDetailsDTBIR.addTab(resourceMap.getString("wizardScrollPaneIssues.TabConstraints.tabTitle"), wizardScrollPaneIssues); // NOI18N

        wizardScrollPaneReview.setName("wizardScrollPaneReview"); // NOI18N

        wizardtaReview.setBackground(resourceMap.getColor("wizardtaReview.background")); // NOI18N
        wizardtaReview.setColumns(20);
        wizardtaReview.setEditable(false);
        wizardtaReview.setRows(5);
        wizardtaReview.setToolTipText(resourceMap.getString("wizardtaReview.toolTipText")); // NOI18N
        wizardtaReview.setName("wizardtaReview"); // NOI18N
        wizardScrollPaneReview.setViewportView(wizardtaReview);

        wizardTabpDetailsDTBIR.addTab(resourceMap.getString("wizardScrollPaneReview.TabConstraints.tabTitle"), wizardScrollPaneReview); // NOI18N

        wizardtfNameOfTester.setText(resourceMap.getString("wizardtfNameOfTester.text")); // NOI18N
        wizardtfNameOfTester.setName("wizardtfNameOfTester"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        wizardtaCharterdynamic.setBackground(resourceMap.getColor("wizardtaCharterdynamic.background")); // NOI18N
        wizardtaCharterdynamic.setColumns(20);
        wizardtaCharterdynamic.setFont(resourceMap.getFont("wizardtaCharterdynamic.font")); // NOI18N
        wizardtaCharterdynamic.setLineWrap(true);
        wizardtaCharterdynamic.setRows(2);
        wizardtaCharterdynamic.setBorder(null);
        wizardtaCharterdynamic.setName("wizardtaCharterdynamic"); // NOI18N
        jScrollPane3.setViewportView(wizardtaCharterdynamic);

        javax.swing.GroupLayout testsessionPanelLayout = new javax.swing.GroupLayout(testsessionPanel);
        testsessionPanel.setLayout(testsessionPanelLayout);
        testsessionPanelLayout.setHorizontalGroup(
            testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testsessionPanelLayout.createSequentialGroup()
                .addGroup(testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(testsessionPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(wizardLblcharterHeadline)
                            .addComponent(wizardLblStart)
                            .addComponent(wizardLblTester))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(testsessionPanelLayout.createSequentialGroup()
                                .addGroup(testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(wizardLblstartTime)
                                    .addComponent(wizardtfNameOfTester, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(wizardTabpDetailsDTBIR, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 919, Short.MAX_VALUE)))
                    .addGroup(testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(wizardPanelTaskBreakd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(wizardPanelTestsessionAreas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        testsessionPanelLayout.setVerticalGroup(
            testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testsessionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wizardLblcharterHeadline)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(testsessionPanelLayout.createSequentialGroup()
                        .addComponent(wizardPanelTestsessionAreas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(11, 11, 11)
                        .addGroup(testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(wizardLblStart)
                            .addComponent(wizardLblstartTime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(testsessionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(wizardLblTester)
                            .addComponent(wizardtfNameOfTester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addComponent(wizardPanelTaskBreakd, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(testsessionPanelLayout.createSequentialGroup()
                        .addComponent(wizardTabpDetailsDTBIR, javax.swing.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
                        .addGap(11, 11, 11))))
        );

        wizardtabp.addTab(resourceMap.getString("testsessionPanel.TabConstraints.tabTitle"), testsessionPanel); // NOI18N

        wizardbtnBack.setText(resourceMap.getString("wizardbtnBack.text")); // NOI18N
        wizardbtnBack.setEnabled(false);
        wizardbtnBack.setName("wizardbtnBack"); // NOI18N
        wizardbtnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWizardBack(evt);
            }
        });

        wizardbtnStart.setText(resourceMap.getString("wizardbtnStart.text")); // NOI18N
        wizardbtnStart.setName("wizardbtnStart"); // NOI18N

        wizardbtnStop.setText(resourceMap.getString("wizardbtnStop.text")); // NOI18N
        wizardbtnStop.setName("wizardbtnStop"); // NOI18N

        wizardbtnSave.setEnabled(false);
        wizardbtnSave.setLabel(resourceMap.getString("SaveButton.label")); // NOI18N
        wizardbtnSave.setName("SaveButton"); // NOI18N
        wizardbtnSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnWizardSaveClicked(evt);
            }
        });

        wizardbtnNext.setText(resourceMap.getString("wizardbtnNext.text")); // NOI18N
        wizardbtnNext.setName("wizardbtnNext"); // NOI18N
        wizardbtnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWizardNext(evt);
            }
        });

        wizardbtnNew.setText(resourceMap.getString("wizardbtnNew.text")); // NOI18N
        wizardbtnNew.setName("wizardbtnNew"); // NOI18N
        wizardbtnNew.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnWizardNewMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout wizardPanelLayout = new javax.swing.GroupLayout(wizardPanel);
        wizardPanel.setLayout(wizardPanelLayout);
        wizardPanelLayout.setHorizontalGroup(
            wizardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, wizardPanelLayout.createSequentialGroup()
                .addGroup(wizardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, wizardPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(wizardbtnBack)
                        .addGap(340, 340, 340)
                        .addComponent(wizardbtnStart)
                        .addGap(2, 2, 2)
                        .addComponent(wizardbtnStop)
                        .addGap(18, 18, 18)
                        .addComponent(wizardbtnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wizardbtnNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 280, Short.MAX_VALUE)
                        .addComponent(wizardbtnNext))
                    .addComponent(WizardToolbar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, wizardPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(wizardtabp, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)))
                .addContainerGap())
        );
        wizardPanelLayout.setVerticalGroup(
            wizardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wizardPanelLayout.createSequentialGroup()
                .addComponent(WizardToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wizardtabp, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(wizardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wizardbtnBack)
                    .addComponent(wizardbtnStart)
                    .addComponent(wizardbtnSave)
                    .addComponent(wizardbtnNew)
                    .addComponent(wizardbtnNext)
                    .addComponent(wizardbtnStop))
                .addContainerGap())
        );

        viewReviewsPanel.setName("viewReviewsPanel"); // NOI18N

        ReviewToolbar.setFloatable(false);
        ReviewToolbar.setRollover(true);
        ReviewToolbar.setName("ReviewToolbar"); // NOI18N

        reviewbtntopSave.setIcon(resourceMap.getIcon("reviewbtntopSave.icon")); // NOI18N
        reviewbtntopSave.setToolTipText(resourceMap.getString("reviewbtntopSave.toolTipText")); // NOI18N
        reviewbtntopSave.setDisabledIcon(resourceMap.getIcon("reviewbtntopSave.disabledIcon")); // NOI18N
        reviewbtntopSave.setEnabled(false);
        reviewbtntopSave.setFocusable(false);
        reviewbtntopSave.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        reviewbtntopSave.setName("reviewbtntopSave"); // NOI18N
        reviewbtntopSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        reviewbtntopSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reviewbtntopSaveMouseClicked(evt);
            }
        });
        ReviewToolbar.add(reviewbtntopSave);

        jSeparator3.setName("jSeparator3"); // NOI18N
        ReviewToolbar.add(jSeparator3);

        reviewbtntopBack.setIcon(resourceMap.getIcon("reviewbtntopBack.icon")); // NOI18N
        reviewbtntopBack.setToolTipText(resourceMap.getString("reviewbtntopBack.toolTipText")); // NOI18N
        reviewbtntopBack.setDisabledIcon(resourceMap.getIcon("reviewbtntopBack.disabledIcon")); // NOI18N
        reviewbtntopBack.setEnabled(false);
        reviewbtntopBack.setFocusable(false);
        reviewbtntopBack.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reviewbtntopBack.setName("reviewbtntopBack"); // NOI18N
        reviewbtntopBack.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        reviewbtntopBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reviewbtntopBackMouseClicked(evt);
            }
        });
        ReviewToolbar.add(reviewbtntopBack);

        reviewbtntopNext.setIcon(resourceMap.getIcon("reviewbtntopNext.icon")); // NOI18N
        reviewbtntopNext.setToolTipText(resourceMap.getString("reviewbtntopNext.toolTipText")); // NOI18N
        reviewbtntopNext.setDisabledIcon(resourceMap.getIcon("reviewbtntopNext.disabledIcon")); // NOI18N
        reviewbtntopNext.setFocusable(false);
        reviewbtntopNext.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reviewbtntopNext.setName("reviewbtntopNext"); // NOI18N
        reviewbtntopNext.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        reviewbtntopNext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reviewbtntopNextMouseClicked(evt);
            }
        });
        ReviewToolbar.add(reviewbtntopNext);

        reviewbtntopMove.setIcon(resourceMap.getIcon("reviewbtntopMove.icon")); // NOI18N
        reviewbtntopMove.setText(resourceMap.getString("reviewbtntopMove.text")); // NOI18N
        reviewbtntopMove.setToolTipText(resourceMap.getString("reviewbtntopMove.toolTipText")); // NOI18N
        reviewbtntopMove.setDisabledIcon(resourceMap.getIcon("reviewbtntopMove.disabledIcon")); // NOI18N
        reviewbtntopMove.setFocusable(false);
        reviewbtntopMove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reviewbtntopMove.setName("reviewbtntopMove"); // NOI18N
        reviewbtntopMove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        reviewbtntopMove.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                moveto(evt);
            }
        });
        ReviewToolbar.add(reviewbtntopMove);

        reviewViewlabel.setFont(resourceMap.getFont("reviewViewlabel.font")); // NOI18N
        reviewViewlabel.setText(resourceMap.getString("reviewViewlabel.text")); // NOI18N
        reviewViewlabel.setName("reviewViewlabel"); // NOI18N

        reviewSessionsTabp.setName("reviewSessionsTabp"); // NOI18N

        reviewToreviewSessionsPanel.setName("reviewToreviewSessionsPanel"); // NOI18N

        reviewCmbxSessiontoReview.setName("reviewCmbxSessiontoReview"); // NOI18N

        reviewtoReviewPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        reviewtoReviewPanel.setName("reviewtoReviewPanel"); // NOI18N
        reviewtoReviewPanel.setLayout(new java.awt.GridLayout(2, 0));

        javax.swing.GroupLayout reviewToreviewSessionsPanelLayout = new javax.swing.GroupLayout(reviewToreviewSessionsPanel);
        reviewToreviewSessionsPanel.setLayout(reviewToreviewSessionsPanelLayout);
        reviewToreviewSessionsPanelLayout.setHorizontalGroup(
            reviewToreviewSessionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewToreviewSessionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(reviewToreviewSessionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reviewtoReviewPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 975, Short.MAX_VALUE)
                    .addComponent(reviewCmbxSessiontoReview, 0, 975, Short.MAX_VALUE))
                .addContainerGap())
        );
        reviewToreviewSessionsPanelLayout.setVerticalGroup(
            reviewToreviewSessionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewToreviewSessionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reviewtoReviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(reviewCmbxSessiontoReview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(520, Short.MAX_VALUE))
        );

        reviewSessionsTabp.addTab(resourceMap.getString("reviewToreviewSessionsPanel.TabConstraints.tabTitle"), reviewToreviewSessionsPanel); // NOI18N

        reviewViewPanel.setName("reviewViewPanel"); // NOI18N

        reviewLblStartTime.setText(resourceMap.getString("reviewLblStartTime.text")); // NOI18N
        reviewLblStartTime.setName("reviewLblStartTime"); // NOI18N

        reviewPaneTaskbreakdown.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("reviewPaneTaskbreakdown.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("reviewPaneTaskbreakdown.border.titleFont"))); // NOI18N
        reviewPaneTaskbreakdown.setName("reviewPaneTaskbreakdown"); // NOI18N

        reviewLblDuration.setText(resourceMap.getString("reviewLblDuration.text")); // NOI18N
        reviewLblDuration.setName("reviewLblDuration"); // NOI18N

        reviewLblTestExecution.setText(resourceMap.getString("reviewLblTestExecution.text")); // NOI18N
        reviewLblTestExecution.setName("reviewLblTestExecution"); // NOI18N

        reviewLblSetup.setText(resourceMap.getString("reviewLblSetup.text")); // NOI18N
        reviewLblSetup.setName("reviewLblSetup"); // NOI18N

        reviewLblChartervs.setText(resourceMap.getString("reviewLblChartervs.text")); // NOI18N
        reviewLblChartervs.setName("reviewLblChartervs"); // NOI18N

        reviewtfDuration.setBackground(resourceMap.getColor("reviewtfDuration.background")); // NOI18N
        reviewtfDuration.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        reviewtfDuration.setName("reviewtfDuration"); // NOI18N

        reviewtfSessionSetup.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        reviewtfSessionSetup.setName("reviewtfSessionSetup"); // NOI18N

        reviewLblBugReporting.setText(resourceMap.getString("reviewLblBugReporting.text")); // NOI18N
        reviewLblBugReporting.setName("reviewLblBugReporting"); // NOI18N

        reviewtfBugInvestigation.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        reviewtfBugInvestigation.setName("reviewtfBugInvestigation"); // NOI18N

        reviewtfCharter.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        reviewtfCharter.setName("reviewtfCharter"); // NOI18N

        reviewtfTestDesignExecution.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        reviewtfTestDesignExecution.setName("reviewtfTestDesignExecution"); // NOI18N

        reviewLblvs.setText(resourceMap.getString("reviewLblvs.text")); // NOI18N
        reviewLblvs.setName("reviewLblvs"); // NOI18N

        reviewLblOpportunity.setText(resourceMap.getString("reviewLblOpportunity.text")); // NOI18N
        reviewLblOpportunity.setName("reviewLblOpportunity"); // NOI18N

        javax.swing.GroupLayout reviewPaneTaskbreakdownLayout = new javax.swing.GroupLayout(reviewPaneTaskbreakdown);
        reviewPaneTaskbreakdown.setLayout(reviewPaneTaskbreakdownLayout);
        reviewPaneTaskbreakdownLayout.setHorizontalGroup(
            reviewPaneTaskbreakdownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewPaneTaskbreakdownLayout.createSequentialGroup()
                .addGroup(reviewPaneTaskbreakdownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reviewPaneTaskbreakdownLayout.createSequentialGroup()
                        .addComponent(reviewLblChartervs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reviewLblvs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reviewLblOpportunity))
                    .addComponent(reviewtfBugInvestigation, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reviewLblDuration)
                    .addComponent(reviewtfDuration, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reviewLblTestExecution)
                    .addComponent(reviewLblSetup)
                    .addComponent(reviewtfSessionSetup, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reviewLblBugReporting)
                    .addComponent(reviewtfTestDesignExecution, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reviewtfCharter, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(172, Short.MAX_VALUE))
        );
        reviewPaneTaskbreakdownLayout.setVerticalGroup(
            reviewPaneTaskbreakdownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewPaneTaskbreakdownLayout.createSequentialGroup()
                .addComponent(reviewLblDuration)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reviewtfDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(reviewLblTestExecution)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reviewtfTestDesignExecution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reviewLblSetup)
                .addGap(9, 9, 9)
                .addComponent(reviewtfSessionSetup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reviewLblBugReporting)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reviewtfBugInvestigation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(reviewPaneTaskbreakdownLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reviewLblChartervs)
                    .addComponent(reviewLblvs)
                    .addComponent(reviewLblOpportunity))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reviewtfCharter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        reviewPanelTestsessionAreas.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), resourceMap.getString("reviewPanelTestsessionAreas.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, resourceMap.getFont("reviewPanelTestsessionAreas.border.titleFont"))); // NOI18N
        reviewPanelTestsessionAreas.setName("reviewPanelTestsessionAreas"); // NOI18N

        reviewScrollPaneArea.setName("reviewScrollPaneArea"); // NOI18N

        reviewtaTestsessionAreasReview.setColumns(20);
        reviewtaTestsessionAreasReview.setRows(5);
        reviewtaTestsessionAreasReview.setName("reviewtaTestsessionAreasReview"); // NOI18N
        reviewScrollPaneArea.setViewportView(reviewtaTestsessionAreasReview);

        javax.swing.GroupLayout reviewPanelTestsessionAreasLayout = new javax.swing.GroupLayout(reviewPanelTestsessionAreas);
        reviewPanelTestsessionAreas.setLayout(reviewPanelTestsessionAreasLayout);
        reviewPanelTestsessionAreasLayout.setHorizontalGroup(
            reviewPanelTestsessionAreasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewPanelTestsessionAreasLayout.createSequentialGroup()
                .addComponent(reviewScrollPaneArea, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addContainerGap())
        );
        reviewPanelTestsessionAreasLayout.setVerticalGroup(
            reviewPanelTestsessionAreasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewPanelTestsessionAreasLayout.createSequentialGroup()
                .addComponent(reviewScrollPaneArea, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addContainerGap())
        );

        reviewCharterHeadlineLabel.setFont(resourceMap.getFont("reviewCharterHeadlineLabel.font")); // NOI18N
        reviewCharterHeadlineLabel.setText(resourceMap.getString("reviewCharterHeadlineLabel.text")); // NOI18N
        reviewCharterHeadlineLabel.setName("reviewCharterHeadlineLabel"); // NOI18N

        reviewLblStart.setFont(resourceMap.getFont("reviewLblStart.font")); // NOI18N
        reviewLblStart.setText(resourceMap.getString("reviewLblStart.text")); // NOI18N
        reviewLblStart.setName("reviewLblStart"); // NOI18N

        reviewLblTester.setFont(resourceMap.getFont("reviewLblTester.font")); // NOI18N
        reviewLblTester.setText(resourceMap.getString("reviewLblTester.text")); // NOI18N
        reviewLblTester.setName("reviewLblTester"); // NOI18N

        reviewTabpDTBIR.setName("reviewTabpDTBIR"); // NOI18N

        reviewScrollPaneDatafile.setName("reviewScrollPaneDatafile"); // NOI18N

        reviewtaDataFiles.setColumns(20);
        reviewtaDataFiles.setFont(resourceMap.getFont("reviewtaDataFiles.font")); // NOI18N
        reviewtaDataFiles.setRows(5);
        reviewtaDataFiles.setName("reviewtaDataFiles"); // NOI18N
        reviewScrollPaneDatafile.setViewportView(reviewtaDataFiles);

        reviewTabpDTBIR.addTab(resourceMap.getString("reviewScrollPaneDatafile.TabConstraints.tabTitle"), reviewScrollPaneDatafile); // NOI18N

        reviewScrollPaneTestNotes.setName("reviewScrollPaneTestNotes"); // NOI18N

        reviewtaTestNotes.setColumns(20);
        reviewtaTestNotes.setFont(resourceMap.getFont("reviewtaTestNotes.font")); // NOI18N
        reviewtaTestNotes.setLineWrap(true);
        reviewtaTestNotes.setRows(5);
        reviewtaTestNotes.setName("reviewtaTestNotes"); // NOI18N
        reviewScrollPaneTestNotes.setViewportView(reviewtaTestNotes);

        reviewTabpDTBIR.addTab(resourceMap.getString("reviewScrollPaneTestNotes.TabConstraints.tabTitle"), reviewScrollPaneTestNotes); // NOI18N

        reviewScrollPaneBugs.setName("reviewScrollPaneBugs"); // NOI18N

        reviewedipaneBugs.setFont(resourceMap.getFont("reviewedipaneBugs.font")); // NOI18N
        reviewedipaneBugs.setName("reviewedipaneBugs"); // NOI18N
        reviewScrollPaneBugs.setViewportView(reviewedipaneBugs);

        reviewTabpDTBIR.addTab(resourceMap.getString("reviewScrollPaneBugs.TabConstraints.tabTitle"), reviewScrollPaneBugs); // NOI18N

        reviewScrollPaneIssues.setName("reviewScrollPaneIssues"); // NOI18N

        reviewtaIssues.setColumns(20);
        reviewtaIssues.setFont(resourceMap.getFont("reviewtaIssues.font")); // NOI18N
        reviewtaIssues.setRows(5);
        reviewtaIssues.setName("reviewtaIssues"); // NOI18N
        reviewScrollPaneIssues.setViewportView(reviewtaIssues);

        reviewTabpDTBIR.addTab(resourceMap.getString("reviewScrollPaneIssues.TabConstraints.tabTitle"), reviewScrollPaneIssues); // NOI18N

        reviewScrollPaneReview.setName("reviewScrollPaneReview"); // NOI18N

        reviewtaReview.setColumns(20);
        reviewtaReview.setFont(resourceMap.getFont("reviewtaReview.font")); // NOI18N
        reviewtaReview.setRows(5);
        reviewtaReview.setName("reviewtaReview"); // NOI18N
        reviewScrollPaneReview.setViewportView(reviewtaReview);

        reviewTabpDTBIR.addTab(resourceMap.getString("reviewScrollPaneReview.TabConstraints.tabTitle"), reviewScrollPaneReview); // NOI18N

        reviewTabpDTBIR.setSelectedIndex(4);

        reviewtfNameOfTester.setText(resourceMap.getString("reviewtfNameOfTester.text")); // NOI18N
        reviewtfNameOfTester.setName("reviewtfNameOfTester"); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        reviewtaCharterdynamic.setBackground(resourceMap.getColor("reviewtaCharterdynamic.background")); // NOI18N
        reviewtaCharterdynamic.setColumns(20);
        reviewtaCharterdynamic.setEditable(false);
        reviewtaCharterdynamic.setFont(resourceMap.getFont("reviewtaCharterdynamic.font")); // NOI18N
        reviewtaCharterdynamic.setLineWrap(true);
        reviewtaCharterdynamic.setRows(2);
        reviewtaCharterdynamic.setText(resourceMap.getString("reviewtaCharterdynamic.text")); // NOI18N
        reviewtaCharterdynamic.setBorder(null);
        reviewtaCharterdynamic.setName("reviewtaCharterdynamic"); // NOI18N
        jScrollPane6.setViewportView(reviewtaCharterdynamic);

        javax.swing.GroupLayout reviewViewPanelLayout = new javax.swing.GroupLayout(reviewViewPanel);
        reviewViewPanel.setLayout(reviewViewPanelLayout);
        reviewViewPanelLayout.setHorizontalGroup(
            reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewViewPanelLayout.createSequentialGroup()
                .addGroup(reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reviewViewPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reviewLblStart)
                            .addComponent(reviewLblTester)
                            .addComponent(reviewCharterHeadlineLabel))
                        .addGap(10, 10, 10)
                        .addGroup(reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(reviewViewPanelLayout.createSequentialGroup()
                                .addGroup(reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(reviewLblStartTime)
                                    .addComponent(reviewtfNameOfTester, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(reviewTabpDTBIR))
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 919, Short.MAX_VALUE)))
                    .addGroup(reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(reviewPaneTaskbreakdown, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(reviewPanelTestsessionAreas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        reviewViewPanelLayout.setVerticalGroup(
            reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewViewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reviewCharterHeadlineLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reviewViewPanelLayout.createSequentialGroup()
                        .addComponent(reviewPanelTestsessionAreas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(11, 11, 11)
                        .addGroup(reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(reviewLblStart)
                            .addComponent(reviewLblStartTime))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(reviewViewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(reviewLblTester)
                            .addComponent(reviewtfNameOfTester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addComponent(reviewPaneTaskbreakdown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(reviewViewPanelLayout.createSequentialGroup()
                        .addComponent(reviewTabpDTBIR, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                        .addGap(11, 11, 11))))
        );

        reviewSessionsTabp.addTab(resourceMap.getString("reviewViewPanel.TabConstraints.tabTitle"), reviewViewPanel); // NOI18N

        reviewbtnNext.setText(resourceMap.getString("reviewbtnNext.text")); // NOI18N
        reviewbtnNext.setName("reviewbtnNext"); // NOI18N
        reviewbtnNext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reviewbtnNextClicked(evt);
            }
        });

        reviewbtnBack.setText(resourceMap.getString("reviewbtnBack.text")); // NOI18N
        reviewbtnBack.setEnabled(false);
        reviewbtnBack.setName("reviewbtnBack"); // NOI18N
        reviewbtnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reviewbtnBackMouseClicked(evt);
            }
        });

        reviewbtnSave.setEnabled(false);
        reviewbtnSave.setLabel(resourceMap.getString("reviewbtnSave.label")); // NOI18N
        reviewbtnSave.setName("reviewbtnSave"); // NOI18N
        reviewbtnSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reviewbtnSaveButtonClicked(evt);
            }
        });

        reviewbtnMove.setText(resourceMap.getString("reviewbtnMove.text")); // NOI18N
        reviewbtnMove.setToolTipText(resourceMap.getString("reviewbtnMove.toolTipText")); // NOI18N
        reviewbtnMove.setEnabled(false);
        reviewbtnMove.setName("reviewbtnMove"); // NOI18N
        reviewbtnMove.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                moveto(evt);
            }
        });

        javax.swing.GroupLayout viewReviewsPanelLayout = new javax.swing.GroupLayout(viewReviewsPanel);
        viewReviewsPanel.setLayout(viewReviewsPanelLayout);
        viewReviewsPanelLayout.setHorizontalGroup(
            viewReviewsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewReviewsPanelLayout.createSequentialGroup()
                .addGroup(viewReviewsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ReviewToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(viewReviewsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(reviewbtnBack)
                        .addGap(466, 466, 466)
                        .addComponent(reviewbtnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 206, Short.MAX_VALUE)
                        .addComponent(reviewbtnMove)
                        .addGap(18, 18, 18)
                        .addComponent(reviewbtnNext))
                    .addGroup(viewReviewsPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(reviewSessionsTabp))
                    .addGroup(viewReviewsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(reviewViewlabel)))
                .addContainerGap())
        );
        viewReviewsPanelLayout.setVerticalGroup(
            viewReviewsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(viewReviewsPanelLayout.createSequentialGroup()
                .addComponent(ReviewToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(reviewViewlabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reviewSessionsTabp, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
                .addGap(25, 25, 25)
                .addGroup(viewReviewsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reviewbtnBack)
                    .addComponent(reviewbtnNext)
                    .addComponent(reviewbtnSave)
                    .addComponent(reviewbtnMove))
                .addContainerGap())
        );

        reportPanel.setName("reportPanel"); // NOI18N

        reportChartPanel.setName("reportChartPanel"); // NOI18N

        javax.swing.GroupLayout reportChartPanelLayout = new javax.swing.GroupLayout(reportChartPanel);
        reportChartPanel.setLayout(reportChartPanelLayout);
        reportChartPanelLayout.setHorizontalGroup(
            reportChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 495, Short.MAX_VALUE)
        );
        reportChartPanelLayout.setVerticalGroup(
            reportChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 436, Short.MAX_VALUE)
        );

        reportScrollPOverviewTabel.setName("reportScrollPOverviewTabel"); // NOI18N

        reportOverviewTable.setBackground(resourceMap.getColor("reportOverviewTable.background")); // NOI18N
        reportOverviewTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Charter", "Number of Testsessions"
            }
        ));
        reportOverviewTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        reportOverviewTable.setName("reportOverviewTable"); // NOI18N
        reportScrollPOverviewTabel.setViewportView(reportOverviewTable);
        reportOverviewTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("reportOverviewTable.columnModel.title0")); // NOI18N
        reportOverviewTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("reportOverviewTable.columnModel.title1")); // NOI18N

        reportlblSum.setText(resourceMap.getString("reportlblSum.text")); // NOI18N
        reportlblSum.setName("reportlblSum"); // NOI18N

        reportlblBug.setText(resourceMap.getString("reportlblBug.text")); // NOI18N
        reportlblBug.setName("reportlblBug"); // NOI18N

        reportlblIssue.setText(resourceMap.getString("reportlblIssue.text")); // NOI18N
        reportlblIssue.setName("reportlblIssue"); // NOI18N

        javax.swing.GroupLayout reportPanelLayout = new javax.swing.GroupLayout(reportPanel);
        reportPanel.setLayout(reportPanelLayout);
        reportPanelLayout.setHorizontalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reportScrollPOverviewTabel, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
                    .addGroup(reportPanelLayout.createSequentialGroup()
                        .addComponent(reportChartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 428, Short.MAX_VALUE)
                        .addGroup(reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(reportlblSum)
                            .addComponent(reportlblIssue)
                            .addComponent(reportlblBug))))
                .addContainerGap())
        );
        reportPanelLayout.setVerticalGroup(
            reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reportPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reportScrollPOverviewTabel, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(reportlblSum)
                .addGap(2, 2, 2)
                .addGroup(reportPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(reportPanelLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(reportlblBug)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reportlblIssue))
                    .addComponent(reportChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(viewReviewsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(wizardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1020, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(reportPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(viewReviewsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(wizardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 715, Short.MAX_VALUE)
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(reportPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGap(0, 0, 0)))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setText(resourceMap.getString("saveMenuItem.text")); // NOI18N
        saveMenuItem.setName("saveMenuItem"); // NOI18N
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuSave(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        sessionMenu.setText(resourceMap.getString("sessionMenu.text")); // NOI18N
        sessionMenu.setName("sessionMenu"); // NOI18N

        sessionWizardMenuItem.setText(resourceMap.getString("sessionWizardMenuItem.text")); // NOI18N
        sessionWizardMenuItem.setName("sessionWizardMenuItem"); // NOI18N
        sessionWizardMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSessionWizard(evt);
            }
        });
        sessionMenu.add(sessionWizardMenuItem);

        reviewVieMenuItem.setText(resourceMap.getString("reviewVieMenuItem.text")); // NOI18N
        reviewVieMenuItem.setName("reviewVieMenuItem"); // NOI18N
        reviewVieMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuReviewView(evt);
            }
        });
        sessionMenu.add(reviewVieMenuItem);

        sessionReportMenuItem.setText(resourceMap.getString("sessionReportMenuItem.text")); // NOI18N
        sessionReportMenuItem.setName("sessionReportMenuItem"); // NOI18N
        sessionReportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuReportView(evt);
            }
        });
        sessionMenu.add(sessionReportMenuItem);

        menuBar.add(sessionMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        helpMenuItem.setAction(actionMap.get("showHelpPdf")); // NOI18N
        helpMenuItem.setText(resourceMap.getString("helpMenuItem.text")); // NOI18N
        helpMenuItem.setName("helpMenuItem"); // NOI18N
        helpMenu.add(helpMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1020, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 850, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        wizardPopUpBugTab.setName("wizardPopUpBugTab"); // NOI18N

        wizardPopUpIssueTab.setName("wizardPopUpIssueTab"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    class chckBxSecondTesterListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            boolean selected = wizardChckBxSecondTester.isSelected();
            if (selected) {
                wizardCmbxMoreTester.setEnabled(selected);
                swingHelper.setTesterCombox(wizardCmbxMoreTester);
                wizardCmbxMoreTester.removeItem(wizardCmbxTester.getSelectedItem());
                if (wizardCmbxMoreTester.getItemCount() == 0) {
                    wizardCmbxMoreTester.addItem("Tester No longer available @Tester.txt");
                    wizardCmbxMoreTester.setEnabled(false);
                    wizardChckBxSecondTester.setSelected(false);
                }
            } else {
                wizardCmbxMoreTester.setEnabled(selected);
                wizardCmbxMoreTester.removeAllItems();
            }
        }
    }

    class chckBxNewCharterListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            boolean selected = wizardChckBxNewCharter.isSelected();
            wizardtaNewCharter.setEditable(selected);
            wizardtaNewCharter.setEnabled(selected);
            wizardCmbxCharter.setEnabled(!selected);
            wizardtaPreviewCharter.setVisible(!selected);

        }
    }

    private void btnWizardNext(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWizardNext
        int selTab = wizardtabp.getSelectedIndex();
        int maxTab = wizardtabp.getTabCount();
        try {
            wizardbtnBack.setEnabled(true);
            wizardbtntopBack.setEnabled(true);
            wizardtabp.setSelectedIndex(selTab + 1);

            if (wizardtabp.getTitleAt(wizardtabp.getSelectedIndex()).contains("Charter")) {
                wizardChckBxNewCharter.addActionListener(new chckBxNewCharterListener());
                swingHelper.setTab1EnableAt(wizardtabp, 1);
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);
                wizardbtnBack.setEnabled(false);
                wizardbtntopBack.setEnabled(false);
                wizardbtnNext.setEnabled(false);
                wizardbtntopNext.setEnabled(false);
                mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                wizardCmbxCharter.removeAllItems();
                fileHelper.charterList.clear();
                final File directory = new File(wizardTfPathTodo.getText());
                worker = new SwingWorker<String, Void>() {

                    @Override
                    protected String doInBackground() {
                        return getCharterBackgroundW(directory);
                    }

                    @Override
                    protected void done() {
                        try {
                            if (!worker.get().contains("no")) {
                                progressBar.setIndeterminate(false);
                                progressBar.setVisible(false);
                                wizardbtnBack.setEnabled(true);
                                wizardbtntopBack.setEnabled(true);
                                wizardbtnNext.setEnabled(true);
                                wizardbtntopNext.setEnabled(true);
                                int no = wizardCmbxCharter.getItemCount();
                                wizardLblChooseCharter.setText("Please select one of " + no + " Charter");
                                mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            } else {
                                progressBar.setIndeterminate(false);
                                progressBar.setVisible(false);
                                wizardbtnBack.setEnabled(true);
                                wizardbtntopBack.setEnabled(true);
                                wizardbtnNext.setEnabled(false);
                                wizardbtntopNext.setEnabled(false);
                                mainPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                worker.execute();
            }
            if (wizardtabp.getTitleAt(wizardtabp.getSelectedIndex()).contains("Areas")) {
                swingHelper.setTab1EnableAt(wizardtabp, 2);
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);
                wizardbtnBack.setEnabled(false);
                wizardbtntopBack.setEnabled(false);
                wizardbtnNext.setEnabled(false);
                wizardbtntopNext.setEnabled(false);
                wizardbtnAddAreas.setEnabled(false);
                if (wizardtaChoosenAreas.getDocument().getLength() == 0) {
                    wizardbtnRemoveArea.setEnabled(false);
                } else {
                    wizardbtnRemoveArea.setEnabled(true);
                }

                wizardtaChoosenAreas.getDocument().addDocumentListener(new choosenAreaListener());
                wizardtabpAreas.removeAll();
                final File coverageini = new File(wizardtfCoverageini.getText());
                getAreasBacgroundW(coverageini);
                wizardbtnBack.setEnabled(true);
                wizardbtntopBack.setEnabled(true);
                wizardbtnNext.setEnabled(true);
                wizardbtntopNext.setEnabled(true);
                if (wizardChckBxNewCharter.isSelected()) {
                    wizardbtnSaveTodo.setEnabled(true);
                }
                wizardbtnNext.setText("Create Testsession");
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                if (!wizardChckBxNewCharter.isSelected()) {
                    String todo = ChoosenCharter.substring(ChoosenCharter.indexOf("(" + wizardTfPathTodo.getText())).replace("(", "").replace(")", "");
                    final File todocheckliste = new File(todo);
                    String msg = "Import all Areas from the Todo-Session?";
                    fileHelper.getAreasChecklist(todocheckliste);
                    fileHelper.areaChecklist.remove(0);
                    if (!fileHelper.areaChecklist.get(0).isEmpty()) {
                        if (JOptionPane.showConfirmDialog(wizardtabpAreas, msg,
                                "Import?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                            wizardtaChoosenAreas.setText("");
                            for (String s : fileHelper.areaChecklist) {
                                wizardtaChoosenAreas.append(s + "\n");
                            }
                            wizardtaChoosenAreas.setCaretPosition(0);
                        }
                    }
                }
            }
            if (wizardtabp.getTitleAt(wizardtabp.getSelectedIndex()).contains("Testsession")) {
                swingHelper.setTab1EnableAt(wizardtabp, 3);
                wizardbtnNext.setVisible(false);
                wizardbtntopNext.setEnabled(false);
                wizardbtnStart.addActionListener(new ButtonListenerStart());
                wizardbtntopStart.addActionListener(new ButtonListenerStart());
                wizardbtnStop.addActionListener(new ButtonListenerStop());
                wizardbtntopStop.addActionListener(new ButtonListenerStop());
                if (wizardChckBxNewCharter.isSelected()) {
                    wizardtaCharterdynamic.setText(wizardtaNewCharter.getText().trim());
                } else {
                    wizardtaCharterdynamic.setText(ChoosenCharter.substring(0, ChoosenCharter.indexOf("(" + wizardTfPathTodo.getText())));
                }
                statusMessageLabel.setText("");
                wizardbtnStart.setVisible(true);
                wizardbtnStart.setEnabled(true);
                wizardbtntopStart.setEnabled(true);
                wizardbtnStop.setVisible(true);
                wizardbtnStop.setEnabled(false);
                wizardbtntopStop.setEnabled(false);
                final JPopupMenu popupMenu = new JPopupMenu();
                final File coverageini = new File(wizardtfCoverageini.getText());
                swingHelper.getAreastoPopupMenu(popupMenu, coverageini, new alBugsIssue());
                popupListener2 = new PopupListener(popupMenu);
                wizardtaTestsessionAreas.addMouseListener(popupListener2);
                wizardtaTestsessionAreas.setText(wizardtaChoosenAreas.getText());
                wizardtaTestsessionAreas.setCaretPosition(0);
                wizardtaCharterdynamic.setCaretPosition(0);
                if (wizardChckBxSecondTester.isSelected()) {
                    TesterRealname = wizardCmbxTester.getSelectedItem().toString();
                    wizardtfNameOfTester.setText(TesterRealname.substring(4) + " " + wizardCmbxMoreTester.getSelectedItem().toString().substring(4));
                } else {
                    TesterRealname = wizardCmbxTester.getSelectedItem().toString();
                    wizardtfNameOfTester.setText(TesterRealname.substring(4));
                }
            }
            if (wizardtabp.getSelectedIndex() == maxTab) {
                wizardbtnNext.setEnabled(false);
                wizardbtntopNext.setEnabled(false);
            }
        } catch (IndexOutOfBoundsException ex) {
            statusMessageLabel.setText(ex.getMessage());
        }
    }//GEN-LAST:event_btnWizardNext

    public String getCharterBackgroundW(File f) {
        fileHelper.charterList.clear();
        if (fileHelper.getFilesWithCharter(f, wizardhCkBxSubfolder.isSelected())) {
            for (String charter : fileHelper.charterList) {
                wizardCmbxCharter.addItem(charter);
            }
            return "yes";
        } else {
            JOptionPane.showMessageDialog(null, "Directory '" + f + "' does not exsits!\nPlease edit in the config.txt the 'Todo'-Path! Then go a page back and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return "no";
        }
    }

    public void getAreasBacgroundW(File f) {
        fileHelper.getAreas(f);
        Iterator<Map.Entry<String, List>> it = fileHelper.areaMap.entrySet().iterator();
        while (it.hasNext()) {
            DefaultListModel dlm = new DefaultListModel();
            JList list = new JList(dlm);
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.getViewport().setView(list);
            Map.Entry en = it.next();
            wizardtabpAreas.addTab(en.getKey().toString().substring(3), scrollPane);
            for (Object o : fileHelper.areaMap.get(en.getKey().toString())) {
                dlm.addElement(o);
            }
            MouseListener mouseListener = new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    JList tabList = (JList) mouseEvent.getSource();
                    if (mouseEvent.getClickCount() == 2) {
                        int index = tabList.locationToIndex(mouseEvent.getPoint());
                        if (index >= 0) {
                            Object o = tabList.getModel().getElementAt(index);
                            wizardtaChoosenAreas.append(o.toString() + "\n");
                        }
                    }
                }
            };
            list.addMouseListener(mouseListener);
            ListSelectionListener listListener = new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    JList list = (JList) e.getSource();
                    if (e.getValueIsAdjusting() == false) {

                        if (list.getSelectedIndex() == -1) {
                            //No selection, disable add button.
                            wizardbtnAddAreas.setEnabled(false);

                        } else {
                            //Selection, enable the add button.
                            wizardbtnAddAreas.setEnabled(true);
                        }
                    }
                }
            };
            list.addListSelectionListener(listListener);
        }
    }

    private void btnWizardBack(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWizardBack
        int selTab = wizardtabp.getSelectedIndex();
        try {
            if (wizardbtnNext.isVisible() == false | wizardbtnStart.isVisible() == true) {
                wizardbtnNext.setVisible(true);
                wizardbtntopNext.setEnabled(true);
                wizardbtnStart.setVisible(false);
                wizardbtntopStart.setEnabled(false);
                wizardbtnStop.setVisible(false);
                wizardbtntopStop.setEnabled(false);
            }
            wizardbtnNext.setText("Next >");
            wizardbtnNext.setEnabled(true);
            wizardbtntopNext.setEnabled(true);
            if (wizardtabp.getSelectedIndex() != 0) {
                wizardtabp.setSelectedIndex(selTab - 1);
                swingHelper.setTab1EnableAt(wizardtabp, selTab - 1);
            }
            statusMessageLabel.setText("");
            if (wizardtabp.getSelectedIndex() == 0) {
                wizardbtnBack.setEnabled(false);
                wizardbtntopBack.setEnabled(false);
            }
        } catch (IndexOutOfBoundsException ex) {
            statusMessageLabel.setText(ex.getMessage());
        }
    }//GEN-LAST:event_btnWizardBack

    private void ChooseCharterjCombobox1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChooseCharterjCombobox1
        if (wizardCmbxCharter.getItemCount() != 0) {
            ChoosenCharter = wizardCmbxCharter.getSelectedItem().toString();
            wizardtaPreviewCharter.setText(ChoosenCharter);


        }
    }//GEN-LAST:event_ChooseCharterjCombobox1

    class ButtonListenerStart implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            wizardbtnStop.setEnabled(true);
            wizardbtntopStop.setEnabled(true);
            wizardbtnStart.setEnabled(false);
            wizardbtntopStart.setEnabled(false);
            wizardbtnBack.setEnabled(false);
            wizardbtntopBack.setEnabled(false);
            wizardLblstartTime.setText(textHelper.Timestamp("MM/dd/yy hh:mma"));
            if (!running) {
                running = true;
                UhrzeitThread uhr = new UhrzeitThread();
                uhr.start();
            }
        }
    }

    class ButtonListenerStop implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            running = false;
            wizardbtnStop.setEnabled(false);
            wizardbtntopStop.setEnabled(false);
            wizardbtnStart.setEnabled(false);
            wizardbtntopStart.setEnabled(false);
            wizardbtnSave.setVisible(true);
            wizardbtnSave.setEnabled(true);
            wizardbtntopSave.setEnabled(true);
            saveMenuItem.setEnabled(true);
            if (min < 75) {
                wizardtfDuration.setText("short");
            } else if (min > 75 && min <= 105) {
                wizardtfDuration.setText("normal");
            } else if (min > 105) {
                wizardtfDuration.setText("long");
            }
        }
    }

    class AddListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int selTab = wizardtabpAreas.getSelectedIndex();
            JScrollPane jsp = (JScrollPane) wizardtabpAreas.getComponentAt(selTab);
            JList list = (JList) jsp.getViewport().getView();
            int minIndex = list.getMinSelectionIndex();
            int maxIndex = list.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (list.isSelectedIndex(i)) {
                    Object selected = list.getModel().getElementAt(i);
                    wizardtaChoosenAreas.append(selected.toString() + "\n");
                }
            }
        }
    }
    
    class UhrzeitThread extends Thread {

        @Override
        public void run() {
            long a = System.currentTimeMillis();
            while (running) {
                long c = System.currentTimeMillis() - a;
                HS = c % 1000;
                sek = (c / 1000) % 60;
                min = c / 60000;

                wizardLblStopfield.setText(String.format("%02d", min) + ":" + String.format("%02d", sek) + ":" + Math.round((double) HS / 10));

            }
        }
    }

    class reviewTaReviewListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            toSave(e);
        }

        public void removeUpdate(DocumentEvent e) {
            toSave(e);
        }

        public void changedUpdate(DocumentEvent e) {
            //Plain text components do not fire these events
        }

        public void toSave(DocumentEvent e) {
            Document doc = (Document) e.getDocument();
            int changeLength = e.getLength();
            if (e.getType().toString().matches("INSERT") && changeLength >= 1) {
                if (!edited) {
                    reviewSessionsTabp.setTitleAt(1, reviewSessionsTabp.getTitleAt(1) + "*");
                }
                edited = true;
            }
            if (doc.getLength() == 0 && edited && e.getType().toString().matches("REMOVE")) {
                reviewSessionsTabp.setTitleAt(1, reviewSessionsTabp.getTitleAt(1).replace("*", ""));
                edited = false;
            }
        }
    }

    class choosenAreaListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            edited(e);
        }

        public void removeUpdate(DocumentEvent e) {
            edited(e);
        }

        public void changedUpdate(DocumentEvent e) {
            //Plain text components do not fire these events
        }

        public void edited(DocumentEvent e) {
            Document doc = (Document) e.getDocument();
            int changeLength = e.getLength();
            if (e.getType().toString().matches("INSERT") && changeLength >= 1) {
//                if (!edited) {
                wizardbtnRemoveArea.setEnabled(true);
//                }
                edited = true;
            }
            if (doc.getLength() == 0 && edited && e.getType().toString().matches("REMOVE")) {
                wizardbtnRemoveArea.setEnabled(false);
                edited = false;
            }
        }
    }

    class alBugsIssue implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().contains("Bug")) {
                wizardtaBugs.append("#BUG");
            }
            if (e.getActionCommand().contains("#Issue")) {
                wizardtaIssues.append("#ISSUE");
            }
            if (!e.getActionCommand().contains("#Issue") && !e.getActionCommand().contains("Bug")) {
                wizardtaTestsessionAreas.append(e.getActionCommand().toString() + "\n");
            }
        }
    };

    private void menuReviewView(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuReviewView
        showReviewPanel();
    }//GEN-LAST:event_menuReviewView

    private void menuSessionWizard(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSessionWizard
        showSessionWizard();
    }//GEN-LAST:event_menuSessionWizard

    private void btnWizardSaveClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnWizardSaveClicked
        saveTestsession();
    }//GEN-LAST:event_btnWizardSaveClicked

    private void jButtonRefreshTester(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonRefreshTester
        swingHelper.setTesterCombox(wizardCmbxTester);
    }//GEN-LAST:event_jButtonRefreshTester

    private void jMenuSave(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuSave
        saveTestsession();
    }//GEN-LAST:event_jMenuSave

    private void wizardbtnRefreshAreasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wizardbtnRefreshAreasMouseClicked
        final File coverageini = new File(wizardtfCoverageini.getText());
        wizardtabpAreas.removeAll();
        getAreasBacgroundW(coverageini);
    }//GEN-LAST:event_wizardbtnRefreshAreasMouseClicked

    private void reviewbtnNextClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reviewbtnNextClicked
        int selTab = reviewSessionsTabp.getSelectedIndex();
        Filename = "";
        String selectedItem = reviewCmbxSessiontoReview.getSelectedItem().toString();
        String filename = selectedItem.substring(selectedItem.lastIndexOf("(") + 1, selectedItem.length() - 1);
        if (filename.contains("Please select a ")) {
            JOptionPane.showMessageDialog(reviewCmbxSessiontoReview, "Please select a session to review!", "Select a Session", JOptionPane.ERROR_MESSAGE);
        } else {
            Filename = filename;
            try {
                swingHelper.setTab1EnableAt(reviewSessionsTabp, 1);
                reviewSessionsTabp.setSelectedIndex(selTab + 1);
                reviewbtnNext.setEnabled(false);
                reviewbtntopNext.setEnabled(false);
                reviewbtnBack.setEnabled(true);
                reviewbtntopBack.setEnabled(true);
                File testsession = new File(Filename);
                fileHelper.getTestsession(testsession);
                createReviewSessionView();
                reviewSessionsTabp.setTitleAt(1, "Review of " + filename);
                reviewbtnSave.setVisible(true);
                reviewbtnSave.setEnabled(true);
                reviewbtntopSave.setEnabled(true);
                reviewtaTestsessionAreasReview.setCaretPosition(0);
            } catch (IndexOutOfBoundsException e) {
            }
        }
    }//GEN-LAST:event_reviewbtnNextClicked

    private void createReviewSessionView() {
        String content = fileHelper.toreviewSession;
        String Charter = content.substring(content.indexOf("CHARTER") + "Charter".length(), content.indexOf("#AREAS")).trim();
        String Areas = content.substring(content.indexOf("#AREAS") + "#AREAS".length(), content.indexOf("START")).trim();
        String Start = content.substring(content.indexOf("START") + "START".length(), content.indexOf("TESTER")).trim();
        String Tester = content.substring(content.indexOf("TESTER") + "TESTER".length(), content.indexOf("TASK BREAKDOWN")).trim();
        String Duration = content.substring(content.indexOf("#DURATION") + "#DURATION".length(), content.indexOf("#SESSION SETUP")).trim();
        String Setup = content.substring(content.indexOf("#SESSION SETUP") + "#SESSION SETUP".length(), content.indexOf("#TEST DESIGN AND EXECUTION")).trim();
        String Design = content.substring(content.indexOf("#TEST DESIGN AND EXECUTION") + "#TEST DESIGN AND EXECUTION".length(), content.indexOf("#BUG INVESTIGATION AND REPORTING")).trim();
        String Bug = content.substring(content.indexOf("#BUG INVESTIGATION AND REPORTING") + "#BUG INVESTIGATION AND REPORTING".length(), content.indexOf("#CHARTER VS. OPPORTUNITY")).trim();
        String CharterVsOpp = content.substring(content.indexOf("#CHARTER VS. OPPORTUNITY") + "#CHARTER VS. OPPORTUNITY".length(), content.indexOf("DATA FILES")).trim();
        String Datafiles = content.substring(content.indexOf("DATA FILES") + "DATA FILES".length(), content.indexOf("TEST NOTES")).trim();
        String Testnotes = content.substring(content.indexOf("TEST NOTES") + "TEST NOTES".length(), content.indexOf("BUGS")).trim();
        String Bugs = content.substring(content.indexOf("BUGS") + "BUGS".length(), content.indexOf("ISSUES")).trim();
        String Issues = content.substring(content.indexOf("ISSUES") + "ISSUES".length(), content.indexOf("REVIEW")).trim();
        String Review = content.substring(content.indexOf("REVIEW") + "REVIEW".length()).trim();
        reviewtaCharterdynamic.setText(Charter);
        reviewtaTestsessionAreasReview.setText(Areas);
        reviewLblStartTime.setText(Start);
        reviewtfNameOfTester.setText(Tester);
        reviewtfDuration.setText(Duration);
        reviewtfSessionSetup.setText(Setup);
        reviewtfTestDesignExecution.setText(Design);
        reviewtfBugInvestigation.setText(Bug);
        reviewtfCharter.setText(CharterVsOpp);
        reviewtaDataFiles.setText(Datafiles);
        reviewtaTestNotes.setText(Testnotes);
        reviewedipaneBugs.setText(Bugs);
        reviewtaIssues.setText(Issues);
        reviewtaReview.setText(Review);
        reviewTabpDTBIR.setTitleAt(2, "Bugs (" + textHelper.countBugIssue(Bugs, "#BUG") + ")");
        reviewTabpDTBIR.setTitleAt(3, "Issues (" + textHelper.countBugIssue(Issues, "#ISSUE") + ")");
    }

    private void reviewbtnBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reviewbtnBackMouseClicked
        int clickCount = evt.getClickCount();
        if (clickCount == 1 && reviewbtnBack.isEnabled() && reviewbtntopBack.isEnabled()) {
            int selTab = reviewSessionsTabp.getSelectedIndex();
            swingHelper.setTab1EnableAt(reviewSessionsTabp, selTab - 1);
            reviewSessionsTabp.setSelectedIndex(selTab - 1);
            reviewbtnNext.setEnabled(true);
            reviewbtntopNext.setEnabled(true);
            reviewbtnBack.setEnabled(false);
            reviewbtntopBack.setEnabled(false);
            reviewbtnMove.setEnabled(false);
            reviewbtntopMove.setEnabled(false);
            reviewbtnSave.setVisible(false);
            reviewbtntopSave.setEnabled(false);
            cleanupReviewPanel();
        } else {
            return;
        }
    }//GEN-LAST:event_reviewbtnBackMouseClicked

    private void cleanupReviewPanel() {
        for (int i = 0; i < reviewCmbxSessiontoReview.getItemCount(); i++) {
            if (reviewCmbxSessiontoReview.getItemAt(i).equals(reviewCmbxSessiontoReview.getSelectedItem().toString())) {
                reviewCmbxSessiontoReview.removeItemAt(i);
                break;
            }
        }
        reviewCmbxSessiontoReview.setSelectedIndex(0);
        Component[] compo = reviewtoReviewPanel.getComponents();
        for (Component c : compo) {
            if (c instanceof JLabel) {
                JLabel jl = (JLabel) c;
                if (jl.getText().contains(reviewtfNameOfTester.getText())) {
                    char nc;
                    String text = jl.getText();
                    nc = text.charAt(jl.getText().lastIndexOf(":") + 2);
                    int no = Character.digit(nc, 10);
                    no--;
                    jl.setText(text.replace(text.charAt(text.lastIndexOf(":") + 2), Character.forDigit(no, 10)));
                    reviewtoReviewPanel.revalidate();
                    reviewtoReviewPanel.repaint();
                    break;
                }
            }
        }
        reviewtaCharterdynamic.setText("");
        reviewtaTestsessionAreasReview.setText("");
        reviewLblStartTime.setText("");
        reviewtfNameOfTester.setText("");
        reviewtfDuration.setText("");
        reviewtfSessionSetup.setText("");
        reviewtfTestDesignExecution.setText("");
        reviewtfBugInvestigation.setText("");
        reviewtfCharter.setText("");
        reviewtaDataFiles.setText("");
        reviewtaTestNotes.setText("");
        reviewedipaneBugs.setText("");
        reviewtaIssues.setText("");
        reviewtaReview.setText("");
    }

    private void reviewbtnSaveButtonClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reviewbtnSaveButtonClicked
        String[] content = {reviewtaCharterdynamic.getText(), reviewtaTestsessionAreasReview.getText(), reviewLblStartTime.getText(),
            reviewtfNameOfTester.getText(), reviewtfDuration.getText(),reviewtfTestDesignExecution.getText(), 
            reviewtfSessionSetup.getText(), reviewtfBugInvestigation.getText(), reviewtfCharter.getText(),
            reviewtaDataFiles.getText(), reviewtaTestNotes.getText(), reviewedipaneBugs.getText(), reviewtaIssues.getText(),
            reviewtaReview.getText()};
        if (fileHelper.createTestsession(Filename, content)) {
            JOptionPane.showMessageDialog(reviewTabpDTBIR, Filename + " saved", "Session saved", JOptionPane.INFORMATION_MESSAGE);
            reviewSessionsTabp.setTitleAt(1, reviewSessionsTabp.getTitleAt(1).replace("*", ""));
        }
        reviewbtnMove.setEnabled(true);
        reviewbtntopMove.setEnabled(true);
    }//GEN-LAST:event_reviewbtnSaveButtonClicked

    private void btnWizardNewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnWizardNewMouseClicked
        wizardtabp.setSelectedIndex(0);
        swingHelper.setTab1EnableAt(wizardtabp, 0);
        wizardbtnStart.setVisible(false);
        wizardbtntopStart.setEnabled(false);
        wizardbtnStop.setVisible(false);
        wizardbtntopStop.setEnabled(false);
        wizardbtnSave.setVisible(false);
        wizardbtntopSave.setEnabled(false);
        wizardbtntopNew.setEnabled(false);
        wizardbtnNew.setVisible(false);
        wizardbtnNext.setText("Next >");
        wizardbtnNext.setVisible(true);
        wizardbtntopNext.setEnabled(true);
        cleanupSessionWizard();
    }//GEN-LAST:event_btnWizardNewMouseClicked

    private void wizardbtnRemoveAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wizardbtnRemoveAreaMouseClicked
        int start;
        int end;
        int count = -1;
        try {
            count = wizardtaChoosenAreas.getLineCount() - 2;
            if (count > 0) {
                start = wizardtaChoosenAreas.getLineStartOffset(count);
                end = wizardtaChoosenAreas.getLineEndOffset(count);
                wizardtaChoosenAreas.replaceRange(null, start, end);
            } else if (count == 0) {
                start = wizardtaChoosenAreas.getLineStartOffset(count);
                end = wizardtaChoosenAreas.getLineEndOffset(count);
                wizardtaChoosenAreas.replaceRange(null, start, end);
            }
        } catch (BadLocationException e1) {
        }
    }//GEN-LAST:event_wizardbtnRemoveAreaMouseClicked

    private void wizardbtntopSaveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wizardbtntopSaveMouseClicked
        if (wizardbtntopSave.isEnabled()) {
            saveTestsession();
        }
    }//GEN-LAST:event_wizardbtntopSaveMouseClicked

    private void wizardbtntopNewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wizardbtntopNewMouseClicked
        if (wizardbtntopNew.isEnabled()) {
            btnWizardNewMouseClicked(evt);
        }
    }//GEN-LAST:event_wizardbtntopNewMouseClicked

    private void wizardbtntopBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wizardbtntopBackActionPerformed
        if (wizardbtntopBack.isEnabled()) {
            btnWizardBack(evt);
        }
    }//GEN-LAST:event_wizardbtntopBackActionPerformed

    private void wizardbtntopNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wizardbtntopNextActionPerformed
        if (wizardbtntopNext.isEnabled()) {
            btnWizardNext(evt);
        }
    }//GEN-LAST:event_wizardbtntopNextActionPerformed

    private void reviewbtntopSaveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reviewbtntopSaveMouseClicked
        if (reviewbtntopSave.isEnabled()) {
            reviewbtnSaveButtonClicked(evt);
        }
    }//GEN-LAST:event_reviewbtntopSaveMouseClicked

    private void reviewbtntopBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reviewbtntopBackMouseClicked
        if (reviewbtntopBack.isEnabled()) {
            reviewbtnBackMouseClicked(evt);
        }
    }//GEN-LAST:event_reviewbtntopBackMouseClicked

    private void reviewbtntopNextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reviewbtntopNextMouseClicked
        if (reviewbtntopNext.isEnabled()) {
            reviewbtnNextClicked(evt);
        }
    }//GEN-LAST:event_reviewbtntopNextMouseClicked

    private void menuReportView(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuReportView
        showReportPanel();
    }//GEN-LAST:event_menuReportView

    private void wizardbtnSaveTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wizardbtnSaveTodoActionPerformed
        if (wizardbtnSaveTodo.isEnabled()) {
            if (swingHelper.saveTodoSession(wizardtaNewCharter.getText(), wizardtaChoosenAreas.getText(), wizardTfPathTodo.getText())) {
                statusMessageLabel.setText("Todo-Session saved!");
            }
        }
    }//GEN-LAST:event_wizardbtnSaveTodoActionPerformed

private void moveto(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_moveto
    if (reviewbtnMove.isEnabled() && reviewbtntopMove.isEnabled()) {
        String approvedDir = fileHelper.getApprovedDir();
        String session = reviewSessionsTabp.getTitleAt(1).substring(10);
        try {
            File file = new File(session);

            // Destination directory
            File dir = new File(approvedDir);

            // Move file to new directory
//            file.renameTo(new File(dir, file.getName()));
            FileUtils.copyFileToDirectory(file, dir);
            file.delete();
            JOptionPane.showMessageDialog(null, "File moved to " + approvedDir);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed moving file " + e);
        }
    }

}//GEN-LAST:event_moveto

    private void cleanupSessionWizard() {
        wizardtaChoosenAreas.setText("");
        wizardLblstartTime.setText("<start time>");
        wizardLblStopfield.setText("00:00:00");
        wizardtfDuration.setText("");
        wizardtfBugInvestigation.setText("");
        wizardtfOpportunity.setText("");
        wizardtfSessionSetup.setText("");
        wizardtfTestDesignExecution.setText("");
        wizardtfCharter.setText("");
        wizardtaDataFiles.setText("");
        wizardtaIssues.setText("");
        wizardtaTestNotes.setText("");
        wizardtaReview.setText("");
        wizardtaBugs.setText("");
    }

    private boolean checkTestsession() {
        if (wizardtfBugInvestigation.getText().isEmpty() || wizardtfSessionSetup.getText().isEmpty() || wizardtfTestDesignExecution.getText().isEmpty()
                || wizardtfCharter.getText().isEmpty() || wizardtfOpportunity.getText().isEmpty()) {
            JOptionPane.showMessageDialog(wizardPanelTaskBreakd, "Please fill out all text fields!", "Missing Value", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        percent.clear();
        percent.put(wizardLblBugReporting, wizardtfBugInvestigation.getText().toString());
        percent.put(wizardLblSetup, wizardtfSessionSetup.getText().toString());
        percent.put(wizardLblDesignExecution, wizardtfTestDesignExecution.getText().toString());

        if (textHelper.checkTaskBreakdown(percent)) {
            percent.clear();
            percent.put(wizardLblChartervs, wizardtfCharter.getText().toString());
            percent.put(wizardLblOpportunity, wizardtfOpportunity.getText().toString());
        } else {
            return false;
        }
        if (textHelper.checkTaskBreakdown(percent)) {
            //Prüfen des Dateinamens
            submittedFolder = wizardTfPathSubmitted.getText();
            realFilename = "et-" + TesterRealname.substring(0, 3) + "-" + textHelper.Timestamp("yyMMdd") + "-a.ses";
            if (submittedFolder.isEmpty()) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new java.io.File("."));
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(wizardbtnSave) == JFileChooser.APPROVE_OPTION) {
                    wizardTfPathSubmitted.setText(chooser.getCurrentDirectory().toString() + "\\");
                    checkTestsession();
                }
            } else {
                return true;
            }

        }
        return false;
    }

    private void saveTestsession() {
        int countSession = 0;
        if (checkTestsession()) {
            Filename = submittedFolder + realFilename;

            //Zusammenbauen des Dateiinhaltes
            String[] content = {wizardtaCharterdynamic.getText(), wizardtaTestsessionAreas.getText(),
                wizardLblstartTime.getText().toLowerCase(), wizardtfNameOfTester.getText(), wizardtfDuration.getText(),
                wizardtfTestDesignExecution.getText(), wizardtfSessionSetup.getText(), wizardtfBugInvestigation.getText(),
                wizardtfCharter.getText() + "/" + wizardtfOpportunity.getText(), wizardtaDataFiles.getText(),
                wizardtaTestNotes.getText(), wizardtaBugs.getText(), wizardtaIssues.getText(),
                wizardtaReview.getText()};

            //Datei wegspeichern
            String checkedFilename = fileHelper.checkFileExists(Filename);
            boolean filecreated = fileHelper.createTestsession(checkedFilename, content);
            if (filecreated) {
                JOptionPane.showMessageDialog(wizardTabpDetailsDTBIR, checkedFilename + " saved", "Session saved", JOptionPane.INFORMATION_MESSAGE);
                saveMenuItem.setEnabled(false);
                wizardbtnSave.setEnabled(false);
                wizardbtntopSave.setEnabled(false);
                wizardbtnNew.setVisible(true);
                wizardbtntopNew.setEnabled(true);
                wizardtaTestsessionAreas.removeMouseListener(popupListener2);
            }
            fileHelper.getSessionList(submittedFolder);
            for (String s : fileHelper.sessionList) {
                if (s.contains(TesterRealname.substring(0, 3))) {
                    countSession++;
                }
            }
            if (countSession >= 2) {
                JOptionPane.showMessageDialog(wizardTabpDetailsDTBIR, "Please do a Session-Review.\nThere are " + countSession + " Sessions!", "Session Review", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar ReviewToolbar;
    private javax.swing.JToolBar WizardToolbar;
    private javax.swing.JPanel areasPanel;
    private javax.swing.JPanel charterPanel;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private JLayeredPane mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JPanel reportChartPanel;
    private javax.swing.JTable reportOverviewTable;
    private javax.swing.JPanel reportPanel;
    private javax.swing.JScrollPane reportScrollPOverviewTabel;
    private javax.swing.JLabel reportlblBug;
    private javax.swing.JLabel reportlblIssue;
    private javax.swing.JLabel reportlblSum;
    private javax.swing.JLabel reviewCharterHeadlineLabel;
    private javax.swing.JComboBox reviewCmbxSessiontoReview;
    private javax.swing.JLabel reviewLblBugReporting;
    private javax.swing.JLabel reviewLblChartervs;
    private javax.swing.JLabel reviewLblDuration;
    private javax.swing.JLabel reviewLblOpportunity;
    private javax.swing.JLabel reviewLblSetup;
    private javax.swing.JLabel reviewLblStart;
    private javax.swing.JLabel reviewLblStartTime;
    private javax.swing.JLabel reviewLblTestExecution;
    private javax.swing.JLabel reviewLblTester;
    private javax.swing.JLabel reviewLblvs;
    private javax.swing.JPanel reviewPaneTaskbreakdown;
    private javax.swing.JPanel reviewPanelTestsessionAreas;
    private javax.swing.JScrollPane reviewScrollPaneArea;
    private javax.swing.JScrollPane reviewScrollPaneBugs;
    private javax.swing.JScrollPane reviewScrollPaneDatafile;
    private javax.swing.JScrollPane reviewScrollPaneIssues;
    private javax.swing.JScrollPane reviewScrollPaneReview;
    private javax.swing.JScrollPane reviewScrollPaneTestNotes;
    public javax.swing.JTabbedPane reviewSessionsTabp;
    private javax.swing.JTabbedPane reviewTabpDTBIR;
    private javax.swing.JPanel reviewToreviewSessionsPanel;
    private javax.swing.JMenuItem reviewVieMenuItem;
    private javax.swing.JPanel reviewViewPanel;
    private javax.swing.JLabel reviewViewlabel;
    private javax.swing.JButton reviewbtnBack;
    private javax.swing.JButton reviewbtnMove;
    private javax.swing.JButton reviewbtnNext;
    private javax.swing.JButton reviewbtnSave;
    private javax.swing.JButton reviewbtntopBack;
    private javax.swing.JButton reviewbtntopMove;
    private javax.swing.JButton reviewbtntopNext;
    private javax.swing.JButton reviewbtntopSave;
    private javax.swing.JEditorPane reviewedipaneBugs;
    private javax.swing.JTextArea reviewtaCharterdynamic;
    private javax.swing.JTextArea reviewtaDataFiles;
    private javax.swing.JTextArea reviewtaIssues;
    private javax.swing.JTextArea reviewtaReview;
    private javax.swing.JTextArea reviewtaTestNotes;
    private javax.swing.JTextArea reviewtaTestsessionAreasReview;
    private javax.swing.JTextField reviewtfBugInvestigation;
    private javax.swing.JTextField reviewtfCharter;
    private javax.swing.JTextField reviewtfDuration;
    private javax.swing.JTextField reviewtfNameOfTester;
    private javax.swing.JTextField reviewtfSessionSetup;
    private javax.swing.JTextField reviewtfTestDesignExecution;
    private javax.swing.JPanel reviewtoReviewPanel;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenu sessionMenu;
    private javax.swing.JMenuItem sessionReportMenuItem;
    private javax.swing.JMenuItem sessionWizardMenuItem;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JPanel testsessionPanel;
    private javax.swing.JPanel viewReviewsPanel;
    private javax.swing.JPanel welcomePanel;
    private javax.swing.JCheckBox wizardChckBxNewCharter;
    private javax.swing.JCheckBox wizardChckBxSecondTester;
    private javax.swing.JComboBox wizardCmbxCharter;
    private javax.swing.JComboBox wizardCmbxMoreTester;
    private javax.swing.JComboBox wizardCmbxTester;
    private javax.swing.JLabel wizardLblBugReporting;
    private javax.swing.JLabel wizardLblChartervs;
    private javax.swing.JLabel wizardLblChooseCharter;
    private javax.swing.JLabel wizardLblChooseName;
    private javax.swing.JLabel wizardLblDesignExecution;
    private javax.swing.JLabel wizardLblDuration;
    private javax.swing.JLabel wizardLblOpportunity;
    private javax.swing.JLabel wizardLblPathSubmitted;
    private javax.swing.JLabel wizardLblSetup;
    private javax.swing.JLabel wizardLblSlash;
    private javax.swing.JLabel wizardLblStart;
    private javax.swing.JLabel wizardLblStopfield;
    private javax.swing.JLabel wizardLblTester;
    private javax.swing.JLabel wizardLblWelcome;
    private javax.swing.JLabel wizardLblcharterHeadline;
    private javax.swing.JLabel wizardLblchooseArea;
    private javax.swing.JLabel wizardLblpathToCoverageini;
    private javax.swing.JLabel wizardLblpathTodo;
    private javax.swing.JLabel wizardLblstartTime;
    private javax.swing.JLabel wizardLblvs;
    private javax.swing.JPanel wizardPanel;
    private javax.swing.JPanel wizardPanelTaskBreakd;
    private javax.swing.JPanel wizardPanelTestsessionAreas;
    private javax.swing.JPopupMenu wizardPopUpBugTab;
    private javax.swing.JPopupMenu wizardPopUpIssueTab;
    private javax.swing.JScrollPane wizardScrollPCharterInfo;
    private javax.swing.JScrollPane wizardScrollPaneAreas;
    private javax.swing.JScrollPane wizardScrollPaneBugs;
    private javax.swing.JScrollPane wizardScrollPaneDataFiles;
    private javax.swing.JScrollPane wizardScrollPaneIssues;
    private javax.swing.JScrollPane wizardScrollPaneReview;
    private javax.swing.JScrollPane wizardScrollPaneTestNotes;
    private javax.swing.JTabbedPane wizardTabpDetailsDTBIR;
    private javax.swing.JTextField wizardTfPathSubmitted;
    private javax.swing.JTextField wizardTfPathTodo;
    private javax.swing.JButton wizardbtnAddAreas;
    private javax.swing.JButton wizardbtnBack;
    private javax.swing.JButton wizardbtnNew;
    private javax.swing.JButton wizardbtnNext;
    private javax.swing.JButton wizardbtnRefreshAreas;
    private javax.swing.JButton wizardbtnRefreshTester;
    private javax.swing.JButton wizardbtnRemoveArea;
    private javax.swing.JButton wizardbtnSave;
    private javax.swing.JButton wizardbtnSaveTodo;
    private javax.swing.JButton wizardbtnStart;
    private javax.swing.JButton wizardbtnStop;
    private javax.swing.JButton wizardbtntopBack;
    private javax.swing.JButton wizardbtntopNew;
    private javax.swing.JButton wizardbtntopNext;
    private javax.swing.JButton wizardbtntopSave;
    private javax.swing.JButton wizardbtntopStart;
    private javax.swing.JButton wizardbtntopStop;
    private javax.swing.JCheckBox wizardhCkBxSubfolder;
    private javax.swing.JScrollPane wizardjScrollPaneAreas;
    private javax.swing.JTextArea wizardtaBugs;
    private javax.swing.JTextArea wizardtaCharterdynamic;
    private javax.swing.JTextArea wizardtaChoosenAreas;
    private javax.swing.JTextArea wizardtaDataFiles;
    private javax.swing.JTextArea wizardtaIssues;
    private javax.swing.JTextArea wizardtaNewCharter;
    private javax.swing.JTextArea wizardtaPreviewCharter;
    private javax.swing.JTextArea wizardtaReview;
    private javax.swing.JTextArea wizardtaTestNotes;
    private javax.swing.JTextArea wizardtaTestsessionAreas;
    private javax.swing.JTabbedPane wizardtabp;
    private javax.swing.JTabbedPane wizardtabpAreas;
    private javax.swing.JTextField wizardtfBugInvestigation;
    private javax.swing.JTextField wizardtfCharter;
    private javax.swing.JTextField wizardtfCoverageini;
    private javax.swing.JTextField wizardtfDuration;
    private javax.swing.JTextField wizardtfNameOfTester;
    private javax.swing.JTextField wizardtfOpportunity;
    private javax.swing.JTextField wizardtfSessionSetup;
    private javax.swing.JTextField wizardtfTestDesignExecution;
    // End of variables declaration//GEN-END:variables
    private JDialog aboutBox;
}
