/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.basyx.aas.metamodel.exception.MetamodelConstructionException;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IAsyncInvocation;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperationVariable;
import org.eclipse.basyx.submodel.metamodel.map.modeltype.ModelType;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;

/**
 * Operation as defined in DAAS document <br>
 * An operation is a submodel element with input and output variables.
 * 
 * @author schnicke
 *
 */
public class Operation extends SubmodelElement implements IOperation {
	// Default timeout for asynchronous operation calls
	public static final int DEFAULT_ASYNC_TIMEOUT = 10000;

	public static final String IN = "inputVariables";
	public static final String OUT = "outputVariables";
	public static final String INOUT = "inoutputVariables";

	public static final String INVOKABLE = "invokable";
	public static final String MODELTYPE = "Operation";

	// Extension of DAAS specification for function storage
	public static final String INVOKE = "invoke";
	public static final String IS_WRAPPED_INVOKABLE = "isWrappedInvokable";

	public Operation() {
		this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	/**
	 * Constructor accepting only mandatory attribute
	 * 
	 * @param idShort
	 */
	public Operation(String idShort) {
		this();
		setIdShort(idShort);
	}

	/**
	 * @param in
	 *            Input parameter of the operation.
	 * @param out
	 *            Output parameter of the operation.
	 * @param inout
	 *            Inoutput parameter of the operation.
	 * 
	 */
	public Operation(Collection<OperationVariable> in, Collection<OperationVariable> out, Collection<OperationVariable> inout) {
		super();
		putAll(new ModelType(MODELTYPE));
		setInputVariables(in);
		setOutputVariables(out);
		setInOutputVariables(inout);
	}

	/**
	 * 
	 * @param in
	 *            Input parameter of the operation.
	 * @param out
	 *            Output parameter of the operation.
	 * @param inout
	 *            Inoutput parameter of the operation.
	 * @param function
	 *            the concrete function that can directly handle unwrapped values
	 * 
	 */
	public Operation(Collection<OperationVariable> in, Collection<OperationVariable> out, Collection<OperationVariable> inout, Function<Object[], Object> function) {
		this(in, out, inout);
		setInvokable(function);
	}

	/**
	 * Create Operations w/o endpoint
	 * 
	 * @param function
	 */
	public Operation(Function<Object[], Object> function) {
		this();
		setInvokable(function);
	}

	/**
	 * Creates an Operation object from a map
	 * 
	 * @param obj
	 *            an Operation object as raw map
	 * @return an Operation object, that behaves like a facade for the given map
	 */
	public static Operation createAsFacade(Map<String, Object> obj) {
		if (obj == null) {
			return null;
		}

		if (!isValid(obj)) {
			throw new MetamodelConstructionException(Operation.class, obj);
		}

		Operation ret = new Operation();
		ret.setMap(obj);
		return ret;
	}

	/**
	 * Checks whether all mandatory elements for the metamodel exist in a map
	 * 
	 * @return true/false
	 */
	public static boolean isValid(Map<String, Object> obj) {
		return SubmodelElement.isValid(obj);
	}

	/**
	 * Returns true if the given submodel element map is recognized as an operation
	 */
	@SuppressWarnings("unchecked")
	public static boolean isOperation(Object value) {
		if (!(value instanceof Map<?, ?>)) {
			return false;
		}

		Map<String, Object> map = (Map<String, Object>) value;

		String modelType = ModelType.createAsFacade(map).getName();
		// Either model type is set or the element type specific attributes are
		// contained
		return MODELTYPE.equals(modelType) || (modelType == null && (map.containsKey(IN) && map.containsKey(OUT) && map.containsKey(INOUT)));
	}

	@Override
	public Collection<IOperationVariable> getInputVariables() {
		return transformToOperationVariables(get(Operation.IN));
	}

	@Override
	public Collection<IOperationVariable> getOutputVariables() {
		return transformToOperationVariables(get(Operation.OUT));
	}

	@Override
	public Collection<IOperationVariable> getInOutputVariables() {
		return transformToOperationVariables(get(Operation.INOUT));
	}

	@Override
	public Object invoke(Object... params) {
		return invokeSimple(params);
	}

	@Override
	public Object invokeSimple(Object... simpleParams) {
		OperationCheckHelper.checkValidParameterLength(simpleParams.length, getIdShort(), getInputVariables());
		OperationCheckHelper.checkSubmodelElementExpectedTypes(simpleParams, getInputVariables());
		if (isWrappedInvokable()) {
			return invokeWrappedInvokableWithSimpleParameters(simpleParams);
		} else {
			return directlyInvokeSimpleInvokable(simpleParams);
		}
	}

	private Object invokeWrappedInvokableWithSimpleParameters(Object... simpleParams) {
		Map<String, SubmodelElement> wrappedParamMap = OperationHelper.wrapSimpleInputParametersInMap(simpleParams, getInputVariables());
		OperationCheckHelper.checkSubmodelElementAsParameter(wrappedParamMap, getInputVariables());
		SubmodelElement[] wrappedResult = directlyInvokeWrappedInvokable(wrappedParamMap);
		return OperationHelper.unwrapResult(wrappedResult);
	}

	@Override
	public SubmodelElement[] invoke(SubmodelElement... elems) {
		OperationCheckHelper.checkValidParameterLength(elems.length, getIdShort(), getInputVariables());
		OperationCheckHelper.checkSubmodelElementAsParameter(elems, getInputVariables());
		Map<String, SubmodelElement> seMap = OperationHelper.convertSubmodelElementArrayToMap(elems);
		return invokeWrappedUnchecked(seMap);
	}

	@Override
	public AsyncInvocation invokeAsync(Object... params) {
		return new AsyncInvocation(this, 10000, params);
	}

	@Override
	public IAsyncInvocation invokeAsyncWithTimeout(int timeout, Object... params) {
		return new AsyncInvocation(this, timeout, params);
	}

	public void setInputVariables(Collection<OperationVariable> in) {
		put(Operation.IN, in);
	}

	public void setOutputVariables(Collection<OperationVariable> out) {
		put(Operation.OUT, out);
	}

	public void setInOutputVariables(Collection<OperationVariable> inOut) {
		put(Operation.INOUT, inOut);
	}

	/**
	 * Sets an invokable that handles submodel elements.
	 * 
	 * @param endpoint
	 */
	public void setWrappedInvokable(Function<Map<String, SubmodelElement>, SubmodelElement[]> endpoint) {
		Function<Object[], Object> wrappedInvokable = prepareWrappedFunctionForVAB(endpoint);
		setInvokable(wrappedInvokable);
		put(Operation.IS_WRAPPED_INVOKABLE, true);
	}

	/**
	 * Sets an invokable that handles a consumer.
	 * 
	 * @param consumer
	 */
	public void setWrappedInvokable(Consumer<Map<String, SubmodelElement>> consumer) {
		Consumer<Object[]> wrappedInvokable = prepareWrappedFunctionForVAB(consumer);
		setInvokable(wrappedInvokable);
		put(Operation.IS_WRAPPED_INVOKABLE, true);
	}

	/**
	 * Sets an invokable that handles a supplier.
	 * 
	 * @param supplier
	 */
	public void setWrappedInvokable(Supplier<SubmodelElement[]> supplier) {
		Supplier<Object> wrappedInvokable = prepareWrappedFunctionForVAB(supplier);
		setInvokable(wrappedInvokable);
		put(Operation.IS_WRAPPED_INVOKABLE, true);
	}

	/**
	 * Sets an invokable that handles direct values.
	 * 
	 * @param endpoint
	 */
	public void setInvokable(Function<Object[], Object> endpoint) {
		setSimpleInvokable(endpoint);
	}

	public void setInvokable(Runnable runnable) {
		setSimpleInvokable(runnable);
	}

	public void setInvokable(Supplier<Object> supplier) {
		setSimpleInvokable(supplier);
	}

	public void setInvokable(Consumer<Object[]> consumer) {
		setSimpleInvokable(consumer);
	}

	@Override
	public Collection<IReference> getDataSpecificationReferences() {
		return HasDataSpecification.createAsFacade(this).getDataSpecificationReferences();
	}

	@Override
	public void setDataSpecificationReferences(Collection<IReference> ref) {
		HasDataSpecification.createAsFacade(this).setDataSpecificationReferences(ref);
	}

	@Override
	public String getIdShort() {
		return Referable.createAsFacade(this, getKeyElement()).getIdShort();
	}

	@Override
	public String getCategory() {
		return Referable.createAsFacade(this, getKeyElement()).getCategory();
	}

	@Override
	public LangStrings getDescription() {
		return Referable.createAsFacade(this, getKeyElement()).getDescription();
	}

	@Override
	protected KeyElements getKeyElement() {
		return KeyElements.OPERATION;
	}

	@Override
	public Object getValue() {
		throw new UnsupportedOperationException("An Operation has no value");
	}

	@Override
	public void setValue(Object value) {
		throw new UnsupportedOperationException("An Operation has no value");
	}

	@Override
	public Operation getLocalCopy() {
		// Create a shallow copy
		Operation copy = new Operation();
		copy.putAll(this);
		// Copy InputVariables
		Collection<IOperationVariable> inVars = copy.getInputVariables();
		Collection<OperationVariable> inVarCopy = new ArrayList<>();
		inVars.stream().forEach(v -> inVarCopy.add(new OperationVariable(v.getValue().getLocalCopy())));
		copy.setInputVariables(inVarCopy);
		// Copy OutputVariables
		Collection<IOperationVariable> outVars = copy.getOutputVariables();
		Collection<OperationVariable> outVarCopy = new ArrayList<>();
		outVars.stream().forEach(v -> outVarCopy.add(new OperationVariable(v.getValue().getLocalCopy())));
		copy.setOutputVariables(outVarCopy);
		// Copy Input/Output-Variables
		Collection<IOperationVariable> inoutVars = copy.getInOutputVariables();
		Collection<OperationVariable> inoutVarCopy = new ArrayList<>();
		inoutVars.stream().forEach(v -> inoutVarCopy.add(new OperationVariable(v.getValue().getLocalCopy())));
		copy.setInOutputVariables(inoutVarCopy);
		return copy;
	}

	private void setSimpleInvokable(Object invokable) {
		put(Operation.INVOKABLE, invokable);
		put(Operation.IS_WRAPPED_INVOKABLE, false);
	}

	@SuppressWarnings("unchecked")
	private Collection<IOperationVariable> transformToOperationVariables(Object obj) {
		if (obj instanceof Collection<?>) {
			Collection<Map<String, Object>> mapCollection = (Collection<Map<String, Object>>) obj;
			return transformToOperationVariable(mapCollection);
		} else {
			return new ArrayList<>();
		}
	}

	private Collection<IOperationVariable> transformToOperationVariable(Collection<Map<String, Object>> mapCollection) {
		Collection<IOperationVariable> ret = new ArrayList<>();
		for (Map<String, Object> m : mapCollection) {
			OperationVariable opVariable = OperationVariable.createAsFacade(m);
			ret.add(opVariable);
		}
		return ret;
	}

	private Object directlyInvokeSimpleInvokable(Object[] simpleParams) {
		return new VABLambdaProvider(this).invokeOperation(INVOKABLE, simpleParams);
	}

	private SubmodelElement[] invokeWrappedUnchecked(Map<String, SubmodelElement> wrappedParamMap) {
		if (isWrappedInvokable()) {
			return directlyInvokeWrappedInvokable(wrappedParamMap);
		} else {
			Object[] unwrappedParams = OperationHelper.unwrapInputParameters(wrappedParamMap, getInputVariables());
			Object unwrappedResult = directlyInvokeSimpleInvokable(unwrappedParams);
			return OperationHelper.wrapResult(unwrappedResult, getOutputVariables());
		}
	}

	private SubmodelElement[] directlyInvokeWrappedInvokable(Map<String, SubmodelElement> wrappedParamMap) {
		return (SubmodelElement[]) new VABLambdaProvider(this).invokeOperation(INVOKABLE, wrappedParamMap);
	}

	private boolean isWrappedInvokable() {
		Object isWrappedInvokable = get(IS_WRAPPED_INVOKABLE);
		return isWrappedInvokable != null && ((boolean) isWrappedInvokable);
	}

	@SuppressWarnings("unchecked")
	private Function<Object[], Object> prepareWrappedFunctionForVAB(Function<Map<String, SubmodelElement>, SubmodelElement[]> wrappedFunction) {
		return elemArray -> wrappedFunction.apply((Map<String, SubmodelElement>) elemArray[0]);
	}

	@SuppressWarnings("unchecked")
	private Consumer<Object[]> prepareWrappedFunctionForVAB(Consumer<Map<String, SubmodelElement>> consumer) {
		return elemArray -> consumer.accept((Map<String, SubmodelElement>) elemArray[0]);
	}

	private Supplier<Object> prepareWrappedFunctionForVAB(Supplier<SubmodelElement[]> supplier) {
		return () -> supplier.get();
	}
}
