package org.locationtech.geowave.core.store.base;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.geowave.core.index.ByteArrayId;
import org.locationtech.geowave.core.store.DataStore;
import org.locationtech.geowave.core.store.callback.DeleteCallback;
import org.locationtech.geowave.core.store.entities.GeoWaveRow;
import org.locationtech.geowave.core.store.operations.ReaderParams;
import org.locationtech.geowave.core.store.query.DataIdQuery;
import org.locationtech.geowave.core.store.query.InsertionIdQuery;
import org.locationtech.geowave.core.store.query.Query;
import org.locationtech.geowave.core.store.query.QueryOptions;

public class DeleteDuplicateCallback<T> implements
		DeleteCallback<T, GeoWaveRow>,
		Closeable
{
	public DataStore dataStore;
	public QueryOptions queryOptions;
	public Query query;
	private Map<ByteArrayId, Short> idMap;
	private List<ByteArrayId> dupRowList;

	private List<InsertionIdData> duplicateInsertionIds;
	private Map<ByteArrayId, Integer> dupCountMap;

	public DeleteDuplicateCallback(
			final DataStore store,
			final QueryOptions options ) {
		dupCountMap = new HashMap<>();
		dupRowList = new ArrayList<>();
		dataStore = store;
		queryOptions = options;
		duplicateInsertionIds = new ArrayList<InsertionIdData>();
	}

	@Override
	public void close()
			throws IOException {

		/*
		 * dataStore.delete( queryOptions, new DataIdQuery( dupRowList));
		 */
		for (int i = 0; i < duplicateInsertionIds.size(); i++) {
			InsertionIdData insertionId = duplicateInsertionIds.get(i);
			dataStore.delete(
					queryOptions,
					new InsertionIdQuery(
							insertionId.partitionKey,
							insertionId.sortKey,
							insertionId.dataId));
		}
	}

	@Override
	public void entryDeleted(
			T entry,
			GeoWaveRow... rows ) {
		for (GeoWaveRow row : rows) {
			final int rowDups = row.getNumberOfDuplicates();

			if (rowDups > 0) {
				final ByteArrayId dataId = new ByteArrayId(
						row.getDataId());

				InsertionIdData insertionId = new InsertionIdData(
						row.getPartitionKey(),
						row.getSortKey(),
						row.getDataId());

				if (!duplicateInsertionIds.contains(insertionId)) duplicateInsertionIds.add(insertionId);

				if (!dupRowList.contains(dataId)) dupRowList.add(dataId);

				final Integer mapDups = dupCountMap.get(dataId);

				if (mapDups == null) {
					dupCountMap.put(
							dataId,
							rowDups);
				}
				else if (mapDups == 1) {
					dupCountMap.remove(dataId);
				}
				else {
					dupCountMap.put(
							dataId,
							mapDups - 1);
				}
			}
		}
	}

	private class InsertionIdData
	{
		public final ByteArrayId partitionKey;
		public final ByteArrayId sortKey;
		public final ByteArrayId dataId;

		public InsertionIdData(
				final byte[] partitionKey,
				final byte[] sortKey,
				final byte[] dataId ) {
			this.partitionKey = new ByteArrayId(
					partitionKey);
			this.sortKey = new ByteArrayId(
					sortKey);
			this.dataId = new ByteArrayId(
					dataId);
		}
	}
}
