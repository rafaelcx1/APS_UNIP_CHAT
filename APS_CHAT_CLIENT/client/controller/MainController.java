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
	private List<MessageController> messageWindows = new ArrayList<>();
	private ObservableList<String> chatWindowsUsers = FXCollections.observableArrayList();
	private boolean connectionStatus;
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

	}

	// Método que irá abrir a janela de logon
	public void openLogonScreen() {

	}

	// Método que irá abrir a janela principal
	public void openPrincipalScreen(String nickname) {

	}

	// Método que irá abrir uma nova janela caso receba uma mensagem
	public void openMessageScreen(MessageRequest msg) {

	}

	// Método de evento do ObservableList chatWindowsUsers
	public void chatWindowsUsersEvent(ObservableList<String> list) {

	}

	// Método que irá executar os procedimentos de perda de conexão
	public void lostConnectionAction() {

	}

	// Método que irá executar os procedimentos de reconexão
	public void reconnectionAction() {

	}

	// Método para deslogar o usuário e fechar a conexão com o servidor
	public void logoff() {

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
				Thread.sleep(100);
			} catch(InterruptedException e) {

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
				Thread.sleep(100);
			} catch(InterruptedException e) {

			}
		}

	}


}
