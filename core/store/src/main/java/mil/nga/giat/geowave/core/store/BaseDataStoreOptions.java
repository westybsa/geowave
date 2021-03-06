/*******************************************************************************
 * Copyright (c) 2013-2017 Contributors to the Eclipse Foundation
 * 
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License,
 * Version 2.0 which accompanies this distribution and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 ******************************************************************************/
package mil.nga.giat.geowave.core.store;

import com.beust.jcommander.Parameter;

public class BaseDataStoreOptions implements
		DataStoreOptions
{
	@Parameter(names = "--persistAdapter", hidden = true, arity = 1)
	protected boolean persistAdapter = true;

	@Parameter(names = "--persistIndex", hidden = true, arity = 1)
	protected boolean persistIndex = true;

	@Parameter(names = "--persistDataStatistics", hidden = true, arity = 1)
	protected boolean persistDataStatistics = true;

	@Parameter(names = "--createTable", hidden = true, arity = 1)
	protected boolean createTable = true;

	// TODO GEOWAVE-1018 the secondaryIndexDataStore isn't implemented so this
	// "alt index" option should not be used at the moment
	@Parameter(names = "--useAltIndex", hidden = true, arity = 1)
	protected boolean useAltIndex = false;

	@Parameter(names = "--enableBlockCache", hidden = true, arity = 1)
	protected boolean enableBlockCache = true;

	@Parameter(names = "--enableServerSideLibrary", arity = 1)
	protected boolean enableServerSideLibrary = true;

	@Parameter(names = "--maxRangeDecomposition", arity = 1)
	protected int configuredMaxRangeDecomposition = Integer.MIN_VALUE;

	@Parameter(names = "--aggregationMaxRangeDecomposition", arity = 1)
	protected int configuredAggregationMaxRangeDecomposition = Integer.MIN_VALUE;

	@Override
	public boolean isPersistDataStatistics() {
		return persistDataStatistics;
	}

	public void setPersistDataStatistics(
			final boolean persistDataStatistics ) {
		this.persistDataStatistics = persistDataStatistics;
	}

	@Override
	public boolean isPersistAdapter() {
		return persistAdapter;
	}

	public void setPersistAdapter(
			final boolean persistAdapter ) {
		this.persistAdapter = persistAdapter;
	}

	@Override
	public boolean isPersistIndex() {
		return persistIndex;
	}

	public void setPersistIndex(
			final boolean persistIndex ) {
		this.persistIndex = persistIndex;
	}

	@Override
	public boolean isCreateTable() {
		return createTable;
	}

	public void setCreateTable(
			final boolean createTable ) {
		this.createTable = createTable;
	}

	@Override
	public boolean isUseAltIndex() {
		return useAltIndex;
	}

	public void setUseAltIndex(
			final boolean useAltIndex ) {
		this.useAltIndex = useAltIndex;
	}

	@Override
	public boolean isEnableBlockCache() {
		return enableBlockCache;
	}

	public void setEnableBlockCache(
			final boolean enableBlockCache ) {
		this.enableBlockCache = enableBlockCache;
	}

	@Override
	public boolean isServerSideLibraryEnabled() {
		return enableServerSideLibrary;
	}

	public void setServerSideLibraryEnabled(
			final boolean enableServerSideLibrary ) {
		this.enableServerSideLibrary = enableServerSideLibrary;
	}

	@Override
	public int getMaxRangeDecomposition() {
		return configuredMaxRangeDecomposition == Integer.MIN_VALUE ? defaultMaxRangeDecomposition()
				: configuredMaxRangeDecomposition;
	}

	protected int defaultMaxRangeDecomposition() {
		return 2000;
	}

	public void setMaxRangeDecomposition(
			final int maxRangeDecomposition ) {
		this.configuredMaxRangeDecomposition = maxRangeDecomposition;
	}

	@Override
	public int getAggregationMaxRangeDecomposition() {
		return configuredAggregationMaxRangeDecomposition == Integer.MIN_VALUE ? defaultAggregationMaxRangeDecomposition()
				: configuredAggregationMaxRangeDecomposition;
	}

	protected int defaultAggregationMaxRangeDecomposition() {
		return 10;
	}

	public void setAggregationMaxRangeDecomposition(
			final int aggregationMaxRangeDecomposition ) {
		this.configuredAggregationMaxRangeDecomposition = aggregationMaxRangeDecomposition;
	}
}
