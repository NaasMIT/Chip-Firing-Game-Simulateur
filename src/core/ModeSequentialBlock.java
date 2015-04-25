package core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graphstream.algorithm.DynamicAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class ModeSequentialBlock implements DynamicAlgorithm {
    private PatternUpdate patternUpdate;
    private Graph graph;
    
    public ModeSequentialBlock (PatternUpdate patternUpdate)
    {
        this.patternUpdate = patternUpdate;
    }

    @Override
    public void terminate() {
        
    }

    @Override
    public void init(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void compute() {
        
        /* start */
        for (Map.Entry<Integer, LinkedList<String>> stepIter : patternUpdate.getAllStep()) {
            int numStep = stepIter.getKey();
            LinkedList<String> parallelPattern = stepIter.getValue();

            HashMap<String, Integer> initialState = new HashMap<>(parallelPattern.size());
            
            for (String nodeId : parallelPattern) {
                initialState.put(nodeId, (int) graph.getNode(nodeId).getAttribute("chips"));
            }

            for (Map.Entry<String, Integer> entry : initialState.entrySet()) {
                if (entry.getValue() >= graph.getNode(entry.getKey()).getOutDegree()) {
                    for (Edge edgeOut : graph.getNode(entry.getKey()).getEachLeavingEdge()) {
                        edgeOut.getNode1().setAttribute("chips", (int) edgeOut.getNode1().getAttribute("chips") + 1);
                        edgeOut.getNode1().setAttribute("ui.label", edgeOut.getNode1().getAttribute("chips").toString());
                        edgeOut.getNode0().setAttribute("chips", (int) edgeOut.getNode0().getAttribute("chips") - 1);
                        edgeOut.getNode0().setAttribute("ui.label", edgeOut.getNode0().getAttribute("chips").toString());
                        edgeOut.setAttribute("ui.class", "marked");
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ModeSequentialBlock.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (Edge e : graph.getEdgeSet()) {
                e.setAttribute("ui.class", "unmarked");
            }

        }

        for (Node node : graph) {
            System.out.println(node.getId() + " : " + node.getAttribute("chips"));
            node.setAttribute("ui.label", node.getAttribute("chips").toString());
        }
    }
}
