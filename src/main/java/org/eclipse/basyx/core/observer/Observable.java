/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package org.eclipse.basyx.core.observer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Generic implementation of an Observable.
 * This class contains all common operations an Observable is supposed to do.
 * Java generics is used to specify which type of Observable is required
 * @author haque
 *
 * @param <T> can be Observers which extends IObserver
 */
public class Observable<T extends IObserver> {

	public Collection<T> observers = new ArrayList<T>();

	/**
	 * Adds an observer to the subscriber list
	 * 
	 * @param observer the observer to be added
	 */
	public void addObserver(T observer) {
		observers.add(observer);
	}
	
	/**
	 * Removes an observer from the subscriber list
	 * 
	 * @param observer the observer to be removed
	 * @return true if the observer was found and removed; false otherwise
	 */
	public boolean removeObserver(T observer) {
		return observers.remove(observer);
	}
}
