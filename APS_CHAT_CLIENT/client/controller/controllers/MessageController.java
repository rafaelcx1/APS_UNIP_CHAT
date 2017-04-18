package controller.controllers;

import java.io.IOException;

import controller.MainController;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.MessageModel;
import model.requests.InfoReturn;
import model.requests.MessageRequest;
import model.requests.OperationType;
import model.requests.Request;

public class MessageController extends Application {

	@FXML
	private AnchorPane rootPane;
	@FXML
	private ScrollPane scrollPaneVBox;
	@FXML
	private VBox vbMsgPane;
	@FXML
	private HBox hbSendMsgPane;
	@FXML
	private Label lblNicknameRecipient;
	@FXML
	private Label lblStatusRecipient;
	@FXML
	private TextField tfSendMsg;
	@FXML
	private Button btnSendMsg;
	@FXML
	private Button btnHelpEmoction;

	private MessageModel messageModel;
	private MainController mainController;
	private Stage rootStage;


	public MessageController(String loginRecipient, MainController mainController) {
		messageModel = new MessageModel(loginRecipient);
		this.mainController = mainController;
		chatMsgListEvent(messageModel.getChatMsg());
		lblNicknameRecipient.setText(loginRecipient);
		lblStatusRecipient.setText("Online");
		launch();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		rootStage = primaryStage;

		try {
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource("../../view/MessageView.fxml"));
			loader.setController(this);
			rootStage.setScene(new Scene(loader.load()));
			rootStage.show();
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
		lblStatusRecipient.setText("Offline");
		tfSendMsg.setText("User Offline");
		tfSendMsg.setDisable(true);
	}

	public void chatMsgListEvent(ObservableList<MessageRequest> chatMsgList) {
		chatMsgList.addListener((ListChangeListener<MessageRequest>) (c) -> {
			while(c.next()) {
				if(c.wasAdded()) {
					// COMPLETAR
				} else {
					// COMPLETAR
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
	}

	public void btnHelpEmoctionEvent(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText(""); // Emoctions
		alert.setHeaderText("Emoctions:");
		alert.setTitle("Help Emoction Window");
		alert.setResizable(false);
		alert.show();
	}

	public void showWindow() {
		rootStage.centerOnScreen();
		rootStage.requestFocus();
		rootStage.show();
	}

	public void close() {
		mainController.closeApp();
	}

}
