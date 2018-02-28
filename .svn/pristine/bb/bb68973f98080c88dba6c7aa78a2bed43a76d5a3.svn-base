/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author steinbeck @created September 9, 2001
 */
public class ChartPanel extends JComponent implements ChartChangeListener {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ChartPanel.class);
    /**
     * The chart that is contained within the panel.
     */
    JFreeChart chart;

    /**
     * Full constructor: returns a panel containing the specified chart.
     *
     * @param chart The chart to display in the panel.
     */
    public ChartPanel(JFreeChart chart) {
        this.chart = chart;
        this.chart.addChangeListener(this);
        setPreferredSize(new Dimension(400, 400));
    }

    /**
     * Returns a reference to the chart displayed in the panel.
     *
     * @param chart The new Chart value
     */
    public void setChart(JFreeChart chart) {
        this.chart = chart;
        this.chart.addChangeListener(this);
    }

    /**
     * Returns a reference to the chart displayed in the panel.
     *
     * @return The Chart value
     */
    public JFreeChart getChart() {
        return this.chart;
    }

    /**
     * Paints the component - this means drawing the chart to fill the entire component, but
     * allowing for the insets (which will be non-zero if a border has been set for this
     * component).
     *
     * @param g Description of Parameter
     */
    
    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        Dimension size = getSize();
        Insets insets = getInsets();
        Rectangle2D chartArea = new Rectangle2D.Double(insets.left,
                insets.top, size.getWidth() - insets.left - insets.right,
                size.getHeight() - insets.top - insets.bottom);
        try {
            chart.draw(g2, chartArea);
        } catch (Exception exc) {
            exc.printStackTrace();
            logger.info(exc.getMessage());
        }
    }

    /**
     * Receives notification of changes to the chart, and redraws the chart.
     *
     * @param event Description of Parameter
     */
    
    public void chartChanged(ChartChangeEvent event) {
        this.repaint();
    }
}
