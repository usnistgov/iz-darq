package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Map;

public class TablePayload {
	private int total;
	Map<String, Integer> codes;
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public Map<String, Integer> getCodes() {
		return codes;
	}
	public void setCodes(Map<String, Integer> codes) {
		this.codes = codes;
	}
}
