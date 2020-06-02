package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class IssueList {
    String[] issues;
    int capacity;
    int head;

    public IssueList(int capacity) {
        assert(capacity > 0);
        this.capacity = capacity;
        this.head = 0;
        this.issues = new String[capacity];
    }

    boolean add(String elm) {
        if(head == capacity) {
            return false;
        }
        else {
            this.issues[this.head++] = elm;
            return true;
        }
    }

    boolean addAllPossible(IssueList list) {
        for(String issue : list.issues) {
            if(!this.add(issue)) {
                return false;
            }
        }
        return true;
    }

    boolean addAllPossible(List<String> list) {
        for(String issue : list) {
            if(!this.add(issue)) {
                return false;
            }
        }
        return true;
    }

    public List<String> toList() {
        return Arrays.asList(Arrays.stream(this.issues, 0, this.head).toArray(String[]::new));
    }
}
