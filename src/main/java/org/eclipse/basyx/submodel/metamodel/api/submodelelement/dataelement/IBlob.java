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
package org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement;

/**
 * A BLOB is a data element that represents a file that is contained with its
 * source code in the value attribute.
 * 
 * @author rajashek, schnicke
 *
 */
public interface IBlob extends IDataElement {
	/**
	 * Gets the value of the Blob instance of a blob data element. The returned
	 * value is Base64 encoded.
	 * 
	 * @return
	 */
	@Override
	public String getValue();

	/**
	 * Sets a Base64 encoded value of the BLOB instance of a blob data element.
	 * 
	 * @param value
	 */
	public void setValue(String value);

	/**
	 * Gets the value of the Blob instance of a blob data element.
	 * 
	 * @return
	 */
	public byte[] getByteArrayValue();

	/**
	 * Sets the value of the Blob instance of a blob data element.
	 * 
	 * @param value
	 */
	public void setByteArrayValue(byte[] value);

	/**
	 * Returns the UTF8 String representation of the byte array BLOB value
	 *
	 * @return
	 */
	public String getUTF8String();

	/**
	 * Sets an UTF8 string as an encoded byte array in the BLOB data element value
	 * 
	 * @param text
	 */
	public void setUTF8String(String text);

	/**
	 * Gets the mime type of the content of the BLOB. The mime type states which
	 * file extension the file has. <br>
	 * Valid values are e.g. “application/json”, “application/xls”, ”image/jpg”.
	 * <br>
	 * The allowed values are defined as in RFC2046.
	 * 
	 * @return
	 */
	public String getMimeType();
}
