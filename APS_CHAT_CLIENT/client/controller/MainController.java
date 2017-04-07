package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import controller.controllers.LoginController;
import controller.controllers.MessageController;
import controller.controllers.PrincipalController;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.requests.MessageRequest;
import model.requests.Request;

@SuppressWarnings("unused")
public class MainController extends Application {

	/* connection = Atributo de conexão com o servidor
	 * testConnection = Instância da Thread que testa a conexão com o servidor
	 * recieveObject = Instância da Thread que recebe os objetos do servidor
	 * loginController = Instância da tela de login
	 * principalController = Instância da tela Principal
	 * messageWindows = Lista com as janelas de mensagem
	 * chatWindowsUsers = Lista com listener com os usuários de cada janela
	 * connectionStatus = Atributo com o status da conexão, caso a conexão falhe, este atributo será setado como false
	 * userLogged = Atributo para indicar se há um usuário logado
	 * nickname = Atributo para armazenar o nickname do usuário
	 * rootStage = Stage(Janela) principal
	 */
	private static Socket connection;
	private TestConnectionThread testConnection;
	private RecieveObjectThread recieveObject;
	private LoginController loginController;
	private PrincipalController principalController;
	private ArrayList<MessageController> messageWindows = new ArrayList<>();
	private ObservableList<String> chatWindowsUsers = FXCollections.observableArrayList();
	private BooleanProperty connectionStatus = new SimpleBooleanProperty(false);
	private boolean userLogged;
	private String nickname;
	private Stage rootStage;

	// Método que retorna o Socket da conexão
	public static Socket getConnection() {
		return connection;
	}

	// Método main - Inicial
	public static void main(String[] args) {
		launch(args);
	}

	// Método chamado através do método main - Serve para a preparação da Janela
	@Override
	public void start(Stage stage) {
		chatWindowsUsersEvent(this.chatWindowsUsers); // Irá inserir um evento na lista chatWindowsUsers
		connectionStatusEvent(this.connectionStatus); // Irá inserir um evento no connectionStatus
		openLogonScreen(); // Abertura da tela de logon
	}

	// Retorna o Stage principal da aplicação(Janela)
	public Stage getStage() {
		return rootStage;
	}

	// Método que irá ser executado quando o usuário clicar no botão fechar da janela
	public void close() {

	}

	// Método que irá executar as ações de recebimento de alguma Request através da Thread RecieveObject
	public void recieveObject(Request request) {
		if(logincontroller != null) {
			loginController.receiveObject(request);
		} else if(principalController != null) {
			principalController.receiveObject(request);
		}
	
	// Método que irá abrir a janela de logon
	public void openLogonScreen() {
		if(principalController == null) {
			loginController = new LoginController(this);
		} else {
			principalController.close();
			principalController = null;
			loginController = new LoginController(this);
		}
	}
	
	// Método que irá abrir a janela principal
	public void openPrincipalScreen(String nickname) {
		if(loginController == null) {
			principalController = new PrincipalController(this);
		} else {
			loginController.close();
			loginController = null;
			principalController = new PrincipalController(this);
		}
	}
		
	// Método que irá abrir uma nova janela caso receba uma mensagem
	public void openMessageScreen(String loginRecipient) {
		chatWindowsUsers.add(loginRecipient);
	}

	// Método que irá inicializar as Threads
	public void initializeThreads() {
		testConnection = new TestConnectionThread();
		recieveObject = new RecieveObjectThread();
		new Thread(testConnection);
		new Thread(recieveObject);
	}

	// Método de evento do ObservableList chatWindowsUsers
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
							if(t.getLoginRecipient().equals("user")) {
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

	// Método de evento do BooleanProperty connectionStatus
	public void connectionStatusEvent(BooleanProperty connectionStatus) {
		connectionStatus.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if(newValue && oldValue != newValue)
				reconnectionAction();
			else
				lostConnectionAction();
		});
	}

	// Método que irá executar os procedimentos de perda de conexão
	public void lostConnectionAction() {
		if(loginController != null) {
			loginController.lostConnection();
		} else {
			principalController.lostConnection();
			loggedStatus = false;
		}
	}

	// Método que irá executar os procedimentos de reconexão
	public void reconnectionAction() {
		if(loginController != null) {
			loginController.reconnect();
		} else {
			principalController.reconnect();
			loggedStatus = true;
		}
	}

	// Método para deslogar o usuário e fechar a conexão com o servidor
	public void logoff() {
		if(connection != null) {
			connection.close();
		}
	}

	/*---------- Threads ----------*/

	/*
	 * Thread para testar a conexão com o servidor a cada 100ms.
	 * Caso a conexão falhe, é setado como false o atributo connectionStatus.
	 * E executa o método lostConnectionAction().
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
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An InterruptedException has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
				alert.setHeaderText("InterruptedException:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.show();
			}
		}

	}

	/*
	 * Thread que verifica o recebimento de objetos do servidor a cada 100ms.
	 * Caso receba um objeto Request do servidor, ele executa o método recieveObject(request).
	 */
	private class RecieveObjectThread implements Runnable {

		@Override
		public void run() {
			try {
				while(true) {
					if(connection != null) {
						ObjectInputStream ois = (ObjectInputStream) connection.getInputStream();
						while(true) {
							if(ois.read() > -1) {
								recieveObject((Request) ois.readObject());
							}
							Thread.sleep(100);
						}
					}
					Thread.sleep(100);
				}
			} catch(InterruptedException | ClassNotFoundException | IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("A FATAL ERROR has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
				alert.setHeaderText("ERROR:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.show();
			}
		}

	}


}
