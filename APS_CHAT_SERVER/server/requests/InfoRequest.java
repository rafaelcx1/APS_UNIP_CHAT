package requests;

public class InfoRequest extends Request{

	private InfoUserModel[] users;

	public InfoRequest(String login) {
		super(OperationType.INFO);
		super.setLogin(login);;
	}

	public InfoUserModel[] getUsers() {
		return users;
	}

	public void setUsers(InfoUserModel[] users) {

	}

}
