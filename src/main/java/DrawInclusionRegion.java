import ij.*;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import java.io.File;
import ij.gui.Toolbar;
import ij.text.TextWindow;
import ij.gui.WaitForUserDialog;
import java.awt.Polygon;

import ij.gui.Overlay;
import ij.gui.Roi;

public class DrawInclusionRegion {
	/*
	 * This gets a user defined coordinate list o
	 * by asking the user to draw a roi (freehand or
	 * polygon). The values are saved to a csv file (imagefilename_region.csv)
	 * in the /counts/regions folder (where the image was loaded from). Columns
	 * in the csv file are "contoursX", and "contoursY".
	 * 
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
		File regionFile = new File(rootPath + "/Counts/Regions/" + imageFileName + ".regions.csv");

		// Ask the user to draw the region using the freehand or polygon tool.
		IJ.setTool("freehand");

		IJ.showMessage(
				"Please draw a region in the image that will define where objects of interest are. Use the freehand, polygon, oval or rectangle tool.\n\nClick *OK* to save the coordinates to the /counts/regions folder (in subfolders where images are)");

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
		regionFile.delete();
		// Save as csv
		// Create /counts/regions folder if not exists.
		regionFile.getParentFile().mkdirs(); 
		ResultsTable tmpTable = new ResultsTable();
		tmpTable.reset();
		tmpTable.showRowNumbers(false);
		for (int row = 0; row < xpoints.length; row++) {
			tmpTable.incrementCounter();
			tmpTable.addValue("regionsX", xpoints[row]);
			tmpTable.addValue("regionsY", ypoints[row]);
		}
		tmpTable.save(regionFile.getPath());
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
			IJ.showMessage("Inclusion region saved for file: " + imageFileName);
		}
		
		
		//Convert to overlay, and give a special name property
		if (WindowManager.getCurrentImage().getOverlay() == null) {
			WindowManager.getCurrentImage().setOverlay(new Overlay());
		}
		Overlay ol = WindowManager.getCurrentImage().getOverlay();
		
		roi.setProperty("inclusionRegion", "true");
		ol.add(roi);
		ol.drawLabels(true);
		ol.drawNames(true);
		if (!(IJ.getVersion().compareTo("1.51v18") < 0)) {
			ol.selectable(false);
		}
	
		
		
		
		System.gc();
	}
}
