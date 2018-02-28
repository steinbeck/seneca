/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.structgen.annealinglog;

import org.apache.log4j.Category;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.xy.XYDataset;

/**
 * @author steinbeck @created September 9, 2001
 */
public class AnnealingDataSource extends AbstractDataset implements
        XYDataset {


    //  double maxScore = 0;
    CommonAnnealingLog annealingLog = null;
    //        int SCORES = 1;
//        int KT = 0;
//        int displayType = SCORES;
//        double maxX = 0;
//        int maxSeries = 0;
    int maxItemCount = 0;
    String seriesName = "";

    /**
     * Constructor for the AnnealingDataSource object
     */
    public AnnealingDataSource() {
    }

    /**
     * Sets the Data attribute of the AnnealingDataSource object
     *
     * @param newAl The new Data value
     */
    public void setData(CommonAnnealingLog newAl) {
        annealingLog = newAl;
    }

    /**
     * Gets the XValue attribute of the AnnealingDataSource object
     *
     * @param series Description of Parameter
     * @param item   Description of Parameter
     * @return The XValue value
     */
    
    public double getXValue(int series, int item) {
        Double[] entry;
        if (annealingLog == null) {
            return new Double(1);
        }

//                if (series == annealingLog.getSeriesCount()) {
//                        entry = annealingLog.getEntry(maxSeries, item);
//                        return entry[0];
//                }

        entry = annealingLog.getEntry(series, item);
//                if (entry[0].doubleValue() > maxX) {
//                        maxX = entry[0].doubleValue();
//                        maxSeries = series;
//                }

        return entry[0];
    }

    
    public Number getY(int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the YValue attribute of the AnnealingDataSource object
     *
     * @param series Description of Parameter
     * @param item   Description of Parameter
     * @return The YValue value
     */
    
    public double getYValue(int series, int item) {
        if (annealingLog == null) {
            return new Double(1);
        }
//                if (series == annealingLog.getSeriesCount()) {
////                        return new Double(maxScore);
//                          return new Double(1);
//                }
        Double[] entry = annealingLog.getEntry(series, item);
//                return entry[1 + displayType];
        return entry[2];
    }

    /**
     * Gets the SeriesCount attribute of the AnnealingDataSource object
     *
     * @return The SeriesCount value
     */
    
    public int getSeriesCount() {
        if (annealingLog == null) {
            return 1;
        }
//                if (displayType == SCORES) {
//                        return annealingLog.getSeriesCount() + 1;
//                }
        return annealingLog.getSeriesCount();
    }

    public String getSeriesName(int series) {
        if (annealingLog == null) {
            return "no data";
        }
        if (series == annealingLog.getSeriesCount()) {
            return "max. Score";
        } else {
            return "host " + (series + 1);
        }
    }

    
    public Comparable getSeriesKey(int series) {
        Integer ret = new Integer(series);
        return ret;
    }

    
    public int indexOf(Comparable comparable) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the Data attribute of the AnnealingDataSource object
     *
     * @return The Data value
     */
    public CommonAnnealingLog getData() {
        return annealingLog;
    }

    
    public DomainOrder getDomainOrder() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the ItemCount attribute of the AnnealingDataSource object
     *
     * @param series Description of Parameter
     * @return The ItemCount value
     */
    
    public int getItemCount(int series) {
        int ic = 0;
        if (annealingLog == null) {
            return 1;
        }
        if (series == annealingLog.getSeriesCount()) {
            return maxItemCount;
        }
        ic = annealingLog.getTotalEntriesCountIn(series);
        if (ic > maxItemCount) {
            maxItemCount = ic;
        }
        return annealingLog.getTotalEntriesCountIn(series);
    }

    
    public Number getX(int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Gets the DisplayType attribute of the AnnealingDataSource object
     *
     * @return The DisplayType value
     */
//        public int getDisplayType() {
//                return displayType;
//        }

    /**
     * Add the Annealing Data for a given host, identified by number series
     *
     * @param log    The feature to be added to the Data attribute
     * @param series The feature to be added to the Data attribute
     */
    public void addData(CommonAnnealingLog log, int series) {
        int totalEntryCount = log.getTotalEntriesCountIn(0);
        Double[] entry;
        for (int f = 0; f < totalEntryCount; f++) {
            entry = log.getEntry(0, f);
            annealingLog.addEntry(series, annealingLog.getTotalEntriesCountIn(series), entry);
        }
    }

    /**
     * Description of the Method
     *
     * @param size Description of Parameter
     */
    public void init(int size) {
        annealingLog = new CommonAnnealingLog(size, 2);
    }

    private boolean isInstance(Object o, String className) {
        try {
            Class clazz = Class.forName(className);
            return clazz.isInstance(o);
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }


    /**
     * Description of the Method
     */
    public void logResults() {
        Category annealinglog = Category.getInstance("seneca.structgen.StochasticGenerator.annealing.log");
        Double[] entry;
        String string;
        int maxItemCount = 0;

        for (int x = 0; x < annealingLog.getSeriesCount(); x++) {
            if (annealingLog.getTotalEntriesCountIn(x) > maxItemCount) {
                maxItemCount = annealingLog.getTotalEntriesCountIn(x);
            }
        }

        Double[][] data = new Double[maxItemCount][annealingLog.getSeriesCount() + 1];

        for (int x = 0; x < annealingLog.getSeriesCount(); x++) {
            for (int y = 0; y < annealingLog.getTotalEntriesCountIn(x); y++) {
                entry = annealingLog.getEntry(x, y);
                data[y][0] = entry[0];
                data[y][x + 1] = entry[2];
            }
        }
        string = "Iteration; ";
        for (int x = 0; x < annealingLog.getSeriesCount(); x++) {
            string += "Server " + (x + 1) + "; ";
        }
        annealinglog.info(string + "\n");

        for (int x = 0; x < maxItemCount; x++) {
            string = "";
            for (int y = 0; y < annealingLog.getSeriesCount() + 1; y++) {
                if (data[x][y] != null) {
                    string += data[x][y];
                }
                string += "; ";

            }
            annealinglog.info(string + "\n");
        }

    }

//        public void setMaxScore(double score) {
//                this.maxScore = score;
//        }
}
