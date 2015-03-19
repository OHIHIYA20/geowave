package mil.nga.giat.geowave.analytics.kmeans.runners;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import mil.nga.giat.geowave.analytics.clustering.CentroidManager;
import mil.nga.giat.geowave.analytics.clustering.LongCentroid;
import mil.nga.giat.geowave.analytics.kmeans.mapreduce.runners.StripWeakCentroidsRunner;
import mil.nga.giat.geowave.analytics.kmeans.mapreduce.runners.StripWeakCentroidsRunner.MaxChangeBreakStrategy;
import mil.nga.giat.geowave.analytics.kmeans.mapreduce.runners.StripWeakCentroidsRunner.StableChangeBreakStrategy;
import mil.nga.giat.geowave.analytics.tools.AnalyticItemWrapper;
import mil.nga.giat.geowave.analytics.tools.PropertyManagement;
import mil.nga.giat.geowave.index.ByteArrayId;
import mil.nga.giat.geowave.index.StringUtils;
import mil.nga.giat.geowave.store.CloseableIterator;
import mil.nga.giat.geowave.store.index.IndexType;

import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

public class StripWeakCentroidsRunnerTest
{
	@Test
	public void testStable()
			throws Exception {
		final StripWeakCentroidsRunnerForTest testObj = new StripWeakCentroidsRunnerForTest(
				60,
				62);
		testObj.setBreakStrategy(new StableChangeBreakStrategy<Long>());
		testObj.run(
				new Configuration(),
				new PropertyManagement());
	}

	@Test
	public void testStable1()
			throws Exception {

		List<AnalyticItemWrapper<Long>> list = new ArrayList<AnalyticItemWrapper<Long>>();
		int cnts[] = new int[] {
			1000,
			851,
			750,
			650,
			525,
			200,
			100,
			90,
			70
		};
		for (int i = 0; i < cnts.length; i++)
			list.add(new LongCentroid(
					i,
					"",
					cnts[i]));
		StableChangeBreakStrategy<Long> breakS = new StableChangeBreakStrategy<Long>();
		assertEquals(
				5,
				breakS.getBreakPoint(list));
	}

	@Test
	public void testCliffMean()
			throws Exception {
		final StripWeakCentroidsRunnerForTest testObj = new StripWeakCentroidsRunnerForTest(
				79,
				81);
		testObj.setBreakStrategy(new MaxChangeBreakStrategy<Long>());
		testObj.run(
				new Configuration(),
				new PropertyManagement());
	}

	@Test
	public void testCliff()
			throws Exception {
		final StripWeakCentroidsRunnerForTestOne testObj = new StripWeakCentroidsRunnerForTestOne();
		testObj.run(
				new Configuration(),
				new PropertyManagement());
	}

	private static class StripWeakCentroidsRunnerForTest extends
			StripWeakCentroidsRunner<Long>
	{
		private final List<AnalyticItemWrapper<Long>> testSet;
		private final int min;
		private final int max;

		StripWeakCentroidsRunnerForTest(
				int min,
				int max ) {
			super();
			this.min = min;
			this.max = max;
			testSet = load();
		}

		protected CentroidManager<Long> constructCentroidManager(
				final Configuration config,
				final PropertyManagement runTimeProperties )
				throws IOException {
			return new CentroidManager<Long>() {

				@Override
				public AnalyticItemWrapper<Long> createNextCentroid(
						final Long feature,
						final String groupID,
						final Coordinate coordinate,
						final String[] extraNames,
						final double[] extraValues ) {
					return new LongCentroid(
							feature,
							groupID,
							1);
				}

				@Override
				public void clear() {

				}

				@Override
				public void delete(
						final String[] dataIds )
						throws IOException {
					Assert.assertTrue(
							dataIds.length + "<=" + max,
							dataIds.length <= max);
					Assert.assertTrue(
							dataIds.length + ">=" + min,
							dataIds.length >= min);
				}

				@Override
				public List<String> getAllCentroidGroups()
						throws IOException {
					return Arrays.asList("1");
				}

				@Override
				public List<AnalyticItemWrapper<Long>> getCentroidsForGroup(
						final String groupID )
						throws IOException {
					Assert.assertEquals(
							"1",
							groupID);
					return testSet;
				}

				@Override
				public List<AnalyticItemWrapper<Long>> getCentroidsForGroup(
						final String batchID,
						final String groupID )
						throws IOException {
					Assert.assertEquals(
							"1",
							groupID);
					return testSet;
				}

				@Override
				public int processForAllGroups(
						final mil.nga.giat.geowave.analytics.clustering.CentroidManager.CentroidProcessingFn<Long> fn )
						throws IOException {

					return fn.processGroup(
							"1",
							testSet);

				}

				@Override
				public AnalyticItemWrapper<Long> getCentroid(
						String id ) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public ByteArrayId getDataTypeId() {
					return new ByteArrayId(
							StringUtils.stringToBinary("centroid"));
				}

				@Override
				public ByteArrayId getIndexId() {
					return new ByteArrayId(
							StringUtils.stringToBinary(IndexType.SPATIAL_VECTOR.getDefaultId()));
				}

			};
		}

		private List<AnalyticItemWrapper<Long>> load() {
			final Random rand = new Random(
					2331);
			int begin = 100000000;
			final List<AnalyticItemWrapper<Long>> centroids = new ArrayList<AnalyticItemWrapper<Long>>();
			for (int i = 0; i <= 100; i++) {
				if ((i > 0) && ((i % 20) == 0)) {
					begin /= (Math.pow(
							100,
							i / 20));
				}
				centroids.add(new LongCentroid(
						i,
						"",
						(int) (Math.abs(rand.nextDouble() * 10000) + begin)));
			}
			return centroids;
		}
	}

	private static class StripWeakCentroidsRunnerForTestOne extends
			StripWeakCentroidsRunner<Long>
	{

		private final List<AnalyticItemWrapper<Long>> testSet = Arrays.asList((AnalyticItemWrapper<Long>) new LongCentroid(
				1L,
				"",
				22));

		StripWeakCentroidsRunnerForTestOne() {
			super();
		}

		protected CentroidManager<Long> constructCentroidManager(
				final Configuration config,
				final PropertyManagement runTimeProperties )
				throws IOException {
			return new CentroidManager<Long>() {

				@Override
				public AnalyticItemWrapper<Long> createNextCentroid(
						final Long feature,
						final String groupID,
						final Coordinate coordinate,
						final String[] extraNames,
						final double[] extraValues ) {
					return new LongCentroid(
							feature,
							groupID,
							1);
				}

				@Override
				public void clear() {

				}

				@Override
				public void delete(
						final String[] dataIds )
						throws IOException {
					Assert.assertFalse(true);
				}

				@Override
				public List<String> getAllCentroidGroups()
						throws IOException {
					return Arrays.asList("1");
				}

				@Override
				public List<AnalyticItemWrapper<Long>> getCentroidsForGroup(
						final String groupID )
						throws IOException {
					Assert.assertEquals(
							"1",
							groupID);
					return testSet;
				}

				@Override
				public List<AnalyticItemWrapper<Long>> getCentroidsForGroup(
						final String batchID,
						final String groupID )
						throws IOException {
					Assert.assertEquals(
							"1",
							groupID);
					return testSet;
				}

				@Override
				public int processForAllGroups(
						final mil.nga.giat.geowave.analytics.clustering.CentroidManager.CentroidProcessingFn<Long> fn )
						throws IOException {

					return fn.processGroup(
							"1",
							testSet);

				}

				@Override
				public AnalyticItemWrapper<Long> getCentroid(
						String id ) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public ByteArrayId getDataTypeId() {
					return new ByteArrayId(
							StringUtils.stringToBinary("centroid"));
				}

				@Override
				public ByteArrayId getIndexId() {
					return new ByteArrayId(
							StringUtils.stringToBinary(IndexType.SPATIAL_VECTOR.getDefaultId()));
				}

			};
		}

	}

}