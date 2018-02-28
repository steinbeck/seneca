package seneca.gui.configurators;

import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.SenecaDataset;
import seneca.judges.AntiBredtJudge;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 15/02/2013
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
public class AntiBredtJudgeConfigurator extends JudgeConfigurator {
    private static final long serialVersionUID = 1L;
    AntiBredtJudge antiBredtJudge;
    SenecaDataset sd = null;

    public AntiBredtJudgeConfigurator(SenecaDataset sd) {
        super(sd, (AntiBredtJudge) sd.getJudge("AntiBredtJudge"));
        this.sd = sd;
        this.antiBredtJudge = (AntiBredtJudge) sd.getJudge("AntiBredtJudge");
        autoconfigure();
        firstAssignmentMade = true;

    }

    
    public void autoconfigure() {
        IAtomContainer ac = sd.getAtomContainer();
        if (ac == null) {
            return;
        }
        antiBredtJudge.setAtomCount(ac.getAtomCount());
    }

    
    protected Box constructCenterBox() {
        Box centerBox = new Box(BoxLayout.X_AXIS);
        JEditorPane reportPane = new JEditorPane();
        reportPane.setEditable(false);
        reportPane.setEditorKit(new HTMLEditorKit());
        reportPane.setText(getMessage());
        JScrollPane scrollpane = new JScrollPane(reportPane);
        scrollpane.setPreferredSize(new Dimension(200, 400));
        centerBox.add(scrollpane);
        return centerBox;
    }

    protected String getMessage() {
        StringBuffer m = new StringBuffer();
        m.append("<html>");
        m.append("<b>AntiBredtJudge</b> penalizes structures ");
        m.append("that have double bond in a bridge atom, ");
        m.append("that is part of seven or less membered ring. ");
        m.append("</html>");
        return m.toString();
    }
}
