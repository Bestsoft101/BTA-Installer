package b100.installer.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public abstract class TimeUtil {
	
	public static String getTimeString(long time) {
		LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
		
		StringBuilder str = new StringBuilder();
		
		str.append(intToStringWithMinLength(localDateTime.getYear(), 4));
		str.append('-');
		str.append(intToStringWithMinLength(localDateTime.getMonthValue(), 2));
		str.append('-');
		str.append(intToStringWithMinLength(localDateTime.getDayOfMonth(), 2));
		str.append(' ');
		str.append(intToStringWithMinLength(localDateTime.getHour(), 2));
		str.append(':');
		str.append(intToStringWithMinLength(localDateTime.getMinute(), 2));
		
		return str.toString();
	}
	
	public static long parseTime(String string) {
		String[] a = string.split(" ");
		String[] b = a[0].split("-");
		String[] c = a[1].split(":");
		
		int year = parseIntNamed(b[0], "year");
		int month = parseIntNamed(b[1], "month");
		int day = parseIntNamed(b[2], "day");
		int hour = parseIntNamed(c[0], "hour");
		int minute = parseIntNamed(c[1], "minute");
		
		return LocalDateTime.of(year, month, day, hour, minute).toEpochSecond(ZoneOffset.UTC);
	}
	
	private static int parseIntNamed(String str, String error) {
		try {
			return Integer.parseInt(str);
		}catch (NumberFormatException e) {
			throw new NumberFormatException("Could not parse " + error + ": " + str);
		}
	}
	
	private static String intToStringWithMinLength(int val, int minLength) {
		StringBuilder str = new StringBuilder();
		str.append(val);
		while(str.length() < minLength) {
			str.insert(0, 0);
		}
		return str.toString();
	}
	
}
