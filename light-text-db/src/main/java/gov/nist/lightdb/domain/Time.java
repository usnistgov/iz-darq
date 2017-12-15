package gov.nist.lightdb.domain;

public enum Time {
	INSTANCE;

	private long start;
	
	public void init(){
		start = System.currentTimeMillis();
	}
	
	public void checkPoint(String name){
		long elapsed = System.currentTimeMillis() - start;
		System.out.println(String.format("[%s] Elapsed : %d", name, elapsed));
		this.init();
	}
	
	public void elapsed(String name){
		long elapsed = System.currentTimeMillis() - start;
		System.out.println(String.format("[%s] Elapsed : %d", name, elapsed));
	}
}
