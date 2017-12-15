package gov.nist.lightdb.service;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import gov.nist.lightdb.domain.EntityTypeRegistry;
import gov.nist.lightdb.domain.Source;
import gov.nist.lightdb.domain.Time;


public class Main {

	public static void main(String[] args) throws Exception {
//		EntityTypeRegistry.register("patient", "patients.data", "p");
//		EntityTypeRegistry.register("vaccination", "vaccines.data", "v");
//		
//		
//		Time.INSTANCE.init();
//		LightWeightIndexer indexer = LightWeightIndexer.builder()
//		.document((ID) ->{
//			return ID.until('\t');
//		})
//		.commit(1000000)
//		.build();
//		
//		
//		Source master = new Source(EntityTypeRegistry.get("patient"), Paths.get("/Users/hnt5/generated_data/patients.data"));
//		List<Source> slaves = Arrays.asList(new Source(EntityTypeRegistry.get("patient"), Paths.get("/Users/hnt5/generated_data/patients.data")));
//		
//		LightTextDB db = LightTextDB.create(master, slaves, indexer);
		
//		Runnable task = () -> {
//		    try {
//				String id = Thread.currentThread().getName();
//				System.out.println("STARTING : "+id);
//				LightTextDB db = new LightTextDB(Paths.get("/Users/hnt5/light-text-db/"+id));

//
//				db.index(IndexType.MASTER, Paths.get("/Users/hnt5/generated_data/patients.data"), EntityTypeRegistry.get("patient"), indexer);
//				Time.INSTANCE.elapsed("MASTER - "+id);
//				db.index(IndexType.SLAVE, Paths.get("/Users/hnt5/generated_data/vaccines.data"), EntityTypeRegistry.get("vaccination"), indexer);
//				Time.INSTANCE.elapsed("SLAVE - "+id);
//				
//				FileUtils.deleteDirectory(db.mount().toFile());
//				System.gc();
//				Thread.sleep(10000);
//			}
//			catch(Exception e){
//				e.printStackTrace();
//			}
//		};
//		
//		int n = 1;
//		for(int i = 0; i < n; i++){
//			Thread thread = new Thread(task);
//			thread.start();
//		}
		
//		LightTextDB db = new LightTextDB();
//		
//		Time.INSTANCE.init();
//		db.index(IndexType.MASTER, Paths.get("/Users/hnt5/generated_data/patients.data"), EntityTypeRegistry.get("patient"), indexer);
//		Time.INSTANCE.checkPoint("MASTER");
//		db.index(IndexType.SLAVE, Paths.get("/Users/hnt5/generated_data/vaccines.data"), EntityTypeRegistry.get("vaccination"), indexer);
//		Time.INSTANCE.checkPoint("SLAVE");
//		
//		LightTextDB db = new LightTextDB(Paths.get("/Users/hnt5/light-text-db"));
//		
//		LightWeightIndexer indexer = LightWeightIndexer.builder()
//		.document((ID) ->{
//			return ID.until('\t');
//		})
//		.commit(1000000)
//		.build();
//		
//		Time.INSTANCE.init();
//		db.index(IndexType.MASTER, Paths.get("/Users/hnt5/generated_data/patients.data"), EntityTypeRegistry.get("patient"), indexer);
//		Time.INSTANCE.checkPoint("MASTER");
//		db.index(IndexType.SLAVE, Paths.get("/Users/hnt5/generated_data/vaccines.data"), EntityTypeRegistry.get("vaccination"), indexer);
//		Time.INSTANCE.checkPoint("SLAVE");
////		
//		ItemIterator iterator = db.iterator(EntityTypeRegistry.get("patient"), EntityTypeRegistry.get("vaccination"));
//		Time.INSTANCE.init();
//		int i = 1;
//		while(iterator.hasNext()){
//			Map<EntityType, List<String>> result = iterator.next();
//			if(result == null)
//				break;
////			System.out.println(result.get(EntityTypeRegistry.get("patient")).get(0));
//			Time.INSTANCE.checkPoint("HIT "+(i++));
//		}
//		
//		Map<EntityType, List<String>> result = db.get("X24239272", EntityTypeRegistry.get("patient"), EntityTypeRegistry.get("vaccination"));
//		Time.INSTANCE.checkPoint("SEARCH");
//		
//		if(result != null){
//			System.out.println("[HIT]");
//			for(EntityType t : result.keySet()){
//				System.out.println(t.i.file);
//				for(String str : result.get(t)){
//					System.out.println(str);
//				}
//			}
//		}
	}
}



//ItemIterator iterator = db.iterator(EntityTypeRegistry.get("patient"), EntityTypeRegistry.get("vaccination"));
//Time.INSTANCE.init();
//int i = 1;
//while(iterator.hasNext()){
//	Map<EntityType, List<String>> result = iterator.next();
//	if(result == null)
//		break;
////	System.out.println(result.get(EntityTypeRegistry.get("patient")).get(0));
//	Time.INSTANCE.checkPoint("HIT "+(i++));
//}
//Time.INSTANCE.checkPoint("[READ ALL]");
//Map<EntityType, List<String>> result = iterator.next();