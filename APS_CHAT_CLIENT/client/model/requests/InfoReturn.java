package model.requests;

public class InfoReturn extends Request {

	private static final long serialVersionUID = -8921135274858873355L;
	private String message;
	private OperationType operationSource;
	private boolean errorMessage;

	public InfoReturn(boolean isErrorMessage) {
		super((isErrorMessage) ? OperationType.ERROR_MSG : OperationType.SUCCESS_MSG);
		this.errorMessage = isErrorMessage;
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

	public boolean isErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(boolean errorMessage) {
		this.errorMessage = errorMessage;
	}

}
