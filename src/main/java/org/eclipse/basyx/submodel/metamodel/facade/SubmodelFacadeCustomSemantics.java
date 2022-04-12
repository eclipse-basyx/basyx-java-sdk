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
package org.eclipse.basyx.submodel.metamodel.facade;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasSemantics;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.haskind.HasKind;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Constraint;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifiable;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;





/**
 * Facade class that supports the development and access of sub models using IRDI (International Registration Data Identifier) semantic definitions
 * 
 * @author kuhn
 *
 */
public class SubmodelFacadeCustomSemantics extends Submodel {
	/**
	 * Constructor without arguments - create a sub model with all meta properties empty / set to default values
	 */
	public SubmodelFacadeCustomSemantics() {
		// Create sub model
		super();
	}

	/**
	 * Sub model constructor for sub models that conform to a globally defined
	 * semantics with Custom semantics <br>
	 * 
	 * Create an instance sub model with all meta properties empty / set to default
	 * values
	 * 
	 * @param semantics
	 *            String that describes the sub model semantics e.g. its type (e.g.
	 *            basys.semantics.transportsystem)
	 * @param idType
	 *            Submodel ID type
	 * @param id
	 *            Sub model ID according to idType
	 * @param idShort
	 *            Short ID of the sub model (e.g. "subsystemTopology")
	 * @param category
	 *            Additional coded meta information regarding the element type that
	 *            affects expected existence of attributes (e.g.
	 *            "transportSystemTopology")
	 * @param description
	 *            Descriptive sub model description (e.g. "This is a machine
	 *            readable description of the transport system topology")
	 * @param constraint
	 *            The qualifier of this sub model (e.g. "plant.maintransport")
	 * @param dataSpecification
	 *            Sub model data specification
	 * @param kind
	 *            Sub model kind
	 * @param version
	 *            Sub model version
	 * @param revision
	 *            Sub model revision
	 */
	public SubmodelFacadeCustomSemantics(String semantics, IdentifierType idType, String id, String idShort, String category, LangStrings description, Constraint constraint, HasDataSpecification dataSpecification, ModelingKind kind,
			String version,
			String revision) {
		// Create sub model
		super(new HasSemantics(new Reference(Collections.singletonList(new Key(KeyElements.GLOBALREFERENCE, false, semantics, KeyType.CUSTOM)))),
				new Identifiable(version, revision, idShort, category, description, IdentifierType.CUSTOM, id),
				new Qualifiable(constraint), 
				dataSpecification,
				new HasKind(kind));
	}

	/**
	 * Sub model constructor for sub models that conform to a globally defined
	 * semantics with Custom semantics <br>
	 * 
	 * Create an instance sub model with all meta properties empty / set to default
	 * values
	 * 
	 * @param semantics
	 *            String that describes the sub model semantics e.g. its type (e.g.
	 *            basys.semantics.transportsystem)
	 * @param idType
	 *            Submodel ID type
	 * @param id
	 *            Sub model ID according to idType
	 * @param idShort
	 *            Short ID of the sub model (e.g. "subsystemTopology")
	 * @param category
	 *            Additional coded meta information regarding the element type that
	 *            affects expected existence of attributes (e.g.
	 *            "transportSystemTopology")
	 * @param description
	 *            Descriptive sub model description (e.g. "This is a machine
	 *            readable description of the transport system topology")
	 * @param constraint
	 *            The collection of qualifiers of this sub model (e.g.
	 *            ["plant.maintransport", "maintransport."])
	 * @param dataSpecification
	 *            Sub model data specification
	 * @param kind
	 *            Sub model kind
	 * @param version
	 *            Sub model version
	 * @param revision
	 *            Sub model revision
	 */
	public SubmodelFacadeCustomSemantics(String semantics, IdentifierType idType, String id, String idShort, String category, LangStrings description, Collection<Constraint> qualifier, Constraint constraint,
			HasDataSpecification dataSpecification, ModelingKind kind, String version, String revision) {
		// Create sub model
		super(new HasSemantics(new Reference(Collections.singletonList(new Key(KeyElements.GLOBALREFERENCE, false, semantics, KeyType.CUSTOM)))),
						new Identifiable(version, revision, idShort, category, description, idType, id), 
						new Qualifiable(qualifier), 
						dataSpecification, 
				new HasKind(kind));
	}
}

