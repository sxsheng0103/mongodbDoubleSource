package anocation.test.cn;

import anocation.util.cn.JDBCConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

	@JDBCConfig(ip = "127.0.0.1", database = "test", encoding = "UTF-8", loginName = "root", password = "admin")
	public class DBUtil {
	    static {
	        try {
	            Class.forName("com.mysql.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	    }
	 
	    public static Connection getConnection() throws SQLException, NoSuchMethodException, SecurityException {
	        JDBCConfig config = DBUtil.class.getAnnotation(JDBCConfig.class);
	        System.out.println(config);
	 
	        String ip = config.ip();
	        int port = config.port();
	        String database = config.database();
	        String encoding = config.encoding();
	        String loginName = config.loginName();
	        String password = config.password();
	 
	        String url = String.format("jdbc:mysql://%s:%d/%s?characterEncoding=%s", ip, port, database, encoding);
	        return DriverManager.getConnection(url, loginName, password);
	    }
	     
	    public static void main(String[] args) throws NoSuchMethodException, SecurityException, SQLException {
	        Connection c = getConnection();
	        System.out.println(c);
	    }
	}
