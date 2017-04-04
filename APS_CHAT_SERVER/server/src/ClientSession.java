package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import requests.InfoRequest;
import requests.InfoReturn;
import requests.OperationType;
import requests.Request;

public class ClientSession implements Runnable {

	private Socket session;
	private String user;

	public ClientSession(Socket session) {
		this.session = session;
	}

	@Override
	public void run() {
		sessionStart();
	}

	public void sessionStart() {
		try(ObjectInputStream ois = (ObjectInputStream) session.getInputStream()) {

			while(true) {
				if(ois.read() > -1) {
					Request request = (Request) ois.readObject();
					new Thread(() -> treatObject(request)).start();
					Thread.sleep(100);
				}
			}

		} catch(IOException e) {
			System.out.println("IOException occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Invalid Object Recieved.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
		} catch (Exception e) {
			System.out.println("An error occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
		}
	}

	public void treatObject(Request request) {
		switch(request.getOperation()){

			case LOGIN: {

				if(!ServerInstance.checkExistClient(user = request.getUserFrom())){
					user = request.getUserFrom();
					System.out.println(user + " logged on server!");
					InfoReturn infoReturn = new InfoReturn(false);
					infoReturn.setUserFrom("Server");
					infoReturn.setUserTo(request.getUserFrom());
					infoReturn.setOperationSource(OperationType.LOGIN);
					infoReturn.setMessage("Conected!");
					ServerTasks.sendObject(infoReturn);
					//Info users
					InfoRequest infoRequest = new InfoRequest("Server");
					infoRequest.setUserTo(request.getUserFrom());
					infoRequest.setUsers(ServerInstance.getOnlineUsers());
					ServerTasks.sendObject(infoRequest);
				} else {
					user = request.getUserFrom();
					System.out.println(user + " failed to loggon!");
					InfoReturn infoReturn = new InfoReturn(true);
					infoReturn.setUserFrom("Server");
					infoReturn.setUserTo(request.getUserFrom());
					infoReturn.setOperationSource(OperationType.LOGIN);
					infoReturn.setMessage("Incorrect Login.");
					ServerTasks.sendObject(infoReturn);
				}

				break;
			}

			case LOGOFF: {

				try {
					System.out.println(request.getUserFrom() + " deslogged on server!");
					session.close();
					ServerInstance.logoffClient(this);
				} catch (Exception e) {
					System.out.println("An error occurred on logoff of the user '" + request.getUserFrom() +"'.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
				}

				break;
			}

			case SEND_OR_RECIEVE_MSG: {
				ServerTasks.sendObject(request);
				break;
			}

			case INFO: {
				InfoRequest infoRequest = new InfoRequest("Server");
				infoRequest.setUserTo(request.getUserFrom());
				infoRequest.setUsers(ServerInstance.getOnlineUsers());
				ServerTasks.sendObject(infoRequest);
				break;
			}
			default: {
				System.out.println(request.getUserFrom() + " sended a invalid object to server!");
				InfoReturn infoReturn = new InfoReturn(true);
				infoReturn.setUserFrom("Server");
				infoReturn.setUserTo(request.getUserFrom());
				infoReturn.setOperationSource(OperationType.LOGIN);
				infoReturn.setMessage("Invalid Object Sended to the Server.");
				ServerTasks.sendObject(infoReturn);
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
