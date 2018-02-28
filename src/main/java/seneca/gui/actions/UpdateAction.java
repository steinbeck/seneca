/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui.actions;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYSeriesLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.AtomContainer;
import seneca.compute.Compute;
import seneca.engine.StructureGeneratorServer;
import seneca.gui.AbstractPanel;
import seneca.gui.ChartPanel;
import seneca.gui.StructureImageGenerator;
import seneca.judges.ScoreSummary;
import seneca.structgen.StructureGeneratorStatus;
import seneca.structgen.annealinglog.AnnealingDataSource;
import seneca.structgen.annealinglog.CommonAnnealingLog;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author kalai
 */
public class UpdateAction extends AbstractAction {

    public static final Logger logger = Logger.getLogger(UpdateAction.class);
    private AbstractPanel panel;
    private ChartPanel chartPanel = null;
    public StructureGeneratorStatus[] structureGeneratorStatus = null;
    private int currentServer = 0;
    private StructureImageGenerator imageGenerator = null;
    private Border border = null;
    private AnnealingDataSource dataSource = null;
    private JFreeChart chart = null;
    private XYLineAndShapeRenderer renderer = null;
    private JScrollPane scrollpane = null;
    private static final long serialVersionUID = 1L;
    private List stoppedServers = null;
    private boolean cancelUpdateTaskScheduled = false;
    CommonAnnealingLog annealingLog = null;
    String title;
    IAtomContainer structure;
    DecimalFormat decimalFormat = new DecimalFormat("##.###");

    public UpdateAction(AbstractPanel panel) {
        super("updateTableFrame");
        this.panel = panel;
        this.border = BorderFactory.createEtchedBorder();
        this.dataSource = panel.dataSource;
        this.scrollpane = panel.serverTablescrollpane;
        this.chart = panel.chart;
        this.chartPanel = panel.chartPanel;
        imageGenerator = new StructureImageGenerator();
        this.stoppedServers = new ArrayList();
        renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setLegendItemLabelGenerator(
                new StandardXYSeriesLabelGenerator() {

                    
                    public String generateLabel(XYDataset dataset, int series) {
                        String label = "local " + series;
                        return label;
                    }
                });
    }

    //    
//    public void actionPerformed(ActionEvent e) {
//        //panel.tableUpdateTimer.stop();
//        if (panel.sgServerProcesses.size() > 0) {
//            StructureGeneratorServer server = (StructureGeneratorServer) panel.sgServerProcesses.get(currentServer);
//            this.structureGeneratorStatus = panel.structureGeneratorStatus;
//            this.structureGeneratorStatus[currentServer] = (StructureGeneratorStatus) server.getTaskStatus();
//            if (structureGeneratorStatus[currentServer] != null) {
//                doAllTheUpdatesUsingDataFrom(server);
//            }
//            currentServer++;
//            if (currentServer == panel.sgServerProcesses.size()) {
//                currentServer = 0;
//            }
//        }
//        scrollpane.repaint();
//       // panel.tableUpdateTimer.start();
//    }
    
    public void actionPerformed(ActionEvent e) {

        Thread queryThread = new Thread() {

            
            public void run() {
                try {
                    if (panel.sgServerProcesses.size() > 0) {
                        StructureGeneratorServer server = (StructureGeneratorServer) panel.sgServerProcesses.get(currentServer);
                        structureGeneratorStatus = panel.structureGeneratorStatus;
                        structureGeneratorStatus[currentServer] = (StructureGeneratorStatus) server.getTaskStatus();
                        if (structureGeneratorStatus[currentServer] != null) {
                            doAllTheUpdatesUsingDataFrom(server);
                        }
                        currentServer++;
                        if (currentServer == panel.sgServerProcesses.size()) {
                            currentServer = 0;
                        }
                    }
                    scrollpane.repaint();
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }
        };
        queryThread.start();
    }

    private void doAllTheUpdatesUsingDataFrom(Compute server) {
        addDataFromAllServersToPlot();
        updateChart();
        updateCurrentStructureandScore(server);
        checkShouldStopUpdating();
    }

    private void updateCurrentStructureandScore(Compute server) {
        structure = (IAtomContainer) ((StructureGeneratorStatus) server.getTaskStatus()).bestStructure;
        if (structure == null) {
            title = "No structure available";
            panel.reportPane.setText("Waiting to report..");
        } else {
            if (checkConnectivityAndGetStructureImage()) {
                setObtainedImageInPanel();
                updateScoreStatus(server);
            }
        }

    }

    private boolean checkConnectivityAndGetStructureImage() {
        boolean success = true;
        if (ConnectivityChecker.isConnected(structure)) {
            IAtomContainer structureToDraw = new AtomContainer(structure);
            if (structureGeneratorStatus[currentServer].bestStructureImage == null) {
                try {
                    structureGeneratorStatus[currentServer].bestStructureImage = imageGenerator.generateStructureImage(structureToDraw, new Dimension(220, 220));
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                    panel.tableUpdateTimer.restart();
                }
            }
        } else {
            //panel.reportPane.setText("Disconnected molecule not displayed");
            System.out.println("Disconnected molecule not displayed");
            logger.info("Disconnected molecule not displayed");
            success = false;
        }
        return success;
    }


    private void setObtainedImageInPanel() {
        title = "Best structure of no." + (currentServer + 1);
        if (structureGeneratorStatus[currentServer].bestStructureImage != null) {
            panel.structurePanel.setIcon(new ImageIcon(structureGeneratorStatus[currentServer].bestStructureImage));
            panel.reportStructure.setBorder(BorderFactory.createTitledBorder(border, title));
        }

    }

    private void updateScoreStatus(Compute server) {
        StringBuilder reportText = new StringBuilder();
        ScoreSummary scoreSummary = structureGeneratorStatus[currentServer].bestEvaluation;
        reportText.append("<html>\n");
        reportText.append("<b>Score: "
                + scoreSummary.score);
        if (scoreSummary.maxScore > 0) {
            reportText.append("/" + scoreSummary.maxScore);
            reportText.append("(Cost - " + decimalFormat.format(scoreSummary.costValue) + ")");
        }
        reportText.append("</b>");
        title = "Evaluation of no. " + (currentServer + 1);
        panel.reportScrollPane.setBorder(BorderFactory.createTitledBorder(border, title));
        reportText.append(((StructureGeneratorStatus) server.getTaskStatus()).bestEvaluation.description
                + "</html>\n");
        panel.reportPane.setText(reportText.toString());

    }

    private void addDataToPlot() {
        annealingLog = structureGeneratorStatus[currentServer].annealingLog;
        if (dataSource.getData() == null) {
            dataSource.init(panel.sgServerProcesses.size());
        }

        dataSource.addData(annealingLog, currentServer);
    }

    private void addDataFromAllServersToPlot() {
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                if (panel.sgServerProcesses.size() == 1 || panel.table.getSelectedRows().length == 1) {
                    addDataToPlot();
                    return;
                }
                int rows[] = panel.table.getSelectedRows();
                int[] selectedRows = new int[panel.sgServerProcesses.size()];
                for (int f = 0; f < rows.length; f++) {
                    selectedRows[rows[f]] = 1;
                }
                for (int f = 0; f < panel.sgServerProcesses.size(); f++) {
                    if (selectedRows[f] == 1) { //&& !stoppedServers.contains(f)
                        Compute server = (Compute) panel.sgServerProcesses.get(f);
                        StructureGeneratorStatus status = (StructureGeneratorStatus) server.getTaskStatus();
                        annealingLog = status.annealingLog;
                        if (dataSource.getData() == null) {
                            dataSource.init(panel.sgServerProcesses.size());
                        }
                        dataSource.addData(annealingLog, f);
                    }
                }
            }
        });
    }

    private void updateChart() {
        chart = ChartFactory.createXYLineChart(
                "",
                panel.xAxisLabel,
                panel.yAxisLabel,
                dataSource,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);
        setupRendereringParameters();
        chartPanel.setChart(chart);
        chartPanel.repaint();
    }

    private void setupRendereringParameters() {

        XYPlot xyPlot = (XYPlot) chart.getPlot();
        ValueAxis axis = xyPlot.getDomainAxis();
        axis.setAutoRange(true);
        axis = xyPlot.getRangeAxis();
        axis.setRange(0, 1);
        xyPlot.setRenderer(renderer);
        LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.BOTTOM);
    }

    private void checkShouldStopUpdating() {
        if ((stoppedServers.size() != panel.sgServerProcesses.size()) && !stoppedServers.contains(currentServer)) {
            String status = this.structureGeneratorStatus[currentServer].status;
            if (status != null) {
                if (status.equalsIgnoreCase("Finished")
                        || status.equalsIgnoreCase("Stopped")) {
                    stoppedServers.add(currentServer);
                }
            }
        }
        if (stoppedServers.size() == panel.sgServerProcesses.size() && !cancelUpdateTaskScheduled) {
            cancelUpdateTaskScheduled = true;
            panel.getResetButton().setEnabled(true);
            panel.getShowCurrentResultButton().setEnabled(true);
            new Timer().schedule(new CancelUpdateTask(), 30000);
        }
    }

    class CancelUpdateTask extends TimerTask {

        
        public void run() {
            panel.tableUpdateTimer.stop();
        }
    }
}
