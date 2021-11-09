/*******************************************************************************
 * Copyright (C) 2021 Festo Didactic SE
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.protocol.opcua;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.basyx.vab.protocol.opcua.connector.milo.BrowsePathHelper;
import org.eclipse.basyx.vab.protocol.opcua.exception.OpcUaException;
import org.eclipse.milo.opcua.stack.core.BuiltinReferenceType;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowsePath;
import org.eclipse.milo.opcua.stack.core.types.structured.RelativePath;
import org.eclipse.milo.opcua.stack.core.types.structured.RelativePathElement;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BrowsePathHelperTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void nullThrowsIllegalArgumentException() {
		thrown.expect(IllegalArgumentException.class);
		
		BrowsePathHelper.parse(null);
	}
	
	@Test
	public void emptyStringThrowsException() {
		thrown.expect(IllegalArgumentException.class);
		
		BrowsePathHelper.parse("");
	}
	
	@Test
	public void validPathWithEscapedPeriod() {
		String s = "/2:Block&.Output";
		BrowsePath bp = BrowsePathHelper.parse(s);
		
		RelativePathElement rpe = new RelativePathElement(
				BuiltinReferenceType.HierarchicalReferences.getNodeId(), 
				false, 
				true, 
				new QualifiedName(2, "Block.Output"));
		
		assertEquals(1, bp.getRelativePath().getElements().length);
		assertEquals(rpe, bp.getRelativePath().getElements()[0]);
	}
	
	@Test
	public void validPathWithTwoElements() {
		String s = "/3:Truck.0:NodeVersion";
		BrowsePath bp = BrowsePathHelper.parse(s);
		
		RelativePathElement rpe1 = new RelativePathElement(
				BuiltinReferenceType.HierarchicalReferences.getNodeId(), 
				false, 
				true, 
				new QualifiedName(3, "Truck"));
		
		RelativePathElement rpe2 = new RelativePathElement(
				BuiltinReferenceType.Aggregates.getNodeId(), 
				false, 
				true, 
				new QualifiedName(0, "NodeVersion"));
		
		assertEquals(2, bp.getRelativePath().getElements().length);
		assertEquals(rpe1, bp.getRelativePath().getElements()[0]);
		assertEquals(rpe2, bp.getRelativePath().getElements()[1]);
	}
	
	@Test
	public void validPathWithTwoElementsAndEscapes() {
		String s = ".3:Truck&/Car/Node&:Version";
		BrowsePath bp = BrowsePathHelper.parse(s);
		
		RelativePathElement rpe1 = new RelativePathElement(
				BuiltinReferenceType.Aggregates.getNodeId(), 
				false, 
				true, 
				new QualifiedName(3, "Truck/Car"));
		
		RelativePathElement rpe2 = new RelativePathElement(
				BuiltinReferenceType.HierarchicalReferences.getNodeId(), 
				false, 
				true, 
				new QualifiedName(0, "Node:Version"));
		
		assertEquals(2, bp.getRelativePath().getElements().length);
		assertEquals(rpe1, bp.getRelativePath().getElements()[0]);
		assertEquals(rpe2, bp.getRelativePath().getElements()[1]);
	}
	
	@Test
	public void multiSelectorPath() {
		String s = "/2:Block/Output.";
		BrowsePath bp = BrowsePathHelper.parse(s);
		
		RelativePathElement last = bp.getRelativePath().getElements()[2];
		assertNull(last.getTargetName());
	}
	
	@Test
	public void invalidPathWithMissingReferenceType() {
		thrown.expect(OpcUaException.class);
		thrown.expectMessage(org.hamcrest.CoreMatchers.containsString("index 0"));
		
		String s = "3:Truck/NodeVersion";
		BrowsePathHelper.parse(s);
	}
	
	@Test
	public void pathWithEmptyPathElement() {
		thrown.expect(OpcUaException.class);
		thrown.expectMessage(org.hamcrest.CoreMatchers.containsString("index 9"));
		
		String s = "/3:Truck/.NodeVersion";
		BrowsePathHelper.parse(s);
	}
	
	@Test
	public void pathWithIllegalNamespace() {
		thrown.expect(OpcUaException.class);
		thrown.expectMessage(org.hamcrest.CoreMatchers.containsString("index 1"));
		
		String s = "/3a:Truck";
		BrowsePathHelper.parse(s);
	}
	
	@Test
	public void pathWithTwoNamespaces() {
		thrown.expect(OpcUaException.class);
		thrown.expectMessage(org.hamcrest.CoreMatchers.containsString("index 9"));
		
		String s = "/3:Truck/2:Car:Bike";
		BrowsePathHelper.parse(s);
	}
	
	@Test
	public void pathWithDoubleColon() {
		thrown.expect(OpcUaException.class);
		thrown.expectMessage(org.hamcrest.CoreMatchers.containsString("index 9"));
		
		String s = "/3:Truck/2::Bike";
		BrowsePathHelper.parse(s);
	}
	
	@Test
	public void pathWithDirectReferenceTypeAtStart() {
		thrown.expect(IllegalArgumentException.class);
		
		String s = "<1:ConnectedTo>1:Boiler/1:HeatSensor";
		BrowsePathHelper.parse(s);
	}
	
	@Test
	public void pathWithDirectReferenceTypeInMiddle() {
		thrown.expect(IllegalArgumentException.class);
		
		String s = ".1:Boiler<1:ConnectedTo>1:HeatSensor";
		BrowsePathHelper.parse(s);
	}
	
	@Test
	public void relativePathToString() {
		RelativePathElement rpe1 = new RelativePathElement(
				BuiltinReferenceType.Aggregates.getNodeId(),
				false,
				true,
				new QualifiedName(0, "Objects"));
		
		RelativePathElement rpe2 = new RelativePathElement(
				BuiltinReferenceType.HierarchicalReferences.getNodeId(),
				false,
				true,
				new QualifiedName(1, "Cars"));
		
		RelativePathElement rpe3 = new RelativePathElement(
				BuiltinReferenceType.HierarchicalReferences.getNodeId(),
				false,
				true,
				null);
		
		RelativePath rp = new RelativePath(new RelativePathElement[] {rpe1, rpe2, rpe3});
		
		String s = BrowsePathHelper.toString(rp);
		
		assertEquals(".0:Objects/1:Cars/", s);
	}
	
	@Test
	public void relativePathWithMissingQualifiedNameToString() {
		thrown.expect(IllegalArgumentException.class);
		
		RelativePathElement rpe1 = new RelativePathElement(
				BuiltinReferenceType.Aggregates.getNodeId(),
				false,
				true,
				new QualifiedName(0, "Objects"));
		
		RelativePathElement rpe2 = new RelativePathElement(
				BuiltinReferenceType.HierarchicalReferences.getNodeId(),
				false,
				true,
				null);
		
		RelativePathElement rpe3 = new RelativePathElement(
				BuiltinReferenceType.HierarchicalReferences.getNodeId(),
				false,
				true,
				new QualifiedName(1, "Cars"));
		
		RelativePath rp = new RelativePath(new RelativePathElement[] {rpe1, rpe2, rpe3});
		
		BrowsePathHelper.toString(rp);
	}
	
	@Test
	public void relativePathWithInverseReferencesToString() {
		thrown.expect(IllegalArgumentException.class);
		
		RelativePathElement rpe1 = new RelativePathElement(
				BuiltinReferenceType.Aggregates.getNodeId(),
				true,
				true,
				new QualifiedName(0, "Objects"));
		
		RelativePath rp = new RelativePath(new RelativePathElement[] {rpe1});
		
		BrowsePathHelper.toString(rp);
	}
}
