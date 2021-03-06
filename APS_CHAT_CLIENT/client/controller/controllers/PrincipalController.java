package controller.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import controller.MainController;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import model.PrincipalModel;
import model.requests.InfoRequest;
import model.requests.InfoReturn;
import model.requests.InfoUserModel;
import model.requests.MessageRequest;
import model.requests.OperationType;
import model.requests.Request;
import util.ChatTextUtil;
import util.HelpEmoticon;

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
	private Button btnHelpEmoticon;
	@FXML
	private Button btnSendMsg;
	@FXML
	private TextField tfMsgBox;
	@FXML
	private HBox hboxTittle;
	@FXML
	private ScrollPane scrollPaneGlobalMsg;

	private MainController mainController;
	private PrincipalModel principalModel;
	private Alert statusLogon;
	private HelpEmoticon<PrincipalController> helpEmoticon;

	private double xOffset;
	private double yOffset;


	public PrincipalController(MainController mainController, String nickname) {
		this.mainController = mainController;
		mainController.getStage().setResizable(true);
		principalModel = new PrincipalModel(nickname);
		helpEmoticon = new HelpEmoticon<PrincipalController>(this);
		globalChatMsgEvent(principalModel.getGlobalChatMsg());
		usersListEvent(principalModel.getUsers());

		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../../view/PrincipalView.fxml"));
			loader.setController(this);
			mainController.getStage().setScene(new Scene(loader.load()));
			mainController.getStage().centerOnScreen();
			vbGlobalChatPane.setMinWidth(scrollPaneGlobalMsg.getWidth() - 50);
			tfMsgBox.requestFocus();
			scrollPaneGlobalMsg.vvalueProperty().bind(vbGlobalChatPane.heightProperty());
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

	public String getNickname() {
		return principalModel.getNickname();
	}

	public TextField getTfMsg() {
		return tfMsgBox;
	}

	public Stage getStage() {
		return mainController.getStage();
	}

	public void recieveObject(Request request) {
		if(request.getOperation() == OperationType.INFO) {

			if(principalModel.treatObject((InfoRequest) request)) {

				if(statusLogon != null) {
					statusLogon.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
					statusLogon.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
					statusLogon.close();

					tfMsgBox.setDisable(false);
					lblStatus.setText("Status: Conectado");
					lblUserNickname.setText("Nickname: " + principalModel.getNickname());
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

				principalModel.setNickname(request.getUserTo());
				lblUserNickname.setText("Nickname: " + principalModel.getNickname());

				Request requestInfo = new Request(OperationType.INFO);
				requestInfo.setUserFrom(principalModel.getNickname());
				requestInfo.setUserTo("Server");
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
			} else if(((InfoReturn)request).getOperationSource() == OperationType.LOGIN && request.getOperation() == OperationType.ERROR_MSG) {
				TextInputDialog alert = new TextInputDialog();
				alert.setHeaderText("Seu login já está em uso");
				alert.setTitle("LOGON ERROR");
				alert.setContentText("Digite outro nickname: ");
				alert.setResizable(false);
				Optional<String> result = alert.showAndWait();
				if(result.isPresent()) {
					mainController.reconnect(result.get());
				} else {
					this.close();
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

	public void moveWindowOnMousePressed(MouseEvent event) {
		xOffset = event.getSceneX();
		yOffset = event.getSceneY();
	}

	public void moveWindowOnMouseDrag(MouseEvent event) {
		mainController.getStage().setX(event.getScreenX() - xOffset);
		mainController.getStage().setY(event.getScreenY() - yOffset);

		if(helpEmoticon != null) {
			helpEmoticon.getStage().setX(event.getScreenX() - xOffset + mainController.getStage().getWidth());
			helpEmoticon.getStage().setY(event.getScreenY() - yOffset);
		}
	}

	public void lostConnection() {
		tfMsgBox.setDisable(true);
		btnSendMsg.setDisable(true);
		lblStatus.setText("Status: Sem conexão com o servidor. Reconectando...");
		mainController.lostConnectionMessages();
		btnLogoffReconnect.setText("Sair");
		btnLogoffReconnect.layout();
	}

	public void reconnect() {
		lblStatus.setText("Status: Conectado.");
		btnSendMsg.setDisable(false);
		btnLogoffReconnect.setText("Logoff");
		mainController.reconnectConnectionMessages();
	}

	public void tfMsgBoxEnterPressed(KeyEvent action) {
		if(action.getCode() == KeyCode.ENTER) {
			btnSendMsg.fire();
			tfMsgBox.setText("");
		}
	}

	public void btnLogoffReconnectAction(ActionEvent action) {
		this.close();
	}

	public void btnHelpEmoticonAction(ActionEvent action) {
		if(helpEmoticon.getStage().isShowing())
			helpEmoticon.focus();
		else {
			helpEmoticon.getStage().setX(mainController.getStage().getX() + mainController.getStage().getWidth());
			helpEmoticon.getStage().setY(mainController.getStage().getY());
			helpEmoticon.getStage().show();
		}
	}

	public void closeHelpEmoticon() {
		helpEmoticon.getStage().hide();
	}

	public void btnSendMsgAction(ActionEvent action) {
		MessageRequest msgRequest = new MessageRequest();
		msgRequest.setUserFrom(principalModel.getNickname());
		msgRequest.setUserTo(null);
		msgRequest.setMessage(tfMsgBox.getText());
		if(tfMsgBox.getText().length() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An error has been occurred.\nDetails: Mensagem vazia!");
			alert.setHeaderText("ERROR:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			alert.show();
		} else {
			if(!principalModel.sendObject(msgRequest)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An error has been occurred.\nDetails: " + principalModel.getErrorMessage());
				alert.setHeaderText("ERROR:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.show();
			} else {
				principalModel.getGlobalChatMsg().add(msgRequest);
				tfMsgBox.setText("");
				tfMsgBox.requestFocus();
			}
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
						lblUser.getStylesheets().add(this.getClass().getResource("../../view/GlobalMsgStyle.css").toExternalForm());
						vbUsersListPane.getChildren().add(lblUser);
					}
				} else {
					for(InfoUserModel user : c.getRemoved()) {
						List<Node> listDelete = new ArrayList<>();
						for(Node label : vbUsersListPane.getChildren()) {
							if(((Label)label).getText().equals(user.getLogin())) {
								listDelete.add(label);
								mainController.offlineUser(user.getLogin());
							}
						}
						for(Node label : listDelete) {
							vbUsersListPane.getChildren().remove(label);
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
						TextFlow tf = ChatTextUtil.parseMsg(msg);
						tf.getStylesheets().add(ChatTextUtil.class.getClass().getResource("../../view/GlobalMsgStyle.css").toExternalForm());
						if(msg.getUserFrom().equals(principalModel.getNickname()))
							tf.getStyleClass().add("messageUser");
						else
							tf.getStyleClass().add("message");
						vbGlobalChatPane.getChildren().add(tf);
					}
				}
			}
		});
	}

	public void close() {
		mainController.closeApp();
	}

}