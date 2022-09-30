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

import static java.lang.String.format;

@ApplicationScoped
public class TwitterIngestRoute extends RouteBuilder {
	
	private static final Logger LOG = Logger.getLogger(TwitterIngestRoute.class);
	
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
		
		fromF("twitter-search:%s?greedy=true&type=direct&delay=%s&sinceId=%d", searchTerms, twitterPollDelay, lastId.get())
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
