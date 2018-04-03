package jdbc.day4;

public class BookDTO {

	private int categoryno;
	private String categoryname;
	private String bookcode;
	private String bookname;
	private String publishday;
	private int rentbookno;
	private int rentyn;

	public BookDTO() { }
	
	public BookDTO(int categoryno, String categoryname, String bookcode, String bookname, String publishday,
			int rentbookno, int rentyn) {
		this.categoryno = categoryno;
		this.categoryname = categoryname;
		this.bookcode = bookcode;
		this.bookname = bookname;
		this.publishday = publishday;
		this.rentbookno = rentbookno;
		this.rentyn = rentyn;
	}

	public int getCategoryno() {
		return categoryno;
	}

	public void setCategoryno(int categoryno) {
		this.categoryno = categoryno;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public String getBookcode() {
		return bookcode;
	}

	public void setBookcode(String bookcode) {
		this.bookcode = bookcode;
	}

	public String getBookname() {
		return bookname;
	}

	public void setBookname(String bookname) {
		this.bookname = bookname;
	}

	public String getPublishday() {
		return publishday;
	}

	public void setPublishday(String publishday) {
		this.publishday = publishday;
	}

	public int getRentbookno() {
		return rentbookno;
	}

	public void setRentbookno(int rentbookno) {
		this.rentbookno = rentbookno;
	}

	public int getRentyn() {
		return rentyn;
	}

	public void setRentyn(int rentyn) {
		this.rentyn = rentyn;
	}
	
	
	
	
}
