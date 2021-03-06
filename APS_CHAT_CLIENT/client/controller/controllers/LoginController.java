package controller.controllers;
import java.io.IOException;
import java.util.Optional;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.LoginModel;
import model.requests.Request;


public class LoginController {

	@FXML
	private BorderPane pane;
	@FXML
	private AnchorPane paneTop;
	@FXML
	private AnchorPane paneCenter;
	@FXML
	private GridPane gridCenter;
	@FXML
	private GridPane gridBottom;
	@FXML
	private ImageView imgBtnNext;
	@FXML
	private ImageView imgBtnBack;
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


	private MainController mainController;
	private LoginModel loginModel;
	private boolean loginScreen;
	private Alert loginStatus;
	private Alert lostConnectionAlert;

	private double xOffset;
	private double yOffset;

	public LoginController(MainController mainController) {
		this.mainController = mainController;
		loginModel = new LoginModel();

		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../../view/LoginServerView.fxml"));
			loader.setController(this);
			mainController.getStage().setScene(new Scene(loader.load()));
			mainController.getStage().getIcons().setAll(new Image(this.getClass().getResourceAsStream("../../view/icon.png")));
			loginScreen = false;
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An IOException has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
			alert.setHeaderText("IOException:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("../../view/error-icon.png")));
			alert.show();

			e.printStackTrace();
		}

	}

	public void moveWindowOnMousePressed(MouseEvent event) {
		xOffset = event.getSceneX();
		yOffset = event.getSceneY();
	}

	public void moveWindowOnMouseDrag(MouseEvent event) {
		mainController.getStage().setX(event.getScreenX() - xOffset);
		mainController.getStage().setY(event.getScreenY() - yOffset);
	}

	public void enterPressed(KeyEvent event) {
		if(event.getCode().equals(KeyCode.ENTER)) {
			btnNext.fire();
		}
	}

	public void btnNextEvent(ActionEvent event) {
		if(loginScreen) {
			if(tfLogin.getText().length() > 0) {
				if(loginModel.login(tfLogin.getText())) {
					loginStatus = new Alert(AlertType.INFORMATION);
					loginStatus.setContentText("Autenticando...");
					loginStatus.setHeaderText("Login:");
					loginStatus.setTitle("Aguarde");
					loginStatus.setResizable(false);
					loginStatus.getButtonTypes().setAll(new ButtonType[] {});
					((Stage) loginStatus.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("../../view/waiting-icon.png")));
					loginStatus.show();
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("An error has been occurred.\nDetails: " + loginModel.getErrorMessage());
					alert.setHeaderText("ERROR:");
					alert.setTitle("APPLICATION ERROR");
					alert.setResizable(false);
					((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("../../view/error-icon.png")));
					alert.show();
					btnBack.setDisable(false);
					btnNext.setDisable(false);
				}
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Insira um Nickname");
				alert.setHeaderText("ERROR:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("../../view/error-icon.png")));
				alert.show();
			}
		} else {
			try {
				if(tfServer.getText().matches("\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}") || tfServer.getText().matches("[a-z]*")) {
					if(mainController.setConnection(tfServer.getText())) {

						FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../../view/LoginView.fxml"));
						loader.setController(this);
						mainController.getStage().setScene(new Scene(loader.load()));
						mainController.initializeThreads();
						loginScreen = true;
					} else {
						throw new IOException("Sem conexão com o servidor");
					}
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Endereço de IP inválido.");
					alert.setHeaderText("Ocorreu um erro:");
					alert.setTitle("ERRO");
					alert.setResizable(false);
					((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("../../view/error-icon.png")));
					alert.show();
				}
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An IOException has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
				alert.setHeaderText("IOException:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("../../view/error-icon.png")));
				alert.show();
			}
		}
	}

	public void btnBackEvent(ActionEvent event) {
		if(loginScreen) {
			try {
				mainController.logoff();

				FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../../view/LoginServerView.fxml"));
				loader.setController(this);
				mainController.getStage().setScene(new Scene(loader.load()));
				loginScreen = false;
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An IOException has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
				alert.setHeaderText("IOException:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("../../view/error-icon.png")));
				alert.show();

				e.printStackTrace();
			}
		} else {
			close();
		}

	}

	public void recieveObject(Request request) {
		if(loginModel.loginObjectRecieve(request)) {
			if(loginStatus != null) {
				loginStatus.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
				loginStatus.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
				loginStatus.close();
			}
			mainController.openPrincipalScreen(tfLogin.getText());
		} else {
			if(loginStatus != null) {
				loginStatus.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
				loginStatus.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
				loginStatus.close();
				loginStatus = null;
				loginStatus = new Alert(AlertType.ERROR);
				loginStatus.setContentText("An error has been occurred.\nDetails: " + loginModel.getErrorMessage());
				loginStatus.setHeaderText("Login:");
				loginStatus.setTitle("ERROR");
				loginStatus.setResizable(false);
				((Stage) loginStatus.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("../../view/error-icon.png")));
				loginStatus.show();
			} else {
				loginStatus = new Alert(AlertType.ERROR);
				loginStatus.setContentText("An error has been occurred.\nDetails: " + loginModel.getErrorMessage());
				loginStatus.setHeaderText("Login:");
				loginStatus.setTitle("ERROR");
				loginStatus.setResizable(false);
				((Stage) loginStatus.getDialogPane().getScene().getWindow()).getIcons().add(new Image(this.getClass().getResourceAsStream("../../view/error-icon.png")));
				loginStatus.show();
			}
		}
	}

	public void lostConnection() {
		lostConnectionAlert = new Alert(AlertType.ERROR);
		lostConnectionAlert.setTitle("Lost Connection");
		lostConnectionAlert.setHeaderText("Erro:");
		lostConnectionAlert.setContentText("Foi perdido a conexão com o servidor. Clique no botão 'OK' abaixo para voltar para a tela inicial ou aguarde.\nAguardando conexão...");
		Optional<ButtonType> result = lostConnectionAlert.showAndWait();
		if(result.isPresent()) {
			if(loginStatus != null) {
				// A��es para tornar poss�vel o fechamento da janela
				loginStatus.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
				loginStatus.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
				loginStatus.close();
			}
			btnBack.fire();
		}
	}

	public void reconnect() {
		lostConnectionAlert.getButtonTypes().setAll(new ButtonType[] {});
		lostConnectionAlert.close();
	}

	public void close() {
		mainController.closeApp();
	}

}