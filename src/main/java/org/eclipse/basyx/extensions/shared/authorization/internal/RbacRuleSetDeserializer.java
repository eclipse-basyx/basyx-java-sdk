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
package org.eclipse.basyx.extensions.shared.authorization.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deserializer for {@link RbacRuleSet}.
 * <p>
 * Supports polymorphism for {@link TargetInformation} using a "@type"
 * discriminator field. If you need to support further implementations, you can
 * register it via {@link ObjectMapper#registerSubtypes(NamedType...)} using the
 * {@link RbacRuleSetDeserializer#RbacRuleSetDeserializer(Consumer)}
 * constructor.
 * <p>
 * Uses jackson.
 *
 * @author wege
 */
public class RbacRuleSetDeserializer {
	private static final Logger logger = LoggerFactory.getLogger(RbacRuleSetDeserializer.class);

	@JsonTypeInfo(use = Id.NAME, property = "@type")
	public static class TargetInformationMixin {

	}

	public static class RbacRuleMixin {
		@JsonCreator
		public RbacRuleMixin(final @JsonProperty("role") String role, final @JsonProperty("action") String action, final @JsonProperty("targetInformation") TargetInformation targetInformation) {

		}
	}

	private final ObjectMapper objectMapper;

	public RbacRuleSetDeserializer() {
		this(mapper -> {
		});
	}

	public RbacRuleSetDeserializer(final Consumer<ObjectMapper> objectMapperConsumer) {
		objectMapper = new ObjectMapper();
		objectMapper.addMixIn(RbacRule.class, RbacRuleMixin.class);
		objectMapper.addMixIn(TargetInformation.class, TargetInformationMixin.class).registerSubtypes(new NamedType(BaSyxObjectTargetInformation.class, "basyx"), new NamedType(TagTargetInformation.class, "tag"));
		objectMapperConsumer.accept(objectMapper);
	}

	public RbacRuleSet fromFile(final String filePath) throws IOException {
		if (filePath == null) {
			throw new IllegalArgumentException("filePath must not be null");
		}

		logger.info("loading rbac rules...");
		try (final InputStream inputStream = getInputStreamFromFile(filePath)) {
			final RbacRule[] rbacRules = deserialize(inputStream);

			logger.info("Read rbac rules: {}", Arrays.toString(rbacRules));
			final RbacRuleSet rbacRuleSet = new RbacRuleSet();
			Arrays.stream(rbacRules).forEach(rbacRuleSet::addRule);
			return rbacRuleSet;
		}
	}

	private InputStream getInputStreamFromFile(final String filePath) throws IOException {
		try {
			final InputStream inputStream = new FileInputStream(filePath);
			logger.info("read {} from file system", filePath);
			return inputStream;
		} catch (FileNotFoundException e) {
			// did not find file on file system, fallback to read from classpath next
			logger.info("did not find {} in file system, try classpath next", filePath);
		}

		try {
			final InputStream inputStream = RbacRuleSet.class.getResourceAsStream(filePath);
			if (inputStream == null) {
				throw new FileNotFoundException("could not find " + filePath);
			}
			logger.info("read {} from classpath", filePath);
			return inputStream;
		} catch (FileNotFoundException e) {
			// did not find file on classpath, give up
			logger.info("did not find {} on classpath, give up", filePath);
		}

		throw new IOException("could not find " + filePath);
	}

	public RbacRule[] deserialize(final InputStream inputStream) throws IOException {
		return objectMapper.readValue(inputStream, RbacRule[].class);
	}
}