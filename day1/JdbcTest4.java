package jdbc.day1;

import java.sql.*;
import java.util.Scanner;

import jdbc.util.MyConnectionType;

public class JdbcTest4 {

	static Connection conn = null;
	static Statement stmt = null;
	static ResultSet rs = null;  // 오직 select 에서만 필요
	
	static {
		// >>>> 1. 오라클 드라이버 로딩 <<<<
		try {
			Class.forName(MyConnectionType.getDriver());
		
			// >>>> 2. 어떤 오라클 DB 서버와 연결을 할래? <<<<	
			Scanner sc = new Scanner(System.in);
			do {
				System.out.print("▷ 연결할 오라클 서버 선택( 1. mine / 2. 규하 bro의 것  / 3. 정원 sister's ) => ");
				String no = sc.nextLine();
				
				if ( "1".equals(no)) {
					conn = DriverManager.getConnection(MyConnectionType.getMyUrl(), MyConnectionType.getUser(), MyConnectionType.getPasswd());
					
					break;
				}
				else if ( "2".equals(no)) {
					conn = DriverManager.getConnection(MyConnectionType.getGyuUrl(), MyConnectionType.getUser(), MyConnectionType.getPasswd());
	
					break;
				}
				else if ( "3".equals(no)) {
					conn = DriverManager.getConnection(MyConnectionType.getJungUrl(), MyConnectionType.getUser(), MyConnectionType.getPasswd());
	
					break;
				}
				else {
					System.out.println("==> 1 ~ 3 에서만 선택하세요!!");
					continue;
				}
				
			} while(true);
			
		// >>>> 3. Statement 객체(우편배달부)를 생성 <<<<
			stmt = conn.createStatement();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e){
			e.printStackTrace();
		} 
	}
	
	// --- *** 자원 반납하기 *** ---
	static void close() {
			try {
				if ( rs != null )   rs.close();
				if ( stmt != null ) stmt.close();
				if ( conn != null ) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}// end of close() -------------------------------------------------------
	
	public static void menu() {
		System.out.println("=============== Menu ===============");
		System.out.println(" 1. 전체조회   2. 글 검색어 조회   3. 글 번호 조회  \r\n "
				        +  "4. 글 쓰기     5. 글 내용 변경     6. 글 삭제 \r\n "
				        +  "7. 종료 ");
		System.out.println("====================================");
	} // end of menu()-------------------------------------------------------

	public static void main(String[] args) {
		
		JdbcTest4 obj = new JdbcTest4();
		Scanner sc = new Scanner(System.in);
		
		String menuno = " ";
		do {
			// === 메뉴 보여주기 ===
			System.out.println("");
			menu();
			
			System.out.print("▷ 메뉴 번호 => ");
			menuno = sc.nextLine();
			
			try {
				switch (menuno) {
				case "1":
					obj.selectAllMemo();
					break;
				case "2":
					obj.searchMemoByWord(sc);
					break;
				case "3":
					obj.seartchNo(sc);
					break;
				case "4":
					obj.insertMemo(sc);
					break;
				case "5":
					obj.updateMemo(sc);
					break;
				case "6":
					obj.deleteMemo(sc);
					break;
				case "7":
					sc.close();
					JdbcTest4.close();					
					break;
				default:
					System.out.println(">>> 메뉴번호는 1 ~ 7 까지 입니다.");
					break;
				}
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} while( !("7".equals(menuno)) );
			


	}// end of main()--------------------------------------------------------

	// ===== title() 메소드 =====
	void title() {
		System.out.println("-----------------------");
		System.out.println(" 글번호 \t 글쓴이 \t 글내용");
		System.out.println("-----------------------");		
	}
	
	// === ★★★ 메소드 메뉴 번호 1. 전체 선택 ===
	void selectAllMemo() throws SQLException {
		
		// >>>> 4. SQL문(쿼리문, 편지) 작성하기 <<<<
		String sql = " select no, name, msg "
				   + " from tbl_memo "
				   + " order by no desc ";
		
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
		
		rs = stmt.executeQuery(sql); // only for select => executeQuery.
			
		int cnt = 0;
		while(rs.next()) { // rs.next()는  다음 행이 있으면 true, 없으면 false를 리턴한다.
			cnt++;
			
			if ( cnt == 1 ) {
				title();
			}
			
			int no = rs.getInt("no");
			String name = rs.getString("name");
			String msg = rs.getString("msg");
			
			System.out.println(no + "\t" + name + "\t" + msg);
			
		} // end of while()------------------------------------------------------------
		
		if(cnt == 0) {
			System.out.println(">>> 데이터가 존재하지 않습니다. <<<");
		}
		
		if ( rs != null ) rs = null;
		
	} // end of void selectAllMemo() ---------------------------------------------------------
	
	// === ★★★ 메소드 메뉴 번호 2. 글 검색어 조회 ===
	void searchMemoByWord(Scanner sc) throws SQLException {

		System.out.print("▷ 글 검색어 입력 => ");
		String searchWord = sc.nextLine();
		
		// >>>> 4. SQL문(쿼리문, 편지) 작성하기 <<<<
		String sql = " select no, name, msg "
				   + " from tbl_memo "
				   + " where msg like '%' || '" + searchWord + "' || '%' "
				   + " order by no desc ";
		
		// >>>> 5. Statement 객체(우편배달부)인 stmt 를 사용하기
		rs = stmt.executeQuery(sql); // only for select => executeQuery.
		
		int cnt = 0;
		while(rs.next()) { // rs.next()는  다음 행이 있으면 true, 없으면 false를 리턴한다.
			cnt++;
			
			if ( cnt == 1 ) {
				title();
			}
			
			int no = rs.getInt("no");
			String name = rs.getString("name");
			String msg = rs.getString("msg");
			
			System.out.println(no + "\t" + name + "\t" + msg);
			
		} // end of while()------------------------------------------------------------
		
		if(cnt == 0) {
			System.out.println(">>> 데이터가 존재하지 않습니다. <<<");
		}
		
		if ( rs != null ) rs = null;
		
	}// end of searchMemoByWord()-------------------------------------------------------------

	// === ★★★ 메소드 메뉴 번호 3. 글 번호 조회 ===
	void seartchNo(Scanner sc) throws SQLException {
		
		System.out.print("▷ 글 번호 입력 => ");
		String searchNo = sc.nextLine();
		
		// >>>> 4. SQL문(쿼리문, 편지) 작성하기 <<<<
		String sql = " select no, name, msg "
				   + " from tbl_memo "
				   + " where no = " + searchNo;
		
		// >>>> 5. Statement 객체(우편배달부)인 stmt 를 사용하기
		rs = stmt.executeQuery(sql); // only for select => executeQuery.
				
		boolean isExists = rs.next();  // rs.next()는  다음 행이 있으면 true, 없으면 false를 리턴한다.
												 // 첫 줄은 NO NAME MSG 써 있는 곳이다.
				
		if ( isExists ) {
			title();
			int no = rs.getInt("no");
			String name = rs.getString("name");
			String msg = rs.getString("msg");
			
			System.out.println(no + "\t" + name + "\t" + msg);
		}
		else {
			System.out.println(">>> 데이터가 존재하지 않습니다. <<<");

		}
				
		if ( rs != null ) rs = null;
		
	}// end of seartchNo()--------------------------------------------------------------
	
	// === ★★★ 메소드 메뉴 번호 4. 글 쓰기 ===
    void insertMemo(Scanner sc) {
		
    	do {
	
	    	System.out.print("▷ 글쓴이 => ");
	    	String name = sc.nextLine();
	    	System.out.print("▷ 메모할 내용을 입력하세요 => ");
	    	String content = sc.nextLine();
	    	
	    	String sql = " insert into tbl_memo(no, name, msg) "
	    			   + " values(seq_memo.nextval, '" + name + "', '" + content + "') ";
	    	
	    	int cnt;
		    	
		    try {
		    	System.out.println(sql);
		    	// Statement 를 사용하여 SQL 문을 작성하는 것은
		    	// sql 쿼리문이 그대로 보여주기 때문에 보안에 취약하다!!!
		    	
				cnt = stmt.executeUpdate(sql);
				
		    	if ( cnt == 1 ) {
		    		System.out.println(">>> 글쓰기 성공!! <<<");
		    		break;
		    	}
		    	else if ( cnt == 0 ) {
		    		System.out.println(">>> 글쓰기 실패!! <<<");
		    	}
		    } catch (SQLIntegrityConstraintViolationException e) {
		    		// 오라클의 제약조건에 위배가 되었을 경우 발생하는 Exception 임.
	    		System.out.println("▶ 에러코드 : " + e.getErrorCode() + "\r\n" + 
	    				           "▶ 오류메시지 : " + e.getMessage());
	    		if (e.getErrorCode() == 1400) {
	    			System.out.println("▷ 글쓰기 실패!! 작성자명과 작성글은 필수 입력사항입니다.");
	    		}
	
	    	} catch (SQLException e) {
				e.printStackTrace();
			}
		} while (true);
		
	}

	// === ★★★ 메소드 메뉴 번호 5. 글 내용 변경 ===
	void updateMemo(Scanner sc) throws SQLException {
		
		System.out.print("▷ 변경하실 글의 번호 => ");
		String strno = sc.nextLine();
		
		// 변경하고자 하는 글번호에 해당하는 행의 존재유무를 파악    [ sql 문의 count(*)를 이용 ]		
		String sql = " select count(*) AS CNT "  // 그룹함수이므로 while 필요 없음 (그냥 입력받은 숫자의 행의 유무만 출력)
				   + " from tbl_memo "
				   + " where no = " + strno;
		// >>>> 5. Statement 객체(우편배달부)인 stmt 를 사용하기
		JdbcTest4.rs = stmt.executeQuery(sql);
		
		JdbcTest4.rs.next();
		
		int cnt = rs.getInt(1);     // only 0 OR 1
		       // JdbcTest4.rs.getInt("CNT");
		
		if ( cnt == 0 ) {
			System.out.println(">>> 존재하지 않는 글번호이므로 글 내용 변경 불가합니다!! <<<");
		}
		else {
			rs = null;
			
			sql = " select no, name, msg "
				+ " from tbl_memo "
				+ " where no = " + strno;
			rs = stmt.executeQuery(sql);

			JdbcTest4.rs.next();
			
			System.out.println("--------------------------------------------");
			System.out.println("▶ 글 번호 : " + rs.getInt("no"));
			System.out.println("▶ 작성자 : " + rs.getString("name"));
			System.out.println("▶ 글 내용 : " + rs.getString("msg"));
			System.out.println("--------------------------------------------");
			
			System.out.println("-------->> 변경시작 <<--------");
			System.out.print("▷ 작성자 : ");
			String name = sc.nextLine();
			System.out.print("▷ 글내용 : ");
			String msg = sc.nextLine();
			
			// >>>> 4. SQL문(쿼리문, 편지) 작성하기 <<<<
			sql = " update tbl_memo set name = '" + name + "', msg = '" + msg + "' " 
				+ " where no = " + strno;
				
			// >>>> 5. stmt 를 사용하여 SQL(쿼리문, 편지) 실행하기 <<<<
			int n = stmt.executeUpdate(sql);
			
			if ( n == 1 ) {
				System.out.println(">>> 글 내용 변경 성공!!! <<<");
			}
			else {
				System.out.println(">>> 글 내용 변경 실패!!! <<<");
			}
		}
		
		
	}// end of updateMemo()-------------------------------------------------------

	// === ★★★ 메소드 메뉴 번호 6. 글 삭제 ===
	void deleteMemo(Scanner sc) throws SQLException {
		
		System.out.print("▷ 삭제하실 글의 번호 => ");
		String strno = sc.nextLine();
		
		// 변경하고자 하는 글번호에 해당하는 행의 존재유무를 파악    [ sql 문의 count(*)를 이용 ]		
		String sql = " select count(*) AS CNT "  // 그룹함수이므로 while 필요 없음 (그냥 입력받은 숫자의 행의 유무만 출력)
				   + " from tbl_memo "
				   + " where no = " + strno;
		// >>>> 5. Statement 객체(우편배달부)인 stmt 를 사용하기
		rs = stmt.executeQuery(sql);
		
		rs.next();
		
		int cnt = rs.getInt(1);     // only 0 OR 1
		       // JdbcTest4.rs.getInt("CNT");
		
		if ( cnt == 0 ) {
			System.out.println(">>> 존재하지 않는 글번호이므로 글 삭제가 불가합니다!! <<<");
		}
		else {
			rs = null;
			
			sql = " select no, name, msg "
				+ " from tbl_memo "
				+ " where no = " + strno;
			rs = stmt.executeQuery(sql);

			rs.next();
			
			System.out.println("--------------------------------------------");
			System.out.println("▶ 글 번호 : " + rs.getInt("no"));
			System.out.println("▶ 작성자 : " + rs.getString("name"));
			System.out.println("▶ 글 내용 : " + rs.getString("msg"));
			System.out.println("--------------------------------------------");
				
			// >>>> 4. SQL문(쿼리문, 편지) 작성하기 <<<<
			sql = " delete from tbl_memo " 
				+ " where no = " + strno;
				
			// >>>> 5. stmt 를 사용하여 SQL(쿼리문, 편지) 실행하기 <<<<
			int n = stmt.executeUpdate(sql);
				
			if ( n == 1 ) {
				System.out.println(">>> 글 삭제 성공!!! <<<");
			}
			else {
				System.out.println(">>> 글 삭제 실패!!! <<<");
			}
		}
	
	}// end of deleteMemo()-------------------------------------------------


} // end of class JdbcTest4 ////////////////////////////////////////////////////////////////////// 




























