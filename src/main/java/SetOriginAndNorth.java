import ij.*;
import ij.gui.*;
import ij.io.Opener;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import java.io.File;
import ij.gui.Toolbar;
import ij.text.TextWindow;
import ij.gui.WaitForUserDialog;
import java.awt.Polygon;
import ij.gui.Roi;

public class SetOriginAndNorth {
	/*
	 * This gets a user defined origin and angle (north) of the current image, by
	 * asking the user to draw a line-segment. The values are saved to a csv
	 * file (origin and North.csv) in the counts folder (where the image was
	 * loaded from). Multiple rows in the csv file hold info for mulitple
	 * images, as long as they are all in the same folder. Columns in the csv
	 * file are "filename", "originX", "originY", and "originAngle".
	 * 
	 * These values are meant to be imported to excel and used to translate
	 * overlays/cells/coordinates/counts form many different images to a 
	 * common origin and aligned according to the angles.
	 */
	Opener opener = new Opener();
	Analyzer analyzer = new Analyzer();
	Toolbar toolbar = new Toolbar();
	WaitForUserDialog tmpDialog;
	public void RUN(String arg) {
		// These variables should be checked every time plugin runs, since user
		// might have changed the current image.
		ImagePlus currentImp = WindowManager.getCurrentImage();
		String rootPath = currentImp.getOriginalFileInfo().directory;
		String imageFileName = currentImp.getOriginalFileInfo().fileName;
		File originFile = new File(rootPath + "/Counts/Origin and North.csv");
		// Ask the user to draw origin and angle with the line tool.
		IJ.setTool("line"); // A bug prevents the gui from being updated(?)
		new ClearCounts_LoadCounts_GenericAlert(
				"Please draw a line, with starting point indicating origin,\nand angle the line towards a common reference point visible in all images (north).\nChose an origin which is visible or locatable in all images.\nIt is not important where you end the line, only the origin and the angle of the line matters.\n\nClick *OK* to save the line coordinates to the /counts folder (in subfolder where images are).",
				3).show();
		Roi roi = currentImp.getRoi();
		if (roi == null || !roi.isLine()) {
			IJ.showMessage("ERROR: No line segment was drawn. Select the line segment tool and try again.");
			return;
		}
		//Subtract 90 to make up in the image north (imageJ gives angle from horizontal line)
		double originAngle = roi.getAngle()-90; 
		// Cast to line object. In pixel values
		Polygon points = ((Line) roi).getPoints(); 
		double originX = points.xpoints[0];
		double originY = points.ypoints[0];
		// Check if csv file already exist. If not, create one with origin/angle
		// values.
		if (!originFile.exists()) {
			// Create counts folder if not exists.
			originFile.getParentFile().mkdirs(); 
			ResultsTable tmpTable = new ResultsTable();
			tmpTable.reset();
			tmpTable.showRowNumbers(false);
			tmpTable.incrementCounter();
			tmpTable.addValue("filename", imageFileName);
			tmpTable.addValue("originX", originX);
			tmpTable.addValue("originY", originY);
			tmpTable.addValue("originAngle", originAngle);
			tmpTable.save(originFile.getPath());
			tmpTable = null;
		} else { // Csv file exists. Update old, or add new entry to the csv file.
			Opener.openResultsTable(originFile.getPath());
			ResultsTable tmpTable = Analyzer.getResultsTable();
			tmpTable.showRowNumbers(false);
			boolean updatedRow = false;
			for (int row = 0; row < tmpTable.size(); row++) {
				String tmp = tmpTable.getStringValue("filename", row);
				if (tmp.equals(imageFileName)) { // Entry exists. Update with new values.
					tmpTable.setValue("originX", row, originX);
					tmpTable.setValue("originY", row, originY);
					tmpTable.setValue("originAngle", row, originAngle);
					updatedRow = true;
				}
			}
			if (!updatedRow) { // Entry does not exists. Add new values.
				tmpTable.incrementCounter();
				tmpTable.addValue("filename", imageFileName);
				tmpTable.addValue("originX", originX);
				tmpTable.addValue("originY", originY);
				tmpTable.addValue("originAngle", originAngle);
			}
			tmpTable.save(originFile.getPath());
			tmpTable = null;
		}
		// Close result window if open
		TextWindow tmp = ResultsTable.getResultsWindow();
		if (tmp != null)
			tmp.close(false);
		IJ.showMessage("Conformation", "Data saved to file: /Counts/Origin and North.csv");
		System.gc();
	}
}
