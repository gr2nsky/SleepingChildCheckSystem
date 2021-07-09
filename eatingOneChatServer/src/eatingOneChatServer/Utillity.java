package eatingOneChatServer;

import java.util.Calendar;
import java.util.TimeZone;

public class Utillity {
	//날자계산
	public static String getTime() {
        //시간구하기
		String time = null;
		String div = "오전";
		
		TimeZone jt = TimeZone.getTimeZone("Asia/Seoul");
        Calendar cal = Calendar.getInstance(jt);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        String minT = null;
        if( hour > 12) {
        	hour = hour - 12;
        	div = "오후";
        }
        if( min < 10) {
        	minT = "0"+Integer.toString(min);
        }
        else {
        	minT = Integer.toString(min);
        }
        time = div + Integer.toString(hour) + ":" + minT;
        
        return time;
	}
	
}
