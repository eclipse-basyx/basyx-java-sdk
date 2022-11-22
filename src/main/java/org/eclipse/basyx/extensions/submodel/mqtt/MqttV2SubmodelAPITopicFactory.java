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
package org.eclipse.basyx.extensions.submodel.mqtt;

import java.util.StringJoiner;

import org.eclipse.basyx.extensions.shared.encoding.IEncoder;
import org.eclipse.basyx.extensions.shared.mqtt.AbstractMqttV2TopicFactory;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * A helper class containing string constants of topics used by the SubmodelAPI.
 * 
 * @author danish, siebert
 *
 */
public class MqttV2SubmodelAPITopicFactory extends AbstractMqttV2TopicFactory {
	private static final String AASREPOSITORY = "aas-repository";
	private static final String SHELLS = "shells";
	private static final String SUBMODELS = "submodels";
	private static final String SUBMODELELEMENTS = "submodelElements";
	private static final String CREATED = "created";
	private static final String UPDATED = "updated";
	private static final String DELETED = "deleted";
	private static final String VALUE = "value";

	/**
	 * @param encoder
	 *            Used for encoding the aasId/submodelId
	 */
	public MqttV2SubmodelAPITopicFactory(IEncoder encoder) {
		super(encoder);
	}

	/**
	 * Creates the hierarchical topic for the create submodel element event
	 * 
	 * @param aasId
	 * @param submodelId
	 * @param idShortPath
	 * @param repoId
	 * @return
	 */
	public String createCreateSubmodelElementTopic(String aasId, String submodelId, String idShortPath, String repoId) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(aasId))
				.add(SUBMODELS)
				.add(encodeId(submodelId))
				.add(SUBMODELELEMENTS)
				.add(idShortPath)
				.add(CREATED)
				.toString();
	}
	
	/**
	 * Creates the hierarchical topic for the update submodel element event
	 * 
	 * @param aasId
	 * @param submodelId
	 * @param idShortPath
	 * @param repoId
	 * @return
	 */
	public String createUpdateSubmodelElementTopic(String aasId, String submodelId, String idShortPath, String repoId) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(aasId))
				.add(SUBMODELS)
				.add(encodeId(submodelId))
				.add(SUBMODELELEMENTS)
				.add(idShortPath)
				.add(UPDATED)
				.toString();
	}
	
	/**
	 * Creates the hierarchical topic for the delete submodel element event
	 * 
	 * @param aasId
	 * @param submodelId
	 * @param idShortPath
	 * @param repoId
	 * @return
	 */
	public String createDeleteSubmodelElementTopic(String aasId, String submodelId, String idShortPath, String repoId) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(aasId))
				.add(SUBMODELS)
				.add(encodeId(submodelId))
				.add(SUBMODELELEMENTS)
				.add(idShortPath)
				.add(DELETED)
				.toString();
	}
	
	/**
	 * Creates the hierarchical topic for the update submodel element value event
	 * 
	 * @param aasId
	 * @param submodelId
	 * @param idShortPath
	 * @param repoId
	 * @return
	 */
	public String createSubmodelElementValueTopic(String aasId, String submodelId, String idShortPath, String repoId) {
		idShortPath = VABPathTools.stripSlashes(idShortPath);
		
		return new StringJoiner("/", "/", "")
				.add(AASREPOSITORY)
				.add(repoId)
				.add(SHELLS)
				.add(encodeId(aasId))
				.add(SUBMODELS)
				.add(encodeId(submodelId))
				.add(SUBMODELELEMENTS)
				.add(idShortPath)
				.add(VALUE)
				.toString();	
	}
}
