package com.dk.niku.startup;

/**
 * @author dinesh.meel
 *
 */
public interface Server extends Runnable {

	@Override
	default void run() {
		try {
			start();
		} catch (Exception e) {
		}
	}

	ServerStatus status();

	void start() throws Exception;

	void stop() throws Exception;

	int port();

	boolean isStopping();
}