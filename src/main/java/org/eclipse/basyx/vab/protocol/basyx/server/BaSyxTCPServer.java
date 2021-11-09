/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.basyx.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.service.api.BaSyxService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * BaSyx TCP server thread
 * 
 * @author kuhn
 *
 */
public class BaSyxTCPServer<T extends IModelProvider> implements Runnable, BaSyxService {
	
	private static Logger logger = LoggerFactory.getLogger(BaSyxTCPServer.class);

	
	/**
	 * Store server socket channel instance
	 */
	protected ServerSocketChannel serverSockChannel = null;

	
	/**
	 * Reference to IModelProvider backend
	 */
	protected T providerBackend = null;

	
	/**
	 * Exit flag
	 */
	protected boolean exit = false;
	
	
	/**
	 * Store thread
	 */
	protected Thread thread = null;
	
	
	/**
	 * Store name
	 */
	protected String name = null;
	
	
	
	/**
	 * Constructor
	 */
	public BaSyxTCPServer(T modelProviderBackend, int serverPort) {
		// Store model provider backend reference
		providerBackend = modelProviderBackend;

		// Create communication channel
		try {
			// The channel should listen on all interfaces, binding on 127.0.0.1 prohibits remote communication
			InetAddress hostIPAddress = InetAddress.getByName("0.0.0.0");

			// Server socket channel
			serverSockChannel = ServerSocketChannel.open();
			serverSockChannel.configureBlocking(true);
			serverSockChannel.socket().bind(new InetSocketAddress(hostIPAddress, serverPort));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception in BaSyxTCPServer", e);
		}
	}

	
	/**
	 * Default constructor without port number
	 */
	public BaSyxTCPServer(T modelProviderBackend) {
		// Invoke 'this' constructor
		this(modelProviderBackend, 6998);
	}

	
	/**
	 * Thread main method
	 */
	@Override
	public void run() {
		// Accept connections
		while (!exit) {
			// Accept incoming connections
			acceptIncomingConnection();
		}
	}
	
	
	/**
	 * Accept an incoming connection
	 */
	public void acceptIncomingConnection() {
		// Implement exception handling
		try {
			// Store socket channel
			SocketChannel communicationSocket = null;

			// Wait for connections
			try {
				communicationSocket = serverSockChannel.accept(); 
			} catch (SocketException e) {
				logger.error("Exception in acceptIncomingConnection", e);
				// End process; Server socket has been closed by shutdown
				exit = true; return;
			}

			// Handle an incoming connection
			// - Create and connect BaSyx client provider for communication socket
			VABBaSyxTCPInterface<T> tcpProvider = new VABBaSyxTCPInterface<T>(providerBackend, communicationSocket);
			// - Start TCP provider
			tcpProvider.start();
		} catch (IOException e) {
			// Indicate exception only iff exit flag is false
			if (!exit) logger.error("Exception in acceptIncomingConnection", e);

			// Return
			return;
		}
	}
	
	
	/**
	 * End server
	 */
	protected void shutdown() {
		// End thread
		exit = true;
		
		// Handle IOException
		try {
			// Close stream
			serverSockChannel.close();
		} catch (IOException e) {
			// Indicate exception
			logger.error("Exception in shutdown", e);
		}			
	}


	/**
	 * Start the server
	 */
	@Override
	public void start() {
		// Create thread
		thread = new Thread(this);
		
		// Start thread
		thread.start();
	}


	/**
	 * Stop the server and block until the server thread is finished
	 */
	@Override
	public void stop() {
		// Shutdown thread
		shutdown();
		
		// Wait for thread end
		try {thread.join();} catch (InterruptedException e) {logger.error("Exception in stop", e);}
	}


	/**
	 * Change service name
	 */
	@Override
	public BaSyxService setName(String newName) {
		// Store name
		name = newName;
		
		// Return 'this' instance
		return this;
	}


	/**
	 * Return service name
	 */
	@Override
	public String getName() {
		// Return service name
		return name;
	}
	
	
	/**
	 * Wait for end of runnable
	 */
	public void waitFor() {
		// Wait for thread end
		try {thread.join();} catch (InterruptedException e) {logger.error("Exception in waitFor", e);}		
	}
	
	
	/**
	 * Indicate if this service has ended
	 */
	public boolean hasEnded() {
		// Return exit flag that indicates requested end of service execution
		return !serverSockChannel.isOpen();
	}
}

