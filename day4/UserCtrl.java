package jdbc.day4;

import java.sql.SQLException;
import java.util.Scanner;

public class UserCtrl {
	
	// ==== 회원가입 ==== //
	public int memberRegister(BookDAO dao, Scanner sc)
			throws SQLException {
		
		String userid = null;
		String passwd = null;
		String name = null;
		String birthday = null;
		String email = null;
		String tel = null;
		String address = null;
		
		do {
			
			System.out.print("▷ 사용자 ID : ");
			userid = sc.nextLine();  // 중복아이디 검사
			
			if(!userid.trim().isEmpty()) {
			// *** 중복 ID 검사하기 *** //	
				boolean isUseUserid = dao.useridDuplicate(userid);
				// 사용자 ID가 중복된 것이 있다면 false 리턴
				// 사용자 ID가 중복된 것이 없다면 true 리턴
				
				if(isUseUserid) 
					break;
				else
					System.out.println(">>> 이미 사용 중인 ID 입니다. 새로운 ID를 입력해주세요. <<<");
			
			}
			else
				System.out.println(">>> 사용자 ID를 입력하세요. <<<");
		} while (true);
		
		do {
			
			System.out.print("▷ passwd : ");
			passwd = sc.nextLine();
			
			if(!passwd.trim().isEmpty())
				break;
			else
				System.out.println(">>> 사용자 비밀번호를 입력하세요. <<<");
		} while (true);
		
		do {
			
			System.out.print("▷ 성명 : ");
			name = sc.nextLine();
			
			if(!name.trim().isEmpty())
				break;
			else
				System.out.println(">>> 사용자 명을 입력하세요. <<<");
		} while (true);
		
		do {

			System.out.print("▷ 생년원일[yyyy-mm-dd] : ");
			birthday = sc.nextLine();
			
			if(!birthday.trim().isEmpty())
				break;
			else
				System.out.println(">>> 생일을 입력하세요. <<<");
		} while (true);
		
		do {

			System.out.print("▷ email : ");
			email = sc.nextLine();
			
			if(!email.trim().isEmpty())
				break;
			else
				System.out.println(">>> Email을 입력하세요. <<<");
		} while (true);
		
		do {

			System.out.print("▷ 전화번호 : ");
			tel = sc.nextLine();
			
			if(!tel.trim().isEmpty())
				break;
			else
				System.out.println(">>> 전화번호을 입력하세요. <<<");
		} while (true);
		
		do {

			System.out.print("▷ 주소 : ");
			address = sc.nextLine();
			
			if(!address.trim().isEmpty())
				break;
			else
				System.out.println(">>> 주소를 입력하세요. <<<");
		} while (true);
			
		UserDTO user = new UserDTO();
		
		user.setUserid(userid);
		user.setPasswd(passwd);
		user.setName(name);
		user.setBirthday(birthday);
		user.setEmail(email);
		user.setTel(tel);
		user.setAddress(address);
		
		int n = dao.memberRegister(user); // 회원가입
		
		return n;
		
		
		
	}

}
