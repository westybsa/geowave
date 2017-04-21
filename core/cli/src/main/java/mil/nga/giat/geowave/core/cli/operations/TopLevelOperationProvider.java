package mil.nga.giat.geowave.core.cli.operations;

import mil.nga.giat.geowave.core.cli.spi.CLIOperationProviderSpi;

/**
 * Top-level operation provider for GeoWave commands
 */
public class TopLevelOperationProvider implements
		CLIOperationProviderSpi
{
	private static final Class<?>[] BASE_OPERATIONS = new Class<?>[] {
		GeowaveTopLevelSection.class,
		ExplainCommand.class,
		HelpCommand.class
	};

	@Override
	public Class<?>[] getOperations() {
		return BASE_OPERATIONS;
	}
}
