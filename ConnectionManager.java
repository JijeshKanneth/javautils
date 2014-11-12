package javautils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnectionManager {

	  public static Connection getConnection(String dataSourceName) throws SQLException {
	        Context ctx = null;
	        DataSource ds = null;

	        try {
	            ctx = new InitialContext();
	            ds = (DataSource)ctx.lookup("java:comp/env/jdbc/" + dataSourceName);
	            ctx.close();

	        } catch (NamingException ne) {
	        }
	        return ds == null ? null  : ds.getConnection();
	    }
}
