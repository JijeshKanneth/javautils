package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;


public class JDBCTemplate {
    private String dataSource;
    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;
    private boolean applyEscape = false;
    
    public JDBCTemplate(String dataSource){
    	this.dataSource = dataSource;
    }
    
    private void openConnection()throws SQLException{
        this.con = ConnectionManager.getConnection(this.dataSource);
        if(this.con == null) throw new SQLException("Connection not established");        
    }
    
    private void createStatement(String query) throws SQLException,Exception{
        this.pst = this.con.prepareStatement(query);
        if(this.pst == null) throw new Exception("Could not create prepared statement");
    }
    
    private void closeConnection(){
        try { if(this.rs != null) this.rs.close(); } catch(SQLException ex) { System.out.println(" Error occured while closing result set "+ ex);}
        try { if(this.pst != null) this.pst.close(); } catch(SQLException ex) { System.out.println(" Error occured while closing prepared statement "+ ex);}
        try { if(con != null) con.close(); } catch(SQLException ex) { System.out.println(" Error occured while closing connection "+ ex);}        
    }
    
    public boolean update(String query, Object [] params) throws Exception{
        try {
            openConnection();
            createStatement(query);
            populateParams(params);
            return this.pst.executeUpdate() > 0;
        } catch(Exception e) {
            throw e;
        } finally {
            closeConnection();
        }
    }
    
    public int[] batchUpdate(String query, List<Object []> params) throws Exception{
    	int [] result = new int[params.size()];
        try {
            openConnection();
            createStatement(query);
            this.con.setAutoCommit(false);
            for(Object [] param : params){
            	populateParams(param);
            	this.pst.addBatch();
            }
            result = this.pst.executeBatch();
            this.con.commit();
            return result;
        } catch(Exception e) {
            throw e;
        } finally {
            closeConnection();
        }
    }
    
    public int getSeqValue(String sequenceName) throws Exception{
        try {
        	if(StringUtils.isBlank(sequenceName)) return 0;
            openConnection();
            createStatement("select "+sequenceName.trim()+".nextval from dual");
            
            this.rs = this.pst.executeQuery();
            int value = 0;
            while(rs.next()){
            	value = rs.getInt(1);
            }
            return value;
        } catch(Exception e) {
            throw e; 
        } finally {
            closeConnection();
        }
    }     
    
    public Object query(String query, Object [] params,Class<?> beanClass) throws Exception{
        try {
            openConnection();
            createStatement(query);
            populateParams(params);
            
            this.rs = this.pst.executeQuery();
            ResultSetMetaData rsmd = (ResultSetMetaData) this.rs.getMetaData();
            Object bean = null;
            if (rs.next()) {
            	bean = beanClass.newInstance();
                do {
                    for(int i = 1; i <= rsmd.getColumnCount(); i++){
                        BeanUtils.setProperty(bean, rsmd.getColumnName(i).toLowerCase(), rs.getObject(rsmd.getColumnName(i)));
                    }
                } while (rs.next());
            }
            return bean;
        } catch(Exception e) {
            throw e; 
        } finally {
            closeConnection();
        }
    }    

    public List<Object> queryForList(String query, Object [] params,Class<?> beanClass) throws Exception{
        try {
        	
            openConnection();
            createStatement(query);
            populateParams(params);
            this.rs = this.pst.executeQuery();
            ResultSetMetaData rsmd = (ResultSetMetaData) this.rs.getMetaData();
            List<Object> list = new ArrayList<Object>();
            while(rs.next()){
                Object bean = beanClass.newInstance();
                for(int i = 1; i <= rsmd.getColumnCount(); i++){
                    BeanUtils.setProperty(bean, rsmd.getColumnName(i).toLowerCase(), rs.getObject(rsmd.getColumnName(i)));
            	}
                
        	list.add(bean);
    		}
        	return list;
        } catch(Exception e) {
            throw e; 
        } finally {
            closeConnection();
        }
    } 

    public List<Map<String, Object>> queryForMap(String query, Object [] params) throws Exception{
        try {
            openConnection();
            createStatement(query);
            populateParams(params);
            
            this.rs = this.pst.executeQuery();
            ResultSetMetaData rsmd = (ResultSetMetaData) this.rs.getMetaData();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
     
            while(rs.next()){
                Map<String, Object> map = new HashMap<String, Object>();
                for(int i = 1; i <= rsmd.getColumnCount(); i++){
                    map.put(rsmd.getColumnName(i), rs.getObject(rsmd.getColumnName(i)));
                }
                list.add(map);
            }
            return list;
        } catch(Exception e) {
            throw e; 
        } finally {
            closeConnection();
        }
    } 
    
    public void populateParams(Object[] params) throws SQLException{
        if(params != null){
        for(int i =1; i <= params.length; i++){
            Object object = params[i-1];
            if(object instanceof String){
                String temp = (String)object;
                if(StringUtils.isNotBlank(temp)){
                	if(this.applyEscape){
	                    temp = StringEscapeUtils.escapeXml(
	                    StringEscapeUtils.escapeHtml(
	                    StringEscapeUtils.escapeJavaScript(
	                    StringEscapeUtils.escapeCsv(temp))));
                	}
                }else{
                    temp = "NA";
                }
                this.pst.setString(i, temp);
            }else if(object instanceof Integer){
                this.pst.setInt(i, object == null ? 0 : (Integer) object);
            }else if(object instanceof Date){
                this.pst.setDate(i, (java.sql.Date)object);
            }else if(object instanceof java.sql.Date){
                this.pst.setDate(i, (java.sql.Date)object);
            }else if(object instanceof Float){
                this.pst.setFloat(i, (Float)object);
            }else if(object instanceof Double){
                this.pst.setDouble(i, (Double)object);
            }else if(object instanceof Boolean){
                this.pst.setBoolean(i, (Boolean) object);
            }
        }
        }
    }
    public void switchEscape(boolean on){
    	this.applyEscape = on;
    }
}
