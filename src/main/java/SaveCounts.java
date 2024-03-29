import ij.*;
import ij.plugin.filter.*;
import ij.io.Opener;
import ij.WindowManager;
import ij.gui.Overlay;
import ij.gui.WaitForUserDialog;
import ij.measure.ResultsTable;
import ij.text.TextWindow;
import java.io.File;

public class SaveCounts {
	WaitForUserDialog tmpDialog;
	Opener opener = new Opener();
	Analyzer analyzer = new Analyzer();
	ResultsTable tmpTable;
	public void save() {
		ImagePlus currentImp = WindowManager.getCurrentImage();
		Overlay ol = currentImp.getOverlay();
		
		int size = ol.size();
		//if there is an inclusion region, size will be one less
		for (int i = 0; i < ol.size(); i++) {
			if (ol.get(i).getProperty("inclusionRegion") != null) {
				size = size -1;
			}
		}
		
		
		double[] xpoints = new double[size];
		double[] ypoints = new double[size];
		int[] zpoints = new int[size];
		String[] names = new String[size];
		int counter = -1;
		for (int i = 0; i < ol.size(); i++) {
			if (ol.get(i).getProperty("inclusionRegion") != null) {
				continue;
			}
			counter++;
			xpoints[counter] = ol.get(i).getXBase() + (ol.get(i).getFloatWidth() / 2);
			ypoints[counter] = ol.get(i).getYBase() + (ol.get(i).getFloatHeight() / 2);
			//System.out.println(ol.get(i).getProperty("customZ"));
			zpoints[counter] = Integer.parseInt(ol.get(i).getProperty("customZ"));
			names[counter] = ol.get(i).getName();
		}
		String rootPath = currentImp.getOriginalFileInfo().directory;
		String imageFileName = currentImp.getOriginalFileInfo().fileName;
		//String imageFileNameExtensionless = imageFileName.substring(0, imageFileName.lastIndexOf('.'));
		File cellDataFile = new File(rootPath + "/Counts/Celldata/" + imageFileName + ".csv");

		// Checking file is open or not
		if (cellDataFile.exists()) {
			File sameFileName = new File(rootPath + "/Counts/Celldata/" + imageFileName + ".csv");
			if (!cellDataFile.renameTo(sameFileName)) {
				// if the file didnt accept the renaming operation it means file
				// is open
				CocUserInterface.showMessageCustom("Image data file is open.Please close it first");
				return;
			}
		}
		cellDataFile.delete();
		// Create celldata folder if not exists wih file.
		cellDataFile.getParentFile().mkdirs(); 
		tmpTable = new ResultsTable();
		tmpTable.reset();
		tmpTable.incrementCounter();
		tmpTable.showRowNumbers(false);

		tmpTable.addValue("X-centre values", xpoints[0]);
		tmpTable.addValue("Y-centre values", ypoints[0]);
		tmpTable.addValue("Z-slice position", zpoints[0]);
		tmpTable.addValue("Categories", Integer.parseInt(names[0].replaceAll(",", "")));
		for (int row = 1; row < xpoints.length; row++) {
			tmpTable.incrementCounter();
			tmpTable.addValue("X-centre values", xpoints[row]);
			tmpTable.addValue("Y-centre values", ypoints[row]);
			tmpTable.addValue("Z-slice position", zpoints[row]);
			tmpTable.addValue("Categories", Integer.parseInt(names[row].replaceAll(",", "")));
		}
		tmpTable.save(cellDataFile.getPath());
		tmpTable = null;
		// Close result window if open
		TextWindow tmp = ResultsTable.getResultsWindow();
		if (tmp != null)
			tmp.close(false);
		CocUserInterface.showMessageCustom("Data saved to file: /Counts/Celldata/" + imageFileName + ".csv");
		CocUserInterface.saveAndDisplayEvent(currentImp, "Data saved to file: " + rootPath +  " Counts/Celldata/" + imageFileName + ".csv");
		//Restore inclusion region
		System.gc();
	}
	

}