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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.requests.OperationType;
import model.requests.Request;

@SuppressWarnings("unused")
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
	private static TestConnectionThread testConnection;
	private static RecieveObjectThread recieveObject;
	private LoginController loginController;
	private PrincipalController principalController;
	private ArrayList<MessageController> messageWindows = new ArrayList<>();
	private ObservableList<String> chatWindowsUsers = FXCollections.observableArrayList();
	private BooleanProperty connectionStatus = new SimpleBooleanProperty(true);
	private boolean userLogged;
	private String nickname;
	private Stage rootStage;


	// M�todo main - Inicial
	public static void main(String[] args) {
		launch(args);
	}

	// M�todo que retorna o Socket da conex�o
	public static Socket getConnection() {
		return connection;
	}

	// M�todo que retorna o Socket da conex�o
	public boolean setConnection(String host) {
		connection = new Socket();
		try {
			connection.connect(new InetSocketAddress(host, 9876), 1500);
			testConnection = new TestConnectionThread();
			recieveObject = new RecieveObjectThread();
			new Thread(testConnection).start();
			new Thread(recieveObject).start();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	// M�todo chamado atrav�s do m�todo main - Serve para a prepara��o da Janela
	@Override
	public void start(Stage stage) {
		rootStage = stage;
		userLogged = false;
		chatWindowsUsersEvent(this.chatWindowsUsers); // Ir� inserir um evento na lista chatWindowsUsers
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
		try {
			this.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// M�todo que ir� executar as a��es de recebimento de alguma Request atrav�s da Thread RecieveObject
	public void recieveObject(Request request) {
		if(loginController != null) {
			loginController.recieveObject(request);
		} else if(principalController != null) {
			principalController.recieveObject(request);
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
			loginController.close();
			loginController = null;
			principalController = new PrincipalController(this, nickname);
		}
		rootStage.initStyle(StageStyle.UNDECORATED);
		rootStage.show();
	}

	// M�todo que ir� abrir uma nova janela caso receba uma mensagem
	public void openMessageScreen(String loginRecipient) {
		chatWindowsUsers.add(loginRecipient);
	}

	// M�todo que ir� inicializar as Threads
	public void initializeThreads() {
		testConnection = new TestConnectionThread();
		recieveObject = new RecieveObjectThread();
		new Thread(testConnection);
		new Thread(recieveObject);
	}

	// M�todo de evento do ObservableList chatWindowsUsers
	public void chatWindowsUsersEvent(ObservableList<String> list) {

		list.addListener((ListChangeListener<String>) (c) -> {
			while(c.next()) {
				if(c.wasAdded()) {
					for(String user : c.getAddedSubList()) {
						messageWindows.add(new MessageController(user));
					}
				} else if(c.wasRemoved()) {
					for(String user : c.getRemoved()) {
						messageWindows.removeIf((t) -> {
							if(t.getRecipient().equals("user")) {
								return true;
							} else {
								return false;
							}
						});
					}
				}
			}
		});

	}

	// M�todo de evento do BooleanProperty connectionStatus
	public void connectionStatusEvent(BooleanProperty connectionStatus) {
		connectionStatus.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if(newValue && oldValue != newValue)
				reconnectionAction();
			else if(!newValue && oldValue != newValue)
				lostConnectionAction();
		});
	}

	// M�todo que ir� executar os procedimentos de perda de conex�o
	public void lostConnectionAction() {
		if(loginController != null) {
			loginController.lostConnection();
		} else {
			principalController.lostConnection();
			userLogged = false;
		}
	}

	// M�todo que ir� executar os procedimentos de reconex�o
	public void reconnectionAction() {
		if(loginController != null) {
			loginController.reconnect();
		} else {
			principalController.reconnect();
			userLogged = true;
		}
	}

	// M�todo para deslogar o usu�rio e fechar a conex�o com o servidor
	public void logoff() {
		try {
			if(!connection.isClosed()) {
				ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
				oos.writeObject(new Request(OperationType.LOGOFF));
				connection.close();
			}
			if(testConnection != null) testConnection.closeThread();
			testConnection = null;
			if(recieveObject != null) recieveObject.closeThread();
			recieveObject = null;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/*---------- Threads ----------*/

	/*
	 * Thread para testar a conex�o com o servidor a cada 100ms.
	 * Caso a conex�o falhe, � setado como false o atributo connectionStatus.
	 * E executa o m�todo lostConnectionAction().
	 */
	private class TestConnectionThread implements Runnable {

		@Override
		public void run() {
			try {
				while(true){
					if(connection.isConnected())
						connectionStatus.setValue(true);
					else
						connectionStatus.setValue(false);
					Thread.sleep(100);
				}
			} catch(InterruptedException e) {
				Platform.runLater(new Runnable(){

					@Override
					public void run() {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setContentText("A FATAL ERROR has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
						alert.setHeaderText("ERROR:");
						alert.setTitle("APPLICATION ERROR");
						alert.setResizable(false);
						alert.show();

					}

				});
			}
		}

		public void closeThread() throws Throwable {
			this.finalize();
			Thread.currentThread().interrupt();
		}
	}

	/*
	 * Thread que verifica o recebimento de objetos do servidor a cada 100ms.
	 * Caso receba um objeto Request do servidor, ele executa o m�todo recieveObject(request).
	 */
	private class RecieveObjectThread implements Runnable {

		@Override
		public void run() {
			try {

				while(true) {
					if(connection != null) {

						try(ObjectInputStream ois = new ObjectInputStream(connection.getInputStream())) {
							while(true) {
								if(ois.read() > -1) {
									recieveObject((Request) ois.readObject());
								}
								Thread.sleep(100);
							}
						} catch(Exception e) {}
					}
					Thread.sleep(100);
				}

			} catch(InterruptedException e) {
				Platform.runLater(new Runnable(){

					@Override
					public void run() {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setContentText("A FATAL ERROR has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
						alert.setHeaderText("ERROR:");
						alert.setTitle("APPLICATION ERROR");
						alert.setResizable(false);
						alert.show();

					}

				});
			}
		}

		public void closeThread() throws Throwable {
			this.finalize();
			Thread.currentThread().interrupt();
		}
	}

}
