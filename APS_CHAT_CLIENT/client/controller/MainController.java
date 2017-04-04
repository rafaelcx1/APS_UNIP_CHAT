package controller;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import controller.controllers.LoginController;
import controller.controllers.MessageController;
import controller.controllers.PrincipalController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.requests.MessageRequest;
import model.requests.Request;

@SuppressWarnings("unused")
public class MainController extends Application {

	/* connection = Atributo de conex�o com o servidor
	 * testConnection = Inst�ncia da Thread que testa a conex�o com o servidor
	 * recieveObject = Inst�ncia da Thread que recebe os objetos do servidor
	 * loginController = Inst�ncia da tela de login
	 * principalController = Inst�ncia da tela Principal
	 * messageWindows = Lista com as janelas de mensagem
	 * chatWindowsUsers = Lista com listener com os usu�rios de cada janela
	 * connectionStatus = Atributo com o status da conex�o, caso a conex�o falhe, este atributo ser� setado como false
	 * userLogged = Atributo para indicar se h� um usu�rio logado
	 * nickname = Atributo para armazenar o nickname do usu�rio
	 * rootStage = Stage(Janela) principal
	 */
	private static Socket connection;
	private TestConnectionThread testConnection;
	private RecieveObjectThread recieveObject;
	private LoginController loginController;
	private PrincipalController principalController;
	private List<MessageController> messageWindows = new ArrayList<>();
	private ObservableList<String> chatWindowsUsers = FXCollections.observableArrayList();
	private boolean connectionStatus;
	private boolean userLogged;
	private String nickname;
	private Stage rootStage;

	// M�todo que retorna o Socket da conex�o
	public static Socket getConnection() {
		return connection;
	}

	// M�todo main - Inicial
	public static void main(String[] args) {
		launch(args);
	}


	// M�todo chamado atrav�s do m�todo main - Serve para a prepara��o da Janela
	@Override
	public void start(Stage stage) {

	}

	// Retorna o Stage principal da aplica��o(Janela)
	public Stage getStage() {
		return rootStage;
	}

	// M�todo que ir� ser executado quando o usu�rio clicar no bot�o fechar da janela
	public void close() {

	}

	// M�todo que ir� executar as a��es de recebimento de alguma Request atrav�s da Thread RecieveObject
	public void recieveObject(Request request) {

	}

	// M�todo que ir� abrir a janela de logon
	public void openLogonScreen() {

	}

	// M�todo que ir� abrir a janela principal
	public void openPrincipalScreen(String nickname) {

	}

	// M�todo que ir� abrir uma nova janela caso receba uma mensagem
	public void openMessageScreen(MessageRequest msg) {

	}

	// M�todo de evento do ObservableList chatWindowsUsers
	public void chatWindowsUsersEvent(ObservableList<String> list) {

	}

	// M�todo que ir� executar os procedimentos de perda de conex�o
	public void lostConnectionAction() {

	}

	// M�todo que ir� executar os procedimentos de reconex�o
	public void reconnectionAction() {

	}

	// M�todo para deslogar o usu�rio e fechar a conex�o com o servidor
	public void logoff() {

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
				Thread.sleep(100);
			} catch(InterruptedException e) {

			}
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
				Thread.sleep(100);
			} catch(InterruptedException e) {

			}
		}

	}


}
