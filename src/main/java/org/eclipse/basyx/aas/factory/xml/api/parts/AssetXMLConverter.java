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
package org.eclipse.basyx.aas.factory.xml.api.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.factory.xml.AASXPackageExplorerCompatibilityHandler;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.submodel.factory.xml.XMLHelper;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.HasDataSpecificationXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.qualifier.IdentifiableXMLConverter;
import org.eclipse.basyx.submodel.factory.xml.converters.reference.ReferenceXMLConverter;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.HasDataSpecification;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

/**
 * Handles the conversion between IAsset objects and the XML tag
 * &lt;aas:assets&gt; in both directions
 * 
 * @author conradi
 *
 */
public class AssetXMLConverter {

	public static final String ASSETS = "aas:assets";
	public static final String ASSET = "aas:asset";
	public static final String ASSET_IDENTIFICATION_MODEL_REF = "aas:assetIdentificationModelRef";
	public static final String ASSET_KIND = "aas:kind";
	public static final String ASSET_BILLOFMATERIAL = "aas:billOfMaterialRef";

	/**
	 * Parses &lt;aas:assets&gt; and builds the Asset objects from it
	 * 
	 * @param xmlAssetObject
	 *            a Map containing the content of the XML tag &lt;aas:assets&gt;
	 * @return a List of IAsset objects parsed form the given XML Map
	 */
	public static List<IAsset> parseAssets(Map<String, Object> xmlAssetObject) {
		List<Map<String, Object>> xmlAssets = XMLHelper.getList(xmlAssetObject.get(ASSET));
		List<IAsset> assets = new ArrayList<>();

		for (Map<String, Object> xmlAsset : xmlAssets) {
			Asset asset = new Asset();

			IdentifiableXMLConverter.populateIdentifiable(xmlAsset, Identifiable.createAsFacadeNonStrict(asset, KeyElements.ASSET));
			HasDataSpecificationXMLConverter.populateHasDataSpecification(xmlAsset, HasDataSpecification.createAsFacade(asset));
			asset.setAssetKind(parseAssetKind(xmlAsset));

			if (xmlAsset.containsKey(ASSET_IDENTIFICATION_MODEL_REF)) {
				asset.setAssetIdentificationModel(parseAssetIdentificationModelRef(xmlAsset));
			}

			assets.add(asset);
		}
		return assets;
	}

	/**
	 * Parses &lt;aas:assetIdentificationModelRef&gt; and builds an IReference
	 * object from it
	 * 
	 * @param xmlObject
	 *            a Map containing the XML tag
	 *            &lt;aas:assetIdentificationModelRef&gt;
	 * @return an IReference object parsed form the given XML Map
	 */
	@SuppressWarnings("unchecked")
	private static IReference parseAssetIdentificationModelRef(Map<String, Object> xmlObject) {
		Map<String, Object> semanticIDObj = (Map<String, Object>) xmlObject.get(ASSET_IDENTIFICATION_MODEL_REF);
		return ReferenceXMLConverter.parseReference(semanticIDObj);
	}

	/**
	 * Parses &lt;aas:akind&gt; and gets the correct AssetKind from it
	 * 
	 * @param xmlObject
	 *            a Map containing the XML tag &lt;aas:kind&gt;
	 * @return the parsed AssetKind or null if none was present
	 */
	private static AssetKind parseAssetKind(Map<String, Object> xmlObject) {
		String assetKindValue = XMLHelper.getString(xmlObject.get(ASSET_KIND));
		if (!Strings.isNullOrEmpty(assetKindValue)) {

			assetKindValue = AASXPackageExplorerCompatibilityHandler.convertAssetKind(assetKindValue);

			return AssetKind.fromString(assetKindValue);
		} else {
			throw new RuntimeException("Necessary value 'AssetKind' was not found for one of the Assets in the XML file.");
		}
	}

	/**
	 * Builds &lt;aas:assets&gt; from a given Collection of IAsset objects
	 * 
	 * @param document
	 *            the XML document
	 * @param assets
	 *            a Collection of IAsset objects to build the XML for
	 * @return the &lt;aas:assets&gt; XML tag for the given IAsset objects
	 */
	public static Element buildAssetsXML(Document document, Collection<IAsset> assets) {
		Element root = document.createElement(ASSETS);

		List<Element> xmlAssetList = new ArrayList<>();
		for (IAsset asset : assets) {
			Element assetRoot = document.createElement(ASSET);
			IdentifiableXMLConverter.populateIdentifiableXML(document, assetRoot, asset);
			HasDataSpecificationXMLConverter.populateHasDataSpecificationXML(document, assetRoot, asset);
			buildAssetIdentificationModelRef(document, assetRoot, asset);
			buildBillOfMaterial(document, assetRoot, asset);
			buildAssetKind(document, assetRoot, asset);
			xmlAssetList.add(assetRoot);
		}

		for (Element element : xmlAssetList) {
			root.appendChild(element);
		}
		return root;
	}

	/**
	 * Builds &lt;aas:assetIdentificationModelRef&gt; from a given IAsset object
	 * 
	 * @param document
	 *            the XML document
	 * @param assetRoot
	 *            the XML tag to be populated
	 * @param asset
	 *            the IAsset object to build the XML for
	 */
	private static void buildAssetIdentificationModelRef(Document document, Element assetRoot, IAsset asset) {
		IReference assetIdentificationModel = asset.getAssetIdentificationModel();
		if (assetIdentificationModel != null) {
			Element assetIdentificationroot = document.createElement(ASSET_IDENTIFICATION_MODEL_REF);
			assetIdentificationroot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, assetIdentificationModel));
			assetRoot.appendChild(assetIdentificationroot);
		}
	}

	/**
	 * Builds &lt;aas:kind&gt; from a given IAsset object
	 * 
	 * @param document
	 *            the XML document
	 * @param assetRoot
	 *            the XML tag to be populated
	 * @param asset
	 *            the IAsset object to build the XML for
	 */
	private static void buildAssetKind(Document document, Element root, IAsset asset) {
		if (asset.getAssetKind() != null) {
			Element kindRoot = document.createElement(ASSET_KIND);
			kindRoot.appendChild(document.createTextNode(asset.getAssetKind().toString()));
			root.appendChild(kindRoot);
		}
	}

	/**
	 * Builds &lt;billOfMaterialRef&gt; from a given IAsset object
	 * 
	 * @param document
	 *            the XML document
	 * @param assetRoot
	 *            the XML tag to be populated
	 * @param asset
	 *            the IAsset object to build the XML for
	 */
	private static void buildBillOfMaterial(Document document, Element root, IAsset asset) {
		IReference billOfMaterial = asset.getBillOfMaterial();
		if (billOfMaterial != null) {
			Element billOfMaterialRoot = document.createElement(ASSET_BILLOFMATERIAL);
			billOfMaterialRoot.appendChild(ReferenceXMLConverter.buildReferenceXML(document, billOfMaterial));
			root.appendChild(billOfMaterialRoot);
		}
	}
}
