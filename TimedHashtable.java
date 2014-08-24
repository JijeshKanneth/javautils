package cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class TimedHashtable {

	private Timer evictionClock;
	private final long timeout;
	private Hashtable<Object, ValueObject> hashTable = null;
	private long age;
	private boolean canExtendLife;

	public TimedHashtable(long timeout) throws IllegalArgumentException {
		if (timeout < 0) {
			throw new IllegalArgumentException();
		}
		this.evictionClock = new Timer();
		this.timeout = timeout * 1000;
		this.hashTable = new Hashtable<Object, ValueObject>();
		this.canExtendLife = true;
		this.evictionClock.schedule(new Evictor(this.hashTable,this.timeout), (long)(this.timeout * 0.25), this.timeout);

	}
	
	public TimedHashtable(long timeout, boolean canExtendLife) throws IllegalArgumentException{
		this(timeout);
		this.age = System.currentTimeMillis();
		this.canExtendLife = canExtendLife;
	}

	public Object put(Object key, Object value) {
		Object object = null;
		if(canExtendLife)
			object = (hashTable.put(key, new ValueObject(value)));
		else
			object = (hashTable.put(key, new ValueObject(value, this.age)));
		return object;
	}

	public Object get(Object key) {
		ValueObject vo = (ValueObject) hashTable.get(key);
		if (vo != null) return vo.getObject();
		return null;
	}

	public Object remove(Object key) {
		ValueObject vo = (ValueObject) hashTable.remove(key);
		if (vo != null)	return vo.getObject();
		return null;
	}
	
	public Object replace(Object key, Object newObject){
		remove(key);
		return put(key, newObject);
	}
	
	public int size() {
		return hashTable.size();
	}

	public boolean isEmpty() {
		return hashTable.isEmpty();
	}

	public boolean containsKey(Object key) {
		return hashTable.contains(key);
	}

	public void clear() {
		hashTable.clear();
	}

	@SuppressWarnings("rawtypes")
	public Set keySet() {
		return hashTable.keySet();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection values() {
		List list = new ArrayList();
		Iterator iter = hashTable.values().iterator();
		while (iter.hasNext()) {
			ValueObject vo = (ValueObject) iter.next();
			list.add(vo.getObject());
			if(!canExtendLife) vo.extendLife();
		}
		
		return list;
	}

	public boolean equals(Object o) {
		return hashTable.equals(o);
	}
}

final class Evictor extends TimerTask {
	private Hashtable<Object, ValueObject> temp;
	private long expireTime;
	private TreeMap<ValueObject, Object> treeMap;
	private Enumeration<Object> enume;
	
	public Evictor(Hashtable<Object, ValueObject> temp, long expireTime ){
		this.temp = temp;
		this.expireTime = expireTime;
	}

	@Override
	public void run() {
	       treeMap = new TreeMap<ValueObject, Object>();
	       enume = temp.keys(); 
	       Object keyTmp;
	        while(enume.hasMoreElements()) {
	            keyTmp = enume.nextElement();
	            treeMap.put(temp.get(keyTmp), keyTmp);
	        }
	        
	        Iterator<Object> iter = treeMap.values().iterator(); 
	        while( iter.hasNext() ) { 
	            keyTmp = iter.next(); 
	            ValueObject voTmp = (ValueObject) temp.get( keyTmp ); 
	            

	            if( ( voTmp != null ) && ( voTmp.getAge() > expireTime ) ) { 
	                temp.remove( keyTmp );
	            } else { break; }
	                
	        } 

	}

}

@SuppressWarnings("rawtypes")
final class ValueObject implements Comparable {

	private int counter;
	private long age;
	private Object object;
	private boolean canExtendLife;

	public ValueObject(Object object) {
		this.object = object;
		updateAge();
		this.canExtendLife = true;
	}
	
	public ValueObject(Object object, long age){
		this.object = object;
		this.age = age;
		this.canExtendLife = false;
	}

	private final void updateAge() {
		age = System.currentTimeMillis();
	}

	public final long getAge() {
		return (System.currentTimeMillis() - age);
	}

	public final int getCounter() {
		return counter;
	}

	public final Object getObject() {
		if(canExtendLife){
			extendLife();
		}
		return (object);
	}

	private final void updateUsage(){
		counter++;
	}
	
	public final void extendLife(){
		updateAge();
		updateUsage();		
	}
	
	public final int compareTo(Object obj) {
        ValueObject vo = (ValueObject) obj;
        
        int compValue = this.getCounter() - vo.getCounter(); // sort counters ascending
        if (compValue == 0) compValue = (int)(vo.getAge() - this.getAge()); // sort by age descending if counters are equals
        if (compValue == 0) compValue = vo.hashCode() - this.hashCode(); // sort by hashCode to make sure none are equal
        
        return compValue;
	}

	public final String toString() {
		return ("\nvalue: " + object.toString() + "\tacessess: " + getCounter() + "\tage: " + getAge());
	}
}
