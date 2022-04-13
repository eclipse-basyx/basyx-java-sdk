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

package org.eclipse.basyx.submodel.types.technicaldata;

import java.util.Collections;
import java.util.Map;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.furtherinformation.FurtherInformation;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.generalinformation.GeneralInformation;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.productclassifications.ProductClassifications;
import org.eclipse.basyx.submodel.types.technicaldata.submodelelementcollections.technicalproperties.TechnicalProperties;

/**
 * TechnicalDataSubmodel as described in the Submodel Template AAS Technical
 * Data Document
 * 
 * this contains Submodel containing technical data of the asset and associated
 * product classifications.
 * 
 * @author haque
 *
 */
public class TechnicalDataSubmodel extends Submodel {
	public static final String GENERALINFORMATIONID = "GeneralInformation";
	public static final String PRODUCTCLASSIFICATIONSID = "ProductClassifications";
	public static final String TECHNICALPROPERTIESID = "TechnicalProperties";
	public static final String FURTHERINFORMATIONID = "FurtherInformation";
	public static final Reference SEMANTICID = new Reference(Collections.singletonList(new Key(KeyElements.CONCEPTDESCRIPTION, false, "http://admin-shell.io/ZVEI/TechnicalData/Submodel/1/1", KeyType.IRI)));
	public static final String SUBMODELID = "TechnicalData";

	private TechnicalDataSubmodel() {
	}

	/**
	 * Constructor with default idShort
	 * 
	 * @param identifier
	 * @param generalInformation
	 * @param productClassifications
	 * @param properties
	 * @param furtherInformation
	 */
	public TechnicalDataSubmodel(Identifier identifier, GeneralInformation generalInformation, ProductClassifications productClassifications, TechnicalProperties properties, FurtherInformation furtherInformation) {
		this(SUBMODELID, identifier, generalInformation, productClassifications, properties, furtherInformation);
	}

	/**
	 * Constructor with mandatory attributes
	 * 
	 * @param idShort
	 * @param identifier
	 * @param generalInformation
	 * @param productClassifications
	 * @param properties
	 * @param furtherInformation
	 */
	public TechnicalDataSubmodel(String idShort, Identifier identifier, GeneralInformation generalInformation, ProductClassifications productClassifications, TechnicalProperties properties, FurtherInformation furtherInformation) {
		super(idShort, identifier);
		setSemanticId(SEMANTICID);
		setGeneralInformation(generalInformation);
		setProductClassifications(productClassifications);
		setTechnicalProperties(properties);
		setFurtherInformation(furtherInformation);
	}

	/**
	 * Creates a TechnicalDataSubmodel object from a map
	 * 
	 * @param obj
	 *            a TechnicalDataSubmodel SMC object as raw map
	 * @return a TechnicalDataSubmodel SMC object, that behaves like a facade for
	 *         the given map
	 */
	public static TechnicalDataSubmodel createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(TechnicalDataSubmodel.class, obj);
		}

		TechnicalDataSubmodel ret = new TechnicalDataSubmodel();
		ret.setMap((Map<String, Object>) SubmodelElementMapCollectionConverter.mapToSM(obj));
		return ret;
	}

	/**
	 * Creates a TechnicalDataSubmodel object from a map without validation
	 * 
	 * @param obj
	 *            a TechnicalDataSubmodel SMC object as raw map
	 * @return a TechnicalDataSubmodel SMC object, that behaves like a facade for
	 *         the given map
	 */
	private static TechnicalDataSubmodel createAsFacadeNonStrict(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		TechnicalDataSubmodel ret = new TechnicalDataSubmodel();
		ret.setMap((Map<String, Object>) SubmodelElementMapCollectionConverter.mapToSM(obj));
		return ret;
	}

	/**
	 * Check whether all mandatory elements for TechnicalDataSubmodel exist in the
	 * map
	 * 
	 * @param obj
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		TechnicalDataSubmodel submodel = createAsFacadeNonStrict(obj);

		return Submodel.isValid(obj) && GeneralInformation.isValid((Map<String, Object>) submodel.getGeneralInformation()) && TechnicalProperties.isValid((Map<String, Object>) submodel.getTechnicalProperties());
	}

	/**
	 * Sets general information, for example ordering and manufacturer information.
	 * 
	 * @param information
	 */
	public void setGeneralInformation(GeneralInformation information) {
		addSubmodelElement(information);
	}

	/**
	 * 
	 * Gets general information, for example ordering and manufacturer information.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public GeneralInformation getGeneralInformation() {
		return GeneralInformation.createAsFacade((Map<String, Object>) getSubmodelElement(GENERALINFORMATIONID));
	}

	/**
	 * Sets product classifications by association of product classes with common
	 * classification systems.
	 * 
	 * @param classifications
	 */
	public void setProductClassifications(ProductClassifications classifications) {
		addSubmodelElement(classifications);
	}

	/**
	 * 
	 * Gets product classifications by association of product classes with common
	 * classification systems.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ProductClassifications getProductClassifications() {
		return ProductClassifications.createAsFacade((Map<String, Object>) getSubmodelElement(PRODUCTCLASSIFICATIONSID));
	}

	/**
	 * Sets technical and product properties. Individual characteristics that
	 * describe the product and its technical properties.
	 * 
	 * @param properties
	 */
	public void setTechnicalProperties(TechnicalProperties properties) {
		addSubmodelElement(properties);
	}

	/**
	 * 
	 * Gets technical and product properties. Individual characteristics that
	 * describe the product and its technical properties.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TechnicalProperties getTechnicalProperties() {
		return TechnicalProperties.createAsFacade((Map<String, Object>) getSubmodelElement(TECHNICALPROPERTIESID));
	}

	/**
	 * Sets further information on the product, the validity of the information
	 * provided and this data record.
	 * 
	 * @param information
	 */
	public void setFurtherInformation(FurtherInformation information) {
		addSubmodelElement(information);
	}

	/**
	 * 
	 * Gets further information on the product, the validity of the information
	 * provided and this data record.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public FurtherInformation getFurtherInformation() {
		return FurtherInformation.createAsFacade((Map<String, Object>) getSubmodelElement(FURTHERINFORMATIONID));
	}
}
