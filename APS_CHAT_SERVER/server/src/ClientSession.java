package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import model.requests.InfoRequest;
import model.requests.InfoReturn;
import model.requests.OperationType;
import model.requests.Request;

public class ClientSession extends Thread {

	private Socket session;
	private String user;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public ClientSession(Socket session) {
		this.session = session;
		try {
			session.setKeepAlive(true);
			this.oos = new ObjectOutputStream(session.getOutputStream());
			this.ois = new ObjectInputStream(session.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ObjectInputStream getOis() {
		return ois;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	@Override
	public void run() {
		sessionStart();
	}

	public void sessionStart() {
		boolean down = false;
		while(true) {
			if(!down) {

				try {

					Object obj = ois.readObject();

					if(obj instanceof Request) {
						Request request = (Request) obj;
						treatObject(request);
					}

				} catch(IOException e) {
					down = true;
					System.out.println("IOException has occurred.\nUser: " + ((user == null) ? "%not logged user%" : user) + ".\nDetails: " + e.getMessage() + "\nTime: " + LocalDateTime.now().toString());
				} catch (ClassNotFoundException e) {
					System.out.println("Invalid Object Recieved.\nUser: " + ((user == null) ? "%not logged user%" : user) + ".\nDetails: " + e.getMessage() + "\n" + LocalDateTime.now().toString() + "\n");
				} catch (Exception e) {
					System.out.println("An error occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n" + LocalDateTime.now().toString() + "\n");
				}

			} else {
				System.out.println("The user " + ((user == null) ? "%not logged user%" : user) + " lost connection to the server. Logging out...");
				ServerInstance.logoffClient(this);
				break;
			}
		}


	}

	public void treatObject(Request request) {
		switch(request.getOperation()){

		case LOGIN: {

			if(!ServerInstance.checkExistClient(user = request.getUserFrom())){
				user = request.getUserFrom();
				System.out.println(user + " logged on server! | " + LocalDateTime.now().toString() + "\n");
				InfoReturn infoReturn = new InfoReturn(false);
				infoReturn.setUserFrom("Server");
				infoReturn.setUserTo(request.getUserFrom());
				infoReturn.setOperationSource(OperationType.LOGIN);
				infoReturn.setMessage("Conected!");
				ServerInstance.loginClient(this);
				ServerTasks.sendObject(infoReturn, this);
			} else {
				user = request.getUserFrom();
				System.out.println("An user tried to logon with an existent nickname logged on | " + LocalDateTime.now().toString() + "\n");
				InfoReturn infoReturn = new InfoReturn(true);
				infoReturn.setUserFrom("Server");
				infoReturn.setUserTo(request.getUserFrom());
				infoReturn.setOperationSource(OperationType.LOGIN);
				infoReturn.setMessage("Incorrect Login. You tried to logon with a nickname that is already logged on.");
				ServerTasks.sendObject(infoReturn, this);
			}

			break;
		}

		case LOGOFF: {

			try {
				System.out.println(((request.getUserFrom() == null) ? "%not logged user%" : request.getUserFrom())  + " deslogged from server! | " + LocalDateTime.now().toString() + "\n");
				session.close();
				ServerInstance.logoffClient(this);
			} catch (Exception e) {
				System.out.println("An error occurred on logoff of the user '" + request.getUserFrom() +"'.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n" + LocalDateTime.now().toString() + "\n");
			}

			break;
		}

		case SEND_OR_RECIEVE_MSG: {
			System.out.println("The user '" + request.getUserFrom() + "' is sending a message to '" + ((request.getUserTo() == null) ? "Global" : request.getUserTo()) + "'. | " + LocalDateTime.now().toString() + "\n");
			if(request.getUserTo() == null) {
				ServerTasks.broadcast(request, ServerInstance.getLoggedClients(), new ClientSession[] {this});
			} else {
				if(ServerTasks.sendObject(request, ServerInstance.getClientSession(request.getUserTo()))){
					InfoReturn infoReturn = new InfoReturn(false);
					infoReturn.setUserFrom(request.getUserTo());
					infoReturn.setUserTo(request.getUserFrom());
					infoReturn.setOperationSource(OperationType.SEND_OR_RECIEVE_MSG);
					infoReturn.setMessage("Message sended!");
					Request requestCast = (Request) infoReturn;
					ServerTasks.sendObject(requestCast, this);
				} else {
					InfoReturn infoReturn = new InfoReturn(true);
					infoReturn.setUserFrom(request.getUserTo());
					infoReturn.setUserTo(request.getUserFrom());
					infoReturn.setOperationSource(OperationType.SEND_OR_RECIEVE_MSG);
					infoReturn.setMessage("Error on send message.");
					Request requestCast = (Request) infoReturn;
					ServerTasks.sendObject(requestCast, this);
				}
			}
			break;
		}

		case INFO: {
			System.out.println("The user '" + request.getUserFrom() + "' is requesting a info from the server!. | " + LocalDateTime.now().toString() + "\n");
			InfoRequest infoRequest = new InfoRequest("Server");
			infoRequest.setUserTo(request.getUserFrom());
			infoRequest.setUsers(ServerInstance.getOnlineUsers());
			Request requestCast = (Request) infoRequest;
			ServerTasks.sendObject(requestCast, this);
			break;
		}
		default: {
			System.out.println(request.getUserFrom() + " sended a invalid object to server! | " + LocalDateTime.now().toString() + "\n");
			InfoReturn infoReturn = new InfoReturn(true);
			infoReturn.setUserFrom("Server");
			infoReturn.setUserTo(request.getUserFrom());
			infoReturn.setOperationSource(OperationType.LOGIN);
			infoReturn.setMessage("Invalid Object Sended to the Server.");
			Request requestCast = (Request) infoReturn;
			ServerTasks.sendObject(requestCast, this);
		}

		}
	}

	public Socket getSession() {
		return session;
	}

	public String getUser() {
		return user;
	}

}
