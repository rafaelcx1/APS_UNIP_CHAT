package model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalTime;

import controller.MainController;
import javafx.collections.ObservableList;
import model.requests.InfoRequest;
import model.requests.InfoUserModel;
import model.requests.MessageRequest;
import model.requests.Request;

public class PrincipalModel {

	private String errorMessage = "";
	private String nickname;
	private ObservableList<InfoUserModel> users;
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

	public ObservableList<InfoUserModel> getUsers() {
		return users;
	}

	public ObservableList<String> getGlobalChatMsg() {
		return globalChatMsg;
	}

	public boolean treatObject(MessageRequest request) {
		if(request.getUserTo().equals(nickname)) {
			globalChatMsg.add(LocalTime.now() + " | " + request.getUserFrom() + ": " + request.getMessage());
			return true;
		} else {
			errorMessage = "Invalid User";
			return false;
		}
	}

	public boolean treatObject(InfoRequest request) {
		if(request.getUserTo().equals(nickname)) {
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
			return true;
		} else {
			errorMessage = "Invalid User";
			return false;
		}
	}

	public boolean sendObject(Request request) {
		try(ObjectOutputStream ois = (ObjectOutputStream) MainController.getConnection().getOutputStream()) {
			ois.writeObject(request);
			return true;
		} catch (IOException e) {
			errorMessage = "IOException has been occurred " + e.getMessage();
			e.printStackTrace();
			return false;
		}
	}
}
