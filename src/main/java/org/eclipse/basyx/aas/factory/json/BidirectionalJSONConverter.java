/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.aas.factory.json;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;

/**
 * Serializes and deserializes shells and submodels to and from json
 * 
 * @author jungjan
 *
 */
public class BidirectionalJSONConverter {
	private static GSONTools gsonTools = new GSONTools(new DefaultTypeFactory());

	public static String serializeSubmodel(ISubmodel submodel) {
		return gsonTools.serialize(submodel);
	}

	@SuppressWarnings("unchecked")
	public static ISubmodel deserializeSubmodel(String jsonSubmodel) {
		return Submodel.createAsFacade((Map<String, Object>) gsonTools.deserialize(jsonSubmodel));
	}

	public static String serializeShell(AssetAdministrationShell shell) {
		return gsonTools.serialize(shell);
	}

	@SuppressWarnings("unchecked")
	public static AssetAdministrationShell deserializeShell(String jsonShell) {
		return AssetAdministrationShell.createAsFacade((Map<String, Object>) gsonTools.deserialize(jsonShell));
	}

	public static <T> String serializeObject(T object) {
		return gsonTools.serialize(object);
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserializeJSON(String json) {
		T retrieved = (T) gsonTools.deserialize(json);
		return retrieved;
	}

	@SuppressWarnings("unchecked")
	public static AASDescriptor deserializeAASDescriptor(String json) {
		return AASDescriptor.createAsFacade((Map<String, Object>) gsonTools.deserialize(json));
	}

	/**
	 * 
	 * @param submodel
	 * @return the semanticId of a submodel serialized according to the following schema:
	 *         {@code {type:<type>;value:<value>;idType:<idType>[/]}}.
	 *         Example:  {@code type:Submodel;value:a value;idType:Custom/type:Submodel;value:another value;idType:Custom}
	 */
	public static String semanticIdAsSString(ISubmodel submodel) {
		if (submodel.getSemanticId() == null) {
			return null; 
		}
		List<IKey> keys = submodel.getSemanticId().getKeys();
		return semanticIdKeysToString(keys);
	}

	public static String semanticIdKeysToString(List<IKey> keys) {
		return keys.stream().map(k -> 
				Key.TYPE + ":" + k.getType() + ";" 
				/* + Key.LOCAL + ":" + k.isLocal() + ";" */ //ignoring local since it won't be relevant for V3
				+ Key.VALUE + ":" + k.getValue() + ";" 
				+ Key.IDTYPE + ":" + k.getIdType())
				.collect(Collectors.joining("/"));
			}

}
