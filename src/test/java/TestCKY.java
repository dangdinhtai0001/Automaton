import hus.k61cis.CKY.CKY;
import hus.k61cis.Grammar.ContextFreeGrammar;
import junit.framework.TestCase;

import java.io.FileNotFoundException;

public class TestCKY extends TestCase {

    public void test() throws FileNotFoundException {
        String path = "C:\\Users\\dangd\\IdeaProjects\\Automaton\\src\\test\\DataTest\\CKY\\TestCky.json";
        ContextFreeGrammar grammar = new ContextFreeGrammar(path);
        System.out.println(grammar);

        CKY cky = new CKY();
        System.out.println(cky.algorithm(grammar, "aaabbb"));
    }
}
