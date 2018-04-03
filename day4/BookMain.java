package jdbc.day4;

import java.util.*;
import java.sql.*;

public class BookMain {
	
	// == 로그인 메뉴 == //
	public static void menu_login() {
		System.out.println("========>> 도서대여 LogIN <<========");
		System.out.println("1. 일반사용자 / 2. 관리자 / 3. 회원가입 / 4. 프로그램 종료");
		System.out.println("=================================");
	}// end of menu_login() ----------------------------------
	
	public static Map<String, Object> login(Scanner sc, String type, BookDAO dao) throws SQLException {
		
		String userid = null;
		String passwd = null;
		Map<String, Object> map = new HashMap<String, Object>();
		
		do {
			
			System.out.print("▷ ID : ");
			userid = sc.nextLine();
			if ( !userid.trim().isEmpty() ) {
				break;
			}
			else {
				System.out.print(">>> ID를 입력하세요.");
			}
			
		} while (true);
		
		do {
			
			System.out.print("▷ passwd : ");
			passwd = sc.nextLine();
			
			if ( !passwd.trim().isEmpty() ) {
				break;
			}
			else {
				System.out.print(">>> passwd를 입력하세요.");
			}
			
		} while (true);
		
		int n = 0;  
		
		if("admin".equals(type)) {
			// 관리자로 로그인
			n = dao.adminLogin(userid, passwd); // 로그인이 성공하면 1, 로그인이 실패하면 0
			
			if(n==1) {
				map.put("n", 1);
				System.out.println(">>> 관리자로 로그인 성공!! <<<");
			}
			else {
				map.put("n", 0);
				System.out.println(">>> 관리자로 로그인 실패!! <<<");
			}
		}
		
		else if("normal".equals(type)) {
			// 일반사용자로 로그인
			n = dao.normalLogin(userid, passwd);  // -1 : 로그인 실패, 
			                                      //  0 : 미성년자로 로그인 성공, 
			                                      //  1 : 성인으로 로그인 성공 
			
			if(n == -1) {
				System.out.println(">>> 로그인 실패!! <<<");
				map.put("n", 0);
			}
			else if(n == 0) { // 미성년자로 로그인 성공
			//	System.out.println(">>> "+msg+" <<<");
				map.put("n", 1);
				map.put("userid", userid);
				map.put("adultCheck", 0);
			}
			else if(n == 1) { // 성인으로 로그인 성공
			//	System.out.println(">>> "+msg+" <<<");
				map.put("n", 1);
				map.put("userid", userid);
				map.put("adultCheck", 1);
			}
			
		}
		
		return map;
		
	}// end of login(Scanner sc, String type, BookDAO dao)----------------------------------

	public static void main(String[] args) throws SQLException {
		
		BookDAO dao = new BookDAO();

		Scanner sc = new Scanner(System.in);
		
		String menuno = null;
		Map<String, Object> map = null;
		int n = 0;    // 로그인이 성공하면 1, 실패하면 0
		
		do {		
			BookMain.menu_login();
			System.out.print("▷  로그인 종류 선택 => ");
			menuno = sc.nextLine();
		
		try {	
			switch (menuno) {
			case "1":
				map = BookMain.login(sc, "normal", dao);
				n = (Integer)(map.get("n"));
				
				break;
	
			case "2":
				map = BookMain.login(sc, "admin", dao);
				n = (Integer)(map.get("n"));
				
				break;
			
			case "3":
				UserCtrl usctrl = new UserCtrl();
				int result = usctrl.memberRegister(dao, sc);
				if( result == 1 ) {
					System.out.println(">>> 회원가입 성공 :) <<<");
				}
				else {
					System.out.println(">>> 회원가입 실패 ;( <<<");
				}
				
				BookMain.menu_login();

				break;
				
			case "4":
				BookDAO.close();  // 자원 다 반납
				System.out.println(">>> 안녕히 가십시오 :) <<<");
				break;
				
			default:
				System.out.println(">>> 메뉴에 있는 번호를 선택해 주세요 :O <<<");
				break;
			}
		} catch(SQLException e) {
			if (e.getErrorCode() == 1722) {
				System.out.println(">>> 숫자로만 입력하세요 :ㅁ <<<");
			}
			e.printStackTrace();
		}
			
		} while ( !"4".equals(menuno) && !(n == 1) );
		 // 프로그램 종료
		
		if(!"4".equals(menuno)) {
			
			BookCtrl bkctrl = new BookCtrl();
			
			if ("1".equals(menuno)) {
				// 일반사용자로 로그인
				try {
					bkctrl.display_user(dao, (String)(map.get("userid")), (int)map.get("adultCheck") );
				 // System.out.println("헤헤헤");
				} catch (SQLException e) {
					if (e.getErrorCode() == 1722) {
						System.out.println(">>> 숫자로만 입력하세요!!");
					}
					e.printStackTrace();
				}
			}
			else if("2".equals(menuno)) {
				// 관리자로 로그인
				try {
					bkctrl.display_admin(dao);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
		

	}// end of main()---------------------------------------------------------

}

























