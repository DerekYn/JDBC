package jdbc.day1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcTest1 {

	public static void main(String[] args) {
		
		Connection conn = null;
		Statement stmt = null;
		
		try {
			// 자바에서 오라클을 사용하기 위한 방법
			// >>>> 1. 오라클 드라이버 로딩 <<<<
			/*
			 	=== 오라클 드라이버의 역할 ===
			 	1). OracleDriver 를 메모리에 로딩시켜준다.
			 	2). OracleDriver 객체를 생성해준다.
			 	3). OracleDriver 객체를 DriverManager에 등록시켜준다.
			 	    --> DriverManager 는 여러 드라이버들을 Vector에 저장하여 관리해주는 클래스이다.
			 */
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// >>>> 2. 어떤 오라클 DB 서버와 연결을 할래? <<<<
			// 특정 오라클 서버와 연결을 해주는 객체가 Connection 클래스 객체이다.
			// Connection 클래스 객체를 얻어오는 명령어는 아래와 같다.
			String url = "jdbc:oracle:thin:@127.0.0.1:1521:xe"; // @ 뒤는 는 IP address(localhost or 127.0.0.1)
			conn = DriverManager.getConnection(url, "myorauser", "eclass");
			
			
			// >>>> 3. 위에서 생성한(리턴받은) Connection 객체인 conn(자바와 오라클 서버를 연결시켜주는 통로[길]라고 보다)을 
			//         사용하여 SQL문(쿼리문, 편지라고 생각하자)을 전송해주는 객체가 필요하다.
			//         SQL문(편지)을 전송해주는 객체를 우편배달부(택배기사)라고 생각하자.
			//         이러한 우편배달부(택배기사)에 해당하는 클래스 객체가 Statement 객체이다.
			//         Statement 클래스 객체를 얻어오는(생성하는) 명령어는 아래와 같이 하면 된다.
			stmt = conn.createStatement();
			
			// >>>> 4. SQL문(쿼리문, 편지)을 작성한다.
			String sql1 = " create table tbl_memo "  
			           +  " ( " 
					   +  " no     number(4), "
			           +  " name   varchar2(20), "
					   +  " msg    varchar2(100) "
			           +  " ) ";      // SQL 문 내부에서 마지막에 ; 를 찍으면 오류!!!!
			
			String sql2 = " create sequence seq_memo "
					   +  " start with 1 "
					   +  " increment by 1 "
					   +  " nomaxvalue "
					   +  " nominvalue "
					   +  " nocycle "
					   +  " nocache ";      // SQL 문 내부에서 마지막에 ; 를 찍으면 오류!!!!
			
			String sql3 = " insert into tbl_memo(no, name, msg) "
					   +  " values(seq_memo.nextval, '이순신', '안녕하세요? 이순신입니다.') ";
			
			// >>>> 5. 작성된 SQL문(쿼리문, 편지)을 우편배달부에 해당하는
			//         Statement 객체 stmt 을 사용하여 오라클서버로 전송해서 실행시키도록 한다.
			boolean isSQL1 = stmt.execute(sql1);
			boolean isSQL2 = stmt.execute(sql2);
			boolean isSQL3 = stmt.execute(sql3);
			/*
			    stmt.execute(sql문); 을 실행하면 리턴값은 true or false 가 나온다. 
				파라미터로 들어가는 sql문이 select 문이 아닌 
				DDL(create, alter, drop, truncate) 이거나
				DML(insert, update, delete, merge) 이라면
				sql문이 오라클서버에서 성공했다라고 하더라도 리턴값은 false 를 리턴시켜준다.
				
				파라미터로 들어가는 sql문이 select 문일때만 true 를 리턴시켜준다.
			 */
			
			System.out.println("isSQL1 : " + isSQL1);
			System.out.println("isSQL2 : " + isSQL2);
			System.out.println("isSQL3 : " + isSQL3);

			
		} catch (ClassNotFoundException e) {
			System.out.println("오라클 드라이버 로딩 실패!! ojdbc6.jar 파일을 확인하세요.");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("오라클 서버와 연결 실패 또는 SQL구문 오류발생!!");
			e.printStackTrace();  // 어디가 잘못됬는지, 오류인지 나타내줌
		} finally {
			// >>>> 6. 오라클 서버와 연결해서 사용하였던 자원을 반납하기 (뒤에서 부터, 생성과 반대 순서로)
				try {
					if( stmt != null ) stmt.close();
					if( conn != null ) conn.close();
				} catch (SQLException e) {
					
				}
			
		}
		
	} // end of main() ------------------------------------------------------

}

































