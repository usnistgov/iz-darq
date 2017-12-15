package gov.nist.healthcare.iz.darq.analysis;

import gov.nist.healthcare.iz.darq.analysis.service.StatisticKind;

public class Statistic {
	
	private String statisticId;
	private int count = 0;
	private int total = 0;
	
	
	public Statistic() {
		super();
	}
	
	public Statistic(StatisticKind sk) {
		super();
		this.statisticId = sk.kindId();
	}
	
	public String getStatisticId() {
		return statisticId;
	}
	public void setStatisticId(String statisticId) {
		this.statisticId = statisticId;
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
	public void factorIn(Statistic s){
		assert(s.getStatisticId() == this.statisticId);
		
		this.count += s.getCount();
		this.total += s.getTotal();
	}
	public void countOne(){
		this.count++;
		this.total++;
	}
	
	public void ignoreOne(){
		this.total++;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statisticId == null) ? 0 : statisticId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Statistic other = (Statistic) obj;
		if (statisticId == null) {
			if (other.statisticId != null)
				return false;
		} else if (!statisticId.equals(other.statisticId))
			return false;
		return true;
	}
	
	

}
