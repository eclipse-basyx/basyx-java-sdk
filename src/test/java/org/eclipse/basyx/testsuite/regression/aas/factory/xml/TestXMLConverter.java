/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.factory.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.eclipse.basyx.aas.factory.xml.MetamodelToXMLConverter;
import org.eclipse.basyx.aas.factory.xml.XMLToMetamodelConverter;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.IConceptDictionary;
import org.eclipse.basyx.aas.metamodel.api.parts.IView;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IDataSpecificationContent;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IHasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IDataElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IRange;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.entity.EntityType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.dataspecification.DataSpecificationIEC61360Content;
import org.eclipse.basyx.submodel.metamodel.map.parts.ConceptDescription;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.Capability;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.Blob;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.MultiLanguageProperty;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.ReferenceElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.entity.Entity;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.event.BasicEvent;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.AnnotatedRelationshipElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.relationship.RelationshipElement;
import org.eclipse.basyx.vab.model.VABModelMap;
import org.eclipse.basyx.vab.support.TypeDestroyer;
import org.junit.Before;
import org.junit.Test;

public class TestXMLConverter {

	private String xmlInPath = "src/test/resources/aas/factory/xml/in.xml";
	private String xmlInExternalAllowedPath = "src/test/resources/aas/factory/xml/inExternalAllowed.xml";
	
	private XMLToMetamodelConverter converter;
	
	@Before
	public void buildConverter() throws Exception {
		String xml = new String(Files.readAllBytes(Paths.get(xmlInPath)));
		converter = new XMLToMetamodelConverter(xml);
	}

	@Test
	public void testParseAAS() throws Exception {
		checkAASs(converter.parseAAS());
	}
	
	@Test
	public void testParseAssets() throws Exception {
		checkAssets(converter.parseAssets());
	}
	
	@Test
	public void testParseConceptDescriptions() {
		checkConceptDescriptions(converter.parseConceptDescriptions());
	}
	
	@Test
	public void testParseSubmodels() {
		checkSubmodels(converter.parseSubmodels());
	}
	
	@Test
	public void testBuildXML() throws Exception {
		//Convert the in.xml to Objects
		List<IAssetAdministrationShell> assetAdministrationShellList = converter.parseAAS();
		List<IAsset> assetList = converter.parseAssets();
		List<IConceptDescription> conceptDescriptionList = converter.parseConceptDescriptions();
		List<ISubmodel> submodelList = converter.parseSubmodels();
		
		//Build XML-File from the Objects and write it to a StringWriter
		StringWriter resultWithTypes = new StringWriter();
		MetamodelToXMLConverter.convertToXML(assetAdministrationShellList, assetList, conceptDescriptionList, submodelList, new StreamResult(resultWithTypes));
		
		
		//Read the content of the StringWriter, convert it into Objects and check them
		XMLToMetamodelConverter converterWithTypes = new XMLToMetamodelConverter(resultWithTypes.toString());
	
		checkAASs(converterWithTypes.parseAAS());
		checkAssets(converterWithTypes.parseAssets());
		checkConceptDescriptions(converterWithTypes.parseConceptDescriptions());
		checkSubmodels(converterWithTypes.parseSubmodels());
		
		//erase the types of the Objects, that they are plain Maps as if they were transferred over the VAB
		List<IAssetAdministrationShell> iAssetAdministrationShellList = destroyAASTypes(assetAdministrationShellList);
		List<IAsset> iAssetList = destroyAssetTypes(assetList);
		List<IConceptDescription> iConceptDescriptionList = destroyConceptDescriptionTypes(conceptDescriptionList);
		List<ISubmodel> iSubmodelList = destroySubmodelTypes(submodelList);
		
		//Build XML-File from the Objects and write it to a StringWriter
		StringWriter resultWithoutTypes = new StringWriter();
		MetamodelToXMLConverter.convertToXML(iAssetAdministrationShellList, iAssetList, iConceptDescriptionList, iSubmodelList, new StreamResult(resultWithoutTypes));
		
		
		//Read the content of the StringWriter, convert it into Objects and check them
		XMLToMetamodelConverter converterWithoutTypes = new XMLToMetamodelConverter(resultWithoutTypes.toString());
		
		checkAASs(converterWithoutTypes.parseAAS());
		checkAssets(converterWithoutTypes.parseAssets());
		checkConceptDescriptions(converterWithoutTypes.parseConceptDescriptions());
		checkSubmodels(converterWithoutTypes.parseSubmodels());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testBuildExternalAllowedXML() throws Exception {
		String xml = new String(Files.readAllBytes(Paths.get(xmlInExternalAllowedPath)));
		converter = new XMLToMetamodelConverter(xml);
		//Convert the in.xml to Objects
		List<IAssetAdministrationShell> assetAdministrationShellList = converter.parseAAS();
		List<IAsset> assetList = converter.parseAssets();
		List<IConceptDescription> conceptDescriptionList = converter.parseConceptDescriptions();
		List<ISubmodel> submodelList = converter.parseSubmodels();
		
		IAssetAdministrationShell secondShell = assetAdministrationShellList.get(1);
		
		assertEquals("", secondShell.getIdShort());
		IIdentifier identifier = secondShell.getIdentification();
		assertEquals(IdentifierType.CUSTOM, identifier.getIdType());
		assertEquals("", identifier.getId());
		IKey key = secondShell.getAssetReference().getKeys().get(0);
		assertEquals(KeyElements.REFERENCEELEMENT, key.getType());
		assertEquals(KeyType.IRI, key.getIdType());
		
		IAsset asset = assetList.get(1);
		assertEquals(IdentifierType.IRI, asset.getIdentification().getIdType());
		
		IProperty property1 = submodelList.get(0).getProperties().get("rotationSpeed");
		assertEquals(ValueType.AnySimpleType, property1.getValueType());

		IProperty property2 = submodelList.get(0).getProperties().get("emptyDouble");
		assertEquals(ValueType.AnyURI, property2.getValueType());
		
		IProperty property3 = submodelList.get(0).getProperties().get("emptyDouble2");
		assertEquals(ValueType.DateTime, property3.getValueType());
		
		IRange range = Range.createAsFacade((Map<String, Object>) submodelList.get(0).getSubmodelElement("range_id"));
		assertEquals(ValueType.Double, range.getValueType());
		
		IOperation operation = Operation.createAsFacade((Map<String, Object>) submodelList.get(0).getSubmodelElement("operation_ID"));
		assertEquals(ModelingKind.TEMPLATE, operation.getInOutputVariables().iterator().next().getValue().getModelingKind());
		
		IDataSpecificationContent content = conceptDescriptionList.get(0).getEmbeddedDataSpecifications().iterator().next().getContent();
		DataSpecificationIEC61360Content parsed = DataSpecificationIEC61360Content.createAsFacade((Map<String, Object>) content);
		assertEquals("Only Description", parsed.getShortName().get("EN"));  
	}
	
	private void checkAASs(List<IAssetAdministrationShell> aasList) {
		assertEquals(2, aasList.size());
		
		IAssetAdministrationShell aas = null;
		
		//select the AAS with a specific ID form the list
		for(IAssetAdministrationShell shell: aasList) {
			if(shell.getIdShort().equals("asset_admin_shell")) {
				aas = shell;
				break;
			}
		}
		
		assertNotNull(aas);
		
		assertEquals("asset_admin_shell", aas.getIdShort());
		assertEquals("test_category", aas.getCategory());
		assertEquals("aas_parent_id", aas.getParent().getKeys().get(0).getValue());
		
		assertEquals("aas_Description", aas.getDescription().get("EN"));
		assertEquals("Beschreibung Verwaltungsschale", aas.getDescription().get("DE"));
		
		assertEquals("www.admin-shell.io/aas-sample/1/0", aas.getIdentification().getId());
		assertEquals(IdentifierType.IRI, aas.getIdentification().getIdType());

		checkDefaultEmbeddedDataSpecification(aas);
		
		assertEquals("1", aas.getAdministration().getVersion());
		assertEquals("0", aas.getAdministration().getRevision());
		
		Collection<IConceptDictionary> conceptDictionary = aas.getConceptDictionary();
		for (IConceptDictionary iConceptDictionary : conceptDictionary) {
			assertEquals("SampleDic", iConceptDictionary.getIdShort());

			// Test concept description reference
			Collection<IReference> conceptDescriptionRefs = iConceptDictionary.getConceptDescriptionReferences();
			assertEquals(1, conceptDescriptionRefs.size());
			IKey key = conceptDescriptionRefs.iterator().next().getKeys().iterator().next();
			assertEquals(KeyElements.CONCEPTDESCRIPTION, key.getType());
			assertEquals(KeyType.IRI, key.getIdType());
			assertEquals("www.festo.com/dic/08111234", key.getValue());
			assertEquals(true, key.isLocal());

			// Test concept description
			Collection<IConceptDescription> conceptDescriptions = iConceptDictionary.getConceptDescriptions();
			assertEquals(1, conceptDescriptions.size());
			IConceptDescription desc = conceptDescriptions.iterator().next();
			assertEquals(IdentifierType.IRI, desc.getIdentification().getIdType());
			assertEquals("www.festo.com/dic/08111234", desc.getIdentification().getId());
			assertEquals("cs_category", desc.getCategory());
			checkDefaultEmbeddedDataSpecification(desc);

		}

		// Test submodel reference retrieval
		
		// Select submodel reference's key
		IKey submodelKey = null;
		boolean foundFirst = false, foundSecond = false;
		for (IReference ref : aas.getSubmodelReferences()) {
			for (IKey k : ref.getKeys())
				if (k.getValue().equals("http://www.zvei.de/demo/submodel/12345679")) {
					submodelKey = k;
					foundFirst = true;
					break;
				} else if (k.getValue().equals("http://www.zvei.de/demo/submodel/12345679_2")) {
					foundSecond = true;
					break;
				}
		}
		// Assert that both submodel references have been found
		assertTrue(foundFirst);
		assertTrue(foundSecond);

		assertNotNull(submodelKey);

		// Equality of value is already guaranteed by selection criteria
		assertEquals(KeyType.IRI, submodelKey.getIdType());
		assertEquals(KeyElements.SUBMODEL, submodelKey.getType());
		assertEquals(true, submodelKey.isLocal());
		
		// Test view retrieval
		Object[] views = aas.getViews().toArray();
		assertEquals(2, views.length);
		IView iView = (IView) views[0];
		if(!iView.getIdShort().equals("SampleView"))
			iView = (IView) views[1];
		assertEquals("SampleView", iView.getIdShort());
		checkDefaultEmbeddedDataSpecification(iView);
		Collection<IReference> containedElement = iView.getContainedElement();
		IReference ref = containedElement.iterator().next();

		// Text keys
		IKey key0 = ref.getKeys().get(0);
		assertEquals(KeyElements.SUBMODEL, key0.getType());
		assertEquals(KeyType.IRI, key0.getIdType());
		assertEquals("\"http://www.zvei.de/demo/submodel/12345679\"", key0.getValue());
		assertEquals(true, key0.isLocal());

		IKey key1 = ref.getKeys().get(1);
		assertEquals(KeyElements.PROPERTY, key1.getType());
		assertEquals(KeyType.IDSHORT, key1.getIdType());
		assertEquals("rotationSpeed", key1.getValue());
		assertEquals(true, key1.isLocal());
	}
	
	private void checkDefaultEmbeddedDataSpecification(IHasDataSpecification hasDataSpecification) {
		Collection<IEmbeddedDataSpecification> embeddedSpecs = hasDataSpecification.getEmbeddedDataSpecifications();
		IEmbeddedDataSpecification embeddedSpec = embeddedSpecs.iterator().next();
		checkDefaultDataSpecificationReference(embeddedSpec.getDataSpecificationTemplate());
		checkDefaultDataSpecificationContent(embeddedSpec.getContent());
	}

	@SuppressWarnings("unchecked")
	private void checkDefaultDataSpecificationContent(IDataSpecificationContent content) {
		DataSpecificationIEC61360Content iecContent = DataSpecificationIEC61360Content
				.createAsFacade((Map<String, Object>) content);
		LangStrings preferredName = iecContent.getPreferredName();
		assertEquals("Drehzahl", preferredName.get("DE"));
		assertEquals("Rotation Speed", preferredName.get("EN"));
		LangStrings shortName = iecContent.getShortName();
		assertEquals("N", shortName.get("DE"));
		assertEquals("1/min", iecContent.getUnit());
		IReference unitId = iecContent.getUnitId();
		IKey unitIdKey = unitId.getKeys().iterator().next();
		assertEquals(KeyType.IRDI, unitIdKey.getIdType());
		assertEquals(KeyElements.GLOBALREFERENCE, unitIdKey.getType());
		assertEquals("0173-1#05-AAA650#002", unitIdKey.getValue());
		assertEquals("NR1..5", iecContent.getValueFormat());
	}

	private void checkDefaultDataSpecificationReference(IReference dataSpecRef) {
		IKey dataSpecKey = dataSpecRef.getKeys().iterator().next();
		assertEquals(1, dataSpecRef.getKeys().size());
		assertEquals(KeyType.IRI, dataSpecKey.getIdType());
		assertEquals(KeyElements.GLOBALREFERENCE, dataSpecKey.getType());
		assertFalse(dataSpecKey.isLocal());
		assertEquals("www.admin-shell.io/DataSpecificationTemplates/DataSpecificationIEC61360", dataSpecKey.getValue());
	}

	private void checkAssets(List<IAsset> assets) {
		assertEquals(2, assets.size());
		IAsset asset = null;
		
		//select the Asset with a specific ID form the list
		for(IAsset a: assets) {
			if(a.getIdShort().equals("3s7plfdrs35_asset1")) {
				asset = a;
				break;
			}
		}
		
		assertNotNull(asset);
		checkDefaultEmbeddedDataSpecification(asset);
		assertEquals("3s7plfdrs35_asset1", asset.getIdShort());
		assertEquals("asset1_Description", asset.getDescription().get("EN"));
		assertEquals(IdentifierType.IRI, asset.getIdentification().getIdType());
		assertEquals("Type", asset.getAssetKind().toString());
		assertEquals("www.festo.com/dic/08111234", asset.getAssetIdentificationModel().getKeys().get(0).getValue());
	}
	
	private void checkConceptDescriptions(List<IConceptDescription> conceptDescriptions) {
		assertEquals(2, conceptDescriptions.size());
		IConceptDescription conceptDescription = conceptDescriptions.get(0);
		
		//make sure to select the correct ConceptDescription1 from the list
		//as there is no order given by the XML file
		if(!conceptDescription.getIdShort().equals("conceptDescription1"))
			conceptDescription = conceptDescriptions.get(1);
		
		assertEquals("conceptDescription1", conceptDescription.getIdShort());
		assertEquals("conceptDescription_Description", conceptDescription.getDescription().get("EN"));
		assertEquals("www.festo.com/dic/08111234", conceptDescription.getIdentification().getId());
		Collection<IReference> refs = conceptDescription.getIsCaseOf();
		assertEquals(1, refs.size());
	}
	
	private void checkSubmodels(List<ISubmodel> submodels) {
		assertEquals(2, submodels.size());
		ISubmodel submodel = null;
		
		//select the Submodel with a specific ID form the list
		for(ISubmodel s: submodels) {
			if(s.getIdShort().equals("3s7plfdrs35_submodel1")) {
				submodel = s;
				break;
			}
		}
		
		assertNotNull(submodel);
		checkDefaultEmbeddedDataSpecification(submodel);
		assertEquals("3s7plfdrs35_submodel1", submodel.getIdShort());
		Collection<IConstraint> constraints = submodel.getQualifiers();
		assertEquals(4, constraints.size());
		checkSubmodelElements(submodel);
	}
	
	@SuppressWarnings("unchecked")
	private void checkSubmodelElements(ISubmodel submodel) {
		Map<String, ISubmodelElement> submodelElements = (Map<String, ISubmodelElement>)
				((Map<String, Object>)submodel).get(Submodel.SUBMODELELEMENT);
				assertEquals(14, submodelElements.size());
		
		ISubmodelElement element = submodelElements.get("rotationSpeed");
		assertTrue(element instanceof Property);
		Property property = (Property) element;
		checkDefaultEmbeddedDataSpecification(property);
		List<IKey> keys = property.getValueId().getKeys();
		assertEquals(1, keys.size());
		assertEquals("0173-1#05-AAA650#002", keys.get(0).getValue());
		assertEquals(2000.0, property.getValue());
		assertEquals(ValueType.Double, property.getValueType());
		assertEquals("rotationSpeed", property.getIdShort());
		
		element = submodelElements.get("emptyDouble");
		assertTrue(element instanceof Property);
		property = (Property) element;
		assertEquals(ValueType.Double, property.getValueType());
		
		element = submodelElements.get("basic_event_id");
		assertTrue(element instanceof BasicEvent);
		BasicEvent basicEvent = (BasicEvent) element;
		keys = basicEvent.getObserved().getKeys();
		assertEquals(1, keys.size());
		assertEquals("http://www.zvei.de/demo/submodelDefinitions/87654346", keys.get(0).getValue());

		element = submodelElements.get("capability_id");
		assertTrue(element instanceof Capability);
		
		element = submodelElements.get("entity_id");
		assertTrue(element instanceof Entity);
		Entity entity = (Entity) element;
		assertTrue(entity.getEntityType().equals(EntityType.COMANAGEDENTITY));
		Collection<ISubmodelElement> statements = entity.getStatements();
		assertEquals(2, statements.size());
		assertTrue(statements.stream().allMatch(s -> s instanceof File || s instanceof Range));
		
		element = submodelElements.get("multi_language_property_id");
		assertTrue(element instanceof MultiLanguageProperty);
		MultiLanguageProperty mLProperty = (MultiLanguageProperty) element;
		keys = mLProperty.getValueId().getKeys();
		assertEquals(1, keys.size());
		assertEquals("0173-1#05-AAA650#002", keys.get(0).getValue());
		LangStrings langStrings = mLProperty.getValue();
		assertEquals(2, langStrings.size());
		assertEquals("Eine Beschreibung auf deutsch", langStrings.get("de"));
		assertEquals("A description in english", langStrings.get("en"));
		
		element = submodelElements.get("range_id");
		assertTrue(element instanceof Range);
		Range range = (Range) element;
		assertEquals(ValueType.Integer, range.getValueType());
		assertEquals("1", range.getMin());
		assertEquals("10", range.getMax());
		
		element = submodelElements.get("file_id");
		assertTrue(element instanceof File);
		File file = (File) element;
		assertEquals("file_mimetype", file.getMimeType());
		assertEquals("file_value", file.getValue());
		
		element = submodelElements.get("blob_id");
		assertTrue(element instanceof Blob);
		Blob blob = (Blob) element;
		assertEquals("blob_mimetype", blob.getMimeType());
		assertEquals("YmxvYit2YWx1ZQ==", blob.getValue());
		
		element = submodelElements.get("reference_ELE_ID");
		assertTrue(element instanceof ReferenceElement);
		ReferenceElement refElem = (ReferenceElement) element;
		keys = refElem.getValue().getKeys();
		assertEquals(1, keys.size());
		assertEquals("0173-1#05-AAA650#002", keys.get(0).getValue());
		
		element = submodelElements.get("submodelElementCollection_ID");
		assertTrue(element instanceof SubmodelElementCollection);
		SubmodelElementCollection smCollection = (SubmodelElementCollection) element;
		Collection<ISubmodelElement> elements = smCollection.getValue();
		assertEquals(2, elements.size());
		assertTrue(smCollection.isAllowDuplicates());
		assertFalse(smCollection.isOrdered());
		
		element = submodelElements.get("relationshipElement_ID");
		assertTrue(element instanceof RelationshipElement);
		RelationshipElement relElem = (RelationshipElement) element;
		keys = relElem.getFirst().getKeys();
		assertEquals(1, keys.size());
		assertEquals("0173-1#05-AAA650#001", keys.get(0).getValue());
		keys = relElem.getSecond().getKeys();
		assertEquals(1, keys.size());
		assertEquals("0173-1#05-AAA650#002", keys.get(0).getValue());
		
		element = submodelElements.get("annotatedRelationshipElement_ID");
		assertTrue(element instanceof AnnotatedRelationshipElement);
		AnnotatedRelationshipElement annotatedElem = (AnnotatedRelationshipElement) element;
		keys = annotatedElem.getFirst().getKeys();
		assertEquals(1, keys.size());
		assertEquals("0173-1#05-AAA650#003", keys.get(0).getValue());
		keys = annotatedElem.getSecond().getKeys();
		assertEquals(1, keys.size());
		assertEquals("0173-1#05-AAA650#004", keys.get(0).getValue());
		Collection<IDataElement> annotations = annotatedElem.getValue().getAnnotations();
		assertEquals(2, annotations.size());
		for(IDataElement annotationElement: annotations) {
			assertTrue(annotationElement instanceof Property);
		}
		
		element = submodelElements.get("operation_ID");
		assertTrue(element instanceof Operation);
		Operation op = (Operation) element;
		Collection<IOperationVariable> parameters = op.getInputVariables();
		assertEquals(2, parameters.size());
		Collection<IOperationVariable> returns = op.getOutputVariables();
		assertEquals(1, returns.size());
		Object o = returns.iterator().next().getValue();
		assertTrue(o instanceof ReferenceElement);
		Collection<IOperationVariable> inout = op.getInOutputVariables();
		assertEquals(1, inout.size());
		o = inout.iterator().next().getValue();
		assertTrue(o instanceof ReferenceElement);
	}
	
	@SuppressWarnings("unchecked")
	private List<IAssetAdministrationShell> destroyAASTypes(List<IAssetAdministrationShell> aasList) {
		List<IAssetAdministrationShell> ret = new ArrayList<>();
		for(IAssetAdministrationShell aas: aasList) {
			ret.add(AssetAdministrationShell.createAsFacade(TypeDestroyer.destroyType((Map<String, Object>) aas)));
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private List<ISubmodel> destroySubmodelTypes(List<ISubmodel> submodelList) {
		List<ISubmodel> ret = new ArrayList<>();
		for(ISubmodel submodel: submodelList) {
			ret.add(Submodel.createAsFacade(new VABModelMap<>(TypeDestroyer.destroyType((Map<String, Object>) submodel))));
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private List<IAsset> destroyAssetTypes(List<IAsset> assetList) {
		List<IAsset> ret = new ArrayList<>();
		for(IAsset asset: assetList) {
			ret.add(Asset.createAsFacade(TypeDestroyer.destroyType((Map<String, Object>) asset)));

		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private List<IConceptDescription> destroyConceptDescriptionTypes(List<IConceptDescription> cdList) {
		List<IConceptDescription> ret = new ArrayList<>();
		for(IConceptDescription cd: cdList) {
			ret.add(ConceptDescription.createAsFacade(TypeDestroyer.destroyType((Map<String, Object>) cd)));
		}
		return ret;
	}
	
}
