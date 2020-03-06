package com.dk.niku.startup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * @author dinesh.meel
 *
 */
public class JettyWebUtil {

	// private static final Logger logger =
	// LoggerFactory.getLogger(JettyWebUtil.class);

	private static URI getWebRootResourceUri() throws FileNotFoundException, URISyntaxException {
		URL indexUri = JettyMobileRestServer.class.getResource("/webroot/");
		if (indexUri == null) {
			throw new FileNotFoundException("Unable to find resource " + "/webroot/");
		}
		// Points to wherever /webroot/ (the resource) is
		return indexUri.toURI();
	}

	/**
	 * Establish Scratch directory for the servlet context (used by JSP compilation)
	 */
	private static File getScratchDir() throws IOException {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File scratchDir = new File(tempDir.toString(), "embedded-jetty-jsp");

		if (!scratchDir.exists()) {
			if (!scratchDir.mkdirs()) {
				throw new IOException("Unable to create scratch directory: " + scratchDir);
			}
		}
		return scratchDir;
	}

	/**
	 * Ensure the jsp engine is initialized correctly
	 */
	private static List<ContainerInitializer> jspInitializers() {
		JettyJasperInitializer sci = new JettyJasperInitializer();
		ContainerInitializer initializer = new ContainerInitializer(sci, null);
		List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
		initializers.add(initializer);
		return initializers;
	}

	/**
	 * Set Classloader of Context to be sane (needed for JSTL) JSP requires a
	 * non-System classloader, this simply wraps the embedded System classloader in
	 * a way that makes it suitable for JSP to use
	 */
	private static ClassLoader getUrlClassLoader() {
		ClassLoader jspClassLoader = new URLClassLoader(new URL[0], JettyMobileRestServer.class.getClassLoader());
		return jspClassLoader;
	}

	/**
	 * Create JSP Servlet (must be named "jsp")
	 */
	private static ServletHolder jspServletHolder() {
		ServletHolder holderJsp = new ServletHolder("jsp", JettyJspServlet.class);
		holderJsp.setInitOrder(0);
		holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
		holderJsp.setInitParameter("fork", "false");
		holderJsp.setInitParameter("xpoweredBy", "false");
		holderJsp.setInitParameter("compilerTargetVM", "1.8");
		holderJsp.setInitParameter("compilerSourceVM", "1.8");
		holderJsp.setInitParameter("keepgenerated", "true");
		return holderJsp;
	}

	/**
	 * Create Default Servlet (must be named "default")
	 */
	private static ServletHolder defaultServletHolder(URI baseUri) {
		ServletHolder holderDefault = new ServletHolder("default", DefaultServlet.class);
		// logger.info("Base URI: " + baseUri);
		holderDefault.setInitParameter("resourceBase", baseUri.toASCIIString());
		holderDefault.setInitParameter("dirAllowed", "true");
		return holderDefault;
	}

	public static WebAppContext getWebAppContext() throws URISyntaxException, IOException {
		URI baseUri = getWebRootResourceUri();
		File scratchDir = getScratchDir();

		WebAppContext webContext = new WebAppContext();
		webContext.setContextPath("/web");
		webContext.setAttribute("javax.servlet.context.tempdir", scratchDir);
		webContext.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
				".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/.*taglibs.*\\.jar$");
		webContext.setResourceBase(baseUri.toASCIIString());
		webContext.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
		webContext.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
		webContext.addBean(new ServletContainerInitializersStarter(webContext), true);
		webContext.setClassLoader(getUrlClassLoader());
		webContext.addServlet(jspServletHolder(), "/jsp/*");

		// Add Application Servlets
		webContext.addServlet(appJspServletHolder(), "/mypro/*");

		webContext.addServlet(defaultServletHolder(baseUri), "/static/*");
		return webContext;
	}

	private static ServletHolder appJspServletHolder() {
		ServletContainer container = new ServletContainer(new JspResourceConfig());
		ServletHolder sh = new ServletHolder(container);
		return sh;
	}

	private static class JspResourceConfig extends ResourceConfig {
		public JspResourceConfig() {
			packages(Resource.class.getPackage().getName());
		}
	}

}
