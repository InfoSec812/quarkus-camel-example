package com.redhat.consulting.acna2022;

import com.redhat.consulting.acna2022.models.Tweet;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.twitter.TwitterConstants;
import org.apache.camel.component.twitter.search.TwitterSearchComponent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import twitter4j.Status;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class TwitterIngestRoute extends RouteBuilder {
	
	@Inject
	Logger log;
	
	@ConfigProperty(name = "twitter.access.token", defaultValue = "")
	String accessToken;
	
	@ConfigProperty(name = "twitter.access.secret", defaultValue = "")
	String accessTokenSecret;
	
	@ConfigProperty(name = "twitter.consumer.key", defaultValue = "")
	String consumerKey;
	
	@ConfigProperty(name = "twitter.consumer.secret", defaultValue = "")
	String consumerSecret;
	
	@Inject
	@Named("sharedProps")
	ConcurrentHashMap<String, String> sharedProps;
	
	private AtomicLong lastId = new AtomicLong(0L);
	
	@Override
	public void configure() throws Exception {
		
		log.debugf("%s\n%s\n%s\n%s", accessToken, accessTokenSecret, consumerKey, consumerSecret);
		
		TwitterSearchComponent twitterSearchComponent = getContext().getComponent("twitter-search", TwitterSearchComponent.class);
		
		twitterSearchComponent.setAccessToken(accessToken);
		twitterSearchComponent.setAccessTokenSecret(accessTokenSecret);
		twitterSearchComponent.setConsumerKey(consumerKey);
		twitterSearchComponent.setConsumerSecret(consumerSecret);
		
		fromF("timer:poller?delay=%s", sharedProps.get("twitterSearchDelay"))
				.setHeader(TwitterConstants.TWITTER_KEYWORDS, constant(sharedProps.get("searchTerms")))
				.setHeader(TwitterConstants.TWITTER_SINCEID, constant(lastId.get()))
				.to("log:Timer")
				.pollEnrich("twitter-search")
				.transform(bodyAs(Status.class))
				.process(e -> {
					Status s = e.getIn().getBody(Status.class);
					Tweet t = new Tweet(s);
					lastId.set(s.getId());
					e.getIn().setBody(t);
				})
				.to("jpa:com.redhat.consulting.acna2022.models.Tweet")
				.marshal().json()
				.to("vertx:com.redhat.consulting.tweet")
				.to("log:TwitterFeedRoute");
	}
}
