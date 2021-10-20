/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.factory.xml.api.dataspecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.LangStringsXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationIEC61360Content;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IValueReferencePair;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.enums.DataTypeIEC61360;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.enums.LevelType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.DataSpecificationIEC61360Content;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.ValueReferencePair;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the conversion between a DataSpecificationIEC61360 object and the XML tag
 * &lt;aas:dataSpecificationIEC61360&gt; in both directions
 * 
 * @author conradi, espen
 *
 */
public class DataSpecificationIEC61360XMLConverter {
	public static final String IEC61360_LANGSTRING = "IEC61360:langString";
	public static final String IEC61360_KEYS = "IEC61360:keys";
	public static final String IEC61360_KEY = "IEC61360:key";

	public static final String IEC61360_PREFERREDNAME = "IEC61360:preferredName";
	public static final String IEC61360_SHORTNAME = "IEC61360:shortName";
	public static final String IEC61360_UNIT = "IEC61360:unit";
	public static final String IEC61360_UNITID = "IEC61360:unitId";
	public static final String IEC61360_SOURCEOFDEFINITION = "IEC61360:sourceOfDefinition";
	public static final String IEC61360_SYMBOL = "IEC61360:symbol";
	public static final String IEC61360_DATATYPE = "IEC61360:dataType";
	public static final String IEC61360_DEFINITION = "IEC61360:definition";
	public static final String IEC61360_VALUEFORMAT = "IEC61360:valueFormat";

	public static final String IEC61360_VALUELIST = "IEC61360:valueList";
	public static final String IEC61360_REFERENCEPAIR = "IEC61360:valueReferencePair";
	public static final String IEC61360_PAIRVALUE = "IEC61360:value";
	public static final String IEC61360_PAIRID = "IEC61360:valueId";

	public static final String IEC61360_VALUE = "IEC61360:value";
	public static final String IEC61360_VALUEID = "IEC61360:valueId";
	public static final String IEC61360_LEVELTYPE = "IEC61360:levelType";
	
	
	/**
	 * Parses the DataSpecificationIEC61360 object from XML
	 * 
	 * @param xmlDataSpecificationContentObject the XML map containing the &lt;aas:dataSpecificationIEC61360&gt; tag
	 * @return the parsed DataSpecificationIEC61360 object
	 */
	@SuppressWarnings("unchecked")
	public static DataSpecificationIEC61360Content parseDataSpecificationContent(Map<String, Object> contentObj) {
		DataSpecificationIEC61360Content spec = new DataSpecificationIEC61360Content();
		if (contentObj == null) {
			return spec;
		}
		
		// PreferredName - LangStrings
		LangStrings preferredName = LangStringsXMLConverter
				.parseLangStrings(contentObj.get(IEC61360_PREFERREDNAME), IEC61360_LANGSTRING);
		spec.setPreferredName(preferredName);
		// ShortName - LangStrings
		LangStrings shortName = LangStringsXMLConverter
				.parseLangStrings(contentObj.get(IEC61360_SHORTNAME), IEC61360_LANGSTRING);
		spec.setShortName(shortName);
		// Unit - String
		spec.setUnit(XMLHelper.getString(contentObj.get(IEC61360_UNIT)));
		// UnitId - IReference
		spec.setUnitId(ReferenceXMLConverter.parseReference((Map<String, Object>) contentObj.get(IEC61360_UNITID),
				IEC61360_KEYS, IEC61360_KEY));
		// Source Of Definition - String
		spec.setSourceOfDefinition(XMLHelper.getString(contentObj.get(IEC61360_SOURCEOFDEFINITION)));
		// Symbol - String
		spec.setSymbol(XMLHelper.getString(contentObj.get(IEC61360_SYMBOL)));
		// Data Type - DataTypeIEC61360
		spec.setDataType(DataTypeIEC61360.fromString(XMLHelper.getString(contentObj.get(IEC61360_DATATYPE))));
		// Definition - LangStrings
		LangStrings definition = LangStringsXMLConverter
				.parseLangStrings(contentObj.get(IEC61360_DEFINITION), IEC61360_LANGSTRING);
		spec.setDefinition(definition);
		// ValueFormat - String
		spec.setValueFormat(XMLHelper.getString(contentObj.get(IEC61360_VALUEFORMAT)));
		// ValueList - Collection<IValueReferencePair>
		spec.setValueList(parseValueList((Map<String, Object>) contentObj.get(IEC61360_VALUELIST)));
		// Value - String
		spec.setValue(XMLHelper.getString(contentObj.get(IEC61360_VALUE)));
		// ValueId - Ref
		spec.setValueId(ReferenceXMLConverter.parseReference((Map<String, Object>) contentObj.get(IEC61360_VALUEID),
				IEC61360_KEYS, IEC61360_KEY));
		// LevelType - LevelType
		spec.setLevelType(LevelType.fromString(XMLHelper.getString(contentObj.get(IEC61360_LEVELTYPE))));
		return spec;
	}
	
	/**
	 * Parses the Collection<IValueReferencePair> object from XML
	 * 
	 * @param xmlObj the XML map containing the &lt;IEC61360:valueReferencePair&gt; tags
	 * @return the parsed collection of IValueReferencePair
	 */
	@SuppressWarnings("unchecked")
	private static Collection<IValueReferencePair> parseValueList(Map<String, Object> xmlObj) {
		if (xmlObj == null) {
			return new ArrayList<>();
		}
		List<Map<String, Object>> xmlPairs = XMLHelper.getList(xmlObj.get(IEC61360_REFERENCEPAIR));
		Collection<IValueReferencePair> pairs = new ArrayList<>();
		for (Map<String, Object> xmlPair : xmlPairs) {
			ValueReferencePair pair = new ValueReferencePair();
			pair.setValue(XMLHelper.getString(xmlPair.get(IEC61360_PAIRVALUE)));
			pair.setValueId(ReferenceXMLConverter.parseReference((Map<String, Object>) xmlPair.get(IEC61360_PAIRID)));
			pairs.add(pair);
		}
		return pairs;
	}

	/**
	 * Populates a DataSpecificationContent XML from the IDataSpecificationIEC61360Content object.
	 * 
	 * @param document    the XML document
	 * @param contentRoot the XML root Element to be populated
	 * @param content     the IDataSpecification object to be converted to XML
	 */
	public static void populateIEC61360ContentXML(Document document, Element contentRoot,
			IDataSpecificationIEC61360Content content) {
		// PreferredName
		LangStrings preferredName = content.getPreferredName();
		if (preferredName != null && !preferredName.isEmpty()) {
			Element preferredNameRoot = document.createElement(IEC61360_PREFERREDNAME);
			LangStringsXMLConverter.buildLangStringsXML(document, preferredNameRoot, IEC61360_LANGSTRING,
					preferredName);
			contentRoot.appendChild(preferredNameRoot);
		}
		// ShortName
		LangStrings shortName = content.getShortName();
		if (shortName != null && !shortName.isEmpty()) {
			Element shortNameRoot = document.createElement(IEC61360_SHORTNAME);
			LangStringsXMLConverter.buildLangStringsXML(document, shortNameRoot, IEC61360_LANGSTRING, shortName);
			contentRoot.appendChild(shortNameRoot);
		}
		// Unit
		String unit = content.getUnit();
		if (unit != null && !unit.isEmpty()) {
			Element unitRoot = document.createElement(IEC61360_UNIT);
			unitRoot.appendChild(document.createTextNode(unit));
			contentRoot.appendChild(unitRoot);
		}
		// UnitId
		IReference unitId = content.getUnitId();
		if (unitId != null) {
			Element unitIdRoot = document.createElement(IEC61360_UNITID);
			unitIdRoot.appendChild(
							ReferenceXMLConverter.buildReferenceXML(document, unitId, IEC61360_KEYS, IEC61360_KEY));
			contentRoot.appendChild(unitIdRoot);
		}
		// Source Of Definition
		String sod = content.getSourceOfDefinition();
		if (sod != null && !sod.isEmpty()) {
			Element sodRoot = document.createElement(IEC61360_SOURCEOFDEFINITION);
			sodRoot.appendChild(document.createTextNode(sod));
			contentRoot.appendChild(sodRoot);
		}
		// Symbol
		String symbol = content.getSymbol();
		if (symbol != null && !symbol.isEmpty()) {
			Element symbolRoot = document.createElement(IEC61360_SYMBOL);
			symbolRoot.appendChild(document.createTextNode(symbol));
			contentRoot.appendChild(symbolRoot);
		}
		// Data Type
		DataTypeIEC61360 dtype = content.getDataType();
		if (dtype != null) {
			Element dataTypeRoot = document.createElement(IEC61360_DATATYPE);
			dataTypeRoot.appendChild(document.createTextNode(dtype.toString()));
			contentRoot.appendChild(dataTypeRoot);
		}
		// Definition
		LangStrings definition = content.getDefinition();
		if (definition != null && !definition.isEmpty()) {
			Element definitionRoot = document.createElement(IEC61360_DEFINITION);
			LangStringsXMLConverter.buildLangStringsXML(document, definitionRoot, IEC61360_LANGSTRING, definition);
			contentRoot.appendChild(definitionRoot);
		}
		// ValueFormat
		String valueFormat = content.getValueFormat();
		if (valueFormat != null && !valueFormat.isEmpty()) {
			Element valueFormatRoot = document.createElement(IEC61360_VALUEFORMAT);
			valueFormatRoot.appendChild(document.createTextNode(valueFormat));
			contentRoot.appendChild(valueFormatRoot);
		}
		// ValueList
		Collection<IValueReferencePair> valueList = content.getValueList();
		if (valueList != null && !valueList.isEmpty()) {
			Element valueListRoot = document.createElement(IEC61360_VALUELIST);
			buildValueListXML(document, valueListRoot, valueList);
			contentRoot.appendChild(valueListRoot);
		}
		// Value
		String value = content.getValue();
		if (value != null && !value.isEmpty()) {
			Element valueRoot = document.createElement(IEC61360_VALUE);
			valueRoot.appendChild(document.createTextNode(value));
			contentRoot.appendChild(valueRoot);
		}
		// ValueId
		IReference valueId = content.getValueId();
		if (valueId != null) {
			Element valueIdRoot = document.createElement(IEC61360_VALUEID);
			valueIdRoot.appendChild(
							ReferenceXMLConverter.buildReferenceXML(document, valueId, IEC61360_KEYS, IEC61360_KEY));
			contentRoot.appendChild(valueIdRoot);
		}
		// LevelType
		LevelType lType = content.getLevelType();
		if (lType != null) {
			Element levelTypeRoot = document.createElement(IEC61360_LEVELTYPE);
			levelTypeRoot.appendChild(document.createTextNode(lType.toString()));
			contentRoot.appendChild(levelTypeRoot);
		}
	}

	private static void buildValueListXML(Document document, Element valueListRoot,
			Collection<IValueReferencePair> valueList) {
		for (IValueReferencePair pair : valueList) {
			Element pairRoot = document.createElement(IEC61360_REFERENCEPAIR);

			IReference valueId = pair.getValueId();
			Element valueIdRoot = document.createElement(IEC61360_PAIRID);
			valueIdRoot
					.appendChild(
							ReferenceXMLConverter.buildReferenceXML(document, valueId, IEC61360_KEYS, IEC61360_KEY));
			pairRoot.appendChild(valueIdRoot);

			Element valueRoot = document.createElement(IEC61360_PAIRVALUE);
			valueRoot.appendChild(document.createTextNode(pair.getValue()));
			pairRoot.appendChild(valueRoot);

			valueListRoot.appendChild(pairRoot);
		}
	}
	
}
