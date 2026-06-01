import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class SessionSummaryExporter {
	private SessionSummaryExporter() {
	}

	public static final class ObjectRecord {
		public final String categories;

		public ObjectRecord(String categories) {
			this.categories = categories;
		}
	}

	public static final class Summary {
		public final String imageFileName;
		public final String generatedAt;
		public final int totalObjects;
		public final Map<String, Integer> categoryCombinationCounts;
		public final Map<String, Integer> individualCategoryCounts;
		public final boolean hasInclusionRegion;
		public final boolean hasContours;
		public final boolean hasOriginAndNorth;

		private Summary(String imageFileName, String generatedAt, int totalObjects,
				Map<String, Integer> categoryCombinationCounts,
				Map<String, Integer> individualCategoryCounts,
				boolean hasInclusionRegion,
				boolean hasContours,
				boolean hasOriginAndNorth) {
			this.imageFileName = imageFileName;
			this.generatedAt = generatedAt;
			this.totalObjects = totalObjects;
			this.categoryCombinationCounts = categoryCombinationCounts;
			this.individualCategoryCounts = individualCategoryCounts;
			this.hasInclusionRegion = hasInclusionRegion;
			this.hasContours = hasContours;
			this.hasOriginAndNorth = hasOriginAndNorth;
		}
	}

	public static Summary createSummary(String imageFileName, String generatedAt, List<ObjectRecord> records,
			boolean hasInclusionRegion, boolean hasContours, boolean hasOriginAndNorth) {
		Map<String, Integer> combinationCounts = new TreeMap<String, Integer>();
		Map<String, Integer> individualCounts = new TreeMap<String, Integer>();

		for (ObjectRecord record : records) {
			String normalizedCategories = normalizeCategories(record.categories);
			if (normalizedCategories.length() == 0) {
				continue;
			}
			increment(combinationCounts, normalizedCategories);
			for (int i = 0; i < normalizedCategories.length(); i++) {
				increment(individualCounts, Character.toString(normalizedCategories.charAt(i)));
			}
		}

		return new Summary(imageFileName, generatedAt, records.size(), combinationCounts, individualCounts,
				hasInclusionRegion, hasContours, hasOriginAndNorth);
	}

	public static String toCsv(Summary summary) {
		StringBuilder csv = new StringBuilder();
		appendRow(csv, "Image filename", summary.imageFileName);
		appendRow(csv, "Generated at", summary.generatedAt);
		appendRow(csv, "Total objects", Integer.toString(summary.totalObjects));
		appendRow(csv, "Saved inclusion region", Boolean.toString(summary.hasInclusionRegion));
		appendRow(csv, "Saved contours", Boolean.toString(summary.hasContours));
		appendRow(csv, "Saved origin and north", Boolean.toString(summary.hasOriginAndNorth));
		csv.append(System.lineSeparator());

		csv.append("Individual category counts").append(System.lineSeparator());
		appendRow(csv, "Category", "Count");
		for (Map.Entry<String, Integer> entry : summary.individualCategoryCounts.entrySet()) {
			appendRow(csv, "Category " + entry.getKey(), Integer.toString(entry.getValue()));
		}
		csv.append(System.lineSeparator());

		csv.append("Category combination counts").append(System.lineSeparator());
		appendRow(csv, "Category combination", "Count");
		for (Map.Entry<String, Integer> entry : summary.categoryCombinationCounts.entrySet()) {
			appendRow(csv, entry.getKey(), Integer.toString(entry.getValue()));
		}
		return csv.toString();
	}

	public static void writeCsv(File outputFile, Summary summary) throws IOException {
		outputFile.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(outputFile);
		try {
			writer.write(toCsv(summary));
		} finally {
			writer.close();
		}
	}

	private static String normalizeCategories(String categories) {
		if (categories == null) {
			return "";
		}
		String digitsOnly = categories.replaceAll("[^1-8]", "");
		char[] chars = digitsOnly.toCharArray();
		java.util.Arrays.sort(chars);
		return new String(chars);
	}

	private static void increment(Map<String, Integer> counts, String key) {
		Integer currentCount = counts.get(key);
		counts.put(key, currentCount == null ? 1 : currentCount + 1);
	}

	private static void appendRow(StringBuilder csv, String firstColumn, String secondColumn) {
		csv.append(escape(firstColumn)).append(",").append(escape(secondColumn)).append(System.lineSeparator());
	}

	private static String escape(String value) {
		if (value == null) {
			return "";
		}
		if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
			return "\"" + value.replace("\"", "\"\"") + "\"";
		}
		return value;
	}
}
