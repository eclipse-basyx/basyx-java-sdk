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
package org.eclipse.basyx.testsuite.regression.aas.metamodel.map.parts;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.EmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.AdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link Asset} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestAsset {
	private static final Reference REFERENCE = new Reference(new Identifier(IdentifierType.CUSTOM, "testValue"), KeyElements.ASSET, false);
	
	private Asset asset;
	
	@Before
	public void buildAsset() {
		asset = new Asset(REFERENCE);
	}
	
	@Test
	public void testConstructor() {
		assertEquals(REFERENCE, asset.getAssetIdentificationModel());
	}
	
	@Test
	public void testSetDataSpecificationReferences() {
		Collection<IReference> refs = Collections.singleton(REFERENCE);
		asset.setDataSpecificationReferences(refs);
		assertEquals(refs, asset.getDataSpecificationReferences());
	}
	
	@Test
	public void testSetEmbeddedDataSpecifications() {
		EmbeddedDataSpecification embeddedDataSpecification = new EmbeddedDataSpecification();
		Collection<IEmbeddedDataSpecification> specifications = Collections.singleton(embeddedDataSpecification);
		asset.setEmbeddedDataSpecifications(specifications);
		assertEquals(specifications, asset.getEmbeddedDataSpecifications());
	}
	
	@Test
	public void testAssetKind() {
		AssetKind kind = AssetKind.INSTANCE;
		asset.setAssetKind(kind);
		assertEquals(kind, asset.getAssetKind());
	}
	
	@Test
	public void testSetAdministration() {
		AdministrativeInformation information = new AdministrativeInformation("1.0", "5");
		asset.setAdministration(information);
		assertEquals(information, asset.getAdministration());
	} 
	
	@Test
	public void testSetIdentification() {
		IdentifierType idType = IdentifierType.IRI;
		String newIdString = "newId";
		asset.setIdentification(idType, newIdString);
		assertEquals(new Identifier(idType, newIdString), asset.getIdentification());
	}
	
	@Test
	public void testSetAssetIdentificationModel() {
		asset.setAssetIdentificationModel(REFERENCE);
		assertEquals(REFERENCE, asset.getAssetIdentificationModel());
	}
	
	@Test
	public void testSetIdShort() {
		String newIdString = "newId";
		asset.setIdShort(newIdString);
		assertEquals(newIdString, asset.getIdShort());
	}
	
	@Test
	public void testSetCategory() {
		String newCategoryString = "newCategory";
		asset.setCategory(newCategoryString);
		assertEquals(newCategoryString, asset.getCategory());
	}
	
	@Test
	public void testSetDescription() {
		LangStrings newDescriptionString = new LangStrings("DE", "newTest");
		asset.setDescription(newDescriptionString);
		assertEquals(newDescriptionString, asset.getDescription());
	}
	
	@Test
	public void testSetParent() {
		Reference parent = new Reference(new Identifier(IdentifierType.IRDI, "testNewId"), KeyElements.ASSET, true);
		asset.setParent(parent);
		assertEquals(parent, asset.getParent());
	}
	
	@Test
	public void testSetBillOfMaterial() {
		asset.setBillOfMaterial(REFERENCE);
		assertEquals(REFERENCE, asset.getBillOfMaterial());
	} 
}
