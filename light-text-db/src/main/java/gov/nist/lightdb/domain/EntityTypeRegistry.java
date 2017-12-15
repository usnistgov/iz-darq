package gov.nist.lightdb.domain;

import java.util.HashMap;
import java.util.Map;

public class EntityTypeRegistry {
	
	public static Map<String, TypeInfo> map = new HashMap<>();
	
	public static class TypeInfo<T> {

		public String file;
		public String id;
		public Class<T> clazz;
		
		public TypeInfo(String file, String id, Class<T> clazz) {
			super();
			this.file = file;
			this.id = id;
			this.clazz = clazz;
		}
	}
	
	public static class EntityType {
		public String name;
		public TypeInfo i;
		private EntityType(String name, TypeInfo map) {
			super();
			this.name = name;
			this.i = map;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			EntityType other = (EntityType) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
	}
	
	public static EntityType get(String name){
		if(map.containsKey(name))
			return new EntityType(name,map.get(name));
		else
			return null;
	}
	
	public static EntityType byId(String eId){
		for(String name : map.keySet()){
			if(map.get(name).id.equals(eId))
				return get(name);
		}
		return null;
	}
	
	public static <T> EntityType register(String name, String file, String id, Class<T> clazz){
		map.put(name, new TypeInfo<T>(file, id, clazz));
		return get(name);
	}
	
	public static EntityType register(String name, String file, String id){
		map.put(name, new TypeInfo(file, id, null));
		return get(name);
	}
	
	@FunctionalInterface
	public interface DataMapper {
		public <T extends Record> T map(String str);
	}

}
