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
package org.eclipse.basyx.testsuite.regression.submodel.metamodel.map.qualifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangString;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.LangStrings;
import org.junit.Test;

/**
 * Tests constructor, setter and getter of {@link LangStrings} for their
 * correctness
 * 
 * @author haque
 *
 */
public class TestLangStrings {
	private static final String LANGUAGE1 = "Eng";
	private static final String LANGUAGE2 = "Deu";
	private static final String TEXT1 = "test";
	private static final String TEXT2 = "test2";

	@Test
	public void testConstructor1() {
		LangStrings langStrings = new LangStrings(LANGUAGE1, TEXT1);
		String textString = langStrings.get(LANGUAGE1);
		assertEquals(TEXT1, textString);
	}

	@Test
	public void testConstructor2() {
		LangString langString1 = new LangString(LANGUAGE1, TEXT1);
		LangString langString2 = new LangString(LANGUAGE2, TEXT2);

		Collection<LangString> listsCollection = new ArrayList<>();
		listsCollection.add(langString1);
		listsCollection.add(langString2);

		LangStrings langStrings = new LangStrings(listsCollection);
		String textString1 = langStrings.get(LANGUAGE1);
		assertEquals(TEXT1, textString1);

		String textString2 = langStrings.get(LANGUAGE2);
		assertEquals(TEXT2, textString2);
	}

	@Test
	public void testConstructor3() {
		LangString langString = new LangString(LANGUAGE1, TEXT1);
		LangStrings langStrings = new LangStrings(langString);
		String textString = langStrings.get(LANGUAGE1);
		assertEquals(TEXT1, textString);
	}

	@Test
	public void testAdd() {
		LangStrings langStrings = new LangStrings(LANGUAGE1, TEXT1);

		langStrings.add(new LangString(LANGUAGE2, TEXT2));
		assertEquals(TEXT2, langStrings.get(LANGUAGE2));
	}

	@Test
	public void testGetLanguages() {
		LangStrings langStrings = new LangStrings(LANGUAGE1, TEXT1);
		langStrings.add(new LangString(LANGUAGE2, TEXT2));

		Set<String> languageSet = new HashSet<String>();
		languageSet.add(LANGUAGE1);
		languageSet.add(LANGUAGE2);
		assertEquals(languageSet, langStrings.getLanguages());
	}

	@Test
	public void testIsLangStrings() {
		LangStrings langStrings = new LangStrings(LANGUAGE1, TEXT1);

		assertTrue(LangStrings.isLangStrings(langStrings));

		LangString langString = new LangString(LANGUAGE1, TEXT1);
		langString.put("language", null);

		langStrings.add(langString);

		assertFalse(LangStrings.isLangStrings(langStrings));
	}

	@Test
	public void testFromStringPairs() {
		LangStrings langStrings = LangStrings.fromStringPairs(LANGUAGE1, TEXT1, LANGUAGE2, TEXT2);
		Iterator<String> actual = langStrings.getLanguages().iterator();
		assertEquals(TEXT1, langStrings.get(actual.next()));
		assertEquals(TEXT2, langStrings.get(actual.next()));
		assertFalse("There should be only two LangStrings", actual.hasNext());
	}

	@Test
	public void testFromStringPairsInverted() {
		LangStrings langStrings = LangStrings.fromStringPairs(LANGUAGE2, TEXT2, LANGUAGE1, TEXT1);
		Iterator<String> actual = langStrings.getLanguages().iterator();
		assertEquals(TEXT2, langStrings.get(actual.next()));
		assertEquals(TEXT1, langStrings.get(actual.next()));
		assertFalse("There should be only two LangStrings", actual.hasNext());
	}

	@Test
	public void testFromStringPairsWithEmptyInput() {
		LangStrings langStrings = LangStrings.fromStringPairs();
		assertEquals(0, langStrings.getLanguages().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStringPairsWithOddNumber() {
		LangStrings.fromStringPairs(LANGUAGE1);
	}
}
