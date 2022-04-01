package org.eclipse.basyx.extensions.submodel.storage.retrieval.filters;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;

public class StorageSubmodelElementTimeFilter extends StorageSubmodelElementFilter {
	StorageSubmodelElementTimeFilter(String key, String filterOperation, String value) {
		super(key, filterOperation, value);
	}

	@Override
	protected Timestamp createValueFromString(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return new Timestamp(df.parse(date.trim()).getTime());
		} catch (ParseException e) {
			throw new MalformedRequestException(e);
		}
	}
}
