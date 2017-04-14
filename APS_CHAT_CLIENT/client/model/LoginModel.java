package model;

import java.io.IOException;
import java.io.ObjectOutputStream;

import controller.MainController;
import model.requests.InfoReturn;
import model.requests.OperationType;
import model.requests.Request;

public class LoginModel {

	private String errorMessage = "";

    public LoginModel(){}

    public String getErrorMessage(){
        return errorMessage;
    }

    public boolean login(String user){
    	Request loginRequest = new Request(OperationType.LOGIN);
    	loginRequest.setUserFrom(user);
    	loginRequest.setUserTo("Server");

    	try(ObjectOutputStream ois = new ObjectOutputStream(MainController.getConnection().getOutputStream())){
    		ois.writeObject(loginRequest);
    		return true;
		} catch (IOException e) {
			errorMessage = e.getMessage() + "\n" + e.getLocalizedMessage();
			e.printStackTrace();
			return false;
		}

    }

    public boolean loginObjectRecieve(Request result){
    	if(result.getOperation() == OperationType.SUCCESS_MSG) {
    		InfoReturn infoReturn = (InfoReturn) result;
    		if(infoReturn.getOperationSource() == OperationType.LOGIN) {
    			return true;
    		} else {
    			errorMessage = "Objeto inválido recebido do servidor.";
    			return false;
    		}
    	} else if(result.getOperation() == OperationType.ERROR_MSG) {
    		InfoReturn infoReturn = (InfoReturn) result;
    		errorMessage = infoReturn.getMessage();
    		return false;
    	} else {
    		errorMessage = "Objeto inválido recebido do servidor.";
			return false;
    	}
    }
}