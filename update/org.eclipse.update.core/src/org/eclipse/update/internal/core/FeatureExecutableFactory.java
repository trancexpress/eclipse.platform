package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;
import org.eclipse.update.core.model.FeatureModel;
import org.eclipse.update.core.model.FeatureModelFactory;

public class FeatureExecutableFactory extends BaseFeatureFactory {

	/*
	 * @see IFeatureFactory#createFeature(URL,ISite)
	 */

	public IFeature createFeature(URL url, ISite site) throws CoreException {

		TargetFeature feature = null;
		InputStream featureStream = null;
		
		if (url==null) return createFeature(site);
		
		// the URL should point to a directory, we will add the feature.xml
		url = validate(url);
		
		try {	
			IFeatureContentProvider contentProvider = new FeatureExecutableContentProvider(url);
			IFeatureContentConsumer contentConsumer = new FeatureExecutableContentConsumer();
			
			URL nonResolvedURL= contentProvider.getFeatureManifestReference(null/*IProgressMonitor*/).asURL();
			URL resolvedURL = URLEncoder.encode(nonResolvedURL);
			featureStream =resolvedURL.openStream();
			
			feature = (TargetFeature)this.parseFeature(featureStream);
			feature.setSite(site);
			
			feature.setFeatureContentProvider(contentProvider);
			feature.setContentConsumer(contentConsumer);
			
			feature.resolve(url, getResourceBundle(url));
			feature.markReadOnly();
		} catch (IOException e) {
			// if we cannot find the feature or the feature.xml...
			// We should not stop the execution 
			// but we must Log it all the time, not only when debugging...
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.WARNING, id, IStatus.OK, "Error opening feature.xml in the feature archive:" + url.toExternalForm(), e);
			UpdateManagerPlugin.getPlugin().getLog().log(status);
		} catch (Exception e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.WARNING, id, IStatus.OK, "Error parsing feature.xml in the feature archive:" + url.toExternalForm(), e);
			throw new CoreException(status);
		} finally {
			try {
				featureStream.close();
			} catch (Exception e) {
			}
		}
		return feature;
	}

	/*
	 * @see FeatureModelFactory#createFeatureModel()
	 */
	public FeatureModel createFeatureModel() {
		return new TargetFeature();
	}

	private IFeature createFeature(ISite site) throws CoreException {
		TargetFeature feature = null;
		
		IFeatureContentProvider contentProvider = new FeatureExecutableContentProvider(null);
		IFeatureContentConsumer contentConsumer = new FeatureExecutableContentConsumer();
			
		feature = (TargetFeature)createFeatureModel();
		feature.setSite(site); 
			
		feature.setFeatureContentProvider(contentProvider);
		feature.setContentConsumer(contentConsumer);
		
		// do not mark read only yet...	
		return feature;	
	}


	/**
	 * validates a URL as a directory URL
	 */
	private URL validate(URL url) throws CoreException {
		
		if (url==null) 
			throw newCoreException("URL is null",null);		
		
		if (!(url.getFile().endsWith("/") || url.getFile().endsWith(File.separator) || url.getFile().endsWith(Feature.FEATURE_XML))){
			try {
				String path = url.getFile() + "/";
				url = new URL(url.getProtocol(),url.getHost(),url.getPort(),path);
			} catch (MalformedURLException e){
				throw newCoreException("Unable to create new URL for url"+url.toExternalForm(),e);				
			}
		}
		return url;
	}

	private CoreException newCoreException(String s, Throwable e) throws CoreException {
		return new CoreException(new Status(IStatus.ERROR,"org.eclipse.update.core",0,s,e));
	}

}