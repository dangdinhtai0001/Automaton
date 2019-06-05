package hus.k61cis.dfa.reader;

import java.util.List;

public class TransitionFunctionReader {
    private String source;
    private String destination;
    private List<String> edges;

    public TransitionFunctionReader(String source, String destination, List<String> edges) {
        this.source = source;
        this.destination = destination;
        this.edges = edges;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public List<String> getEdges() {
        return edges;
    }
}