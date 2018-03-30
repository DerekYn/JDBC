package jdbc.day1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class JdbcTest3 {

	public static void title() {
		System.out.println("-----------------------");
		System.out.println(" 글번호 \t 글쓴이 \t 글내용");
		System.out.println("-----------------------");
	}
	
	
	public static void main(String[] args) {
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;     // 오직 select 에서만 필요
	
		try {
			// >>>> 1. 오라클 드라이버 로딩 <<<<
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// >>>> 2. 어떤 오라클 DB 서버와 연결을 할래? <<<<
			String url = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
			
			conn = DriverManager.getConnection(url, "myorauser", "eclass");
			
			// >>>> 3. Statement 객체(우편배달부)를 생성 <<<<
			stmt = conn.createStatement();
			
			// >>>> 4. SQL문(쿼리문, 편지) 작성 <<<<
			String sql = " select * "
					   + " from tbl_memo order by no desc ";
			
			// >>>> 5. Statement 객체(우편배달부)인 stmt 를 사용하여
			//         오라클 서버에 SQL문(편지)를 전송해서 실행시키도록 한다.
			//         현재 SQL문은 select 문이므로 실행시키는 메소드는
			//         executeQuery(sql); 이다.
			//         select 되어진 결과물의 리턴타입은 ResultSet 이다.
			//         ResultSet 인터페이스에 담긴 결과물(select 되어진 결과물)을 처리해주는
			//         ---- next() 메소드 : 커서를 다음으로 옮기며 처리한다.
			//         ---- first() 메소드 : 커서를 가장 처음으로 이동시켜준다.
			//         ---- last() 메소드 : 커서를 가장 마지막으로 이동시켜준다.
			//         ---- 데이터 읽기는 getInt(), getLong(), getFloat(), getDouble(), getString()
			rs = stmt.executeQuery(sql);
			
			int cnt = 0;  // select 되어진 행의 갯수를 누적.
			while(rs.next()) { // rs.next()는  다음 행이 있으면 true, 없으면 false를 리턴한다.
				
				cnt++;
				if ( cnt == 1 ) {      // 타이틀은 단 한번만 출력된다.
					JdbcTest3.title();
				}
				
				//int no = rs.getInt("no");  
				// 컬럼명은 대문자, 소문자 상관 없다. 또 위치값을 사용할수도 있다.
				int no = rs.getInt(1);  // 첫 번째 컬럼.
				String name = rs.getString("name");
				String msg = rs.getString("msg");
				
				System.out.println(no + "\t" + name + "\t" + msg);
				
				/*if(!rs.next()) {
				   System.out.println("==> 추출된 데이터의 갯수 : " + cnt + "개");
				}*/
				
			}// end of while()--------------------------------------------------
			
			System.out.println("==> 추출된 데이터의 갯수 : " + cnt + "개");
			
			
			if ( cnt == 0 ) {
				System.out.println(">>> 데이터가 존재 하지 않습니다. <<<");
			}
			
		/////////////////////////////////////////////////////////////////
			
			if( rs != null) rs.close(); // rs를 싹비우기
			
			Scanner sc = new Scanner(System.in);
			System.out.print("\n\n▷ 메모 내용 검색어 : ");
			String searchWord = sc.nextLine();
			
			sql += " where msg like '%' || '" + searchWord + "' || '%' "
		         + " order by no desc ";
			//where msg like '% " + searchWord + " %';
			
			rs = stmt.executeQuery(sql);
			
			cnt = 0;  // select 되어진 행의 갯수를 누적.
			while(rs.next()) { // rs.next()는  다음 행이 있으면 true, 없으면 false를 리턴한다.
				
				cnt++;
				if ( cnt == 1 ) {      // 타이틀은 단 한번만 출력된다.
					JdbcTest3.title();
				}
				
				//int no = rs.getInt("no");  
				// 컬럼명은 대문자, 소문자 상관 없다. 또 위치값을 사용할수도 있다.
				int no = rs.getInt(1);  // 첫 번째 컬럼.
				String name = rs.getString("name");
				String msg = rs.getString("msg");
				
				System.out.println(no + "\t" + name + "\t" + msg);
				
			}// end of while()--------------------------------------------------
			
			if ( cnt == 0 ) {
				System.out.println(">>> 데이터가 존재 하지 않습니다. <<<");
			}
			System.out.println("==> 검색된 데이터의 갯수 : " + cnt + "개");
			
	
			sc.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// >>>> 6. 오라클 서버와 연결해서 사용하였던 자원을 반납하기 (뒤에서 부터, 생성과 반대 순서로) <<<<
				try {
					if ( rs != null ) rs.close();
					if ( stmt != null ) stmt.close();
					if ( conn != null ) conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
		}
	
	} // end of main() ---------------------------------------------------------------

}
















