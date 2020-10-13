import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.Roi;
import java.awt.Color;
import java.io.File;

/**
 *
 * @author M. Hassan Rehan
 */
public class Category {

	Overlay overlay = new Overlay();;

	public void initializeOverlay(String OnCategory, boolean Selectable, int ActiveWidth, int ActiveHeight) {
		if (OnCategory != null) {
			overlay.clear();
			ImagePlus currentImp = WindowManager.getCurrentImage();
			String rootPath = currentImp.getOriginalFileInfo().directory;
			File cellDataDir = new File(rootPath + "/Counts/Celldata");
			cellDataDir.mkdirs();

			int[] ypoints = currentImp.getRoi().getPolygon().ypoints;
			int[] xpoints = currentImp.getRoi().getPolygon().xpoints;
			
			PointRoi currentRois = (PointRoi) currentImp.getRoi(); //To get Z values of multipoints

			for (int i = 0; i < xpoints.length; i++) {
				int pointZPos;
				if (currentImp.getNSlices() == 1) {
					pointZPos = 1;
				} else {
					pointZPos = currentImp.convertIndexToPosition(currentRois.getPointPosition(i))[1];
				}
				int x = xpoints[i] - (int) (ActiveWidth / 2);
				int y = ypoints[i] - (int) (ActiveHeight / 2);
				Roi o = new OvalRoi(x, y, ActiveWidth, ActiveHeight);
				if (UserInterface.overlayInSlices.isSelected()) {
					if (currentImp.isHyperStack()) {
						o.setPosition(0, pointZPos, 0); //Display on all channels and frames, but tied to slice
					} else { 
						o.setPosition(pointZPos);
					}
				}
				o.setName(OnCategory);
				o.setProperty("customZ", Integer.toString(pointZPos));
				overlay.add(o);
			}
			overlay.drawLabels(true);
			overlay.drawNames(true);
			if (!(IJ.getVersion().compareTo("1.51v18") < 0)) {
				overlay.selectable(Selectable);
			}
			//overlay.setStrokeColor(Color.yellow);
			//overlay.setLabelColor(Color.yellow);
			currentImp.setOverlay(overlay);
		}
	}

	public Overlay getOverlay() {
		return overlay;
	}

}
