package seneca.gui.actions;

import seneca.gui.Seneca;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ExitAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ExitAction() {
        super(Seneca.exitAction);
    }

    
    public void actionPerformed(ActionEvent e) {
        Seneca.getInstance().trySystemExit();
    }

}