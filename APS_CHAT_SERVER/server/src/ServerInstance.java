package src;

import java.io.IOException;
import java.net.ServerSocket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ServerInstance {

	private static ObservableList<ClientSession> loggedClients = FXCollections.observableArrayList();

	public static void main(String[] args) throws IOException {
		ServerSocket serverInstance = null;
		try {
			serverInstance = new ServerSocket(9876);

			while(true) {
				ClientSession clientConnection = new ClientSession(serverInstance.accept());
				loggedClients.add(clientConnection);
				clientConnection.start();
			}

		} catch (IOException e) {
			System.out.println("IOExeption error.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n");
			e.printStackTrace();
		} finally {
			if(serverInstance != null) serverInstance.close();
		}

	}

}
