/*******************************************************************************
 * Copyright (C) 2021 Festo Didactic SE
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
package org.eclipse.basyx.vab.protocol.opcua.types;

import java.math.BigInteger;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;

/** An unsigned 16-bit integer, matching the UInt16 type from OPC UA. */
public final class UnsignedShort {
	/** The lowest possible value of an unsigned 16-bit integer. */
	public static final int MIN_VALUE = UShort.MIN_VALUE;

	/** The highest possible value of an unsigned 16-bit integer. */
	public static final int MAX_VALUE = UShort.MAX_VALUE;

	/**
	 * A constant instance holding the lowest possible value of an unsigned 16-bit
	 * integer.
	 */
	public static final UnsignedShort MIN = new UnsignedShort(UShort.MIN);

	/**
	 * A constant instance holding the highest possible value of an unsigned 16-bit
	 * integer.
	 */
	public static final UnsignedShort MAX = new UnsignedShort(UShort.MAX);

	private final UShort value;

	/**
	 * Creates a new instance from the given short integer.
	 *
	 * @param value
	 *            The value.
	 *
	 * @throws NumberFormatException
	 *             if the value is outside the allowed range of {@link #MIN_VALUE}
	 *             to {@link #MAX_VALUE}.
	 */
	public UnsignedShort(short value) throws NumberFormatException {
		this.value = UShort.valueOf(value);
	}

	/**
	 * Creates a new instance from the given integer.
	 *
	 * @param value
	 *            The value.
	 *
	 * @throws NumberFormatException
	 *             if the value is outside the allowed range of {@link #MIN_VALUE}
	 *             to {@link #MAX_VALUE}.
	 */
	public UnsignedShort(int value) throws NumberFormatException {
		this.value = UShort.valueOf(value);
	}

	/**
	 * Creates a new instance by parsing the given string.
	 *
	 * @param value
	 *            A decimal string representation of the value.
	 *
	 * @throws NumberFormatException
	 *             if the string doesn't contain a decimal integer or the value is
	 *             outside the allowed range of an unsigned 64 bit integer.
	 */
	public UnsignedShort(String value) throws NumberFormatException {
		this.value = UShort.valueOf(value);
	}

	/**
	 * Creates a new instance with the given internal value. Client code should
	 * normally not need to use this.
	 *
	 * @param value
	 *            The internal value.
	 */
	public UnsignedShort(UShort value) {
		this.value = value;
	}

	/**
	 * Gets the internal representation of this value. Client code should not
	 * normally use this.
	 *
	 * @return The internal representation of this value.
	 */
	public UShort getInternalValue() {
		return value;
	}

	/**
	 * Adds another {@link UnsignedShort} to this one.
	 *
	 * @param other
	 *            The value to add.
	 *
	 * @return A new instance holding the sum of this one and <code>other</code>.
	 *
	 * @throws NumberFormatException
	 *             if the result is outside the allowed range of {@link #MIN_VALUE}
	 *             to {@link #MAX_VALUE}.
	 */
	public UnsignedShort add(UnsignedShort other) throws NumberFormatException {
		return new UnsignedShort(value.add(other.toShort()));
	}

	/**
	 * Adds a short integer to this {@link UnsignedShort}.
	 *
	 * @param other
	 *            The value to add.
	 *
	 * @return A new instance holding the sum of this one and <code>other</code>.
	 *
	 * @throws NumberFormatException
	 *             if the result is outside the allowed range of {@link #MIN_VALUE}
	 *             to {@link #MAX_VALUE}.
	 */
	public UnsignedShort add(short other) throws NumberFormatException {
		return new UnsignedShort(value.add(other));
	}

	/**
	 * Subtracts another {@link UnsignedShort} from this one.
	 *
	 * @param other
	 *            The value to subtract.
	 *
	 * @return A new instance holding the difference between this one and
	 *         <code>other</code>.
	 *
	 * @throws NumberFormatException
	 *             if the result is outside the allowed range of {@link #MIN_VALUE}
	 *             to {@link #MAX_VALUE}.
	 */
	public UnsignedShort subtract(UnsignedShort other) throws NumberFormatException {
		return new UnsignedShort(value.subtract(other.toShort()));
	}

	/**
	 * Subtracts a short integer from this {@link UnsignedShort}.
	 *
	 * @param other
	 *            The value to subtract.
	 *
	 * @return A new instance holding the difference between this one and
	 *         <code>other</code>.
	 *
	 * @throws NumberFormatException
	 *             if the result is outside the allowed range of {@link #MIN_VALUE}
	 *             to {@link #MAX_VALUE}.
	 */
	public UnsignedShort subtract(short other) throws NumberFormatException {
		return new UnsignedShort(value.subtract(other));
	}

	/**
	 * Gets a short integer with this object's value.
	 *
	 * <p>
	 * The returned value is signed. Calling code needs to take care to stick to
	 * unsigned semantics when dealing with it.
	 *
	 * @return A short integer with this object's value.
	 */
	public short toShort() {
		return value.shortValue();
	}

	/**
	 * Gets an integer with this object's value.
	 *
	 * <p>
	 * The returned value is signed. Calling code needs to take care to stick to
	 * unsigned semantics when dealing with it.
	 *
	 * @return An integer with this object's value.
	 */
	public int toInt() {
		return value.intValue();
	}

	/**
	 * Gets a long with this object's value.
	 *
	 * <p>
	 * The returned value is signed. Calling code needs to take care to stick to
	 * unsigned semantics when dealing with it.
	 *
	 * @return A long with this object's value.
	 */
	public long toLong() {
		return value.longValue();
	}

	/**
	 * Gets a {@link BigInteger} with this object's value.
	 *
	 * <p>
	 * The returned value is signed. Calling code needs to take care to stick to
	 * unsigned semantics when dealing with it.
	 *
	 * @return A {@link BigInteger} with this object's value.
	 */
	public BigInteger toBigInteger() {
		return value.toBigInteger();
	}
}