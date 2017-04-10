package model;

import javafx.collections.ObservableList;
import model.requests.MessageRequest;

public class MessageModel {

	private String loginRecipient;
	private ObservableList<MessageRequest> chatMsg;

	public MessageModel(String loginRecipient) {

	}

	public String getLoginRecipient() {
		return loginRecipient;
	}

	public ObservableList<MessageRequest> getChatMsg() {
		return chatMsg;
	}

	public boolean treatObject(MessageRequest msgRequest) {
		return false;
	}

	public boolean sendMsgObject(MessageRequest msgRequest) {
		return false;
	}

}
