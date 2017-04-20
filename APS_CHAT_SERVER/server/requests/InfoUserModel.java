package requests;

public class InfoUserModel {

	private String login;
	private boolean status;

	public InfoUserModel() {

	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object user) {
		if(login.equals( ((InfoUserModel) user).getLogin()) ) {
			return true;
		}
		return false;
	}
}
