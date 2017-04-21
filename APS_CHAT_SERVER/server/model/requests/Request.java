package model.requests;

import java.io.Serializable;

public class Request implements Serializable {

	private static final long serialVersionUID = -8674865266376875860L;
	private String userFrom;
	private String userTo;
	private OperationType operation;

	public Request(OperationType operation) {
		this.operation = operation;
	}

	public String getUserFrom() {
		return userFrom;
	}

	public void setUserFrom(String userFrom) {
		this.userFrom = userFrom;
	}

	public String getUserTo() {
		return userTo;
	}

	public void setUserTo(String userTo) {
		this.userTo = userTo;
	}

	public OperationType getOperation() {
		return operation;
	}

	public void setOperation(OperationType operation) {
		this.operation = operation;
	}

}
