import java.awt.List;
import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.ChannelSplitter;
import ij.plugin.Duplicator;
import ij.plugin.ImageCalculator;
import ij.process.ImageProcessor;

public class AutoDetect {
	//Values from the options/settings panel
	String detectColor;
	String detectInsideColor;
	int blurRadius;
	String noiseTolerance;
	boolean excludeOnEdges, lightBackground, ignoreTopSlice, showOutput;
	String radiusxy, radiusz;
	boolean error;
	
	//Other values
	ImagePlus currentImp, currentImp_duplicate, currentImp_duplicate_filtered;
	boolean doStack;
	
	Duplicator duplicator = new Duplicator();
	ImageCalculator ic = new ImageCalculator();

	
	
	
	public AutoDetect() {
		this.error = false;
		if (WindowManager.getCurrentImage() == null) {
			IJ.showMessage("No image open.");
			this.error = true;
			return;
		}
		if (WindowManager.getWindow("Filtered output") != null) { //output from "Show filtered output"
			WindowManager.getImage("Filtered output").close(); 
			}
		this.currentImp = WindowManager.getCurrentImage();
		
		currentImp_duplicate_filtered = null;
		currentImp_duplicate = null;
		
		
		// Get variables from input buttons/boxes
		this.detectColor = String.valueOf(UserInterface.detectColorButton.getSelectedItem());
		this.detectInsideColor = String.valueOf(UserInterface.detectInsideColorButton.getSelectedItem());
		this.blurRadius = Integer.parseInt(UserInterface.blurRadiusButton.getText());
		//double noiseTolerance = Integer.parseInt(noiseToleranceButton.getText());
		this.noiseTolerance = UserInterface.noiseToleranceButton.getText();
		this.excludeOnEdges = UserInterface.excludeOnEdgesButton.isSelected();
		this.lightBackground = UserInterface.lightBackgroundButton.isSelected();
		this.ignoreTopSlice = UserInterface.ignoreTopSlice.isSelected();
		this.showOutput = UserInterface.showOutput.isSelected();
		this.radiusxy = UserInterface.radiusxyButton.getText();
		this.radiusz = UserInterface.radiuszButton.getText();
		
	}
	
	
	public void findMaxima(String mode) {
		if (mode.equals("2d") || currentImp.getNSlices() == 1) {
			doStack = false;
		} else if (mode.equals("3d")){ 
			doStack = true;}
		
		//Get inclusion region, if there is one
		Overlay ol = WindowManager.getCurrentImage().getOverlay();
		Roi inclusionROI = null;
		boolean wasInclusionOverlay = true;
		if (ol != null) {
			for (int i = 0; i < ol.size(); i++) {
				if (ol.get(i).getProperty("inclusionRegion") != null) {
					inclusionROI = (Roi) ol.get(i).clone();
					wasInclusionOverlay = true;
				}
			}
		}
		
		
		currentImp_duplicate_filtered = applyFilterSettings();
		currentImp_duplicate_filtered.setOverlay(null);
		IJ.run(currentImp_duplicate_filtered, "Select None", "");
		if (!doStack) {
			findMaxima_2d(currentImp_duplicate_filtered, inclusionROI);
		} else {
			findMaxima_3d(currentImp_duplicate_filtered, inclusionROI);}
		
		
		
		UserInterface.setPointerApperanceSettings(currentImp);
		UserInterface.setPointerApperanceSettings(currentImp_duplicate_filtered);
		
		currentImp_duplicate_filtered.setTitle("Filtered output");
		if (!showOutput) {
			currentImp_duplicate_filtered.changes = false;
			currentImp_duplicate_filtered.close();
		} else {
			currentImp_duplicate_filtered.show();
			currentImp_duplicate_filtered.changes = false;
		} 
		System.gc();
	}
	
	public void findMaxima_3d(ImagePlus currentImp_duplicate_filtered, Roi inclusionROI) {
		IJ.run(currentImp, "Select None", "");
		if (!ij.Menus.getCommands().toString().toLowerCase().contains("3d maxima finder")) {
			IJ.showMessage("<html><b>ERROR: The 3D ImageJ Suite (MCIB3D) plugin was not found. </b><br>"
					+ "<br>"
					+ "<br>"
					+ "<b>To install from FIJI:</b><br>"
					+ "<br>"
					+ "1. Click Help>Update>Manage Update Sites<br>"
					+ "2. Activate \"3D ImageJ Suite | https://sites.imagej.net/Tboudier/ <br>"
					+ "3. Click Close>Apply changes<br>"
					+ "4. Restart FIJI<br>"
					+ "<br>"
					+ "<b>To install in ImageJ</b><br>"
					+ "<br>"
					+ "1. Download the most recent \"mcib3d-core\" AND \"mcib3d-plugins\" files from <a href=\"https://sites.imagej.net/Tboudier/plugins/mcib3d-suite/\">https://sites.imagej.net/Tboudier/plugins/mcib3d-suite/</a> <br>"
					+ "2. Put the files in the ImageJ/plugins folder <br>"
					+ "3. Rename the files to have a .jar extension<br>"
					+ "</html>");
		}
		else {
			String command = "3D Maxima Finder";
			String options = String.format("radiusxy=%s radiusz=%s noise=%s", radiusxy, radiusz, noiseTolerance);
			if (ignoreTopSlice) { //This replaces the below deprecated code
				if (currentImp_duplicate_filtered.getNSlices() > 1) {
					if (lightBackground) {
						double maxValue = currentImp_duplicate_filtered.getStack().getProcessor(1).maxValue();
						currentImp_duplicate_filtered.getStack().getProcessor(1).set(maxValue); //make z=1 white //TODO: doesnt work with 3d. 3D maxima plugin has no option for this.
					} else {
					currentImp_duplicate_filtered.getStack().getProcessor(1).set(0); //make z=1 black
					}
				}
			}
			IJ.run(currentImp_duplicate_filtered, command, options);
			WindowManager.getImage("peaks").close(); 
			ResultsTable rt = ij.plugin.filter.Analyzer.getResultsTable();
			rt.showRowNumbers(false);
			double[] xpoints = rt.getColumnAsDoubles(0);
			double[] ypoints = rt.getColumnAsDoubles(1);
			double[] zpoints = rt.getColumnAsDoubles(2);


			//DEPRECATED BELOW: Now, we delete top slice instead, to ignore top slice.
			//The below code deleted any points that landed on top slice
			
			//Remove z == 1 if "ignore top slice" is enabled
			/*
			
			double[] xpoints_beforeTopRemove = rt.getColumnAsDoubles(0);
			double[] ypoints_beforeTopRemove = rt.getColumnAsDoubles(1);
			double[] zpoints_beforeTopRemove = rt.getColumnAsDoubles(2);
			
			double[] xpoints = null;
			double[] ypoints = null; 
			double[] zpoints = null;
	
			if (zpoints_beforeTopRemove != null) {
				if (ignoreTopSlice == false) {
					xpoints = xpoints_beforeTopRemove;
					ypoints = ypoints_beforeTopRemove;
					zpoints = zpoints_beforeTopRemove;
				} else {
					//Calculate occurences of markers on top slice
					int occurrences = 0;
					for (double e : zpoints_beforeTopRemove) {
						if (e == 0) {
							occurrences = occurrences + 1;
						}
					}
					UserInterface.saveAndDisplayEvent(currentImp, "Removed/ignored " + String.valueOf(occurrences) + " maxima on top slice");
					
					//Create new arrays with new size
					int newSize = zpoints_beforeTopRemove.length - occurrences;
					xpoints = new double[newSize];
					ypoints = new double[newSize];
					zpoints = new double[newSize];
					
					//Fill new arrays, ignore top slice
					int occurrences2 = 0;
					int i = 0;
					for (double e : zpoints_beforeTopRemove) {
						if (e != 0) {
							xpoints[occurrences2] = xpoints_beforeTopRemove[i];
							ypoints[occurrences2] = ypoints_beforeTopRemove[i];
							zpoints[occurrences2] = zpoints_beforeTopRemove[i];
							occurrences2 = occurrences2 + 1;
						}
						i = i +1;
			}}}
			*/
			
			PointRoi multipoints = new PointRoi();
			PointRoi multipoints_filteredImg = new PointRoi();
			Overlay ol = currentImp.getOverlay();
			//int pointZPos = currentImp.getZ();
			String s = "";

			if (xpoints == null) {
				IJ.run(currentImp, "Select None", "");
				String eventString = "Find 3D maxima: noise=" + noiseTolerance + " detectColor=" + detectColor + " detectInsideColor=" + detectInsideColor + " blurRadius=" + blurRadius + " excludeOnEdges=" + excludeOnEdges + " lightBackground=" + lightBackground + " radiXY=" + radiusxy + " radiZ=" + radiusz + " ignoreTopSlice=" + String.valueOf(ignoreTopSlice);
				IJ.log(eventString);
				UserInterface.saveAndDisplayEvent(currentImp, eventString);
				UserInterface.saveAndDisplayEvent(currentImp, "Amount of maxima points found=0");
				roiHistory.addAllRoisDeleted(currentImp, true);
				} 
			else {
				//Add maxima to current Z in image, and print info
				for (int i=0; i<xpoints.length; i++) {
					//Ignore any point outside inclusionROI
					if (inclusionROI != null) {
						if (!inclusionROI.contains((int) xpoints[i], (int) ypoints[i])) {
							continue;
						}
					}
					
					
					int insideOverlay = -1;
					//Add rois to original image
					currentImp.setZ((int) zpoints[i]+1);
					multipoints.addUserPoint(currentImp, xpoints[i], ypoints[i]);
					//Add rois to filtered image
					currentImp_duplicate_filtered.setZ((int) zpoints[i]+1);
					multipoints_filteredImg.addUserPoint(currentImp_duplicate_filtered, xpoints[i], ypoints[i]);
					//Print info with name reflecting inside/outside previous overlays
					if (ol != null) {
						for (int j = 0; j < ol.size(); j++) {
							int olPos = ol.get(j).getZPosition();
							if (olPos == 0 || olPos == zpoints[i] + 1) {
								if (ol.get(j).contains((int) xpoints[i], (int) ypoints[i])) { //If point tied to existing overlay
									insideOverlay = j;
								}
							}
						}
					}
					if (insideOverlay != -1) { //If point tied to existing overlay
						s += String.format("Point %s   x=%f, y=%f   Inside category %s \n", i+1, xpoints[i], ypoints[i], ol.get(insideOverlay).getName());
					}
					else {
						s += String.format("Point %s   x=%s, y=%s   Outside \n", i+1,  xpoints[i], ypoints[i]);	
					}
				}
				IJ.log(s);
				currentImp.setRoi(multipoints);
				currentImp_duplicate_filtered.setRoi(multipoints_filteredImg);
				//Save event to log:
				String eventString = "Find 3D maxima: noise=" + noiseTolerance + " detectColor=" + detectColor + " detectInsideColor=" + detectInsideColor + " blurRadius=" + blurRadius + " excludeOnEdges=" + excludeOnEdges + " lightBackground=" + lightBackground + " radiXY=" + radiusxy + " radiZ=" + radiusz + " ignoreTopSlice=" + String.valueOf(ignoreTopSlice);
				IJ.log(eventString);
				UserInterface.saveAndDisplayEvent(currentImp, eventString);
				UserInterface.saveAndDisplayEvent(currentImp, "Amount of maxima points found=" + xpoints.length);
				//Update image roi info for UserInputLogger
				roiHistory.add(currentImp, (Roi) currentImp.getRoi().clone());
				roiHistory.addAllRoisDeleted(currentImp, false);
				///
				
			}		
		}
	}
	
	
	public void findMaxima_2d(ImagePlus currentImp_duplicate_filtered, Roi inclusionROI) {
		IJ.run(currentImp_duplicate_filtered, "Find Maxima...", "noise=" + noiseTolerance + " output=[Point Selection]");
		Roi maxima = currentImp_duplicate_filtered.getRoi();
		IJ.log("\\Clear");
		IJ.run(currentImp, "Select None", "");
		
		if (maxima == null) {
			//Save event to log:
			String eventString = "Find 2D maxima: noise=" + noiseTolerance + " detectColor=" + detectColor + " detectInsideColor=" + detectInsideColor + " blurRadius=" + blurRadius + " excludeOnEdges=" + excludeOnEdges + " lightBackground=" + lightBackground;
			UserInterface.saveAndDisplayEvent(currentImp, eventString);
			UserInterface.saveAndDisplayEvent(currentImp, "Amount of maxima points found=0");
			IJ.log(eventString);
			IJ.log("Amount of maxima points found=0");
		}
		
		if (maxima != null) {
			PointRoi multipoints = new PointRoi();
			PointRoi multipoints_filteredImg = new PointRoi();
			Overlay ol = currentImp.getOverlay();
			int pointZPos = currentImp.getZ();
			String s = "";
			int i = 0;
			
			//Add maxima to current Z in image, and print info
			for (Point p : maxima) {
				//Ignore any point outside inclusionROI
				if (inclusionROI != null) {
					if (!inclusionROI.contains(p.x, p.y)) {
						continue;
					}
				}
				
				i += 1;
				int insideOverlay = -1;
				//Add rois to original image
				multipoints.addUserPoint(currentImp, p.x,  p.y);
				//Add rois to filtered image
				currentImp_duplicate_filtered.setZ(currentImp.getZ());
				multipoints_filteredImg.addUserPoint(currentImp_duplicate_filtered, p.x,  p.y);			
				//Print info with name reflecting inside/outside previous overlays
				if (ol != null) {
					for (int j = 0; j < ol.size(); j++) {
						int olPos = ol.get(j).getZPosition();
						if (olPos == 0 || olPos == pointZPos) {
							if (ol.get(j).contains(p.x, p.y)) { //If point tied to existing overlay
								insideOverlay = j;
							}
						}
					}
				}
				if (insideOverlay != -1) { //If point tied to existing overlay
					s += String.format("Point %s   x=%d, y=%d   Inside category %s \n", i, p.x, p.y, ol.get(insideOverlay).getName());
				}
				else {
					s += String.format("Point %s   x=%s, y=%s   Outside \n", i,  p.x, p.y);	
				}
			}
			IJ.log(s);
			currentImp.setRoi(multipoints);
			currentImp_duplicate_filtered.setRoi(multipoints_filteredImg);
			
			//Save event to log:
			String eventString = "Find 2D maxima: noise=" + noiseTolerance + " detectColor=" + detectColor + " detectInsideColor=" + detectInsideColor + " blurRadius=" + blurRadius + " excludeOnEdges=" + excludeOnEdges + " lightBackground=" + lightBackground;
			IJ.log(eventString);
			UserInterface.saveAndDisplayEvent(currentImp, eventString);
			UserInterface.saveAndDisplayEvent(currentImp, "Amount of maxima points found=" + multipoints.size());
			//Update image roi info for UserInputLogger
			roiHistory.add(currentImp, (Roi) multipoints.clone());
			roiHistory.addAllRoisDeleted(currentImp, false);
		}
	}
	
	public ImagePlus applyFilterSettings() {
		/*
		 * This enables a simple way to automatically create multipoints 
		 * in an image based on the find maxima function, 
		 * with some options for selection what color
		 * of objects to look for. The method which restricts guessing to
		 * outlined cells expects outlines of cell images to be made in the
		 * arranger plugin. Only the currently activated channel/slice/frame
		 * is processed.
		 */
		
		currentImp_duplicate = null;

		if (!doStack) {
			// Duplicate current slice in current channel
			currentImp_duplicate = duplicator.run(currentImp, currentImp.getC(), currentImp.getC(),
					currentImp.getZ(), currentImp.getZ(), currentImp.getT(), currentImp.getT());
		}
		else { // Duplicate current channel
			currentImp_duplicate = duplicator.run(currentImp, currentImp.getC(), currentImp.getC(),
					1, currentImp.getNSlices(), currentImp.getT(), currentImp.getT());
			}


		if (!(currentImp.getBitDepth() == 24)) { // If not RGB
			detectColor = "Any";
			detectInsideColor = "Disable";
		}


		// If guess only outlined cells, erase everything from
		// currentImp_singlePlane except whats inside outline
		if (!detectInsideColor.equals("Disable")) {
			ImagePlus currentImp_outlineOnly = extractSingleColor(currentImp_duplicate, detectInsideColor);
			ImagePlus currentImp_outlineOnly_holesFilled = duplicator.run(currentImp_outlineOnly);
			IJ.run(currentImp_outlineOnly_holesFilled, "8-bit", "stack");
			IJ.run(currentImp_outlineOnly_holesFilled, "Fill Holes", "stack");
			IJ.run(currentImp_outlineOnly_holesFilled, "RGB Color", "stack");
			// Get what's inside outlines
			currentImp_duplicate = ic.run("AND create stack", currentImp_duplicate,	currentImp_outlineOnly_holesFilled);
			//Remove outlines
			currentImp_duplicate = ic.run("Subtract create stack", currentImp_duplicate, currentImp_outlineOnly); 
			currentImp_outlineOnly.close();
			currentImp_outlineOnly_holesFilled.close();
		}


		// If only 1 color should be detected, remove other colors
		if (detectColor.equals("Any")) {
			currentImp_duplicate_filtered = currentImp_duplicate;
		} else {
			//2nd arg keepsource. Spilts the RGB stack into three 8-bit grayscale stacks
			ImageStack[] imgStack = ChannelSplitter.splitRGB(currentImp_duplicate.getImageStack(), false); 
			ImagePlus r = new ImagePlus("r", imgStack[0]);
			ImagePlus g = new ImagePlus("g", imgStack[1]);
			ImagePlus b = new ImagePlus("b", imgStack[2]);
			// single color (RGB) - subtract the two other colors from the
			// single one (r = r-g-b).
			if (detectColor.equals("Red")) {
				currentImp_duplicate_filtered = ic.run("Subtract create stack", r, g);
				currentImp_duplicate_filtered = ic.run("Subtract create stack", currentImp_duplicate_filtered, b);
			}
			if (detectColor.equals("Green")) {
				currentImp_duplicate_filtered = ic.run("Subtract create stack", g, r);
				currentImp_duplicate_filtered = ic.run("Subtract create stack", currentImp_duplicate_filtered, b);
			}
			if (detectColor.equals("Blue")) {
				currentImp_duplicate_filtered = ic.run("Subtract create stack", b, r);
				currentImp_duplicate_filtered = ic.run("Subtract create stack", currentImp_duplicate_filtered, g);
			}
			// double color (RGB) - minimum of the two colors. subtract the
			// third (cyan = min(b,g) - r)
			if (detectColor.equals("Cyan")) {
				currentImp_duplicate_filtered = ic.run("Min create stack", b, g);
				currentImp_duplicate_filtered = ic.run("Subtract create stack", currentImp_duplicate_filtered, r);
			}
			if (detectColor.equals("Yellow")) {
				currentImp_duplicate_filtered = ic.run("Min create stack", r, g);
				currentImp_duplicate_filtered = ic.run("Subtract create stack", currentImp_duplicate_filtered, b);
			}
			if (detectColor.equals("Magenta")) {
				currentImp_duplicate_filtered = ic.run("Min create stack", b, r);
				currentImp_duplicate_filtered = ic.run("Subtract create stack", currentImp_duplicate_filtered, g);
			}
			// tripple color (white/gray) - minimum of the three colors.
			if (detectColor.equals("White") || detectColor.equals("Grey") || detectColor.equals("Gray")) {
				currentImp_duplicate_filtered = ic.run("Min create stack", r, g);
				currentImp_duplicate_filtered = ic.run("Min create stack", currentImp_duplicate_filtered, b);
			}
			currentImp_duplicate.close();
		}

		// At this point we have a filtered image to detect maxima on
		// Apply blur and find maxima in currentImp_singlePlane, and show on
		// currentImp
		if (lightBackground) {
			IJ.run(currentImp_duplicate_filtered, "Invert", "stack");
		}
		if (blurRadius > 0) {
			IJ.run(currentImp_duplicate_filtered, "Gaussian Blur...", "stack sigma=" + blurRadius);
		}
		

		if ((currentImp_duplicate_filtered.getBitDepth() == 24)) { // If the filtered image is RGB at this point (happens when some of the filters are not applied), convert to 8 bit as appropriate for the 3D maxima finder
			IJ.run(currentImp_duplicate_filtered, "8-bit", "stack");
		}

		
		return currentImp_duplicate_filtered;
		}

	
	
	public ImagePlus extractSingleColor(ImagePlus inputImp, String color) {
		// Returns a duplicate where the input color (must be saturated)
		// is turned white, and all other colors black.
		double color_hex;
		ImageProcessor imageProcessor;
		ImageStack imageStack;
		ImagePlus impToReturn;
		color_hex = 0xffffff; // dummy initialization value
		impToReturn = new Duplicator().run(inputImp);
		if (color.equals("Red"))
			color_hex = 0xff0000;
		else if (color.equals("Green"))
			color_hex = 0x00ff00;
		else if (color.equals("Blue"))
			color_hex = 0x0000ff;
		else if (color.equals("Cyan"))
			color_hex = 0x00ffff;
		else if (color.equals("Magenta"))
			color_hex = 0xff00ff;
		else if (color.equals("Yellow"))
			color_hex = 0xffff00;
		else if (color.equals("White"))
			color_hex = 0xffffff;
		color_hex = (int) color_hex & 0xffffff;
		
		imageStack = impToReturn.getStack();
		
		for (int slice = 1; slice <= inputImp.getNSlices(); slice++) {
			imageProcessor = imageStack.getProcessor(slice);
			int xmin = 0, ymin = 0, xmax = imageProcessor.getWidth(), ymax = imageProcessor.getHeight();
			double v;
			for (int y = ymin; y < ymax; y++) {
				for (int x = xmin; x < xmax; x++) {
					v = imageProcessor.getPixel(x, y) & 0xffffff;
					if (v == color_hex) {
						imageProcessor.putPixel(x, y, (int) 0xffffff);
					} else {
						imageProcessor.putPixel(x, y, (int) 0x000000);
					}
				}
			}
			imageStack.setProcessor(imageProcessor, slice);
			
		}
	impToReturn.setStack(imageStack);
	return impToReturn;
	}
	
	
}