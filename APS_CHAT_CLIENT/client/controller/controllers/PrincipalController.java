package controller.controllers;

import java.io.IOException;
import java.time.LocalTime;

import controller.MainController;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.PrincipalModel;
import model.requests.InfoRequest;
import model.requests.InfoReturn;
import model.requests.InfoUserModel;
import model.requests.MessageRequest;
import model.requests.OperationType;
import model.requests.Request;

public class PrincipalController {

	@FXML
	private VBox vbUsersListPane;
	@FXML
	private VBox vbGlobalChatPane;
	@FXML
	private Label lblUserNickname;
	@FXML
	private Label lblStatus;
	@FXML
	private Button btnLogoffReconnect;
	@FXML
	private Button btnHelpEmoction;
	@FXML
	private Button btnSendMsg;
	@FXML
	private TextField tfMsgBox;
	@FXML
	private HBox hboxTittle;

	private MainController mainController;
	private PrincipalModel principalModel;
	private Alert statusLogon;

	private double xOffset;
	private double yOffset;


	public PrincipalController(MainController mainController, String nickname) {
		this.mainController = mainController;
		mainController.getStage().setResizable(true);
		principalModel = new PrincipalModel(nickname);
		globalChatMsgEvent(principalModel.getGlobalChatMsg());
		usersListEvent(principalModel.getUsers());

		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../../view/PrincipalView.fxml"));
			loader.setController(this);
			mainController.getStage().setScene(new Scene(loader.load()));
		} catch(IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An IOException has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
			alert.setHeaderText("IOException:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			alert.show();

			e.printStackTrace();
		}

		Request request = new Request(OperationType.INFO);
		request.setUserFrom(nickname);
		request.setUserTo("Server");
		if(principalModel.sendObject(request)) {
			statusLogon = new Alert(AlertType.INFORMATION);
			statusLogon.setTitle("Logon");
			statusLogon.setHeaderText("Buscando dados do servidor...");
			statusLogon.setContentText("Aguarde...");
			statusLogon.setResizable(false);
			statusLogon.getButtonTypes().setAll(new ButtonType[] {});
			statusLogon.show();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An error has been occurred.\nDetails: " + principalModel.getErrorMessage());
			alert.setHeaderText("ERROR:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			alert.show();
		}

	}

	public void recieveObject(Request request) {
		if(request.getOperation() == OperationType.INFO) {

			if(principalModel.treatObject((InfoRequest) request)) {

				if(statusLogon != null) {
					// A��es para tornar poss�vel o fechamento da janela de status
					statusLogon.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
					statusLogon.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
					statusLogon.close();

					tfMsgBox.setDisable(false);
					lblStatus.setText("Status: Conectado");
					statusLogon = null;
				}

			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An error has been occurred.\nDetails: " + principalModel.getErrorMessage());
				alert.setHeaderText("ERROR:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.show();
			}

		} else if(request.getOperation() == OperationType.SEND_OR_RECIEVE_MSG) {

			if(request.getUserTo() == null) {
				if(!principalModel.treatObject((MessageRequest) request)) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("An error has been occurred.\nDetails: " + principalModel.getErrorMessage());
					alert.setHeaderText("ERROR:");
					alert.setTitle("APPLICATION ERROR");
					alert.setResizable(false);
					alert.show();
				}
			}

		} else if(request.getOperation() == OperationType.SUCCESS_MSG || request.getOperation() == OperationType.ERROR_MSG) {

			if(((InfoReturn)request).getOperationSource() == OperationType.LOGIN && request.getOperation() == OperationType.SUCCESS_MSG) {
				Request requestInfo = new Request(OperationType.INFO);
				request.setUserFrom(principalModel.getNickname());
				request.setUserTo("Server");
				if(principalModel.sendObject(requestInfo)) {
					statusLogon = new Alert(AlertType.INFORMATION);
					statusLogon.setTitle("Logon");
					statusLogon.setHeaderText("Buscando dados do servidor...");
					statusLogon.setContentText("Aguarde...");
					statusLogon.setResizable(false);
					statusLogon.getButtonTypes().setAll(new ButtonType[] {});
					statusLogon.show();
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("An error has been occurred.\nDetails: " + principalModel.getErrorMessage());
					alert.setHeaderText("ERROR:");
					alert.setTitle("APPLICATION ERROR");
					alert.setResizable(false);
					alert.show();
				}
			} else if(request.getOperation() == OperationType.ERROR_MSG) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An error has been occurred.\nDetails: " + ((InfoReturn)request).getMessage());
				alert.setHeaderText("ERROR:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.showAndWait();
				this.close();
			}

		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An error has been occurred.\nDetails: Invalid Object");
			alert.setHeaderText("ERROR:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			alert.show();
		}
	}

	public String getNickname() {
		return principalModel.getNickname();
	}

	// M�todo de evento quando o mouse � pressionado no paneTop
	public void moveWindowOnMousePressed(MouseEvent event) {
		xOffset = event.getSceneX();
		yOffset = event.getSceneY();
	}

	// M�todo de evento quando o mouse � arrastado no paneTop
	public void moveWindowOnMouseDrag(MouseEvent event) {
		mainController.getStage().setX(event.getScreenX() - xOffset);
		mainController.getStage().setY(event.getScreenY() - yOffset);
	}


	public void lostConnection() {
		tfMsgBox.setDisable(true);
		lblStatus.setText("Sem conexão com o servidor.");
		btnLogoffReconnect.setText("Reconectar");
	}

	public void reconnect() {
		lblStatus.setText("Conexão feita. Clique no botão 'Reconectar' para relogar no servidor.");
	}

	public void tfMsgBoxEnterPressed(KeyEvent action) {
		if(action.getCode() == KeyCode.ENTER) {
			btnSendMsg.fire();
		}
	}

	public void btnLogoffReconnectAction(ActionEvent action) {
		if(btnLogoffReconnect.getText().equals("Reconectar")) {
			Request loginRequest = new Request(OperationType.LOGIN);
	    	loginRequest.setUserFrom(principalModel.getNickname());
	    	loginRequest.setUserTo("Server");

	    	if(!principalModel.sendObject(loginRequest)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An IOException error has been occurred.\nDetails: " + principalModel.getErrorMessage());
				alert.setHeaderText("ERROR:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.show();
			}
		} else {
			this.close();
		}
	}

	public void btnHelpEmoctionAction(ActionEvent action) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText(""); // Emoctions
		alert.setHeaderText("Emoctions:");
		alert.setTitle("Help Emoction Window");
		alert.setResizable(false);
		alert.show();
	}

	public void btnSendMsgAction(ActionEvent action) {
		MessageRequest msgRequest = new MessageRequest();
		msgRequest.setUserFrom(principalModel.getNickname());
		msgRequest.setUserTo(null);
		msgRequest.setMessage(tfMsgBox.getText());
		if(!principalModel.sendObject(msgRequest)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An error has been occurred.\nDetails: " + principalModel.getErrorMessage());
			alert.setHeaderText("ERROR:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			alert.show();
		} else {
			principalModel.getGlobalChatMsg().add(msgRequest);
		}
	}

	public void openMessagePrivateWindow(Event user) {
		mainController.openMessageScreen(((Label)user.getSource()).getText());
	}

	public void usersListEvent(ObservableList<InfoUserModel> users) {
		users.addListener((ListChangeListener<InfoUserModel>) (c) -> {
			while(c.next()) {
				if(c.wasAdded()) {
					for(InfoUserModel user : c.getAddedSubList()) {
						Label lblUser = new Label(user.getLogin());
						lblUser.setOnMouseClicked((event) -> openMessagePrivateWindow(event));
						lblUser.getStyleClass().add("recipient");
						lblUser.getPseudoClassStates().add(PseudoClass.getPseudoClass("hover"));
						lblUser.getPseudoClassStates().add(PseudoClass.getPseudoClass("click"));
						lblUser.getStylesheets().add(this.getClass().getResource("../../view/GlobalMsgStyle.css").toExternalForm());
						vbUsersListPane.getChildren().add(lblUser);
					}
				} else {
					for(InfoUserModel user : c.getRemoved()) {
						for(Node label : vbUsersListPane.getChildren()) {
							if(((Label)label).getText().equals(user.getLogin())) {
								vbUsersListPane.getChildren().remove(label);
							}
						}
					}
				}
			}
		});
	}

	public void globalChatMsgEvent(ObservableList<MessageRequest> globalChat) {
		globalChat.addListener((ListChangeListener<MessageRequest>) (c) -> {
			while(c.next()) {
				if(c.wasAdded()) {
					for(MessageRequest msg : c.getAddedSubList()) {
						Label lblmsg = new Label("<" + LocalTime.now().toString() + ">" + " " + msg.getUserFrom() + ": " + msg.getMessage());
						if(msg.getUserFrom().equals(principalModel.getNickname()))
							lblmsg.getStyleClass().add("messageUser");
						else
							lblmsg.getStyleClass().add("message");
						lblmsg.getStylesheets().add(this.getClass().getResource("../../view/GlobalMsgStyle.css").toExternalForm());
						vbGlobalChatPane.getChildren().add(lblmsg);
					}
				}
			}
		});
	}

	public void close() {
		mainController.closeApp();
	}

}