package gov.nist.lightdb.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

import gov.nist.lightdb.domain.Line;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nist.lightdb.domain.EntityTypeRegistry;
import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;
import gov.nist.lightdb.exception.InvalidValueException;
import gov.nist.lightdb.service.LightWeightIndexer.Document;

public class LightWeigthSearcher implements Searcher {
	
	public abstract class AbstractIterator<T> implements Iterator<T>{

		protected int i;
		protected int size;
		protected Path index;
		protected Path data;
		protected EntityType[] types;
		protected Scanner stream;
		protected Map<EntityType,RandomAccessFile> files = new HashMap<>(); 
		final Logger logger = LoggerFactory.getLogger(LightWeigthSearcher.class.getName());

		
		private AbstractIterator(Path index, Path data, EntityType... types) throws IOException{
			i = 0;
			this.index = index;
			this.data = data;
			this.types = types;
			this.stream = new Scanner(new FileInputStream(Paths.get(index.toString(), "master.idx").toFile()));
			for(EntityType t : types){
				files.put(t, new RandomAccessFile(Paths.get(data.toString(), t.i.file).toString(), "r"));
			}
		}
		
		@Override
		public synchronized boolean hasNext() {
			if(!this.stream.hasNext()){
				this.close();
				return false;
			}
			return true;
		}
		
		public void close(){
			for(EntityType type : files.keySet()){
				try {
					files.get(type).close();
				} catch (IOException e) {
					logger.error("[Oops error while closing Chunk]",e);
				}
			}
		}
		
		public int records(){
			return this.i;
		}

		@Override
		public abstract T next();
	}
	
	public class LinesIterator extends AbstractIterator<Map<EntityType, List<String>>>{
		final Logger logger = LoggerFactory.getLogger(LightWeigthSearcher.class.getName());

		
		private LinesIterator(Path index, Path data, EntityType... types) throws IOException{
			super(index,data,types);
		}
		
		@Override
		public Map<EntityType, List<String>> next() {
			Map<EntityType, List<String>> result = new HashMap<EntityType, List<String>>();
			
			try {
				
				String line = stream.nextLine();
				IndexLineReader reader = new IndexLineReader(line);
				for(EntityType t : types){
					if(reader.types().contains(t)){
						result.put(t, read(files.get(t), reader.get(t)));
					}
				}
				this.i++;
				return result;
			}
			catch(Exception e){
				logger.error("ERROR",e);
				return null;
			}
			
		}
		
	}
	
	public class ItemIterator<T> extends AbstractIterator<T>{
		ObjectComposer<T> composer;
		final Logger logger = LoggerFactory.getLogger(LightWeigthSearcher.class.getName());

		private ItemIterator(ObjectComposer<T> composer, Path index, Path data, EntityType... types) throws IOException{
			super(index,data,types);
			this.composer = composer;
		}
		
		@Override
		public synchronized T next() {
			Map<EntityType, List<String>> result = new HashMap<EntityType, List<String>>();

			try {
				
				String line = stream.nextLine();
				IndexLineReader reader = new IndexLineReader(line);
				for(EntityType t : types){
					if(reader.types().contains(t)){
						result.put(t, read(files.get(t), reader.get(t)));
					}
				}

				this.i++;
				//TODO Expose ID
				//TODO FIX
				return composer.compose("", null);
			}
			catch(Exception e){
				logger.error("ERROR",e);
				return null;
			}
			
		}
		
	}
	
	public static class ChunkItemIterator<T> implements Iterator<T>{
		
		ObjectComposer<T> composer;
		protected int start;
		protected int ID;
		protected int size;
		protected int i;
		protected Path index;
		protected Path data;
		protected boolean forceStop = false;
		protected EntityType[] types;
		protected RandomAccessFile stream;
		protected Map<EntityType,RandomAccessFile> files = new HashMap<>(); 
		static final Logger logger = LoggerFactory.getLogger(LightWeigthSearcher.class.getName());
		
		private ChunkItemIterator(Path index, Path data, Chunk chunk, ObjectComposer<T> composer, int x, EntityType... types) throws IOException{
			i = 0;
			this.composer = composer;
			this.size = chunk.size;
			this.index = index;
			this.start = chunk.start;
			this.data = data;
			this.ID = x;
			this.types = types;
			this.stream = new RandomAccessFile(Paths.get(index.toString(), "master.idx").toString(),"r");
			this.stream.seek(start);
			
			for(EntityType t : types){
				files.put(t, new RandomAccessFile(Paths.get(data.toString(), t.i.file).toString(), "r"));
			}
		}
		
		@Override
		public synchronized boolean hasNext() {
			if(this.i >= this.size && !forceStop){
				this.close();
				return false;
			}
			return true;
		}
		
		
		public int getSize() {
			return size;
		}

		public void close(){
			for(EntityType type : files.keySet()){
				try {
					files.get(type).close();
				} catch (IOException e) {
					logger.error("[Oops error while closing Chunk "+ID+"]",e);
				}
			}
		}
		
		public int records(){
			return this.i;
		}
		
		@Override
		public synchronized T next() {
			
			Map<EntityType, List<String>> result = new HashMap<EntityType, List<String>>();
		
			try {
				
				String line = stream.readLine();
				
				IndexLineReader reader = new IndexLineReader(line);
				for(EntityType t : types){
					if(reader.types().contains(t)){
						result.put(t, read(files.get(t), reader.get(t)));
					}
				}

				this.i++;
				//TODO Expose ID
				//TODO Fix
				return composer.compose("", null);
			}
			catch(Exception e){
				logger.error("[Oops error while getting record from Chunk "+ID+"]",e);
				this.forceStop = true;
				return null;
			}
			
		}
		
		public synchronized T nextRecord() throws InvalidValueException {
			Map<EntityType, List<Line>> result = new HashMap<EntityType, List<Line>>();
			
			try {

				String line;
				do {
					line = stream.readLine();
				} while(!LightWeightIndexer.valid_mtu(line));

				if(LightWeightIndexer.valid_mtu(line)){
					logger.info("READING FROM CHUNK ["+ID+"][ VALID ] : Index Line "+line);
				}
				else {
					logger.info("READING FROM CHUNK ["+ID+"][INVALID] : Index Line "+line);
				}

				IndexLineReader reader = new IndexLineReader(line);
				for(EntityType t : types){
					if(reader.types().contains(t)){
						//TODO FIX
//						result.put(t, read(files.get(t), reader.get(t)));
					}
				}

				this.i++;
				return composer.compose("", result);
			}
			catch(IOException e){
				logger.error("[Oops error while getting record from Chunk "+ID+"]",e);
				this.forceStop = true;
				return null;
			}
			
		}
		
	}
	
	public static class Chunk {
		public int start;
		public int size;
		public int line;
		public Chunk(int start, int size, int line) {
			super();
			this.start = start;
			this.size = size;
			this.line = line;
		}
	}
	
	public static class IndexLineReader {
		private String id;
		private Map<EntityType,List<Document.Load>> loads;
		
		public IndexLineReader(String line){
			loads = new HashMap<>();
			String[] sub = line.split(">");
			this.id = sub[0];
			parse(line);
		}
		
		public static boolean isId(String ID, String line){
			String[] sub = line.split(">");
			if(sub[0].equals(ID)){
				return true;
			}
			return false;
		}
		
		public Set<EntityType> types(){
			return loads.keySet();
		}
		
		public IndexLineReader(String line, String id) throws IllegalArgumentException {
			loads = new HashMap<>();
			String[] sub = line.split(">");
			this.id = sub[0];
			if(this.id.equals(id)){
				parse(line);
			}
			else throw new IllegalArgumentException();
		}
		
		private void parse(String line){
			String[] chunks = line.split(">")[1].split("\\|");
			for(String chunk : chunks){
				if(chunk.isEmpty())
					break;

				String[] chunkSub = chunk.split(":");
				String eId = chunkSub[0];
				List<Document.Load> L = crunch(chunkSub);
				EntityType type = EntityTypeRegistry.byId(eId);
				if(loads.containsKey(type)){
					loads.get(type).addAll(L);
				}
				else {
					loads.put(EntityTypeRegistry.byId(eId), L);
				}
			}
		}
		
		private List<Document.Load> crunch(String[] loads){
			List<Document.Load> in = new ArrayList<>();
			for(int i = 1; i < loads.length; i++){
				String load = loads[i];
				int offset = Integer.parseInt(load.split("-")[0]);
				int span = Integer.parseInt(load.split("-")[1]);
				in.add(new Document.Load(offset,span));
			}
			return in;
		}
		public String getId() {
			return id;
		}
		public List<Document.Load> get(EntityType t){
			return loads.get(t);
		}
	}

	@Override
	public Map<EntityType, List<String>> get(Path index, Path data, String ID, EntityType... types) throws IOException {
		return this.get(index, data, ID,IndexLineReader::isId, types);
	}
	
	@FunctionalInterface
	public interface PassFilter {
		boolean apply(String id, String line);
	}
	
	public LinesIterator iterator(Path index, Path data, EntityType... types) throws IOException{
		return new LinesIterator(index, data, types);
	}
	 
	public <T> ItemIterator<T> iterator(Path index, Path data, ObjectComposer<T> composer, EntityType... types) throws IOException{
		return new ItemIterator<T>(composer, index, data, types);
	}
	
	public <T> List<ChunkItemIterator<T>> iterator(Path index, Path data, List<Chunk> chunks, ObjectComposer<T> composer, EntityType... types) throws IOException{
		List<ChunkItemIterator<T>> l = new ArrayList<>();
		int i = 0;
		for(Chunk chunk : chunks){
			l.add(new ChunkItemIterator<>(index, data, chunk, composer, i++, types));
		}
		return l;
	}
	
	
	public Map<EntityType, List<String>> get(Path index, Path data, String ID, PassFilter predicate, EntityType... types) throws IOException {
		Map<EntityType, List<String>> result = new HashMap<EntityType, List<String>>();
		Map<EntityType,RandomAccessFile> files = new HashMap<>(); 
		for(EntityType t : types){
			files.put(t, new RandomAccessFile(Paths.get(data.toString(), t.i.file).toString(), "r"));
		}
		try (
			Stream<String> input = Files.lines(Paths.get(index.toString(), "master.idx"));
		) {
		
			Optional<String> option = input.filter(x -> predicate.apply(ID, x)).findFirst();
			if(option.isPresent()){
				String str = option.get();
				IndexLineReader reader = new IndexLineReader(str);
				for(EntityType t : types){
					if(reader.types().contains(t)){
						result.put(t, read(files.get(t), reader.get(t)));
					}
				}
			}
			else {
				return null;
			}
		}
		
		for(EntityType type : files.keySet()){
			files.get(type).close();
		}
		
		return result;
	}
	
	
	
	private static List<String> read(RandomAccessFile f,List<Document.Load> loads) throws IOException {
		List<String> result = new ArrayList<>();
		for(Document.Load load : loads){
			f.seek(load.offset);
			byte[] bytes = new byte[(int) load.span];
			for(int i = 0; i < load.span; i++){
				bytes[i] = f.readByte();
			}
			result.add(new String(bytes, Charset.forName("UTF-8")));
		}
		return result;
	}

}
