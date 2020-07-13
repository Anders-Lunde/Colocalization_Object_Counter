import ij.*;
import ij.plugin.filter.*;
import ij.io.Opener;
import ij.WindowManager;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.text.TextWindow;
import java.io.File;

public class SaveScalingInfo {
	/*
	 * This plugin saves scaling and some other info to a csv file in the counts
	 * subfolder. Each file is associated with its own scaling info on separate
	 * rows.
	 */
	static Opener opener = new Opener();
	Analyzer analyzer = new Analyzer();
	static ResultsTable tmpTable;
	static public void save() {
		ImagePlus currentImp = WindowManager.getCurrentImage();
		String rootPath = currentImp.getOriginalFileInfo().directory;
		String imageFileName = currentImp.getOriginalFileInfo().fileName;
		// String imageFileNameExtensionless = imageFileName.substring(0,
		// imageFileName.lastIndexOf('.'));
		Calibration cal = currentImp.getCalibration();
		File calibrationFile = new File(rootPath + "/Counts/Calibration.csv");
		// Create counts folder if not exists.
		calibrationFile.getParentFile().mkdirs(); 
		// Save as csv
		// Check if csv file already exist. If not, create one with origin/angle
		// values.
		if (!calibrationFile.exists()) {
			tmpTable = new ResultsTable();
			tmpTable.reset();
			tmpTable.showRowNumbers(false);
			tmpTable.incrementCounter();
			tmpTable.addValue("imageFileName", imageFileName);
			tmpTable.addValue("rootPath", rootPath);
			tmpTable.addValue("calibrated", String.valueOf(cal.calibrated()));
			tmpTable.addValue("scaled", String.valueOf(cal.scaled()));
			tmpTable.addValue("timeUnit", cal.getTimeUnit());
			tmpTable.addValue("unit", cal.getUnit());
			tmpTable.addValue("units", cal.getUnits());
			tmpTable.addValue("valueUnits", cal.getValueUnit());
			tmpTable.addValue("xValue", cal.getX(1));
			tmpTable.addValue("yValue", cal.getY(1));
			tmpTable.addValue("zValue", cal.getZ(1));
			tmpTable.addValue("xUnit", cal.getXUnit());
			tmpTable.addValue("yUnit", cal.getYUnit());
			tmpTable.addValue("zUnit", cal.getZUnit());
			tmpTable.addValue("bitDepth", currentImp.getBitDepth());
			tmpTable.save(calibrationFile.getPath());
			tmpTable = null;
		} else { // Csv file exists. Update old, or add new entry to the csv file.
			Opener.openResultsTable(calibrationFile.getPath());
			tmpTable = Analyzer.getResultsTable();
			tmpTable.showRowNumbers(false);
			boolean updatedRow = false;
			for (int row = 0; row < tmpTable.size(); row++) {
				String tmp = tmpTable.getStringValue("imageFileName", row);
				// Entry exists. Update with new values.
				if (tmp.equals(imageFileName)) { 
					tmpTable.setValue("imageFileName", row, imageFileName);
					tmpTable.setValue("rootPath", row, rootPath);
					tmpTable.setValue("calibrated", row, String.valueOf(cal.calibrated()));
					tmpTable.setValue("scaled", row, String.valueOf(cal.scaled()));
					tmpTable.setValue("timeUnit", row, cal.getTimeUnit());
					tmpTable.setValue("unit", row, cal.getUnit());
					tmpTable.setValue("units", row, cal.getUnits());
					tmpTable.setValue("valueUnits", row, cal.getValueUnit());
					tmpTable.setValue("xValue", row, cal.getX(1));
					tmpTable.setValue("yValue", row, cal.getY(1));
					tmpTable.setValue("zValue", row, cal.getZ(1));
					tmpTable.setValue("xUnit", row, cal.getXUnit());
					tmpTable.setValue("yUnit", row, cal.getYUnit());
					tmpTable.setValue("zUnit", row, cal.getZUnit());
					tmpTable.setValue("bitDepth", row, currentImp.getBitDepth());
					updatedRow = true;
				}
			}
			if (!updatedRow) { // Entry does not exists. Add new values.
				tmpTable.incrementCounter();
				tmpTable.addValue("imageFileName", imageFileName);
				tmpTable.addValue("rootPath", rootPath);
				tmpTable.addValue("calibrated", String.valueOf(cal.calibrated()));
				tmpTable.addValue("scaled", String.valueOf(cal.scaled()));
				tmpTable.addValue("timeUnit", cal.getTimeUnit());
				tmpTable.addValue("unit", cal.getUnit());
				tmpTable.addValue("units", cal.getUnits());
				tmpTable.addValue("valueUnits", cal.getValueUnit());
				tmpTable.addValue("xValue", cal.getX(1));
				tmpTable.addValue("yValue", cal.getY(1));
				tmpTable.addValue("zValue", cal.getZ(1));
				tmpTable.addValue("xUnit", cal.getXUnit());
				tmpTable.addValue("yUnit", cal.getYUnit());
				tmpTable.addValue("zUnit", cal.getZUnit());
				tmpTable.addValue("bitDepth", currentImp.getBitDepth());
				tmpTable.save(calibrationFile.getPath());
			}
			tmpTable.save(calibrationFile.getPath());
			tmpTable = null;
		}
		// Close result window if open
		TextWindow tmp = ResultsTable.getResultsWindow();
		if (tmp != null)
			tmp.close(false);
		System.gc();
	}
}
