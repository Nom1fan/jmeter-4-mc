package infra;

import config.Config;
import config.JavaConfig;

public class ObjectFactory {

	private static ObjectFactory instance;
	private Config config = new JavaConfig();
	
	private ObjectFactory() {		
	}
	
	public static ObjectFactory getInstance() {
		if(instance==null)
			instance = new ObjectFactory();
		return instance;
	}
	
	public <T> T createObject(Class<T> type) throws Exception {
		T resultObj;
		type = resolveImplClass(type);
		resultObj = type.newInstance();
		return resultObj;
	}
	
	 private <T> Class<T> resolveImplClass(Class<T> type) {		 
		 if(type.isInterface()) {
			 type = config.getImplType(type);
		 }
		 return type;		 
	 }

}
