package jdbc.day3;

import java.sql.*;
import java.util.*;

import jdbc.util.MyConnectionType;

public class MemoDAO {

// **** ==== DAO(Database Access Object) ==== ****
//      ==> 데이터베이스에 접근하는 객체 
	
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
	
	
	// ==== 전체선택 ==== //
	public List<MemoDTO> selectAllMemo() throws SQLException {
		
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql = "SELECT rno, " 
				+"  no, " 
				+"  name, " 
				+"  msg " 
				+"FROM " 
				+"  (SELECT rownum AS RNO, " 
				+"    V.no, " 
				+"    V.name, " 
				+"    V.msg " 
				+"  FROM " 
				+"    ( SELECT no, name, msg FROM tbl_memo ORDER BY no ASC " 
				+"    ) V " 
				+"  ) T " 
				+"ORDER BY rno DESC";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		
		// >>> 5. SQL구문 실행하기
		rs = pstmt.executeQuery();
		
		List<MemoDTO> memoList = null;
		
		int cnt = 0;
		while(rs.next()) {
			cnt++;

			if(cnt == 1) {
				memoList = new ArrayList<MemoDTO>();
			}
			
			MemoDTO dto = new MemoDTO();
			dto.setRno(rs.getInt("rno"));
			dto.setNo(rs.getInt("no"));
			dto.setName(rs.getString("name"));
			dto.setMsg(rs.getString("msg"));
			
			memoList.add(dto);
		}
		
		return memoList;
		
	}// end of selectAllMemo()-------------------------
	
	
	// ==== 글검색어 조회 ==== //
	public List<MemoDTO> searchMemoByWord(String searchword) throws SQLException {
		
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql = "SELECT rno, no, name, msg "
				+ " FROM view_memo "
				+ " WHERE msg LIKE ? ";
				
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, "%"+searchword+"%");
		
		// >>> 5. SQL구문 실행하기
		rs = pstmt.executeQuery();
		
		List<MemoDTO> memoList = null;
		
		int cnt = 0;
		while(rs.next()) {
			cnt++;

			if(cnt == 1) {
				memoList = new ArrayList<MemoDTO>();
			}
			
			MemoDTO dto = new MemoDTO(rs.getInt("rno"), rs.getInt("no"), rs.getString("name"), rs.getString("msg")); 
			memoList.add(dto);
		}
		
		return memoList;		
		
	}// end of searchMemoByWord(String searchword)------------
	
	
	// ====== 글번호 조회 ======= //
	public MemoDTO searchMemoByNo(String no) throws SQLException {
		
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql = "SELECT rno, no, name, msg "
				+ " FROM view_memo "
				+ " WHERE rno = ? ";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, no);
		
		// >>> 5. SQL구문 실행하기
		rs = pstmt.executeQuery();
		
		MemoDTO mdto = null;
		
	    if (rs.next()) {
	    	mdto = new MemoDTO(rs.getInt("rno"), rs.getInt("no"), rs.getString("name"), rs.getString("msg")); 
	    }
		
	    return mdto;
		
	}// end of searchMemoByNo(String no)-------------------
	
	
	// ===== 글쓰기 ====== //
	public int insertMemo(String name, String msg) throws SQLException { 
		
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql = " insert into tbl_memo(no, name, msg) "
				   + " values(seq_memo.nextval, ?, ?) ";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, name);
		pstmt.setString(2, msg);
		
		// >>> 5. SQL구문 실행하기 <<<
		int n = pstmt.executeUpdate();
		
		return n;
		
	}// end of insertMemo(String name, String msg)-----------
	
	
	// ===== 글내용 변경 ===== //
	public int updateMemo(String no, String name, String msg) throws SQLException {
		
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql = " update tbl_memo set name = ? " +
				     "                   , msg = ? " +
				     " where no = (select no " +
				     "             from view_memo " +
				     "             where rno = ?) ";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, name);
		pstmt.setString(2, msg);
		pstmt.setString(3, no);
		
		// >>> 5. SQL구문 실행하기 <<<
		int n = pstmt.executeUpdate();
		
		return n;
		
	}// end of updateMemo(String no, String name, String msg)-------
	
	
	// ===== 최근 몇일 이내 작성된 글을 보여주는 것 ===== //
	public List<MemoDTO> selectByDate(int day) throws SQLException {
		
	  // >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		 String sql = "SELECT rno, no, " 
		  	 	 +"  name, " 
				 +"  msg , " 
				 +"  writeday " 
				 +"FROM view_memo " 
				 +"WHERE to_date(TO_CHAR(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') - to_date(substr(writeday, 1, 10),'yyyy-mm-dd') <= ? " 
				 +"ORDER BY no DESC";
		 
	  // >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
	     pstmt = conn.prepareStatement(sql);
	     pstmt.setInt(1, day);
	     
	  // >>> 5. SQL구문 실행하기 <<<
	     rs = pstmt.executeQuery();
	     
	     List<MemoDTO> list = null;
	     
	     int cnt = 0;
	     while(rs.next()) {
	    	 cnt++;
	    	 if(cnt==1) {
	    		 list = new ArrayList<MemoDTO>(); 
	    	 }
	    	 MemoDTO mdto = new MemoDTO(rs.getInt("rno"), rs.getInt("no"), rs.getString("name"), rs.getString("msg"), rs.getString("WRITEDAY"));
	    	 list.add(mdto);
	     }
	     
	     return list;
		
	}// end of selectByDate(int day)------------------------
	
	
	// ====== 날짜 구간 검색 ======= //
	public List<MemoDTO> selectByDate(String startday, String endday) throws SQLException {
		
		// >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql = "SELECT rno, no, " 
				+"  name, " 
				+"  msg , " 
				+"  writeday " 
				+"FROM view_memo " 
				+"WHERE writeday BETWEEN ? AND ? " 
				+"ORDER BY no DESC";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
	     pstmt = conn.prepareStatement(sql);
	     pstmt.setString(1, startday);
	     pstmt.setString(2, endday);
	     
	    // >>> 5. SQL구문 실행하기 <<<
	    rs = pstmt.executeQuery();
	    
	    List<MemoDTO> list = null;
	    
	    int cnt = 0;
	    while(rs.next()) {
	    	cnt++;
	    	
	    	if(cnt==1) {
	    		list = new ArrayList<MemoDTO>();
	    	}
	    	
	    	MemoDTO mdto = new MemoDTO(rs.getInt("rno"), rs.getInt("no"), rs.getString("name"), rs.getString("msg"), rs.getString("WRITEDAY"));
	    	list.add(mdto);
	    }
		
	    return list;
	    
	}// end of selectByDate(String startday, String endday)---------------
	
	
	// ======= 글삭제하기 ======= //
	public int deleteMemo(String no) throws SQLException {
		
	    // >>> 3. SQL문(쿼리문, 편지) 작성하기 <<<
		String sql = " delete from tbl_memo " +
				     " where no = (select no " +
				     "             from view_memo " +
				     "             where rno = ?) ";
		
		// >>> 4. PreparedStatement 객체(우편배달부)를 생성 <<<
	     pstmt = conn.prepareStatement(sql);
	     pstmt.setString(1, no);
	     
	    // >>> 5. SQL구문 실행하기 <<<
	     int n = pstmt.executeUpdate();
		
		 return n;
		 
	}// end of deleteMemo(String no)------------------
	
	
	
	
	
}
