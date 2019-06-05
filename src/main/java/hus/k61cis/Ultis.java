package hus.k61cis;

import hus.k61cis.Grammar.ProductionRule;

import java.util.*;

public class Ultis {
    public static Set<String> getSetFromString(String s, String regex) {
        Set<String> set = new HashSet<>();
        String[] a = s.split(regex);
        for (int i = 0; i < a.length; i++) {
            set.add(a[i]);
        }
        return set;
    }

    public static String getStringFromCollection(Collection<String> collection, String regex) {
        String s = collection.toString();
        s = s.substring(1, s.length() - 1).replaceAll(" ", "");
        return s.replaceAll(",", regex);
    }

    public static List<ProductionRule> coppyRule(Collection<ProductionRule> collection) {
        List<ProductionRule> rules = new LinkedList<>();
        for (ProductionRule productionRule : collection) {
            rules.add(productionRule);
        }
        return rules;
    }

    public static void replaceAllStringInList(List<String> collection, String old, String replace) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).equals(old)) {
                collection.set(i, replace);
            }
        }
    }

    public static void replaceFirstStringInList(List<String> collection, String old, String replace) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).equals(old)) {
                collection.set(i, replace);
                break;
            }
        }
    }

    public static boolean isOnly1StringInList(List<String> list) {
        String first = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (!list.get(i).equals(first)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalList(List<String> list1, List<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }

}
