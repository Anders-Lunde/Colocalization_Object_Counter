import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Overlay;
import ij.io.Opener;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTable;

public class InformationTables extends JFrame {
	
	
public void SummaryAcrossImages_per_category() {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setBounds(100, 100, (int) (screenWidth*0.5), (int) (screenHeight*0.8));
		
		setResizable(true);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		ImagePlus currentImp = WindowManager.getCurrentImage();
		String rootPath = currentImp.getOriginalFileInfo().directory;
		String imageFileName = currentImp.getOriginalFileInfo().fileName;
		//String imageFileNameExtensionless = imageFileName.substring(0, imageFileName.lastIndexOf('.'));
		
		//Find all Celldata files
		File dir = new File(rootPath + "/Counts/Celldata/");
		File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
		
		//initialize global lists
		ArrayList<String> filenames = new ArrayList<String>();
		ArrayList<Double> xpoints = new ArrayList<>();
		ArrayList<Double> ypoints = new ArrayList<>();
		ArrayList<Integer> categories = new ArrayList<>();
		
		//For each data file
		for (File file : files) {
			//Read data into arrays using ImageJ results table
			ResultsTable tmpTable = new ResultsTable();
			Opener.openResultsTable(file.getAbsolutePath());
			tmpTable = Analyzer.getResultsTable();
			tmpTable.showRowNumbers(false);
			double[] x = tmpTable.getColumnAsDoubles(0);
			double[] y = tmpTable.getColumnAsDoubles(1);
			double[] cat = tmpTable.getColumnAsDoubles(2);
			//Add to global lists
			for (int i=0;i<x.length;i++) {
				xpoints.add(x[i]);
				ypoints.add(y[i]);
				categories.add((int) cat[i]);
				filenames.add(file.getName());
			}		
		}
		IJ.run("Clear Results", "");
		
		//Count how many of each category
		ArrayList<String> arr = new ArrayList<>();
		for (int i = 0; i < categories.size(); i++) {
			String a = Integer.toString(categories.get(i));
			String h = a.replaceAll(",", "");
			arr.add(sortString(h));
		}
		ArrayList<Integer> count = new ArrayList<>();
		ArrayList<String> categories_tmp = new ArrayList<>();
		for (int i = 0; i < arr.size(); i++) {
			if (!arr.get(i).equals("-1")) {
				count.add(Collections.frequency(arr, arr.get(i)));
				categories_tmp.add(arr.get(i));
				Collections.replaceAll(arr, arr.get(i), "-1");
			}
		}
		Object[][] data = new Object[categories_tmp.size()][2];
		for (int i = 0; i < categories_tmp.size(); i++) {
			data[i][0] = categories_tmp.get(i);
			data[i][1] = count.get(i);
		}

		String[] columnNames = { "Category(es)", "Counts" };
		JTable table = new JTable(data, columnNames);

		//scrollPane.setViewportView(table);
		//setVisible(true);

		

		//String[] columnNames = { "Filename", "X-raw", "Y-raw", "Categories"};
		//JTable table = new JTable(data, columnNames);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 22, (int) (screenWidth*0.4) - 70, (int) (screenHeight*0.8) - 100);
		contentPane.add(scrollPane);
		scrollPane.setViewportView(table);
		setVisible(true);
	}
	
	public void SummaryAcrossImages() {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setBounds(100, 100, (int) (screenWidth*0.8), (int) (screenHeight*0.8));
		
		setResizable(true);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		ImagePlus currentImp = WindowManager.getCurrentImage();
		String rootPath = currentImp.getOriginalFileInfo().directory;
		String imageFileName = currentImp.getOriginalFileInfo().fileName;
		//String imageFileNameExtensionless = imageFileName.substring(0, imageFileName.lastIndexOf('.'));
		
		//Find all Celldata files
		File dir = new File(rootPath + "/Counts/Celldata/");
		File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
		
		//initialize global lists
		ArrayList<String> filenames = new ArrayList<String>();
		ArrayList<Double> xpoints = new ArrayList<>();
		ArrayList<Double> ypoints = new ArrayList<>();
		ArrayList<Double> zpoints = new ArrayList<>();
		ArrayList<Integer> categories = new ArrayList<>();
		
		//For each data file
		for (File file : files) {
			//Read data into arrays using ImageJ results table
			ResultsTable tmpTable = new ResultsTable();
			Opener.openResultsTable(file.getAbsolutePath());
			tmpTable = Analyzer.getResultsTable();
			tmpTable.showRowNumbers(false);
			double[] x = tmpTable.getColumnAsDoubles(0);
			double[] y = tmpTable.getColumnAsDoubles(1);
			double[] z = tmpTable.getColumnAsDoubles(2);
			double[] cat = tmpTable.getColumnAsDoubles(3);
			//Add to global lists
			for (int i=0;i<x.length;i++) {
				xpoints.add(x[i]);
				ypoints.add(y[i]);
				zpoints.add(z[i]);
				categories.add((int) cat[i]);
				filenames.add(file.getName());
			}		
		}
		IJ.run("Clear Results", "");
		
		//Create data object that is used for the JTable
		Object[][] data = new Object[xpoints.size()][5];
		for (int i = 0; i < xpoints.size(); i++) {
			data[i][0] = filenames.get(i);
			data[i][1] = xpoints.get(i);
			data[i][2] = ypoints.get(i);
			data[i][3] = zpoints.get(i);
			data[i][4] = categories.get(i);
		}

		String[] columnNames = { "Filename", "X-raw", "Y-raw", "Z-slice", "Categories"};
		JTable table = new JTable(data, columnNames);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 22, (int) (screenWidth*0.8) - 70, (int) (screenHeight*0.8) - 100);
		contentPane.add(scrollPane);
		scrollPane.setViewportView(table);
		setVisible(true);
	}
	
	
	
	public void LoadInformationTable() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setBounds(100, 100, (int) (screenWidth*0.5), (int) (screenHeight*0.8));
		
		setResizable(true);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		ImagePlus currentImp = WindowManager.getCurrentImage();
		String rootPath = currentImp.getOriginalFileInfo().directory;
		String imageFileName = currentImp.getOriginalFileInfo().fileName;
		//String imageFileNameExtensionless = imageFileName.substring(0, imageFileName.lastIndexOf('.'));
		File file = new File(rootPath + "/Counts/Celldata/" + imageFileName + ".csv");
		if (!file.exists()) {
			IJ.showMessage("No information save for this file: " + imageFileName);
			return;
		}
		ResultsTable tmpTable = new ResultsTable();

		Opener.openResultsTable(rootPath + "/Counts/Celldata/" + imageFileName + ".csv");
		tmpTable = Analyzer.getResultsTable();
		tmpTable.showRowNumbers(false);
		double[] xpoints = tmpTable.getColumnAsDoubles(0);
		double[] ypoints = tmpTable.getColumnAsDoubles(1);
		double[] zpoints = tmpTable.getColumnAsDoubles(2);
		double[] names = tmpTable.getColumnAsDoubles(3);

		Object[][] data = new Object[xpoints.length][4];
		for (int i = 0; i < xpoints.length; i++) {
			data[i][0] = (int) names[i];
			data[i][1] = xpoints[i];
			data[i][2] = ypoints[i];
			data[i][3] = zpoints[i];
		}

		String[] columnNames = { "Categories", "X-raw", "Y-raw", "Z-slice"};

		JTable table = new JTable(data, columnNames);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 22, (int) (screenWidth*0.4) - 70, (int) (screenHeight*0.8) - 100);
		contentPane.add(scrollPane);
		scrollPane.setViewportView(table);
		setVisible(true);
	}

	public void CategoryCountTable(Object[][] data) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		setBounds(100, 100, (int) (screenWidth*0.5), (int) (screenHeight*0.8));
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 22, (int) (screenWidth*0.4) - 70, (int) (screenHeight*0.8) - 100);
		contentPane.add(scrollPane);

		String[] columnNames = { "Category(es)", "Counts" };
		JTable table = new JTable(data, columnNames);

		scrollPane.setViewportView(table);
		setVisible(true);

	}
	// Method to sort a string alphabetically (helping method)
	public static String sortString(String inputString) {
		// convert input string to char array
		char tempArray[] = inputString.toCharArray();
		// sort tempArray
		Arrays.sort(tempArray);
		// return new sorted string
		return new String(tempArray);
	}
}
