package model;

import core.IChipOperation;
import java.util.concurrent.ConcurrentSkipListSet;
import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class ModelMainFrame extends AbstractModel {

    private ConcurrentSkipListSet<String> selectedNode;

    public ModelMainFrame() {
        System.setProperty("sun.java2d.opengl", "True");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        selectedNode = new ConcurrentSkipListSet<>();

        graph = new SingleGraph("Tutorial 1");

        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", "url('file:////home/jon-snow/NetBeansProjects/Chip-Firing-Game-Simulateur/src/view/myCSS.css')");

        graph.addEdge("12", "1", "2", true);
        graph.addEdge("13", "1", "3", true);
        graph.addEdge("23", "2", "3", true);
        graph.addEdge("32", "3", "2", true);

        for (Node node : graph) {
            node.addAttribute("ui.class", "unmarked");
            node.addAttribute("chips", 2);
            node.addAttribute("ui.label", node.getAttribute("chips").toString());
        }

        for (Node node : graph) {
            for (Edge edgeOut : node.getEachLeavingEdge()) {
                edgeOut.addAttribute("ui.class", "unmarked");
            }
        }
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void changeColor(String id) {
        graph.getNode(id).setAttribute("ui.class", "marked");

        setChanged();
        notifyObservers(graph.getNode(id));
        clearChanged();
    }

    private void setSelectedNode(String id) {
        graph.getNode(id).setAttribute("ui.class", "marked");
        selectedNode.add(id);
    }

    private void setUnselectedNode(String id) {
        graph.getNode(id).setAttribute("ui.class", "unmarked");
        /* TODO assert */
        assert selectedNode.contains(id);

        selectedNode.remove(id);
    }

    public boolean isSelected(String id) {
        return graph.getNode(id).getAttribute("ui.class").equals("marked");
    }

    public void execute(DynamicAlgorithm algo) {
        algo.init(graph);
        algo.compute();
        algo.terminate();

        setChanged();
        notifyObservers(graph);
        clearChanged();
    }

    public void toggleSelectedNode(String id) {
        if (isSelected(id)) {
            setUnselectedNode(id);

        } else {
            setSelectedNode(id);
        }

        setChanged();
        notifyObservers(selectedNode);
        clearChanged();
    }

    public void computeNodesValues(int nbToApply, IChipOperation op) {
        for (String nodeId : selectedNode) {
            int nodeCurrentChips = ((int) graph.getNode(nodeId).getAttribute("chips"));
            graph.getNode(nodeId).setAttribute("chips",  op.compute(nbToApply, nodeCurrentChips));
            graph.getNode(nodeId).setAttribute("ui.label", (int)graph.getNode(nodeId).getAttribute("chips"));
            setUnselectedNode(nodeId);
        }

        setChanged();
        notifyObservers(selectedNode);
        clearChanged();
    }
    
    
}
