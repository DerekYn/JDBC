package jdbc.util;

public class MyConnectionType {
	
	// 1. 오라클 드라이버
	private final static String driver = "oracle.jdbc.driver.OracleDriver";
	
	// 2. 오라클 서버의 물리적 주소
	private final static String myUrl = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
	private final static String gyuUrl = "jdbc:oracle:thin:@192.168.50.25:1521:xe";
	private final static String jungUrl = "jdbc:oracle:thin:@192.168.50.5:1521:xe";
	
	// 3. 오라클 서버에 연결할 계정명
	private final static String user = "myorauser";
	
	// 4. 오라클 서버에 연결할 계정의 암호
	private final static String passwd = "eclass";
	
	public static String getDriver() {
		return driver;
	}
	
	public static String getMyUrl() {
		return myUrl;
	}
	
	public static String getGyuUrl() {
		return gyuUrl;
	}
	
	public static String getJungUrl() {
		return jungUrl;
	}
	
	public static String getUser() {
		return user;
	}
	
	public static String getPasswd() {
		return passwd;
	}
	
}
