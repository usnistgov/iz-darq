package gov.nist.healthcare.iz.darq.digest.domain;

public class DetectionSum {

	// TODO Refactor code to change from positive/negative to found/notfound
	// Positive is + 1 when a detection was checked but not found
	private int positive;
	// Negative is + 1 when a detection is found
	private int negative;

	public boolean exists() {
		return negative >= 1;
	}

	public boolean isChecked() {
		return positive > 0 || negative > 0;
	}
	
	public DetectionSum() {
		super();
	}
	public DetectionSum(int positive, int negative) {
		super();
		this.positive = positive;
		this.negative = negative;
	}
	public int getPositive() {
		return positive;
	}
	public void setPositive(int positive) {
		this.positive = positive;
	}
	public int getNegative() {
		return negative;
	}
	public void setNegative(int negative) {
		this.negative = negative;
	}
	public static DetectionSum merge(DetectionSum a, DetectionSum b){
		return new DetectionSum(a.positive + b.positive, a.negative + b.negative);
	}
	public void addNegative(int i) {
		this.negative = this.negative + i;
	}
	public void addPositive(int i) {
		this.positive = this.positive + i;
	}
	@Override
	public String toString() {
		return "DetectionSum [positive=" + positive + ", negative=" + negative + "]";
	}
	
	
}
