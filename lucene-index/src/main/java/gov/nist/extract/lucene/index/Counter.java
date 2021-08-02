package gov.nist.extract.lucene.index;

class Counter {
    private int i;

    Counter(int i){
        this.i = i;
    }

    int inc() {
        i++;
        return i;
    }

    int value() {
        return i;
    }
}
