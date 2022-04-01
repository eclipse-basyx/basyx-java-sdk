package org.eclipse.basyx.extensions.submodel.storage.retrieval.filters;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.extensions.submodel.storage.retrieval.StorageSubmodelElementQueryStringBuilder;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

public abstract class StorageSubmodelElementFilter {
	protected String filterOperation;
	protected String key;
	protected Map<String, Object> parameterMap;

	StorageSubmodelElementFilter(String key, String filterOperation, String value) {
		this.filterOperation = filterOperation;
		this.key = key;
		this.parameterMap = createParameterMap(value);
	}

	protected Map<String, Object> createParameterMap(String value) {
		Map<String, Object> parameters = new HashMap<>();

		if (filterOperation.equals(StorageRetrievalOperationAbbreviations.BETWEEN)) {
			String[] betweenValues = value.split(",");
			if (betweenValues.length != 2) {
				throw new MalformedRequestException("'Between' operation takes exactly two input parameters.");
			}
			parameters.put(key + StorageSubmodelElementQueryStringBuilder.BETWEEN_START_SUFFIX, createValueFromString(betweenValues[0]));
			parameters.put(key + StorageSubmodelElementQueryStringBuilder.BETWEEN_END_SUFFIX, createValueFromString(betweenValues[1]));
		} else {
			parameters.put(key, createValueFromString(value));
		}

		return parameters;
	}

	abstract Object createValueFromString(String value);

	public String getFilterOperation() {
		return filterOperation.trim();
	}

	public String getKey() {
		return key.trim();
	}

	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}

}
