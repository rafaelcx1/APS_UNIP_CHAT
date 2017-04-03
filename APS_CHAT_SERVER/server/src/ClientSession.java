package src;

import java.net.Socket;

import requests.Request;

public class ClientSession extends Thread {

	private Socket session;
	private String user;

	public ClientSession(Socket session) {
		this.session = session;
	}

	@Override
	public void run() {
		sessionStart();
	}

	public void sessionStart() {

	}

	public void treatObject(Request request) {

	}

	public Socket getSession() {
		return session;
	}

	public String getUser() {
		return user;
	}

}
