package com.chatstream.models;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.json.JsonObject;

public class User {
	private String userID;
	private List<Channel> channels;
	
	public User(String userID){
		this.userID = userID;
		this.channels = new ArrayList<Channel>();
	}
	
	public void subscribeToChannel(Channel channel){
		this.channels.add(channel);
	}
	
	public String getUserID(){
		return this.userID;
	}
	
	public List<Channel> getChannels(){
		return this.channels;
	}
	
	public List<String> getChannelIDs(){
		List<String> channelIDList = new ArrayList<String>();
		for(Channel channel : this.channels){
			channelIDList.add(channel.getChannelID());
		}
		return channelIDList;
	}
	
	public JsonObject toJson(){
		List<String> channelList = new ArrayList<String>();
		for(Channel channel : this.channels){
			channelList.add(channel.getChannelID());
		}
		
		
		JsonObject userJson = new JsonObject();
		userJson.put("User ID", this.userID);
		userJson.put("Channels", channelList);
		
		return userJson;
	}
}
