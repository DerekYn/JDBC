package jdbc.day1;

import java.sql.*;
import java.util.Scanner;

import jdbc.util.MyConnectionType;
import oracle.jdbc.internal.OracleTypes;

public class JdbcTest6 {

	static Connection conn = null;  // 어느 DB에 연결할지..
	static CallableStatement cstmt = null;
	/* 	오라클의 프로시저를 호출해주는 우편배달부에 해당한다고 보면 된다.	 */
	static ResultSet rs = null;  // 오직 select 에서만 필요

	// static 초기화 블럭 //
	static {
		try {
			// >>> 1. 오라클 드라이버 로딩 하기 <<<
			Class.forName(MyConnectionType.getDriver());
			
			// >>> 2. 어떤 오라클 서버와 연결을 할래? <<<
			Scanner sc = new Scanner(System.in);
			
			do {
				System.out.print("▷ 연결할 오라클 서버 선택( 1. mine / 2. 규하 bro의 것  / 3. 정원 sister's ) => ");
				String no = sc.nextLine();
				
				// 연결해준다 conn = DriverManager.getConnection(URL, ID, PW);
				
				if("1".equals(no)) {
					conn = DriverManager.getConnection(MyConnectionType.getMyUrl(), MyConnectionType.getUser(), MyConnectionType.getPasswd());
					break;
				}
				else if("2".equals(no)) {
					conn = DriverManager.getConnection(MyConnectionType.getGyuUrl(), MyConnectionType.getUser(), MyConnectionType.getPasswd());
					break;
				}
				else if("3".equals(no)) {
					conn = DriverManager.getConnection(MyConnectionType.getJungUrl(), MyConnectionType.getUser(), MyConnectionType.getPasswd());
					break;
				}
				else {
					System.out.println(">>> 1, 2, 3 중에서 선택하세요!!! <<<");
					continue;
				}
				
			} while (true);
			
			// >>> 3. CallableStatement 객체(우편배달부)를 생성 <<<
			// Connection 객체인 conn 을 사용하여 prepareCall() 메소드를 호출하면
			// CallableStatement 객체(우편배달부)가 생성된다.
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} // static 초기화 블록 ------------------------------------------------------------------------
	
	
	// --- *** 자원 반납하기 *** ---
		static void close() {
				try {
					if ( rs != null )   rs.close();
					if ( cstmt != null ) cstmt.close();
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
		
		JdbcTest6 obj = new JdbcTest6();
		Scanner sc = new Scanner(System.in);
		
		String menuno = " ";
		do {
			// === 메뉴 보여주기 ===
			System.out.println("");
			JdbcTest6.menu();
			
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
					//obj.deleteMemo(sc);
					break;
				case "7":
					sc.close();
					JdbcTest6.close();					
					break;
				default:
					System.out.println(">>> 메뉴번호는 1 ~ 7 까지 입니다.");
					break;
				}
			
			} catch (SQLException e) {
				System.out.println(">>> 오류코드번호 : " + e.getErrorCode());
				e.printStackTrace();
			}
		} while( !("7".equals(menuno)) );
			
	} // end of main() -------------------------------------------------------------------
	
	
	// ===== title() 메소드 =====
	void title() {
		System.out.println("-----------------------");
		System.out.println(" 글번호 \t 글쓴이 \t 글내용");
		System.out.println("-----------------------");		
	}
	
	// === ★★★ 메소드 메뉴 번호 1. 전체 선택 ===
	void selectAllMemo() throws SQLException {
		
	// >>> 3. CallableStatement 객체(우편배달부)를 생성 <<<
	// Connection 객체인 conn 을 사용하여 prepareCall() 메소드를 호출하면
	// CallableStatement 객체(우편배달부)가 생성된다.
		
	cstmt = conn.prepareCall(" {call pcd_selectAllMemo(?) } ");
	// 매번 호출해주는 프로시져가 다르니까 계속 변경해줄 변수가 필요하다.
	/*
		 -- Stored Procedure 생성하기 --
 
		 create or replace procedure pcd_selectAllMemo
		 ( o_date OUT SYS_REFCURSOR )
		 is
		 begin
		     open o_date for 
		     select no, name, msg AS MESSAGE
		     from tbl_memo;
		 end pcd_selectAllMemo;
	 */
	// 프로시저의 OUT 로 되어진 파라미터에 저장되어진 값을 자바에서 꺼내오려면
	// cstmt.registerOutParameter() 메소드를 사용한다.
	/*
	 	==> registerOutParameter() 메소드는?
	 	 public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException
	 	 : 프로시저를 실행하여 받아온 값을 JDBC타입(자바에서 인식하는 타입)으로 등록시켜주는 메소드이다.
	 	      오라클의 OUT 모드 변수에 저장되어 있는 값들 (오라클 타입)을 읽어서
	 	   JDBC타입(자바에서 인식하는 타입)으로 변경하는 과정을 거쳐야만 한다.
	 	      대표적인 sqlType을 알아보면
	 	   NULL, FLOAT, INTEGER, VARCHAR, DATE, CLOB, BLOB 등이 있다.
	 */
		cstmt.registerOutParameter(1, OracleTypes.CURSOR);
		
		cstmt.executeUpdate();
		// 프로시저의 실행은 cstmt.executeUpdate();
		// 또는 cstmt.execute(); 이다.
		
		// 지금의 프로시저의 실행된 결과는 첫번째 파라미터(OUT mode)에 저장되어진다.
		// 그러므로 첫번째 파라미터(OUT mode)에 저장되어진 결과물을 꺼내온다.
		rs = (ResultSet)cstmt.getObject(1);
		// 여기서 숫자 1은 프로시저의 파라미터순서를 말한다.
		// 즉, 1번째 파라미터에 저장된 정보를 꺼내오는 리턴타입은 Object 이다.
		// 여기서는 1번째 파라미터는 CURSOR로 되어진 OUT모드이며 select 되어진 결과물이다.
		// 그러므로 Object 타입으로 리턴된 것은 ResultSet 타입으로 casting(강제형변환)시켜야 한다.
		
		int cnt = 0;
		while(rs.next()) {
			
			cnt++;
			 
			if(cnt == 1) {
				title();
			}
			
			int no = rs.getInt("no");
			String name = rs.getString("name");
			String message = rs.getString("MESSAGE");
			
			System.out.println(no + "\t" + name + "\t" + message);
			
		} // end of while()---------------------------------------------------------------
		
	} // end of void selectAllMemo()------------------------------------------------------
	
	// === ★★★ 메소드 메뉴 번호 2. 글 검색어 조회 ===
	void searchMemoByWord(Scanner sc) throws SQLException {
		
		System.out.print("▷ 글 검색어 입력 => ");
		String word = sc.nextLine();
		
		// >>> 3. CallableStatement 객체(우편배달부)를 생성 <<<
		// Connection 객체인 conn 을 사용하여 prepareCall() 메소드를 호출하면
		// CallableStatement 객체(우편배달부)가 생성된다.
			
		cstmt = conn.prepareCall(" {call pcd_searchMemoByWord(?, ?) } ");
		// 매번 호출해주는 프로시져가 다르니까 계속 변경해줄 변수가 필요하다.
		
		/*
		 	 create or replace procedure pcd_searchMemoByWord
			 ( p_word IN varchar2,
			   o_data OUT SYS_REFCURSOR )
			 is
			 begin
			     open o_data for
			     select no, name, msg
			     from tbl_memo
			     where msg like '%' || p_word || '%';
			 end pcd_searchMemoByWord;
		 */
		
		cstmt.setString(1, word);
		// 숫자 1은 첫번째 ? 를 말한다. 오라클의 IN mode 에 들어갈 값 대입하기.
		
		// >>> 4. cstmt.registerOutParameter() 메소드를 사용하여 JDBC 타입 등록하기 <<<
		cstmt.registerOutParameter(2, OracleTypes.CURSOR);
		// 숫자 2는 2두번째 ? 를 말한다.
		
		cstmt.execute();  // 프로시저 실행하기
		// 또는 cstmt.executeUpdate();
		
		rs = (ResultSet)cstmt.getObject(2);
		
		int cnt = 0;
		
		while(rs.next()) {
			
			cnt++;
			
			if(cnt == 1) {
				title();
			}	
			
			int no = rs.getInt("no");
			String name = rs.getString("name");
			String msg = rs.getString("msg");
				
		}// end of while------------------------------------------------------------
		
		if(cnt == 0) {
			System.out.println("검색하신 단어 : \"" + word + "\" 가 없습니다.");
		}
		
	} // end of searchMemoByWord()-------------------------------------------------
	
	// === ★★★ 메소드 메뉴 번호 3. 글 번호 조회 ===
	void seartchNo(Scanner sc) {

		System.out.print("▷ 검색할 글 번호 => ");
		String no = sc.nextLine();
		
		/*
		    create or replace procedure pcd_searchMemoByNo
			 ( p_no IN tbl_memo.no%type,
			   o_no OUT tbl_memo.no%type,
			   o_name OUT tbl_memo.name%type,
			   o_msg OUT tbl_memo.msg%type
			 )
			 is
			 begin
			     select no, name, msg
			          into
			              o_no, o_name, o_msg
			     from tbl_memo
			     where no = p_no;
			 end pcd_searchMemoByNo;
		 */
		try {
			cstmt = conn.prepareCall(" {call pcd_searchMemoByNo(?, ?, ?, ?) } ");
			cstmt.setString(1, no);
			cstmt.registerOutParameter(2, OracleTypes.INTEGER);
			cstmt.registerOutParameter(3, OracleTypes.VARCHAR);
			cstmt.registerOutParameter(4, OracleTypes.VARCHAR);
			
			cstmt.execute();  // 프로시저 실행하기
			// 또는 cstmt.executeUpdate();
			
			int num = cstmt.getInt(2);  
			// 숫자 2는 cstmt = conn.prepareCall(" {call pcd_selectAllMemo(?, ?, ?, ?) } ");
	        // 에서 두번째 ? 의 결과값을 말한다.

			String name = cstmt.getString(3);  
			// 숫자 3는 cstmt = conn.prepareCall(" {call pcd_selectAllMemo(?, ?, ?, ?) } ");
	        // 에서 세번째 ? 의 결과값을 말한다.

			String msg = cstmt.getString(4);  
			// 숫자 4는 cstmt = conn.prepareCall(" {call pcd_selectAllMemo(?, ?, ?, ?) } ");
	        // 에서 네번째 ? 의 결과값을 말한다.
			
			title();
			
			System.out.println(num + "\t" + name + "\t" + msg);

		} catch (SQLException e) {
			if ( e.getErrorCode() == 1403 ) {
				// 오류코드 번호 1403 은 프로시저를 수행한 후 select 결과물이 없으면
				// 발생하는 오류번호이다.
				// 오라클에서는 프로시저를 수행한 후 select 되어진 결과물이 없으면 오류이다.
				System.out.println("검색하신 글 번호 : \"" + no + "\" 은 존재 하지 않습니다.");
			}
		}
		
	}// end of seartchNo()----------------------------------------------------------------

	// === ★★★ 메소드 메뉴 번호 4. 글 쓰기 ===
	void insertMemo(Scanner sc) {
		
		do {
	    	System.out.print("▷ 글쓴이 => ");
	    	String name = sc.nextLine();
	    	
	    	System.out.print("▷ 메모할 내용을 입력하세요 => ");
	    	String msg = sc.nextLine();
	    	
	    	try {
	    		cstmt = conn.prepareCall(" {call pcd_insertMemo(?, ?) } ");
	    		
	    		cstmt.setString(1, name);
	    		cstmt.setString(2, msg);
	    		
	    		int n = cstmt.executeUpdate();
	    		// cstmt.execute();

	    		if ( n == 1 ) {
	    			System.out.println(">>> 글 쓰기 성공!! <<<");
	    			break;
	    		}
	    		
	    	} catch(SQLException e) {
	    		if ( e.getErrorCode() == 1400 ) {
	    			// 오류번호 1400은 NOT NULL 제약 위배시 발생하는 오라클 오류번호이다. 
	    			System.out.println(">>>> 작성자명과 글 내용은 필수 입력사항입니다.");
	    		}
	    		//e.printStackTrace();
	    	}
		} while(true);
		
	} // end of void insertMemo(Scanner sc)--------------------
	
	// === ★★★ 메소드 메뉴 번호 5. 글 내용 변경 ===
	void updateMemo(Scanner sc) {

			System.out.print("▷ 변경하실 글의 번호 => ");
			String strno = sc.nextLine();
														
			try {
				cstmt = conn.prepareCall(" {call pcd_searchMemoByNo(?, ?, ?, ?) } ");
				cstmt.setString(1, strno);
				cstmt.registerOutParameter(2, OracleTypes.INTEGER);
				cstmt.registerOutParameter(3, OracleTypes.VARCHAR);
				cstmt.registerOutParameter(4, OracleTypes.VARCHAR);
				
				cstmt.execute();  // 프로시저 실행하기
				// 또는 cstmt.executeUpdate();
				
				System.out.println("--------------------------------------------");
				System.out.println("▶ 글 번호 : " + cstmt.getInt(2));
				System.out.println("▶ 작성자 : " + cstmt.getString(3));
				System.out.println("▶ 글 내용 : " + cstmt.getString(4));
				System.out.println("--------------------------------------------");
				
				System.out.println("-------->> 변경시작 <<--------");
				
				System.out.print("▷ 작성자 => ");
				String name = sc.nextLine();
				
				System.out.print("▷ 메모 내용 => ");
				String msg = sc.nextLine();
				
				if (name.length() == 0 || msg.length() == 0) {
					System.out.println(">>> 작성자명과 글내용은 필수입력사항입니다. <<<");
					return;  // 메소드 종료!!
				}
				
				cstmt = null;
				
				cstmt = conn.prepareCall(" {call pcd_updateMemo(?, ?, ?) } ");
				
				cstmt.setString(1, strno);
				cstmt.setString(2, name);
				cstmt.setString(3, msg);
				
				int n = cstmt.executeUpdate();
				
				if (n == 1) {
					System.out.println(">>> 글내용 변경 성공!! <<<");
				}

			} catch (SQLException e) {
				if (e.getErrorCode() == 1403) {
					// 오류코드 번호 1403 은 프로시저를 수행한 후 select 결과물이 없으면 
					// 발생하는 오류번호이다. 
					// 오라클에서는 프로시저를 수행한 후 select 되어진 결과물이 없으면 오류이다.!!!
					System.out.println(">>> 변경하고자 하는 글번호 " + strno + "번 글은 존재하지 않습니다.");  
				}
				if (e.getErrorCode() == 1400) { 
					// 오류번호 1400 은 NOT NULL 제약 위배시 발생하는 오라클 오류번호이다. 
					System.out.println(">>>> 작성자명과 글내용은 필수입력사항입니다.");
				}
			}
	} // end of updateMemo() --------------------------------------------------------------
	
	
	
	
}



























