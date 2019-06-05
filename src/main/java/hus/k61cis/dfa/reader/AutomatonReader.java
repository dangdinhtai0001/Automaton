package hus.k61cis.dfa.reader;

import com.google.gson.Gson;

import java.io.*;
import java.util.List;
import java.util.Set;

public class AutomatonReader {
    private Set<String> states;
    private Set<String> symbols;
    private String startState;
    private Set<String> acceptStates;
    private List<TransitionFunctionReader> transitionFunctionReaders;


    public AutomatonReader(String jsonFile) throws FileNotFoundException {
        Gson gson = new Gson();
        InputStream in = new FileInputStream(jsonFile);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
        AutomatonReader reader = gson.fromJson(buffer, AutomatonReader.class);

        this.states = reader.getStates();
        this.symbols = reader.getSymbols();
        this.startState = reader.getStartState();
        this.acceptStates = reader.getAcceptStates();
        this.transitionFunctionReaders = reader.getTransitionFunctionReaders();
    }

    public Set<String> getStates() {
        return states;
    }

    public Set<String> getSymbols() {
        return symbols;
    }

    public String getStartState() {
        return startState;
    }

    public Set<String> getAcceptStates() {
        return acceptStates;
    }

    public List<TransitionFunctionReader> getTransitionFunctionReaders() {
        return transitionFunctionReaders;
    }
}


