package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import requests.Request;

public class ClientSession implements Runnable {

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
		try(ObjectInputStream ois = (ObjectInputStream) session.getInputStream()) {
			// continuar
		} catch(IOException e) {
			// continuar
		}
	}

	public void treatObject(Request request) {
		// continuar
	}

	public Socket getSession() {
		return session;
	}

	public String getUser() {
		return user;
	}

}
