/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import seneca.core.SenecaDataset;
import seneca.core.ServerConstants;
import seneca.engine.StructureGeneratorServer;
import seneca.gui.actions.ResetSelectedAction;
import seneca.gui.actions.StartStructureGenerationAction;
import seneca.gui.actions.SummariseElucidatedResultsAction;
import seneca.gui.actions.UpdateAction;
import seneca.gui.configurators.JudgeConfiguratorListener;
import seneca.gui.tables.StructureGenerationServerTableModel;
import seneca.judges.Judge;
import seneca.structgen.StructureGenerator;
import seneca.structgen.StructureGeneratorStatus;
import seneca.structgen.annealinglog.AnnealingDataSource;
import seneca.structgen.ea.EAStochasticGenerator;
import seneca.structgen.sa.adaptive.ASAStochasticGenerator;
import seneca.structgen.sa.regular.SAStochasticGenerator;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author kalai
 */
public class AbstractPanel extends JPanel implements ChangeListener, JudgeConfiguratorListener {

    private static final long serialVersionUID = 1L;
    public JTable table;
    public JScrollPane serverTablescrollpane;
    public JScrollPane reportScrollPane;
    public JPanel reportStructure;
    public Box northBox;
    public Box southBox;
    public Box centerBox;
    public Box bottomCenterBox;
    public Box topCenterBox;
    public JLabel structurePanel;
    JPanel algorithmsButtonsPanel;
    JPanel judgesCheckBoxPanel;
    JPanel handleSelectedButtonsPanel;
    JPanel resultButtonsPanel;
    public JEditorPane reportPane;

    public JButton startLocalServerButton;
    public JButton startStructureGeneratorButton;
    public JButton stopSelectedButton;
    JButton showCurrentResultButton;
    JButton logAnnealingDataButton;
    JButton resetButton;

    JRadioButton simulatedSA;
    JRadioButton adaptiveSimulatedSA;
    JRadioButton evolutionaryAlgorithm;

    public javax.swing.Timer tableUpdateTimer;
    public java.util.List sgServerProcesses;
    public ButtonGroup buttonGroup;
    public ChartPanel chartPanel;
    public JFreeChart chart;
    public AnnealingDataSource dataSource;
    public double maxScore = 0;
    public StructureGeneratorStatus[] structureGeneratorStatus = null;
    public Border border = null;
    public SenecaDataset sd;
    public StructureGenerator structureGenerator = null;
    public ServerConstants severConstants = null;
    public String xAxisLabel = "Generation";
    public String yAxisLabel = "Fitness";
    private static final Logger logger = Logger.getLogger(AbstractPanel.class);
    private java.util.List<JCheckBox> judgeCheckBoxes = null;

    public AbstractPanel(SenecaDataset sd) {
        super();
        this.sd = sd;
        setUpServerTable();
        setCellRendering();
        setJudgesButtonPanel();
        serverTablescrollpane = new JScrollPane(table);
        serverTablescrollpane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        setLayout(new BorderLayout());
        northBox = new Box(BoxLayout.Y_AXIS);
        southBox = new Box(BoxLayout.X_AXIS);
        centerBox = new Box(BoxLayout.Y_AXIS);
        topCenterBox = new Box(BoxLayout.X_AXIS);
        bottomCenterBox = new Box(BoxLayout.X_AXIS);
        border = BorderFactory.createEtchedBorder();

        String title = "Select a algorithm to run";
        //radio buttons for algorithms
        simulatedSA = new JRadioButton("Simulated annealing");
        simulatedSA.setForeground(Color.blue);
        simulatedSA.setToolTipText("Select to run simulated annealing with annealing parameters set by you");

        adaptiveSimulatedSA = new JRadioButton("Adaptive Simulated annealing");
        adaptiveSimulatedSA.setForeground(Color.blue);
        adaptiveSimulatedSA.setToolTipText("Select to run auto-configured simulated annealing. Needs number of step though !");

        evolutionaryAlgorithm = new JRadioButton("Evolutionary algorithm");
        evolutionaryAlgorithm.setForeground(Color.blue);
        evolutionaryAlgorithm.setToolTipText("Select to run evolutionary algorithm");
        evolutionaryAlgorithm.setSelected(true);
        structureGenerator = new EAStochasticGenerator();

        simulatedSA.addActionListener(new ActionListener() {

            
            public void actionPerformed(ActionEvent e) {
                structureGenerator = new SAStochasticGenerator();
                xAxisLabel = "Iteration";
                yAxisLabel = "Score";
            }
        });
        adaptiveSimulatedSA.addActionListener(new ActionListener() {

            
            public void actionPerformed(ActionEvent e) {
                structureGenerator = new ASAStochasticGenerator();
                xAxisLabel = "Iteration";
                yAxisLabel = "Score";
            }
        });
        evolutionaryAlgorithm.addActionListener(new ActionListener() {

            
            public void actionPerformed(ActionEvent e) {
                structureGenerator = new EAStochasticGenerator();
                xAxisLabel = "Generation";
                yAxisLabel = "Fitness";
            }
        });
        buttonGroup = new ButtonGroup();
        buttonGroup.add(simulatedSA);
        buttonGroup.add(adaptiveSimulatedSA);
        buttonGroup.add(evolutionaryAlgorithm);


        algorithmsButtonsPanel = new JPanel(new GridLayout(1, 2));
        algorithmsButtonsPanel.add(adaptiveSimulatedSA);
        algorithmsButtonsPanel.add(simulatedSA);
        algorithmsButtonsPanel.add(evolutionaryAlgorithm);
        algorithmsButtonsPanel.setPreferredSize(new Dimension(100, 50));
        algorithmsButtonsPanel.setBorder(BorderFactory.createTitledBorder(border, title));
//        algorithmsButtonsPanel.setBackground(Color.lightGray);  //new Color(181, 181, 169)
//        algorithmsButtonsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), title, TitledBorder.RIGHT, TitledBorder.RIGHT, new Font("times new roman",Font.PLAIN,16), Color.BLACK));

        // end of algo buttons

        title = "Selections";
        handleSelectedButtonsPanel = new JPanel(new GridLayout(3, 1));
        handleSelectedButtonsPanel.setBorder(BorderFactory.createTitledBorder(border, title));
//        handleSelectedButtonsPanel.setBackground(new Color(178,200,217));
//        handleSelectedButtonsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(178,200,217)), title, TitledBorder.LEFT, TitledBorder.LEFT, new Font("times new roman",Font.PLAIN,16), Color.BLACK));

        startLocalServerButton = new JButton("Start Local Server");
        startLocalServerButton.setToolTipText("Click me to start your local server to run the simulation");
        startLocalServerButton.addActionListener(new StartLocalServerAction());
        startLocalServerButton.doClick();

        startStructureGeneratorButton = new JButton("Start structure generation");
        startStructureGeneratorButton.setToolTipText("Before clicking me you need to start local server first. "
                + " Click on the started server (yellow line) and click me to start structure generation.");
        startStructureGeneratorButton.addActionListener(new StartStructureGenerationAction(this));
        stopSelectedButton = new JButton("Stop structure generation");
        stopSelectedButton.setToolTipText("Click me to stop the current structure generation");
        stopSelectedButton.addActionListener(new stopSelectedAction());
        stopSelectedButton.setEnabled(false);

        handleSelectedButtonsPanel.add(startLocalServerButton);
        handleSelectedButtonsPanel.add(startStructureGeneratorButton);
        handleSelectedButtonsPanel.add(stopSelectedButton);

        if (!this.sd.dataConsistency.isDatasetCompleteForStructureElucidation()) {
            for (Component button : handleSelectedButtonsPanel.getComponents()) {
                button.setEnabled(false);
            }
        }

        title = "Result";
        resultButtonsPanel = new JPanel(new GridLayout(3, 1));
        resultButtonsPanel.setBorder(BorderFactory.createTitledBorder(border, title));
//        resultButtonsPanel.setBackground(new Color(178,200,217));
//        resultButtonsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(178,200,217)), title, TitledBorder.LEFT, TitledBorder.LEFT, new Font("times new roman",Font.PLAIN,16), Color.BLACK));

        showCurrentResultButton = new JButton("Summarize Result");
        showCurrentResultButton.addActionListener(new SummariseElucidatedResultsAction(this));
        showCurrentResultButton.setToolTipText("Click to see high scoring structures");
        logAnnealingDataButton = new JButton("Show Annealing/Evolution log Data");
        logAnnealingDataButton.addActionListener(new LogAnnealingDataAction());
        logAnnealingDataButton.setToolTipText("Click to go to Logs directory");
        resetButton = new JButton("Reset simulation");
        resetButton.addActionListener(new ResetSelectedAction(this));
        resetButton.setToolTipText("Click to reset the current simulation and judges");
        showCurrentResultButton.setEnabled(false);
        resetButton.setEnabled(false);

        resultButtonsPanel.add(showCurrentResultButton);
        resultButtonsPanel.add(logAnnealingDataButton);
        resultButtonsPanel.add(resetButton);

        southBox.add(handleSelectedButtonsPanel);
        southBox.add(resultButtonsPanel);

        serverTablescrollpane.setPreferredSize(new Dimension(400, 200)); // 400, 200
        reportPane = new JEditorPane();
        reportPane.setEditable(false);
        reportPane.setEditorKit(new HTMLEditorKit());
        reportScrollPane = new JScrollPane(reportPane);
        reportScrollPane.setPreferredSize(new Dimension(200, 200)); //200, 300


        reportStructure = new JPanel();
        title = "Current available structure";
        reportStructure.setPreferredSize(new Dimension(200, 200)); //200, 300
        reportStructure.setBorder(BorderFactory.createTitledBorder(border,
                title));
        reportStructure.setBackground(Color.WHITE);
        structurePanel = new JLabel();
        reportStructure.add(structurePanel, BorderLayout.CENTER);

        title = "Current Structure Evaluation";
        reportScrollPane.setBorder(BorderFactory.createTitledBorder(border,
                title));
        reportScrollPane.setBackground(Color.white);

        title = "Fitness progress";
        chartPanel = getAnnealingViewPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder(border, title));
        chartPanel.setPreferredSize(new Dimension(400, 200)); // 400,200
        topCenterBox.add(serverTablescrollpane);
        topCenterBox.add(chartPanel);
        centerBox.add(topCenterBox);

        bottomCenterBox.add(reportStructure);
        bottomCenterBox.add(reportScrollPane);
        centerBox.add(bottomCenterBox);

        if (sd.judges.size() != 0) {
            northBox.add(judgesCheckBoxPanel);
        }
        northBox.add(algorithmsButtonsPanel);
        add("Center", centerBox);
        add("North", northBox);
        add("South", southBox);

        tableUpdateTimer = new javax.swing.Timer(1000, new UpdateAction(this));
        serverTablescrollpane.repaint();
        this.sd.addChangeListener(this);
        this.sd.dataConsistency.setJudgeConfiguratorListener(this);
    }

    public void setUpServerTable() {
        sgServerProcesses = new ArrayList();
        table = new JTable(new StructureGenerationServerTableModel(sgServerProcesses)) {

            private static final long serialVersionUID = 1L;

            
            public Dimension getPreferredSize() {
                return (new Dimension(serverTablescrollpane.getSize().width,
                        (getRowCount() + 2) * getRowHeight()));
            }
        };
        FontMetrics fm = getFontMetrics(table.getFont());
        for (int f = 0; f < table.getColumnCount(); f++) {
            TableColumn column = table.getColumn(table.getColumnName(f));
            column.setWidth((int) (fm.stringWidth(table.getColumnName(f)) * 1.2));
            DefaultTableCellRenderer columnRenderer = new DefaultTableCellRenderer();
            columnRenderer.setBackground(Color.yellow);
        }
    }

    /**
     * @since
     */
    final void setCellRendering() {
        JLabel label = new JLabel();
        TableColumn column;
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        for (int f = 0; f < table.getColumnCount(); f++) {
            column = table.getColumn(table.getColumnName(f));
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            DefaultTableCellRenderer columnRenderer = new DefaultTableCellRenderer();
            columnRenderer.setBackground(Color.yellow);
            column.setCellRenderer(columnRenderer);
        }
    }

    private ChartPanel getAnnealingViewPanel() {

        dataSource = new AnnealingDataSource();
        chart = ChartFactory.createXYLineChart(
                "",
                xAxisLabel,
                yAxisLabel,
                dataSource,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
        setupRenderer();
        chartPanel = new ChartPanel(chart);
        return chartPanel;

    }

    public void setupRenderer() {

        XYPlot plot = (XYPlot) chart.getPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis = plot.getRangeAxis();
        axis.setRange(0, 1);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(renderer);
    }

    public void setJudgesButtonPanel() {
        judgesCheckBoxPanel = new JPanel(new GridLayout(1, this.sd.judges.size()));
        judgesCheckBoxPanel.setPreferredSize(new Dimension(100, 50));
        judgesCheckBoxPanel.setBorder(BorderFactory.createTitledBorder(border, "Selected judges"));
        judgesCheckBoxPanel.setBackground(Color.white);
        judgeCheckBoxes = new ArrayList<JCheckBox>();
        for (int g = 0; g < this.sd.judges.size(); g++) {
            Judge judge = ((Judge) this.sd.judges.get(g));
            if (Seneca.treeFactory.getComponentByName(judge.getName()) != null) {
                JCheckBox newJudgeCheckBox = new JCheckBox(judge.getName());
                newJudgeCheckBox.setSelected(judge.getEnabled());
                newJudgeCheckBox.setForeground(Color.blue);
                newJudgeCheckBox.addActionListener(new JudgeSelectedAction());
                judgeCheckBoxes.add(newJudgeCheckBox);
                judgesCheckBoxPanel.add(newJudgeCheckBox);
            }
        }
    }

    
    public void stateChanged(ChangeEvent e) {
        for (Component button : handleSelectedButtonsPanel.getComponents()) {
            button.setEnabled(this.sd.dataConsistency.isDatasetCompleteForStructureElucidation());
        }
        this.stopSelectedButton.setEnabled(false);
    }

    
    public void judgeConfigurationChanged() {
        stateChanged(new ChangeEvent(sd));
        judgeDataChanged();
    }

    private void judgeDataChanged() {
        for (JCheckBox box : judgeCheckBoxes) {
            Judge judge = (Judge) sd.getJudge(box.getText());
            box.setSelected(judge.getEnabled());
        }
    }

    class stopSelectedAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        stopSelectedAction() {
            super("stopSelected");
        }

        
        public void actionPerformed(ActionEvent e) {

            int rows[] = table.getSelectedRows();
            int[] selrows = new int[sgServerProcesses.size()];
            for (int f = 0; f < rows.length; f++) {
                selrows[rows[f]] = 1;
            }
            StructureGeneratorServer server;

            for (int f = 0; f < sgServerProcesses.size(); f++) {
                if (selrows[f] == 1) {
                    server = (StructureGeneratorServer) sgServerProcesses.get(f);
                    try {
                        server.endTask();
                    } catch (Exception exc) {
                        logger.info(exc.getMessage());
                    }
                }
            }
            resetButton.setEnabled(true);
            showCurrentResultButton.setEnabled(true);
            // tableUpdateTimer.stop();
        }
    }

    /**
     * Description of the Class
     *
     * @author steinbeck @created September 9, 2001
     */
    class LogAnnealingDataAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        LogAnnealingDataAction() {
            super("LogAnnealingData");
        }

        
        public void actionPerformed(ActionEvent e) {
            //dataSource.logResults();
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(new File(System.getProperty("userApp.root")));
                } catch (IOException e1) {
                    logger.error("Cannot show log file directory to the user");
                }
            }
        }
    }

    /**
     * Description of the Class
     *
     * @author steinbeck @created September 9, 2001
     */
    class StartLocalServerAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        StartLocalServerAction() {
            super("StartLocal");

        }

        
        public void actionPerformed(ActionEvent e) {
            try {
                StructureGeneratorServer structureGeneratorServer = new StructureGeneratorServer(
                        "local" + sgServerProcesses.size());
                sgServerProcesses.add(structureGeneratorServer);
                updateSgs();
                System.out.println("Local server started");
                int lastIndex = sgServerProcesses.size() - 1;
                structureGeneratorStatus[structureGeneratorStatus.length - 1] = (StructureGeneratorStatus) ((StructureGeneratorServer) sgServerProcesses.get(lastIndex)).getTaskStatus();
                serverTablescrollpane.repaint();
            } catch (Exception exc) {
                logger.info("Local StoicGent service could not be created.");
                logger.error(exc.getMessage());
            }
            table.revalidate();
            table.setRowSelectionInterval(0, 0);
            serverTablescrollpane.revalidate();

        }
    }

    class JudgeSelectedAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        JudgeSelectedAction() {
            super("JudgeSelected");
        }

        
        public void actionPerformed(ActionEvent e) {
            AbstractButton button = (AbstractButton) e.getSource();
            Judge judge = (Judge) sd.getJudge(button.getText());
            judge.setEnabled(button.isSelected());
        }
    }

    void updateSgs() {
        StructureGeneratorStatus[] newSgs = new StructureGeneratorStatus[sgServerProcesses.size()];
        if (structureGeneratorStatus != null) {
            for (int f = 0; f < structureGeneratorStatus.length; f++) {
                newSgs[f] = structureGeneratorStatus[f];
            }
        }
        structureGeneratorStatus = newSgs;
    }

    public JButton getResetButton() {
        return this.resetButton;
    }

    public JButton getShowCurrentResultButton() {
        return this.showCurrentResultButton;
    }
}
