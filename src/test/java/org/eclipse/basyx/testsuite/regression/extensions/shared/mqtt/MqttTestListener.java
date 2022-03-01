/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptAcknowledgedMessage;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptSubscribeMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;

/**
 * Very simple MQTT broker listener for testing Submodel API events. Stores the
 * last received event and makes its topic and payload available for reading.
 * 
 * @author espen
 *
 */
public class MqttTestListener implements InterceptHandler {
	// Topic and payload of the most recent event
	public String lastTopic;
	public String lastPayload;
	public ArrayList<String> topics = new ArrayList<>();

	@Override
	public String getID() {
		return null;
	}

	@Override
	public Class<?>[] getInterceptedMessageTypes() {
		return null;
	}

	@Override
	public void onConnect(InterceptConnectMessage arg0) {
	}

	@Override
	public void onConnectionLost(InterceptConnectionLostMessage arg0) {
	}

	@Override
	public void onDisconnect(InterceptDisconnectMessage arg0) {
	}

	@Override
	public void onMessageAcknowledged(InterceptAcknowledgedMessage arg0) {
	}

	@Override
	public synchronized void onPublish(InterceptPublishMessage msg) {
		topics.add(msg.getTopicName());
		lastTopic = msg.getTopicName();
		lastPayload = msg.getPayload().toString(StandardCharsets.UTF_8);
	}

	@Override
	public void onSubscribe(InterceptSubscribeMessage arg0) {
	}

	@Override
	public void onUnsubscribe(InterceptUnsubscribeMessage arg0) {
	}
}
