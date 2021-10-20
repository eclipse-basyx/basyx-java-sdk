/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.vab.protocol.opcua;

import org.eclipse.basyx.vab.gateway.ConnectorProviderMapper;
import org.eclipse.basyx.vab.protocol.opcua.connector.OpcUaConnectorProvider;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.junit.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test VAB using OpcUa protocol. This is an integration test
 * 
 * @author kdorofeev
 *
 */
public class TestVABOpcUa {
	
	private static Logger logger = LoggerFactory.getLogger(TestVABOpcUa.class); 
	
    protected ConnectorProviderMapper clientMapper = new ConnectorProviderMapper();

//    @Test
    public void testOpcUaMethodCall() {
        clientMapper.addConnectorProvider("opc.tcp", new OpcUaConnectorProvider());
        try {
            Object methodCallRes = clientMapper.getConnector("opc.tcp://opcua.demo-this.com:51210/UA/SampleServer")
                    .invokeOperation("0:Objects/2:Data/2:Static/2:MethodTest/2:ScalarMethod1",
                            new Object[] { Boolean.valueOf("true"), Byte.valueOf("1"), UByte.valueOf("2"),
                                    Short.valueOf("3"), UShort.valueOf("5"), 8, UInteger.valueOf(13),
                                    Long.valueOf("21"), ULong.valueOf("34"), Float.valueOf("55.0"),
                                    Double.valueOf("89.0") });
            Assert.assertEquals("true 1 2 3 5 8 13 21 34 55.0 89.0 ", methodCallRes);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("[TEST] Exception in testOpcUaMethodCall", e);
        }
    }
    
//    @Test
    public void testOpcUaReadAndWrite() {
        clientMapper.addConnectorProvider("opc.tcp", new OpcUaConnectorProvider());
        try {
            clientMapper.getConnector("opc.tcp://opcua.demo-this.com:51210/UA/SampleServer")
                    .setValue("0:Objects/2:Data/2:Static/2:AnalogScalar/2:Int32Value", 42);

            Object ret = clientMapper.getConnector("opc.tcp://opcua.demo-this.com:51210/UA/SampleServer")
                    .getValue("0:Objects/2:Data/2:Static/2:AnalogScalar/2:Int32Value");
            Assert.assertEquals("42", ret);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("[TEST] Exception in testOpcUaReadAndWrite", e);
        }
    }
}
