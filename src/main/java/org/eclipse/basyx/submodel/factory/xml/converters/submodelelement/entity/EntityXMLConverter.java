/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.submodelelement.SubmodelElementXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.EntityType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.IEntity;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parses &lt;aas:entity&gt; and builds the Entity object from it <br>
 * Builds &lt;aas:entity&gt; from a given Entity object
 * 
 * @author conradi
 *
 */
public class EntityXMLConverter extends SubmodelElementXMLConverter {
	
	public static final String ENTITY = "aas:entity";
	public static final String ENTITY_TYPE = "aas:entityType";
	public static final String STATEMENTS = "aas:statements";
	public static final String ASSET_REF = "aas:assetRef";
	
	
	/**
	 * Parses a Map containing the content of XML tag &lt;aas:entity&gt;
	 * 
	 * @param xmlObject the Map with the content of XML tag &lt;aas:entity&gt;
	 * @return the parsed Entity
	 */
	@SuppressWarnings("unchecked")
	public static Entity parseEntity(Map<String, Object> xmlObject) {
		String entityTypeString = XMLHelper.getString(xmlObject.get(ENTITY_TYPE));
		EntityType entityType = EntityType.fromString(entityTypeString);
		Map<String, Object> xmlAssetRef = (Map<String, Object>) xmlObject.get(ASSET_REF);
		Reference assetRef = ReferenceXMLConverter.parseReference(xmlAssetRef);
		Map<String, Object> xmlStatement = (Map<String, Object>) xmlObject.get(STATEMENTS);
		List<ISubmodelElement> statements = getSubmodelElements(xmlStatement);
		Entity entity = new Entity(entityType, statements, assetRef);
		populateSubmodelElement(xmlObject, entity);
		return entity;
	}
	
	
	
	
	/**
	 * Builds the &lt;aas:entity&gt; XML tag for an Entity
	 * 
	 * @param document the XML document
	 * @param entity the IEntity to build the XML for
	 * @return the &lt;aas:entity&gt; XML tag for the given Entity
	 */
	public static Element buildEntity(Document document, IEntity entity) {
		Element entityRoot = document.createElement(ENTITY);
		
		populateSubmodelElement(document, entityRoot, entity);
		
		IReference assetRef = entity.getAsset();
		if(assetRef != null) {
			Element assetRefRoot = document.createElement(ASSET_REF);
			assetRefRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, assetRef));
			entityRoot.appendChild(assetRefRoot);
		}
		
		Object entityTypeObj = entity.getEntityType();
		String entityType = entityTypeObj == null ? null : entityTypeObj.toString();
		if(entityType != null) {
			Element entityTypeRoot = document.createElement(ENTITY_TYPE);
			entityTypeRoot.appendChild(document.createTextNode(entityType));
			entityRoot.appendChild(entityTypeRoot);
		}
		
		Collection<ISubmodelElement> statement = entity.getStatements();
		
		//recursively build the SubmodelElements contained in the statement
		if(statement != null) {
			Element statementsRoot = document.createElement(STATEMENTS);
			entityRoot.appendChild(statementsRoot);
			buildSubmodelElements(document, statementsRoot, statement);
		}

		return entityRoot;
	}
}
