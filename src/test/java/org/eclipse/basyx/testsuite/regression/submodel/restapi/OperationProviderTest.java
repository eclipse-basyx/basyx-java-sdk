/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/
package org.eclipse.basyx.testsuite.regression.submodel.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.haskind.ModelingKind;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.OperationVariable;
import org.eclipse.basyx.submodel.restapi.OperationProvider;
import org.eclipse.basyx.submodel.restapi.operation.CallbackResponse;
import org.eclipse.basyx.submodel.restapi.operation.DelegatedInvocationHelper;
import org.eclipse.basyx.submodel.restapi.operation.InvocationRequest;
import org.eclipse.basyx.submodel.restapi.operation.InvocationResponse;
import org.eclipse.basyx.vab.exception.provider.MalformedRequestException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the OperationProvider
 * 
 * @author conradi
 *
 */
public class OperationProviderTest {
	private static final String SERVER = "localhost";
	private static final int PORT = 4000;
	private static final String CONTEXT_PATH = "operation";

	private static final String OPID_IN = "opIn";
	private static final String OPID_OUT = "opOut";
	
	private static final String API_INVOKE_URL = "http://" + SERVER + ":" + PORT + "/" + CONTEXT_PATH + "/" + OPID_OUT + "/invoke";
	
	private static Integer requestId = 0;
	
	private static final Function<Object[], Object> NULL_RETURN_FUNC = (Function<Object[], Object>) v -> {
		// Do nothing, just return
		// This is a function with return type "void" 
		return null;
	};
	
	private static final Function<Object[], Object> SUB_RETURN_FUNC = (Function<Object[], Object>) v -> {
		return (Integer) v[0] - (Integer) v[1];
	};
	
	private static OperationProvider opProviderIn;
	private static OperationProvider opProviderOut;
	
	
	@BeforeClass
	public static void setup() {
		
		Collection<OperationVariable> in = getInVariables();
		Collection<OperationVariable> out = getOutVariables();
		
		Operation inOperation = createOperation(OPID_IN, in, new ArrayList<>(), NULL_RETURN_FUNC);
		opProviderIn = new OperationProvider(new VABLambdaProvider(inOperation));
		
		
		Operation outOperation = createOperation(OPID_OUT, in, out, SUB_RETURN_FUNC);
		opProviderOut = new OperationProvider(new VABLambdaProvider(outOperation));
		
	}
	
	
	private static Operation createOperation(String id, Collection<OperationVariable> in,
			Collection<OperationVariable> out, Function<Object[], Object> func) {
		Operation operation = new Operation(id);
		operation.setInputVariables(in);
		operation.setOutputVariables(out);
		operation.setInvokable(func);
		return operation;
	}
		
	
	private static Collection<OperationVariable> getInVariables() {
		Collection<OperationVariable> in = new ArrayList<>();
		
		Property inProp1 = new Property("testIn1", 0);
		inProp1.setModelingKind(ModelingKind.TEMPLATE);
		Property inProp2 = new Property("testIn2", 0);
		inProp1.setModelingKind(ModelingKind.TEMPLATE);
		
		in.add(new OperationVariable(inProp1));
		in.add(new OperationVariable(inProp2));
		
		return in;
	}
	
	private static Collection<OperationVariable> getOutVariables() {
		Collection<OperationVariable> out = new ArrayList<>();
		
		Property outProp = new Property("testOut", 0);
		outProp.setModelingKind(ModelingKind.TEMPLATE);

		out.add(new OperationVariable(outProp));
		
		return out;
	}
	
	
	/**
	 * Tests an Operation call with non wrapped parameters
	 * Operation has no return value
	 */
	@Test
	public void testNonWrappedInputWithoutOutput() {
		opProviderIn.invokeOperation("invoke", 1, 2);
	}
	
	/**
	 * Tests an Operation call with an InvocationRequest
	 * Operation has no return value
	 */
	@Test
	public void testInvocationRequestInputWithoutOutput() {	
		Property inProp1 = new Property("testIn1", 10);
		Property inProp2 = new Property("testIn2", 6);
		InvocationRequest request = getInvocationRequest(inProp1, inProp2);
		invokeSync(opProviderIn, request);
	}
	
	/**
	 * Tests an Operation call with non wrapped parameters
	 * Operation returns the given parameter
	 */
	@Test
	public void testNonWrappedInputWithOutput() {
		assertEquals(4, opProviderOut.invokeOperation("invoke", 10, 6));
	}
	
	/**
	 * Tests an Operation call with an InvocationRequest
	 * Operation returns the given parameter
	 */
	@Test
	public void testInvocationRequestInputWithOutput() throws Exception {
		
		Property inProp1 = new Property("testIn1", 10);
		Property inProp2 = new Property("testIn2", 6);
		InvocationRequest request = getInvocationRequest(inProp1, inProp2);
		
		Collection<IOperationVariable> outResponseSync = invokeSync(opProviderOut, request);
		Collection<IOperationVariable> outResponseAsync = invokeAsync(opProviderOut, request);
		assertEquals(1, outResponseSync.size());
		assertEquals(1, outResponseAsync.size());
		
		Property propSync = (Property) outResponseSync.iterator().next().getValue();
		Property propAsync = (Property) outResponseAsync.iterator().next().getValue();
		
		assertEquals(4, propSync.getValue());
		assertEquals(4, propAsync.getValue());
	}
	
	/**
	 * Swap the parameters in request to check if they are sorted by id
	 */
	@Test
	public void testInvocationRequestWithSwappedParameters() throws Exception {
		
		Property inProp1 = new Property("testIn1", 10);
		Property inProp2 = new Property("testIn2", 6);
		InvocationRequest request = getInvocationRequest(inProp2, inProp1);
		
		
		Collection<IOperationVariable> outResponseSync = invokeSync(opProviderOut, request);
		Collection<IOperationVariable> outResponseAsync = invokeAsync(opProviderOut, request);
		assertEquals(1, outResponseSync.size());
		assertEquals(1, outResponseAsync.size());
		
		Property propSync = (Property) outResponseSync.iterator().next().getValue();
		Property propAsync = (Property) outResponseAsync.iterator().next().getValue();
		
		assertEquals(4, propSync.getValue());
		assertEquals(4, propAsync.getValue());
	}
	
	/**
	 * Tests to call an Operation expecting 2 parameters with only 1
	 */
	@Test
	public void testInvocationRequestWithTooFewParameters() throws Exception {
		Property inProp1 = new Property("testIn1", 5);
		
		InvocationRequest request = getInvocationRequest(inProp1);
		
		try {
			invokeSync(opProviderOut, request);
			fail();
		} catch(MalformedRequestException e) {
		}
		
		try {
			invokeAsync(opProviderOut, request);
			fail();
		} catch(MalformedRequestException e) {
		}
	}
	
	/**
	 * Tests to call Operation with right number of parameters but a wrong id
	 */
	@Test
	public void testInvocationRequestWithWrongParamId() throws Exception {
		Property inProp1 = new Property("testIn1", 10);
		Property inProp2 = new Property("testIn3", 6);

		InvocationRequest request = getInvocationRequest(inProp1, inProp2);
		
		try {
			invokeSync(opProviderOut, request);
			fail();
		} catch(MalformedRequestException e) {
		}
		
		try {
			invokeAsync(opProviderOut, request);
			fail();
		} catch(MalformedRequestException e) {
		}
	}
	
	@Test
	public void testInvocationDelegation() {
		// Start an http server with an operation
		BaSyxContext context = new BaSyxContext("/" + CONTEXT_PATH, "", SERVER, PORT);
		context.addServletMapping("/" + OPID_OUT + "/*", new VABHTTPInterface<IModelProvider>(opProviderOut));
		BaSyxHTTPServer server = new BaSyxHTTPServer(context);
		server.start();
		
		Operation delegatedOperation = createOperation("delegatedOperation", null, null, null);
		
		// Create a delegated qualifier and add to the operation
		Qualifier qualifier = new Qualifier(DelegatedInvocationHelper.DELEGATION_TYPE, API_INVOKE_URL, "string", null);
		delegatedOperation.setQualifiers(Arrays.asList(qualifier));
		
		OperationProvider delegatedOpProvider = new OperationProvider(new VABLambdaProvider(delegatedOperation));
		
		assertEquals(4, delegatedOpProvider.invokeOperation("invoke", 10, 6));
		
		server.shutdown();
	}
	
	@SuppressWarnings("unchecked")
	private Collection<IOperationVariable> invokeSync(OperationProvider provider, InvocationRequest request) {
		InvocationResponse response = InvocationResponse
				.createAsFacade((Map<String, Object>) provider.invokeOperation("invoke", request));
		return response.getOutputArguments();
	}
	
	@SuppressWarnings("unchecked")
	private Collection<IOperationVariable> invokeAsync(OperationProvider provider, InvocationRequest request) throws Exception {
		Object response = provider.invokeOperation("invoke?async=true", request);
		assertTrue(response instanceof CallbackResponse);
		Thread.sleep(10);
		
		InvocationResponse invokeResponse = InvocationResponse.createAsFacade((Map<String, Object>) provider.getValue("/invocationList/" + request.getRequestId()));
		return invokeResponse.getOutputArguments();
	}
	
	/**
	 * Builds an InvocationRequest with given parameters
	 * 
	 * @param in the parameters for the InvocationRequest
	 * @return the InvocationRequest
	 */
	private InvocationRequest getInvocationRequest(Property... in) {
		Collection<IOperationVariable> inout = new ArrayList<>();
		
		Collection<IOperationVariable> inVariables = Arrays.asList(in).stream()
				.map(i -> new OperationVariable(i)).collect(Collectors.toList());
		
		return new InvocationRequest((requestId++).toString(), inout, inVariables, 100);
	}
}
