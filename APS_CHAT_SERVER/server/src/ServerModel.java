package src;

import java.net.Socket;

import javafx.collections.ObservableList;
import requests.Request;

public class ServerModel {

	public static boolean broadCast(ObservableList<ClientSession> clients, Request request) {
		return false;
	}

	public static boolean sendObject(Request request, Socket client) {
		return false;
	}
}
