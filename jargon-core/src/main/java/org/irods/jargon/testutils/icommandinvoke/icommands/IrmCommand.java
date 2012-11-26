/**
 *
 */
package org.irods.jargon.testutils.icommandinvoke.icommands;

import java.util.ArrayList;
import java.util.List;

import org.irods.jargon.testutils.icommandinvoke.IcommandException;

/**
 * Implement the irm irods icommand specifying an object and options
 * 
 * @author Mike Conway, DICE (www.irods.org)
 * @since 10/20/2009
 */
public class IrmCommand implements Icommand {
	private String objectName = "";
	private boolean force = false;
	private boolean verbose = false;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(final boolean force) {
		this.force = force;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(final String objectName) {
		this.objectName = objectName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.irods.jargon.icommandinvoke.icommands.Icommand#buildCommand()
	 */
	@Override
	public List<String> buildCommand() throws IcommandException {
		if ((objectName == null) || (objectName.length() == 0)) {
			throw new IllegalArgumentException("no object name specified");
		}

		List<String> commands = new ArrayList<String>();
		commands.add("irm");

		if (isForce()) {
			commands.add("-f");
		}

		if (isVerbose()) {
			commands.add("-vv");
		}

		commands.add("-r");
		commands.add(objectName);

		return commands;
	}

}