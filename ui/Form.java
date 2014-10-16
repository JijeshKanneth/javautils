package javautils.ui.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javautils.ReflectWrapper;

@SuppressWarnings("serial")
public class Form extends BodyTagSupport{
	private String action;
	private String name;
	private String id;
	private Object model;
	private StringBuffer formBody;
	private Document doc;

	public void setAction(String action){
		this.action = action;
	}
    
	public void setName(String name){
		this.name = name;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void setModel(Object model){
		this.model = model;
	}
	
	public int doAfterBody() throws JspException{
	        BodyContent bodyContent = super.getBodyContent();
	        String      bodyString  = bodyContent.getString();
	        
	        formBody = new StringBuffer();
	        formBody.append("<form ");
	        if(this.id != null) formBody.append(" id=\"").append(this.id).append("\"");
	        formBody.append(" name=\"").append(this.name).append("\"");
	        formBody.append(" action=\"").append(this.action).append("\"");
	    
		doc = Jsoup.parse(bodyString);
		
		ReflectWrapper rw = new ReflectWrapper();
		
		if(this.model != null){
			bindValue(doc.select("input[type=hidden]"), rw);
			bindValue(doc.select("input[type=password]"), rw);
			bindValue(doc.select("input[type=text]"), rw);
			bindValue(doc.select("textarea"), rw);
			bindValue(doc.select("select"), rw);
			bindValue(doc.select("input[type=radio]"), rw);
			bindValue(doc.select("input[type=checkbox]"), rw);
		}
	        formBody.append(">").append(doc.toString()).append("</form>");
	    
	        return SKIP_BODY;
	}

	private void bindValue(Elements elements, ReflectWrapper rw) {
		List<String> group = new ArrayList<String>();
		for(int i = 0; i < elements.size(); i++){
			Element element = elements.get(i);
			String name =  element.attr("name");
			
			if(name == null) continue;
			if(group.contains(name)) continue;
			
			String type = element.attr("type");
			Object value = rw.invoke(this.model, "get"+StringUtils.capitalize(name));
			
			if(value != null){
				if("textarea".equals(element.tagName())){
					element.text(String.valueOf(value));
				}else if("select".equals(element.tagName())){
					Elements selOption = doc.select("select[name="+name+"] > option[value="+value+"]");
					if(selOption.size() > 0){
						selOption.get(0).attr("selected","true");
					}
				}else if("radio".equals(type)){
					group.add(name);
					value = rw.invoke(this.model, "get"+StringUtils.capitalize(name));
					Elements selected = elements.select("input[value="+value+"]");
					selected.get(0).attr("checked", "true"); 
				}else if("checkbox".equals(type)){
					group.add(name);
					String [] vals = String.valueOf(value).split(",");
					for(String checkedValue : vals){
						Elements selected = elements.select("input[value="+checkedValue+"]");
						selected.get(0).attr("checked", "true"); 
					}
				}else{
					element.val(String.valueOf(value));
				}
			}
		}
	}

	public int doEndTag() throws JspException{
	        try {
                        pageContext.getOut().print(formBody.toString());
                        return EVAL_PAGE;
                }catch (IOException ioe){
                        throw new JspException(ioe.getMessage());
                }
	}
}
