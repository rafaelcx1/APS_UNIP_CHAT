package util;

import java.time.LocalTime;

public class DateUtil {

	public static String timeNow() {
		int hours = LocalTime.now().getHour();
		int minutes = LocalTime.now().getMinute();
		int seconds = LocalTime.now().getSecond();
		String time = "";

		if(hours < 10)
			time = "0" + hours + ":";
		else
			time = hours + ":";

		if(minutes < 10)
			time += "0" + minutes + ":";
		else
			time += minutes + ":";

		if(seconds < 10)
			time += "0" + seconds;
		else
			time += seconds;

		return time;
	}

}
