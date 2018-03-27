package gov.nist.healthcare.iz.darq.digest.domain;

public class AgeGroupCount implements Comparable<AgeGroupCount> {

	Range range;
	int nb;

	
	public AgeGroupCount() {
		super();
	}

	public AgeGroupCount(Range range, int nb) {
		super();
		this.range = range;
		this.nb = nb;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public int getNb() {
		return nb;
	}

	public void setNb(int nb) {
		this.nb = nb;
	}

	@Override
	public int compareTo(AgeGroupCount o) {
		return this.range.compareTo(o.range);
	}

}
