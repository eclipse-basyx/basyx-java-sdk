package org.eclipse.basyx.extensions.submodel.storage.elements;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;

public class StorageSubmodelElementComponentHelper {

	public static String getModelTypeSpecial(ISubmodelElement submodelElement) {
		Map<String, Object> elementMap = submodelElement.getLocalCopy();
		if (Property.isProperty(elementMap)) {
			return Property.createAsFacade(elementMap).getValueType().toString();
		} else if (Range.isRange(elementMap)) {
			return Range.createAsFacade(elementMap).getValueType().toString();
		}
		return null;
	}
}
