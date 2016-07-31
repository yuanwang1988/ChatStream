package com.chatstream.models;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;

public class Channel {
	private String channelID;
	private List<User> users;
	private List<Msg> messageHistory;
	
	public Channel(String channelID){
		this.channelID = channelID;
		this.users = new ArrayList<User>();
		this.messageHistory = new ArrayList<Msg>();
	}
	
	public void registerUser(User user){
		this.users.add(user);
	}
	
	public void consumeMessage(Msg msg){
		this.messageHistory.add(msg);
	}
	
	public String getChannelID(){
		return this.channelID;
	}
	
	public List<User> getUsers(){
		return this.users;
	}
	
	public List<String> getUserIDs(){
		List<String> userIDList = new ArrayList<String>();
		for(User user : this.users){
			userIDList.add(user.getUserID());
		}
		return userIDList;
	}
	
	public List<Msg> getMessageHistory(){
		return this.messageHistory;
	}
	
	public JsonObject toJson(){
		List<String> userIDList = new ArrayList<String>();
		for(User user : this.users){
			userIDList.add(user.getUserID());
		}
		
		List<JsonObject> msgHistoryList = new ArrayList<JsonObject>();
		for(Msg msg : this.messageHistory){
			msgHistoryList.add(msg.toJson());
		}
		
		JsonObject channelJson = new JsonObject();
		channelJson.put("Channel ID", channelID);
		channelJson.put("Users", userIDList);
		channelJson.put("Message History", msgHistoryList);
		
		return channelJson;
	}

}
