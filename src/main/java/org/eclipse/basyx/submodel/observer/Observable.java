/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

* 
* SPDX-License-Identifier: MIT
******************************************************************************/

package org.eclipse.basyx.submodel.observer;

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
