package com.redhat.consulting.acna2022;

import com.redhat.consulting.acna2022.models.Tweet;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.StreamCache;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonb.JsonbDataFormat;
import org.apache.camel.component.twitter.search.TwitterSearchComponent;
import org.apache.camel.converter.stream.InputStreamCache;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import twitter4j.Status;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class TwitterIngestRoute extends RouteBuilder {
	
	private static final Logger LOG = Logger.getLogger(TwitterIngestRoute.class);
	
	@ConfigProperty(name = "twitter.access.token", defaultValue = "")
	String accessToken;
	
	@ConfigProperty(name = "twitter.access.secret", defaultValue = "")
	String accessTokenSecret;
	
	@ConfigProperty(name = "twitter.consumer.key", defaultValue = "")
	String consumerKey;
	
	@ConfigProperty(name = "twitter.consumer.secret", defaultValue = "")
	String consumerSecret;
	
//	String searchTerms = "#ACNA2022,@ApacheCamel,@ApacheCon,@TheASF,@InfoSec812,@ApacheGroovy,@QuarkusIO";
	String searchTerms = "#TrumpIsGuilty";
	
	String twitterPollDelay = "5s";
	
	private AtomicLong lastId = new AtomicLong(0L);
	
	RouteBuilder currentRoute;
	
	@Inject
	Vertx vertx;
	
	@Inject
	CamelContext context;
	
	public void configure() throws Exception {
		
		TwitterSearchComponent twitterSearchComponent = context.getComponent("twitter-search", TwitterSearchComponent.class);
		
		twitterSearchComponent.setAccessToken(accessToken);
		twitterSearchComponent.setAccessTokenSecret(accessTokenSecret);
		twitterSearchComponent.setConsumerKey(consumerKey);
		twitterSearchComponent.setConsumerSecret(consumerSecret);
		
		JsonbDataFormat jsonbDataFormat = new JsonbDataFormat();
		
		fromF("twitter-search:%s?delay=%s&sinceId=%d", searchTerms, twitterPollDelay, lastId.get())
				.process(e -> {
					Status s = e.getIn().getBody(Status.class);
					Tweet t = new Tweet(s);
					lastId.set(s.getId());
					e.getIn().setBody(t);
				})
				.to("jpa://com.redhat.consulting.acna2022.models.Tweet")
				.process(e -> {
					var tweet = e.getIn().getBody(Tweet.class);
					var json = JsonbBuilder.create().toJson(tweet);
					e.getIn().setBody(json);
				})
				.to("vertx:com.redhat.consulting.tweet?pubSub=true")
				.to("log:TwitterFeedRoute");
	}
}
