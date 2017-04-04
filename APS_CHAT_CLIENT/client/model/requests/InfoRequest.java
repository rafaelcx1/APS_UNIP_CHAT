package model.requests;

public class InfoRequest extends Request {

	private static final long serialVersionUID = 7134939562156205459L;
	private InfoUserModel[] users;

	public InfoRequest(String user) {
		super(OperationType.INFO);
		super.setUserFrom(user);
	}

	public InfoUserModel[] getUsers() {
		return users;
	}

	public void setUsers(InfoUserModel[] users) {

	}

}
