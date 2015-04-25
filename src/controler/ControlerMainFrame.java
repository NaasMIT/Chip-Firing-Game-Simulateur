package controler;

import core.RetrieveChipOp;
import core.AddChipOp;
import core.IChipOperation;
import core.ModeSequentialBlock;
import core.PatternUpdate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.AbstractModel;
import model.ModelLogFrame;
import model.ModelMainFrame;
import org.graphstream.ui.view.ViewerPipe;
import view.ViewLog;
import view.ViewMainFrame;

public class ControlerMainFrame extends AbstractControler {

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

        final ViewerPipe fromViewer;
        fromViewer = viewMainFrame.getViewer().newViewerPipe();
        fromViewer.addViewerListener(new Click(modelMainFrame));
        fromViewer.addSink(model.getGraph());

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
            optionControlRun();
        }

        if (ae.getSource() == viewMainFrame.getOptionControlForward()) {
            optionControlForward();
        }

        if (ae.getSource() == viewMainFrame.getValideOptionChips()) {
            valideOptionChips();
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

    public void optionControlRun() {
        compute = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    PatternUpdate p = new PatternUpdate();
                    try {
                        p.initPattern("({1,2},{3})", model.getGraph().getNodeCount());
                    } catch (Exception ex) {
                        Logger.getLogger(ControlerMainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    ((ModelMainFrame) model).execute(new ModeSequentialBlock(p));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ControlerMainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        compute.start();
    }

    public void optionControlForward() {
        compute = new Thread(new Runnable() {
            @Override
            public void run() {
                PatternUpdate p = new PatternUpdate();
                try {
                    p.initPattern("({1,2},{3})", model.getGraph().getNodeCount());
                } catch (Exception ex) {
                    Logger.getLogger(ControlerMainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                ((ModelMainFrame) model).execute(new ModeSequentialBlock(p));
            }
        });

        compute.start();
    }

    private void valideOptionChips() {
        IChipOperation op;
        
        if (viewMainFrame.getModeAddChips().isSelected())
        {
            op = new AddChipOp();
        } else 
        {
            op = new RetrieveChipOp();
        }
        
        int nbChips = Integer.parseInt(viewMainFrame.getInputNbChips().getText());
        ((ModelMainFrame)model).computeNodesValues(nbChips, op);
    }
}
