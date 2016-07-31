package com.chatstream.models;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.JsonObject;

public class Msg {
	private String userID;
	private String msgBody;
	
	public Msg(){
	}
	
	public Msg(String userID, String msgBody){
		this.userID = userID;
		this.msgBody = msgBody;
	}
	
	@JsonProperty("User ID")
	public String getUserID(){
		return this.userID;
	}
	
	@JsonProperty("Message Body")
	public String getMsgBody(){
		return this.msgBody;
	}
	
	//======================//
	// Setters				//
	//======================//
	public void setUserID(String userID){
		this.userID = userID;
	}
	
	public void setMsgBody(String msgBody){
		this.msgBody = msgBody;
	}
	
	
	public String encode(){
		try{
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(this);
		}catch(JsonProcessingException e){
			return null;
		}
	}
	
	public JsonObject toJson(){
		return new JsonObject(this.encode());
	}
	
	public static Msg getInstanceFromJsonString(String msgJsonStr) 
			throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Msg msg = mapper.readValue(msgJsonStr, Msg.class);
		return msg;
	}
	
	public static Msg getInstanceFromJson(JsonObject msgJson) 
			throws JsonParseException, JsonMappingException, IOException{
		String msgJsonStr = msgJson.encode();
		Msg msg = Msg.getInstanceFromJsonString(msgJsonStr);
		return msg;
	}
	
	public static void main(String[] args) throws IOException{
		Msg msg = new Msg("Yuan", "Hello there!");
		System.out.println(msg.encode());
		Msg msgDup = Msg.getInstanceFromJsonString("{\"User ID\":\"Yuan\",\"Message Body\":\"Hello there!\"}");
		System.out.println(msgDup.encode());
		
	}
}
