package net.dzikoysk.funnyguilds.basic.util;

import java.util.ArrayList;
import java.util.List;

import net.dzikoysk.funnyguilds.basic.Guild;
import net.dzikoysk.funnyguilds.basic.User;

public class UserUtils {
	
	private static List<User> users = new ArrayList<User>();

	public static List<User> getUsers(){
		List<User> x = new ArrayList<User>(users);
		return x;
	}
	
	public static void addUser(User user){
		users.add(user);
	}
	
	public static void removeUser(User user){
		users.remove(user);
	}
	
	public static void removeGuild(List<User> users){
		for(User u : users){
			u.removeGuild();
		}
	}
	
	public static void setGuild(List<User> users, Guild guild){
		for(User u : users){
			u.setGuild(guild);
		}
	}
	
	public static List<String> getNames(List<User> users){
		List<String> list = new ArrayList<>();
		for(User u : users){
			list.add(u.getName());
		}
		return list;
	}
	
	public static List<User> getUsers(List<String> names){
		List<User> list = new ArrayList<>();
		for(String s : names){
			list.add(User.get(s));
		}
		return list;
	}
}
