package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.requests.InfoRequest;
import model.requests.InfoReturn;
import model.requests.OperationType;
import model.requests.Request;
import util.DateUtil;

public class ClientSession extends Thread {

	private Socket session;
	private String user;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private boolean logoffRequested;

	public ClientSession(Socket session) {
		logoffRequested = false;
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

	public Socket getSession() {
		return session;
	}

	public String getUser() {
		return ((user == null) ? "%not logged user%" : user);
	}

	@Override
	public void run() {
		new Thread(() -> {

			while(true) {
				if(session != null) {

					try {
						if(!session.isConnected() || !session.getInetAddress().isReachable(1500)) {

							try {
								session.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							ServerInstance.logoffClient(this);

						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

				try {
					Thread.sleep(1000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}

			}

		}).start();

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

					if(session.isClosed()) {
						System.out.println("<" + DateUtil.dateTimeNow() + "> Session closed with the user '" + getUser() + "'.\n");
						ServerInstance.logoffClient(this);
						break;
					} else {
						down = true;
						if(!logoffRequested)
							System.out.println("<" + DateUtil.dateTimeNow() + "> IOException has occurred.\nUser: '" + getUser() + "'.\nDetails: " + e.getMessage() + "\n");
					}

				} catch (ClassNotFoundException e) {
					System.out.println("<" + DateUtil.dateTimeNow() + "> Invalid Object Recieved.\nUser: '" + getUser() + "'.\nDetails: " + e.getMessage() + "\n");
				} catch (Exception e) {
					System.out.println("<" + DateUtil.dateTimeNow() + "> An error occurred.\nUser: '" + getUser() + "'\nDetails: " + e.getMessage() + "\n");
				}

			} else {
				if(!logoffRequested) {
					System.out.println("<" + DateUtil.dateTimeNow() + "> The user '" + getUser() + "' lost connection to the server. Logging out...");
					ServerInstance.logoffClient(this);
				}
				break;
			}
		}


	}

	public void treatObject(Request request) {

		switch(request.getOperation()){

			case LOGIN: {

				if(!ServerInstance.checkExistClient(user = request.getUserFrom())){
					user = request.getUserFrom();
					System.out.println("<" + DateUtil.dateTimeNow() + "> " + getUser() + " logged on server! \n");
					InfoReturn infoReturn = new InfoReturn(false);
					infoReturn.setUserFrom("Server");
					infoReturn.setUserTo(request.getUserFrom());
					infoReturn.setOperationSource(OperationType.LOGIN);
					infoReturn.setMessage("Conected!");
					ServerInstance.loginClient(this);
					ServerTasks.sendObject(infoReturn, this);
				} else {
					user = request.getUserFrom();
					System.out.println("<" + DateUtil.dateTimeNow() + "> An user tried to logon with an existent nickname logged on.\nIP Address: " + session.getInetAddress().getHostAddress() + "\n");
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
					System.out.println("<" + DateUtil.dateTimeNow() + "> " + getUser() + " disconnected from server! \n");
					logoffRequested = true;
					if(!session.isClosed()) session.close();
					ServerInstance.logoffClient(this);
				} catch (Exception e) {
					System.out.println("<" + DateUtil.dateTimeNow() + "> An error occurred on logoff of the user '" + getUser() +"'.\nDetails: " + e.getMessage() + "\n");
				}

				break;
			}

			case SEND_OR_RECIEVE_MSG: {
				System.out.println("<" + DateUtil.dateTimeNow() + "> The user '" + request.getUserFrom() + "' is sending a message to '" + ((request.getUserTo() == null) ? "Global" : request.getUserTo()) + "'.\n");
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
				System.out.println("<" + DateUtil.dateTimeNow() + "> The user '" + request.getUserFrom() + "' is requesting a info from the server!.\n");
				InfoRequest infoRequest = new InfoRequest("Server");
				infoRequest.setUserTo(request.getUserFrom());
				infoRequest.setUsers(ServerInstance.getOnlineUsers());
				Request requestCast = (Request) infoRequest;
				ServerTasks.sendObject(requestCast, this);
				break;
			}

			default: {
				System.out.println("<" + DateUtil.dateTimeNow() + "> " + request.getUserFrom() + "sended a invalid object to server!\n");
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

}
