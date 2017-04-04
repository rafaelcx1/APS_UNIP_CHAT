package model;

import model.requests.Request;

public class LoginModel {

	private String errorMessage = "";

    public LoginModel(){

    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public boolean login(String user, String pwd){
    	return false;
    }

    public boolean loginObjectRecieve(Request result){
    	return false;
    }
}
