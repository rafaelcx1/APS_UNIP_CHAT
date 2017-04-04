package src;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import requests.Request;

public class ServerTasks {

	public static void broadcast(Request request, ClientSession[] clients) {
		for(ClientSession client : clients) {
			request.setUserTo(client.getUser());
			try(ObjectOutputStream oos = (ObjectOutputStream) client.getSession().getOutputStream()) {
				System.out.println("Sending request to '" + client.getUser() + "' from '" + request.getUserFrom() + "'...");
				oos.writeObject(request);
				System.out.println("Send request to '" + client.getUser() + "' from '" + request.getUserFrom() + "' complete with success.\n");
			} catch (IOException e) {
				System.out.println("IOExeption error with user '" + client.getUser() + "' from '" + request.getUserFrom() + "' on broadcast.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n");
			} catch(NullPointerException e) {
				System.out.println("NullPointerException error with user '" + client.getUser() + "' from '" + request.getUserFrom() + "' on broadcast.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n");
			} catch(Exception e) {
				System.out.println("Exception error with user '" + client.getUser() + "' from '" + request.getUserFrom() + "' on broadcast.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n");
			}
		}
		System.out.println("Broadcast Request Done!");
	}

	public static boolean sendObject(Request request) {
		Socket userToConnection = ServerInstance.getClientSession(request.getUserTo());
		try(ObjectOutputStream oos = (ObjectOutputStream) userToConnection.getOutputStream()) {
			System.out.println("Sending request to '" + request.getUserTo() + "' from '" + request.getUserFrom() + "'...");
			oos.writeObject(request);
			System.out.println("Send request to '" + request.getUserTo() + "' from '" + request.getUserFrom() + "' complete with success.\n");
			return true;
		} catch(IOException e) {
			System.out.println("IOExeption error with user '" + request.getUserTo() + "' from '" + request.getUserFrom() + "' on sendObject.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n");
			return false;
		} catch(NullPointerException e) {
			System.out.println("NullPointerException error with user '" + request.getUserTo() + "' from '" + request.getUserFrom() + "' on sendObject.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n");
			return false;
		} catch(Exception e) {
			System.out.println("Exception error with user '" + request.getUserTo() + "' from '" + request.getUserFrom() + "' on sendObject.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n");
			return false;
		}
	}

}