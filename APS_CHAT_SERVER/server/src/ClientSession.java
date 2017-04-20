package src;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import requests.InfoRequest;
import requests.InfoReturn;
import requests.OperationType;
import requests.Request;

public class ClientSession implements Runnable {

	private Socket session;
	private String user;
	private LerObjetoArquivo ois;
	private ObjectOutputStream oos;

	public ClientSession(Socket session) {
		this.session = session;
		try {
			this.oos = new GravarObjetoArquivo(session.getOutputStream());
			this.ois = new LerObjetoArquivo(session.getInputStream());
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
		// Thread para testar conexão com o client
		new Thread(() -> {
			while(!session.isClosed() && session.isConnected()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("An error occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n" + LocalDateTime.now().toString() + "\n");
				}
			}
			try {
				ServerInstance.logoffClient(this);
				System.out.println("Closing session with user '" + ((user != null) ? user : "%not logged user%") + "'... | " + LocalDateTime.now().toString());
				System.out.println("Session with user '" + ((user != null) ? user : "%not logged user%") + "' closed. | " + LocalDateTime.now().toString());
				this.finalize();
				this.closeThread();
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				System.out.println("An error occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n" + LocalDateTime.now().toString() + "\n");
			} catch (Throwable e) {
				System.out.println("An error occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n" + LocalDateTime.now().toString() + "\n");
			}
		}).start();

		sessionStart();
	}

	public void closeThread() throws Throwable {
		Thread.currentThread().interrupt();
		this.finalize();
	}

	public void sessionStart() {

		while(!session.isClosed() && session.isConnected()) {
			try {
				if(session.getInputStream().read() > -1) {
					Request request = (Request) ois.readObject();
					treatObject(request);
				}
			} catch(IOException e) {
				if(session == null || session.isClosed()) {
					try {
						closeThread();
						e.printStackTrace();
					} catch (Throwable e1) {
						e1.printStackTrace();
					}
				} else {
					System.out.println("IOException occurred.\nDetails: " + e.getMessage() + "\n" + e.getCause() + "\n" + LocalDateTime.now().toString() + "\n");
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Invalid Object Recieved.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n" + LocalDateTime.now().toString() + "\n");
			} catch (Exception e) {
				System.out.println("An error occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage() + "\n" + LocalDateTime.now().toString() + "\n");
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
				ServerTasks.sendObject(infoReturn);
				ServerInstance.loginClient(this);
			} else {
				user = request.getUserFrom();
				System.out.println(user + " failed to loggon! | " + LocalDateTime.now().toString() + "\n");
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
				System.out.println(request.getUserFrom() + " deslogged on server! | " + LocalDateTime.now().toString() + "\n");
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
				if(ServerTasks.sendObject(request)){
					InfoReturn infoReturn = new InfoReturn(false);
					infoReturn.setUserFrom(request.getUserTo());
					infoReturn.setUserTo(request.getUserFrom());
					infoReturn.setOperationSource(OperationType.SUCCESS_MSG);
					infoReturn.setMessage("Message sended!");
					ServerTasks.sendObject(infoReturn);
				} else {
					InfoReturn infoReturn = new InfoReturn(true);
					infoReturn.setUserFrom(request.getUserTo());
					infoReturn.setUserTo(request.getUserFrom());
					infoReturn.setOperationSource(OperationType.ERROR_MSG);
					infoReturn.setMessage("Error on send message.");
					ServerTasks.sendObject(infoReturn);
				}
			}
			break;
		}

		case INFO: {
			System.out.println("The user '" + request.getUserFrom() + "' is requesting a info from the server!. | " + LocalDateTime.now().toString() + "\n");
			InfoRequest infoRequest = new InfoRequest("Server");
			infoRequest.setUserTo(request.getUserFrom());
			infoRequest.setUsers(ServerInstance.getOnlineUsers());
			ServerTasks.sendObject(infoRequest);
			break;
		}
		default: {
			System.out.println(request.getUserFrom() + " sended a invalid object to server! | " + LocalDateTime.now().toString() + "\n");
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

	public class LerObjetoArquivo extends ObjectInputStream{

	  public LerObjetoArquivo(InputStream in) throws IOException {
		  super(in);
	  }

	  @Override
	  protected void readStreamHeader() throws IOException {

	  }

	}

	public class GravarObjetoArquivo extends ObjectOutputStream {

	  public GravarObjetoArquivo(OutputStream out) throws IOException {
		  super(out);
	  }

	  @Override
	  protected void writeStreamHeader() throws IOException {
		  reset();
	  }

	}
}
