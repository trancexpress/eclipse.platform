/*******************************************************************************
 *  Copyright (c) 2007, 2014 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     Thirumala Reddy Mutchukota - Bug 432049, JobGroup API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.runtime.jobs;

import java.io.*;
import junit.framework.TestCase;
import org.eclipse.core.internal.jobs.JobManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Common superclass for all tests of the org.eclipse.core.runtime.jobs API. Provides
 * convenience methods useful for testing jobs.
 */
public class AbstractJobTest extends TestCase {
	public AbstractJobTest() {
		super("");
	}

	public AbstractJobTest(String name) {
		super(name);
	}

	/**
	 * Fails the test due to the given exception.
	 * @param message
	 * @param e
	 */
	public void fail(String message, Throwable e) {
		// If the exception is a CoreException with a multistatus
		// then print out the multistatus so we can see all the info.
		if (e instanceof CoreException) {
			IStatus status = ((CoreException) e).getStatus();
			if (status.getChildren().length > 0) {
				write(status, 0);
			}
		}
		fail(message + ": " + e);
	}

	protected void indent(OutputStream output, int indent) {
		for (int i = 0; i < indent; i++)
			try {
				output.write("\t".getBytes());
			} catch (IOException e) {
				//ignore
			}
	}

	protected void sleep(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			//ignore
		}
	}

	protected void write(IStatus status, int indent) {
		PrintStream output = System.out;
		indent(output, indent);
		output.println("Severity: " + status.getSeverity());

		indent(output, indent);
		output.println("Plugin ID: " + status.getPlugin());

		indent(output, indent);
		output.println("Code: " + status.getCode());

		indent(output, indent);
		output.println("Message: " + status.getMessage());

		if (status.isMultiStatus()) {
			IStatus[] children = status.getChildren();
			for (int i = 0; i < children.length; i++) {
				write(children[i], indent + 1);
			}
		}
	}

	/**
	 * Ensures job completes within the given time.
	 * @param job
	 * @param waitTime time in milliseconds
	 */
	protected void waitForCompletion(Job job, int waitTime) {
		int i = 0;
		int tickLength = 10;
		int ticks = waitTime / tickLength;
		while (job.getState() != Job.NONE) {
			sleep(tickLength);
			// sanity test to avoid hanging tests
			if (i++ > ticks) {
				dumpState();
				assertTrue("Timeout waiting for job to complete", false);
			}
		}
	}

	/**
	 * Ensures given job completes within a second.
	 */
	protected void waitForCompletion(Job job) {
		waitForCompletion(job, 1000);
	}

	/**
	 * Extra debugging for bug 109898
	 */
	protected void dumpState() {
		System.out.println("**** BEGIN DUMP JOB MANAGER INFORMATION ****");
		Job[] jobs = Job.getJobManager().find(null);
		for (int j = 0; j < jobs.length; j++)
			System.out.println("" + jobs[j] + " state: " + JobManager.printState(jobs[j]));
		System.out.println("**** END DUMP JOB MANAGER INFORMATION ****");
	}

}
