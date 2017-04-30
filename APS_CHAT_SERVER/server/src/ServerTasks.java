package src;

import java.io.IOException;

import model.requests.Request;
import util.DateUtil;

public class ServerTasks {

	public static void broadcast(Request request, ClientSession[] clients, ClientSession[] clientExceptions) {
		System.out.println("<" + DateUtil.dateTimeNow() + "> Broadcast Requested!\n");
		for(ClientSession client : clients) {
			boolean contains = false;
			for(ClientSession clientException : clientExceptions) {
				if(client.getUser().equals(clientException.getUser())) {
					contains = true;
					break;
				}
			}
			if(!contains) {
				if(request.getUserTo() != null)
					request.setUserTo(client.getUser());
				try {
					System.out.println("<" + DateUtil.dateTimeNow() + "> Sending request(" + request.getOperation() + ") to '" + client.getUser() + "' from '" + request.getUserFrom() + "'... ");
					client.getOos().writeObject(request);
					System.out.println("<" + DateUtil.dateTimeNow() + "> Send request(" + request.getOperation() + ") to '" + client.getUser() + "' from '" + request.getUserFrom() + "' complete with success.\n");
				} catch (IOException e) {
					System.out.println("<" + DateUtil.dateTimeNow() + "> IOExeption error with user '" + client.getUser() + "' from '" + request.getUserFrom() + "' on broadcast.\nDetails: " + e.getMessage());
				} catch(NullPointerException e) {
					System.out.println("<" + DateUtil.dateTimeNow() + "> NullPointerException error with user '" + client.getUser() + "' from '" + request.getUserFrom() + "' on broadcast.\nDetails: " + e.getMessage());
				} catch(Exception e) {
					System.out.println("<" + DateUtil.dateTimeNow() + "> Exception error with user '" + client.getUser() + "' from '" + request.getUserFrom() + "' on broadcast.\nDetails: " + e.getMessage());
				}
			}
		}
		System.out.println("<" + DateUtil.dateTimeNow() + "> Broadcast Request Done!\n");
	}

	public static boolean sendObject(Request request, ClientSession client) {
		try {
			System.out.println("<" + DateUtil.dateTimeNow() + "> Sending request(" + request.getOperation() + ") to '" + request.getUserTo() + "' from '" + request.getUserFrom());
			client.getOos().writeObject(request);
			System.out.println("<" + DateUtil.dateTimeNow() + "> Send request(" + request.getOperation() + ") to '" + request.getUserTo() + "' from '" + request.getUserFrom() + "' complete with success.\n");
			return true;
		} catch(IOException e) {
			System.out.println("<" + DateUtil.dateTimeNow() + "> IOExeption error with user '" + request.getUserTo() + "' from '" + request.getUserFrom() + "' on sendObject.\nDetails: " + e.getMessage() + "\n");
			return false;
		} catch(NullPointerException e) {
			System.out.println("<" + DateUtil.dateTimeNow() + "> NullPointerException error with user '" + request.getUserTo() + "' from '" + request.getUserFrom() + "' on sendObject.\nDetails: " + e.getMessage() + "\n");
			return false;
		} catch(Exception e) {
			System.out.println("<" + DateUtil.dateTimeNow() + "> Exception error with user '" + request.getUserTo() + "' from '" + request.getUserFrom() + "' on sendObject.\nDetails: " + e.getMessage() + "\n");
			return false;
		}
	}

}