package hus.k61cis.minimizationDFA;

import java.util.HashSet;
import java.util.Set;

public class Partitioned {
    private Set<Set<String>> oldPartitions;
    private Set<Set<String>> currentPartitions;

    public Partitioned(Set<Set<String>> partitions) {
        this.currentPartitions = partitions;
        oldPartitions= new HashSet<>();
    }

    public void update(Set<Set<String>> partitions) {
        this.oldPartitions = this.currentPartitions;
        this.currentPartitions = partitions;
    }

    public boolean isNoChangeInPartitions() {
        return oldPartitions.size() == currentPartitions.size();
    }

    public Set<Set<String>> getCurrentPartitions() {
        return currentPartitions;
    }
}
