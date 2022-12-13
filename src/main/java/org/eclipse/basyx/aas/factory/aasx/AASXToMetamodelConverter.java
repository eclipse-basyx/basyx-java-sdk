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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleFactory;
import org.eclipse.basyx.aas.factory.exception.MultipleThumbnailFoundException;
import org.eclipse.basyx.aas.factory.xml.XMLToMetamodelConverter;
import org.eclipse.basyx.aas.metamodel.map.AasEnv;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IFile;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * The AASX package converter converts a aasx package into a list of aas, a list
 * of submodels a list of assets, a list of Concept descriptions
 * 
 * The aas provides the references to the submodels and assets
 * 
 * @author zhangzai, conradi, danish
 *
 */
public class AASXToMetamodelConverter {

	private static final String XML_TYPE = "http://www.admin-shell.io/aasx/relationships/aas-spec";
	private static final String AASX_ORIGIN = "/aasx/aasx-origin";

	private String aasxPath;
	private OPCPackage aasxRoot;
	private InputStream aasxInputStream;

	private Set<AASBundle> bundles;

	private AasEnv aasEnv;

	private static Logger logger = LoggerFactory.getLogger(AASXToMetamodelConverter.class);

	public AASXToMetamodelConverter(String path) {
		this.aasxPath = path;
	}

	public AASXToMetamodelConverter(InputStream stream) {
		this.aasxInputStream = stream;
	}

	public AasEnv retrieveAasEnv() throws ParserConfigurationException, SAXException, IOException, InvalidFormatException {
		if (aasEnv != null) {
			return aasEnv;
		}

		loadAASX();

		String xmlContent = getXMLResourceString(aasxRoot);
		XMLToMetamodelConverter converter = new XMLToMetamodelConverter(xmlContent);
		closeOPCPackage();
		return converter.parseAasEnv();
	}
	
	public InputStream retrieveThumbnail() throws IOException, InvalidFormatException {
		loadAASX();

		InputStream thumbnailStream = getThumbnailStream(aasxRoot);
		
		closeOPCPackage();
		
		return thumbnailStream;
	}

	@SuppressWarnings("unchecked")
	public <T extends AASBundle> Set<T> retrieveAASBundles() throws IOException, ParserConfigurationException, SAXException, InvalidFormatException {

		// If the XML was already parsed return cached Bundles
		if (bundles != null) {
			return (Set<T>) bundles;
		}

		loadAASX();

		AasEnv localAasEnv = retrieveAasEnv();

		bundles = new AASBundleFactory().create(localAasEnv.getAssetAdministrationShells(), localAasEnv.getSubmodels(), localAasEnv.getAssets());

		closeOPCPackage();

		return (Set<T>) bundles;
	}

	private void loadAASX() throws IOException, InvalidFormatException {
		if (aasxInputStream == null) {
			aasxInputStream = FileLoaderHelper.getInputStream(aasxPath);
		}

		if (aasxRoot == null) {
			aasxRoot = OPCPackage.open(aasxInputStream);
		}
	}

	private void closeOPCPackage() throws IOException {
		aasxRoot.close();
	}

	/**
	 * Return the Content of the XML file in the aasx-package as String
	 * 
	 * @param aasxPackage
	 *            - the root package of the AASX
	 * @return Content of XML as String
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	private String getXMLResourceString(OPCPackage aasxPackage) throws InvalidFormatException, IOException {

		// Get the "/aasx/aasx-origin" Part. It is Relationship source for the
		// XML-Document
		PackagePart originPart = aasxPackage.getPart(PackagingURIHelper.createPartName(AASX_ORIGIN));

		// Get the Relation to the XML Document
		PackageRelationshipCollection originRelationships = originPart.getRelationshipsByType(XML_TYPE);

		// If there is more than one or no XML-Document that is an error
		if (originRelationships.size() > 1) {
			throw new RuntimeException("More than one 'aasx-spec' document found in .aasx");
		} else if (originRelationships.size() == 0) {
			throw new RuntimeException("No 'aasx-spec' document found in .aasx");
		}

		// Get the PackagePart of the XML-Document
		PackagePart xmlPart = originPart.getRelatedPart(originRelationships.getRelationship(0));

		// Read the content from the PackagePart
		InputStream stream = xmlPart.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
		return writer.toString();
	}
	
	private InputStream getThumbnailStream(OPCPackage aasxPackage) throws IOException {
		PackageRelationshipCollection thumbnailPackageRelationship = aasxPackage.getRelationshipsByType(MetamodelToAASXConverter.THUMBNAIL_TYPE);
		
		checkIfThumbnailExists(thumbnailPackageRelationship);
		
		PackagePart thumbnailPart = aasxPackage.getPart(thumbnailPackageRelationship.getRelationship(0));
		
		return thumbnailPart.getInputStream();
	}

	private void checkIfThumbnailExists(PackageRelationshipCollection thumbnailPackageRelationship) throws MultipleThumbnailFoundException, FileNotFoundException {
		if (thumbnailPackageRelationship.size() > 1) {
			throw new MultipleThumbnailFoundException("More than one Thumbnail found in the specified package");
		} else if (thumbnailPackageRelationship.size() == 0) {
			throw new FileNotFoundException("No Thumbnail found in the specified package");
		}
	}

	/**
	 * Load the referenced filepaths in the submodels such as PDF, PNG files from
	 * the package
	 * 
	 * @return a map of the folder name and folder path, the folder holds the files
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws InvalidFormatException
	 * 
	 */
	private List<String> parseReferencedFilePathsFromAASX() throws IOException, ParserConfigurationException, SAXException, InvalidFormatException {

		Set<? extends AASBundle> bundles = retrieveAASBundles();

		List<ISubmodel> submodels = new ArrayList<>();

		// Get the Submodels from all AASBundles
		for (AASBundle bundle : bundles) {
			submodels.addAll(bundle.getSubmodels());
		}

		List<String> paths = new ArrayList<String>();

		for (ISubmodel sm : submodels) {
			paths.addAll(parseElements(sm.getSubmodelElements().values()));
		}
		return paths;
	}

	/**
	 * Gets the paths from a collection of ISubmodelElement
	 * 
	 * @param elements
	 * @return the Paths from the File elements
	 */
	private List<String> parseElements(Collection<ISubmodelElement> elements) {
		List<String> paths = new ArrayList<String>();

		for (ISubmodelElement element : elements) {
			if (element instanceof IFile) {
				IFile file = (IFile) element;
				// If the path contains a "://", we can assume, that the Path is a link to an
				// other server
				// e.g. http://localhost:8080/aasx/...
				if (!file.getValue().contains("://")) {
					paths.add(file.getValue());
				}
			} else if (element instanceof ISubmodelElementCollection) {
				ISubmodelElementCollection collection = (ISubmodelElementCollection) element;
				paths.addAll(parseElements(collection.getSubmodelElements().values()));
			}
		}
		return paths;
	}

	/**
	 * Unzips all files referenced by the aasx file according to its relationships
	 * 
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException
	 * @throws InvalidFormatException
	 */
	public void unzipRelatedFiles() throws IOException, ParserConfigurationException, SAXException, URISyntaxException, InvalidFormatException {
		// load folder which stores the files
		loadAASX();

		var rootFolder = getRootFolder();

		List<String> files = parseReferencedFilePathsFromAASX();
		for (String filePath : files) {
			// name of the folder
			unzipFile(filePath, aasxRoot, rootFolder);
		}

		closeOPCPackage();
	}
	
	/**
	 * Unzips all files referenced by the aasx file to a specified directory
	 *
	 * @param pathToDirectory
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws URISyntaxException
	 */
	public void unzipRelatedFiles(Path pathToDirectory) throws InvalidFormatException, IOException, ParserConfigurationException, SAXException, URISyntaxException {
	  loadAASX();
	  
	  List<String> files = parseReferencedFilePathsFromAASX();
	  for (String filePath: files) {
	    unzipFile(filePath, aasxRoot, pathToDirectory);
	  }
	  
	  closeOPCPackage();
	}

	/**
	 * Create a folder to hold the unpackaged files The folder has the path
	 * \target\classes\docs
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	protected Path getRootFolder() throws IOException, URISyntaxException {
		URI uri = AASXToMetamodelConverter.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		URI parent = new File(uri).getParentFile().toURI();
		return Paths.get(parent);
	}

	/**
	 * unzip the file folders
	 * 
	 * @param filePath
	 *            - path of the file in the aasx to unzip
	 * @param aasxPath
	 *            - aasx path
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws InvalidFormatException
	 */
	private void unzipFile(String filePath, OPCPackage aasxRoot, Path pathToDirectory) throws IOException, URISyntaxException, InvalidFormatException {
		// Create destination directory
		if (filePath.startsWith("/")) {
			filePath = filePath.substring(1);
		}
		if (filePath.isEmpty()) {
			logger.warn("A file with empty path can not be unzipped.");
			return;
		}
		
		logger.info("Unzipping " + filePath);
		String relativePath = "files/" + VABPathTools.getParentPath(filePath);
		Path destDir;

	    destDir = pathToDirectory.resolve(relativePath);

		logger.info("Unzipping to " + destDir);
		Files.createDirectories(destDir);

		PackagePart part = aasxRoot.getPart(PackagingURIHelper.createPartName("/" + filePath));

		if (part == null) {
			logger.warn("File '" + filePath + "' could not be unzipped. It does not exist in .aasx.");
			return;
		}

		String targetPath = destDir.toString() + "/" + VABPathTools.getLastElement(filePath);
		InputStream stream = part.getInputStream();
		FileUtils.copyInputStreamToFile(stream, new File(targetPath));
	}
}
