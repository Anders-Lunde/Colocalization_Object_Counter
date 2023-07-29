import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.io.Opener;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

/**
 *
 * @author M. Hassan Rehan
 */
public class ClearCounts_LoadCounts_GenericAlert extends JDialog {
	private JPanel contentPane;
	Opener opener = new Opener();
	Analyzer analyzer = new Analyzer();
	ResultsTable tmpTable;

	// b=1 for clear all counts alert
	// b=2 for load counts from csv alert and
	// b=3 for any alert (Dialog) Message
	public ClearCounts_LoadCounts_GenericAlert(String Message, int b) {
		super((Window) null);
		setModal(true);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);

		JLabel label = new JLabel("<html>" + Message + "</html>");
		if (b == 1 || b == 2)
			label.setBounds(10, 11, 366, 74);
		else {
			label.setBounds(22, 11, 509, 127);
		}
		contentPane.add(label);

		JButton btnOk = new JButton("OK");
		if (b == 1 || b == 2)
			btnOk.setBounds(317, 90, 59, 23);
		else
			btnOk.setBounds(483, 138, 59, 23);

		btnOk.addActionListener((ActionEvent e) -> {
			if (b == 1) {
				ImagePlus currentImp = WindowManager.getCurrentImage();

				Overlay ol = new Overlay();
				ol.drawLabels(true);
				ol.drawNames(true);
				//ol.setStrokeColor(Color.yellow);
				//ol.setLabelColor(Color.yellow);
				if (!(IJ.getVersion().compareTo("1.51v18") < 0)) {
					ol.selectable(currentImp.getOverlay().isSelectable());
				}
				currentImp.setOverlay(ol);

			}
			if (b == 2) { //Load counts
				ImagePlus currentImp = WindowManager.getCurrentImage();
				String rootPath = currentImp.getOriginalFileInfo().directory;
				String imageFileName = currentImp.getOriginalFileInfo().fileName;
				//String imageFileNameExtensionless = imageFileName.substring(0, imageFileName.lastIndexOf('.'));
				File file = new File(rootPath + "/Counts/Celldata/" + imageFileName + ".csv");
				if (!file.exists()) {
					CocUserInterface.showMessageCustom("No datafile found in " + "/Counts/Celldata/" + imageFileName + ".csv");
					dispose();
					return;
				}
				tmpTable = new ResultsTable();
				Opener.openResultsTable(rootPath + "/Counts/Celldata/" + imageFileName + ".csv");
				tmpTable = Analyzer.getResultsTable();
				tmpTable.showRowNumbers(false);
				double[] xpoints = tmpTable.getColumnAsDoubles(0);
				double[] ypoints = tmpTable.getColumnAsDoubles(1);
				double[] zpoints = tmpTable.getColumnAsDoubles(2);
				double[] names = tmpTable.getColumnAsDoubles(3);
				String[] namesStr = new String[xpoints.length];
				String[] zpointsStr = new String[xpoints.length];
				for (int j = 0; j < names.length; j++) {
					namesStr[j] = withBuilder(String.valueOf((int) names[j]));
					zpointsStr[j] = String.valueOf((int) zpoints[j]);
				}
				Overlay ol = new Overlay();
				for (int i = 0; i < xpoints.length; i++) {
					OvalRoi oval = new OvalRoi(xpoints[i] - 25, ypoints[i] - 25, 50, 50);
					//Sort categories to ascending order
					String[] aUnsorted = namesStr[i].split(",");
					Arrays.sort(aUnsorted);
					namesStr[i] = String.join(",", aUnsorted);
					oval.setName(namesStr[i]);
					if (CocUserInterface.overlayInSlices.isSelected()) {
						if (currentImp.isHyperStack()) {
							oval.setPosition(0, (int) zpoints[i], 0); //Display on all channels and frames, but tied to slice
						} else { 
							oval.setPosition((int) zpoints[i]);
						}
					}
					oval.setProperty("customZ", zpointsStr[i]);
					ol.add(oval);
				}
				ol.drawLabels(true);
				ol.drawNames(true);
				//ol.setStrokeColor(Color.yellow);
				//ol.setLabelColor(Color.yellow);
				currentImp.setOverlay(ol);
				CocUserInterface.saveAndDisplayEvent(currentImp, "Data loaded from file: " + rootPath + " Counts/Celldata/" + imageFileName + ".csv");

			}
			dispose();
		});
		contentPane.add(btnOk);

		setContentPane(contentPane);
		if (b == 1 || b == 2)
			setBounds(100, 100, 402, 160);
		else
			setBounds(100, 100, 568, 211);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	

    

	private static String withBuilder(String s) {
		StringBuilder builder = new StringBuilder(s);
		int index = 1;
		for (int i = 0; i < s.length() - 1; i++) {
			builder.insert(index, ",");
			index += 2;
		}
		return builder.toString();
	}

}
