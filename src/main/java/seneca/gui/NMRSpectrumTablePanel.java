/*
 *  NMRSpectrumTablePanel.java
 *
 *  Copyright (C) 1997, 1998, 1999  Dr. Christoph Steinbeck
 *
 *  Contact: steinbeck@ice.mpg.de
 *
 *  This software is published and distributed under artistic license.
 *  The intent of this license is to state the conditions under which this Package
 *  may be copied, such that the Copyright Holder maintains some semblance
 *  of artistic control over the development of the package, while giving the
 *  users of the package the right to use and distribute the Package in a
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

import org.apache.log4j.Logger;
import org.openscience.spectra.model.NMRSpectrum;
import seneca.core.SenecaDataset;
import seneca.core.SpectrumImporter1D;
import seneca.core.SpectrumImporter2D;
import seneca.gui.tables.NMRSpectrumTableModel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

/**
 * @author steinbeck
 * @created September 9, 2001
 */
public class NMRSpectrumTablePanel extends JPanel implements ChangeListener {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(NMRSpectrumTablePanel.class);
    public boolean debug = false;
    public NMRSpectrum nmrSpectrum;
    protected TableModel tableModel;
    protected JTable table;
    protected JScrollPane scrollpane;
    protected JPanel southPanel;
    protected JLabel northLabel;
    protected JPanel centerPanel;
    protected JButton addSignalButton;
    protected JButton delSignalButton;
    protected JButton importPPButton;
    protected JButton showEditorButton;
    protected JButton closeEditorButton;
    protected JTextArea textArea;
    JComboBox ppSelector;

    SenecaDataset sd = null;

    NMRSpectrumTablePanel(SenecaDataset sd, NMRSpectrum nmrSpectrum) {
        super();
        this.sd = sd;
        this.nmrSpectrum = nmrSpectrum;
        setLayout(new BorderLayout());
        table = new JTable(new NMRSpectrumTableModel(nmrSpectrum));
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        JComboBox phaseSelector = new JComboBox();
        phaseSelector.addItem("NONE");
        phaseSelector.addItem("POSITIVE");
        phaseSelector.addItem("NEGATIVE");
        phaseSelector.setAlignmentX(Component.RIGHT_ALIGNMENT);
        TableColumn phaseColumn = table.getColumnModel().getColumn(
                nmrSpectrum.dim + 2);
        phaseColumn.setCellEditor(new DefaultCellEditor(phaseSelector));
        setPreferredSize(new Dimension(500, 300));
        scrollpane = new JScrollPane(table);
        scrollpane.setBorder(new BevelBorder(BevelBorder.LOWERED));
        southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        addSignalButton = new JButton("Add Signal");
        addSignalButton.addActionListener(new AddNMRSignalAction());
        delSignalButton = new JButton("Delete Signal(s)");
        delSignalButton.addActionListener(new RemoveSignalAction());
        ppSelector = new JComboBox();
        ppSelector.addItem("Import peak picking");
        ppSelector.addItem("Paste WinNMR PP from clipboard");
        ppSelector.addItem("Paste Mestre-C PP from clipboard");
        ppSelector.addItem("Paste Simple List from clipboard");
        ppSelector.addItem("Paste simple 2D");
        ppSelector.addActionListener(new ActionListener() {

            
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                int index = cb.getSelectedIndex();
                if (index == 1) {
                    pasteWinNMRPP();
                } else if (index == 2) {
                    pasteMestrecPP();
                } else if (index == 3) {
                    pasteSimpleListPP();
                } else if (index == 4) {
                    pasteSimple2DList();
                }
                ppSelector.setSelectedIndex(0);
            }

        });

        showEditorButton = new JButton("Show editor");
        closeEditorButton = new JButton("Close editor");
        closeEditorButton.setEnabled(false);
        showEditorButton.addActionListener(new AbstractAction() {
            
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("opening action");
                textArea = new JTextArea();
                textArea.setSize(new Dimension(200, 200));
                JSplitPane splitted = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollpane, new JScrollPane(textArea));
                splitted.setDividerLocation(500);
                centerPanel.removeAll();
                centerPanel.add("Center", splitted);
                add("Center", centerPanel);
                closeEditorButton.setEnabled(true);
                showEditorButton.setEnabled(false);
                revalidate();
            }
        });
        closeEditorButton.addActionListener(new AbstractAction() {
            
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("closing action");
                centerPanel.removeAll();
                centerPanel.add("Center", scrollpane);
                add("Center", centerPanel);
                closeEditorButton.setEnabled(false);
                showEditorButton.setEnabled(true);
                revalidate();
            }
        });

        southPanel.add(addSignalButton);
        southPanel.add(delSignalButton);
        southPanel.add(ppSelector);
        southPanel.add(showEditorButton);
        southPanel.add(closeEditorButton);
        String s = nmrSpectrum.size() + " Signals in ";
        s += nmrSpectrum.specType + ".";
        northLabel = new JLabel(s);
        add("North", northLabel);
        add("South", southPanel);
        scrollpane.setPreferredSize(new Dimension(400, 400));
        centerPanel.add("Center", scrollpane);
        add("Center", centerPanel);
        sd.addChangeListener(this);
    }

    public void pasteWinNMRPP() {
        String s = getStringFromClipboard();
        if (s.indexOf("Peak Picking results:") > 0) {
            nmrSpectrum = SpectrumImporter1D.importWinNMR1D(s, nmrSpectrum);
        } else {
            nmrSpectrum = SpectrumImporter2D.importWinNMR2D(s, nmrSpectrum);
        }
        table.revalidate();
    }

    public void pasteMestrecPP() {
        String s = getStringFromClipboard();
        nmrSpectrum = SpectrumImporter1D.importMestrec1D(s, nmrSpectrum);
        table.revalidate();
    }

    public void pasteSimpleListPP() {
        System.out.println("Importing Simple List from Clipboard");
        logger.info("Importing Simple List from Clipboard");
        String s = getStringFromClipboard();
        System.out.println("Clipboard looks like: " + s);
        logger.info("Clipboard looks like: " + s);
        StringTokenizer strTok = new StringTokenizer(s);
        String firstToken = strTok.nextToken();

        String[] frags = firstToken.split(";");
        // System.out.println("Found " + (frags.length - 1) + " semicolons in "
        // + firstToken);
        try {

            if (frags.length == 1) {
                nmrSpectrum = SpectrumImporter1D.importShifts1D(s,
                        nmrSpectrum);
            }
            if (frags.length == 2) {
                nmrSpectrum = SpectrumImporter1D.importSimpleList1D(s,
                        nmrSpectrum);
            } else if (frags.length == 3) {
                nmrSpectrum = SpectrumImporter2D.importSimpleList2D(s,
                        nmrSpectrum);
            } else {
                System.out
                        .println("Clipboard doesn't look like something that I understand.");
                logger.info("Clipboard doesn't look like something that I understand.");
            }
        } catch (Exception exc) {
            System.out
                    .println("Error while parsing the spectrum from clipboard content. \nThe Clipboard looked like: "
                            + s
                            + "\nNOTE to Developers: This Error should be reported by a GUI");
            exc.printStackTrace();
            logger.info("Error while parsing the spectrum from clipboard content. \nThe Clipboard looked like: "
                    + s
                    + "\nNOTE to Developers: This Error should be reported by a GUI");
            logger.error(exc.getMessage());
        }
        table.revalidate();
    }

    private void pasteSimple2DList() {
        System.out.println("Importing Simple List from Clipboard");
        logger.info("Importing Simple List from Clipboard");
        String s = getStringFromClipboard();
        System.out.println("Clipboard looks like: " + s);
        logger.info("Clipboard looks like: " + s);
        StringTokenizer strTok = new StringTokenizer(s, "\n");
        String firstToken = strTok.nextToken().trim();

        String[] possible_hmbc = firstToken.split("\\s+");
        try {
            if (possible_hmbc.length == 2) {
                nmrSpectrum = SpectrumImporter2D.importShiftsHMBC(s,
                        nmrSpectrum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        table.revalidate();

    }

    public String getStringFromClipboard() {
        java.awt.datatransfer.Clipboard clipboard = getToolkit()
                .getSystemClipboard();
        java.awt.datatransfer.Transferable data = clipboard.getContents(this);
        String s;
        try {
            s = (String) (data
                    .getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor));
        } catch (Exception ex) {
            s = data.toString();
            System.out.println("copied stuff: " + s);
            logger.error(ex.getMessage());
        }
        return s;
    }

    
    public void stateChanged(ChangeEvent e) {
        if (nmrSpectrum == null) {
            return;
        }
        String s = nmrSpectrum.size() + " signals in ";
        s += nmrSpectrum.specType + ".";
        northLabel.setText(s);
    }

    protected void setCellRendering() {
        JLabel label = new JLabel();
        table.setRowSelectionAllowed(false);

        for (int f = 0; f < table.getColumnCount(); f++) {
            table.getColumn(table.getColumnName(f));
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    class AddNMRSignalAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        AddNMRSignalAction() {
            super("addSignal");
        }

        
        public void actionPerformed(ActionEvent e) {
            nmrSpectrum.newSignal();
            table.revalidate();
        }

    }

    class RemoveSignalAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        RemoveSignalAction() {
            super("remSignal");
        }

        
        public void actionPerformed(ActionEvent e) {
            int[] rows = table.getSelectedRows();
            for (int i = rows.length - 1; i >= 0; i--) {
                nmrSpectrum.remove(rows[i]);
            }
            table.repaint();
        }

    }

    class toggleAction extends AbstractAction {
        private static final long serialVersionUID = 1L;

        toggleAction() {
            super("toggleDEPTPhases");
        }

        
        public void actionPerformed(ActionEvent e) {
            int r = table.getSelectedRow();
            int c = table.getSelectedColumn();
            if (c > 1) {
                String s = (table.getValueAt(r, c)).toString().trim();
                if (s.equals("none")) {
                    table.setValueAt("positive", r, c);
                } else if (s.equals("positive")) {
                    table.setValueAt("negative", r, c);
                } else {
                    table.setValueAt("none", r, c);
                }
            }
        }

    }

}
