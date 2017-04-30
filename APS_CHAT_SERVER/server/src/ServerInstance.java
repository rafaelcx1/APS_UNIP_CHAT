package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import model.requests.InfoRequest;
import model.requests.InfoUserModel;
import model.requests.Request;
import util.DateUtil;

public class ServerInstance {

	private static ObservableList<ClientSession> loggedClients = FXCollections.observableArrayList();
	private static List<ClientSession> clientsThread = new ArrayList<>();

	public static void main(String[] args) {
		loggedClients.addListener(new LoggedClientEvent());

		try(ServerSocket serverInstance = new ServerSocket(9876)) {
			System.out.println("<" + DateUtil.dateTimeNow() + "> Server started! Logs below:\n\n");

			while(true) {
				ClientSession clientConnection = new ClientSession(serverInstance.accept());
				clientsThread.add(clientConnection);
				clientConnection.start();
				System.out.println("<" + DateUtil.dateTimeNow() + ">" + " Client connection. IP: " + clientConnection.getSession().getInetAddress().getHostAddress() + ">\n");
			}

		} catch (IOException e) {
			System.out.println("<" + DateUtil.dateTimeNow() + ">" + " IOExeption error.\nDetails: " + e.getMessage() + "\n");
		}

	}

	public static void logoffClient(ClientSession client) {
		loggedClients.remove(client);
		int index;
		if((index = clientsThread.indexOf(client)) > -1) {
			clientsThread.get(index).interrupt();
			clientsThread.remove(client);
		}
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
		InfoUserModel[] usersArray = new InfoUserModel[users.size()];
		return users.toArray(usersArray);
	}

	public static ClientSession[] getLoggedClients() {
		ClientSession[] loggedClientsArray = new ClientSession[loggedClients.size()];
		return loggedClients.toArray(loggedClientsArray);
	}

	private static class LoggedClientEvent implements ListChangeListener<ClientSession> {

		@Override
		public void onChanged(Change<? extends ClientSession> c) {

			while(c.next()) {

				if(c.wasAdded()) {
					ArrayList<InfoUserModel> users = new ArrayList<>();

					for(ClientSession client : c.getAddedSubList()) {
						InfoUserModel user = new InfoUserModel();
						user.setLogin(client.getUser());
						user.setStatus(true);
						users.add(user);
					}


					InfoUserModel[] infoUser = new InfoUserModel[users.size()];
					ClientSession[] clients = new ClientSession[loggedClients.size()];
					ClientSession[] exceptions = new ClientSession[c.getRemoved().size()];
					InfoRequest infoRequest = new InfoRequest("Server");
					infoRequest.setUsers(users.toArray(infoUser));
					Request request = (Request) infoRequest;
					ServerTasks.broadcast(request, loggedClients.toArray(clients), c.getAddedSubList().toArray(exceptions));

				} else {
					List<InfoUserModel> users = new ArrayList<>();

					for(ClientSession client : c.getRemoved()) {
						InfoUserModel user = new InfoUserModel();
						user.setLogin(client.getUser());
						user.setStatus(false);
						users.add(user);
					}

					InfoUserModel[] infoUser = new InfoUserModel[users.size()];
					ClientSession[] clients = new ClientSession[loggedClients.size()];
					ClientSession[] exceptions = new ClientSession[c.getRemoved().size()];
					InfoRequest infoRequest = new InfoRequest("Server");
					infoRequest.setUsers(users.toArray(infoUser));
					Request request = (Request) infoRequest;
					ServerTasks.broadcast(request, loggedClients.toArray(clients), c.getRemoved().toArray(exceptions));
				}

			}

		}

	}

}
