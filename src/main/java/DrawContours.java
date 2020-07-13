import ij.*;
import ij.WindowManager;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import java.io.File;
import ij.gui.Toolbar;
import ij.text.TextWindow;
import ij.gui.WaitForUserDialog;
import java.awt.Polygon;
import ij.gui.Roi;

public class DrawContours {
	/*
	 * This gets a user defined coordinate list of the image/tissue section
	 * contours/outlines, by asking the user to draw a roi (freehand or
	 * polygon). The values are saved to a csv file (imagefilename_contours.csv)
	 * in the /counts/contours folder (where the image was loaded from). Columns
	 * in the csv file are "contoursX", and "contoursY".
	 * 
	 * These values are meant to be imported to excel, translated according to
	 * setOriginAndNorth values, and exported again for visualization in e.g.
	 * matlab (together with cells/counts).
	 */

	Analyzer analyzer = new Analyzer();
	Toolbar toolbar = new Toolbar();
	WaitForUserDialog tmpDialog;

	public void RUN(String arg) {

		// These variables should be checked every time plugin runs, since user
		// might have changed the current image.
		ImagePlus currentImp = WindowManager.getCurrentImage();
		String rootPath = currentImp.getOriginalFileInfo().directory;
		String imageFileName = currentImp.getOriginalFileInfo().fileName;
		File contourFile = new File(rootPath + "/Counts/Contours/" + imageFileName + ".contours.csv");

		// Ask the user to draw the contour using the freehand or polygon tool.
		IJ.setTool("freehand");

		IJ.showMessage(
				"Please draw a contour around the image, indicating edge of tissue or other feature you wish to visualize. Use the freehand, polygon, oval or rectangle tool.\n\nClick *OK* to save the coordinates to the /counts/contours folder (in subfolders where images are)");

		Roi roi = currentImp.getRoi();

		// Check if roi is of correct type or not drawn.
		if (roi == null) {
			IJ.showMessage("ERROR: No usable roi drawn. Please try again.");
			return;
		}
		String roiType = roi.getTypeAsString();
		if (!roiType.equals("Freehand") && !roiType.equals("Polygon") && !roiType.equals("Rectangle")
				&& !roiType.equals("Oval")) {
			IJ.showMessage("ERROR: No usable roi drawn/active. Please try again.");
			return;
		}

		// Get coordinates
		// Cast to line object. In pixel values
		Polygon points = roi.getPolygon(); 
		int[] xpoints = points.xpoints;
		int[] ypoints = points.ypoints;
		// Delete old file if exists:
		contourFile.delete();
		// Save as csv
		// Create /counts/contours folder if not exists.
		contourFile.getParentFile().mkdirs(); 
		ResultsTable tmpTable = new ResultsTable();
		tmpTable.reset();
		tmpTable.showRowNumbers(false);
		for (int row = 0; row < xpoints.length; row++) {
			tmpTable.incrementCounter();
			tmpTable.addValue("contoursX", xpoints[row]);
			tmpTable.addValue("contoursY", ypoints[row]);
		}
		tmpTable.save(contourFile.getPath());
		tmpTable = null;
		// If there's no celldata for this image, add an entry with 0 counts
		File cellDataFile = new File(rootPath + "/Counts/Celldata/" + imageFileName + ".csv");
		if (!cellDataFile.exists()) {
			cellDataFile.getParentFile().mkdirs();
			IJ.saveString("X-centre values,Y-centre values,Categories", cellDataFile.getAbsolutePath());
		}
		// Save scaling info
		SaveScalingInfo.save();
		// Close result window if open
		TextWindow tmp = ResultsTable.getResultsWindow();
		if (tmp != null)
			tmp.close(false);
		{
			IJ.showMessage("Values saved for file: " + imageFileName);
		}
		System.gc();
	}
}
