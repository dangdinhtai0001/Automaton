import hus.k61cis.Grammar.ContextFreeGrammar;
import junit.framework.TestCase;

import java.io.FileNotFoundException;

public class TestCNF extends TestCase {
    public void testCreateGrammar() throws FileNotFoundException {
        String path = "C:\\Users\\dangd\\IdeaProjects\\Automaton\\src\\test\\DataTest\\CKY\\TestCFG2CNF.json";
        ContextFreeGrammar grammar = new ContextFreeGrammar(path);
        System.out.println(grammar.toString());
    }

    public void testElimiteUseless() throws FileNotFoundException {
        String path = "C:\\Users\\dangd\\IdeaProjects\\Automaton\\src\\test\\DataTest\\CKY\\TestEliminateUseless.json";
        ContextFreeGrammar grammar = new ContextFreeGrammar(path);
        System.out.println(grammar.toString());

        grammar.simplify();
        System.out.println(grammar.toString());
    }

    public void testEliminateNull() throws FileNotFoundException {
        String path = "C:\\Users\\dangd\\IdeaProjects\\Automaton\\src\\test\\DataTest\\CKY\\TestEliminateNull.json";
        ContextFreeGrammar grammar = new ContextFreeGrammar(path);
        System.out.println(grammar.toString());

        grammar.eliminateNull();
        System.out.println(grammar.toString());
    }

    public void testEliminateUnit() throws FileNotFoundException {
        String path = "C:\\Users\\dangd\\IdeaProjects\\Automaton\\src\\test\\DataTest\\CKY\\TestEliminateUnit.json";
        ContextFreeGrammar grammar = new ContextFreeGrammar(path);
        System.out.println(grammar.toString());

        grammar.eliminateUnit();
        System.out.println(grammar.toString());
    }

    public void testCFG2CNF() throws FileNotFoundException {
        String path = "C:\\Users\\dangd\\IdeaProjects\\Automaton\\src\\test\\DataTest\\CKY\\TestCFG2CNF.json";
        ContextFreeGrammar grammar = new ContextFreeGrammar(path);
//        System.out.println(grammar.toString());
        grammar.convert2CNF();
        System.out.println(grammar.toString());

    }
}
