/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.remote.internal.terminal.connector;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.remote.internal.terminal.settings.RemoteSettings;
import org.eclipse.remote.internal.terminal.settings.RemoteSettingsPage;
import org.eclipse.remote.internal.terminal.view.TerminalViewPlugin;
import org.eclipse.remote.terminal.IRemoteSettings;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsPage;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

@SuppressWarnings("restriction")
public class RemoteConnector extends TerminalConnectorImpl {
	private OutputStream fOutputStream;
	private InputStream fInputStream;
	private RemoteConnectionManager fConnection;
	private int fWidth;
	private int fHeight;
	private final RemoteSettings fSettings;

	public RemoteConnector() {
		this(new RemoteSettings());
	}

	public RemoteConnector(RemoteSettings settings) {
		fSettings = settings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#connect(org.eclipse.tm.internal.terminal.
	 * provisional.api.ITerminalControl)
	 */
	@Override
	public void connect(ITerminalControl control) {
		super.connect(control);
		fControl.setState(TerminalState.CONNECTING);
		fConnection = new RemoteConnectionManager(this, control);
		fConnection.schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#doDisconnect()
	 */
	@Override
	public synchronized void doDisconnect() {
		if (getInputStream() != null) {
			try {
				getInputStream().close();
			} catch (Exception exception) {
				TerminalViewPlugin.log(exception);
			}
		}

		if (getTerminalToRemoteStream() != null) {
			try {
				getTerminalToRemoteStream().close();
			} catch (Exception exception) {
				TerminalViewPlugin.log(exception);
			}
		}

		fConnection.cancel();
	}

	public InputStream getInputStream() {
		return fInputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#getSettingsSummary()
	 */
	@Override
	public String getSettingsSummary() {
		return fSettings.getSummary();
	}

	/**
	 * Return the SSH Settings.
	 * 
	 * @return the settings for a concrete connection.
	 * @since org.eclipse.tm.terminal.ssh 2.0 renamed from getTelnetSettings()
	 */
	public IRemoteSettings getSshSettings() {
		return fSettings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#getTerminalToRemoteStream()
	 */
	@Override
	public OutputStream getTerminalToRemoteStream() {
		return fOutputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#initialize()
	 */
	@Override
	public void initialize() throws Exception {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#load(org.eclipse.tm.internal.terminal.provisional
	 * .api.ISettingsStore)
	 */
	@Override
	public void load(ISettingsStore store) {
		fSettings.load(store);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#makeSettingsPage()
	 */
	@Override
	public ISettingsPage makeSettingsPage() {
		return new RemoteSettingsPage(fSettings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#save(org.eclipse.tm.internal.terminal.provisional
	 * .api.ISettingsStore)
	 */
	@Override
	public void save(ISettingsStore store) {
		fSettings.save(store);
	}

	public void setInputStream(InputStream inputStream) {
		fInputStream = inputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		fOutputStream = outputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl#setTerminalSize(int, int)
	 */
	@Override
	public void setTerminalSize(int newWidth, int newHeight) {
		if (fConnection != null && (newWidth != fWidth || newHeight != fHeight)) {
			// avoid excessive communications due to change size requests by caching previous size
			fConnection.setTerminalSize(newWidth, newHeight, 8 * newWidth, 8 * newHeight);
			fWidth = newWidth;
			fHeight = newHeight;
		}
	}
}