package org.eclipse.basyx.extensions.submodel.storage.retrieval;

import java.util.Map.Entry;

public class StorageSubmodelElementFilter {

	private String filterOperation;
	private String key;
	private String value;

	public StorageSubmodelElementFilter(Entry<String, String> queryParameter) {
		this.filterOperation = getFilterOperation(queryParameter.getValue());
		this.key = queryParameter.getKey();
		this.value = getValueWithoutOperation(queryParameter.getValue());
	}

	public String getFilterOperation() {
		return filterOperation.trim();
	}

	public String getKey() {
		return key.trim();
	}

	public String getValue() {
		return value.trim();
	}

	private String getValueWithoutOperation(String value) {
		int open = value.indexOf("(");
		int close = value.lastIndexOf(")");
		return value.substring(open + 1, close);
	}

	private String getFilterOperation(String value) {
		return value.split("\\(")[0];
	}

	public class StorageRetrievalOperationAbbreviations {
		public static final String EQUAL = "eq";
		public static final String NOT_EQUAL = "ne";
		public static final String GREATER_THAN = "gt";
		public static final String GREATER_OR_EQUAL = "ge";
		public static final String LESS_THAN = "lt";
		public static final String LESS_OR_EQUAL = "le";
		public static final String BETWEEN = "btw";
	}
}
