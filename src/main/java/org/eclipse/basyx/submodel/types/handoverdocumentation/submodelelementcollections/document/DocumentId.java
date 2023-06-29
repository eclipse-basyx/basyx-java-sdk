package org.eclipse.basyx.submodel.types.handoverdocumentation.submodelelementcollections.document;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

import java.util.Collections;

/**
 * This SubmodelElementCollection holds the information for a VDI 2770 DocumentIdDomain entity and the DocumentId property.
 */
public class DocumentId extends SubmodelElementCollection {

	public static final Reference SEMANTIC_ID = new Reference(
			Collections.singletonList(new Key(KeyElements.CONCEPTDESCRIPTION, false,
					"0173-1#02-ABI501#001/0173-1#01-AHF580#001", KeyType.IRDI)));
	public static final String DOCUMENT_DOMAIN_ID = "DocumentDomainId";
	public static final String DOCUMENT_DOMAIN_ID_IRDI = "0173-1#02-ABH994#001";
	public static final String VALUE_ID = "ValueId";
	public static final String VALUE_ID_IRDI = "0173-1#02-AAO099#002";
	public static final String IS_PRIMARY_ID = "IsPrimary";
	public static final String IS_PRIMARY_ID_IRDI = "0173-1#02-ABH995#001";


	public DocumentId(String idShort, String domainId, String valueId) {
		this(idShort, domainId, valueId, false);
	}
	public DocumentId(String idShort, String domainId, String valueId, boolean isPrimary) {
		super(idShort);
		setSemanticId(SEMANTIC_ID);
		setDomainId(domainId);
		setValueId(valueId);
		setIsPrimary(isPrimary);
	}

	public void setDomainId(String domainId) {
		Property domainIdProp = new Property(DOCUMENT_DOMAIN_ID, ValueType.String);
		domainIdProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				DOCUMENT_DOMAIN_ID_IRDI, IdentifierType.IRDI)));
		domainIdProp.setValue(domainId);
		setDomainId(domainIdProp);
	}
	public void setDomainId(Property domainId) {
		addSubmodelElement(domainId);
	}

	public void setValueId(String valueId) {
		Property valueIdProp = new Property(VALUE_ID, ValueType.String);
		valueIdProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				VALUE_ID_IRDI, IdentifierType.IRDI)));
		valueIdProp.setValue(valueId);
		setValueId(valueIdProp);
	}
	public void setValueId(Property isPrimary) {
		addSubmodelElement(isPrimary);
	}

	public void setIsPrimary(boolean isPrimary) {
		Property isPrimaryProp = new Property(IS_PRIMARY_ID, ValueType.Boolean);
		isPrimaryProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				IS_PRIMARY_ID_IRDI, IdentifierType.IRDI)));
		isPrimaryProp.setValue(isPrimary);
		setIsPrimary(isPrimaryProp);
	}

	public void setIsPrimary(Property isPrimary) {
		addSubmodelElement(isPrimary);
	}

}
