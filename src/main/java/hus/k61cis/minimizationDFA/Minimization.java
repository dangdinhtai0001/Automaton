package hus.k61cis.minimizationDFA;

import hus.k61cis.Ultis;
import hus.k61cis.dfa.Automaton;
import hus.k61cis.dfa.TransitionFunction;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * https://www.geeksforgeeks.org/theory-computation-minimization-dfa/
 * DFA minimizationDFA stands for converting a given DFA to its equivalent DFA with minimum number of states.
 */
public class Minimization {
    private Partitioned partitioned;

    public Automaton minimization(Automaton automaton) {
        //-Đơn định hóa trước -----------------
        SimplifyingDFA simplifyingDFA = new SimplifyingDFA();
        automaton = simplifyingDFA.simplify(automaton);
        //-Đơn định hóa trước -----------------

        firstPartitions(automaton);
        calculateNextpPartition(automaton);
        //-----tạo automaton tương đương mới------------------
        Set<String> symbols = automaton.getSymbols();
        Set<String> states = getNewStates();
        Set<String> acceptStates = getNewAcceptStates(automaton, states);
        String startState = getNewStartState(automaton, states);
        TransitionFunction transitionFunction = getNewTransitionFunction(states, symbols, automaton);
        //-----tạo automaton tương đương mới------------------

        return new Automaton(states, symbols, startState, acceptStates, transitionFunction);
    }

    //START-----------------TÌM HÀM CHUYỂN TRẠNG THÁI MỚI---------------------
    private TransitionFunction getNewTransitionFunction(Set<String> states, Set<String> symbols, Automaton automaton) {
        TransitionFunction transitionFunction = new TransitionFunction(states, symbols);
        for (String state : automaton.getStates())
            for (String symbol : symbols) {
                String s = Ultis.getStringFromCollection(automaton.getTransitionFunction().findAllTargetFromSourceAndEdge(state, symbol), "");
                String source = null, target = null;
                for (String combinedState : states) {
                    if (combinedState.contains(s) || combinedState.equals(s)) {
                        target = combinedState;
                    }
                    if (combinedState.contains(state) || combinedState.equals(state)) {
                        source = combinedState;
                    }
                }
                try {
                    transitionFunction.addEdge(source, target, symbol);
                } catch (NullPointerException ignored) {
                }
            }
        return transitionFunction;
    }
    //END-----------------TÌM HÀM CHUYỂN TRẠNG THÁI MỚI---------------------

    //START-----------------TÌM TRẠNG THÁI KẾT THÚC MỚI---------------------
    private Set<String> getNewAcceptStates(Automaton automaton, Set<String> states) {
        Set<String> acceptStates = new HashSet<>();
        for (String state : states) {
            for (String acceptState : automaton.getAcceptStates()) {
                if (state.contains(acceptState)) {
                    acceptStates.add(state);
                }
            }
        }
        return acceptStates;
    }
    //END-----------------TÌM TRẠNG THÁI BKẾT THÚC MỚI---------------------

    //START-----------------TÌM TRẠNG THÁI BẮT ĐẦU MỚI---------------------
    private String getNewStartState(Automaton automaton, Set<String> states) {
        String startState = "";
        for (String state : states) {
            if (state.contains(automaton.getStartState())) {
                startState = state;
            }
        }
        return startState;
    }
    //END-----------------TÌM TRẠNG THÁI BẮT ĐẦU MỚI---------------------

    //START-----------------TÌM TẬP TRẠNG THÁI MỚI---------------------
    private Set<String> getNewStates() {
        Set<String> states = new HashSet<>();
        for (Set<String> partition : partitioned.getCurrentPartitions()) {
            states.add(Ultis.getStringFromCollection(partition, "&"));
        }
        return states;
    }
    //END-----------------TÌM TẬP TRẠNG THÁI MỚI---------------------

    //START-------------------STEP 1------------------------------------------------------------------------------------

    /**
     * Chia states thành hai bộ. Một bộ sẽ chứa tất cả các acceptStates và bộ khác
     * sẽ không chứa bất kì acceptStates nào. Phân vùng này được gọi là P0.
     */
    private void firstPartitions(Automaton automaton) {
        Set<String> unacceptStates = new HashSet<>(automaton.getStates());

        unacceptStates.removeAll(automaton.getAcceptStates());

        Set<Set<String>> partitions = new HashSet<>();
        partitions.add(automaton.getAcceptStates());
        partitions.add(unacceptStates);

        partitioned = new Partitioned(partitions);
    }

    //END-------------------STEP 1------------------------------------------------------------------------------------

    //START------------------------------------------------------------------------------------------------------------
    private void calculateNextpPartition(Automaton automaton) {
        boolean loop = true;
        while (loop) {
            //-----với mỗi partitioned---------------------
            Set<Set<String>> res = new HashSet<>();
            for (Set<String> partition : partitioned.getCurrentPartitions()) {
                if (partition.size() > 1) {
                    Set<Set<String>> set = dividePartition(partition, automaton);
                    res.addAll(set);
                } else {
                    res.add(partition);
                }
            }
            partitioned.update(res);
            loop = !partitioned.isNoChangeInPartitions();
        }
    }
    //END------------------------------------------------------------------------------------------------------------

    //START------------------Tách các trạng thái phan biệt đc trong 1 Partition-------------------------
    private Set<Set<String>> dividePartition(Set<String> partition, Automaton automaton) {
        Set<Set<String>> res = new HashSet<>();
        //start--------Tạo mảng đánh dấu , vs các cặp trạng thái không xét thì =-1-----------------
        int length = partition.size();
        int[][] marked = new int[length][length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (i <= j) {
                    marked[i][j] = -1;
                }
            }
        }
        //end--------Tạo mảng đánh dấu , vs các cặp trạng thái không xét thì =-1-----------------

        //start----------đánh chỉ số cho state------------
        Map<String, Integer> index = new HashMap<>();
        int temp = 0;
        for (
                String state : partition) {
            index.put(state, temp);
            temp++;
        }
        //end----------đánh chỉ số cho state------------

        //start-----------với các cặp trạng thái phân biệt đc thì đánh marked =1---------------
        for (String state1 : partition) {
            for (String state2 : partition) {
                if (marked[index.get(state1)][index.get(state2)] == 0 && isDistinguishable(state1, state2, automaton)) {
                    marked[index.get(state1)][index.get(state2)] = 1;
                }
            }
        }
        //end-----------với các cặp trạng thái phân biệt đc thì đánh marked =1---------------

        //start--------------------Tìm tất cả các trạng thái độc lập -----------------------------------------
        for (String state1 : partition) {
            boolean conditional = true;
            int count = 0;

            for (String state2 : partition) {
                if (marked[index.get(state2)][index.get(state1)] == 0) {
                    conditional = false;
                    break;
                }
                if (marked[index.get(state2)][index.get(state1)] == -1) {
                    count++;
                }
            }
            if (conditional && (count != partition.size() | count == partition.size() - 1)) {
                Set<String> set = new HashSet<>();
                set.add(state1);
                res.add(set);
            }
        }
        //end----------------Tìm tất cả các trạng thái độc lập---------------------------------------------
        //---------------------------------------------------
        for (String state1 : partition) {
            if (!constains(res, state1)) {
                Set<String> set = new HashSet<>();
                for (String state2 : partition) {
                    if (marked[index.get(state2)][index.get(state1)] == 0) {
                        set.add(state1);
                        set.add(state2);
                    }
                }
                if (set.size() != 0) {
                    res.add(set);
                }
            }
        }
        return res;
    }
    //END------------------Tách các trạng thái phan biệt đc trong 1 Partition-------------------------

    //-----------Kiểm tra hai trạng thái có phân biệt hay không-----------
    private boolean isDistinguishable(String state1, String state2, Automaton automaton) {
        TransitionFunction transitionFunction = automaton.getTransitionFunction();
        boolean res = false;

        for (String symbol : automaton.getSymbols()) {
            //--------Vì otomat đã đơn định rồi nên set thu đc bowti hàm findAllTargetFromSourceAndEdge chỉ có 1 phần tử ---> để regex là gì cũng đc
            String target1 = Ultis.getStringFromCollection(transitionFunction.findAllTargetFromSourceAndEdge(state1, symbol), "");
            String target2 = Ultis.getStringFromCollection(transitionFunction.findAllTargetFromSourceAndEdge(state2, symbol), "");

            for (Set<String> partition : partitioned.getCurrentPartitions()) {
                res = !((partition.contains(target1) && partition.contains(target2)) | (!partition.contains(target1) && !partition.contains(target2)));
            }
        }
        return res;
    }
    //-----------Kiểm tra hai trạng thái có phân biệt hay không-----------

    //----------kiểm tra state có xuất hiện trong set hay ko
    private boolean constains(Set<Set<String>> set, String state) {
        for (Set<String> stringSet : set) {
            if (stringSet.contains(state)) {
                return true;
            }
        }
        return false;
    }
    //----------kiểm tra state có xuất hiện trong set hay ko
}
