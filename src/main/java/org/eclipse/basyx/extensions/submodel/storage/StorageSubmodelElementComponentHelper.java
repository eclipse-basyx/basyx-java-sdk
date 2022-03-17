package org.eclipse.basyx.extensions.submodel.storage;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.event.BasicEvent;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;

public class StorageSubmodelElementComponentHelper {
	public static final String QUALIFIER = "storage";

	public static String getModelTypeSpecial(ISubmodelElement submodelElement) {
		Map<String, Object> elementMap = submodelElement.getLocalCopy();
		if (Property.isProperty(elementMap)) {
			return Property.createAsFacade(elementMap).getValueType().toString();
		} else if (Range.isRange(elementMap)) {
			return Range.createAsFacade(elementMap).getValueType().toString();
		}
		return null;
	}

	public static boolean isElementPersistable(SubmodelElement submodelElement) {
		List<String> supportedModelTypes = Arrays.asList(Property.MODELTYPE, SubmodelElementCollection.MODELTYPE, Range.MODELTYPE, MultiLanguageProperty.MODELTYPE, ReferenceElement.MODELTYPE, BasicEvent.MODELTYPE, Blob.MODELTYPE,
				File.MODELTYPE, AnnotatedRelationshipElement.MODELTYPE, RelationshipElement.MODELTYPE, Entity.MODELTYPE);
		return supportedModelTypes.contains(submodelElement.getModelType());
	}

	public static boolean isStorageQualifierSet(ISubmodelElement submodelElement) {
		Collection<IConstraint> qualifiers = submodelElement.getQualifiers();

		for (IConstraint qualifierConstraint : qualifiers) {
			Qualifier qualifier = (Qualifier) qualifierConstraint;
			if (isStorageQualifierTrue(qualifier)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isStorageQualifierTrue(Qualifier qualifier) {
		return qualifier.getType().equals(QUALIFIER) && qualifier.getValue().equals("true");
	}

}
