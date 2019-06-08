package hus.k61cis.minimizationDFA;

import java.util.HashSet;
import java.util.Set;

class Partitioned {
    private Set<Set<String>> oldPartitions;
    private Set<Set<String>> currentPartitions;

    Partitioned(Set<Set<String>> partitions) {
        this.currentPartitions = partitions;
        oldPartitions = new HashSet<>();
    }

    void update(Set<Set<String>> partitions) {
        this.oldPartitions = this.currentPartitions;
        this.currentPartitions = partitions;
    }

    boolean isNoChangeInPartitions() {
        return oldPartitions.size() == currentPartitions.size();
    }

    Set<Set<String>> getCurrentPartitions() {
        return currentPartitions;
    }
}
