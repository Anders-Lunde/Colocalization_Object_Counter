import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class SessionSummaryExporterTest {
	@Test
	public void summaryCountsObjectsByCategoryCombinationAndIndividualCategory() {
		SessionSummaryExporter.Summary summary = SessionSummaryExporter.createSummary(
				"image one.tif",
				"2026-06-01 10:45:00",
				Arrays.asList(
						new SessionSummaryExporter.ObjectRecord("2,1"),
						new SessionSummaryExporter.ObjectRecord("1,2"),
						new SessionSummaryExporter.ObjectRecord("3")),
				true,
				false,
				true);

		assertEquals(3, summary.totalObjects);
		assertEquals(Integer.valueOf(2), summary.categoryCombinationCounts.get("12"));
		assertEquals(Integer.valueOf(1), summary.categoryCombinationCounts.get("3"));
		assertEquals(Integer.valueOf(2), summary.individualCategoryCounts.get("1"));
		assertEquals(Integer.valueOf(2), summary.individualCategoryCounts.get("2"));
		assertEquals(Integer.valueOf(1), summary.individualCategoryCounts.get("3"));
	}

	@Test
	public void csvIncludesMetadataAndCounts() {
		SessionSummaryExporter.Summary summary = SessionSummaryExporter.createSummary(
				"image,one.tif",
				"2026-06-01 10:45:00",
				Arrays.asList(
						new SessionSummaryExporter.ObjectRecord("1,2"),
						new SessionSummaryExporter.ObjectRecord("3")),
				true,
				false,
				true);

		String csv = SessionSummaryExporter.toCsv(summary);

		assertTrue(csv.contains("Image filename,\"image,one.tif\""));
		assertTrue(csv.contains("Generated at,2026-06-01 10:45:00"));
		assertTrue(csv.contains("Total objects,2"));
		assertTrue(csv.contains("Saved inclusion region,true"));
		assertTrue(csv.contains("Saved contours,false"));
		assertTrue(csv.contains("Saved origin and north,true"));
		assertTrue(csv.contains("Individual category counts"));
		assertTrue(csv.contains("Category 1,1"));
		assertTrue(csv.contains("Category 2,1"));
		assertTrue(csv.contains("Category 3,1"));
		assertTrue(csv.contains("Category combination counts"));
		assertTrue(csv.contains("12,1"));
		assertTrue(csv.contains("3,1"));
	}
}
