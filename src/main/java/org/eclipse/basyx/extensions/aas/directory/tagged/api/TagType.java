/* Copyright 2021 objective partner AG, all rights reserved */
package org.eclipse.basyx.extensions.aas.directory.tagged.api;

import org.eclipse.basyx.submodel.metamodel.enumhelper.StandardizedLiteralEnum;

/**
 * Enum that implements the {@link StandardizedLiteralEnum} interface. Used to
 * specify which type of tag is processed.
 * 
 * @author msiebert
 *
 */

public enum TagType implements StandardizedLiteralEnum {
	SUBMODEL("submodelTags"), AAS("tags");

	private String standardizedLiteral;

	TagType(String name) {
		this.standardizedLiteral = name;
	}

	@Override
	public String getStandardizedLiteral() {
		return this.standardizedLiteral;
	}
}
