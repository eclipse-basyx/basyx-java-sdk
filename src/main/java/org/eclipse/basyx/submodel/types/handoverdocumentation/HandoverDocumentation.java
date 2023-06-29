package org.eclipse.basyx.submodel.types.handoverdocumentation;

import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.eclipse.basyx.submodel.types.handoverdocumentation.submodelelementcollections.document.Document;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The Submodel defines a set meta data for the handover of documentation
 * from the manufacturer to the operator for industrial equipment.
 *
 * It follows the spec "IDTA 02004-1-2 Handover Documentation" from march 2023
 */
public class HandoverDocumentation extends Submodel {

	public static final Reference SEMANTIC_ID = new Reference(
			Collections.singletonList(new Key(KeyElements.CONCEPTDESCRIPTION, false,
					"0173-1#01-AHF578#001", KeyType.IRDI)));
	public static final String SUBMODEL_ID = "HandoverDocumentation";

	public static final Reference DOC_RELATED_ENTITY_SEMANTICID = new Reference(
			Collections.singletonList(new Key(KeyElements.CONCEPTDESCRIPTION, false,
					"https://admin-shell.io/vdi/2770/1/0/EntityForDocumentation", KeyType.IRDI)));


	public HandoverDocumentation() {
		setIdShort(SUBMODEL_ID);
		setSemanticId(SEMANTIC_ID);
	}

	public void addDocument(Document document) {
		addSubmodelElement(document);
	}

	public List<Document> getDocuments() {
		return getSubmodelElements().values().stream()
				.filter(x -> x.getSemanticId().getKeys().containsAll(Document.SEMANTICID.getKeys()))
				.map(d -> Document.createAsFacade((Map<String, Object>)d))
				.collect(Collectors.toList());
	}

	public void addEntity(Entity docRelatedEntity) {
		docRelatedEntity.setSemanticId(DOC_RELATED_ENTITY_SEMANTICID);
		addSubmodelElement(docRelatedEntity);
	}

	public List<Entity> getEntities() {
		return getSubmodelElements().values().stream()
				.filter(x -> x.getSemanticId().getKeys().containsAll(DOC_RELATED_ENTITY_SEMANTICID.getKeys()))
				.map(d -> Entity.createAsFacade((Map<String, Object>)d))
				.collect(Collectors.toList());
	}
}
