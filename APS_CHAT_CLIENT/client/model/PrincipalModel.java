package model;

import java.io.IOException;

import controller.MainController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.requests.InfoRequest;
import model.requests.InfoUserModel;
import model.requests.MessageRequest;
import model.requests.Request;

public class PrincipalModel {

	private String errorMessage = "";
	private String nickname;
	private ObservableList<InfoUserModel> users;
	private ObservableList<MessageRequest> globalChatMsg;

	public PrincipalModel(String nickname) {
		this.nickname = nickname;
		users = FXCollections.observableArrayList();
		globalChatMsg = FXCollections.observableArrayList();
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getNickname() {
		return nickname;
	}

	public ObservableList<InfoUserModel> getUsers() {
		return users;
	}

	public ObservableList<MessageRequest> getGlobalChatMsg() {
		return globalChatMsg;
	}

	public boolean treatObject(MessageRequest request) {
		if(request.getUserTo() == null) {
			globalChatMsg.add(request);
			return true;
		} else {
			errorMessage = "Invalid User";
			return false;
		}
	}

	public boolean treatObject(InfoRequest request) {
		if(request.getUserTo().equals(nickname)) {
			if(request.getUsers() != null) {
				for(InfoUserModel user : request.getUsers()) {
					if(!user.getLogin().equals(nickname)) {
						if(users.contains(user)) {
							if(!user.isStatus())
								users.remove(user);
						} else {
							users.add(user);
						}
					}
				}
			}
			return true;
		} else {
			errorMessage = "Invalid User";
			return false;
		}
	}

	public boolean sendObject(Request request) {
		try {
			MainController.getOos().writeObject(request);
			return true;
		} catch (IOException e) {
			errorMessage = "IOException has been occurred " + e.getMessage();
			e.printStackTrace();
			return false;
		}
	}
}
