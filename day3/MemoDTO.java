package jdbc.day3;

/*
    **** ==== DTO(Data Transfer Object) ==== ****
    ==> 데이터 전송 객체(데이터베이스의 테이블에 정보를 담는 객체)  
 */

public class MemoDTO {

	private int rno;
	private int no;
	private String name;
	private String msg;
	private String writeday;
	
	public MemoDTO() { }
	
	public MemoDTO(int no, String name, String msg) {
		this.no = no;
		this.name = name;
		this.msg = msg;
	}
	
	public MemoDTO(int rno, int no, String name, String msg) {
		this.rno = rno;
		this.no = no;
		this.name = name;
		this.msg = msg;
	}
	
	public MemoDTO(int no, String name, String msg, String writeday) {
		this.no = no;
		this.name = name;
		this.msg = msg;
		this.writeday = writeday;
	}
	
	public MemoDTO(int rno, int no, String name, String msg, String writeday) {
		this.rno = rno;
		this.no = no;
		this.name = name;
		this.msg = msg;
		this.writeday = writeday;
	}
	
	public int getRno() {
		return rno;
	}
	
	public void setRno(int rno) {
		this.rno = rno;
	}
	
	public String getWriteday() {
		return writeday;
	}
	
	public void setWriteday(String writeday) {
		this.writeday = writeday;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public int getNo() {
		return no;
	}
	
	public void setNo(int no) {
		this.no = no;
	}
	
}
