package config;

import java.util.HashMap;
import java.util.Map;

import proxies.SimpleServerProxy;
import ClientObjects.ServerProxy;


public class JavaConfig implements Config {

	Map<Class, Class> ifc2implType = new HashMap<Class,Class>() {{
		put(ServerProxy.class, SimpleServerProxy.class);
	}};
	
	public JavaConfig() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Class getImplType(Class ifc) {
		return ifc2implType.get(ifc);
	}

}
