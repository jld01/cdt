/*******************************************************************************
 * Copyright (c) 2000, 2007 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Markus Schorn (Wind River Systems)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.model;

 
import java.util.ArrayList;
import java.util.Map;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.IBinaryContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.core.util.MementoTokenizer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;

public class BinaryContainer extends Openable implements IBinaryContainer {

	public BinaryContainer (CProject cProject) {
		super (cProject, null, CCorePlugin.getResourceString("CoreModel.BinaryContainer.Binaries"), ICElement.C_VCONTAINER); //$NON-NLS-1$
	}

	synchronized void sync() {
		BinaryRunner runner = CModelManager.getDefault().getBinaryRunner(getCProject());
		if (runner != null) {
			runner.waitIfRunning();
		}
	}

	public IBinary[] getBinaries() throws CModelException {
		sync();
		ICElement[] e = getChildren();
		ArrayList list = new ArrayList(e.length);
		for (int i = 0; i < e.length; i++) {
			if (e[i] instanceof IBinary) {
				IBinary bin = (IBinary)e[i];
				if (bin.showInBinaryContainer()) {
					list.add(bin);
				}
			}
		}
		IBinary[] b = new IBinary[list.size()];
		list.toArray(b);
		return b;
	}

	public CElementInfo createElementInfo() {
		return new BinaryContainerInfo(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.internal.core.model.Openable#buildStructure(org.eclipse.cdt.internal.core.model.OpenableInfo, org.eclipse.core.runtime.IProgressMonitor, java.util.Map, org.eclipse.core.resources.IResource)
	 */
	protected boolean buildStructure(OpenableInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource)
		throws CModelException {
		// this will bootstrap/start the runner for the project.
		CModelManager.getDefault().getBinaryRunner(getCProject());
		return true;
	}

	@Override
	public ICElement getHandleFromMemento(String token, MementoTokenizer memento) {
		return null;
	}

	@Override
	public String getHandleMemento() {
		return null;
	}

	@Override
	protected char getHandleMementoDelimiter() {
		Assert.isTrue(false, "Should not be called"); //$NON-NLS-1$
		return 0;
	}

}
