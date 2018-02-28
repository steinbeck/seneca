/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui;

import org.apache.log4j.Logger;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.StructureIO;
import seneca.structgen.ea.Individual;
import seneca.structgen.ea.Population;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kalai
 */
public class PopulationTableFrame extends JFrame {

    private int numColumns = 26;
    private int numRows = 1;
    private int generation = 0;
    public JTable table;
    public JButton addButton;
    private StructureImageGenerator imageGenerator = new StructureImageGenerator();
    DefaultTableModel model;
    public Object[][] allData;
    public ImageIcon dummyIcon = getIcon("/images/chcosy.gif");
    JScrollPane scrollpane;
    private static final Logger logger = Logger.getLogger(PopulationTableFrame.class);

    public PopulationTableFrame() {
        allData = new Object[][]{};
        model = new DefaultTableModel(allData, getColumns()) {
            
            public Class getColumnClass(int col) {
                if (getRowCount() == 0) {
                    return Object.class;
                }
                if (col >= 2) {
                    return BufferedImage.class;
                }
                Object cellValue = getValueAt(0, col);
                return (cellValue != null ? cellValue.getClass() : Object.class);
            }

            
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(130);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(BufferedImage.class, new ImageRenderer());
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scrollpane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        getContentPane().setPreferredSize(
                new Dimension((int) (screenDim.width),
                        (int) (screenDim.height)));
        getContentPane().add(new JScrollPane(scrollpane));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setSize(800, 800);
        setVisible(true);
    }

    public static ImageIcon getIcon(String image) {

        java.net.URL imageURL = Seneca.class.getResource(image);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            return null;
        }
    }

    private String[] getColumns() {
        String[] columns = new String[numColumns];
        columns[0] = "Row";
        columns[1] = "gen";
        int count = 1;
        for (int i = 2; i < columns.length; i++) {
            columns[i] = "mol-" + count;
            count++;
        }
        return columns;
    }

    public void add(Population<Individual> population) {
        generation = population.getGeneration();
        List<BufferedImage> imageList = new ArrayList<BufferedImage>();
        for (int i = 0; i < population.size(); i++) {
            try {
                if (ConnectivityChecker.isConnected(population.get(i).getMolecule())) {
                    imageList.add(imageGenerator.generateStructureImage(population.get(i).getMolecule(), new Dimension(124, 124)));
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                continue;
            }
        }
        add(imageList);
    }

    private void add(List<BufferedImage> images) {
        Object[] data = new Object[images.size() + 2];
        data[0] = numRows;
        data[1] = generation;
        for (int i = 0; i < images.size(); i++) {
            data[i + 2] = images.get(i);
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(data);
        model.fireTableRowsInserted(model.getRowCount(), model.getRowCount());
        table.revalidate();
        this.repaint();
        numRows++;
    }

    public void addFirst(BufferedImage[] images) {
        Object[] data = new Object[images.length + 2];
        data[0] = numRows;
        data[1] = generation;
        for (int i = 0; i < images.length; i++) {
            data[i + 2] = images[i];
        }
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < data.length; j++) {
                allData[i][j] = data[j];
            }
        }
        numRows++;
    }

    private List<BufferedImage> loadAllImages() throws Exception {

        List<IAtomContainer> readSDF = StructureIO.readSDF("/Users/kalai/Desktop/polycarpolStrs.sdf");
        List<BufferedImage> images = new ArrayList<BufferedImage>();
        for (int i = 0; i < readSDF.size(); i++) {
            images.add(imageGenerator.generateStructureImage(readSDF.get(i), new Dimension(140, 140)));
        }
        System.out.println("images size: " + images.size());
        return images;
    }

    class ImageRenderer extends DefaultTableCellRenderer {

        
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            if (value == null) {
                setIcon(dummyIcon);
            } else {
                setIcon(new ImageIcon((BufferedImage) value));
            }

            setHorizontalAlignment(JLabel.CENTER);
            setText("");
            TableColumn col = table.getColumnModel().getColumn(column);
            col.setPreferredWidth(130);
            return this;
        }

        
        public void validate() {
        }

        
        public void revalidate() {
        }

        
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        }

        
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        }
    }

    public static void main(String[] args) {
        try {
            PopulationTableFrame populationTableFrame = new PopulationTableFrame();
            System.out.println("displayed.. now adding..");
            populationTableFrame.add(populationTableFrame.loadAllImages());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }
}
