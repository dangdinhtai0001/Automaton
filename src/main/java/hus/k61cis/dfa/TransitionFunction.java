package hus.k61cis.dfa;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import dnl.utils.text.table.TextTable;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DirectedPseudograph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TransitionFunction {
    private DirectedPseudograph<String, LabelEdge> graph;
    private Set<String> states;
    private Set<String> symbols;

    public TransitionFunction(Set<String> states, Set<String> symbols) {
        graph = new DirectedPseudograph<>(LabelEdge.class);
        this.states = states;
        this.symbols = symbols;

        for (String state : states) {
            graph.addVertex(state);
        }
    }


    public void addEdge(String source, String target, String symbol) {
        if (states.contains(source) && states.contains(target)) {
            graph.addEdge(source, target, new LabelEdge(symbol));
        }
        if (!states.contains(source) && states.contains(target) && symbols.contains(symbol)) {
            states.add(source);
            graph.addVertex(source);
            graph.addEdge(source, target, new LabelEdge(symbol));
        }
        if (states.contains(source) && !states.contains(target) && symbols.contains(symbol)) {
            states.add(target);
            graph.addVertex(target);
            graph.addEdge(source, target, new LabelEdge(symbol));
        }
        if (!states.contains(source) && !states.contains(target)) {
            states.add(source);
            graph.addVertex(source);
            states.add(target);
            graph.addVertex(target);
            graph.addEdge(source, target, new LabelEdge(symbol));
        }
        if (!states.contains(source) && states.contains(target) && !symbols.contains(symbol)) {
            states.add(source);
            graph.addVertex(source);
            graph.addEdge(source, target, new LabelEdge(symbol));
        }
        if (states.contains(source) && !states.contains(target) && !symbols.contains(symbol)) {
            states.add(target);
            graph.addVertex(target);
            graph.addEdge(source, target, new LabelEdge(symbol));
        }
    }

    void drawGraph(String path) throws IOException {
        JGraphXAdapter<String, LabelEdge> graphAdapter =
                new JGraphXAdapter<>(graph);
        mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
        layout.execute(graphAdapter.getDefaultParent());

        BufferedImage image =
                mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
        File imgFile = new File(path);
        ImageIO.write(image, "PNG", imgFile);
    }


    void printTransitionTable() {
        String[][] transition = new String[states.size()][symbols.size() + 1];
        String[] symbols = new String[this.symbols.size() + 1];
        String[] s = this.symbols.toArray(new String[0]);

        symbols[0] = "";
        System.arraycopy(s, 0, symbols, 1, symbols.length - 1);
        int i = 0;
        for (String state : states) {
            transition[i][0] = state;
            for (int j = 1; j < symbols.length; j++) {
                String temp = findAllTargetFromSourceAndEdge(state, symbols[j]).toString().replaceAll(" ", "");
                if (temp.length() < 1) {
                    transition[i][j] = "";
                } else {
                    transition[i][j] = temp.substring(1, temp.length() - 1);
                }
            }
            i++;
        }

        TextTable textTable = new TextTable(symbols, transition);
        textTable.setAddRowNumbering(true);
        textTable.printTable();

    }

    public Set<String> findAllTargetFromSourceAndEdge(String source, String symbol) {
        Set<String> target = new HashSet<>();
        for (LabelEdge edge : graph.outgoingEdgesOf(source)) {
            if (edge.getLabel().equals(symbol)) {
                target.add(graph.getEdgeTarget(edge));
            }
        }
        return target;
    }

    public Set<String> getStates() {
        return states;
    }

    public Set<String> getSymbols() {
        return symbols;
    }
}
