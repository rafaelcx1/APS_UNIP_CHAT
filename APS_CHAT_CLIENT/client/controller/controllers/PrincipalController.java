package controller.controllers;

import java.io.IOException;

import controller.MainController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.PrincipalModel;
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


	public PrincipalController(MainController mainController, String nickname) {
		this.mainController = mainController;
		principalModel = new PrincipalModel(nickname);

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
		
		
	}

	public void recieveObject(Request request) {

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

	public void usersListEvent(ObservableList<String> users) {

	}

	public void globalChatMsgEvent(ObservableList<String> users) {

	}

	public void close() {
		// TODO Auto-generated method stub

	}

}
