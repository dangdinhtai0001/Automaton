package hus.k61cis.CKY;

import hus.k61cis.Grammar.ContextFreeGrammar;
import hus.k61cis.Grammar.ProductionRule;
import hus.k61cis.Ultis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CKY {
    //-------------------tìm vế trái của rule khi có vế phả--------------------------
    private String findVarialesSource(ContextFreeGrammar grammar, String target1, String target2, String regex) {
        if (!target1.contains(regex) && !target2.contains(regex)) {
            String target = target1 + target2;
            return findVarialesSource(grammar, target);
        } else {
            if (!target1.contains(regex) && target2.contains(regex)) {
                String temp = target2;
                target2 = target1;
                target1 = temp;
            }
            Set<String> result = new HashSet<>();

            String[] array1 = target1.split(regex);

            for (String s : array1) {
                String r = findVarialesSource(grammar, s + target2);
                String r1 = findVarialesSource(grammar, target2 + s);

                if (!r.equals("0")) {
                    result.add(r);
                }
                if (!r1.equals("0")) {
                    result.add(r1);
                }
            }
            return Ultis.getStringFromCollection(result, regex);
        }
    }

    private String findVarialesSource(ContextFreeGrammar grammar, String target) {
        List<String> list = Arrays.asList(target.split(""));
        Set<String> result = new HashSet<>();

        for (ProductionRule productionRule : grammar.getRules()) {
            if (Ultis.equalList(productionRule.getRightSide(), list)) {
                result.add(productionRule.getLeftSide());
            }
        }

        if (result.size() == 0) {
            return "0";
        }
        return Ultis.getStringFromCollection(result, "&");
    }
    //-------------------tìm vế trái của rule khi có vế phả--------------------------

    public boolean algorithm(ContextFreeGrammar grammar, String words) {
        String[] array = (" " + words).split("");

        int wordLength = words.length();
        String[][] table;
        table = new String[wordLength][wordLength + 1];

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                table[i][j] = "0";
            }
        }

        for (int i = 1; i <= wordLength; i++) {
            table[i - 1][i] = findVarialesSource(grammar, array[i], "", "");
        }

        for (int i = 2; i <= wordLength; i++) {
            for (int j = i - 2; j >= 0; j--) {
                for (int k = j + 1; k <= i - 1; k++) {
                    String B = table[j][k];
                    String C = table[k][i];

                    String A = findVarialesSource(grammar, B, C, "&");

                    if (table[j][i].equals("0")) {
                        table[j][i] = A;
                    }

//                    System.out.println("j : " + j + "\tk : " + k + "\ti  : " + i + "\tB : " + B + "\tC : " + C + "\tA : " + A);
                }
            }
        }
        List<String> list = Arrays.asList(table[0][wordLength].split("&"));

        Ultis.printMatrix(table);

        return list.contains(grammar.getStartVariable());
    }

}
