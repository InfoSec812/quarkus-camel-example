package com.redhat.consulting.acna2022;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class WebSocketEvents {
	
	private static final Logger LOG = Logger.getLogger(WebSocketEvents.class);
	
	@Inject
	Vertx vertx;
	
	ConcurrentHashMap<String, String> sharedProps;
	
	public void handleSearchTermUpdates(@Observes @TwitterConfig ConcurrentHashMap<String, String> sharedProps) {
		this.sharedProps = sharedProps;
		vertx.eventBus().consumer("com.redhat.consulting.config").handler(this::handleConfigMessage);
	}
	
	public void handleConfigMessage(Message<Object> msg) {
		if (msg.body() instanceof JsonObject) {
			JsonObject config = (JsonObject)msg.body();
			if (config.containsKey("searchTerms")) {
				sharedProps.put("searchTerms", config.getString("searchTerms"));
			}
			if (config.containsKey("twitterSearchDelay")) {
				sharedProps.put("twitterSearchDelay", config.getString("twitterSearchDelay"));
			}
		} else {
			LOG.warn("Received a config event without a JsonObject body");
		}
	}
}
