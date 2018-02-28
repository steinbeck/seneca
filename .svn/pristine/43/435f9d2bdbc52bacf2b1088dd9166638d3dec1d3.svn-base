package seneca.gui.actions;

import org.apache.log4j.Logger;
import seneca.core.SenecaDataset;
import seneca.core.assigners.CarbonShiftAssigner;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;

public class AutoAssignCarbonShiftsAction extends AbstractAction {

    SenecaDataset sd;
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AutoAssignCarbonShiftsAction.class);

    public AutoAssignCarbonShiftsAction(SenecaDataset sd) {
        super("AutoAssign");
        this.sd = sd;
    }

    
    public void actionPerformed(ActionEvent e) {
        new CarbonShiftAssigner(sd).assign();
        logger.info("Carbon shifts are assigned");
        sd.stateChanged(new ChangeEvent(this));
    }
}