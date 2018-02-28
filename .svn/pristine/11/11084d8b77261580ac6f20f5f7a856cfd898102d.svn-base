package seneca.gui.actions;

import seneca.gui.Seneca;
import seneca.gui.SenecaComponentFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AboutAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AboutAction() {
        super(Seneca.aboutAction);
    }

    
    public void actionPerformed(ActionEvent e) {
        SenecaComponentFactory componentFactory = SenecaComponentFactory
                .getInstance();
        String title = componentFactory.getResourceString("AboutTitle");
        int lineNum = (Integer.valueOf(componentFactory
                .getResourceString("AboutLines"))).intValue();
        String msg = "";
        for (int f = 0; f < lineNum; f++) {
            msg += componentFactory.getResourceString("AboutMsg" + (f + 1))
                    + "\n";
        }
        JOptionPane.showMessageDialog(null, msg, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

}