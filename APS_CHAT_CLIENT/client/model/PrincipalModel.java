package model;

import javafx.collections.ObservableList;
import model.requests.InfoRequest;
import model.requests.MessageRequest;

public class PrincipalModel {

	private String errorMessage = "";
	private String nickname;
	private ObservableList<String> users;
	private ObservableList<String> globalChatMsg;

	public PrincipalModel(String nickname) {

	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getnickname() {
		return nickname;
	}

	public ObservableList<String> getUsers() {
		return users;
	}

	public ObservableList<String> getGlobalChatMsg() {
		return globalChatMsg;
	}

	public boolean treatObject(MessageRequest request) {
		return false;
	}

	public boolean treatObject(InfoRequest request) {
		return false;
	}

	public boolean sedObject(MessageRequest request) {
		return false;
	}
}
