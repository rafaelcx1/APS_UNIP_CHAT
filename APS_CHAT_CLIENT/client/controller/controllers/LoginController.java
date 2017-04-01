package controller.controllers;
import model.LoginModel; //Falta adicionar na biblioteca para poder importar.
import controller.MainController; //Falta adicionar na biblioteca para poder importar.
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class LoginController {
    private MainController mainController;
    private LoginModel loginModel;
    private BorderPane pane;
    private Label lbServer;
    private Label lbLogin;
    private TextField tfServer;
    private TextField tfLogin;
    private Button btnBack;
    private Button btnNext;
    //Falta statusBar
    private boolean loginScreen;
    
    public LoginController(){
        
    }
    
    public void initialize(){
    
    }
    
    public void setMainControler(MainController mainController){
    }
    
    public void btnNextEvent(ActionEvent event){
    }
    
    public void btnExitEvent(ActionEvent event){
    }
    
    public void setStatusText(String text){
        
    }
    
    public void recieveObjetc(Request request){
        
    }
}
