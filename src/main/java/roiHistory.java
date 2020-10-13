import java.util.HashMap;
import java.util.Map;

import ij.ImagePlus;
import ij.gui.Roi;

	//Used for the logging feature. To keep track of previous state of multipoints,
	//so that what was changed can be detected.

	public class roiHistory {

		private static final Map<ImagePlus, Roi> roiHistoryMap = new HashMap<>();
		private static final Map<ImagePlus, Boolean> allRoisDeleted = new HashMap<>();
	    private static roiHistory instance = new roiHistory();

	    private roiHistory() {
	    }

	    public static roiHistory getInstance() {
	        return instance;
	    }

	    public static Roi getValue(ImagePlus key) {
	        return roiHistoryMap.get(key);
	    }

	    public static void add(ImagePlus key, Roi val) {
        	roiHistoryMap.put(key, val);
	    }
	    
	    public static Boolean getValueAllRoisDeleted(ImagePlus key) {
	        return allRoisDeleted.get(key);
	    }

	    public static void addAllRoisDeleted(ImagePlus key, Boolean val) {
	    	allRoisDeleted.put(key, val);
	    }
	    
	}
