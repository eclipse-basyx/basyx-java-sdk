package org.eclipse.basyx.extensions.submodel.storage.retrieval.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

public class StorageSubmodelElementFilterFactory {
	private static final String TIMESTAMP = "timestamp";

	private StorageSubmodelElementFilterFactory() {
	}

	public static Collection<StorageSubmodelElementFilter> createAllFilters(Entry<String, String> queryParameter) {
		Collection<StorageSubmodelElementFilter> filterCollection = new ArrayList<>();

		String queryValue = queryParameter.getValue();
		if (queryValue == null) {
			throw new MalformedRequestException("The query parameter value can not be null.");
		}

		String filterKey = queryParameter.getKey();
		String filterOperation = getFilterOperation(queryValue);
		String filterValue = getValueStringWithoutOperation(queryValue);

		filterCollection.add(createSpecificSubmodelElementFilter(filterKey, filterOperation, filterValue));

		return filterCollection;
	}

	private static StorageSubmodelElementFilter createSpecificSubmodelElementFilter(String filterKey, String filterOperation, String filterValue) {
		return createSpecificSubmodelElementFilter(filterKey, filterKey, filterOperation, filterValue);
	}

	private static StorageSubmodelElementFilter createSpecificSubmodelElementFilter(String filterKey, String parameterKey, String filterOperation, String filterValue) {
		if (filterKey.startsWith(TIMESTAMP)) {
			return new StorageSubmodelElementTimeFilter(filterKey, filterOperation, filterValue);
		} else {
			return new StorageSubmodelElementStringFilter(filterKey, filterOperation, filterValue);
		}
	}

	private static String getFilterOperation(String parameterValue) {
		return parameterValue.split("\\(")[0];
	}

	private static String getValueStringWithoutOperation(String value) {
		int open = value.indexOf("(");
		int close = value.lastIndexOf(")");
		if (open < 0 || close < 0) {
			throw new MalformedRequestException("No correct filter operation given. Maybe there are missing parenthesis.");
		}
		return value.substring(open + 1, close);
	}

}
