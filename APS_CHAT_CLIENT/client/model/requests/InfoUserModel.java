package model.requests;

import java.io.Serializable;

public class InfoUserModel implements Serializable {

	private static final long serialVersionUID = 7690493801655956285L;
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
