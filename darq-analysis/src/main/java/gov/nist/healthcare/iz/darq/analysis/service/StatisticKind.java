package gov.nist.healthcare.iz.darq.analysis.service;

import java.util.List;

import gov.nist.healthcare.iz.darq.analysis.Statistic;
import gov.nist.healthcare.iz.darq.parser.type.DataUnit;

public abstract class StatisticKind {
	
	public abstract String kindId();
	public abstract List<String> only();
	protected abstract <T> boolean consider(DataUnit<T> dataUnit);
	public <T> Statistic process(DataUnit<T> dataUnit){
		Statistic s = new Statistic(this);
		if(consider(dataUnit)){
			s.countOne();
		}
		else {
			s.ignoreOne();
		}
		return s;
	}
	
}
