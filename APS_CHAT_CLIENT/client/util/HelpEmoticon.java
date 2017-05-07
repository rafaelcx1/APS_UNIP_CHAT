package util;

import controller.controllers.MessageController;
import controller.controllers.PrincipalController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelpEmoticon<T> {

	private Stage stage;
	private T controller;

	public HelpEmoticon(T controller) {
		this.controller = controller;
		stage = new Stage();
		stage.setOnCloseRequest((event) -> close());
		stage.setTitle("Emoticons");
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10,10,10,10));
		TextFlow tx = new TextFlow(new Text("Clique em um emoticon abaixo para inserir na caixa de texto"));
		vbox.getChildren().add(tx);
		vbox.getChildren().add(makeButtonEmoticon(":)"));
		vbox.getChildren().add(makeButtonEmoticon(":D"));
		vbox.getChildren().add(makeButtonEmoticon(":P"));
		vbox.getChildren().add(makeButtonEmoticon(":O"));
		vbox.getChildren().add(makeButtonEmoticon(":Z"));
		vbox.getChildren().add(makeButtonEmoticon(":b"));
		vbox.getChildren().add(makeButtonEmoticon(":i"));
		vbox.getChildren().add(makeButtonEmoticon(":m"));
		vbox.getChildren().add(makeButtonEmoticon(":a"));
		vbox.getChildren().add(makeButtonEmoticon(":s"));
		vbox.getChildren().add(makeButtonEmoticon(":z"));
		vbox.getChildren().add(makeButtonEmoticon(":&"));
		vbox.getChildren().add(makeButtonEmoticon(":L"));
		vbox.getChildren().add(makeButtonEmoticon(":ok"));
		vbox.getChildren().add(makeButtonEmoticon(":nl"));
		vbox.getChildren().add(makeButtonEmoticon(":G"));
		vbox.getChildren().add(makeButtonEmoticon(":¬"));
		vbox.getChildren().add(makeButtonEmoticon(":cl"));
		vbox.getChildren().add(makeButtonEmoticon(":E"));
		vbox.setStyle("*{-fx-font-size: 14px; -fx-font-weight: bold}");
		vbox.getStylesheets().add(this.getClass().getResource("../view/stylefx.css").toExternalForm());
		stage.setScene(new Scene(vbox));
		stage.initStyle(StageStyle.UTILITY);
		stage.setResizable(false);
		stage.setWidth(150);
		stage.setHeight(580);
	}

	private FlowPane makeButtonEmoticon(String emoticon) {
		FlowPane fp = new FlowPane(ChatTextUtil.parseMsg(emoticon), new Label(" = " + emoticon));
		fp.setOnMouseClicked((e) -> {
			if(controller instanceof PrincipalController) {
				PrincipalController controllerInstance = (PrincipalController) controller;
				if(!controllerInstance.getTfMsg().isDisabled()) {
					String tfText = controllerInstance.getTfMsg().getText();
					controllerInstance.getTfMsg().setText(tfText + emoticon);
					controllerInstance.getStage().requestFocus();
					controllerInstance.getTfMsg().requestFocus();
					controllerInstance.getTfMsg().positionCaret(((PrincipalController) controller).getTfMsg().getText().length());
				}
			} else {
				MessageController controllerInstance = (MessageController) controller;
				if(!controllerInstance.getTfMsg().isDisabled()) {
					String tfText = controllerInstance.getTfMsg().getText();
					controllerInstance.getTfMsg().setText(tfText + emoticon);
					controllerInstance.getStage().requestFocus();
					controllerInstance.getTfMsg().requestFocus();
					controllerInstance.getTfMsg().positionCaret(((MessageController) controller).getTfMsg().getText().length());
				}
			}
		});
		fp.getStyleClass().add("emoticonButton");
		fp.setAlignment(Pos.CENTER);
		return fp;
	}

	public void close() {
		if(controller instanceof PrincipalController)
			((PrincipalController) controller).closeHelpEmoticon();
		else
			((MessageController) controller).closeHelpEmoticon();
	}

	public void focus() {
		stage.requestFocus();
	}

	public Stage getStage() {
		return stage;
	}
}
