package javautils.ui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.hp.gagawa.java.elements.Script;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Tbody;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Thead;
import com.hp.gagawa.java.elements.Tr;
import javautils.ReflectWrapper;

public class Grid<T> {
	private static ReflectWrapper rw = new ReflectWrapper();
	private static final String GETTER_PREFIX = "get";
	private Table table;
	private List<T> beans;
	private String [] columnNames;
	private String [] getters;
	private Hashtable<String, CellDefinition<T>> additionalColumns;
	
	public Grid(String id){
		this(id,null);
	}
	
	public Grid(String id, String title){
		this.table = new Table();
		if(StringUtils.isNotBlank(id)) this.table.setId(id);
		if(StringUtils.isNotBlank(title)) this.table.setTitle(title);
	}
	
	public void setStyle(String style){
		this.table.setStyle(style);
	}
	
	public void setBgColor(String color){
		this.table.setBgcolor(color);
	}
	
	public void setAlign(String align){
		this.table.setAlign(align);
	}
	
	public void setCssClass(String cssRule){
		this.table.setCSSClass(cssRule);
	}
	
	public void setCellPadding(String padding){
		this.table.setCellpadding(padding);
	}
	
	public void setCellSpacing(String spacing){
		this.table.setCellspacing(spacing);
	}
	
	public void bindSource(List<T> beans, String [] getters){
		bindSource(beans, null, getters);
	}
	
	public void bindSource(List<T> beans, String [] columnNames, String [] getters){
		this.beans = beans;
		this.columnNames = columnNames;
		this.getters = getters;
	}
	
	public void setAdditionalColDef(String name, CellDefinition<T> definition){
		if(null == this.additionalColumns) this.additionalColumns = new Hashtable<String, CellDefinition<T>>();
		this.additionalColumns.put(name, definition);
	}
	
	private void buildTable(){
		Thead thead = new Thead();
		Tr header = new Tr();
		if(this.columnNames == null) this.columnNames = getters;
		for(String column : this.columnNames){
			Th th = new Th();
			th.appendText(column);
			header.appendChild(th);
		}
		if(null != this.additionalColumns){
			Enumeration<String> keys = this.additionalColumns.keys();
			while(keys.hasMoreElements()){
				Th th = new Th();
				th.appendText(keys.nextElement());
				header.appendChild(th);				
			}
		}
		thead.appendChild(header);
		Tbody tbody = new Tbody();
		if(null == this.beans){
			Tr row = new Tr();
			tbody.appendChild(row);
		}else{
			for(T obj : this.beans){
				Tr row = new Tr();
				for(String getter : this.getters){
					Td cell = new Td();
					cell.appendText(String.valueOf(rw.invoke(obj, GETTER_PREFIX + getter)));
					row.appendChild(cell);
				}
				if(null != this.additionalColumns){
					List<CellDefinition<T>> values = new ArrayList<CellDefinition<T>>(this.additionalColumns.values());
					for(CellDefinition<T> definition : values){
						Td cell = new Td();
						cell.appendText( (null != definition ? definition.define(obj) : "") );
						row.appendChild(cell);		
					}
				}
				tbody.appendChild(row);
			}
		}
		this.table.appendChild(thead);
		this.table.appendChild(tbody);	
	}
	
	public String toString(){
		buildTable();
		return this.table.write();
	}
}
