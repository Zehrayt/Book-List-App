package util;

public class Author {

	private String autName, autSurname;
	private int autAge, authId;
	
	public Author() {
		// TODO Auto-generated constructor stub
	}

	public String getAutName() {
		return autName;
	}

	public void setAutName(String autName) {
		this.autName = autName;
	}

	public String getAutSurname() {
		return autSurname;
	}

	public void setAutSurname(String autSurname) {
		this.autSurname = autSurname;
	}

	public int getAutAge() {
		return autAge;
	}

	public void setAutAge(int autAge) {
		this.autAge = autAge;
	}

	public int getAuthId() {
		return authId;
	}

	public void setAuthId(int authId) {
		this.authId = authId;
	}
	
	@Override
	public String toString() {
	    return autName + " " + autSurname;
	}
}
