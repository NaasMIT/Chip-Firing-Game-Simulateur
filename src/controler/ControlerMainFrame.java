package controler;

import core.SubstractChipOp;
import core.AddChipOp;
import core.IChipOperation;
import core.ModeSequentialBlock;
import core.PatternUpdate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.AbstractModel;
import model.ModelIteration;
import model.ModelLogFrame;
import model.ModelMainFrame;
import org.graphstream.ui.view.ViewerPipe;
import view.ViewIteration;
import view.ViewLog;
import view.ViewMainFrame;

public class ControlerMainFrame extends AbstractControler {

    private ControlerIteration controlerIteration;
    private ViewIteration viewIteration;
    private ModelIteration modelIteration;

    private ViewMainFrame viewMainFrame;
    private Thread checkUpdateGraph;
    private Thread compute;

    public ControlerMainFrame(AbstractModel model) {
        super(model);
    }

    public ControlerMainFrame(ViewMainFrame viewMainFrame, ModelMainFrame modelMainFrame) {
        super(modelMainFrame);
        this.viewMainFrame = viewMainFrame;

        modelMainFrame.addObserver(viewMainFrame);
        viewMainFrame.getLogButton().addActionListener((ActionListener) this);
        viewMainFrame.getOptionControlRun().addActionListener((ActionListener) this);
        viewMainFrame.getOptionControlForward().addActionListener((ActionListener) this);
        viewMainFrame.getModeAddChips().addActionListener((ActionListener) this);
        viewMainFrame.getModeAddChips().addActionListener((ActionListener) this);
        viewMainFrame.getValideOptionChips().addActionListener((ActionListener) this);
        viewMainFrame.getInputNbChips().addActionListener((ActionListener) this);
        viewMainFrame.getIterationButton().addActionListener((ActionListener) this);
        viewMainFrame.getValidateTime().addActionListener((ActionListener) this);

        final ViewerPipe fromViewer;
        fromViewer = viewMainFrame.getViewer().newViewerPipe();
        fromViewer.addViewerListener(new Click(modelMainFrame));
        fromViewer.addSink(model.getGraph());

        modelIteration = new ModelIteration(model.getGraph());
        viewIteration = new ViewIteration(modelIteration);
        controlerIteration = new ControlerIteration(viewIteration, modelIteration);

        modelMainFrame.setTimeAnimation(1000);
        modelMainFrame.setTimeExec(1000);

        checkUpdateGraph = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        fromViewer.blockingPump();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ViewMainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        checkUpdateGraph.start();
    }

    public void changeColorNode(String id) {
        model.setAttribute(id, "ui.class", "marked");
    }

    public void selectedNodeForChange(String id) {
        if (model.getGraph().getNode(id).getAttribute("ui.class") == "marked") {
            model.addSelectedNode(id);
        } else {
            model.setAttribute(id, "ui.class", "marked");
        }
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == viewMainFrame.getLogButton()) {
            logButtonPerformed();
        }

        if (ae.getSource() == viewMainFrame.getOptionControlRun()) {
            optionControlRunPerformed();
        }

        if (ae.getSource() == viewMainFrame.getOptionControlForward()) {
            optionControlForwardPerformed();
        }

        if (ae.getSource() == viewMainFrame.getValideOptionChips()) {
            valideOptionChipsPerformed();
        }

        if (ae.getSource() == viewMainFrame.getIterationButton()) {
            iterationButtonPerformed();
        }

        if (ae.getSource() == viewMainFrame.getValidateTime()) {
            validateTimeButtonPerformed();
        }
    }

    @Override
    public void control() {
        if (compute.isAlive()) {

        }
    }

    public void logButtonPerformed() {
        ModelLogFrame modelLogFrame = new ModelLogFrame();
        ViewLog viewLog = new ViewLog(modelLogFrame);
        ControlerLog controlerLog = new ControlerLog(viewLog, modelLogFrame);
        viewLog.setVisible(true);
    }

    public void optionControlRunPerformed() {
        if (!controlerIteration.getCurrentPattern().isValid()) {
            iterationButtonPerformed();
            return;
        }

        compute = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    PatternUpdate p = controlerIteration.getCurrentPattern();
                    ((ModelMainFrame) model).execute(new ModeSequentialBlock(p, ((ModelMainFrame) model).getTimeExec(), ((ModelMainFrame) model).getTimeAnimation()));
                }
            }
        });

        compute.start();
    }

    public void optionControlForwardPerformed() {
        if (!controlerIteration.getCurrentPattern().isValid()) {
            iterationButtonPerformed();
            return;
        }

        compute = new Thread(new Runnable() {
            @Override
            public void run() {
                PatternUpdate p = controlerIteration.getCurrentPattern();
                ((ModelMainFrame) model).execute(new ModeSequentialBlock(p, ((ModelMainFrame) model).getTimeExec(), ((ModelMainFrame) model).getTimeAnimation()));
            }
        });

        compute.start();
    }

    private void valideOptionChipsPerformed() {
        IChipOperation op;

        if (viewMainFrame.getModeAddChips().isSelected()) {
            op = new AddChipOp();
        } else {
            op = new SubstractChipOp();
        }

        int nbChips = Integer.parseInt(viewMainFrame.getInputNbChips().getText());
        ((ModelMainFrame) model).computeNodesValues(nbChips, op);
    }

    private void iterationButtonPerformed() {
        viewIteration.setVisible(true);
    }

    private void validateTimeButtonPerformed() {
        double timeExec = Double.parseDouble(viewMainFrame.getOptionControlTime().getText());
        ((ModelMainFrame) model).setTimeExec(timeExec);
        ((ModelMainFrame) model).setTimeAnimation(timeExec);
    }
}
