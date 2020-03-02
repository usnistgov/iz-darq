package gov.nist.healthcare.iz.darq.digest.domain;

public class Fraction {
	
	private int count;
	private int total;
	
	
	public void digest(Fraction f){
		this.count += f.count;
		this.total += f.total;
	}
	
	public Fraction() {
		super();
	}


	public static float round(float number, int scale) {
	    int pow = 10;
	    for (int i = 1; i < scale; i++)
	        pow *= 10;
	    float tmp = number * pow;
	    return ( (float) ( (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) ) ) / pow;
	}


	public Fraction(int count, int total) {
		super();
		this.count = count;
		this.total = total;
	}

	public void incCount(){
		this.count++;
		this.total++;
	}
	
	public void incTotal(){
		this.total++;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public double percent(){
		if(total == 0) return 0;
		return round((float) (count/ (double) total) * 100,2);
	}
	
	public static Fraction merge(Fraction a, Fraction b){
		return new Fraction(a.getCount()+b.getCount(), a.getTotal()+b.getTotal());
	}



	@Override
	public String toString() {
		return "Fraction [count=" + count + ", total=" + total + "]";
	}
	
	
	
}
