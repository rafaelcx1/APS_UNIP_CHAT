package util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateUtil {

	public static String timeNow() {
		LocalTime localTime = LocalTime.now();
		int hours = localTime.getHour();
		int minutes = localTime.getMinute();
		int seconds = localTime.getSecond();
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

	public static String dateTimeNow() {
		LocalDateTime dateTime = LocalDateTime.now();
		int year = dateTime.getYear();
		int month = dateTime.getMonthValue();
		int day = dateTime.getDayOfMonth();
		int hours = dateTime.getHour();
		int minutes = dateTime.getMinute();
		int seconds = dateTime.getSecond();
		String time = "";

		if(day < 10)
			time += "0" + day + "/";
		else
			time += day + "/";

		if(month < 10)
			time += "0" + month + "/";
		else
			time += month + "/";

		time += year + " ";

		if(hours < 10)
			time += "0" + hours + ":";
		else
			time += hours + ":";

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
