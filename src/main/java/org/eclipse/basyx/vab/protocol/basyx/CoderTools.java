/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.vab.protocol.basyx;

import java.nio.charset.StandardCharsets;



/**
 * Byte en/decoding tools
 * 
 * @author kuhn
 *
 */
public class CoderTools {

	
	/**
	 * Get a uint32 from a byte array with offset, MSB is first bit
	 */
	public static int getInt32(byte[] data, int offset) {
		long result = 0;
		
		result += data[offset+3] & 0xFF; result = result << 8;
		result += data[offset+2] & 0xFF; result = result << 8;
		result += data[offset+1] & 0xFF; result = result << 8;
		result += data[offset+0] & 0xFF; 
		
		return (int) result;
	}


	/**
	 * Copy an uint32 to a byte array at given offset, MSB is first bit
	 */
	public static byte[] setInt32(byte[] data, int offset, int value) {
		data[offset+3] = (byte) ((value >> 24) & 0xFF);
		data[offset+2] = (byte) ((value >> 16) & 0xFF);
		data[offset+1] = (byte) ((value >>  8) & 0xFF);
		data[offset+0] = (byte) (value         & 0xFF);
		
		return data;
	}

	
	
	/**
	 * Get a uint16 from a byte array with offset, MSB is first bit
	 */
	public static int getInt16(byte[] data, int offset) {
		int result = 0;
		
		result += data[offset+1] & 0xFF; result = result << 8;
		result += data[offset+0] & 0xFF;
		
		return result;
	}

	
	/**
	 * Copy an uint16 to a byte array at given offset, MSB is first bit
	 */
	public static byte[] setInt16(byte[] data, int offset, int value) {
		data[offset+1] = (byte) ((value >>  8) & 0xFF);
		data[offset+0] = (byte) (value         & 0xFF);
		
		return data;
	}

	
	
	/**
	 * Get a uint8 from a byte array with offset, MSB is first bit
	 */
	public static int getInt8(byte[] data, int offset) {
		int result = 0;
		
		result += data[offset+0] & 0xFF; 
		
		return result;
	}

	
	/**
	 * Copy an uint8 to a byte array at given offset, MSB is first bit
	 */
	public static byte[] setInt8(byte[] data, int offset, int value) {
		data[offset+0] = (byte) (value & 0xFF);
		
		return data;
	}


	
	/**
	 * Get all remaining bytes from byte array
	 */
	public static byte[] getByteArray(byte[] data, int offset) {
		byte[] result = new byte[data.length-offset];
		
		for (int i=offset; i<data.length; i++) result[i-offset]=data[i]; 
		
		return result;
	}

	
	/**
	 * Get a number of bytes from byte array
	 */
	public static byte[] getByteArray(byte[] data, int offset, int length) {
		byte[] result = new byte[length];
		
		for (int i=offset; i<offset+length; i++) result[i-offset]=data[i]; 
		
		return result;
	}

	
	/**
	 * Copy bytes to byte array
	 */
	public static byte[] setByteArray(byte[] data, int offset, byte[] newValue) {
		for (int i=0; i<newValue.length; i++) data[i+offset]=newValue[i]; 
		
		return data;
	}


	/**
	 * Copy bytes to byte array
	 */
	public static byte[] setByteArray(byte[] data, int offset, byte[] newValue, int len) {
		for (int i=0; i<len; i++) data[i+offset]=newValue[i]; 
		
		return data;
	}

	
	/**
	 * Extract a string from a byte array
	 */
	public static String getString(byte[] data, int offset) {
		int cnt = 0;
		StringBuffer str = new StringBuffer();
		
		while (data[offset+cnt] != 0) {
			str.append(new String(new byte[] {(byte) data[offset+cnt]}, StandardCharsets.US_ASCII));
			cnt++;
		}
		
		return str.toString();
	}


	/**
	 * Put a string into a byte array
	 */
	public static byte[] setString(byte[] data, int offset, String newValue) {
		for (int i=0; i<newValue.length(); i++) {
			data[i+offset] = (byte) newValue.charAt(i);
		}
		
		return data;
	}
}


