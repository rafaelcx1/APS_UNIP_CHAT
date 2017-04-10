package controller.controllers;

import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.MessageModel;
import model.requests.MessageRequest;

@SuppressWarnings("unused")
public class MessageController {

	@FXML
	private AnchorPane rootPane;
	@FXML
	private ScrollPane scrollPaneVBox;
	@FXML
	private VBox vbMsgPane;
	@FXML
	private HBox hbSendMsgPane;
	@FXML
	private Label lblnicknameRecipient;
	@FXML
	private Label lblStatusRecipient;
	@FXML
	private TextField tfSendMsg;
	@FXML
	private Button btnSendMsg;
	@FXML
	private Button btnHelpEmoction;

	private MessageModel messageModel;


	public MessageController(String loginRecipient) {

	}

	public boolean recieveObject(MessageRequest msgRequest) {
		return false;
	}

	public String getRecipient() {
		return "";
	}

	public void lostConnection() {

	}

	public void reconnected() {

	}

	public void recipientDisconnected() {

	}

	public void btnSendMsgEvent(ActionEvent event) {

	}

	public void btnHelpEmoctionEvent(ActionEvent event) {

	}

	public void close() {

	}

}
