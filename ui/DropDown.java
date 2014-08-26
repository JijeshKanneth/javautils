package javautils.ui;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.hp.gagawa.java.elements.Option;
import com.hp.gagawa.java.elements.Select;
import com.hp.gagawa.java.elements.Text;
import javautils.ReflectWrapper;

public class DropDown{
	private static ReflectWrapper rw = new ReflectWrapper();
	private static final String GETTER_PREFIX = "get";
	private Select select;
	
	public DropDown(String name){
		this(null,name);
	}
	
	public DropDown(String id, String name){
		this.select = new Select();
		if(StringUtils.isNotBlank(id)) this.select.setId(id);
		if(StringUtils.isNotBlank(name)) this.select.setName(name);
	}
	
	public void setCSSclass(String cssRule){
		this.select.setCSSClass(cssRule);
	}
	
	public void setAttribute(String key, String value){
		this.select.setAttribute(key, value);
	}
	
	public void setMultiple(boolean isMultiple){
		this.select.setMultiple(isMultiple ? "true" : "false");
	}
	
	
	public void addOptions(List<?> beans, String valueFeild, String textFeild){
		addOptions(beans, valueFeild, textFeild, null);
	}
	
	public void addOptions(List<?> beans, String valueFeild, String textFeild, String selectedValue){
		int count = 0;
		String [][] options = new String[beans.size()][2];

		for(Object obj : beans){
			String value = String.valueOf(rw.invoke(obj, GETTER_PREFIX + valueFeild));
			String text =  String.valueOf(rw.invoke(obj, GETTER_PREFIX + textFeild));
			
			options[count][0] = value;
			options[count++][1] = text;
		}
		
		addOptions(options, StringUtils.isNotBlank(selectedValue) ? selectedValue : null);	
	}
	
	public void addOptions(String [][] options, String selectedValue){
		if(options != null){
	        for ( String [] option : options ) {
	            Option opt = new Option();
	            String value = option[0];
	            
	            opt.setValue( value );
	            opt.appendChild( new Text(option[1]) );
	            
	            if(StringUtils.isNotBlank(selectedValue) && StringUtils.isNotBlank(value)){
	            	if(StringUtils.equals(selectedValue, value))
	            		opt.setSelected("true");
	            }
	            this.select.appendChild( opt );
	        }
		}
	}
	
	public void addOptions(String [][] options){
		addOptions(options, null);
	}
	
	public String toString(){
		return this.select.write();
	}
}
