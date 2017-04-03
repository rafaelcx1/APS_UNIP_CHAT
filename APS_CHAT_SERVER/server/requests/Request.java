package requests;

@SuppressWarnings("unused")
public class Request {

	private String login;
	private OperationType operation;

	public Request(OperationType operation) {
		this.operation = operation;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
}
