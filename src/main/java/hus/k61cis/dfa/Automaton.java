package hus.k61cis.dfa;

import com.google.gson.Gson;
import hus.k61cis.dfa.reader.AutomatonReader;
import hus.k61cis.dfa.reader.TransitionFunctionReader;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Automaton {
    private Set<String> states;
    private Set<String> symbols;
    private String startState;
    private Set<String> acceptStates;
    private TransitionFunction transitionFunction;

    public Automaton(Set<String> states, Set<String> symbols, String startState, Set<String> acceptStates,
                     TransitionFunction transitionFunction) {
        this.states = states;
        this.symbols = symbols;
        this.startState = startState;
        this.acceptStates = acceptStates;
        this.transitionFunction = transitionFunction;
    }

    public Automaton(String jsonFile) throws FileNotFoundException {
        AutomatonReader reader = new AutomatonReader(jsonFile);

        this.states = reader.getStates();
        this.symbols = reader.getSymbols();
        this.startState = reader.getStartState();
        this.acceptStates = reader.getAcceptStates();
        this.transitionFunction = new TransitionFunction(states, symbols);

        List<TransitionFunctionReader> readers = reader.getTransitionFunctionReaders();
        for (TransitionFunctionReader r : readers) {
            for (String symbol : r.getEdges()) {
                this.transitionFunction.addEdge(r.getSource(), r.getDestination(), symbol);
            }
        }
    }

    public Automaton() {
        this.states = new HashSet<>();
        this.symbols = new HashSet<>();
        this.startState = "";
        this.acceptStates = new HashSet<>();
        this.transitionFunction = new TransitionFunction(states, symbols);
    }
    //END------------------THUỘC TÍNH VÀ HÀM KHỞI TẠO-------------------------------------------------------------------

    public void print() {
        System.out.println("Tập trạng thái        : " + states.toString());
        System.out.println("Bảng chữ cái          : " + symbols.toString());
        System.out.println("Trạng thái bắt đầu    : [" + startState + "]");
        System.out.println("Tập trạng thái kết    : " + acceptStates.toString());
        System.out.println("Hàm chuyển trạng thái : ");
        transitionFunction.printTransitionTable();
    }
}
