import hus.k61cis.dfa.Automaton;
import hus.k61cis.minimizationDFA.Minimization;
import hus.k61cis.minimizationDFA.SimplifyingDFA;
import junit.framework.TestCase;

import java.io.FileNotFoundException;

public class TestDFA extends TestCase {
    public void testCreateAutomaton() throws FileNotFoundException {
        String path = "C:\\Users\\dangd\\IdeaProjects\\Automaton\\src\\test\\DataTest\\DFA\\TestSimplicityData.json";
        Automaton automaton = new Automaton(path);
        automaton.print();
    }

    public void testSimplicity() throws FileNotFoundException {
        String path = "C:\\Users\\dangd\\IdeaProjects\\Automaton\\src\\test\\DataTest\\DFA\\TestSimplicityData.json";
        Automaton automaton = new Automaton(path);
        SimplifyingDFA simplifyingDFA = new SimplifyingDFA();
//        TransitionFunction transitionFunction=simplifyingDFA.getNewTransitionFunction();
//        transitionFunction.printTransitionTable();

        Automaton automaton1 = simplifyingDFA.simplify(automaton);
        automaton1.print();
    }

    public void testMnimization() throws FileNotFoundException {
        String path = "C:\\Users\\dangd\\IdeaProjects\\Automaton\\src\\test\\DataTest\\DFA\\TestMinimizationData.json";
        Minimization minimization = new Minimization();

        Automaton automaton = new Automaton(path);

        automaton.print();

        automaton = minimization.minimization(automaton);
        automaton.print();
    }

}

