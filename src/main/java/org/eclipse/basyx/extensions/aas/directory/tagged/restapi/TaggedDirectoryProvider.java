/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.extensions.aas.directory.tagged.restapi;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TagType;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedAASDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.TaggedSubmodelDescriptor;
import org.eclipse.basyx.extensions.aas.directory.tagged.map.MapTaggedDirectory;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

import com.google.common.base.Splitter;

public class TaggedDirectoryProvider extends AASRegistryModelProvider {
	private MapTaggedDirectory directory;
	public static final String PREFIX = "api/v1/directory";
	public static final String API_ACCESS = "tags=";
	public static final String SUBMODEL_API_ACCESS = "submodelTags=";

	public TaggedDirectoryProvider() {
		this(new MapTaggedDirectory(new LinkedHashMap<>(), new LinkedHashMap<>()));
	}

	public TaggedDirectoryProvider(MapTaggedDirectory directory) {
		super(directory);
		this.directory = directory;
	}

	@Override
	public Object getValue(String path) throws ProviderException {
		path = VABPathTools.stripSlashes(path);
		if (path.startsWith(PREFIX)) {
			if (path.contains(API_ACCESS) && path.contains(SUBMODEL_API_ACCESS)) {
				return directory.lookupBothAasAndSubmodelTags(extractTags(path, TagType.AAS.getStandardizedLiteral()), extractTags(path, TagType.SUBMODEL.getStandardizedLiteral()));
			} else if (path.contains(SUBMODEL_API_ACCESS)) {
				return directory.lookupSubmodelTags(extractTags(path, TagType.SUBMODEL.getStandardizedLiteral()));
			} else {
				return directory.lookupTags(extractTags(path, TagType.AAS.getStandardizedLiteral()));
			}
		} else {
			return super.getValue(path);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createValue(String path, Object newEntity) throws ProviderException {
		path = VABPathTools.decodePathElement(path);
		path = VABPathTools.stripSlashes(path);
		if (path.startsWith(PREFIX)) {
			if (path.contains("/submodels/")) {
				registerSubmodel(path, newEntity);
			} else {
				directory.register(TaggedAASDescriptor.createAsFacade((Map<String, Object>) newEntity));
			}
		} else {
			super.createValue(path, newEntity);
		}
	}

	@SuppressWarnings("unchecked")
	private void registerSubmodel(String path, Object newEntity) {
		String aasIdWithSlashes = path.replace(PREFIX, "").replace(path.substring(path.indexOf("/submodels/")), "");
		String aasIdWithoutSlashes = VABPathTools.stripSlashes(aasIdWithSlashes);

		Identifier id = new Identifier();
		id.setId(aasIdWithoutSlashes);
		directory.registerSubmodel(id, TaggedSubmodelDescriptor.createAsFacade((Map<String, Object>) newEntity));
	}

	private Set<String> extractTags(String path, String tagType) {
		path = VABPathTools.stripSlashes(path);
		path = path.split("\\?")[1];
		Map<String, String> queryParams = Splitter.on("&").trimResults().withKeyValueSeparator("=").split(path);
		String tags = queryParams.get(tagType);

		return getTagsAsSet(tags);
	}

	private Set<String> getTagsAsSet(String tags) {
		if (tags.isEmpty()) {
			return new LinkedHashSet<>();
		} else {
			return Arrays.stream(tags.split(",")).collect(Collectors.toSet());
		}
	}

}
