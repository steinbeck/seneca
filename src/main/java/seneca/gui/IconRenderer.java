/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * @author kalai
 */
class IconRenderer extends DefaultTreeCellRenderer {

    public IconRenderer() {
    }

    public Component getTreeCellRendererComponent(JTree tree,
                                                  Object value,
                                                  boolean sel,
                                                  boolean expanded,
                                                  boolean leaf,
                                                  int row,
                                                  boolean hasFocus) {
        // start with default behavior
        super.getTreeCellRendererComponent(tree, value, sel, expanded,
                leaf, row, hasFocus);


        // customize based on local conditions/state
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        String userObject = (String) node.getUserObject();

        if (node.isRoot()) {
            setIcon(Seneca.folderIcon);
            return this;
        } else if (userObject.equalsIgnoreCase("General Settings")) {
            setIcon(Seneca.generalSettingsIcon);
            return this;
        } else if (userObject.equalsIgnoreCase("Atom Properties")) {
            setIcon(Seneca.propsIcon);
            return this;
        } else if (userObject.equalsIgnoreCase("NMR Spectra")) {
            setIcon(Seneca.spectrumIcon);
            return this;
        } else if (userObject.equalsIgnoreCase("Judges")) {
            setIcon(Seneca.judgesIcon);
        } else if (userObject.equalsIgnoreCase("Structure Generation")) {
            setIcon(Seneca.simulationIcon);
            return this;
        } else {
            setIcon(Seneca.leafConfigIcon);
        }
        return this;
    }
}
