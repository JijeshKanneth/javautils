package javautils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({ "unchecked","rawtypes"})
public class TypeUtils {
	private static final Map<Class, Class> PRIMITIVES_TO_WRAPPERS = getWrapperMaps();
	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
	
	private TypeUtils(){}
	
	public static <T> Class<T> getWrapper(Class<T> c) {
		return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
	}
	
    public static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }
    
	private static Map<Class, Class> getWrapperMaps(){
    	Map<Class, Class> map = new HashMap<Class, Class>();
    	map.put(boolean.class, Boolean.class);
    	map.put(byte.class, Byte.class);
    	map.put(char.class, Character.class);
    	map.put(double.class, Double.class);
    	map.put(float.class, Float.class);
    	map.put(int.class, Integer.class);
    	map.put(long.class, Long.class);
    	map.put(short.class, Short.class);
    	map.put(void.class, Void.class);	
    	return map;
    }

    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
}
