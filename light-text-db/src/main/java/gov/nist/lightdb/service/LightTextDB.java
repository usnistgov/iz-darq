package gov.nist.lightdb.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;


import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;
import gov.nist.lightdb.domain.Source;
import gov.nist.lightdb.service.LightWeightIndexer.Offset;
import gov.nist.lightdb.service.LightWeigthSearcher.Chunk;
import gov.nist.lightdb.service.LightWeigthSearcher.ChunkItemIterator;
import gov.nist.lightdb.service.LightWeigthSearcher.ItemIterator;
import gov.nist.lightdb.service.LightWeigthSearcher.LinesIterator;


public class LightTextDB {
	Path mount;
	LightWeightIndexer.Builder builder;
	Map<Class<?>, ObjectComposer<?>> composers = new HashMap<>();
	
	public enum IndexType {
		MASTER, SLAVE
	}
	
	//---- CONSTRUCTORS
	
	private LightTextDB(Path mount) {
		this.mount = mount;
		index().toFile().mkdirs();
		data().toFile().mkdirs();
	}
	
	private LightTextDB(String path) {
		this.mount = Paths.get(path);
	}
	
	private LightTextDB() throws IOException {
		File dir = com.google.common.io.Files.createTempDir();
		
		if(dir.mkdirs()){
			this.mount = Paths.get(dir.getAbsolutePath());
		}
		else {
			throw new IOException();
		}
	}
	
	// ---- COMPOSERS
	
	public <T> void addComposer(Class<T> clazz, ObjectComposer<T> obj){
		this.composers.put(clazz, obj);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ObjectComposer<T> getComposer(Class<T> clazz){
		return (ObjectComposer<T>) this.composers.get(clazz);
	}
	
	// ---- INDEX
	
	public LightTextDB index(IndexType idx, Source source, LightWeightIndexer indexer) throws Exception {
		
		if(idx.equals(IndexType.MASTER) && this.anchored()) throw new IllegalAccessError("DB already anchored at "+index());		
		FileUtils.copyInputStreamToFile(source.getPayload(), Paths.get(data().toString(), source.getType().i.file).toFile());
		switch(idx){
		case MASTER : indexer.master(data(), index(), source.getType());
		break;
		case SLAVE : indexer.slave(data(), index(), source.getType());
		}
		
		return this;
	}
	
	public LightTextDB index(IndexType idx, EntityType type, LightWeightIndexer indexer) throws Exception {
		
		if(idx.equals(IndexType.MASTER) && this.anchored()) throw new IllegalAccessError("DB already anchored at "+index());		
		switch(idx){
		case MASTER : indexer.master(data(), index(), type);
		break;
		case SLAVE : indexer.slave(data(), index(), type);
		}
		
		return this;
	}
	
	// ---- SEARCH 
	
	public Map<EntityType, List<String>> get(String ID, EntityType... types) throws IOException {
		LightWeigthSearcher searcher = new LightWeigthSearcher();
		return searcher.get(index(), data(), ID, types);
	}
	
	public LinesIterator iterator(EntityType... types) throws IOException {
		LightWeigthSearcher searcher = new LightWeigthSearcher();
		return searcher.iterator(this.index(), this.data(), types);
	}
	
	public <T> ItemIterator<T> iterator(Class<T> clazz) throws IOException, IllegalArgumentException {
		if(!this.composers.containsKey(clazz)){
			throw new IllegalArgumentException("No Composer for "+clazz.getName());
		}
		
		LightWeigthSearcher searcher = new LightWeigthSearcher();
		ObjectComposer<T> composer = this.getComposer(clazz);
		EntityType[] types = composer.types().toArray(new EntityType[0]);
		return searcher.iterator(this.index(), this.data(), composer, types);
	}
	
	public <T> List<ChunkItemIterator<T>> chunkIterator(Class<T> clazz, int size) throws IOException, IllegalArgumentException {
		if(!this.composers.containsKey(clazz)){
			throw new IllegalArgumentException("No Composer for "+clazz.getName());
		}
		
		LightWeigthSearcher searcher = new LightWeigthSearcher();
		ObjectComposer<T> composer = this.getComposer(clazz);
		List<Chunk> chunks = chunks(size);
		
		EntityType[] types = composer.types().toArray(new EntityType[0]);
		return searcher.iterator(this.index(), this.data(), chunks, composer, types);
	}
	
	// ---- CHUNKS
	
	private List<Chunk> chunks(int size) throws IOException{
		List<Chunk> chunks = new ArrayList<>();
		
		long masterLines = Files.lines(Paths.get(index().toString(), "master.idx")).parallel().count();
		int chunkSize = size > masterLines ? (int) masterLines : (int) masterLines / size;
		
		try (
				Stream<String> input = Files.lines(Paths.get(index().toString(), "master.idx"));	
		) {
			
			Offset offset = new Offset();
			input.forEach(str -> {
				
				//-- CHUNK CHECKPOINT - LAST GET ALL
				if(offset.count() % chunkSize == 0 && (masterLines - offset.count()) >= chunkSize){
					if((masterLines - offset.count()) < chunkSize * 2){
						chunks.add(new Chunk(offset.get(),(int) (masterLines - offset.count())));
					}
					else {
						chunks.add(new Chunk(offset.get(), chunkSize));
					}
				}
				offset.inc((str+"\n").getBytes().length);
			});
			
			return chunks;
		}
		catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
	}
	
	// ---- SANITY
	
	public boolean anchored(){
		return Paths.get(index().toString(), "master.idx").toFile().exists() && this.data().toFile().exists();
	}
	
	// ---- FACTORIES 
	
	public static LightTextDB open(String path) throws IllegalArgumentException {
		if(path != null && !path.isEmpty()){
			LightTextDB db = new LightTextDB(path);
			if(!db.anchored()) throw new IllegalArgumentException("Could not open database at "+path+" no index found");
			else return db;
		}
		else throw new IllegalArgumentException();
	}
	
	public static LightTextDB refresh(String path, EntityType master, LightWeightIndexer indexer, EntityType... slaves) throws Exception {
		if(path != null && !path.isEmpty()){
			LightTextDB db = new LightTextDB(path);
			db.index(IndexType.MASTER, master, indexer);
			for(EntityType slave : slaves){
				db.index(IndexType.SLAVE, slave, indexer);
			}
			return db;
		}
		else throw new IllegalArgumentException();
	}
	
	private static LightTextDB create(Source master, List<Source> slaves, LightWeightIndexer indexer) throws Exception {
		LightTextDB db = new LightTextDB();
		db.index(IndexType.MASTER, master, indexer);
		for(Source slave : slaves){
			db.index(IndexType.SLAVE, slave, indexer);
		}
		return db;
	}
	
	private static LightTextDB create(Path mount, Source master, List<Source> slaves, LightWeightIndexer indexer) throws Exception {
		LightTextDB db = new LightTextDB(mount);
		db.index(IndexType.MASTER, master, indexer);
		for(Source slave : slaves){
			db.index(IndexType.SLAVE, slave, indexer);
		}
		return db;
	}
	
	public static LightTextDBBuilder builder() {
		return new LightTextDBBuilder();
	}
	
	public static class LightTextDBBuilder {
		boolean dir = false;
		Path dirp;
		List<Source> slaves;
		Source master;
		LightWeightIndexer indexer;
		Map<Class<?>, ObjectComposer<?>> composers = new HashMap<>();
		
		public LightTextDBBuilder(){
			this.slaves = new ArrayList<>();
		}
		
		public LightTextDBBuilder use(String dir) {
			this.dir = true;
			this.dirp = Paths.get(dir);
			return this;
		};
		
		public LightTextDBBuilder slave(Source slave) {
			this.slaves.add(slave);
			return this;
		};
		
		public LightTextDBBuilder slaves(Source... slaves) {
			this.slaves.addAll(Arrays.asList(slaves));
			return this;
		};
		
		public LightTextDBBuilder slaves(List<Source> slaves) {
			this.slaves.addAll(slaves);
			return this;
		};
		
		public LightTextDBBuilder indexer(LightWeightIndexer indexer) {
			this.indexer = indexer;
			return this;
		};
		
		public LightTextDB openWithMaster(Source master) throws Exception {
			return this.dir ? LightTextDB.create(this.dirp,master, slaves, indexer) : LightTextDB.create(master, slaves, indexer);
		};
		
	}
	


	// ---- PATHS 
	
	public Path index(){
		return Paths.get(mount.toString(),"index");
	}
	
	public Path data(){
		return Paths.get(mount.toString(),"data");
	}
	
	public Path mount(){
		return mount;
	}

	public Path chunk(){
		return Paths.get(index().toString(), "chunks.idx");
	}
}
