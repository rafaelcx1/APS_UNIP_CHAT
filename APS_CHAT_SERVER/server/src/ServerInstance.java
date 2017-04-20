package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import requests.InfoRequest;
import requests.InfoUserModel;

public class ServerInstance {

	private static ObservableList<ClientSession> loggedClients = FXCollections.observableArrayList();

	public static void main(String[] args) {
		loggedClients.addListener(new LoggedClientEvent());

		try(ServerSocket serverInstance = new ServerSocket(9876)) {
			System.out.println("Server started!\nLogs below:\n");

			while(true) {
				ClientSession clientConnection = new ClientSession(serverInstance.accept());
				new Thread(clientConnection).start();
				System.out.println("Client connection. IP: " + clientConnection.getSession().getInetAddress().getHostAddress() + " | " + LocalDateTime.now().toString() + "\n");
			}

		} catch (IOException e) {
			System.out.println("IOExeption error.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n");
		}

	}

	public static void logoffClient(ClientSession client) {
		loggedClients.remove(client);
	}

	public static void loginClient(ClientSession client) {
		loggedClients.add(client);
	}

	public static ClientSession getClientSession(String user) {
		for(ClientSession client : loggedClients) {
			if(client.getUser().equals(user))
				return client;
		}
		return null;
	}

	public static boolean checkExistClient(String user) {
		for(ClientSession client : loggedClients) {
			if(client.getUser().equals(user))
				return true;
		}
		return false;
	}

	public static InfoUserModel[] getOnlineUsers() {
		List<InfoUserModel> users = new ArrayList<>();
		for(ClientSession client : loggedClients) {
			InfoUserModel user = new InfoUserModel();
			user.setLogin(client.getUser());
			user.setStatus(true);
			users.add(user);
		}
		return (InfoUserModel[]) users.toArray();
	}

	public static ClientSession[] getLoggedClients() {
		return (ClientSession[]) loggedClients.toArray();
	}

	private static class LoggedClientEvent implements ListChangeListener<ClientSession> {

		@Override
		public void onChanged(Change<? extends ClientSession> c) {

			while(c.next()) {

				if(c.wasAdded()) {
					List<InfoUserModel> users = new ArrayList<>();

					for(ClientSession client : c.getAddedSubList()) {
						InfoUserModel user = new InfoUserModel();
						user.setLogin(client.getUser());
						user.setStatus(true);
						users.add(user);
					}

					InfoRequest infoRequest = new InfoRequest("Server");
					infoRequest.setUsers((InfoUserModel[]) users.toArray());
					ServerTasks.broadcast(infoRequest, (ClientSession[]) loggedClients.toArray(), (ClientSession[]) c.getAddedSubList().toArray());

				} else {
					List<InfoUserModel> users = new ArrayList<>();

					for(ClientSession client : c.getRemoved()) {
						InfoUserModel user = new InfoUserModel();
						user.setLogin(client.getUser());
						user.setStatus(false);
						users.add(user);
					}

					InfoRequest infoRequest = new InfoRequest("Server");
					infoRequest.setUsers((InfoUserModel[]) users.toArray());
					ServerTasks.broadcast(infoRequest, (ClientSession[]) loggedClients.toArray(), (ClientSession[]) c.getRemoved().toArray());
				}

			}

		}

	}

}
