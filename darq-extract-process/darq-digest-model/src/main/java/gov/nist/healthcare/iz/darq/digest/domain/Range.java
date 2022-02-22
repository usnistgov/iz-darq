package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Objects;

public class Range implements Comparable<Range> {
	private Bracket min;
	private Bracket max;

	public Range() {
		super();
	}
	public Range(Bracket min, Bracket max) {
		super();
		this.min = min;
		this.max = max;
	}
	public Bracket getMin() {
		return min;
	}

	public Bracket getMax() {
		return max;
	}

	@Override
	public String toString() {
		return  min + " -> " + max;
	}

	@Override
	public int compareTo(Range o) {
		return min.compareTo(o.min);
	}

	public boolean same(Range r){
		return min.compareTo(r.min) == 0 && max.compareTo(r.max) == 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Range other = (Range) obj;
		return this.same(other);
	}

	@Override
	public int hashCode() {
		return Objects.hash(min, max);
	}
}