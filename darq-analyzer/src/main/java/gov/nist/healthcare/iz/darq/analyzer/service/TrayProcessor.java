package gov.nist.healthcare.iz.darq.analyzer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.domain.Field._CG;
import gov.nist.healthcare.iz.darq.analyzer.domain.Tray;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

public abstract class TrayProcessor {

	protected Function<Tray, Action> predicate;
	protected List<Tray> work;
	
	public TrayProcessor(Function<Tray, Action> predicate) {
		super();
		this.predicate = predicate;
		this.work = new ArrayList<>();
	}
	
	public List<Tray> process(ADFile file){
		this.work = new ArrayList<>();
		return inner(file);
	};
	
	public abstract List<Tray> inner(ADFile file);
	public abstract _CG analysisPath();
	
	protected void finalize(Tray t){
		if(predicate.apply(t).equals(Action.TAKE)) work.add(t.cloneTray());
	}
	
	protected boolean guard(Tray t) { 
		return predicate.apply(t).equals(Action.KILL);
	}
}
