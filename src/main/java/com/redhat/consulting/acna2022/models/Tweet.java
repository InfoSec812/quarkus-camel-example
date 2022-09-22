package com.redhat.consulting.acna2022.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import twitter4j.Status;
import twitter4j.TweetEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tweets")
public class Tweet extends PanacheEntityBase {
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getHandle() {
		return handle;
	}
	
	public void setHandle(String handle) {
		this.handle = handle;
	}
	
	@Id
	private Long id;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	
	private String handle;
	
	private String url;
	
	public Tweet() {
		super();
	}
	
	public String getUrl() {
		return url;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tweet tweet = (Tweet) o;
		return id.equals(tweet.id) && content.equals(tweet.content) && timestamp.equals(tweet.timestamp) && handle.equals(tweet.handle) && url.equals(tweet.url);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, content, timestamp, handle, url);
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Tweet(Status tweet) {
		super();
		this.id = tweet.getId();
		this.url = String.format("https://twitter.com/%s/status/%s", tweet.getUser().getScreenName(), tweet.getId());
		this.content = tweet.getText();
		this.timestamp = tweet.getCreatedAt();
		this.handle = tweet.getUser().getScreenName();
	}
}
