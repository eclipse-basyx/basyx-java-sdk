package org.eclipse.basyx.extensions.submodel.storage.retrieval.filters;

public class StorageSubmodelElementStringFilter extends StorageSubmodelElementFilter {

	StorageSubmodelElementStringFilter(String key, String filterOperation, String value) {
		super(key, filterOperation, value);
	}

	@Override
	protected String createValueFromString(String value) {
		return value;
	}
}
