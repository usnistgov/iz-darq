package gov.nist.extract.lucene.index;

class Counter {
    int i;

    public Counter(int i){
        this.i = i;
    }

    int inc() {
        i++;
        return i;
    }

    int val() {
        return i;
    }

    void set(int i) {
        this.i = i;
    }
}
