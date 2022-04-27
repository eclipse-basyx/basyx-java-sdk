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
package org.eclipse.basyx.aas.factory.aasx;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.RelationshipSource;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.internal.MemoryPackagePart;
import org.eclipse.basyx.aas.factory.xml.MetamodelToXMLConverter;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.IAsset;
import org.eclipse.basyx.aas.metamodel.map.AasEnv;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.parts.IConceptDescription;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can be used to generate an .aasx file from Metamodel Objects and
 * the Files referred to in the Submodels
 * 
 * @author conradi
 *
 */
public class MetamodelToAASXConverter {

	private static Logger logger = LoggerFactory.getLogger(MetamodelToAASXConverter.class);

	private static final String MIME_PLAINTXT = "text/plain";
	private static final String MIME_XML = "application/xml";

	private static final String ORIGIN_RELTYPE = "http://www.admin-shell.io/aasx/relationships/aasx-origin";
	private static final String ORIGIN_PATH = "/aasx/aasx-origin";
	private static final String ORIGIN_CONTENT = "Intentionally empty.";

	private static final String AASSPEC_RELTYPE = "http://www.admin-shell.io/aasx/relationships/aas-spec";
	private static final String XML_PATH = "/aasx/xml/content.xml";

	private static final String AASSUPPL_RELTYPE = "http://www.admin-shell.io/aasx/relationships/aas-suppl";

	/**
	 * Generates the .aasx file and writes it to the given OutputStream
	 * 
	 * @param aasList
	 *            the AASs to be saved in the .aasx
	 * @param assetList
	 *            the Assets to be saved in the .aasx
	 * @param conceptDescriptionList
	 *            the ConceptDescriptions to be saved in the .aasx
	 * @param submodelList
	 *            the Submodels to be saved in the .aasx
	 * @param files
	 *            the files referred to in the Submodels
	 * @param os
	 *            the OutputStream the resulting .aasx is written to
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	public static void buildAASX(Collection<IAssetAdministrationShell> aasList, Collection<IAsset> assetList, Collection<IConceptDescription> conceptDescriptionList, Collection<ISubmodel> submodelList, Collection<InMemoryFile> files,
			OutputStream os) throws IOException, TransformerException, ParserConfigurationException {

		prepareFilePaths(submodelList, files);

		OPCPackage rootPackage = OPCPackage.create(os);

		PackagePart origin = createAASXPart(rootPackage, rootPackage, ORIGIN_PATH, MIME_PLAINTXT, ORIGIN_RELTYPE, ORIGIN_CONTENT.getBytes());

		String xml = convertToXML(aasList, assetList, conceptDescriptionList, submodelList);

		PackagePart xmlPart = createAASXPart(rootPackage, origin, XML_PATH, MIME_XML, AASSPEC_RELTYPE, xml.getBytes());

		storeFilesInAASX(submodelList, files, rootPackage, xmlPart);

		saveAASX(os, rootPackage);
	}

	/**
	 * Generates the .aasx file and writes it to the given OutputStream
	 * 
	 * @param aasEnv
	 * @param files
	 * @param os
	 *            the OutputStream the resulting .aasx is written to
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	public static void buildAASX(AasEnv aasEnv, Collection<InMemoryFile> files, OutputStream os) throws IOException, TransformerException, ParserConfigurationException {
		buildAASX(aasEnv.getAssetAdministrationShells(), aasEnv.getAssets(), aasEnv.getConceptDescriptions(), aasEnv.getSubmodels(), files, os);
	}

	/**
	 * Stores the files from the Submodels in the .aasx file
	 * 
	 * @param submodelList
	 *            the Submodels
	 * @param files
	 *            the content of the files
	 * @param rootPackage
	 *            the OPCPackage
	 * @param xmlPart
	 *            the Part the files should be related to
	 */
	private static void storeFilesInAASX(Collection<ISubmodel> submodelList, Collection<InMemoryFile> files, OPCPackage rootPackage, PackagePart xmlPart) {

		for (ISubmodel sm : submodelList) {
			for (File file : findFileElements(sm.getSubmodelElements().values())) {
				String filePath = file.getValue();
				storeFileInAASX(files, rootPackage, xmlPart, file, filePath);
			}
		}
	}

	/**
	 * Stores a single file in the .aasx file
	 * 
	 * @param files
	 * @param rootPackage
	 * @param xmlPart
	 * @param file
	 * @param filePath
	 */
	private static void storeFileInAASX(Collection<InMemoryFile> files, OPCPackage rootPackage, PackagePart xmlPart, File file, String filePath) {
		try {
			InMemoryFile content = findFileByPath(files, filePath);
			logger.trace("Writing file '" + filePath + "' to .aasx.");
			createAASXPart(rootPackage, xmlPart, filePath, file.getMimeType(), AASSUPPL_RELTYPE, content.getFileContent());
		} catch (ResourceNotFoundException e) {
			// Log that a file is missing and continue building the .aasx
			logger.warn("Could not add File '" + filePath + "'. It was not contained in given InMemoryFiles.");
		}
	}

	/**
	 * Saves the OPCPackage to the given OutputStream
	 * 
	 * @param os
	 *            the Stream to be saved to
	 * @param rootPackage
	 *            the Package to be saved
	 * @throws IOException
	 */
	private static void saveAASX(OutputStream os, OPCPackage rootPackage) throws IOException {
		rootPackage.flush();
		rootPackage.save(os);
	}

	/**
	 * Generates a UUID. Every element of the .aasx needs a unique Id according to
	 * the specification
	 * 
	 * @return UUID
	 */
	private static String createUniqueID() {
		// only letters or underscore as start of id allowed
		// https://www.w3.org/TR/1999/REC-xml-names-19990114/#ns-qualnames
		//
		// old AASX Package Explorer versions expect a leading R
		return "Rid_" + UUID.randomUUID().toString();
	}

	/**
	 * Creates a Part (a file in the .aasx) of the .aasx and adds it to the Package
	 * 
	 * @param root
	 *            the OPCPackage
	 * @param relateTo
	 *            the Part of the OPC the relationship of the new Part should be
	 *            added to
	 * @param path
	 *            the path inside the .aasx where the new Part should be created
	 * @param mimeType
	 *            the mime-type of the file
	 * @param relType
	 *            the type of the Relationship
	 * @param content
	 *            the data the new part should contain
	 * @return the created PackagePart; Returned in case it is needed late as a Part
	 *         to relate to
	 */
	private static PackagePart createAASXPart(OPCPackage root, RelationshipSource relateTo, String path, String mimeType, String relType, byte[] content) {
		if (mimeType == null || mimeType.equals("")) {
			throw new RuntimeException("Could not create AASX Part '" + path + "'. No MIME_TYPE specified.");
		}

		PackagePartName partName = null;
		MemoryPackagePart part = null;
		try {
			partName = PackagingURIHelper.createPartName(path);
			part = new MemoryPackagePart(root, partName, mimeType);
		} catch (InvalidFormatException e) {
			// This occurs if the given MIME-Type is not valid according to RFC2046
			throw new RuntimeException("Could not create AASX Part '" + path + "'", e);
		}
		writeDataToPart(part, content);
		root.registerPartAndContentType(part);
		// set TargetMode to EXTERNAL to force absolute file paths
		// this step is necessary for compatibility reasons with AASXPackageExplorer
		relateTo.addRelationship(partName, TargetMode.EXTERNAL, relType, createUniqueID());
		return part;
	}

	/**
	 * Writes the content of a byte[] to a Part
	 * 
	 * @param part
	 *            the Part to be written to
	 * @param content
	 *            the content to be written to the part
	 */
	private static void writeDataToPart(PackagePart part, byte[] content) {
		try (OutputStream ostream = part.getOutputStream();) {
			ostream.write(content);
			ostream.flush();
		} catch (Exception e) {
			throw new RuntimeException("Failed to write content to AASX Part '" + part.getPartName().getName() + "'", e);
		}
	}

	/**
	 * Uses the MetamodelToXMLConverter to generate the XML
	 */
	private static String convertToXML(Collection<IAssetAdministrationShell> aasList, Collection<IAsset> assetList, Collection<IConceptDescription> conceptDescriptionList, Collection<ISubmodel> submodelList)
			throws TransformerException, ParserConfigurationException {

		StringWriter writer = new StringWriter();
		MetamodelToXMLConverter.convertToXML(aasList, assetList, conceptDescriptionList, submodelList, new StreamResult(writer));

		return writer.toString();
	}

	/**
	 * Gets the File elements from a collection of elements Also recursively
	 * searches in SubmodelElementCollections
	 * 
	 * @param elements
	 *            the Elements to be searched for File elements
	 * @return the found Files
	 */
	private static Collection<File> findFileElements(Collection<ISubmodelElement> elements) {
		Collection<File> files = new ArrayList<>();

		for (ISubmodelElement element : elements) {
			if (element instanceof File) {
				files.add((File) element);
			} else if (element instanceof SubmodelElementCollection) {
				// Recursive call to deal with SubmodelElementCollections
				files.addAll(findFileElements(((SubmodelElementCollection) element).getSubmodelElements().values()));
			}
		}

		return files;
	}

	/**
	 * Find files which has a valid in memory file path
	 * 
	 * @param elements
	 * @param inMemoryFiles
	 * @return
	 */
	private static Collection<File> findInMemoryFileElements(Collection<ISubmodelElement> elements, Collection<InMemoryFile> inMemoryFiles) {
		Collection<File> files = findFileElements(elements);
		return files.stream().filter(f -> isInMemoryFile(inMemoryFiles, f.getValue())).collect(Collectors.toList());
	}

	/**
	 * Replaces the path in File Elements which has an in memory file with the
	 * result of preparePath
	 * 
	 * @param submodels
	 *            the Submodels
	 */
	private static void prepareFilePaths(Collection<ISubmodel> submodels, Collection<InMemoryFile> inMemoryFiles) {
		submodels.stream().forEach(sm -> findInMemoryFileElements(sm.getSubmodelElements().values(), inMemoryFiles).stream().forEach(f -> f.setValue(preparePath(f.getValue()))));
	}

	/**
	 * Removes the serverpart from a path. VABPathTools.getPathFromURL() also
	 * ensures that it starts with a slash.
	 * 
	 * @param path
	 *            the path to be prepared
	 * @return the prepared path
	 */
	private static String preparePath(String path) {
		return VABPathTools.getPathFromURL(path);
	}

	/**
	 * Finds an InMemoryFile by its path
	 * 
	 * @param files
	 *            the InMemoryFiles
	 * @param path
	 *            the path of the wanted file
	 * @return the InMemoryFile if it was found; else null
	 */
	private static InMemoryFile findFileByPath(Collection<InMemoryFile> files, String path) {
		for (InMemoryFile file : files) {
			if (preparePath(file.getPath()).equals(path)) {
				return file;
			}
		}
		throw new ResourceNotFoundException("The wanted file '" + path + "' was not found in the given files.");
	}

	/**
	 * Finds an InMemoryFile by its path
	 * 
	 * @param files
	 *            the InMemoryFiles
	 * @param path
	 *            the path of the wanted file
	 * @return the InMemoryFile if it was found; else null
	 */
	private static boolean isInMemoryFile(Collection<InMemoryFile> files, String path) {
		for (InMemoryFile file : files) {
			if (VABPathTools.stripSlashes(file.getPath()).equals(VABPathTools.stripSlashes(path))) {
				return true;
			}
		}
		return false;
	}
}
