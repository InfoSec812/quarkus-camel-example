package com.redhat.consulting.acna2022;

import com.redhat.consulting.acna2022.models.Tweet;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApplicationScoped
public class WebsocketInitializer {
	
	private static final Logger LOG = Logger.getLogger(WebsocketInitializer.class);
	
	@Inject
	Vertx vertx;
	
	public void initVertx(@Observes Router router) {
		LOG.info("Adding EventBus websocket handler");
		
		var permitted = new PermittedOptions().setAddressRegex(".*");
		
		var sockJsOpts = new SockJSBridgeOptions()
				                                        .addInboundPermitted(permitted)
				                                        .addOutboundPermitted(permitted);
		var sockJSHandler = SockJSHandler.create(vertx);
		router.route("/eventbus/*").subRouter(sockJSHandler.bridge(sockJsOpts));
	}
}
