package com.redhat.consulting.acna2022;

import io.vertx.core.json.JsonObject;
import org.apache.camel.builder.RouteBuilder;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

@ApplicationScoped
public class TwitterIngestRoute extends RouteBuilder {
	
	private static final Logger LOG = Logger.getLogger(TwitterIngestRoute.class);
	
	String searchTerms = "#ACNA2022,@ApacheCamel,@ApacheCon,@TheASF,@InfoSec812,@ApacheGroovy,@QuarkusIO";
	
	String twitterPollDelay = "5s";
	
	private AtomicLong lastId = new AtomicLong(0L);
	
	public void configure() throws Exception {
		
//		fromF("twitter-search:%s?greedy=true&type=direct&delay=%s", searchTerms, twitterPollDelay)
//				.to("log:rawTweet")
//				.process(e -> {
//					var s = e.getIn().getBody(Status.class);
//					e.getIn().setHeader("tid", s.getId());
//					e.getIn().setHeader("content", s.getText());
//					e.getIn().setHeader("handle", s.getUser().getScreenName());
//					e.getIn().setHeader("url", format("https://twitter.com/%s/status/%s", s.getUser().getScreenName(), s.getId()));
//					e.getIn().setBody("INSERT INTO tweets (id, content, handle, url) VALUES (:?tid, :?content, :?handle, :?url)");
//				})
//				.to("log:debugQuery")
//				.to("jdbc:default?useHeadersAsParameters=true")
//				.setBody(simple("""
//            {
//              "id": "${header.tid}",
//              "url": "${header.url}",
//              "content": "${header.content}",
//              "handle": "${header.handle}"
//            }
//						"""))
//				.to("vertx:com.redhat.consulting.tweet?pubSub=true")
//				.to("log:TwitterFeedRoute");
//
//		from("timer:aggregate?delay=0&period=10000")
//				.setBody(constant("SELECT COUNT(1) as count FROM tweets WHERE timestamp >= (now() - interval '10 seconds')"))
//				.to("jdbc:default?outputType=StreamList")
//				.split(body())
//				.process(e -> {
//					var rs = e.getIn().getBody(LinkedHashMap.class);
//					var count = (Long)rs.getOrDefault("count", 0);
//					var json = new JsonObject().put("count", count).put("timestamp", Instant.now().toEpochMilli());
//					e.getIn().setBody(json.encodePrettily());
//				})
//				.to("vertx:com.redhat.consulting.aggregate?pubSub=true");
	}
}
