package javautils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ReflectWrapper {
	
	public Object newInstance(Class className){
		Object [] nullArray = null;
		return newInstance(className, nullArray);
	}
	
	public Object newInstance(Class className, Object[] params){
		Constructor cons = findConstructor(className, params);
		if(null != cons)
			try {
				return cons.newInstance(params);
			} catch (InstantiationException e) {
				System.out.println("Error occured while instantiating the class : "+e.getMessage());
			} catch (IllegalAccessException e) {
				System.out.println("Error occured while instantiating the class : "+e.getMessage());
			} catch (IllegalArgumentException e) {
				System.out.println("Error occured while instantiating the class : "+e.getMessage());
			} catch (InvocationTargetException e) {
				System.out.println("Error occured while instantiating the class : "+e.getMessage());
			}
		return null;
	}
	
	private Constructor findConstructor(Class className, Object[] params) {
		Constructor [] cons = className.getDeclaredConstructors();
		for(Constructor c : cons){
			Class [] args = c.getParameterTypes();
			int noOfArgs = args.length;
			if(noOfArgs == params.length){
				boolean found = true;
				for(int i = 0; i < noOfArgs; i++){
					if(TypeUtils.getWrapper(args[i]) != params[i].getClass()){
						found = false;
					}
				}
				if(found) return c;
			}
		}
		return null;
	}
	
	public Object invoke(Object obj, String methodName){
		Object [] nullArray = null;
		return invoke(obj, methodName, nullArray);
	}
	
	public Object invoke(Object obj,String methodName, Object [] params){
		Method meth = findMethod(methodName,params,obj.getClass());
		if(null != meth)
			try {
				return meth.invoke(obj, params);
			} catch (IllegalAccessException e) {
				System.out.println("Error occured while invoking the method : "+e.getMessage());
			} catch (IllegalArgumentException e) {
				System.out.println("Error occured while invoking the method : "+e.getMessage());
			} catch (InvocationTargetException e) {
				System.out.println("Error occured while invoking the method : "+e.getMessage());
			}
	
		return null;
	}
	
	private Method findMethod(String method, Object[] params, Class className) {
		Method [] meths = className.getMethods();
		boolean hasParams = (null != params && 0 != params.length);
		for(Method meth : meths){
			if(StringUtils.equals(meth.getName(), method)){
				Class [] args = meth.getParameterTypes();
				int noOfArgs = args.length;
				
				if(!hasParams)
					if(noOfArgs == 0) return meth;
					else continue;
					
				if(noOfArgs == params.length){
					boolean found = true;
					for(int i = 0; i < noOfArgs; i++){
						if(TypeUtils.getWrapper(args[i]) != params[i].getClass()){
							found = false;
						}
					}
					if(found) return meth;
				}
			}
		}
		return null;
	}	
}
