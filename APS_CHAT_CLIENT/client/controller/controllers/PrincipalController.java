package controller.controllers;

import java.io.IOException;
import java.util.List;

import controller.MainController;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.PrincipalModel;
import model.requests.InfoRequest;
import model.requests.InfoReturn;
import model.requests.InfoUserModel;
import model.requests.MessageRequest;
import model.requests.OperationType;
import model.requests.Request;

@SuppressWarnings("unused")
public class PrincipalController {

	@FXML
	private AnchorPane rootPane;
	@FXML
	private BorderPane usersPane;
	@FXML
	private BorderPane globalChatPane;
	@FXML
	private ScrollPane scrollPaneUsers;
	@FXML
	private ScrollPane scrollPaneChat;
	@FXML
	private VBox vbUsersListPane;
	@FXML
	private VBox vbGlobalChatPane;
	@FXML
	private HBox hbSendMsgBox;
	@FXML
	private FlowPane statusBar;
	@FXML
	private Label lblUsernickname;
	@FXML
	private Label lblTitleUsersPane;
	@FXML
	private Label lblTitleGlobalChatPane;
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

	private MainController mainController;
	private PrincipalModel principalModel;
	private Alert statusLogon;


	public PrincipalController(MainController mainController, String nickname) {
		this.mainController = mainController;
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
					// Ações para tornar possÃ­vel o fechamento da janela de status
					statusLogon.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
					statusLogon.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
					statusLogon.close();
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

		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An error has been occurred.\nDetails: Invalid Object");
			alert.setHeaderText("ERROR:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			alert.show();
		}
	}

	public void lostConnection() {

	}

	public void reconnect() {

	}

	public void btnLogoffReconnectAction(ActionEvent action) {

	}

	public void btnHelpEmoctionAction(ActionEvent action) {

	}

	public void btnSendMsgAction(ActionEvent action) {

	}

	public void openMessagePrivateWindow(Label user) {

	}

	public void usersListEvent(ObservableList<InfoUserModel> users) {
		users.addListener((ListChangeListener<InfoUserModel>) (c) -> {
			while(c.next()) {
				if(c.wasAdded()) {
					// COMPLETAR
				} else {
					// COMPLETAR
				}
			}
		});
	}

	public void globalChatMsgEvent(ObservableList<MessageRequest> globalChat) {
		globalChat.addListener((ListChangeListener<MessageRequest>) (c) -> {
			while(c.next()) {
				if(c.wasAdded()) {
					// COMPLETAR
				}
			}
		});
	}

	public void close() {
		mainController.closeApp();
	}

}
