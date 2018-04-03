package jdbc.day4;

import java.sql.*;
import java.util.*;

import jdbc.day3.Main;
import jdbc.day3.MemoDTO;

public class BookCtrl {
	
	Scanner sc = new Scanner(System.in);
	
	// **** 일반사용자로 로그인 했을시 화면에 보여줄 내용 **** //
	public void display_user(BookDAO dao, String userid, int adultCheck) throws SQLException {
		
		String menuno = null;
		List<BookDTO> list = null;
		
		String result = dao.selectUserName(userid);
		System.out.println("\n===> '" + result + "' 님 환영합니다 :) <===");
		
		do {
		
			bookmenu();
			
			menuno = sc.nextLine();
			
			switch (menuno) {
				case "1":   //  1. 도서 카테고리 조회
					List<HashMap<String, String>> categoryList = dao.selectBookcategory(); 
					if (categoryList != null) {
						System.out.println("\n-------------------------------");
						System.out.println(" 카테고리번호    카테고리명 \t 종류");
						System.out.println("-------------------------------");
						
						for(HashMap<String, String> map : categoryList) {
							System.out.println(map.get("CATEGORYNO")+"\t"+map.get("CATEGORYNAME")+"\t"+map.get("CNT"));  
						}
					}
					else {
						System.out.println(">>> 도서 카테고리 목록내용이 없습니다. :( ");
					}
					break;
					
				case "2": // 2. 나의 도서 대출 정보
					
					List<HashMap<String, String>> rentBookList = dao.selectRentBookList(result); 
					if (rentBookList != null) {
						System.out.println("\n-------------------------------");
						System.out.println(" 카테고리명 \t 이름 \t 연체일 수 \t 연체료");
						System.out.println("-------------------------------");
	
					for(HashMap<String, String> map : rentBookList) {
						System.out.println(map.get("CATEGORYNAME")+"\t"+map.get("BOOKNAME")+"\t"+map.get("CNT"));  
						}
					}
					else {
						System.out.println(">>> 도서 카테고리 목록내용이 없습니다. :( ");
					}
						
					break;
					
				case "3":  //  3. 도서 카테고리 별 조회
					System.out.print("▶ 조회할 카테고리 번호 => ");
					String categoryno = sc.nextLine();

					
					List<HashMap<String, String>> bookList = dao.selectBookInfo(categoryno, adultCheck); 
					if (bookList != null) {
						System.out.println("\n--------------------------------");
						System.out.println(" 신간구간 \t 도서코드 \t 도서명 \t 출판일자 \t 총권수 \t 비치권수 \t 대여권수");
						System.out.println("---------------------------------");
						
						for(HashMap<String, String> map : bookList) {
							System.out.println(map.get("OLDNEW")+" \t "+map.get("BOOKCODE")+" \t "+map.get("BOOKNAME")
							+" \t "+map.get("PUBLISHDAY")+" \t "+map.get("TOTALCNT")
							+" \t "+map.get("EXISTSCNT")+" \t "+map.get("RENTINGCNT"));         
						}
					}
					else {
						System.out.println(">>> 도서 카테고리번호 "+categoryno+ "에 해당하는 도서는 비치하지 않았습니다.");
					}
					
					break;	
					
				case "4": // 4. 도서 대출

					break;
					
				case "5": // 5. 도서 반납
					
					break;
					
				case "6": // 6. 내 정보 조회
					
					break;
					
				case "7": // 7. 내 정보 수정
					
					break;
					
				default:
					break;
				}
		} while (!"5".equals(menuno));
	}// end of display_user(BookDAO dao, String userid)--------------------------------------------------------
	
	
	// **** 관리자로 로그인 했을시 화면에 보여줄 내용 **** //
	public void display_admin(BookDAO dao) 
		throws SQLException {
		
		String menuno = null;
		
		System.out.println("\n===> 관리자님 환영합니다. :) <===" );
		do {
			adminMenu();
			menuno = sc.nextLine();
			
			switch (menuno) {
				case "1":   //  1. 도서 카테고리 조회
					List<HashMap<String, String>> categoryList = dao.selectBookcategory(); 
					if (categoryList != null) {
						System.out.println("\n-------------------------------");
						System.out.println(" 카테고리번호    카테고리명 \t 종류");
						System.out.println("-------------------------------");
						
						for(HashMap<String, String> map : categoryList) {
							System.out.println(map.get("CATEGORYNO")+"\t"+map.get("CATEGORYNAME")+"\t"+map.get("CNT"));  
						}
					}
					else {
						System.out.println(">>> 도서 카테고리 목록내용이 없습니다. :( ");
					}
					break;
					
				case "2":   //  2. 신규 도서 등록
					List<BookDTO> listBookDTO = new ArrayList<BookDTO>();
					
					String YN = null;
					
					do {
						
						System.out.print("▷ 도서코드 => ");
						String bookcode = sc.nextLine();

						System.out.print("▷ 카테고리 번호 => ");
						String fk_categoryno = sc.nextLine();

						System.out.print("▷ 도서명 => ");
						String bookname = sc.nextLine();

						System.out.print("▷ 출판일[yyyy-mm-dd] => ");
						String publishday = sc.nextLine();
													
						BookDTO book = new BookDTO();
						book.setBookcode(bookcode);
						book.setCategoryno(Integer.parseInt(fk_categoryno));
						book.setBookname(bookname);
						book.setPublishday(publishday);
						
						listBookDTO.add(book);
						
						if("N".equalsIgnoreCase(YN)) break;  // 대소문자 무시하고 문자열 자체를 비교
						
					} while (true);
					
					int n = dao.insertBook(listBookDTO);
					if( n == 1 ) {
						System.out.println(">>> 신규도서 등록 성공 !! :> ");
					}
					else {
						System.out.println(">>> 신규도서 등록 실패 !! :< ");
					}
					break;
					
				case "3":
					System.out.print("▶ 조회할 카테고리 번호 => ");
					String categoryno = sc.nextLine();
					
					List<HashMap<String, String>> bookList = dao.selectBookInfo(categoryno, 1); 
					if (bookList != null) {
						System.out.println("\n--------------------------------");
						System.out.println(" 신간구간 \t 도서명 \t 출판일자");
						System.out.println("---------------------------------");
						
						for(HashMap<String, String> map : bookList) {
							System.out.println(map.get("OLDNEW")+" \t "+map.get("BOOKNAME")+" \t "+map.get("PUBLISHDAY"));         
						}
					}
					else {
						System.out.println(">>> 도서 카테고리번호 "+categoryno+ "에 해당하는 도서는 비치하지 않았습니다.");
					}
					
					break;
					
				case "4":  // 4.렌트도서 입력
					
					String bookcode = null;
					int bookcnt = 0;
					
					do {
						System.out.print("▶ 도서코드 입력 => ");
						bookcode = sc.nextLine();
					
						if(!bookcode.trim().isEmpty()) 
							break;
						else 
							System.out.println(">>> 도서코드를 입력하세요!!");
					} while(true);
					
					do {
						System.out.print("▶ 권수 => ");
						String strbookcnt = sc.nextLine();
						
						if(!strbookcnt.trim().isEmpty()) {
							try {
								bookcnt = Integer.parseInt(strbookcnt);
								if(bookcnt <= 0) {
									System.out.println(">>> 도서권수는 0보다 커야합니다  :) ");
									continue;
								}
							} catch(NumberFormatException e) {
								System.out.println(">>> 도서권수는 숫자로만 입력하세요  :) "); 
								continue;
							}
							
							break;
						}	
						else 
							System.out.println(">>> 권수를 입력하세요  :ㅁ  ");
					} while(true);
					
					try {
						n = dao.insertRentBook(bookcode, bookcnt);
						
						if(n==1) 
							System.out.println(">>> 렌트도서 " + bookcode + " " + bookcnt+"권 입력 성공  :)" );  
						else 
							System.out.println(">>> 렌트도서 " + bookcode + " " + bookcnt+"권 입력 실패  :(" ); 
					} catch (SQLIntegrityConstraintViolationException e) {
						if(e.getErrorCode() == 2291) { // foreign key 에 위배되었을 경우
							System.out.println(">>> 존재하지 않는 도서코드 입니다. 다시 입력하세요  :<  ");
							continue;
						}
					}
					
					break;	
				
				case "5": // 5. 사용자 코인 입력
					 
					
				default:
					break;
				}
		} while (!"5".equals(menuno));
		
		
	}// end of display_admin(BookDAO dao)---------------------------------------------------
	
	
	public void bookmenu() {
		
	   System.out.println("\n==========>> 도서대여 메뉴 <<==========");
	   System.out.println("1. 도서카테고리 조회\t2. 도서대출정보\t3. 도서정보조회");
	   System.out.println("4. 도서대출\t5. 도서반납 \t6. 내 정보 조회");
	   System.out.println("7. 내 정보 수정\t9. 종료");  
	   System.out.println("=====================================");
		
		System.out.print("▷ 메뉴번호 선택 => ");
	}
	
	public void adminMenu()  {
		
	   System.out.println("\n==========>> 관리자 메뉴 <<==========");
	   System.out.println("1. 도서카테고리 조회\t2. 신규도서 입력\t3. 도서정보조회");
	   System.out.println("4. 대출도서 입력\t5. 회원 정보 조회\t6. 도서삭제");
	   System.out.println("7. 코인 충전\t9. 종료");
	   System.out.println("=====================================");

		System.out.print("▷ 메뉴번호 선택 => ");
		
	}
	

}



















