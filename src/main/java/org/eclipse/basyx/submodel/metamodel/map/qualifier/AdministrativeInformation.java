/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.qualifier;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.dataspecification.IEmbeddedDataSpecification;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.IAdministrativeInformation;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.vab.model.VABModelMap;

import com.google.common.base.Strings;

/**
 * AdministrativeInformation class
 * 
 * @author kuhn
 *
 */
public class AdministrativeInformation extends VABModelMap<Object> implements IAdministrativeInformation {
	public static final String VERSION = "version";

	public static final String REVISION = "revision";

	/**
	 * Constructor
	 */
	public AdministrativeInformation() {
		// Add qualifier
		putAll(new HasDataSpecification());

	}

	/**
	 * Constructor
	 */
	public AdministrativeInformation(String version, String revision) {
		// Add qualifier
		putAll(new HasDataSpecification());

		setVersionInformation(version, revision);
	}

	/**
	 * Creates a AdministrativeInformation object from a map
	 * 
	 * @param map
	 *            a AdministrativeInformation object as raw map
	 * @return a AdministrativeInformation object, that behaves like a facade for
	 *         the given map
	 */
	public static AdministrativeInformation createAsFacade(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		AdministrativeInformation ret = new AdministrativeInformation();
		ret.setMap(map);
		return ret;
	}

	@Override
	public Collection<IReference> getDataSpecificationReferences() {
		return HasDataSpecification.createAsFacade(this).getDataSpecificationReferences();
	}

	public void setDataSpecificationReferences(Collection<IReference> ref) {
		HasDataSpecification.createAsFacade(this).setDataSpecificationReferences(ref);
	}

	@Override
	public Collection<IEmbeddedDataSpecification> getEmbeddedDataSpecifications() {
		return HasDataSpecification.createAsFacade(this).getEmbeddedDataSpecifications();
	}

	public void setEmbeddedDataSpecifications(Collection<IEmbeddedDataSpecification> embeddedDataSpecifications) {
		HasDataSpecification.createAsFacade(this).setEmbeddedDataSpecifications(embeddedDataSpecifications);
	}
	
	/**
	 * Sets version and revision
	 * @param version
	 * @param revision 
	 * 
	 * @throws RuntimeException when revision is given without a valid version
	 */
	public void setVersionInformation(String version, String revision) {
		setVersion(version);
		if (!Strings.isNullOrEmpty(revision)) {
			if (Strings.isNullOrEmpty(version)) {
				throw new RuntimeException("revision cannot be set while version is not set");
			}
		}
		setRevision(revision);
	}

	/**
	 *
	 * @param version
	 */
	private void setVersion(String version) {
		put(AdministrativeInformation.VERSION, version);
	}

	@Override
	public String getVersion() {
		return (String) get(AdministrativeInformation.VERSION);
	}

	/**
	 * 
	 * @param revision
	 */
	private void setRevision(String revision) {
		put(AdministrativeInformation.REVISION, revision);
	}

	@Override
	public String getRevision() {
		return (String) get(AdministrativeInformation.REVISION);
	}
}
