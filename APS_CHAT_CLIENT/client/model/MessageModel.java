package model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import controller.MainController;
import javafx.collections.ObservableList;
import model.requests.InfoReturn;
import model.requests.MessageRequest;
import model.requests.OperationType;

public class MessageModel {

	private String loginRecipient;
	private ObservableList<MessageRequest> chatMsg;
	private String errorMessage = "";
	private List<MessageRequest> tempMsg = new ArrayList<>();

	public MessageModel(String loginRecipient) {
		this.loginRecipient = loginRecipient;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public List<MessageRequest> getTempMsg() {
		return tempMsg;
	}

	public String getLoginRecipient() {
		return loginRecipient;
	}

	public ObservableList<MessageRequest> getChatMsg() {
		return chatMsg;
	}

	public boolean treatObject(MessageRequest msgRequest) {
		chatMsg.add(msgRequest);
		return true;
	}

	public boolean treatObject(InfoReturn info) {
		if(info.getOperation() == OperationType.SUCCESS_MSG && info.getOperation() == OperationType.SEND_OR_RECIEVE_MSG) {
			for(MessageRequest msg : tempMsg) {
				chatMsg.add(msg);
				tempMsg.remove(msg);
			}
			return true;
		} else {
			errorMessage = info.getMessage();
			return false;
		}
	}

	public boolean sendMsgObject(MessageRequest msgRequest) {
		try(ObjectOutputStream oos = new ObjectOutputStream(MainController.getConnection().getOutputStream())) {
			oos.writeObject(msgRequest);
			tempMsg.add(msgRequest);
			return true;
		} catch(IOException e) {
			errorMessage = "IOException has occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage();
			return false;
		}
	}

}
