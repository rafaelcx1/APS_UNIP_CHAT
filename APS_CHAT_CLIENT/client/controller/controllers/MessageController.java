package controller.controllers;

import java.io.IOException;

import controller.MainController;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.MessageModel;
import model.requests.InfoReturn;
import model.requests.MessageRequest;
import model.requests.OperationType;
import model.requests.Request;
import util.ChatTextUtil;
import util.DateUtil;
import util.HelpEmoticon;

public class MessageController {

	@FXML
	private VBox vbMsgPane;
	@FXML
	private HBox hbTopPane;
	@FXML
	private Label lblNicknameRecipient;
	@FXML
	private Label lblStatusRecipient;
	@FXML
	private TextField tfSendMsg;
	@FXML
	private Button btnSendMsg;
	@FXML
	private Button btnClose;
	@FXML
	private Button btnHelpEmoticon;
	@FXML
	private ScrollPane scrollPaneMsg;


	private MessageModel messageModel;
	private MainController mainController;
	private Stage rootStage;
	private HelpEmoticon<MessageController> helpEmoticon;

	private double xOffset;
	private double yOffset;


	public MessageController(String loginRecipient, MainController mainController) {
		messageModel = new MessageModel(loginRecipient);
		this.mainController = mainController;
		chatMsgListEvent(messageModel.getChatMsg());
		start();
	}

	public void start() {
		rootStage = new Stage();
		helpEmoticon = new HelpEmoticon<MessageController>(this);
		rootStage.setOnCloseRequest((event) -> {
			this.close();
		});
		rootStage.initStyle(StageStyle.UNDECORATED);
		rootStage.getIcons().setAll(new Image(this.getClass().getResourceAsStream("../../view/icon.png")));
		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../../view/MessageView.fxml"));
			loader.setController(this);
			rootStage.setScene(new Scene(loader.load()));
			rootStage.getIcons().setAll(new Image(this.getClass().getResourceAsStream("../../view/icon.png")));
			lblNicknameRecipient.setText("Usuário: " + messageModel.getLoginRecipient());
			lblStatusRecipient.setText("Status: Online");
			rootStage.show();
			tfSendMsg.requestFocus();
			scrollPaneMsg.vvalueProperty().bind(vbMsgPane.heightProperty());
			if(!mainController.getConnectionStatus())
				lostConnection();
		} catch(IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An IOException has been occurred.\nDetails: " + e.getMessage() + "\n" + e.getLocalizedMessage());
			alert.setHeaderText("IOException:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			alert.show();

			e.printStackTrace();
		}
	}

	public void moveWindowOnMousePressed(MouseEvent event) {
		xOffset = event.getSceneX();
		yOffset = event.getSceneY();
	}

	public void moveWindowOnMouseDrag(MouseEvent event) {
		rootStage.setX(event.getScreenX() - xOffset);
		rootStage.setY(event.getScreenY() - yOffset);

		if(helpEmoticon != null) {
			helpEmoticon.getStage().setX(event.getScreenX() - xOffset + rootStage.getWidth());
			helpEmoticon.getStage().setY(event.getScreenY() - yOffset);
		}
	}

	public void recieveObject(Request request) {
		if(request.getOperation() == OperationType.SUCCESS_MSG || request.getOperation() == OperationType.ERROR_MSG) {

			if(!messageModel.treatObject((InfoReturn) request)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An error has been occurred.\nDetails: " + messageModel.getErrorMessage());
				alert.setHeaderText("ERROR:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.show();
			}

		} else if(request.getOperation() == OperationType.SEND_OR_RECIEVE_MSG) {

			if(!messageModel.treatObject((MessageRequest) request)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("An error has been occurred.\nDetails: " + messageModel.getErrorMessage());
				alert.setHeaderText("ERROR:");
				alert.setTitle("APPLICATION ERROR");
				alert.setResizable(false);
				alert.show();
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

	public Stage getStage() {
		return rootStage;
	}

	public TextField getTfMsg() {
		return tfSendMsg;
	}

	public String getRecipient() {
		return messageModel.getLoginRecipient();
	}

	public void lostConnection() {
		tfSendMsg.setText("You are Offline");
		tfSendMsg.setDisable(true);
	}

	public void reconnected() {
		tfSendMsg.setText("");
		tfSendMsg.setDisable(false);
	}

	public void recipientDisconnected() {
		lblStatusRecipient.setText("Status: Offline");
		tfSendMsg.setText("User Offline");
		tfSendMsg.setDisable(true);
		btnSendMsg.setDisable(true);
	}

	public void recipientReconnected() {
		lblStatusRecipient.setText("Status: Online");
		tfSendMsg.setText("");
		tfSendMsg.setDisable(false);
		btnSendMsg.setDisable(false);
	}

	public void chatMsgListEvent(ObservableList<MessageRequest> chatMsgList) {
		chatMsgList.addListener((ListChangeListener<MessageRequest>) (c) -> {
			while(c.next()) {
				if(c.wasAdded()) {
					for(MessageRequest msg : c.getAddedSubList()) {
						TextFlow tf = ChatTextUtil.parseMsg(msg.getMessage());
						Label lblRecipient = new Label("<" + DateUtil.timeNow() + "> " +  msg.getUserFrom() + ":");
						if(msg.getUserFrom().equals(mainController.getNickname())) {
							lblRecipient.getStyleClass().add("recipientUser");
							tf.getStyleClass().add("messageUser");
						}
						else {
							lblRecipient.getStyleClass().add("recipient");
							tf.getStyleClass().add("message");
						}

						VBox vboxMsg = new VBox(lblRecipient, tf);
						vboxMsg.setPadding(new Insets(0,0,0,0));
						vboxMsg.setFillWidth(true);
						vboxMsg.getStylesheets().add(this.getClass().getResource("../../view/MessageItemStyle.css").toExternalForm());
						vbMsgPane.getChildren().add(vboxMsg);
					}
				}
			}
		});
	}

	public void btnSendMsgEvent(ActionEvent event) {
		MessageRequest msgRequest = new MessageRequest();
		msgRequest.setMessage(tfSendMsg.getText());
		msgRequest.setUserFrom(mainController.getNickname());
		msgRequest.setUserTo(getRecipient());
		if(!messageModel.sendMsgObject(msgRequest)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("An error has been occurred.\nDetails: " + messageModel.getErrorMessage());
			alert.setHeaderText("ERROR:");
			alert.setTitle("APPLICATION ERROR");
			alert.setResizable(false);
			alert.show();
		}
		tfSendMsg.setText("");
	}

	public void btnCloseEvent(ActionEvent event) {
		this.close();
		helpEmoticon.getStage().close();
	}

	public void tfSendMsgEnterPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			btnSendMsg.fire();
			tfSendMsg.setText("");
		}
	}

	public void btnHelpEmoticonAction(ActionEvent action) {
		if(helpEmoticon.getStage().isShowing())
			helpEmoticon.focus();
		else {
			helpEmoticon.getStage().setX(rootStage.getX() + rootStage.getWidth());
			helpEmoticon.getStage().setY(rootStage.getY());
			helpEmoticon.getStage().show();
		}
	}

	public void closeHelpEmoticon() {
		helpEmoticon = null;
	}

	public void showWindow() {
		rootStage.centerOnScreen();
		rootStage.requestFocus();
	}

	public void close() {
		mainController.closeChatWindow(messageModel.getLoginRecipient());
	}

}
