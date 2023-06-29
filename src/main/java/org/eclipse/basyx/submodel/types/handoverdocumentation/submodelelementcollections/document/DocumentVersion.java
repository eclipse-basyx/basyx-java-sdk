package org.eclipse.basyx.submodel.types.handoverdocumentation.submodelelementcollections.document;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This SubmodelElementCollection holds the information for a VDI2770 DocumentVersion entity.
 */
public class DocumentVersion extends SubmodelElementCollection {

	public static final Reference SEMANTIC_ID = new Reference(
			Collections.singletonList(new Key(KeyElements.CONCEPTDESCRIPTION, false,
					"0173-1#02-ABI503#001/0173-1#01-AHF582#001", KeyType.IRDI)));

	public static final String LANGUAGE_ID = "Language";
	public static final String LANGUAGE_ID_IRDI = "0173-1#02-AAN468#006";
	public static final String DOCUMENT_VERSION_ID = "DocumentVersionId";
	public static final String DOCUMENT_VERSION_IRDI = "0173-1#02-ABI503#001/0173-1#01-AHF582#001";
	public static final String TITLE_ID = "Title";
	public static final String TITLE_IRDI = "0173-1#02-AAO105#002";
	public static final String SUB_TITLE_ID = "Title";
	public static final String SUB_TITLE_IRDI = "0173-1#02-ABH998#001";
	public static final String SUMMARY_ID = "Summary";
	public static final String SUMMARY_IRDI = "0173-1#02-AAO106#002";
	public static final String KEYWORDS_ID = "Keywords";
	public static final String KEYWORDS_IRDI = "0173-1#02-ABH999#001";
	public static final String STATUS_SET_DATE_ID = "StatusSetDate";
	public static final String STATUS_SET_DATE_IRDI = "0173-1#02-ABI000#001";
	public static final String STATUS_VALUE_ID = "StatusValue";
	public static final String STATUS_VALUE_IRDI = "0173-1#02-ABI001#001";
	public static final String ORGANIZATION_NAME_ID = "OrganizationName";
	public static final String ORGANIZATION_NAME_IRDI = "0173-1#02-ABI002#001";
	public static final String ORGANIZATION_OFFICIAL_NAME_ID = "OrganizationOfficialName";
	public static final String ORGANIZATION_OFFICIAL_NAME_IRDI = "0173-1#02-ABI004#001";
	public static final String DIGITAL_FILE_ID = "DigitalFile";
	public static final String DIGITAL_FILE_IRDI = "0173-1#02-ABI504#001/0173- 1#01-AHF583#001";
	public static final String PREVIEW_FILE_ID = "PreviewFile";
	public static final String PREVIEW_FILE_IRDI = "0173-1#02-ABI505#001/0173- 1#01-AHF584#001";
	public static final String REFERS_TO_ID = "RefersTo";
	public static final String REFERS_TO_IRDI = "0173-1#02-ABI006#001";
	public static final String BASED_ON_ID = "BasedOn";
	public static final String BASED_ON_IRDI = "0173-1#02-ABI006#001";
	public static final String TRANSLATION_OF_ID = "TranslationOf";
	public static final String TRANSLATION_OF_IRDI = "0173-1#02-ABI008#001";


	public DocumentVersion(String idShort) {
		super(idShort);
		setSemanticId(SEMANTIC_ID);
	}

	private DocumentVersion() {
		super();
	}

	public void setLanguages(String... languages) {
		AtomicInteger langCounter = new AtomicInteger();
		List<Property> languageProps = Arrays.stream(languages).map(l -> {
				Property languageProp = new Property(LANGUAGE_ID + langCounter.getAndIncrement(), ValueType.String);
				languageProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				LANGUAGE_ID_IRDI, IdentifierType.IRDI)));
				languageProp.setValue(l);
				return languageProp;
		}).collect(Collectors.toList());
		setLanguages(languageProps);
	}
	public void setLanguages(Collection<Property> languages) {
		languages.stream().forEach(this::addSubmodelElement);
	}

	public void setDocumentVersion(String docVersion) {
		Property docVersionProp = new Property(DOCUMENT_VERSION_ID, ValueType.String);
		docVersionProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				DOCUMENT_VERSION_IRDI, IdentifierType.IRDI)));
		docVersionProp.setValue(docVersion);
		setDocumentVersion(docVersionProp);
	}
	public void setDocumentVersion(Property docVersion) {
		addSubmodelElement(docVersion);
	}

	/**
	 *
	 * @param titlePairs an equal number of strings as pairs of language code and title.
	 */
	public void setTitle(String... titlePairs) {
		MultiLanguageProperty titleProp = new MultiLanguageProperty(TITLE_ID);
		titleProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				TITLE_IRDI, IdentifierType.IRDI)));
		titleProp.setValue(LangStrings.fromStringPairs(titlePairs));
		setTitle(titleProp);
	}
	public void setTitle(MultiLanguageProperty title) {
		addSubmodelElement(title);
	}
	/**
	 *
	 * @param subTitlePairs an equal number of strings as pairs of language code and subtitle.
	 */
	public void setSubTitle(String... subTitlePairs) {
		MultiLanguageProperty subTitleProp = new MultiLanguageProperty(SUB_TITLE_ID);
		subTitleProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				SUB_TITLE_IRDI, IdentifierType.IRDI)));
		subTitleProp.setValue(LangStrings.fromStringPairs(subTitlePairs));
		setSubTitle(subTitleProp);
	}
	public void setSubTitle(MultiLanguageProperty subTitle) {
		addSubmodelElement(subTitle);
	}
	/**
	 *
	 * @param summaryPairs an equal number of strings as pairs of language code and summary.
	 */
	public void setSummary(String... summaryPairs) {
		MultiLanguageProperty summaryProp = new MultiLanguageProperty(SUMMARY_ID);
		summaryProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				SUMMARY_IRDI, IdentifierType.IRDI)));
		summaryProp.setValue(LangStrings.fromStringPairs(summaryPairs));
		setSummary(summaryProp);
	}
	public void setSummary(MultiLanguageProperty summary) {
		addSubmodelElement(summary);
	}
	/**
	 *
	 * @param keywordPairs an equal number of strings as pairs of language code and comma separated list of keywords.
	 */
	public void setKeywords(String... keywordPairs) {
		MultiLanguageProperty keywordsProp = new MultiLanguageProperty(KEYWORDS_ID);
		keywordsProp.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				KEYWORDS_IRDI, IdentifierType.IRDI)));
		keywordsProp.setValue(LangStrings.fromStringPairs(keywordPairs));
		setKeywords(keywordsProp);
	}
	public void setKeywords(MultiLanguageProperty keywords) {
		addSubmodelElement(keywords);
	}

	public void setDigitalFile(String path, String mimeType) {
		File digitalFile = new File(path, mimeType);
		digitalFile.setIdShort(DIGITAL_FILE_ID);
		digitalFile.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				DIGITAL_FILE_IRDI, IdentifierType.IRDI)));
		setDigitalFile(digitalFile);
	}
	public void setDigitalFile(File digitalFile) {
		addSubmodelElement(digitalFile);
	}

	public void setPreviewFile(String path, String mimeType) {
		File previewFile = new File(path, mimeType);
		previewFile.setIdShort(PREVIEW_FILE_ID);
		previewFile.setSemanticId(new Reference(new Key(KeyElements.CONCEPTDESCRIPTION, false,
				PREVIEW_FILE_IRDI, IdentifierType.IRDI)));
		setPreviewFile(previewFile);
	}
	public void setPreviewFile(File previewFile) {
		addSubmodelElement(previewFile);
	}

	/**
	 * Creates a DocumentVersion object from a map
	 *
	 * @param obj
	 *            a DocumentVersion object as raw map
	 * @return a DocumentVersion object, that behaves like a facade for
	 *         the given map
	 */
	public static DocumentVersion createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(DocumentVersion.class, obj);
		}

		DocumentVersion documentVersion = new DocumentVersion();
		documentVersion.setMap(obj);
		return documentVersion;
	}
}
