package org.eclipse.basyx.submodel.types.handoverdocumentation.submodelelementcollections.document;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The SubmodelElementCollection (SMC) Document contains the information for a VDI 2770 “Document”.
 * Such a “Document” can refer to multiple “DocumentVersions”, which are individual SubmodelElementCollections
 * contained within the superordinate “Document” SMC.
 */
public class Document extends SubmodelElementCollection {

	public static final String IDSHORT_DEFAULT = "Document01";
	public static final Reference SEMANTICID = new Reference(
			Collections.singletonList(new Key(KeyElements.CONCEPTDESCRIPTION, false,
					"0173-1#02-ABI500#001/0173-1#01-AHF579#001", KeyType.IRDI)));

	private Document(String idShort) {
		super(idShort);
		setSemanticId(SEMANTICID);
	}

	public Document() {
		this(IDSHORT_DEFAULT);
	}

	public Document(String idShort, DocumentId docId, DocumentClassification classification, DocumentVersion docVersion) {
		this(idShort);
		addSubmodelElement(docId);
		setClassifications(List.of(classification));
		setDocumentVersions(List.of(docVersion));
	}

	// TODO Constraint: at least one classification according to VDI 2770 shall be provided.
	public void setClassifications(Collection<DocumentClassification> classifications) {
		if (classifications != null && !classifications.isEmpty()) {
			classifications.stream().forEach(this::addSubmodelElement);
		}
	}

	public Collection<DocumentClassification> getClassifications() {
		return getSubmodelElements().values().stream()
				.filter(x -> x.getSemanticId().getKeys().containsAll(DocumentClassification.SEMANTIC_ID.getKeys()))
				.map(d -> DocumentClassification.createAsFacade((Map<String, Object>)d))
				.collect(Collectors.toList());
	}

	public void setDocumentVersions(Collection<DocumentVersion> versions) {
		if (versions != null && !versions.isEmpty()) {
			versions.stream().forEach(this::addSubmodelElement);
		}
	}

	public Collection<DocumentVersion> getDocumentVersions() {
		return getSubmodelElements().values().stream()
				.filter(x -> x.getSemanticId().getKeys().containsAll(DocumentVersion.SEMANTIC_ID.getKeys()))
				.map(d -> DocumentVersion.createAsFacade((Map<String, Object>)d))
				.collect(Collectors.toList());
	}

	/**
	 * Creates a Document SMC object from a map
	 *
	 * @param obj
	 *            a Document SMC object as raw map
	 * @return a Document SMC object, that behaves like a facade for the given map
	 */
	public static Document createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Document.class, obj);
		}

		Document doc = new Document();
		doc.setMap(obj);
		return doc;
	}
}
