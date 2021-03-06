/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
 *   
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Apache License,
 *  Version 2.0 which accompanies this distribution and is available at
 *  http://www.apache.org/licenses/LICENSE-2.0.txt
 ******************************************************************************/
package org.locationtech.geowave.core.store.cli.remote;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.geowave.core.cli.annotations.GeowaveOperation;
import org.locationtech.geowave.core.cli.api.Command;
import org.locationtech.geowave.core.cli.api.DefaultOperation;
import org.locationtech.geowave.core.cli.api.OperationParams;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

@GeowaveOperation(name = "rmindex", parentOperation = RemoteSection.class)
@Parameters(hidden = true, commandDescription = "Remove an index from the remote store and all associated data for the index")
public class RemoveIndexCommand extends
		DefaultOperation implements
		Command
{

	@Parameter(description = "<store name> <indexId>")
	private List<String> parameters = new ArrayList<String>();

	@Override
	public void execute(
			final OperationParams params ) {
		// Ensure we have all the required arguments
		if (parameters.size() != 2) {
			throw new ParameterException(
					"Requires arguments: <store name> <indexId>");
		}

		throw new UnsupportedOperationException(
				"This operation is not yet supported");
	}
}
