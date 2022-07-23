/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import java.io.IOException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * Home for file system-related utility methods.
 */
public class FileSystemHelper {
	/** counter for generating unique random file system locations */
	protected static int nextLocationCounter = 0;
	private static final long MASK = 0x00000000FFFFFFFFL;

	/*
	 * Return the root directory for the temp dir.
	 */
	public static IPath getTempDir() {
		String tempPath = System.getProperty("java.io.tmpdir");
		try {
			tempPath = new java.io.File(tempPath).getCanonicalPath();
		} catch (IOException e) {
			//ignore and use non-canonical path
		}
		return new Path(tempPath);
	}

	/**
	 * Returns a unique location on disk.  It is guaranteed that no file currently
	 * exists at that location.  The returned location will be unique with respect
	 * to all other locations generated by this method in the current session.
	 * If the caller creates a folder or file at this location, they are responsible for
	 * deleting it when finished.
	 */
	public static IPath getRandomLocation(IPath parent) {
		IPath path = computeRandomLocation(parent);
		while (path.toFile().exists()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// ignore
			}
			path = computeRandomLocation(parent);
		}
		return path;
	}

	public static IPath computeRandomLocation(IPath parent) {
		long segment = (((long) ++nextLocationCounter) << 32) | (System.currentTimeMillis() & MASK);
		return parent.append(Long.toString(segment));
	}

	public static void clear(java.io.File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			String[] files = file.list();
			if (files != null) {
				for (String child : files) {
					clear(new java.io.File(file, child));
				}
			}
		}
		if (!file.delete()) {
			String message = "ensureDoesNotExistInFileSystem(File) could not delete: " + file.getPath();
			CoreTest.log(CoreTest.PI_HARNESS, new Status(IStatus.WARNING, CoreTest.PI_HARNESS, IStatus.OK, message, null));
		}
	}
}