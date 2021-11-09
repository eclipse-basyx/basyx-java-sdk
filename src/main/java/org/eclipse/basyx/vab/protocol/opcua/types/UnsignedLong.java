/*******************************************************************************
 * Copyright (C) 2021 Festo Didactic SE
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.opcua.types;

import java.math.BigInteger;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;

/** An unsigned 64-bit integer, matching the UInt64 type from OPC UA. */
public final class UnsignedLong {
    /** The lowest possible value of an unsigned 64-bit integer. */
    public static final BigInteger MIN_VALUE = ULong.MIN_VALUE;

    /** The highest possible value of an unsigned 64-bit integer. */
    public static final BigInteger MAX_VALUE = ULong.MAX_VALUE;

    /** A constant instance holding the lowest possible value of an unsigned 64-bit integer. */
    public static final UnsignedLong MIN = new UnsignedLong(ULong.MIN);

    /** A constant instance holding the highest possible value of an unsigned 64-bit integer. */
    public static final UnsignedLong MAX = new UnsignedLong(ULong.MAX);

    private final ULong value;

    /**
     * Creates a new instance from the given long value.
     *
     * @param value The value.
     *
     * @throws NumberFormatException if the value is outside the allowed range of {@link #MIN_VALUE} to
     *                               {@link #MAX_VALUE}.
     */
    public UnsignedLong(long value) throws NumberFormatException {
        this.value = ULong.valueOf(value);
    }

    /**
     * Creates a new instance from the given {@link BigInteger}.
     *
     * @param value The value.
     *
     * @throws NumberFormatException if the value is outside the allowed range of {@link #MIN_VALUE} to
     *                               {@link #MAX_VALUE}.
     */
    public UnsignedLong(BigInteger value) throws NumberFormatException {
        this.value = ULong.valueOf(value);
    }

    /**
     * Creates a new instance by parsing the given string.
     *
     * @param value A decimal string representation of the value.
     *
     * @throws NumberFormatException if the string doesn't contain a decimal integer or the value is
     *                               outside the allowed range of an unsigned 64 bit integer.
     */
    public UnsignedLong(String value) throws NumberFormatException {
        this.value = ULong.valueOf(value);
    }

    /**
     * Creates a new instance with the given internal value. Client code should normally not need to use
     * this.
     *
     * @param value The internal value.
     */
    public UnsignedLong(ULong value) {
        this.value = value;
    }

    /**
     * Gets the internal representation of this value. Client code should not normally use this.
     *
     * @return The internal representation of this value.
     */
    public ULong getInternalValue() {
        return value;
    }

    /**
     * Adds another {@link UnsignedLong} to this one.
     *
     * @param other The value to add.
     *
     * @return A new instance holding the sum of this one and <code>other</code>.
     *
     * @throws NumberFormatException if the result is outside the allowed range of {@link #MIN_VALUE} to
     *                               {@link #MAX_VALUE}.
     */
    public UnsignedLong add(UnsignedLong other) throws NumberFormatException {
        return new UnsignedLong(value.add(other.toLong()));
    }

    /**
     * Adds a long integer to this {@link UnsignedLong}.
     *
     * @param other The value to add.
     *
     * @return A new instance holding the sum of this one and <code>other</code>.
     *
     * @throws NumberFormatException if the result is outside the allowed range of {@link #MIN_VALUE} to
     *                               {@link #MAX_VALUE}.
     */
    public UnsignedLong add(long other) throws NumberFormatException {
        return new UnsignedLong(value.add(other));
    }

    /**
     * Subtracts another {@link UnsignedLong} from this one.
     *
     * @param other The value to subtract.
     *
     * @return A new instance holding the difference between this one and <code>other</code>.
     *
     * @throws NumberFormatException if the result is outside the allowed range of {@link #MIN_VALUE} to
     *                               {@link #MAX_VALUE}.
     */
    public UnsignedLong subtract(UnsignedLong other) throws NumberFormatException {
        return new UnsignedLong(value.subtract(other.toLong()));
    }

    /**
     * Subtracts a long integer from this {@link UnsignedLong}.
     *
     * @param other The value to subtract.
     *
     * @return A new instance holding the difference between this one and <code>other</code>.
     *
     * @throws NumberFormatException if the result is outside the allowed range of {@link #MIN_VALUE} to
     *                               {@link #MAX_VALUE}.
     */
    public UnsignedLong subtract(long other) throws NumberFormatException {
        return new UnsignedLong(value.subtract(other));
    }

    /**
     * Gets a long with this object's value.
     *
     * <p>
     * The returned value is signed. Calling code needs to take care to stick to unsigned semantics when
     * dealing with it.
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
     * The returned value is signed. Calling code needs to take care to stick to unsigned semantics when
     * dealing with it.
     *
     * @return A {@link BigInteger} with this object's value.
     */
    public BigInteger toBigInteger() {
        return value.toBigInteger();
    }
}