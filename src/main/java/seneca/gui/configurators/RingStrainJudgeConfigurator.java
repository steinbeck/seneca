package seneca.gui.configurators;

import org.openscience.cdk.interfaces.IAtomContainer;
import seneca.core.SenecaDataset;
import seneca.judges.RingStrainJudge;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 11/05/2013
 * Time: 18:40
 * To change this template use File | Settings | File Templates.
 */
public class RingStrainJudgeConfigurator extends JudgeConfigurator {
    private static final long serialVersionUID = 1L;
    RingStrainJudge ringStrainJudge;
    SenecaDataset sd = null;

    public RingStrainJudgeConfigurator(SenecaDataset sd) {
        super(sd, (RingStrainJudge) sd.getJudge("RingStrainJudge"));
        this.sd = sd;
        this.ringStrainJudge = (RingStrainJudge) sd.getJudge("RingStrainJudge");
        autoconfigure();
        firstAssignmentMade = true;

    }

    
    public void autoconfigure() {
        IAtomContainer ac = sd.getAtomContainer();
        if (ac == null) {
            return;
        }
        ringStrainJudge.setAtomCount(ac.getAtomCount());
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
        m.append("<b>RingStrainJudge</b> penalizes structures ");
        m.append("that have rings that are sterically constrained");
        m.append("</html>");
        return m.toString();
    }
}
