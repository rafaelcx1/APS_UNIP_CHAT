package controller.controllers;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.Socket;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import model.LoginModel;
import model.requests.Request;


public class LoginController {

	/*
	 * Atributos com anotaÁ„o @FXML = Elementos de interface
	 * mainController = Inst√¢ncia do mainController
	 * loginModel = Inst√¢ncia do loginModel para fazer o envio e tratamento de Requests
	 * loginScreen = booleano para indicar se a tela est· em config do servidor(false) ou do nickname(true)
	 * */

	@FXML
	private BorderPane pane;
	@FXML
	private FlowLayout statusBar;
	@FXML
	private ImageView logo;
	@FXML
	private Label lbServer;
	@FXML
	private Label lbLogin;
	@FXML
	private TextField tfServer;
	@FXML
	private TextField tfLogin;
	@FXML
	private Button btnBack;
	@FXML
	private Button btnNext;
	@FXML
	private Label lblStatus;

	private MainController mainController;
	private LoginModel loginModel;
	private boolean loginScreen;

	public LoginController(MainController mainController) {
		this.mainController = mainController;
		loginModel = new LoginModel();

		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../../view/LoginServerView.fxml"));
			loader.setController(this);
			BorderPane scene = loader.load();
			mainController.getStage().setScene(new Scene(scene));
			loginScreen = false;
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An IOException has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
			alert.setHeaderText("IOException:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			alert.show();

			e.printStackTrace();
		}

	}

	// MÈtodo padr„o do FXML que È chamado ao carregar os elementos
	public void initialize() {}

	// MÈtodo do bot„o btnNext, ele ir· logar se o loginScreen for true, ou passar para a tela de login se esta vari·vel for false
	public void btnNextEvent(ActionEvent event) {
		if(loginScreen) {
			if(!loginModel.login(tfLogin.getText())) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An error has been occurred.\nDetails: " + loginModel.getErrorMessage());
				alert.setHeaderText("LOGIN ERROR:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.show();
			}
		} else {
			try {
				MainController.setConnection(new Socket(tfServer.getText(), 9876));

				FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../../view/LoginView.fxml"));
				loader.setController(this);
				BorderPane scene = loader.load();
				mainController.getStage().setScene(new Scene(scene));
				loginScreen = true;
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An IOException has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
				alert.setHeaderText("IOException:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.show();

				e.printStackTrace();
			}

		}
	}

	// MÈtodo do bot„o btnExit, ir· sair se o loginScreen for false, ou voltar caso seja true
	public void btnExitEvent(ActionEvent event) {
		if(loginScreen) {
			try {
				MainController.getConnection().close();
				MainController.setConnection(null);

				FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../../view/LoginServerView.fxml"));
				loader.setController(this);
				BorderPane scene = loader.load();
				mainController.getStage().setScene(new Scene(scene));
				loginScreen = false;
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An IOException has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
				alert.setHeaderText("IOException:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.show();

				e.printStackTrace();
			}
		} else {
			close();
		}

	}

	// MÈtodo ir· passar o request para o login model
	public void recieveObject(Request request) {
		if(loginModel.loginObjectRecieve(request)) {
			mainController.openPrincipalScreen(tfLogin.getText());
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An error has been occurred.\nDetails: " + loginModel.getErrorMessage());
			alert.setHeaderText("LOGIN ERROR:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			alert.show();
		}
	}

	// M√©todo que ir√° executar a√ß√µes quando a conex√£o com o servidor cair
	public void lostConnection() {
		lblStatus.setText("STATUS: Sem conex„o com o servidor.");
		tfLogin.setDisable(true);
		btnNext.setDisable(true);
	}

	// M√©todo que ir√° executar a√ß√µes quando a conex√£o com o servidor voltar
	public void reconnect() {
		lblStatus.setText("STATUS: Conectado.");
		tfLogin.setDisable(false);
		btnNext.setDisable(false);
	}

	// MÈtodo que ir· fechar a janela
	public void close() {
		mainController.close();
	}

}
