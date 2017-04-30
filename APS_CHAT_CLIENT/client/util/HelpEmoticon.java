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
	private Stage stageController;
	private T controller;

	public HelpEmoticon(T controller) {
		if(controller instanceof PrincipalController)
			stageController = ((PrincipalController) controller).getStage();
		 else
			stageController = ((MessageController) controller).getStage();

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
		stage.setX(stageController.getX() + stageController.getWidth());
		stage.setY(stageController.getY());
		stage.show();


	}

	private FlowPane makeButtonEmoticon(String emoticon) {
		FlowPane fp = new FlowPane(ChatTextUtil.parseMsg(emoticon), new Label(" = " + emoticon));
		fp.setOnMouseClicked((e) -> {
			if(controller instanceof PrincipalController) {
				String tfText = ((PrincipalController) controller).getTfMsg().getText();
				((PrincipalController) controller).getTfMsg().setText(tfText + emoticon);
				((PrincipalController) controller).getTfMsg().requestFocus();
			} else {
				String tfText = ((MessageController) controller).getTfMsg().getText();
				((MessageController) controller).getTfMsg().setText(tfText + emoticon);
				((MessageController) controller).getTfMsg().requestFocus();
			}
		});
		fp.getStyleClass().add("emoticonButton");
		fp.setAlignment(Pos.CENTER);
		return fp;
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

	public Stage getStage() {
		return stage;
	}
}
