package hus.k61cis.minimizationDFA;

import hus.k61cis.Ultis;
import hus.k61cis.dfa.Automaton;
import hus.k61cis.dfa.TransitionFunction;

import java.util.HashSet;
import java.util.Set;

public class SimplifyingDFA {

    public Automaton simplify(Automaton automaton) {
        TransitionFunction newTransitionFunction = getNewTransitionFunction(automaton);
        Set<String> states = newTransitionFunction.getStates();
        Set<String> acceptStates = getNewAcceptStates(automaton, newTransitionFunction);
        Set<String> symbols = automaton.getSymbols();
        String startState = automaton.getStartState();

        return new Automaton(states, symbols, startState, acceptStates, newTransitionFunction);
    }

    //START---------------HÀM CHUYỂN TRẠNG THÁI MỚI--------------------------------------------------------------------
    private TransitionFunction getNewTransitionFunction(Automaton automaton) {
        TransitionFunction transitionFunction = automaton.getTransitionFunction();

        //-------Tìm các trạng thái không tối thiểu------------
        Set<String> set = new HashSet<>();
        for (String state : automaton.getStates()) {
            for (String symbol : automaton.getSymbols()) {
                Set<String> target = transitionFunction.findAllTargetFromSourceAndEdge(state, symbol);
                if (target.size() > 1) {
                    set.add(Ultis.getStringFromCollection(target, "&"));
                }
            }
        }
        //-------Tìm các trạng thái không tối thiểu------------
        //-------Hoàn thành hàm chuyển------------
        for (String s : set) {
            Set<String> temp = Ultis.getSetFromString(s, "&");
            for (String symbol : automaton.getSymbols()) {
                Set<String> target = new HashSet<>();
                for (String source : temp) {
                    target.addAll(transitionFunction.findAllTargetFromSourceAndEdge(source, symbol));
                }
                if (target.size() > 0) {
                    transitionFunction.addEdge(s, Ultis.getStringFromCollection(target, "&"), symbol);
                }
            }
        }
        //-------Hoàn thành hàm chuyển------------

        return transitionFunction;
    }

    //END-----------------HÀM CHUYỂN TRẠNG THÁI MỚI--------------------------------------------------------------------
    //START-----------------TÌM TẬP TRẠNG THÁI KẾT THÚC MỚI------------------------------------------------------------
    private Set<String> getNewAcceptStates(Automaton automaton, TransitionFunction transitionFunction) {
        Set<String> oldAcceptStates = automaton.getAcceptStates();
        Set<String> newAcceptStates = new HashSet<>();

        //-------------
        for (String state : transitionFunction.getStates()) {
            for (String old : oldAcceptStates) {
                if (state.contains(old)) {
                    newAcceptStates.add(state);
                }
            }
        }
        //-------------
        newAcceptStates.addAll(oldAcceptStates);
        return newAcceptStates;
    }
    //END-----------------TÌM TẬP TRẠNG THÁI KẾT THÚC MỚI------------------------------------------------------------

}
