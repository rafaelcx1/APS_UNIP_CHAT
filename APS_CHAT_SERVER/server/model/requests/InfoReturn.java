package model.requests;

public class InfoReturn extends Request {

	private static final long serialVersionUID = -8921135274858873355L;
	private String message;
	private OperationType operationSource;

	public InfoReturn(boolean isErrorMessage) {
		super((isErrorMessage) ? OperationType.ERROR_MSG : OperationType.SUCCESS_MSG);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public OperationType getOperationSource() {
		return operationSource;
	}

	public void setOperationSource(OperationType operationSource) {
		this.operationSource = operationSource;
	}

}
