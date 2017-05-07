package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import controller.controllers.LoginController;
import controller.controllers.MessageController;
import controller.controllers.PrincipalController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.requests.OperationType;
import model.requests.Request;

public class MainController extends Application {

	private static Socket connection = new Socket();
	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;
	private static RecieveObjectThread recieveObject;
	private static Thread recieveObjectT = new Thread(recieveObject);
	private LoginController loginController;
	private PrincipalController principalController;
	private ArrayList<MessageController> messageWindows = new ArrayList<>();
	private ObservableSet<String> chatWindowsUsers = FXCollections.observableSet();
	private BooleanProperty connectionStatus = new SimpleBooleanProperty(true);
	private boolean userLogged;
	private boolean logoffRequested = false;
	private String nickname;
	private Stage rootStage;
	private String host;


	public static void main(String[] args) {
		launch(args);
	}

	public static Socket getConnection() {
		return connection;
	}

	public static ObjectInputStream getOis() {
		return ois;
	}

	public static ObjectOutputStream getOos() {
		return oos;
	}

	public String getNickname() {
		return nickname;
	}

	public synchronized boolean setConnection(String host) {
		this.host = host;
		connection = new Socket();
		try {
			connection.connect(new InetSocketAddress(host, 9876), 1200);
			connection.setKeepAlive(true);
			oos = new ObjectOutputStream(connection.getOutputStream());
			ois = new ObjectInputStream(connection.getInputStream());
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public void start(Stage stage) {
		rootStage = stage;
		userLogged = false;
		recieveObject = new RecieveObjectThread();
		connectionStatusEvent(this.connectionStatus);
		rootStage.setOnCloseRequest((event) -> closeApp());
		openLogonScreen();
	}

	public Stage getStage() {
		return rootStage;
	}

	public void closeApp() {
		logoff();
		rootStage.close();
		Platform.exit();
		System.exit(0);
	}

	public void recieveObject(Request request) {
		if(loginController != null && !userLogged) {
			new Thread(() -> {
				Platform.runLater(() -> {
					loginController.recieveObject(request);
				});
			}).start();
		} else if(principalController != null && userLogged) {
			new Thread(() -> {
				Platform.runLater(() -> {
					if(!isMessageWindowRequest(request));
					principalController.recieveObject(request);
				});
			}).start();
		}
	}

	public void openLogonScreen() {
		if(principalController == null) {
			loginController = new LoginController(this);

		} else {
			principalController.close();
			principalController = null;
			loginController = new LoginController(this);
		}
		rootStage.setResizable(false);
		rootStage.initStyle(StageStyle.UNDECORATED);
		rootStage.show();
	}

	public void openPrincipalScreen(String nickname) {
		if(loginController == null) {
			principalController = new PrincipalController(this, nickname);
		} else {
			loginController = null;
			principalController = new PrincipalController(this, nickname);
		}
		this.nickname = nickname;
		userLogged = true;
	}

	public void openMessageScreen(String loginRecipient) {
		if(chatWindowsUsers.add(loginRecipient)) {
			messageWindows.add(new MessageController(loginRecipient, this));
		} else {
			for(MessageController msgWindow : messageWindows) {
				if(msgWindow.getRecipient().equals(loginRecipient)) {
					msgWindow.showWindow();
					break;
				}
			}
		}
	}

	public void closeChatWindow(String loginRecipient) {
		if(chatWindowsUsers.remove(loginRecipient)) {
			messageWindows.removeIf((t) -> {
				if(t.getRecipient().equals(loginRecipient)) {
					t.getStage().close();
					return true;
				} else {
					return false;
				}
			});
		}
	}

	public void offlineUser(String loginRecipient) {
		for(MessageController window : messageWindows) {
			if(window.getRecipient().equals(loginRecipient))
				window.recipientDisconnected();
		}
	}

	public boolean isMessageWindowRequest(Request msg) {
		if(msg.getOperation() == OperationType.SEND_OR_RECIEVE_MSG && !msg.getUserFrom().equals("Server") && msg.getUserTo() != null) {
			boolean found = false;
			for(MessageController msgWindow : messageWindows) {
				if(msgWindow.getRecipient().equals(msg.getUserFrom())) {
					msgWindow.recieveObject(msg);
					found = true;
					break;
				}
			}
			if(!found) {
				openMessageScreen(msg.getUserFrom());
				isMessageWindowRequest(msg);
			}
			return true;
		} else if(msg.getOperation() == OperationType.SUCCESS_MSG || msg.getOperation() == OperationType.ERROR_MSG && !msg.getUserFrom().equals("Server")){
			for(MessageController msgWindow : messageWindows) {
				if(msgWindow.getRecipient().equals(msg.getUserFrom())) {
					msgWindow.recieveObject(msg);
					break;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public void initializeThreads() {
		recieveObjectT = new Thread(recieveObject);
		recieveObjectT.start();
	}

	public void finalizeConnection() {
		if(connection != null) {
			if(!connection.isClosed())
				try {
					connection.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		connection = null;
	}

	public void connectionStatusEvent(BooleanProperty connectionStatus) {
		connectionStatus.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if(newValue && oldValue != newValue) {
				reconnectionAction();
			}
			else if(!newValue && oldValue != newValue) {
				lostConnectionAction();
			}
		});
	}

	public void lostConnectionAction() {
		if(loginController != null && !userLogged) {
			finalizeConnection();
			if(!logoffRequested)
				Platform.runLater(() -> loginController.lostConnection());
		} else {
			finalizeConnection();
			if(!logoffRequested)
				Platform.runLater(() -> principalController.lostConnection());
			userLogged = false;
		}
	}

	public boolean reconnect(String nickname) {
		Request loginRequest = new Request(OperationType.LOGIN);
		loginRequest.setUserFrom(nickname);
		loginRequest.setUserTo("Server");
		try {
			if(connection != null && oos != null) {
				if(!connection.isClosed() && connection.isConnected()) {
					oos.writeObject(loginRequest);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch(Exception e) {
			return false;
		}
	}

	public void reconnectionAction() {
		if(loginController != null) {
			Platform.runLater(() -> loginController.reconnect());
		} else {
			Platform.runLater(() -> principalController.reconnect());
			userLogged = true;
		}
	}

	public void logoff() {
		try {
			logoffRequested = true;
			if(connection != null) {
				if(!connection.isClosed()) {

					if(connection.isConnected()) {
						Request requestLogoff = new Request(OperationType.LOGOFF);
						requestLogoff.setUserTo("Server");

						if(loginController != null) {
							requestLogoff.setUserFrom(null);
						} else {
							requestLogoff.setUserFrom(principalController.getNickname());
						}

						oos.writeObject(requestLogoff);
						Thread.sleep(100);
					}
					if(connection != null) connection.close();
					recieveObjectT.interrupt();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/*---------- Threads ----------*/

	private class RecieveObjectThread implements Runnable {

		@Override
		public void run() {
			new Thread(() -> {

				while(true) {
					if(connection != null) {

						try {
							if((!connection.isConnected() || !connection.getInetAddress().isReachable(1500)) && !logoffRequested) {

								if(!(connectionStatus.getValue() == false))
									connectionStatus.set(false);

							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					try {
						Thread.sleep(1000);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}

				}

			}).start();

			boolean down = false;
			while(true) {

				if(!down) {

					try {
						Object obj = ois.readObject();

						if(obj != null) {

							if(obj instanceof Request) {
								Request request = (Request) obj;

								if(loginController != null || !userLogged) {

									try {
										Thread.sleep(500);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

								}

								recieveObject(request);
							}

						} else {
							throw new IOException("Lost connection");
						}

					} catch(IOException e) {
						if(!(connectionStatus.get() == false))
							connectionStatus.setValue(false);
						down = true;

					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}

				} else {
					if(!logoffRequested) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						if(setConnection(host)) {
							if(nickname != null) {
								if(reconnect(nickname)) {
									connectionStatus.setValue(true);
									down = false;
								}
							}
							down = false;

						} else {
							if(!(connectionStatus.getValue() == false))
								connectionStatus.setValue(false);
						}
					} else {
						break;
					}
				}
			}
		}
	}
}
