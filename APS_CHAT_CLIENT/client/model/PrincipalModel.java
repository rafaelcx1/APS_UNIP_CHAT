package model;

import javafx.collections.ObservableList;
import model.requests.InfoRequest;
import model.requests.MessageRequest;
import model.requests.Request;

public class PrincipalModel {

	private String errorMessage = "";
	private String nickname;
	private ObservableList<String> users;
	private ObservableList<String> globalChatMsg;

	public PrincipalModel(String nickname) {
		this.nickname = nickname;
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

	public boolean sendObject(Request request) {
		return false;
	}
}
