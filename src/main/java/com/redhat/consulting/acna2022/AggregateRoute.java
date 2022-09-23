package com.redhat.consulting.acna2022;

import io.vertx.core.json.JsonObject;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.LinkedHashMap;

@ApplicationScoped
public class AggregateRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		from("timer:aggregate?delay=0&period=10000")
				.setBody(constant("SELECT COUNT(1) as count FROM tweets WHERE timestamp >= (now() - interval '10 seconds')"))
				.to("jdbc:default?outputType=StreamList")
				.split(body())
				.process(e -> {
					var rs = e.getIn().getBody(LinkedHashMap.class);
					var count = (Long)rs.getOrDefault("count", 0);
					var json = new JsonObject().put("count", count).put("timestamp", Instant.now().toEpochMilli());
					e.getIn().setBody(json.encodePrettily());
				})
				.to("vertx:com.redhat.consulting.aggregate?pubSub=true");
	}
}
