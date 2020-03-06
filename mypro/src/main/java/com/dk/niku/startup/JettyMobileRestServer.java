package com.dk.niku.startup;

import java.util.Arrays;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * @author dinesh.meel
 *
 */
public class JettyMobileRestServer implements com.dk.niku.startup.Server {
	private final int port;
	private ServerStatus status;
	private Server server;
	private static final String SHUTDOWN_TOKEN = "$hutd0wn@ETP@m3nt$";
	// curl -X POST http://localhost:8080/shutdown?token='$hutd0wn@ETP@m3nt$'

	public JettyMobileRestServer(int port) {
		this.port = port;
	}

	public ServerStatus status() {
		return status;
	}

	public void start() throws Exception {
		status = ServerStatus.STARTING;
		ServletContainer container = new ServletContainer(new JerseyResourceConfig());
		ServletHolder sh = new ServletHolder(container);
		ServletContextHandler context = new ServletContextHandler(
				ServletContextHandler.NO_SESSIONS | ServletContextHandler.NO_SECURITY);
		context.setContextPath("/");
		context.addServlet(sh, "/*");

		System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");
		WebAppContext webAppContext = JettyWebUtil.getWebAppContext();

		ShutdownHandler shutdown = new ShutdownHandler(SHUTDOWN_TOKEN, true, true);
		StatisticsHandler stats = new StatisticsHandler();
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { webAppContext, shutdown, context });
		stats.setHandler(handlers);

		server = new Server();
		server.setHandler(stats);
		HttpConfiguration httpConfiguration = new HttpConfiguration();
		ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
		httpConnector.setPort(port);
		httpConnector.setIdleTimeout(30000);
		Connector[] connectors = new Connector[] { httpConnector };
		server.setConnectors(connectors);
		server.start();
		status = ServerStatus.STARTED;
		System.out.println("Server started on port:" + port);
		server.join();
	}

	/**
	 * Add more elements to an Array.
	 * 
	 * @param array
	 * @param elements
	 * @return
	 */
	@SafeVarargs
	public static <T> T[] growArray(T[] array, T... elements) {
		T[] second = elements;
		return concatArraysOfSameType(array, second);
	}

	@SafeVarargs
	public static <T> T[] concatArraysOfSameType(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	public void stop() throws Exception {
		status = ServerStatus.STOPPING;
		// server.destroy();
		server.stop();
		status = ServerStatus.STOPPED;
	}

	public int port() {
		return port;
	}

	public boolean isStopping() {
		return status == ServerStatus.STOPPING;
	}
}
