package jdbc.day1;

import java.sql.*;
import java.util.Scanner;

import jdbc.util.MyConnectionType;

public class JdbcTest5 {

	static Connection conn = null;
	// static Statement stmt = null;
	static PreparedStatement pstmt = null;
	static ResultSet rs = null;  // 오직 select 에서만 필요
	
	/*
	   ========== Statement 와 PreparedStatement ==========
	   
	      위 두개의 가장 큰 차이점은 캐시 사용유무 이다.
	      이들은 쿼리문장(sql문)을 분석( [ == 파싱 parsing ] 문법검사, 인덱스 사용유무 )하고
	      컴파일 후 실행된다.
	   Statement 는 매번 쿼리문장(SQL문)을 수행할때 마다 모든 단계를 파싱(parsing)을 하지만
	   PreparedStatement는 처음 한번만 모든 단계를 파싱(parsing)을 수행한 후 캐시에 담아 재사용한다.
	      그러므로 동일한 쿼리문장(SQL문)을 수행시 PreparedStatement 가 DB에 훨씬 적은 부하를 주므로
	      성능이 좋아진다. 그리고 Statement 보다는 PreparedStatement가 보안상 좋으므로
	 */
	
	
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
				if ( pstmt != null ) pstmt.close();
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
		
		JdbcTest5 obj = new JdbcTest5();
		Scanner sc = new Scanner(System.in);
		
		String menuno = " ";
		do {
			// === 메뉴 보여주기 ===
			System.out.println("");
			JdbcTest5.menu();
			
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
					JdbcTest5.close();					
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
		
		// >>>> 3. SQL문(쿼리문, 편지) 작성하기 <<<<
		String sql = " select no, name, msg "
				   + " from tbl_memo "
				   + " order by no desc ";
		
		// >>>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<<
		pstmt = conn.prepareStatement(sql);
		
		// >>>> 5. Statement 객체(우편배달부)인 pstmt 를 사용하여
					//         오라클 서버에 SQL문(편지)를 전송해서 실행시키도록 한다.
					//         현재 SQL문은 select 문이므로 실행시키는 메소드는
					//         executeQuery(); 이다.
					//         select 되어진 결과물의 리턴타입은 ResultSet 이다.
					//         ResultSet 인터페이스에 담긴 결과물(select 되어진 결과물)을 처리해주는
					//         ---- next() 메소드 : 커서를 다음으로 옮기며 처리한다.
					//         ---- first() 메소드 : 커서를 가장 처음으로 이동시켜준다.
					//         ---- last() 메소드 : 커서를 가장 마지막으로 이동시켜준다.
					//         ---- 데이터 읽기는 getInt(), getLong(), getFloat(), getDouble(), getString()
		
		rs = pstmt.executeQuery(); // only for select => executeQuery.
		             // main() 이라면 JdbcTest5.~~ 해줘야 하지만 아니니까.
			
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
		
		// >>>> 3. SQL문(쿼리문, 편지) 작성하기 <<<<
		String sql = " select no, name, msg "
				   + " from tbl_memo "
				   + " where msg like ? "        // prepareStatement는 ?(변수)로 사용한다.
				   + " order by no desc ";
		
		// >>>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, "%" + searchWord + "%");   // 첫번째 ?에는 "%" + searchWord + "%" 값이 들어간다.
		
		// >>>> 5. PreparedStatement 객체(우편배달부)인 pstmt 를 사용하기
		rs = pstmt.executeQuery(); // only for select => executeQuery.
		

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
		
		// >>>> 3. SQL문(쿼리문, 편지) 작성하기 <<<<
		String sql = " select no, name, msg "
				   + " from tbl_memo "
				   + " where no = ?";
		
		// >>>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, searchNo);   // 두번째 ?에는 searchNo 값이 들어간다.
		
		// >>>> 5. PreparedStatement 객체(우편배달부)인 pstmt 를 사용하기
		rs = pstmt.executeQuery(); // only for select => executeQuery.
		
		
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
	    	String msg = sc.nextLine();
	    	
	    	// >>>> 3. SQL문(쿼리문, 편지) 작성하기 <<<<
	    	String sql = " insert into tbl_memo(no, name, msg) "
	    			   + " values(seq_memo.nextval, ?, ?) ";
	    	
	    	int cnt;
	    	
	    	try {
		    	// >>>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<<
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, name);
				pstmt.setString(2, msg);
				
		    	System.out.println(sql);
		    	// PreparedStatement 를 사용하여 SQL 문을 작성하므로
		    	// sql 쿼리문이 그대로 안보여지므로 보안에 강하다.!!
		    	
		 
				// >>>> 5. PreparedStatement 객체(우편배달부)인 pstmt 를 사용하기
				cnt = pstmt.executeUpdate();
				
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
				   + " where no = ? ";
		
		// >>>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, strno);
		
		// >>>> 5. PreparedStatement 객체(우편배달부)인 pstmt 를 사용하기
		rs = pstmt.executeQuery();
		
		rs.next();
		
		int cnt = rs.getInt(1);     // only 0 OR 1
		       // rs.getInt("CNT");
		
		if ( cnt == 0 ) {
			System.out.println(">>> 존재하지 않는 글번호이므로 글 내용 변경 불가합니다!! <<<");
		}
		else {
			rs = null;
			
			sql = " select no, name, msg "
				+ " from tbl_memo "
				+ " where no = ? ";
			
			pstmt = null;         // 위에 있는 sql 문과 다른 sql 문이므로 null로 pstmt를 초기화 해준다.
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, strno);            // 새로운 pstmt의 첫번째 ? 값.

			rs = pstmt.executeQuery();

			rs.next();
			
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
			sql = " update tbl_memo set name = ? "
			    + "                   , msg  = ? "
			    + " where no = ? ";
			
			pstmt = null;				// 기존에 있던 pstmt를 날리고 새로운 sql을 전달하기 위해서 
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, msg);
			pstmt.setString(3, strno);
				
			// >>>> 5. pstmt 를 사용하여 SQL(쿼리문, 편지) 실행하기 <<<<
			int n = pstmt.executeUpdate();
			
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
				   + " where no = ? ";

		// >>>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, strno);
		
		// >>>> 5. pstmt 를 사용하여 SQL(쿼리문, 편지) 실행하기 <<<<
		rs = pstmt.executeQuery();		// select 문 이기에 executeQuery() 이용.
		
		rs.next();
		
		int cnt = rs.getInt(1);     // only 0 OR 1
		       // JdbcTest5.rs.getInt("CNT");
		
		if ( cnt == 0 ) {
			System.out.println(">>> 존재하지 않는 글번호이므로 글 삭제가 불가합니다!! <<<");
		}
		else {
			// >>>> 3. SQL문(쿼리문, 편지) 작성하기 <<<<
			sql = " delete from tbl_memo " 
				+ " where no = ? ";
			
			// >>>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<<
			pstmt = null;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, strno);
				
			// >>>> 5. pstmt 를 사용하여 SQL(쿼리문, 편지) 실행하기 <<<<
			int n = pstmt.executeUpdate();
			
			if ( n == 1 ) {
				System.out.println(">>> 글 삭제 성공!!! <<<");
			}
			else {
				System.out.println(">>> 글 삭제 실패!!! <<<");
			}
		}
	
	}// end of deleteMemo()-------------------------------------------------

} // end of class JdbcTest5 ////////////////////////////////////////////////////////////////////// 




























