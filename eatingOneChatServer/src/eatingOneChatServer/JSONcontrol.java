package eatingOneChatServer;

import org.json.simple.JSONObject;

public class JSONcontrol {
	String room;
	String sender;
	String chat;
	
	void MsgParsing(JSONObject jo) {
		room = (String) jo.get("room");
		sender = (String) jo.get("sender"); 
		chat = (String) jo.get("chat");
	}
	
	void print () {
		System.out.printf("(%s) %s : %s", room, sender, chat);
	}
}
