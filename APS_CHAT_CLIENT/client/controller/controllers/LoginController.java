package controller.controllers;
import java.awt.FlowLayout;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import model.LoginModel;
import model.requests.Request;

@SuppressWarnings("unused")
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
    	// Terminar
    }

    // MÈtodo padr„o do FXML que È chamado ao carregar os elementos
    public void initialize() {

    }

    // MÈtodo do bot„o btnNext, ele ir· logar se o loginScreen for true, ou passar para a tela de login se esta vari·vel for false
    public void btnNextEvent(ActionEvent event) {
	if(loginScreen) {
		// Terminar
	} else {
		// Terminar
	}
    }

    // MÈtodo do bot„o btnExit, ir· sair se o loginScreen for false, ou voltar caso seja true
    public void btnExitEvent(ActionEvent event) {
    	if(loginScreen) {
    		// Terminar
    	} else {
    		// Terminar
    	}

    }

    // MÈtodo ir· passar o request para o login model
    public void recieveObject(Request request) {
    	if(loginModel.loginObjectRecieve(request)) {
    		// Terminar
    	} else {
    		// Terminar
    	}
    }

    // M√©todo que ir√° executar a√ß√µes quando a conex√£o com o servidor cair
    public void lostConnection() {
	    // Terminar
    }

    // M√©todo que ir√° executar a√ß√µes quando a conex√£o com o servidor voltar
    public void reconnect() {
	    // Terminar
    }

    // MÈtodo que ir· fechar a janela
    public void close() {
	// Terminar
    }

}
