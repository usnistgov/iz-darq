package gov.nist.lightdb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import gov.nist.lightdb.domain.Record;
import gov.nist.lightdb.exception.InvalidValueException;
import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;


public class ObjectComposer<T> {
	
	@FunctionalInterface
	public interface Compose<T> {
		T apply(String ID, Pieces pieces);
	}
	
	Map<EntityType, Parser<? extends Record>> parsers;
	Compose<T> composer;
	
	public static class CastHelper {
		
		@SuppressWarnings("unchecked")
		public static <E extends Record> E as(Class<E> clazz, Record record){
			if(clazz.isInstance(record)){
				return (E) record;
			}
			throw new IllegalArgumentException();
		}
		
		@SuppressWarnings("unchecked")
		public static <E extends Record> List<E> as(Class<E> clazz, List<Record> records){
			List<E> result = new ArrayList<>();
			if(records == null){
				return result;
			}
			for(Record r : records){
				if(clazz.isInstance(r)){
					result.add((E) r);
				}
				else {
					throw new IllegalArgumentException();
				}
			}
			return result;
		}
	}
	
	public static class ObjectComposerBuilder<U> {
		Map<EntityType, Parser<? extends Record>> parsers;
		Compose<U> compose;
		
		public ObjectComposerBuilder() {
			parsers = new HashMap<>();
		}
		
		public <V extends Record> ObjectComposerBuilder<U> parser(EntityType _for, Parser<V> parser){
			this.parsers.put(_for, parser);
			return this;
		}
		
		public ObjectComposerBuilder<U> composer(Compose<U> compose){
			this.compose = compose;
			return this;
		}
		
		public ObjectComposer<U> build(){
			return new ObjectComposer<>(parsers, compose);
		}
	}
	
	public static <U> ObjectComposerBuilder<U> builder(){
		return new ObjectComposerBuilder<U>();
	}

	private ObjectComposer(Map<EntityType, Parser<? extends Record>> parsers, Compose<T> compose) {
		super();
		this.parsers = parsers;
		this.composer = compose;
	}
	
	public List<EntityType> types(){
		return new ArrayList<>(this.parsers.keySet());
	}
	
	public T compose(String ID, Map<EntityType,List<String>> lines) throws InvalidValueException {
		Map<EntityType, List<Record>> entities = new HashMap<>();
		for(EntityType t : lines.keySet()){
			List<Record> list = new ArrayList<>();
			for(String line : lines.get(t)){
				try {
					list.add(this.parsers.get(t).parse(line));
				} catch (InvalidValueException e) {
					throw new InvalidValueException("["+t.name+"] " + e.getMessage());
				}
			}
			entities.put(t, list);
		}
		return this.composer.apply(ID, new Pieces(entities));
	}
	
	public static class Pieces {
		Map<EntityType, List<Record>> entities;
		
		private Pieces(Map<EntityType, List<Record>> entities){
			this.entities = entities;
		}
		
		@SuppressWarnings("unchecked")
		public <E> List<E> get(EntityType type){
			List<Record> records = entities.get(type);
			return CastHelper.as(type.i.clazz, records);
		}
	}
	
	
	
	
}
