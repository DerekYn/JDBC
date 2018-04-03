package jdbc.day4;

import java.sql.*;
import java.util.*;

import jdbc.day3.MemoDTO;
import jdbc.util.MyConnectionType;

public class BookDAO {
	// **** ==== DAO(Database Access Object) ==== ****
	//  ==> 데이터베이스에 접근하는 객체 
	
	static Connection conn = null;
	static PreparedStatement pstmt = null;
	static ResultSet rs = null;
	
	// ===== static 초기화 블럭 시작 ===== // 
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
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	} // ===== static 초기화 블럭 끝 ===== // 
	
	
	// *** ==== 자원반납하기 ==== *** //
	static void close() {
		try {
			if(rs != null)		rs.close();
			if(pstmt != null)	pstmt.close();
			if(conn != null)	conn.close();
		} catch (SQLException e) {
				e.printStackTrace();
		}
	}// end of close()----------------------

	// ===== 관리자 로그인 처리 =====
	public int adminLogin(String userid, String passwd) throws SQLException {
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql =  " select count(*) AS CNT "
				   +  " from tbl_admin "
				   +  " where userid = ? and passwd = ? ";  // count(*)을 이요한 그룹함수이므로 행이 한 개나온다.
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, userid);
		pstmt.setString(2, passwd);

		// >>> 5. SQL구문 실행하기 <<<
		rs = pstmt.executeQuery();
		
		rs.next();
		
		int cnt = rs.getInt("CNT");
		
		return cnt;
	}

	// ===== 일반 사용자 로그인 처리 =====
	public int normalLogin(String userid, String passwd) throws SQLException {
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql =  " SELECT " 
				   +  "  CASE " 
				   +  "    WHEN extract(YEAR FROM sysdate) - to_number(SUBSTR(birthday, 1, 4)) < 20 " 
				   +  "    THEN 0 " 
				   +  "    ELSE 1 " 
				   +  "  END AS ADULTCHECK " 
				   +  " FROM tbl_member A " 
				   +  " JOIN tbl_member_detail B " 
				   +  " ON A.userid  = B.fk_userid " 
				   +  " WHERE userid = ? " 
				   +  " AND passwd   = ? ";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, userid);
		pstmt.setString(2, passwd);
		
		// >>> 5. SQL구문 실행하기 <<<
		rs = pstmt.executeQuery();
		
		int adultCheck = -1;
		
		if(rs.next()) {
			adultCheck = rs.getInt("ADULTCHECK");
		}
		
		return adultCheck;		// -1 : 로그인 실패
								//  0 : 미성년자로 로그인
								//  1 : 성인으로 로그인
	}

	public boolean useridDuplicate(String userid) throws SQLException {
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql =  " select count(*) AS CNT "
				   +  " from tbl_member "
				   +  " where userid = ? ";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, userid);
		
		// >>> 5. SQL구문 실행하기 <<<
		rs = pstmt.executeQuery();
		
		rs.next();
		
		int cnt = rs.getInt("CNT");
		
		if(cnt == 1)
			return false;
		else
			return true;
		
	}// end of useridDuplicate(String userid)--------------------------------------
	
	// **** 회원가입 **** //
	public int memberRegister(UserDTO user) throws SQLException {

		// **** Transaction 처리 **** //
		
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql =  " insert into tbl_member(userid, passwd, name) "
				   +  " values(?, ?, ?) ";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, user.getUserid());
		pstmt.setString(2, user.getPasswd());
		pstmt.setString(3, user.getName());
		
		// >>> 5. SQL구문 실행하기 <<<
		// ******** 수동 커밋으로 전환 ******** //
		conn.setAutoCommit(false);
		
		int n = pstmt.executeUpdate();
		
		int m = 0;
		
		if( n == 1 ) {
			// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
			sql =  " insert into tbl_member_detail(fk_userid, birthday, email, tel, address) "
				+  " values(?, ?, ?, ?, ?) ";
			
			// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.getUserid());
			pstmt.setString(2, user.getBirthday());
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getTel());
			pstmt.setString(5, user.getAddress());
			
			// >>> 5. SQL구문 실행하기 <<<
			m = pstmt.executeUpdate();
			
			if( m == 1 ) 
				conn.commit();
			else
				conn.rollback();
		}
			
			// ******** 자동 커밋으로 전환(기본) ******** //
			conn.setAutoCommit(true);

		return m;
		
	}// end of memberregister(UserDTO user)----------------------------------------
	
	
	// ===== 도서정보 조회 (성인은 모든 정보 조회, 미성년자는 성인물 제외 후 조회) ===== //
	public List<HashMap<String, String>> selectBookInfo(String categoryno, int adultCheck) // adultCheck => 1: 성년 / 0: 미성년 
			throws SQLException {
			// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
			String sql = "SELECT A.OLDNEW, " 
					+"  A.BOOKCODE, " 
					+"  A.BOOKNAME, " 
					+"  A.PUBLISHDAY, " 
					+"  B.TOTALCNT, " 
					+"  B.EXISTSCNT, " 
					+"  B.RENTINGCNT " 
					+"FROM " 
					+"  (SELECT " 
					+"    CASE " 
					+"      WHEN months_between(to_date(TO_CHAR(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd'), publishday) < 6 " 
					+"      THEN '[신간]' " 
					+"      ELSE '[구간]' " 
					+"    END AS OLDNEW, " 
					+"    bookcode, " 
					+"    bookname, " 
					+"    TO_CHAR(publishday, 'yyyy-mm-dd') AS publishday " 
					+"  FROM tbl_book " 
					+"  WHERE fk_categoryno = ? ";
			
			if(adultCheck == 0) {
				sql +=   " AND adult          != 1 ";
			}
			
			sql +=   "  ) A " 
					+"JOIN " 
					+"  (SELECT fk_bookcode, " 
					+"    COUNT(*) AS TOTALCNT, " 
					+"    SUM( " 
					+"    CASE rentyn " 
					+"      WHEN 1 " 
					+"      THEN 1 " 
					+"      ELSE 0 " 
					+"    END) AS EXISTSCNT, " 
					+"    SUM( " 
					+"    CASE rentyn " 
					+"      WHEN 0 " 
					+"      THEN 1 " 
					+"      ELSE 0 " 
					+"    END) AS RENTINGCNT " 
					+"  FROM tbl_rentbook " 
					+"  GROUP BY fk_bookcode " 
					+"  ) B " 
					+"ON A.bookcode = B.fk_bookcode " 
					+"ORDER BY 2";
			
			// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, categoryno);
			
			// >>> 5. SQL구문 실행하기
			rs = pstmt.executeQuery();
			
			List<HashMap<String, String>> bookList = null;
			
			int cnt = 0;
			while(rs.next()) {
				cnt++;
				
				if(cnt==1) {
					bookList = new ArrayList<HashMap<String, String>>();
				}
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				map.put("OLDNEW", rs.getString("OLDNEW"));
				map.put("BOOKCODE", rs.getString("BOOKCODE"));
				map.put("BOOKNAME", rs.getString("BOOKNAME"));
				map.put("PUBLISHDAY", rs.getString("PUBLISHDAY"));
				map.put("TOTALCNT", rs.getString("TOTALCNT"));
				map.put("EXISTSCNT", rs.getString("EXISTSCNT"));
				map.put("RENTINGCNT", rs.getString("RENTINGCNT"));
				
				
				bookList.add(map);
			}
					
			return bookList;	
			
		}// end of selectBookInfo(String categoryno)----------------
	
	// **** 나의 도서 대출 정보 *** //
	public List<HashMap<String, String>> selectRentBookList(String result) throws SQLException {
		
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql = "SELECT A.OLDNEW, " 
				+"  B.categoryname, " 
				+"  A.bookname, " 
				+"  c.rentyn " 
				+"FROM " 
				+"  (SELECT " 
				+"    CASE " 
				+"      WHEN months_between(to_date(TO_CHAR(sysdate, 'yyyy-mm-dd'), 'yyyy-mm-dd'), publishday) < 6 " 
				+"      THEN '[신간]' " 
				+"      ELSE '[구간]' " 
				+"    END AS OLDNEW, " 
				+"    bookname, " 
				+"    bookcode, " 
				+"    fk_categoryno " 
				+"  FROM tbl_book " 
				+"  ) A " 
				+"JOIN " 
				+"  ( SELECT categoryno, categoryname FROM tbl_bookcategory " 
				+"  ) B " 
				+"ON A.fk_categoryno = B.categoryno " 
				+"JOIN tbl_rentbook C " 
				+"ON A.bookcode = C.fk_bookcode "
				+"where userid = ? ";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, result);
		
		// >>> 5. SQL구문 실행하기
		rs = pstmt.executeQuery();
		
		List<HashMap<String, String>> bookList = null;
		
		int cnt = 0;
		while(rs.next()) {
			cnt++;
			
			if(cnt==1) {
				bookList = new ArrayList<HashMap<String, String>>();
			}
			
			HashMap<String, String> map = new HashMap<String, String>();
			
			map.put("OLDNEW", rs.getString("OLDNEW"));
			map.put("CATEGORYNAME", rs.getString("CATEGORYNAME"));
			map.put("BOOKNAME", rs.getString("BOOKNAME"));
			
			
			bookList.add(map);
		}
				
		return bookList;
	
	}// end of selectRentBookList()----------------------------------------------

	
	
	// **** 일반 사용자로 로그인 시 화면에 해당 사용자의 성명을 출력하기 위한 용도 **** //
	public String selectUserName(String userid) throws SQLException {
		
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql =  " select A.name, B.email "
				   +  " from tbl_member A join tbl_member_detail B "
				   +  " on A.userid = B.fk_userid "
				   +  " where A.userid = ? ";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, userid);
		
		// >>> 5. SQL구문 실행하기 <<<
		rs = pstmt.executeQuery();
		
		rs.next();
		
		String name = rs.getString("name");

		return name;
		
	}
	
	
	
	// **** 도서 카테고리 목록을 출력 **** // 
	public List<HashMap<String, String>> selectBookcategory() 
		throws SQLException{
	
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql = "SELECT a.categoryno, " 
				   + " a.categoryname, " 
				   + " b.cnt " 
				   + " FROM tbl_bookcategory A " 
				   + " JOIN " 
				   + " ( SELECT fk_categoryno, COUNT(*) AS CNT FROM tbl_book GROUP BY fk_categoryno " 
				   + " ) B " 
				   + " ON A.categoryno = B.fk_categoryno " 
				   + " ORDER BY 1 ";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
				
		// >>> 5. SQL구문 실행하기
		rs = pstmt.executeQuery();
		
		List<HashMap<String, String> > categoryList = null;
		
		int cnt = 0;
		while(rs.next()) {
			cnt++;
			
			if(cnt==1) {
				categoryList = new ArrayList<HashMap<String, String>>();
			}
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("CATEGORYNO", String.valueOf(rs.getInt("CATEGORYNO")));
			map.put("CATEGORYNAME", rs.getString("CATEGORYNAME"));
			map.put("CNT", rs.getString("CNT"));
			
			categoryList.add(map);
		}
		
		return categoryList;
		
	}// end of selectBookcategory()---------------------------

	
	// ==== 신규도서 입력(insert) ==== //
	public int insertBook(List<BookDTO> listBookDTO) throws SQLException {  // listBookDTO의 사이즈만큼 insert 한다.
		
		// 수동커밋으로 전환
		conn.setAutoCommit(false);
		
		int cnt = 0;
		
		for(int i = 0; i < listBookDTO.size(); i++) {
			
			BookDTO book = listBookDTO.get(i);
			
			// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
			String sql = " insert into tbl_book(bookcode, fk_categoryno, bookname, publishday) "
					   + " values( ?, ?, ?, to_date(?, 'yyyy-mm-dd') ) ";
			
			// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, book.getBookcode());
			pstmt.setInt(2, book.getCategoryno());
			pstmt.setString(3, book.getBookname());
			pstmt.setString(4, book.getPublishday());
					
			// >>> 5. SQL구문 실행하기
			int n = pstmt.executeUpdate();
			
			if(n == 1) cnt++;
			
		} // end of for()------------------------------------------------------------------------
		
		int result = 0;
		if(listBookDTO.size() == cnt) {
			conn.commit();
			result = 1;
		}
		else {
			conn.rollback();
			result = 0;
		}
		
		// 자동커밋으로 전환
		conn.setAutoCommit(true);
		
		return result;
		
	}// end of insertBook(List<BookDTO> listBookDTO)----------------------------------------
	
	// ===== 렌트도서 입력 ====== //
	public int insertRentBook(String bookcode, int bookcnt)
		throws SQLException {
		
		// 수동커밋으로 전환
		conn.setAutoCommit(false);
		
		int cnt = 0;
		
		for(int i=0; i<bookcnt; i++) {
			// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
			String sql = "insert into tbl_rentbook(rentbookno, fk_bookcode) "     
					  + " values(seq_rentbook.nextval, ? ) ";
			
			// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bookcode);
			
			// >>> 5. SQL구문 실행하기
			int n = pstmt.executeUpdate();
			
			if(n==1) cnt++;
		
		}// end of for -----------------------
		
		int result = 0;
		
		if(bookcnt == cnt) {
			conn.commit();
			result = 1;
		}	
		else {
			conn.rollback();
			result = 0;
		}
		
		// 자동커밋으로 전환
		conn.setAutoCommit(true);
		
		return result;	
		
	}// end of insertRentBook(String bookcode, int bookcnt)------------------

		
}






















