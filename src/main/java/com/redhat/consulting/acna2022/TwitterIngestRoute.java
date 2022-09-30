package com.redhat.consulting.acna2022;

import io.vertx.core.Vertx;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonb.JsonbDataFormat;
import org.apache.camel.component.twitter.search.TwitterSearchComponent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import twitter4j.Status;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

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
	String searchTerms = "Trump";
	
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
		
		fromF("twitter-search:%s?numberOfPages=10&delay=5000&type=direct", searchTerms)
				.process(e -> {
					var s = e.getIn().getBody(Status.class);
					e.getIn().setHeader("tid", s.getId());
					e.getIn().setHeader("content", s.getText());
					e.getIn().setHeader("handle", s.getUser().getScreenName());
					e.getIn().setHeader("url", format("https://twitter.com/%s/status/%s", s.getUser().getScreenName(), s.getId()));
					e.getIn().setBody("INSERT INTO tweets (id, content, handle, url) VALUES (:?tid, :?content, :?handle, :?url)");
				})
				.to("jdbc:default?useHeadersAsParameters=true")
				.to("log:debugQuery")
				.delay(simple("${random(100,1000)}"))
				.setBody(simple("""
            {
              "id": "${header.tid}",
              "url": "${header.url}",
              "content": "${header.content}",
              "handle": "${header.handle}"
            }
						"""))
				.to("vertx:com.redhat.consulting.tweet?pubSub=true")
				.to("log:TwitterFeedRoute");
	}
}
