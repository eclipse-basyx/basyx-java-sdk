/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.aas.metamodel.connected;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.IConceptDictionary;
import org.eclipse.basyx.aas.metamodel.api.parts.IView;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.api.security.ISecurity;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.metamodel.map.parts.ConceptDictionary;
import org.eclipse.basyx.aas.metamodel.map.parts.View;
import org.eclipse.basyx.aas.metamodel.map.security.Security;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IAdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedElement;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.facade.SubmodelElementMapCollectionConverter;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.reference.ReferenceHelper;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * "Connected" implementation of IAssetAdministrationShell
 * 
 * @author rajashek, Zai Zhang, schnicke
 *
 */
public class ConnectedAssetAdministrationShell extends ConnectedElement implements IAssetAdministrationShell {
	/**
	 * Constructor creating a ConnectedAAS pointing to the AAS represented by proxy
	 * 
	 * @param proxy
	 */
	public ConnectedAssetAdministrationShell(VABElementProxy proxy) {
		super(proxy);
	}

	/**
	 * Constructor creating a ConnectedAAS pointing to the AAS represented by proxy
	 * and an already cached local copy
	 * 
	 * @param proxy
	 * @param localCopy
	 */
	public ConnectedAssetAdministrationShell(VABElementProxy proxy, AssetAdministrationShell localCopy) {
		super(proxy);
	}

	/**
	 * Copy constructor, allowing to create a ConnectedAAS pointing to the same AAS
	 * as <i>shell</i>
	 * 
	 * @param shell
	 */
	public ConnectedAssetAdministrationShell(ConnectedAssetAdministrationShell shell) {
		super(shell.getProxy());
	}

	@Override
	public IAdministrativeInformation getAdministration() {
		return Identifiable.createAsFacade(getElem(), getKeyElement()).getAdministration();
	}

	@Override
	public IIdentifier getIdentification() {
		return Identifiable.createAsFacade(getElem(), getKeyElement()).getIdentification();
	}

	@Override
	public Collection<IReference> getDataSpecificationReferences() {
		return HasDataSpecification.createAsFacade(getElem()).getDataSpecificationReferences();
	}

	@Override
	public Collection<IEmbeddedDataSpecification> getEmbeddedDataSpecifications() {
		return HasDataSpecification.createAsFacade(getElem()).getEmbeddedDataSpecifications();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ISecurity getSecurity() {
		return Security.createAsFacade((Map<String, Object>) getElem().getPath(AssetAdministrationShell.SECURITY));
	}

	@SuppressWarnings("unchecked")
	@Override
	public IReference getDerivedFrom() {
		return Reference.createAsFacade((Map<String, Object>) getElem().getPath(AssetAdministrationShell.DERIVEDFROM));
	}

	@SuppressWarnings("unchecked")
	@Override
	public IAsset getAsset() {
		return Asset.createAsFacade((Map<String, Object>) getElem().getPath(AssetAdministrationShell.ASSET));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IView> getViews() {
		Collection<Map<String, Object>> coll = (Collection<Map<String, Object>>) getElem().getPath(AssetAdministrationShell.VIEWS);
		return coll.stream().map(View::createAsFacade).collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IConceptDictionary> getConceptDictionary() {
		Collection<Map<String, Object>> set = (Collection<Map<String, Object>>) getElem().getPath(AssetAdministrationShell.CONCEPTDICTIONARY);
		return set.stream().map(ConceptDictionary::createAsFacade).collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ISubmodel> getSubmodels() {
		Collection<Map<String, Object>> submodelCollection = (Collection<Map<String, Object>>) getProxy().getValue(AssetAdministrationShell.SUBMODELS);

		Map<String, ISubmodel> ret = new LinkedHashMap<>();

		for (Map<String, Object> m : submodelCollection) {
			Submodel sm = Submodel.createAsFacade(m);
			String path = VABPathTools.concatenatePaths(AssetAdministrationShell.SUBMODELS, sm.getIdShort(), SubmodelProvider.SUBMODEL);
			ret.put(sm.getIdShort(), new ConnectedSubmodel(getProxy().getDeepProxy(path), sm));
		}

		return ret;
	}

	@Override
	public void addSubmodel(Submodel subModel) {
		subModel.setParent(getReference());
		Map<String, Object> convertedMap = SubmodelElementMapCollectionConverter.smToMap(subModel);
		String accessPath = VABPathTools.concatenatePaths(AssetAdministrationShell.SUBMODELS, subModel.getIdShort());
		getProxy().setValue(accessPath, convertedMap);
	}

	@Override
	public String getIdShort() {
		return (String) getElem().get(Referable.IDSHORT);
	}

	@Override
	public String getCategory() {
		return (String) getElem().get(Referable.CATEGORY);
	}

	@Override
	public LangStrings getDescription() {
		return Referable.createAsFacade(getElem(), getKeyElement()).getDescription();
	}

	@SuppressWarnings("unchecked")
	@Override
	public IReference getParent() {
		return Reference.createAsFacade((Map<String, Object>) getElem().getPath(Referable.PARENT));
	}

	@Override
	public Collection<IReference> getSubmodelReferences() {
		return ReferenceHelper.transform(getElemLive().getPath(AssetAdministrationShell.SUBMODELS));
	}

	@SuppressWarnings("unchecked")
	@Override
	public IReference getAssetReference() {
		return Reference.createAsFacade((Map<String, Object>) getElem().getPath(AssetAdministrationShell.ASSETREF));
	}

	private KeyElements getKeyElement() {
		return KeyElements.ASSETADMINISTRATIONSHELL;
	}

	@Override
	public IReference getReference() {
		return Identifiable.createAsFacade(getElem(), getKeyElement()).getReference();
	}

	/**
	 * Returns a local copy of the AAS, i.e. a snapshot of the current state. <br>
	 * No changes of this copy are reflected in the remote AAS
	 * 
	 * @return the local copy
	 */
	public AssetAdministrationShell getLocalCopy() {
		return AssetAdministrationShell.createAsFacade(getElem());
	}

	@Override
	public void removeSubmodel(IIdentifier id) {
		ISubmodel sm = getSubmodel(id);

		String path = VABPathTools.concatenatePaths(AssetAdministrationShell.SUBMODELS, sm.getIdShort());
		getProxy().deleteValue(path);
	}

	@Override
	public ISubmodel getSubmodel(IIdentifier id) {
		// FIXME: Change this when AAS API supports Submodel retrieval by Identifier
		Optional<ISubmodel> op = getSubmodels().values().stream().filter(sm -> sm.getIdentification().getId().equals(id.getId())).findFirst();
		if (!op.isPresent()) {
			throw new ResourceNotFoundException("AAS " + getIdentification() + " does not have a submodel with id " + id);
		}
		return op.get();
	}
}
