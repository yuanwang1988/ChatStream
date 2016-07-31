package com.chatstream.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chatstream.models.Channel;
import com.chatstream.models.Msg;
import com.chatstream.models.User;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class ServerVerticle extends AbstractVerticle {
	
	private static final int port = 8080;
	private static Map<String,Channel> channelMap = new HashMap<String,Channel>();
	private static Map<String,User> userMap = new HashMap<String,User>();
	
	
	private static Logger logger = LoggerFactory.getLogger(ServerVerticle.class);
	
	public void start(Future<Void> startFuture){
		
		//==============================//
		// Set up test message			//
		//==============================//
		userMap.put("Joe", new User("Joe"));
		userMap.put("Jill", new User("Jill"));
		
		channelMap.put("General", new Channel("General"));
		Msg msgOne = new Msg("Joe", "Hello.");
		Msg msgTwo = new Msg("Jill", "Hey there!");
		channelMap.get("General").consumeMessage(msgOne);
		channelMap.get("General").consumeMessage(msgTwo);
		
		//==============================//
		
		HttpServer server = vertx.createHttpServer();
	
		Router router = Router.router(vertx);
		
		router.route().handler(BodyHandler.create());
		
		//GET or Add to list of Users
		router.get("/users").handler(rc -> {
			logger.info("Gest request received for a list of users");
			List<String> userIDList = new ArrayList<String>(userMap.keySet());
			JsonObject response = new JsonObject();
			response.put("Users", userIDList);
			
			HttpServerResponse httpResponse = rc.response();
			httpResponse.putHeader("content-type", "application/json");
			httpResponse.end(response.encodePrettily());
		});
		
		router.post("/users").handler(rc -> {
			JsonObject userJson = rc.getBodyAsJson();
			logger.info("Request received to add new user: [{}]", userJson);
			
			String userID = userJson.getString("User ID");
			
			userMap.put(userID, new User(userID));
			
			rc.reroute(HttpMethod.GET, rc.normalisedPath());
		});
		
		router.get("/users/:userid").handler(rc -> {
			
			String userID = rc.pathParam("userid");
			User user = userMap.get(userID);
			
			logger.info("Get request received for Channel [{}]", userID);
			
			HttpServerResponse httpResponse = rc.response();
			httpResponse.putHeader("content-type", "application/json");
			httpResponse.end(user.toJson().encodePrettily());
			
		});
		
		//Subscribe users to channels
		router.get("/users/:userid/channelsubscriptions").handler(rc -> {
			
			String userID = rc.pathParam("userid");
			User user = userMap.get(userID);
			
			logger.info("Get request received for Channel [{}]", userID);
			JsonObject response = new JsonObject();
			response.put("Channels Subscribed", user.getChannelIDs());
			
			HttpServerResponse httpResponse = rc.response();
			httpResponse.putHeader("content-type", "application/json");
			httpResponse.end(response.encodePrettily());
			
		});
		
		router.post("/users/:userid/channelsubscriptions").handler(rc -> {
			
			String userID = rc.pathParam("userid");
			User user = userMap.get(userID);
			
			JsonObject channelJson = rc.getBodyAsJson();
			String channelID = channelJson.getString("Channel ID");
			
			logger.info("Request received to add Channel [{}] to User [{}] subscriptions", channelID, userID);
			
			Channel channel = channelMap.get(channelID);
			user.subscribeToChannel(channel);
			channel.registerUser(user);
			
			rc.reroute(HttpMethod.GET, rc.normalisedPath());
			
		});
		
		
		//GET or ADD to list of Channels
		router.get("/channels").handler(rc -> {
			logger.info("Get requested received for a list of channels");
			
			List<String> channelIDList = new ArrayList<String>(channelMap.keySet());
			JsonObject response = new JsonObject();
			response.put("Channels", channelIDList);
			
			HttpServerResponse httpResponse = rc.response();
			httpResponse.putHeader("content-type", "application/json");
			httpResponse.end(response.encodePrettily());
		});
		
		router.post("/channels").handler(rc -> {
			
			JsonObject channelJson = rc.getBodyAsJson();
			logger.info("Request received to add new channel: [{}]", channelJson);
			
			String channelID = channelJson.getString("Channel ID");
			
			channelMap.put(channelID, new Channel(channelID));
			
			rc.reroute(HttpMethod.GET, "/channels");
			
		});
		
		//GET or POST messages to channel
		router.get("/channels/:channelid").handler(rc -> {
			String channelID = rc.pathParam("channelid");
			Channel channel = channelMap.get(channelID);
			
			logger.info("Get request received for Channel [{}]", channelID);
			
			HttpServerResponse httpResponse = rc.response();
			httpResponse.putHeader("content-type", "application/json");
			httpResponse.end(channel.toJson().encodePrettily());
			
		});
		
		router.post("/channels/:channelid").handler(rc -> {
			String channelID = rc.pathParam("channelid");
			Channel channel = channelMap.get(channelID);
			
			JsonObject msgJson = rc.getBodyAsJson();
			logger.info("New Message Received: [{}] for Channel [{}]", msgJson.encodePrettily(), channelID);
			try {
				Msg msg = Msg.getInstanceFromJson(msgJson);
				channel.consumeMessage(msg);
				
				rc.reroute(HttpMethod.GET, rc.normalisedPath());
				
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Failed to parse message. Error message: [{}]", e.getMessage());
			}
		});
		
		//router.put("/channels/:channelid").handler(requestHandler)
//		
//		vertx.eventBus().consumer("user/signIn").handler(msg ->{
//			msg.reply(new JsonObject().put("status", 200));
//			vertx.eventBus().publish("users/SignedIn", msg.body());
//		});
	
		server.requestHandler(router::accept).listen(port);
		logger.info("Server deployed. Listening to port: [{}]", Integer.toString(port));	
	}
}
