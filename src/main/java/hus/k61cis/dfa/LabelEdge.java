package hus.k61cis.dfa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jgrapht.graph.DefaultEdge;

@AllArgsConstructor
@Getter
public class LabelEdge extends DefaultEdge {
    private String label;

    @Override
    public String toString() {
        return label;
    }
}
