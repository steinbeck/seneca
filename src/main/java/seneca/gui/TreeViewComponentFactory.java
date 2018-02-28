/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui;

import seneca.core.SenecaDataset;
import seneca.gui.configurators.*;
import seneca.gui.tables.GeneralTableModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.HashMap;

/**
 * @author kalai
 */
public class TreeViewComponentFactory {

    private JPanel allPanels = new JPanel();
    private boolean showAllNMRSpectraPanels = false;
    private CardLayout cards = new CardLayout();
    private SenecaDataset sd = null;
    private DefaultMutableTreeNode root = null;
    private JTree tree = null;
    private HashMap<String, JComponent> judgeComponentsMap = null;

    private TreeViewComponentFactory() {
    }

    private static class InstanceHolder {

        private static final TreeViewComponentFactory INSTANCE = new TreeViewComponentFactory();
    }

    public static TreeViewComponentFactory getInstance() {
        return TreeViewComponentFactory.InstanceHolder.INSTANCE;
    }

    public JSplitPane loadTreeViewSettings(SenecaDataset sd, boolean shouldLoadAllNMRSpectraPanel) {
        this.sd = sd;
        this.showAllNMRSpectraPanels = shouldLoadAllNMRSpectraPanel;
        allPanels.setLayout(cards);
        root = new DefaultMutableTreeNode(sd.getName());
        System.out.println("root : " + root.toString());

        createGeneralSettingsLeaf();
        createNodesForSpectraLeaf();
        if (!shouldLoadAllNMRSpectraPanel) {
            createAtomPropertiesLeaf();
            createNodesForJudgeLeaf();
            createNodesForStructureGenLeaf();
        } else {
            this.sd.atomContainer = null;
        }

        this.tree = createTreeAndAddListeners();
        JSplitPane splitPane = createJSplitPaneWithTreeAndAssociatedPanels();
        return splitPane;

    }

    private void createGeneralSettingsLeaf() {

        JTable generalSettingsTable = new JTable();
        generalSettingsTable.setModel(new GeneralTableModel(sd));
        JPanel generalSettingsPanel = new JPanel();
        generalSettingsPanel.setLayout(new BorderLayout());
        generalSettingsPanel.add(generalSettingsTable.getTableHeader(), BorderLayout.NORTH);
        generalSettingsPanel.add(generalSettingsTable, BorderLayout.CENTER);
        generalSettingsPanel.setBackground(Color.pink);
        DefaultMutableTreeNode generalSettings = new DefaultMutableTreeNode("General Settings");
        allPanels.add("General Settings", generalSettingsPanel);
        root.add(generalSettings);
    }

    private void createNodesForSpectraLeaf() {

        DefaultMutableTreeNode spectraPane = new DefaultMutableTreeNode("NMR Spectra");
        DefaultMutableTreeNode spectraNodes = null;

        if (showAllNMRSpectraPanels || sd.carbon1D.size() != 0) {
            spectraNodes = new DefaultMutableTreeNode("1D Carbon");
            JPanel carbonSpectrumPanel = new NMRSpectrumTablePanel(sd, sd.carbon1D);
            spectraPane.add(spectraNodes);
            allPanels.add("1D Carbon", carbonSpectrumPanel);
        }
        if (showAllNMRSpectraPanels || sd.dept90.size() != 0) {
            spectraNodes = new DefaultMutableTreeNode("DEPT-90");
            JPanel dept90Panel = new NMRSpectrumTablePanel(sd, sd.dept90);
            spectraPane.add(spectraNodes);
            allPanels.add("DEPT-90", dept90Panel);
        }
        if (showAllNMRSpectraPanels || sd.dept135.size() != 0) {
            spectraNodes = new DefaultMutableTreeNode("DEPT-135");
            JPanel dept135Panel = new NMRSpectrumTablePanel(sd, sd.dept135);
            spectraPane.add(spectraNodes);
            allPanels.add("DEPT-135", dept135Panel);
        }

        if (showAllNMRSpectraPanels || sd.hetcor.size() != 0) {
            spectraNodes = new DefaultMutableTreeNode("H,C-HetCor");
            JPanel hcHetCorPanel = new NMRSpectrumTablePanel(sd, sd.hetcor);
            spectraPane.add(spectraNodes);
            allPanels.add("H,C-HetCor", hcHetCorPanel);
        }
        if (showAllNMRSpectraPanels || sd.ch_hetcorlr.size() != 0) {
            spectraNodes = new DefaultMutableTreeNode("H,C-lr-HetCor");
            JPanel hcLRHetCorPanel = new NMRSpectrumTablePanel(sd, sd.ch_hetcorlr);
            spectraPane.add(spectraNodes);
            allPanels.add("H,C-lr-HetCor", hcLRHetCorPanel);
        }
        if (showAllNMRSpectraPanels || sd.nh_hetcorlr.size() != 0) {
            spectraNodes = new DefaultMutableTreeNode("H,N-lr-HetCor");
            JPanel nhLRHetCorPanel = new NMRSpectrumTablePanel(sd, sd.nh_hetcorlr);
            spectraPane.add(spectraNodes);
            allPanels.add("H,N-lr-HetCor", nhLRHetCorPanel);
        }
        if (showAllNMRSpectraPanels || sd.hhcosy.size() != 0) {
            spectraNodes = new DefaultMutableTreeNode("H,H-COSY");
            JPanel hhcosyPanel = new NMRSpectrumTablePanel(sd, sd.hhcosy);
            spectraPane.add(spectraNodes);
            allPanels.add("H,H-COSY", hhcosyPanel);
        }
        root.add(spectraPane);

    }

    private void createAtomPropertiesLeaf() {
        DefaultMutableTreeNode atomProperties = new DefaultMutableTreeNode("Atom Properties");
        JPanel atomPropertiesPanel = new AtomPropertyPanel(sd);
        allPanels.add("Atom Properties", atomPropertiesPanel);
        root.add(atomProperties);
    }

    private void createNodesForJudgeLeaf() {

        DefaultMutableTreeNode judges = new DefaultMutableTreeNode("Judges");
        DefaultMutableTreeNode childrenOfJudges = null;
        judgeComponentsMap = new HashMap<String, JComponent>();


        childrenOfJudges = new DefaultMutableTreeNode("HOSECodeJudge");
        JPanel CarbonShiftsJudgePanel = new HOSECodeJudgeConfigurator(sd);
        allPanels.add("HOSECodeJudge", CarbonShiftsJudgePanel);
        judgeComponentsMap.put("HOSECodeJudge", CarbonShiftsJudgePanel);
        judges.add(childrenOfJudges);

        childrenOfJudges = new DefaultMutableTreeNode("NMRShiftDbJudge");
        JPanel nmrShiftDBShiftsJudgePanel = new NMRShiftDBJudgeConfigurator(sd);
        allPanels.add("NMRShiftDbJudge", nmrShiftDBShiftsJudgePanel);
        judgeComponentsMap.put("NMRShiftDbJudge", nmrShiftDBShiftsJudgePanel);
        judges.add(childrenOfJudges);

        childrenOfJudges = new DefaultMutableTreeNode("NPLikenessJudge");
        JPanel NPLikenessJudgePanel = new NPLikenessJudgeConfigurator(sd);
        allPanels.add("NPLikenessJudge", NPLikenessJudgePanel);
        judgeComponentsMap.put("NPLikenessJudge", NPLikenessJudgePanel);
        judges.add(childrenOfJudges);

        childrenOfJudges = new DefaultMutableTreeNode("AntiBredtJudge");
        JPanel antiBredtJudgePanel = new AntiBredtJudgeConfigurator(sd);
        allPanels.add("AntiBredtJudge", antiBredtJudgePanel);
        judgeComponentsMap.put("AntiBredtJudge", antiBredtJudgePanel);
        judges.add(childrenOfJudges);

        childrenOfJudges = new DefaultMutableTreeNode("RingStrainJudge");
        JPanel ringStrainJudgePanel = new RingStrainJudgeConfigurator(sd);
        allPanels.add("RingStrainJudge", ringStrainJudgePanel);
        judgeComponentsMap.put("RingStrainJudge", ringStrainJudgePanel);
        judges.add(childrenOfJudges);

        if (showAllNMRSpectraPanels || sd.hhcosy.size() != 0) {
            childrenOfJudges = new DefaultMutableTreeNode("HHCOSYJudge");
            JPanel hhcosyJudgePanel = new HHCOSYJudgeConfigurator(sd);
            allPanels.add("HHCOSYJudge", hhcosyJudgePanel);
            judgeComponentsMap.put("HHCOSYJudge", hhcosyJudgePanel);
            judges.add(childrenOfJudges);
        }

        if (showAllNMRSpectraPanels || sd.ch_hetcorlr.size() != 0) {
            childrenOfJudges = new DefaultMutableTreeNode("HMBCJudge");
            JPanel hmbc_Judge_Panel = new HMBCJudgeConfigurator(sd);
            allPanels.add("HMBCJudge", hmbc_Judge_Panel);
            judgeComponentsMap.put("HMBCJudge", hmbc_Judge_Panel);
            judges.add(childrenOfJudges);
        }
        /*
        * not used for now ! childrenOfJudges = new DefaultMutableTreeNode("Symmetric Atoms
        * Classes"); JPanel symmetryJudgePanel = new SymmetryJudgeConfigurator(sd);
        * allPanels.add("Symmetric Atoms Classes", symmetryJudgePanel);
        * judges.add(childrenOfJudges);
        *
        */
        root.add(judges);
    }

    private void createNodesForStructureGenLeaf() {

        DefaultMutableTreeNode structureGen = new DefaultMutableTreeNode("Structure Generation");
        DefaultMutableTreeNode configuration = new DefaultMutableTreeNode("Configuration");
        DefaultMutableTreeNode simulation = new DefaultMutableTreeNode("Simulation");

        JPanel annealingSchedulePanel = new ConvergenceAnnealingEngineConfigurator(sd);
        allPanels.add("Configuration", annealingSchedulePanel);

        JPanel strgenSerPanel = new AbstractPanel(sd);
        allPanels.add("Simulation", strgenSerPanel);

        structureGen.add(configuration);
        structureGen.add(simulation);
        root.add(structureGen);
    }

    private JTree createTreeAndAddListeners() {
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        final JTree tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRootVisible(true);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {

                DefaultMutableTreeNode nodes = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (nodes == null) {
                    return;
                }
                cards.show(allPanels, nodes.toString());

            }
        });
        return tree;
    }

    private JSplitPane createJSplitPaneWithTreeAndAssociatedPanels() {
        tree.setCellRenderer(new IconRenderer());
        tree.updateUI();
        int whateverRowYouWantToSelect = 1;
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        if (!showAllNMRSpectraPanels) {
            whateverRowYouWantToSelect = tree.getRowCount() - 1;
        }
        TreePath path = this.tree.getPathForRow(whateverRowYouWantToSelect);
        tree.setSelectionPath(path);
        return combineTreeAndPanels(tree, allPanels);
    }

    private JSplitPane combineTreeAndPanels(JTree tree, JPanel allPanels) {
        JSplitPane splitted = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), new JScrollPane(allPanels));
        splitted.setDividerLocation(300);
        return splitted;
    }

    public JPanel getPanels() {
        return this.allPanels;
    }

    public void reloadTree() {
        DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
        model.reload(root);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        int whateverRowYouWantToSelect = tree.getRowCount() - 1;
        TreePath path = this.tree.getPathForRow(whateverRowYouWantToSelect);
        tree.setSelectionPath(path);
    }

    public Component getComponentByName(String name) {
        return judgeComponentsMap.containsKey(name) ? (Component) judgeComponentsMap.get(name) : null;
    }

}
