package org.eclipse.basyx.testsuite.regression.submodel.restapi;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.Range;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.range.RangeValue;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.coder.json.provider.JSONProvider;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.map.VABMapProvider;
import org.junit.Test;

public class SubmodelProviderIntegrationTest {
	protected final String RANGE_IDSHORT = "rangeProperty";
	protected final RangeValue RANGE_VALUE = new RangeValue(1, 2);
	protected final String DURATION_IDSHORT = "durationProperty";
	protected final Duration DURATION_VALUE = Duration.ofSeconds(10);

	private final String VALUES_PATH = VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, SubmodelProvider.VALUES);

	private GSONTools serializer = new GSONTools(new DefaultTypeFactory());

	@Test
	public void serializePropertyValue() {
		Property durationProp = new Property(DURATION_IDSHORT, DURATION_VALUE);

		JSONProvider<IModelProvider> jsonProvider = wrapInJSONSubmodelProvider(durationProp);
		Object value = deserializeThroughProvider(jsonProvider, getValuePath(DURATION_IDSHORT));

		assertEquals(DURATION_VALUE.toString(), value);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void serializeSubmodelPropertyValues() {
		Property durationProp = new Property(DURATION_IDSHORT, DURATION_VALUE);

		JSONProvider<IModelProvider> jsonProvider = wrapInJSONSubmodelProvider(durationProp);
		Object values = deserializeThroughProvider(jsonProvider, VALUES_PATH);

		Map<String, Object> valuesMap = (Map<String, Object>) values;
		assertEquals(1, valuesMap.size());
		assertEquals(DURATION_VALUE.toString(), valuesMap.get(DURATION_IDSHORT));
	}

	@Test
	public void serializeNonPropertyValue() {
		Range rangeProp = new Range(RANGE_IDSHORT, ValueType.Integer);
		rangeProp.setValue(RANGE_VALUE);

		JSONProvider<IModelProvider> jsonProvider = wrapInJSONSubmodelProvider(rangeProp);
		Object valueMap = deserializeThroughProvider(jsonProvider, getValuePath(RANGE_IDSHORT));

		assertEquals(RANGE_VALUE, valueMap);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void serializeSubmodelNonPropertyValues() {
		Range rangeProp = new Range(RANGE_IDSHORT, ValueType.Integer);
		rangeProp.setValue(RANGE_VALUE);

		JSONProvider<IModelProvider> jsonProvider = wrapInJSONSubmodelProvider(rangeProp);
		Map<String, Object> values = (Map<String, Object>) deserializeThroughProvider(jsonProvider, VALUES_PATH);

		assertEquals(1, values.size());
		assertEquals(RANGE_VALUE, values.get(RANGE_IDSHORT));
	}

	private String getValuePath(String propertyIdShort) {
		return VABPathTools.concatenatePaths(SubmodelProvider.SUBMODEL, Submodel.SUBMODELELEMENT, propertyIdShort, Property.VALUE);
	}

	private Object deserializeThroughProvider(JSONProvider<IModelProvider> provider, String path) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		provider.processBaSysGet(path, outputStream);
		return serializer.deserialize(outputStream.toString());
	}

	private JSONProvider<IModelProvider> wrapInJSONSubmodelProvider(SubmodelElement submodelElement) {
		Submodel durationSubmodel = new Submodel();
		durationSubmodel.addSubmodelElement(submodelElement);
		IModelProvider durationMapProvider = new VABMapProvider(durationSubmodel);
		IModelProvider durationSMProvider = new SubmodelProvider(durationMapProvider);
		return new JSONProvider<>(durationSMProvider);
	}
}
