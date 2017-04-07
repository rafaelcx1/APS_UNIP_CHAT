package controller.controllers;
import java.awt.FlowLayout;

import controller.MainController;
import javafx.collections.ObservableList;
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

	/*
	 * Atributos com anotação @FXML = Elementos de interface
	 * mainController = Instância do mainController
	 * loginModel = Instância do loginModel para fazer o envio e tratamento de Requests
	 * loginScreen = booleano para indicar se a tela está em config do servidor(false) ou do nickname(true)
	 * */

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
	// Terminar
    }

    // Método padrão do FXML que é chamado ao carregar os elementos
    public void initialize() {

    }

    // Método do botão btnNext, ele irá logar se o loginScreen for true, ou passar para a tela de login se esta variável for false
    public void btnNextEvent(ActionEvent event) {

    }

    // Método do botão btnExit, irá sair se o loginScreen for false, ou voltar caso seja true
    public void btnExitEvent(ActionEvent event) {

    }

    // Método irá passar o request para o login model
    public void recieveObject(Request request) {

    }

    // Método que irá executar ações quando a conexão com o servidor cair
    public void lostConnection() {
	    
    }

    // Método que irá executar ações quando a conexão com o servidor voltar
    public void reconnect() {
	    
    }
	
    // Método que irá fechar a janela
    public void close() {

    }
	
}
