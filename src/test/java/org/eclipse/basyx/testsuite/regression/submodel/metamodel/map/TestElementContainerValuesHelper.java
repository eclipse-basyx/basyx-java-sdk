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


package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.EntityType;
import org.eclipse.basyx.submodel.metamodel.facade.ElementContainerValuesHelper;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class TestElementContainerValuesHelper {

	@Test
	public void statementValue() {
		Entity enclosedEntity = new Entity("enclosed", EntityType.COMANAGEDENTITY);

		Property prop = new Property("prop", "123");

		SubmodelElementCollection smc = new SubmodelElementCollection("smc");
		smc.addSubmodelElement(prop);

		Entity rootEntity = new Entity("root", EntityType.COMANAGEDENTITY);
		rootEntity.setAsset(getDummyReference());
		rootEntity.setStatements(List.of(enclosedEntity, smc));

		Submodel sm = new Submodel("idShort", new CustomId("id"));
		sm.addSubmodelElement(rootEntity);
		
		Map<String, Object> values = ElementContainerValuesHelper.getSubmodelValue(sm);
		Map<String, Object> expected = createExpectedMap(enclosedEntity, smc, prop, rootEntity);

		assertEquals(expected, values);
	}

	private Map<String, Object> createExpectedMap(Entity enclosedEntity, SubmodelElementCollection smc, Property prop, Entity rootEntity) {
		return Map.ofEntries(
				Map.entry(rootEntity.getIdShort(), 
						Map.ofEntries(
								Map.entry(
										Entity.STATEMENT,
										Map.ofEntries(
												Map.entry(
														enclosedEntity.getIdShort(), 
														Map.ofEntries(
																Map.entry(
																		Entity.STATEMENT, 
																		Collections.emptyMap()
																		)
																)
														),
												Map.entry(
														smc.getIdShort(), 
														Map.ofEntries(
																Map.entry(
																		prop.getIdShort(), 
																		prop.getValue()
																		)
																)
														)

												)
								), Map.entry(
										Entity.ASSET, Map.ofEntries(
											Map.entry(Reference.KEY, Collections.emptyList())
											)
										)
								)
						)
				);
	}

	private IReference getDummyReference() {
		return new Reference(Collections.emptyList());
	}
}
