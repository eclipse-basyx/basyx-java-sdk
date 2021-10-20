/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.service.api;



/**
 * Runnable BaSyx service
 * 
 * @author kuhn
 *
 */
public interface BaSyxService {

	
	/**
	 * Start the runnable
	 */
	public void start();
	
	
	/**
	 * Stop the runnable
	 */
	public void stop();
	
	
	/**
	 * Wait for end of runnable
	 */
	public void waitFor(); 
	
	
	/**
	 * Change the runnable name
	 */
	public BaSyxService setName(String newName);
	
	
	/**
	 * Get runnable name
	 */
	public String getName();
	
	
	/**
	 * Indicate if this service has ended
	 */
	public boolean hasEnded();
}
