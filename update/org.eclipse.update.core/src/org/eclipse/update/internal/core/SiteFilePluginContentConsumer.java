package org.eclipse.update.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.*;
import java.net.URL;

import org.eclipse.core.runtime.*;
import org.eclipse.update.core.*;

/**
 * Plugin Content Consumer on a Site
 */
public class SiteFilePluginContentConsumer extends ContentConsumer {

	private IPluginEntry pluginEntry;
	private ISite site;

	/**
	 * Constructor for FileSite
	 */
	public SiteFilePluginContentConsumer(IPluginEntry pluginEntry, ISite site) {
		this.pluginEntry = pluginEntry;
		this.site = site;
	}

	/*
	 * @see ISiteContentConsumer#store(ContentReference, IProgressMonitor)
	 */
	public void store(ContentReference contentReference, IProgressMonitor monitor) throws CoreException {
		//String path = site.getURL().getFile();
		InputStream inStream = null;
		String pluginPath = null;		
		
		try {
			// FIXME: fragment code
			if (pluginEntry.isFragment()) {
				URL newURL = new URL(site.getURL(), Site.DEFAULT_FRAGMENT_PATH + pluginEntry.getVersionIdentifier().toString());
				pluginPath = newURL.getFile();
			} else {
				URL newURL = new URL(site.getURL(), Site.DEFAULT_PLUGIN_PATH + pluginEntry.getVersionIdentifier().toString());
				pluginPath = newURL.getFile();
			}
			String contentKey = contentReference.getIdentifier();
			pluginPath += pluginPath.endsWith(File.separator) ? contentKey : File.separator + contentKey;

			inStream = contentReference.getInputStream();
			UpdateManagerUtils.copyToLocal(inStream, pluginPath, null);
		} catch (IOException e) {
			String id = UpdateManagerPlugin.getPlugin().getDescriptor().getUniqueIdentifier();
			IStatus status = new Status(IStatus.ERROR, id, IStatus.OK, "Error creating file:" + pluginPath, e);
			throw new CoreException(status);
		} finally {
			try {
				// close stream
				inStream.close();
			} catch (Exception e) {
			}
		}

	}

	/*
	 * @see ISiteContentConsumer#close()
	 */
	public void close() {
		((Site)site).addPluginEntry(pluginEntry);		
	}
	
	
	
}