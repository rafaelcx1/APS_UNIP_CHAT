package util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.requests.MessageRequest;

public class ChatTextUtil {

	public static TextFlow parseMsg(MessageRequest message) {
		TextFlow tf = new TextFlow();
		Text tx = new Text("<" + DateUtil.timeNow() + "> " + message.getUserFrom() + ":");
		tx.getStyleClass().add("title");
		tf.getChildren().add(tx);
		return parse(tf, message.getMessage().toCharArray());
	}

	public static TextFlow parseMsg(String message) {
		return parse(new TextFlow(), message.toCharArray());
	}

	private static TextFlow parse(TextFlow tf, char[] message) {
		String text = " ";
		for(int counter = 0; counter < message.length - 1; counter++) {
			if(message[counter] == ':') {

				switch(message[counter + 1]) {
				case ')' : {
					ImageView iv = new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/smiling.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'D' : {
					ImageView iv = new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/grinning.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'P': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/delicious.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'O': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/fear.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'Z': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/tired.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'b': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/kiss.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'i': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/smirking.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'm': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/thinking.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'a': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/halo.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 's': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/nauseated.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'z': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/sleeping.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case '&': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/unamused.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'L': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/sunglasses.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'o': {
					if(counter <= message.length - 3) {
						if(message[counter + 2] == 'k') {
							ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/thumbs-up.png")));
							iv.setTranslateY(3);
							tf.getChildren().addAll(new Text(text + " "), iv);
							text = "";
							counter += 2;
						} else {
							text += "" + message[counter] + message[counter + 1] + message[counter + 2];
							counter += 2;
						}
					} else {
						text += "" + message[counter] + message[counter + 1];
						counter += 1;
					}
					break;
				}
				case 'n': {
					if(counter <= message.length - 3) {
						if(message[counter + 2] == 'l') {
							ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/thumbs-down.png")));
							iv.setTranslateY(3);
							tf.getChildren().addAll(new Text(text + " "), iv);
							text = "";
							counter += 2;
						} else {
							text += "" + message[counter] + message[counter + 1] + message[counter + 2];
							counter += 2;
						}
					} else {
						text += "" + message[counter] + message[counter + 1];
						counter += 1;
					}
					break;
				}
				case 'G': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/upside-down.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case '¬': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/horns.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				case 'c': {
					if(counter <= message.length - 3) {
						if(message[counter + 2] == 'l') {
							ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/clapping.png")));
							iv.setTranslateY(3);
							tf.getChildren().addAll(new Text(text + " "), iv);
							text = "";
							counter += 2;
						} else {
							text += "" + message[counter] + message[counter + 1] + message[counter + 2];
							counter += 2;
						}
					} else {
						text += "" + message[counter] + message[counter + 1];
						counter += 1;
					}
					break;
				}
				case 'E': {
					ImageView iv =  new ImageView(new Image(ChatTextUtil.class.getResourceAsStream("../view/emoticons/confounded.png")));
					iv.setTranslateY(3);
					tf.getChildren().addAll(new Text(text + " "), iv);
					text = "";
					counter += 1;
					break;
				}
				default:
					text += "" + message[counter] + message[counter + 1];
				}
			} else {
				if(counter == message.length - 2)
					text += "" + message[counter] + message[counter + 1];
				else
					text += "" + message[counter];
			}
		}
		if(message.length == 1)
			text += message[0];
		tf.getChildren().add(new Text(text));
		return tf;
	}

}
