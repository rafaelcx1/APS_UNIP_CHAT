package model.requests;

import java.io.Serializable;

public class MessageRequest extends Request implements Serializable {

	private static final long serialVersionUID = 657771955333502982L;
	private String message;

	public MessageRequest() {
		super(OperationType.SEND_OR_RECIEVE_MSG);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
