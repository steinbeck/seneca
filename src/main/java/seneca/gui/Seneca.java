/*
 *  Seneca.java
 *
 *  Copyright (C) 1997, 1998, 1999  Dr. Christoph Steinbeck
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This software is published and distributed under artistic license.
 *  The intent of this license is to state the conditions under which this Package
 *  may be copied, such that the Copyright Holder maintains some semblance
 *  of artistic control over the development of the package, while giving the
 *  users of the package the right to use and distribute the Package in awr
 *  more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *  THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  The complete text of the license can be found in a file called LICENSE
 *  accompanying this package.
 */
package seneca.gui;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import seneca.core.FilePreference;
import seneca.core.SenecaDataset;
import seneca.core.SpecMLReader;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Properties;

public class Seneca extends JFrame implements ChangeListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private SenecaDataset sd;
    protected FileDialog fileDialog;
    JPanel framepanel;
    JPanel mainpanel;
    JPanel statuspanel;
    JLabel status1;
    JLabel status2;
    JLabel status3;
    JMenuBar menuBar;
    JSplitPane datasetPane;
    protected transient EventListenerList changeListeners = new EventListenerList();
    boolean DEBUG = true;
    boolean isClosed = false;
    public static SenecaDatasetCollection senecaDatasets = new SenecaDatasetCollection();
    public final static String newAction = "new";
    public final static String openAllAction = "openAll";
    public final static String saveAllAction = "saveAll";
    public final static String openPreviousFileAction = "openPrevious";
    public final static String saveJSXAction = "saveJSX";
    public final static String exitAction = "exit";
    public final static String aboutAction = "about";
    public final static String closeAction = "close";
    public final static String editAction = "editdata";
    static Category senecalog;
    static Category structgenlog;
    static boolean debug = false;
    public static final ImageIcon logo_512x512 = getIcon("/images/senecaicon512x512.png");
    private static final Logger logger = Logger.getLogger(Seneca.class);
    SenecaComponentFactory componentFactory;
    static TreeViewComponentFactory treeFactory;
    public static final ImageIcon propsIcon = getIcon("/images/tree/atomProps.gif");
    public static final ImageIcon folderIcon = getIcon("/images/tree/openFolder.gif");
    public static final ImageIcon generalSettingsIcon = getIcon("/images/tree/config.gif");
    public static final ImageIcon judgesIcon = getIcon("/images/tree/justiceHammer.gif");
    public static final ImageIcon simulationIcon = getIcon("/images/tree/simulation1.gif");
    public static final ImageIcon spectrumIcon = getIcon("/images/tree/1Dspect.gif");
    public static final ImageIcon leafConfigIcon = getIcon("/images/tree/settings.gif");


    private Seneca() {
        super("Seneca");
        setupAppDataDirectory();
        setupLogging();

        componentFactory = SenecaComponentFactory.getInstance();
        treeFactory = TreeViewComponentFactory.getInstance();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(178, 200, 217));
//        //122, 197, 205
////        //setUpFrontFrame();
//        JEditorPane jEditorPane = new JEditorPane();
//        jEditorPane.setEditable(false);
//
//        try {
//            jEditorPane.setContentType("text/html");
//            jEditorPane.setPage(new File("/Users/kalai/Desktop/index.html").toURI().toURL());
//        } catch (Exception e) {
//            e.printStackTrace();
//            jEditorPane.setContentType("text/html");
//            jEditorPane.setText("<html>Could not load http://www.oreilly.com </html>");
//        }
//        JScrollPane scrollPane = new JScrollPane(jEditorPane);
//        getContentPane().add(scrollPane, BorderLayout.CENTER);


        setupLookAndFeel();
        setupDesktop();
        setupMenubar();
        setupWindowBehavior();
        decorateWindow();

        setVisible(true);

        senecaDatasets.addChangeListener(this);
        pack();
        setStatus("No dataset available", 1);
        this.setVisible(true);
    }

    private void setupAppDataDirectory() {
        System.setProperty("lucene.root", FilePreference.getOSSystemDataRoot().getAbsolutePath());
        System.setProperty("userApp.root", FilePreference.getOSAppDataRoot().getAbsolutePath() + File.separator + "seneca" + File.separator);
        logger.info("User app directory set: " + FilePreference.getOSAppDataRoot().getAbsolutePath() + File.separator + "seneca" + File.separator);
    }

    private void setupLogging() {
        try {
            Properties loggingProps = new Properties();
            loggingProps.load(this.getClass().getClassLoader().getResourceAsStream("properties/logging.properties"));
            PropertyConfigurator.configure(loggingProps);
        } catch (Exception exc) {
            System.err.println("Could not setup file logging");
            logger.error("Could not setup logging");
        }

    }

    private void setUpFrontFrame() {
//        String url = "http://sourceforge.net/projects/seneca/";
//        JEditorPane htmlPane = null;
//        try {
//            htmlPane = new JEditorPane(url);
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        htmlPane.setEditable(false);
//        getContentPane().add(new JScrollPane(htmlPane));

        JTextPane textPane = new JTextPane(); // creates an empty text pane
        textPane.setContentType("html"); // lets Java know it will be HTML
        // textPane.setText("<span style='font-size: 20pt'>Big</span>");  // sets its text
        textPane.setText("<iframe width='560' height='315' src='http://www.youtube.com/watch?v=kJgsnXjXFGc' frameborder='0' allowfullscreen></iframe>");//embeded link
        getContentPane().add(new JScrollPane(textPane));


    }

    private static class InstanceHolder {

        private static final Seneca INSTANCE = new Seneca();
    }

    public static Seneca getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private void setupDesktop() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        getContentPane().setPreferredSize(
                new Dimension((int) (screenDim.width * 0.8),
                        (int) (screenDim.height * 0.8)));
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exc) {
            System.err.println("Error loading L&F: " + exc);
            logger.error("Error loading L&F: " + exc.getMessage());
        }
    }

    private void decorateWindow() {
        getContentPane().add(componentFactory.createToolbar(),
                BorderLayout.NORTH);
        statuspanel = makeStatusPanel();
        getContentPane().add(statuspanel, BorderLayout.SOUTH);
    }

    private void setupWindowBehavior() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            
            public void windowClosing(WindowEvent e) {
                trySystemExit();
            }
        });
    }

    private void setupMenubar() {
        menuBar = componentFactory.createMenubar();
        setJMenuBar(menuBar);
    }

    private JPanel makeStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));
        panel.setPreferredSize(new Dimension(640, 30));
        status1 = new JLabel();
        status1.setPreferredSize(new Dimension(100, 100));
        status1.setBorder(new BevelBorder(BevelBorder.LOWERED));
        status1.setHorizontalAlignment(SwingConstants.CENTER);
        status2 = new JLabel();
        status2.setPreferredSize(new Dimension(100, 100));
        status2.setBorder(new BevelBorder(BevelBorder.LOWERED));
        status2.setHorizontalAlignment(SwingConstants.CENTER);
        status3 = new JLabel();
        status3.setPreferredSize(new Dimension(100, 100));
        status3.setBorder(new BevelBorder(BevelBorder.LOWERED));
        status3.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(status1);
        panel.add(status2);
        panel.add(status3);
        return panel;
    }

    public void setStatus(String s, int i) {
        switch (i) {
            case 1:
                status1.setText(s);
                break;
            case 2:
                status2.setText(s);
                break;
            case 3:
                status3.setText(s);
                break;
        }
    }

    public void trySystemExit() {
        String title = componentFactory.getResourceString("ExitTitle");
        int lineNum = (Integer.valueOf(componentFactory.getResourceString("ExitLines"))).intValue();
        String msg = "";
        for (int f = 0; f < lineNum; f++) {
            msg += componentFactory.getResourceString("ExitMsg" + (f + 1))
                    + "\n";
        }
        int answer = JOptionPane.showConfirmDialog(this, msg, title,
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            new SenecaParameters().setSenecaCurrentNewDataFileLocation("");
            this.setVisible(false);
            this.dispose();
            System.exit(0);
        }
    }

    private void checkIfLuceneDirExists() {

    }

    public int shouldCloseCurrentDataSet() {
        String title = componentFactory.getResourceString("ExitTitle");
        int lineNum = (Integer.valueOf(componentFactory.getResourceString("ExitLines"))).intValue();
        String msg = "";
        for (int f = 0; f < lineNum; f++) {
            msg += componentFactory.getResourceString("ExitMsg" + (f + 2))
                    + "\n";
        }
        int answer = JOptionPane.showConfirmDialog(this, msg, title,
                JOptionPane.YES_NO_OPTION);
        return answer;
    }

    /**
     * Prompts for a directory and returns the name. This is for example used if the standard
     * location for server files is null and needs to be set for the first time.
     */
    public static String getDirectory() {
        File file = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showDialog(null,
                "Select desired config file location");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            if (!file.isDirectory()) {
                file = file.getParentFile();
            }
            logger.debug(file.getAbsolutePath());
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * Opens the content of the sml file
     *
     * @param file
     */
    public void openFile(File file) {
        SpecMLReader smlr = new SpecMLReader(file, false);

        SenecaDataset sd = smlr.getSenecaDataset();
        if (sd == null) {
            System.err.println("There was a fatal problem while trying to import SenecaDataset "
                    + file.getName());
            logger.error("There was a fatal problem while trying to import SenecaDataset " + file.getName());
            return;
        }
        logger.info("Importing Seneca dataset " + file + " done.");
        senecaDatasets.add(sd);
        System.out.println("senecadatasets suze: " + senecaDatasets.size());
        this.sd = sd;
        createTreeComponentsForSenecaDataset();

    }

    public void createTreeComponentsForSenecaDataset() {
        datasetPane = treeFactory.loadTreeViewSettings(this.sd, false);
        datasetPane.setVisible(true);
        getContentPane().add(datasetPane);
    }

    private void createNewDataSetFrame() {
        SenecaDatasetFrame sdf = new SenecaDatasetFrame(senecaDatasets.getCurrentDataset());
        getContentPane().add(sdf);
        sdf.toFront();
        try {
            sdf.setVisible(true);
            sdf.setSelected(true);
        } catch (java.beans.PropertyVetoException pvexc) {
            System.err.println("Problem activating new JInternalFrame");
            logger.info("Problem activating new JInternalFrame");
            logger.error(pvexc.getMessage());
        }
    }

    
    public void stateChanged(ChangeEvent e) {
        SenecaDatasetCollection sdc = (SenecaDatasetCollection) e.getSource();
        JMenu jma = menuBar.getMenu(menuBar.getMenuCount() - 2);
        JMenu jmb = (JMenu) (jma.getItem(0));
        jmb.removeAll();
        if (sdc.getCurrentDataset() != null) {
            setStatus(sdc.getCurrentDataset().getName(), 1);
            if (sdc.getCurrentDataset().getMolecularFormula() != null) {
                setStatus(
                        "<html>"
                                + MolecularFormulaManipulator.getHTML(sdc.getCurrentDataset().getMolecularFormula())
                                + "</html>", 2);
            } else {
                /*
                * TODO create a method to initialize all the tabs to insert the spectra
                * manually
                */

                setStatus("<html>"
                        + MolecularFormulaManipulator.getHTML(sdc.getCurrentDataset().getMolecularFormula())
                        + "</html>", 2);

            }
        } else {
            setStatus("No dataset loaded", 1);
            setStatus("", 2);

        }

    }

    public void addChangeListener(ChangeListener x) {
        changeListeners.add(ChangeListener.class, x);
        // bring it up to date with current state
        x.stateChanged(new ChangeEvent(this));
    }

    public void removeChangeListener(ChangeListener x) {
        changeListeners.remove(ChangeListener.class, x);
    }

    protected void fireChange() {
        // Create the event:
        ChangeEvent c = new ChangeEvent(this);
        // Get the listener list
        Object[] listeners = changeListeners.getListenerList();
        // Process the listeners last to first
        // List is in pairs, Class and instance
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                ChangeListener cl = (ChangeListener) listeners[i + 1];
                cl.stateChanged(c);
            }
        }
    }

    public static ImageIcon getIcon(String image) {

        java.net.URL imageURL = Seneca.class.getResource(image);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            return null;
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static TreeViewComponentFactory getTreeFactory() {
        return treeFactory;
    }

    public JSplitPane getDatasetPane() {
        return datasetPane;
    }

    public SenecaDatasetCollection getSenecaDatasets() {
        return senecaDatasets;
    }

    public SenecaDataset getCurrentDataset() {
        return this.sd;
    }

    public void setCurrentDataset(SenecaDataset sd) {
        this.sd = sd;
    }

    public void setDatasetPane(JSplitPane pane) {
        this.datasetPane = pane;
    }

}
