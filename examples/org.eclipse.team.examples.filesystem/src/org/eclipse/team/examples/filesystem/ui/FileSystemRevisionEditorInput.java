package org.eclipse.team.examples.filesystem.ui;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.team.core.history.IFileRevision;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class FileSystemRevisionEditorInput extends PlatformObject implements IWorkbenchAdapter, IStorageEditorInput {

	private IFileRevision fileRevision;
	private IStorage storage;

	public FileSystemRevisionEditorInput(IFileRevision revision) {
		this.fileRevision = revision;
		try {
			this.storage = revision.getStorage(new NullProgressMonitor());
		} catch (CoreException e) {
		}
	}

	public Object[] getChildren(Object o) {
		return new Object[0];
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	public String getLabel(Object o) {
		if (storage != null) {
			return storage.getName();
		}
		return ""; //$NON-NLS-1$
	}

	public Object getParent(Object o) {
		return null;
	}

	public IStorage getStorage() throws CoreException {
		return storage;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		if (fileRevision != null)
			return fileRevision.getName() + " " + fileRevision.getContentIdentifier();  //$NON-NLS-1$

		if (storage != null) {
			return storage.getName() + " " + DateFormat.getInstance().format(new Date(((IFileState) storage).getModificationTime())); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		if (fileRevision != null)
			try {
				return getStorage().getFullPath().toString();
			} catch (CoreException e) {
			}

		if (storage != null)
			return storage.getFullPath().toString();

		return ""; //$NON-NLS-1$
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class) {
			return this;
		}
		if (adapter == IFileRevision.class)
			return fileRevision;
		return super.getAdapter(adapter);
	}

}
