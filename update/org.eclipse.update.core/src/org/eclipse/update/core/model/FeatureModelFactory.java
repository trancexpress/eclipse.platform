package org.eclipse.update.core.model;

/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IStatus;
import org.xml.sax.SAXException;

/**
 * An object which can create install related model objects (typically when
 * parsing feature manifest files and site maps).
 * <p>
 * This class may be instantiated, or further subclassed.
 * </p>
 */

public class FeatureModelFactory {
	
	/**
	 * Creates a factory which can be used to create install model objects.
	 */
	public FeatureModelFactory() {
		super();
	}
	
	/**
	 * Constructs a feature model from stream
	 * 
	 * @param stream feature stream
	 */
	public FeatureModel parseFeature(InputStream stream) throws ParsingException, IOException, SAXException {
		DefaultFeatureParser parser = new DefaultFeatureParser(this);
		
		FeatureModel featureModel =  parser.parse(stream);
			if (parser.getStatus().getChildren().length>0){
				// some internalError were detected
				IStatus[] children = parser.getStatus().getChildren();
				String error = "";
				for (int i = 0; i < children.length; i++) {
					error = error + "\r\n"+ children[i].getMessage();
				}
				throw new ParsingException(new Exception(error));
			}		
		return featureModel;
	}

	/**
	 * Returns a new feature model which is not initialized.
	 *
	 * @return a new feature model
	 */
	public FeatureModel createFeatureModel() {
		return new FeatureModel();
	}

	/**
	 * Returns a new feature install handler model which is not initialized.
	 *
	 * @return a new feature install handler model
	 */
	public InstallHandlerEntryModel createInstallHandlerEntryModel() {
		return new InstallHandlerEntryModel();
	}

	/**
	 * Returns a new import model which is not initialized.
	 *
	 * @return a new import model
	 */
	public ImportModel createImportModel() {
		return new ImportModel();
	}

	/**
	 * Returns a new plug-in entry model which is not initialized.
	 *
	 * @return a new plug-in entry model
	 */
	public PluginEntryModel createPluginEntryModel() {
		return new PluginEntryModel();
	}

	/**
	 * Returns a new non-plug-in entry model which is not initialized.
	 *
	 * @return a new non-plug-in entry model
	 */
	public NonPluginEntryModel createNonPluginEntryModel() {
		return new NonPluginEntryModel();
	}

	/**
	 * Returns a new content group model which is not initialized.
	 *
	 * @return a new content group model
	 */
	public ContentGroupModel createContentGroupModel() {
		return new ContentGroupModel();
	}

	/**
	 * Returns a new URL Entry model which is not initialized.
	 *
	 * @return a new URL Entry model
	 */
	public URLEntryModel createURLEntryModel() {
		return new URLEntryModel();
	}

	}
