package model.requests;

import java.io.Serializable;

public enum OperationType implements Serializable {
	SEND_OR_RECIEVE_MSG, LOGIN, LOGOFF, INFO, ERROR_MSG, SUCCESS_MSG;
}
