package com.redhat.consulting.acna2022.models;

import twitter4j.Status;
import twitter4j.TweetEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tweets")
public class Tweet {
	
	@Id
	@GeneratedValue
	private UUID id = UUID.randomUUID();
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	
	
	public Tweet() {
		super();
	}
	
	public Tweet(Status tweet) {
		super();
		this.id = UUID.randomUUID();
		this.content = tweet.getText();
		this.timestamp = tweet.getCreatedAt();
	}
}
