package com.dk.niku.startup;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author dinesh.meel
 *
 */
public class JerseyResourceConfig extends ResourceConfig {

	public JerseyResourceConfig() {
		packages(MyResource.class.getPackage().getName());
		register(JacksonFeature.class);
		register(MultiPartFeature.class);
	}
}
