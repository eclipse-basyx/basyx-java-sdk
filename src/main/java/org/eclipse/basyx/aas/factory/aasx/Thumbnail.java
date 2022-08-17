/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.aas.factory.aasx;

import java.io.InputStream;

/**
 * Class for configuring Thumbnail
 * for the AASX Package
 * 
 * @author danish
 *
 */
public class Thumbnail {
	
	private static final String FILENAME = "Thumbnail";
	
	private InputStream thumbnailStream;
	private ThumbnailExtension thumbnailExtension;
	
	public enum ThumbnailExtension {
	    PNG, JPG, JPEG;

	    @Override
	    public String toString() {
	        return name().toLowerCase();
	    }
	}
	
	public Thumbnail(ThumbnailExtension extension, InputStream thumbnailInputStream) {
		this.thumbnailExtension = extension;
		
		this.thumbnailStream = thumbnailInputStream;
	}
	
	public InputStream getThumbnailStream() {
		return thumbnailStream;
	}
	
	public void setThumbnailStream(InputStream thumbnailStream) {
		this.thumbnailStream = thumbnailStream;
	}
	
	public void setThumbnailExtension(ThumbnailExtension extension) {
		this.thumbnailExtension = extension;
	}
	
	public ThumbnailExtension getThumbnailExtension() {
		return thumbnailExtension;
	}
	
	public String getThumbnailFilename() {
		return FILENAME + "." + thumbnailExtension.toString();
	}
}
