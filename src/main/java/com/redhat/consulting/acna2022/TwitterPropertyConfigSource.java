package com.redhat.consulting.acna2022;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static java.lang.String.format;
import static java.lang.System.getenv;

/**
 * Allows for reading the of Twitter API credentials from a directory located outside of
 * our source repository.
 *
 * Format:
 *    twitter.access.token=000000000-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 *    twitter.access.secret=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 *    twitter.consumer.key=xxxxxxxxxxxxxxxxxxxxxxxxx
 *    twitter.consumer.secret=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 */
public class TwitterPropertyConfigSource implements ConfigSource {
	
	private static final Logger LOG = Logger.getLogger(TwitterPropertyConfigSource.class);
	
	private final Map<String,String> props = new HashMap<>();
	
	public TwitterPropertyConfigSource() {
		super();
		try (FileInputStream fis = new FileInputStream(format("%s/.twitter.properties", getenv().getOrDefault("HOME", "~")))) {
			var load = new Properties();
			load.load(fis);
			for (Map.Entry<Object, Object> e: load.entrySet()) {
				String key = (String)e.getKey();
				String val = (String)e.getValue();
				props.put(key, val);
				System.setProperty(key, val);
			}
		} catch (Exception e) {
			LOG.warn(e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public Map<String, String> getProperties() {
		return props;
	}
	
	@Override
	public Set<String> getPropertyNames() {
		return props.keySet();
	}
	
	@Override
	public int getOrdinal() {
		return 390;
	}
	
	@Override
	public String getValue(String s) {
		return props.get(s);
	}
	
	@Override
	public String getName() {
		return "TwitterAPIConfig";
	}
}
