package util;

import controller.controllers.MessageController;
import controller.controllers.PrincipalController;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelpEmoticon<T> {

	private Stage stage;
	private T controller;

	public HelpEmoticon(T controller) {
		this.controller = controller;
		stage = new Stage();
		stage.setOnCloseRequest((event) -> close());
		stage.setTitle("Emoctions");
		VBox vbox = new VBox();
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":)"), new Label(" = :)")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":D"), new Label(" = :D")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":P"), new Label(" = :P")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":O"), new Label(" = :O")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":Z"), new Label(" = :Z")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":b"), new Label(" = :b")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":i"), new Label(" = :i")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":m"), new Label(" = :m")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":a"), new Label(" = :a")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":s"), new Label(" = :s")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":z"), new Label(" = :z")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":&"), new Label(" = :&")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":L"), new Label(" = :L")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":ok"), new Label(" = :ok")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":nl"), new Label(" = :nl")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":G"), new Label(" = :G")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":¬"), new Label(" = :¬")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":cl"), new Label(" = :cl")));
		vbox.getChildren().addAll(new FlowPane(ChatTextUtil.parseMsg(":E"), new Label(" = :E")));
		vbox.setStyle("*{-fx-font-size: 14px; -fx-font-weight: bold}");
		stage.setScene(new Scene(vbox));
		stage.initStyle(StageStyle.UTILITY);
		stage.setResizable(false);
		stage.setWidth(120);
		stage.setHeight(450);
		stage.show();
	}

	public void close() {
		stage.close();
		if(controller instanceof PrincipalController)
			((PrincipalController) controller).closeHelpEmoticon();
		else
			((MessageController) controller).closeHelpEmoticon();
	}

	public void focus() {
		stage.requestFocus();
	}

}
