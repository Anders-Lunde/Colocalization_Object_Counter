import java.awt.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.text.TextWindow;

public class OutputEmbeddedJarFiles {
	private static final Path InputStream = null;

	static public void save() throws Exception {
		ImagePlus currentImp = WindowManager.getCurrentImage();
		String rootPath = currentImp.getOriginalFileInfo().directory;

		ArrayList<File> fileList=new ArrayList<File>();  
		fileList.add(new File(rootPath + "/Counts/ColocalizationObjectCounter_Importer.xlsm"));
		fileList.add(new File(rootPath + "/Counts/Export/Colocalization_Visualization_3D_GUI.fig"));
		fileList.add(new File(rootPath + "/Counts/Export/Colocalization_Visualization_3D_GUI.m"));
		fileList.add(new File(rootPath + "/Counts/Export/Colocalization_Visualization_3D_non_GUI.m"));
		fileList.add(new File(rootPath + "/Counts/Export/colors.txt"));
		
		for (File f : fileList) {
			f.getParentFile().mkdirs(); // Create folders if not exists.
			if (!f.exists()) {
				ExportResource("/" + f.getName(), f.getCanonicalPath());
			} 
		}
		System.gc();
	}
	
	 /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @throws Exception
     */
    static public void ExportResource(String resourceName, String destinationPath) throws Exception {
    	IJ.log("resourceName: " + resourceName);
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = OutputEmbeddedJarFiles.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(destinationPath);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
        return;
    }
}

