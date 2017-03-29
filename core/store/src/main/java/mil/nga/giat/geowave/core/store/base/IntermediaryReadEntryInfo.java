package mil.nga.giat.geowave.core.store.base;


import mil.nga.giat.geowave.core.index.ByteArrayId;
import mil.nga.giat.geowave.core.store.adapter.AdapterStore;
import mil.nga.giat.geowave.core.store.adapter.DataAdapter;
import mil.nga.giat.geowave.core.store.data.PersistentDataset;
import mil.nga.giat.geowave.core.store.index.CommonIndexValue;
import mil.nga.giat.geowave.core.store.index.PrimaryIndex;

public class IntermediaryReadEntryInfo<T>
{
	private final PersistentDataset<CommonIndexValue> indexData = new PersistentDataset<CommonIndexValue>();
	private final PersistentDataset<Object> extendedData = new PersistentDataset<Object>();
	private final PersistentDataset<byte[]> unknownData = new PersistentDataset<byte[]>();

	private final boolean decodeRow;
	private final PrimaryIndex index;

	private DataAdapter<T> dataAdapter;
	boolean adapterVerified;

	public IntermediaryReadEntryInfo(
			PrimaryIndex index,
			boolean decodeRow ) {
		this.index = index;
		this.decodeRow = decodeRow;
	}

	public PrimaryIndex getIndex() {
		return index;
	}

	public boolean isDecodeRow() {
		return decodeRow;
	}

	// Adapter is set either by the user or from the data
	// If null, expect it from data, so no verify needed
	public boolean setDataAdapter(
			DataAdapter<T> dataAdapter,
			boolean fromData ) {
		this.dataAdapter = dataAdapter;
		this.adapterVerified = fromData ? true : (dataAdapter == null);
		return hasDataAdapter();
	}

	public boolean verifyAdapter(
			ByteArrayId adapterId ) {
		if (this.dataAdapter == null || adapterId == null) {
			return false;
		}

		this.adapterVerified = adapterId.equals(dataAdapter.getAdapterId());

		return this.adapterVerified;
	}

	public boolean setOrRetrieveAdapter(
			DataAdapter<T> adapter,
			ByteArrayId adapterId,
			AdapterStore adapterStore ) {
		// Verify the current data adapter
		if (setDataAdapter(
				adapter,
				false)) {
			return true;
		}

		// Can't retrieve an adapter without the store
		if (adapterStore == null) {
			return false;
		}

		// Try to retrieve the adapter from the store
		if (setDataAdapter(
				(DataAdapter<T>) adapterStore.getAdapter(adapterId),
				true)) {
			return true;
		}

		// No adapter set or retrieved
		return false;
	}

	public boolean isAdapterVerified() {
		return this.adapterVerified;
	}

	public boolean hasDataAdapter() {
		return this.dataAdapter != null;
	}

	public DataAdapter<T> getDataAdapter() {
		return dataAdapter;
	}

	public ByteArrayId getAdapterId() {
		if (dataAdapter != null) {
			return dataAdapter.getAdapterId();
		}

		return null;
	}

	public PersistentDataset<CommonIndexValue> getIndexData() {
		return indexData;
	}

	public PersistentDataset<Object> getExtendedData() {
		return extendedData;
	}

	public PersistentDataset<byte[]> getUnknownData() {
		return unknownData;
	}
}