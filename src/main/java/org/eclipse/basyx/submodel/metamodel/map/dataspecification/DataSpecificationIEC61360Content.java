/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.dataspecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationIEC61360Content;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IValueReferencePair;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.enums.DataTypeIEC61360;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.enums.LevelType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.vab.model.VABModelMap;

/**
 * DataSpecification class
 * 
 * @author elsheikh
 *
 */
public class DataSpecificationIEC61360Content extends VABModelMap<Object> implements IDataSpecificationIEC61360Content {
	public static final String PREFERREDNAME = "preferredName";
	public static final String SHORTNAME = "shortName";
	public static final String UNIT = "unit";
	public static final String UNITID = "unitId";
	public static final String SOURCEOFDEFINITION = "sourceOfDefinition";
	public static final String SYMBOL = "symbol";
	public static final String DATATYPE = "dataType";
	public static final String DEFINITION = "definition";
	public static final String VALUEFORMAT = "valueFormat";
	public static final String VALUELIST = "valueList";
	public static final String VALUE = "value";
	public static final String VALUEID = "valueId";
	public static final String LEVELTYPE = "levelType";

	/**
	 * Constructor
	 */
	public DataSpecificationIEC61360Content() {}

	/**
	 * Constructor
	 */
	public DataSpecificationIEC61360Content(LangStrings preferredName, LangStrings shortName, String unit,
			IReference unitId, String sourceOfDefinition, String symbol, DataTypeIEC61360 dataType,
			LangStrings definition, String valueFormat, Collection<IValueReferencePair> valueList, String value,
			IReference valueId, LevelType levelType) {
		// Default values
		put(PREFERREDNAME, preferredName);
		put(SHORTNAME, shortName);
		put(UNIT, unit);
		put(UNITID, unitId);
		put(SOURCEOFDEFINITION, sourceOfDefinition);
		put(SYMBOL, symbol);
		put(DATATYPE, dataType.toString());
		put(DEFINITION, definition);
		put(VALUEFORMAT, valueFormat);
		put(VALUELIST, valueList);
		put(VALUE, value);
		put(VALUEID, valueId);
		put(LEVELTYPE, levelType.toString());
	}

	/**
	 * Creates a DataSpecificationIEC61360 object from a map
	 * 
	 * @param map
	 *            a DataSpecificationIEC61360 object as raw map
	 * @return a DataSpecificationIEC61360 object, that behaves like a facade for
	 *         the given map
	 */
	public static DataSpecificationIEC61360Content createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		DataSpecificationIEC61360Content ret = new DataSpecificationIEC61360Content();
		ret.putAll(map);
		return ret;
	}

	/**
	 * @return A Set of Strings containing all languages for the preferredName
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LangStrings getPreferredName() {
		return LangStrings.createAsFacade((Collection<Map<String, Object>>) get(DataSpecificationIEC61360Content.PREFERREDNAME));
	}

	/**
	 * @return A Set of Strings containing all languages for the shortName
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LangStrings getShortName() {
		return LangStrings.createAsFacade((Collection<Map<String, Object>>) get(DataSpecificationIEC61360Content.SHORTNAME));
	}

	/**
	 * @return The unit string
	 */
	@Override
	public String getUnit() {
		return (String) get(DataSpecificationIEC61360Content.UNIT);
	}

	/**
	 * @return The reference to the unit id
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IReference getUnitId() {
		return Reference.createAsFacade((Map<String, Object>) get(DataSpecificationIEC61360Content.UNITID));
	}

	/**
	 * @return The source of definition
	 */
	@Override
	public String getSourceOfDefinition() {
		return (String) get(DataSpecificationIEC61360Content.SOURCEOFDEFINITION);
	}

	/**
	 * @return The symbol string
	 */
	@Override
	public String getSymbol() {
		return (String) get(DataSpecificationIEC61360Content.SYMBOL);
	}

	/**
	 * @return The data type
	 */
	@Override
	public DataTypeIEC61360 getDataType() {
		return DataTypeIEC61360.fromString((String) get(DataSpecificationIEC61360Content.DATATYPE));
	}

	/**
	 * @return A Set of Strings containing all languages for the definition
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LangStrings getDefinition() {
		return LangStrings.createAsFacade((Collection<Map<String, Object>>) get(DataSpecificationIEC61360Content.DEFINITION));
	}

	/**
	 * @return The value format
	 */
	@Override
	public String getValueFormat() {
		return (String) get(DataSpecificationIEC61360Content.VALUEFORMAT);
	}

	/**
	 * @return The value list (contains pairs of values and value ids)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<IValueReferencePair> getValueList() {
		ArrayList<IValueReferencePair> result = new ArrayList<>();
		Object list = get(DataSpecificationIEC61360Content.VALUELIST);
		if ( !(list instanceof Collection<?>) ) {
			return result;
		}
		for (Map<String, Object> pair : (Collection<Map<String, Object>>) list) {
			result.add(ValueReferencePair.createAsFacade(pair));
		}
		return result;
	}

	/**
	 * @return The value string
	 */
	@Override
	public String getValue() {
		return (String) get(DataSpecificationIEC61360Content.VALUE);
	}

	/**
	 * @return The valueId reference
	 */
	@SuppressWarnings("unchecked")
	@Override
	public IReference getValueId() {
		return Reference.createAsFacade((Map<String, Object>) get(DataSpecificationIEC61360Content.VALUEID));
	}
	
	/**
	 * @return The level types
	 */
	@Override
	public LevelType getLevelType() {
		return LevelType.fromString((String) get(DataSpecificationIEC61360Content.LEVELTYPE));
	}

	public void setPreferredName(LangStrings preferredName) {
		if (preferredName != null)
			put(DataSpecificationIEC61360Content.PREFERREDNAME, preferredName);
	}

	public void setShortName(LangStrings shortName) {
		if (shortName != null)
			put(DataSpecificationIEC61360Content.SHORTNAME, shortName);
	}

	public void setUnit(String unit) {
		if (unit != null)
			put(DataSpecificationIEC61360Content.UNIT, unit);
	}

	public void setUnitId(IReference unitId) {
		if (unitId != null)
			put(DataSpecificationIEC61360Content.UNITID, unitId);
	}

	public void setSourceOfDefinition(String sourceOfDefinition) {
		if (sourceOfDefinition != null)
			put(DataSpecificationIEC61360Content.SOURCEOFDEFINITION, sourceOfDefinition);
	}

	public void setSymbol(String symbol) {
		if (symbol != null)
			put(DataSpecificationIEC61360Content.SYMBOL, symbol);
	}

	public void setDataType(DataTypeIEC61360 dataType) {
		if (dataType != null)
			put(DataSpecificationIEC61360Content.DATATYPE, dataType.toString());
	}

	public void setDefinition(LangStrings definition) {
		if (definition != null)
			put(DataSpecificationIEC61360Content.DEFINITION, definition);
	}

	public void setValueFormat(String valueFormat) {
		if (valueFormat != null)
			put(DataSpecificationIEC61360Content.VALUEFORMAT, valueFormat);
	}

	public void setValueList(Collection<IValueReferencePair> pairList) {
		if (pairList != null && !pairList.isEmpty())
			put(DataSpecificationIEC61360Content.VALUELIST, pairList);
	}

	public void setValue(String value) {
		if (value != null)
			put(DataSpecificationIEC61360Content.VALUE, value);
	}

	public void setValueId(IReference valueId) {
		if (valueId != null)
			put(DataSpecificationIEC61360Content.VALUEID, valueId);
	}

	public void setLevelType(LevelType levelType) {
		if (levelType != null)
			put(DataSpecificationIEC61360Content.LEVELTYPE, levelType.toString());
	}

}
