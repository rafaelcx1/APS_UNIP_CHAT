package model.requests;

public class MessageRequest extends Request {

	private static final long serialVersionUID = 657771955333502982L;
	private String message;
	private String recipient;

	public MessageRequest() {
		super(OperationType.SEND_OR_RECIEVE_MSG);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

}
