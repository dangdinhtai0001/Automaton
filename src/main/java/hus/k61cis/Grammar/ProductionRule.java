package hus.k61cis.Grammar;

import hus.k61cis.Ultis;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
public class ProductionRule {
    private String leftSide;
    private List<String> rightSide;

    ProductionRule(String leftSide, String rightSide, String regex) {
        this.leftSide = leftSide;

        String[] r = rightSide.split(regex);
        this.rightSide = Arrays.asList(r);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(leftSide);

        str.append("->");
        for (String s : rightSide) {
            str.append(" ").append(s);
        }
        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionRule rule = (ProductionRule) o;
        return this.leftSide.equals(rule.leftSide) &&
                Ultis.equalList(rightSide, rule.rightSide);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftSide, rightSide);
    }
}
