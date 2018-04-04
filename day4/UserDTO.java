package jdbc.day4;

public class UserDTO {
	
	private String userid;
	private String passwd;
	private String name;
	private String birthday;
	private String email;
	private String tel;
	private String address;
	private int coin;
	private int point;
	private int renttotal;
	private int noreturn;
	private int arear;
	
	public UserDTO() { }
	
	public UserDTO(String userid, String passwd, String name, String birthday, String email, String tel, String address,
			int coin, int point, int renttotal, int noreturn, int arear) {
		
		this.userid = userid;
		this.passwd = passwd;
		this.name = name;
		this.birthday = birthday;
		this.email = email;
		this.tel = tel;
		this.address = address;
		this.coin = coin;
		this.point = point;
		this.renttotal = renttotal;
		this.noreturn = noreturn;
		this.arear = arear;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getRenttotal() {
		return renttotal;
	}

	public void setRenttotal(int renttotal) {
		this.renttotal = renttotal;
	}

	public int getNoreturn() {
		return noreturn;
	}

	public void setNoreturn(int noreturn) {
		this.noreturn = noreturn;
	}

	public int getArear() {
		return arear;
	}

	public void setArear(int arear) {
		this.arear = arear;
	}
	
}
