package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import controller.controllers.LoginController;
import controller.controllers.MessageController;
import controller.controllers.PrincipalController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.requests.OperationType;
import model.requests.Request;

public class MainController extends Application {

	/* connection = Atributo de conex�o com o servidor
	 * testConnection = InstÃ¢ncia da Thread que testa a conex�o com o servidor
	 * recieveObject = InstÃ¢ncia da Thread que recebe os objetos do servidor
	 * loginController = InstÃ¢ncia da tela de login
	 * principalController = InstÃ¢ncia da tela Principal
	 * messageWindows = Lista com as janelas de mensagem
	 * chatWindowsUsers = Lista com listener com os usu�rios de cada janela
	 * connectionStatus = Atributo com o status da conex�o, caso a conex�o falhe, este atributo ser� setado como false
	 * userLogged = Atributo para indicar se h� um usu�rio logado
	 * nickname = Atributo para armazenar o nickname do usu�rio
	 * rootStage = Stage(Janela) principal
	 */
	private static Socket connection = new Socket();
	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;
	private static RecieveObjectThread recieveObject;
	private static Thread recieveObjectT = new Thread(recieveObject);
	private LoginController loginController;
	private PrincipalController principalController;
	private ArrayList<MessageController> messageWindows = new ArrayList<>();
	private ObservableSet<String> chatWindowsUsers = FXCollections.observableSet();
	private BooleanProperty connectionStatus = new SimpleBooleanProperty(true);
	private boolean userLogged;
	private String nickname;
	private Stage rootStage;
	private String host;


	// M�todo main - Inicial
	public static void main(String[] args) {
		launch(args);
	}

	// M�todo que retorna o Socket da conex�o
	public static Socket getConnection() {
		return connection;
	}

	public static ObjectInputStream getOis() {
		return ois;
	}

	public static ObjectOutputStream getOos() {
		return oos;
	}

	// M�todo get que retorna o nickname
	public String getNickname() {
		return nickname;
	}

	// M�todo que retorna o Socket da conex�o
	public synchronized boolean setConnection(String host) {
		this.host = host;
		connection = new Socket();
		try {
			connection.connect(new InetSocketAddress(host, 9876), 1200);
			connection.setKeepAlive(true);
			oos = new ObjectOutputStream(connection.getOutputStream());
			ois = new ObjectInputStream(connection.getInputStream());
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	// M�todo chamado atrav�s do m�todo main - Serve para a prepara��o da Janela
	@Override
	public void start(Stage stage) {
		rootStage = stage;
		userLogged = false;
		recieveObject = new RecieveObjectThread();
		connectionStatusEvent(this.connectionStatus); // Ir� inserir um evento no connectionStatus
		rootStage.setOnCloseRequest((event) -> closeApp());
		openLogonScreen(); // Abertura da tela de logon
	}

	// Retorna o Stage principal da aplica��o(Janela)
	public Stage getStage() {
		return rootStage;
	}

	// M�todo que ir� ser executado quando o usu�rio clicar no bot�o fechar da janela
	public void closeApp() {
		logoff();
		rootStage.close();
		Platform.exit();
		System.exit(0);
	}

	// M�todo que ir� executar as a��es de recebimento de alguma Request atrav�s da Thread RecieveObject
	public void recieveObject(Request request) {
		if(loginController != null && !userLogged) {
			new Thread(() -> {
				Platform.runLater(() -> {
					loginController.recieveObject(request);
				});
			}).start();
		} else if(principalController != null && userLogged) {
			new Thread(() -> {
				Platform.runLater(() -> {
					if(!isMessageWindowRequest(request));
					principalController.recieveObject(request);
				});
			}).start();
		}
	}

	// M�todo que ir� abrir a janela de logon
	public void openLogonScreen() {
		if(principalController == null) {
			loginController = new LoginController(this);

		} else {
			principalController.close();
			principalController = null;
			loginController = new LoginController(this);
		}
		rootStage.setResizable(false);
		rootStage.initStyle(StageStyle.UNDECORATED);
		rootStage.show();
	}

	// M�todo que ir� abrir a janela principal
	public void openPrincipalScreen(String nickname) {
		if(loginController == null) {
			principalController = new PrincipalController(this, nickname);
		} else {
			loginController = null;
			principalController = new PrincipalController(this, nickname);
		}
		this.nickname = nickname;
		userLogged = true;
	}

	// M�todo que ir� abrir uma nova janela caso receba uma mensagem
	public void openMessageScreen(String loginRecipient) {
		if(chatWindowsUsers.add(loginRecipient)) {
			messageWindows.add(new MessageController(loginRecipient, this));
		} else {
			for(MessageController msgWindow : messageWindows) {
				if(msgWindow.getRecipient().equals(loginRecipient)) {
					msgWindow.showWindow();
					break;
				}
			}
		}
	}

	// Método que fecha uma janela de mensagem ativa
	public void closeChatWindow(String loginRecipient) {
		if(chatWindowsUsers.remove(loginRecipient)) {
			messageWindows.removeIf((t) -> {
				if(t.getRecipient().equals(loginRecipient)) {
					t.getStage().close();
					return true;
				} else {
					return false;
				}
			});
		}
	}

	public void offlineUser(String loginRecipient) {
		for(MessageController window : messageWindows) {
			if(window.getRecipient().equals(loginRecipient))
				window.recipientDisconnected();
		}
	}

	// M�todo que ir� tratar os requests para as janelas de mensagens
	public boolean isMessageWindowRequest(Request msg) {
		if(msg.getOperation() == OperationType.SEND_OR_RECIEVE_MSG && !msg.getUserFrom().equals("Server") && msg.getUserTo() != null) {
			boolean found = false;
			for(MessageController msgWindow : messageWindows) {
				if(msgWindow.getRecipient().equals(msg.getUserFrom())) {
					msgWindow.recieveObject(msg);
					found = true;
					break;
				}
			}
			if(!found) {
				openMessageScreen(msg.getUserFrom());
				isMessageWindowRequest(msg);
			}
			return true;
		} else if(msg.getOperation() == OperationType.SUCCESS_MSG || msg.getOperation() == OperationType.ERROR_MSG && !msg.getUserFrom().equals("Server")){
			for(MessageController msgWindow : messageWindows) {
				if(msgWindow.getRecipient().equals(msg.getUserFrom())) {
					msgWindow.recieveObject(msg);
					break;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	// M�todo que ir� inicializar as Threads
	public void initializeThreads() {
		recieveObjectT = new Thread(recieveObject);
		recieveObjectT.start();
	}

	public void finalizeConnection() {
		if(connection != null) {
			if(!connection.isClosed())
				try {
					connection.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		connection = null;
	}

	// M�todo de evento do BooleanProperty connectionStatus
	public void connectionStatusEvent(BooleanProperty connectionStatus) {
		connectionStatus.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if(newValue && oldValue != newValue) {
				reconnectionAction();
			}
			else if(!newValue && oldValue != newValue) {
				lostConnectionAction();
			}
		});
	}

	// M�todo que ir� executar os procedimentos de perda de conex�o
	public void lostConnectionAction() {
		if(loginController != null && !userLogged) {
			finalizeConnection();
			Platform.runLater(() -> loginController.lostConnection());
		} else {
			finalizeConnection();
			Platform.runLater(() -> principalController.lostConnection());
			userLogged = false;
		}
	}

	public boolean reconnect(String nickname) {
		Request loginRequest = new Request(OperationType.LOGIN);
		loginRequest.setUserFrom(nickname);
		loginRequest.setUserTo("Server");
		try {
			if(connection != null && oos != null) {
				if(!connection.isClosed() && connection.isConnected()) {
					oos.writeObject(loginRequest);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch(Exception e) {
			return false;
		}
	}

	// M�todo que ir� executar os procedimentos de reconex�o
	public void reconnectionAction() {
		if(loginController != null) {
			Platform.runLater(() -> loginController.reconnect());
		} else {
			Platform.runLater(() -> principalController.reconnect());
			userLogged = true;
		}
	}

	// M�todo para deslogar o usu�rio e fechar a conex�o com o servidor
	public void logoff() {
		try {
			if(connection != null) {
				if(!connection.isClosed()) {

					if(connection.isConnected()) {
						Request requestLogoff = new Request(OperationType.LOGOFF);
						requestLogoff.setUserTo("Server");

						if(loginController != null) {
							requestLogoff.setUserFrom(null);
						} else {
							requestLogoff.setUserFrom(principalController.getNickname());
						}

						oos.writeObject(requestLogoff);
						Thread.sleep(1000);
					}
					recieveObjectT.interrupt();
					connection.close();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/*---------- Threads ----------*/

	/*
	 * Thread que verifica o recebimento de objetos do servidor a cada 100ms.
	 * Caso receba um objeto Request do servidor, ele executa o m�todo recieveObject(request).
	 */
	private class RecieveObjectThread implements Runnable {

		@Override
		public void run() {
			new Thread(() -> {

				while(true) {
					if(connection != null) {

						if(!connection.isConnected()) {

							if(!(connectionStatus.getValue() == false))
								connectionStatus.set(false);

						}

					}

					try {
						Thread.sleep(500);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}

				}

			});

			boolean down = false;
			while(true) {

				if(!down) {

					try {
						Object obj = ois.readObject();

						if(obj != null) {

							if(obj instanceof Request) {
								Request request = (Request) obj;

								if(loginController != null || !userLogged) {

									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

								}

								recieveObject(request);
							}

						} else {
							throw new IOException("Lost connection");
						}

					} catch(IOException e) {
						if(!(connectionStatus.get() == false))
							connectionStatus.setValue(false);
						down = true;

					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

				} else {

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					if(setConnection(host)) {
						if(nickname != null) {
							if(reconnect(nickname)) {
								connectionStatus.setValue(true);
								down = false;
							}
						}
						down = false;

					} else {
						if(!(connectionStatus.getValue() == false))
							connectionStatus.setValue(false);
					}

				}
			}
		}
	}
}
