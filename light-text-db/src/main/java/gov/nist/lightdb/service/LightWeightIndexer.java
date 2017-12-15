package gov.nist.lightdb.service;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;

public class LightWeightIndexer implements Indexer {

	LineProfile profile;
	HashMap<String, List<Document.Load>> buffer;
	int commitSize = 1000;
	int chunks = 5;
	
	public class Worker {
		public boolean available;
		private ExecutorService executor = Executors.newFixedThreadPool(1);
		
		public void commit(Path index, String eId, HashMap<String, List<Document.Load>> load){
			Callable<Void> task = () -> {
				LightWeightIndexer.commit(index, eId, load);
				return null;
			};
			executor.submit(task);
		};
		
		public void shutdown() throws InterruptedException{
			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.HOURS);
		}
		
		public boolean available(){
			return ((ThreadPoolExecutor) executor).getActiveCount() == 0;
		}
	}
	
	public static class Document {
		
		public static class Load {
			public int offset;
			public int span;
			public Load(int offset, int span) {
				super();
				this.offset = offset;
				this.span = span;
			}
		}
		
		private String id;
		private int offset;
		private int span;
		
		public class Builder {
			
			String line;
			int size = 0;
			int offset = 0;
			
			private void init(String line, int offset){
				this.line = line;
				size = this.line.getBytes(Charset.forName("UTF-8")).length;
				this.offset = offset;
			}
			
			public Document first(int x) throws IllegalArgumentException {
				if(x <= 0) throw new IllegalArgumentException();
				String sub = this.line.substring(0, x - 1);
				return new Document(sub,offset,size);
			}
			
			public Document until(char x) throws IllegalArgumentException {
				if(!line.contains(x+"")) throw new IllegalArgumentException();
				else {
					String sub = line.split(x+"")[0];
					return new Document(sub,offset,size);
				}
			}
		
		}

		private Document(){
			
		}
		
		private Document(String id, int offset, int span) {
			super();
			this.id = id;
			this.span = span;
			this.offset = offset;
		}
		
		private Load load(){
			return new Load(offset, span);
		}
		
	}
	
	@FunctionalInterface
	public interface LineProfile {
		public Document build(Document.Builder builder);
	}
	
	public class Builder {
		LineProfile profile;
		int commit = 100;
		int chunk = 5;
		
		public Builder commit(int x){
			this.commit = x;
			return this;
		}
		
		public Builder chunks(int x){
			this.chunk = x;
			return this;
		}
		
		public Builder document(LineProfile profile){
			this.profile = profile;
			return this;
		}
		
		public LightWeightIndexer build(){
			return new LightWeightIndexer(profile, commit, chunk);
		}
		
	}
	
	public static class Offset {
		private int offset = 0;
		private int lines = 0;

		public void inc(long bytes){
			offset += bytes;
			lines++;
		}
		public int get(){
			return offset;
		}
		public int count(){
			return lines;
		}
		public void reset(){
			lines = 0;
		}
	}
	
	private LightWeightIndexer(){
		
	}
	
	private LightWeightIndexer(LineProfile profile, int commitSize, int chunk) {
		super();
		this.profile = profile;
		this.commitSize = commitSize;
		this.chunks = chunk;
		this.buffer = new HashMap<>();
	}
	
	public static Builder builder(){
		return new LightWeightIndexer().new Builder();
	}
	

	@Override
	public void master(Path dataDir, Path index, EntityType type) throws Exception {
		
		Path master_file = Paths.get(index.toString(), "master.idx");
		Path dataFile = Paths.get(dataDir.toString(), type.i.file);
				 
		//-- MASTER
		String ID = type.i.id;
		try (
				PrintWriter writer = new PrintWriter(master_file.toFile());
				Stream<String> input = Files.lines(dataFile);
		) {
			Offset offset = new Offset();
			input.forEach(str -> {
				
				//-- GET DOCUMENT
				Document.Builder builder = new Document().new Builder();
				builder.init(str,offset.get());
				Document doc = profile.build(builder);
				
				//-- WRITE INDEX
				mtu_write(doc, writer, ID);
				offset.inc((str+"\n").getBytes().length);
			});
		}
		catch(Exception e){
			System.out.println("HERE-EX");
			throw new Exception("Invalid "+type.name+" file");
		}
	}

	private void mtu_write(Document doc, PrintWriter pr, String ID){
		String entry = String.format("%s>%s:%d-%d|", doc.id, ID, doc.offset, doc.span);
		pr.println(entry);
	}
	
	@Override
	public void slave(Path dataDir, Path index, EntityType type) {
		Worker worker = new Worker();
		String ID = type.i.id;
		Path dataFile = Paths.get(dataDir.toString(), type.i.file);
		
		try (
				Stream<String> input = Files.lines(dataFile);	
		) {
			
			Offset offset = new Offset();
			input.forEach(str -> {
				Document.Builder builder = new Document().new Builder();
				builder.init(str,offset.get());
				Document doc = profile.build(builder);
				addToBuffer(doc.id, doc.load());
				offset.inc((str+"\n").getBytes().length);
				if(offset.count() >= this.commitSize && worker.available()) {
					worker.commit(index, ID, flush());
					offset.reset();
				}
				
			});
			if(this.buffer.keySet().size() > 0)
				worker.commit(index, ID, flush());
			
			worker.shutdown();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	private void addToBuffer(String id,Document.Load doc){
		if(buffer.containsKey(id)){
			buffer.get(id).add(doc);
		}
		else {
			List<Document.Load> list = new ArrayList<Document.Load>();
			list.add(doc);
			buffer.put(id, list);
		}
	}
	
	private HashMap<String, List<Document.Load>> flush(){
		HashMap<String, List<Document.Load>> packge = this.buffer;
		this.buffer = new HashMap<String, List<Document.Load>>();
		return packge;
	}
	
	private static String payload(HashMap<String, List<Document.Load>> page, String id){
		if(page.containsKey(id)){
			String p = "";
			for(Document.Load d : page.get(id)){
				p += ":" + d.offset + "-" + d.span;
			}
			return p;
		}
		return "";
	}
	
	private static void commit(Path index, String entity, final HashMap<String, List<Document.Load>> page){
		String commit_id = UUID.randomUUID().toString();
		try (
				PrintWriter writer = new PrintWriter(Paths.get(index.toString(), commit_id).toFile());
				Stream<String> input = Files.lines(Paths.get(index.toString(), "master.idx"));
		) {
			
			input.forEach(str -> {
				String id = str.split(">")[0];
				String p = payload(page,id);
				if(!p.isEmpty()){
					String n = String.format("%s%s%s|", str, entity, p);
					writer.println(n);
				}
				else {
					writer.println(str);
				}
			});
			
			writer.flush();
			writer.close();
			input.close();
			
			Paths.get(index.toString(), "master.idx").toFile().delete();
			Paths.get(index.toString(), commit_id).toFile().renameTo(Paths.get(index.toString(), "master.idx").toFile());
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
