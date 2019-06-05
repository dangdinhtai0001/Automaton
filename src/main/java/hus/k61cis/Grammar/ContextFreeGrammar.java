package hus.k61cis.Grammar;

import com.google.gson.Gson;
import hus.k61cis.Ultis;
import lombok.Getter;

import java.io.*;
import java.util.*;

@Getter
public class ContextFreeGrammar {
    //    V= variables , Σ= symbols , R= rules , S= startVariable
    private Set<String> variables;
    private String startVariable;
    private Set<String> symbols;
    private Set<ProductionRule> rules;

    public ContextFreeGrammar(Set<String> variables, String startVariable, Set<String> symbols, Set<ProductionRule> rules) {
        this.variables = variables;
        this.startVariable = startVariable;
        this.symbols = symbols;
        this.rules = rules;
    }

    public ContextFreeGrammar(String path) throws FileNotFoundException {
        Gson gsonRead = new Gson();
        InputStream in = new FileInputStream(path);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
        ContextFreeGrammar grammar = gsonRead.fromJson(buffer, ContextFreeGrammar.class);

        this.rules = grammar.getRules();
        this.symbols = grammar.getSymbols();
        this.startVariable = grammar.getStartVariable();
        this.variables = grammar.getVariables();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Grammar\n{" +
                "\tvariables=" + variables + "\n" +
                "\tstartVariable= " + startVariable + "\n" +
                "\tsymbols=" + symbols + "\n" +
                "\trules=[\n");
        for (String var : variables) {
            if (findAllRuleStartBy(var).size() > 0) {
                s.append("\t\t").append(var).append("-> ")
                        .append(Ultis.getStringFromCollection(findAllRuleStartBy(var), " | ")).append("\n");
            }
        }
        s.append("\t]\n}");
        return s.toString();
    }

    private List<String> findAllRuleStartBy(String var) {
        List<String> list = new LinkedList<>();

        for (ProductionRule productionRule : rules) {
            if (productionRule.getLeftSide().equals(var)) {
                list.add(Ultis.getStringFromCollection(productionRule.getRightSide(), " "));
            }
        }

        return list;
    }

    public void convert2CNF() {
        eliminateStartSymbol();
        simplify();
        term();
        bin();
    }

    //
    //START-----------------------Thay đổi trạng thái bắt đầu ------------------------------------------------------------------------------
    //
    private void eliminateStartSymbol() {
        String newStartVariables = startVariable + "\'";
        for (ProductionRule rule : rules) {
            if (rule.getRightSide().contains(startVariable)) {
                // ở đây regex phải là " " thay vì "" nếu không sẽ tách dấu ' trong newStartVariables ra
                this.rules.add(new ProductionRule(newStartVariables, startVariable, " "));
                break;
            }
        }
        this.startVariable = newStartVariables;
        variables.add(startVariable);
    }

    //
    //END-----------------------Thay đổi trạng thái bắt đầu --------------------------------------------------------------------------------
    //
    //

    //START--------------------------------------------------------Simplifying-------------------------------------------------------
    public void simplify() {
        eliminateNull();
        eliminateUnit();
        eliminateUseless();
    }

    //START-------------------------Eliminate Useless -----------------------------------------------------------------
    public void eliminateUseless() {
        eliminateNonterminateRules();
        eliminateNeverReachedFromStartingVariable();
    }

    //---------------Tìm tập các rule useless-----------------
    public void eliminateNonterminateRules() {
        List<ProductionRule> terminateRules = new LinkedList<>();
        Set<String> terminateVariables = new HashSet<>();
        List<ProductionRule> nonterminateRules = Ultis.coppyRule(rules);

        //-------------------Tìm các rule có vế phải chỉ toàn là chữ cái chính-------------------
        for (ProductionRule productionRule : nonterminateRules) {
            if (symbols.containsAll(productionRule.getRightSide())) {
                terminateRules.add(productionRule);
                terminateVariables.add(productionRule.getLeftSide());
            }
        }
        //-------------------Tìm các rule có vế phải chỉ toàn là chữ cái chính-------------------
        nonterminateRules.removeAll(terminateRules);
        //-------------------------Tìm tất cả các rule có thể kết thúc được--------------------------
        int oldIndex = -1;
        int newIndex = terminateRules.size();
        do {
            for (ProductionRule productionRule : nonterminateRules) {
                for (String s : productionRule.getRightSide()) {
                    if (variables.contains(s)) {
                        if (terminateVariables.contains(s) | startVariable.equals(s)) {
                            terminateRules.add(productionRule);
                            terminateVariables.add(productionRule.getLeftSide());
                        }
                    }
                }

                oldIndex = newIndex;
                newIndex = terminateRules.size();
            }
        }
        while (oldIndex != newIndex);

        //-------------------------Tìm tất cả các rulr có thể kết thúc được--------------------------
        nonterminateRules.removeAll(terminateRules);
        rules.removeAll(nonterminateRules);
    }
    //---------------Tìm tập các rule useless-----------------

    //-----------------------Tìm tập các rule không thể suy ra đc từ start var------------------------------
    public void eliminateNeverReachedFromStartingVariable() {
        List<ProductionRule> list = new LinkedList<>();
        Set<String> start = new HashSet<>();

        for (ProductionRule rule : rules) {
            if (rule.getLeftSide().equals(startVariable)) {
                for (String s : rule.getRightSide()) {
                    if (!symbols.contains(s) && !startVariable.equals(s)) {
                        start.add(s);
                    }
                }
            }
        }
        for (ProductionRule rule : rules) {
            if (!startVariable.equals(rule.getLeftSide())) {
                if (!start.contains(rule.getLeftSide())) {
                    list.add(rule);
                }
            }
        }
        rules.removeAll(list);
    }
    //-----------------------Tìm tập các rule không thể suy ra đc từ start var------------------------------
    //END-------------------------Eliminate Useless -----------------------------------------------------------------

    //START-------------------------Eliminate null -----------------------------------------------------------------
    public void eliminateNull() {
        List<ProductionRule> nullableRule = new LinkedList<>();
        List<String> nullableVariables = new ArrayList<>();

        //Tìm các rule sinh ra xâu eplison------------------------------------------------------------------------------
        for (ProductionRule rule : rules) {
            if (rule.getRightSide().get(0).equals("$")) {
                nullableRule.add(rule);
            }
        }

        for (ProductionRule rule : nullableRule) {
            nullableVariables.add(rule.getLeftSide());
        }
        //Tìm các rule sinh ra xâu eplison------------------------------------------------------------------------------
        //xóa các rule sinh ra xâu epsilon------------------------------------------------------------------------------
        for (ProductionRule rule : rules) {
            if (checkNullable(rule.getRightSide(), nullableVariables)) {
                nullableVariables.add(rule.getLeftSide());
            }
        }
        rules.removeAll(nullableRule);
        //xóa các rule sinh ra xâu epsilon------------------------------------------------------------------------------
        //thay thế các biến sinh ra epsilon-----------------------------------------------------------------------------
        List<ProductionRule> newRule = new LinkedList<>();
        for (ProductionRule rule : rules) {
            if (!symbols.containsAll(rule.getRightSide()))
                newRule.addAll(replaceNullVariables(rule, nullableVariables));
        }

        rules.addAll(newRule);
        //thay thế các biến sinh ra epsilon-----------------------------------------------------------------------------
    }

    private boolean checkNullable(List<String> rightSide, List<String> nullable) {
        return (nullable.containsAll(rightSide));
    }

    private List<ProductionRule> replaceNullVariables(ProductionRule rule, List<String> nullableVariables) {
        List<ProductionRule> res = new LinkedList<>();
        List<String> nullable = new LinkedList<>();
        int n = 0;
        for (String s : rule.getRightSide()) {
            if (nullableVariables.contains(s)) {
                nullable.add(s);
                n++;
            }
        }
        //nếu vế phải không có variable trống thì thôi -----------------------------------------------------------------
        if (n != 0) {
            int[] x = new int[n];
            List<String> rightSide = rule.getRightSide();
            String leftSide = rule.getLeftSide();
            getPossibleCombinationsVariables(0, n, x, nullable, res, rightSide, leftSide);
        }
        //nếu vế phải không có variable trống thì thôi -----------------------------------------------------------------
        return res;
    }

    //tìm tất cả các tập con của tập các variable null để thay thế -----------------------------------------------------
    private void getPossibleCombinationsVariables(int i, int n, int[] x, List<String> nullableVariables,
                                                  List<ProductionRule> ruleList, List<String> rightSide, String leftSide) {

        for (int j = 0; j <= 1; j++) {
            x[i] = j;
            if (i == n - 1) {
                getPossibleCombinationsRules(x, nullableVariables, ruleList, rightSide, leftSide);
            } else {
                getPossibleCombinationsVariables(i + 1, n, x, nullableVariables, ruleList, rightSide, leftSide);
            }
        }
    }

    private void getPossibleCombinationsRules(int[] x, List<String> nullableVariables,
                                              List<ProductionRule> ruleList, List<String> rightSide, String leftSide) {
        List<String> right = new LinkedList<>();
        right.addAll(rightSide);

        List<String> list = new LinkedList<>();
        for (int i = 0; i < x.length; i++) {
            if (x[i] == 1) {
                list.add(nullableVariables.get(i));
            }
        }
        if (list.size() > 0 && list.size() != right.size()) {
            right.removeAll(list);
            ruleList.add(new ProductionRule(leftSide, Ultis.getStringFromCollection(right, ""), " "));
        }

    }

    //tìm tất cả các tập con của tập các variable null để thay thế -----------------------------------------------------
    //END-------------------------Eliminate null -----------------------------------------------------------------

    // START-------------------------Eliminate unit -----------------------------------------------------------------
    public void eliminateUnit() {
        List<ProductionRule> unitRules = new LinkedList<>();
        //Tìm các unit rule---------------------------------------------------------------------------------------------
        for (ProductionRule rule : rules) {
            if (rule.getRightSide().size() == 1 && variables.contains(rule.getRightSide().get(0))) {
                unitRules.add(rule);
            }
        }
        rules.removeAll(unitRules);
        //Tìm các unit rule---------------------------------------------------------------------------------------------

        //thay thế các unit rule----------------------------------------------------------------------------------------
        List<ProductionRule> newRules = new LinkedList<>();
        int oldIndex = -1;
        int newIndex = rules.size();

        while (oldIndex != newIndex) {
            for (ProductionRule unitRule : unitRules) {
                String s = unitRule.getRightSide().get(0);
                for (ProductionRule productionRule : rules) {
                    if (productionRule.getLeftSide().equals(s)) {
                        newRules.add(new ProductionRule(unitRule.getLeftSide(),
                                Ultis.getStringFromCollection(productionRule.getRightSide(), " "), " "));
                    }
                }
            }
            rules.addAll(newRules);
            newRules.removeAll(newRules);
            oldIndex = newIndex;
            newIndex = rules.size();
        }
        //thay thế các unit rule----------------------------------------------------------------------------------------
    }
    //END-------------------------Eliminate unit -----------------------------------------------------------------
    //END--------------------------------------------------------Simplifying-------------------------------------------------------

    /**
     * TERM: Eliminate rules with non solitary terminals
     * <p>
     * [nếu vế phải có xuất hiện các chữ cái chính thay chúng bằng một biến mới và thêm quy tắc sinh
     * {biến mới} -> {chữ cái chính đó}]
     */
    private void term() {
        Set<ProductionRule> newRules = new HashSet<>();
        Set<ProductionRule> ruleSet = new HashSet<>();


        //----------Tìm các rule có dạng A -> bbbbbbbbbbb vs a in variables , b in symbols
        for (ProductionRule rule : rules) {
            if (symbols.containsAll(rule.getRightSide()) && rule.getRightSide().size() > 1
                    && Ultis.isOnly1StringInList(rule.getRightSide())) {
                ruleSet.add(rule);
            }
        }
        //----------Tìm các rule có dạng A -> bbbbbbbbbbb vs a in variables , b in symbols

        //-----------Thay thế các symbol bằng các var(trừ các rule vừa tìm đc ở trên )------------------------
        for (ProductionRule rule : rules) {
            if (!ruleSet.contains(rule)) {
                for (String s : rule.getRightSide()) {
                    if (symbols.contains(s)) {
                        String newVariables = s.toUpperCase() + "'";

                        Ultis.replaceAllStringInList(rule.getRightSide(), s, newVariables);
                        newRules.add(new ProductionRule(newVariables, s, " "));
                        variables.add(newVariables);
                    }
                }
            }
        }
        //-----------Thay thế các symbol bằng các var(trừ các rule vừa tìm đc ở trên )------------------------

        for (ProductionRule productionRule : ruleSet) {
            //----tất cả rule ở ruleSet có tất cả ptuwr ở vế phải là giống nhau --------
            String newVariables = productionRule.getRightSide().get(0).toUpperCase();
            for (String s : productionRule.getRightSide()) {
                newVariables += "'";
                Ultis.replaceFirstStringInList(productionRule.getRightSide(), s, newVariables);
                newRules.add(new ProductionRule(newVariables, s, " "));

                variables.add(newVariables);
            }
        }
        this.rules.addAll(newRules);
    }

    /**
     * BIN: Eliminate right-hand sides with more than 2 non terminals
     * <p>
     * với mỗi quy tắc dạng
     * A->X0 X1 ...X(n-1) X(n)
     * chuyển về dạng :(bằng cách duyệt theo hệ số X1 X2 ...X(n))
     * A-> X0 A0;---------TH đầu
     * A(0) -> X1 A1
     * .......
     * A(i-1) -> Xi A(i)
     * ........
     * A(n-1) -> X(n-1) X(n) ---------TH cuối
     */
    private void bin() {
        //---tìm các rule có độ dài lớn hơn 2----------------------
        Set<ProductionRule> ruleSet = new HashSet<>();

        for (ProductionRule productionRule : rules) {
            if (productionRule.getRightSide().size() > 2) {
                ruleSet.add(productionRule);
            }
        }
        //---tìm các rule có độ dài lớn hơn 2----------------------

        List<ProductionRule> newRule = new LinkedList<>();

        for (ProductionRule productionRule : ruleSet) {
            String var = productionRule.getLeftSide();
            String firstRight = productionRule.getRightSide().get(0) + " " + var + "0";

            variables.add(var + "0");

            newRule.add(new ProductionRule(var, firstRight, " "));
            for (int i = 1; i < productionRule.getRightSide().size() - 2; i++) {
                String right = productionRule.getRightSide().get(i) + " " + var + i;
                String left = var + (i - 1);
                newRule.add(new ProductionRule(left, right, " "));

                variables.add(var + i);
            }
            int n = productionRule.getRightSide().size();
            String right = productionRule.getRightSide().get(n - 2) + " " + productionRule.getRightSide().get(n - 1);
            String left = var + (n - 3);
            newRule.add(new ProductionRule(left, right, " "));

            variables.add(var + (n - 3));
        }


        Set<ProductionRule> set = new HashSet<>();
        for (ProductionRule productionRule : rules) {
            if (!ruleSet.contains(productionRule)) {
                set.add(productionRule);
            }
        }
        set.addAll(newRule);

        rules = set;
    }
}



