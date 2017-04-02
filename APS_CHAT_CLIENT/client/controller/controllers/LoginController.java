package controller.controllers;
import java.awt.FlowLayout;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import model.LoginModel;
import model.requests.Request;

@SuppressWarnings("unused")
public class LoginController {

	@FXML
    private BorderPane pane;
	@FXML
    private FlowLayout statusBar;
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
    }

    public void initialize() {

    }

    public void btnNextEvent(ActionEvent event) {

    }

    public void btnExitEvent(ActionEvent event) {

    }

    public void setStatusText(String text) {

    }

    public void recieveObjetc(Request request) {

    }
}
