package com.dk.niku.startup;

/**
 * @author dinesh.meel
 *
 */
public class Launcher {
	private static volatile Server server;

	public static Server server() {
		return server;
	}

	public static final void launch() {
		try {
			server = new JettyMobileRestServer(8080);
			server.start();
		} catch (Exception e) {
			System.exit(-1);
		}
	}

	public static void main(String[] args) throws Exception {
		launch();
	}
}
