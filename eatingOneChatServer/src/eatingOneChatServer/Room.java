package eatingOneChatServer;

import java.util.ArrayList;

public class Room {
	
	int roomId;
	ArrayList<UserClass> user_list;
	
	Room(int roomId){
		this.roomId = roomId;
	}
	
	void add(UserClass user) {
		user_list.add(user);
	}
	
	void del(UserClass user) {
		user_list.remove(user);
	}
	int size() {
		return user_list.size();
	}
}
