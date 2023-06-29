package org.eclipse.basyx.submodel.types.handoverdocumentation.submodelelementcollections.document;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

import java.util.Collections;
import java.util.Map;

/**
 * This SubmodelElementCollection holds the information for a VDI 2770 DocumentClassification entity.
 */
public class DocumentClassification extends SubmodelElementCollection {

	public static final Reference SEMANTIC_ID = new Reference(
			Collections.singletonList(new Key(KeyElements.CONCEPTDESCRIPTION, false,
					"0173-1#02-ABI502#001/0173-1#01-AHF581#001", KeyType.IRDI)));

	public static final String CLASS_ID = "ClassId";
	public static final String CLASS_IRDI = "0173-1#02-ABH996#001";
	public static final String CLASS_NAME = "ClassName";
	public static final String CLASS_NAME_IRDI = "0173-1#02-AAO102#003";
	public static final String CLASSIFICATION_SYSTEM_ID = "ClassificationSystem";
	public static final String CLASSIFICATION_SYSTEM_IRDI = "0173-1#02-ABH997#001";

	DocumentClassification(String idShort) {
		super(idShort);
		setSemanticId(SEMANTIC_ID);
	}

	public DocumentClassification(String idShort, String classId, String... classNamePairs) {
		this(idShort);
		setClassId(classId);
		setClassName(classNamePairs);
	}

	private DocumentClassification() {
		super();
	}

	public void setClassId(String classId) {
		Property classIdProp = new Property(CLASS_ID, ValueType.String);
		classIdProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				CLASS_IRDI, IdentifierType.IRDI)));
		classIdProp.setValue(classId);
		setClassId(classIdProp);
	}
	public void setClassId(Property classId) {
		addSubmodelElement(classId);
	}

	/**
	 *
	 * @param classNamePairs an equal number of strings as pairs of language code and class name.
	 */
	public void setClassName(String... classNamePairs) {
		MultiLanguageProperty classNameProp = new MultiLanguageProperty(CLASS_NAME);
		classNameProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				CLASS_NAME_IRDI, IdentifierType.IRDI)));
		classNameProp.setValue(LangStrings.fromStringPairs(classNamePairs));
		setClassName(classNameProp);
	}
	public void setClassName(MultiLanguageProperty className) {
		addSubmodelElement(className);
	}

	public void setClassificationSystem(String classificationSystem) {
		Property classificationSystemProp = new Property(CLASSIFICATION_SYSTEM_ID, ValueType.String);
		classificationSystemProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				CLASSIFICATION_SYSTEM_IRDI, IdentifierType.IRDI)));
		classificationSystemProp.setValue(classificationSystem);
		setClassificationSystem(classificationSystemProp);
	}
	public void setClassificationSystem(Property classificationSystem) {
		addSubmodelElement(classificationSystem);
	}

	/**
	 * Creates a DocumentClassification object from a map
	 *
	 * @param obj
	 *            a DocumentClassification object as raw map
	 * @return a DocumentClassification object, that behaves like a facade for
	 *         the given map
	 */
	public static DocumentClassification createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(DocumentClassification.class, obj);
		}

		DocumentClassification classification = new DocumentClassification();
		classification.setMap(obj);
		return classification;
	}
}
