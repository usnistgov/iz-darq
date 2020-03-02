package gov.nist.healthcare.iz.darq.digest.domain;

public class ExtractFraction {
    public int valued;
    public int excluded;
    public int notCollected;
    public int notExtracted;
    public int valuePresent;
    public int valueNotPresent;
    public int valueLength;
    public int empty;
    public int total;

    public ExtractFraction() {
    }

    public void incValued() {
        this.valued++;
        this.total++;
    }

    public void incExcluded() {
        this.excluded++;
        this.total++;
    }

    public void incNotExtracted() {
        this.notExtracted++;
        this.total++;
    }

    public void incNotCollected() {
        this.notCollected++;
        this.total++;
    }

    public void incValuePresent() {
        this.valuePresent++;
        this.total++;
    }

    public void incValueNotPresent() {
        this.valueNotPresent++;
        this.total++;
    }

    public void incValueLength() {
        this.valueLength++;
        this.total++;
    }

    public void incEmpty() {
        this.empty++;
        this.total++;
    }

    public int getValued() {
        return valued;
    }

    public void setValued(int valued) {
        this.valued = valued;
    }

    public int getExcluded() {
        return excluded;
    }

    public void setExcluded(int excluded) {
        this.excluded = excluded;
    }

    public int getNotCollected() {
        return notCollected;
    }

    public void setNotCollected(int notCollected) {
        this.notCollected = notCollected;
    }

    public int getNotExtracted() {
        return notExtracted;
    }

    public void setNotExtracted(int notExtracted) {
        this.notExtracted = notExtracted;
    }

    public int getValuePresent() {
        return valuePresent;
    }

    public void setValuePresent(int valuePresent) {
        this.valuePresent = valuePresent;
    }

    public int getValueNotPresent() {
        return valueNotPresent;
    }

    public void setValueNotPresent(int valueNotPresent) {
        this.valueNotPresent = valueNotPresent;
    }

    public int getValueLength() {
        return valueLength;
    }

    public void setValueLength(int valueLength) {
        this.valueLength = valueLength;
    }

    public int getEmpty() {
        return empty;
    }

    public void setEmpty(int empty) {
        this.empty = empty;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public static ExtractFraction merge(ExtractFraction a, ExtractFraction b) {
        ExtractFraction fr = new ExtractFraction();
        fr.setValued(a.getValued() + b.getValued());
        fr.setExcluded(a.getExcluded() + b.getExcluded());
        fr.setNotCollected(a.getNotCollected() + b.getNotCollected());
        fr.setNotExtracted(a.getNotExtracted() + b.getNotExtracted());
        fr.setValuePresent(a.getValuePresent() + b.getValuePresent());
        fr.setValueNotPresent(a.getValueNotPresent() + b.getValueNotPresent());
        fr.setValueLength(a.getValueLength() + b.getValueLength());
        fr.setEmpty(a.getEmpty() + b.getEmpty());
        fr.setTotal(a.getTotal() + b.getTotal());
        return fr;
    }

    @Override
    public String toString() {
        return "ExtractFraction{" +
                "valued=" + valued +
                ", excluded=" + excluded +
                ", notCollected=" + notCollected +
                ", notExtracted=" + notExtracted +
                ", valuePresent=" + valuePresent +
                ", valueNotPresent=" + valueNotPresent +
                ", valueLength=" + valueLength +
                ", empty=" + empty +
                ", total=" + total +
                '}';
    }
}