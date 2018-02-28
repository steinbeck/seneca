/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package seneca.gui.actions;

import org.apache.log4j.Logger;
import seneca.gui.AbstractPanel;
import seneca.gui.Seneca;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author kalai
 */
public class ResetSelectedAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ResetSelectedAction.class);
    private AbstractPanel panel = null;

    public ResetSelectedAction(AbstractPanel panel) {
        this.panel = panel;
    }

    
    public void actionPerformed(ActionEvent e) {
        logger.info("Resetting the current dataset: " + panel.sd.getName());
        Seneca.getTreeFactory().getPanels().add("Simulation", new AbstractPanel(panel.sd));
        Seneca.getTreeFactory().reloadTree();
    }
}
