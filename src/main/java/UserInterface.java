/**
 *
 * @author M. Hassan Rehan
 * @author Anders Lunde
 */
import ij.IJ;
import static ij.IJ.setTool;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.ChannelSplitter;
import ij.plugin.Duplicator;
import ij.plugin.EventListener;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.MaximumFinder;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.NumberFormatter;

public class UserInterface extends JFrame {

	private JPanel contentPane;	
	JCheckBox checkBox_1, checkBox_2, checkBox_3, checkBox_4, checkBox_5, checkBox_6, checkBox_7, checkBox_8;		
	JLabel label_1, label_2, label_3, label_4, label_5, label_6, label_7, label_8;
	Category Category = new Category();
	JPopupMenu menu;
	JMenuItem m1, m2, m3, m4, m5, m6, m7, m8;
	ArrayList<String> inside = null;
	ArrayList<String> outside = null;
	Overlay over = null; // for show/hide category overlay button
	SaveCounts SSI = new SaveCounts();
	SetOriginAndNorth SOAN = new SetOriginAndNorth();
	DrawContours DC = new DrawContours();
	InformationTables table = new InformationTables();
	JCheckBox Selectable;
	static JCheckBox overlayInSlices;
	static boolean disableNewImageWarnings = false;
	boolean wasShown_find3DWarning = false;
	JCheckBox multipointsInSlices;
	int toggler = 0; // for toggle multipoints button
	int ActiveWidth = 50;
	int ActiveHeight = 50;
	int added_height_space = 60;
	RoiManager rm = RoiManager.getInstance();	
	//Auto Detect Settings panel
	static JFormattedTextField blurRadiusButton, noiseToleranceButton, radiusxyButton, radiuszButton;
	static JComboBox detectColorButton, detectInsideColorButton;
	static JCheckBox excludeOnEdgesButton, lightBackgroundButton, ignoreTopSlice, showOutput;
	


	/**
	 * Create the frame.
	 */
	public UserInterface() {

		UserInputLogger userInputLogger = new UserInputLogger();
		userInputLogger.run("");
		
		//Set some defaults
		IJ.run("Point Tool...", "type=Cross color=Cyan size=[Extra Large] counter=0"); //TODO: Remove ?
		IJ.run("Overlay Options...", "set");
		Prefs.showAllPoints = true; //If changed, also change corresponding checkbox default (associate multipoints with slices)  
		
		
		contentPane = new JPanel();
		contentPane.setBackground(new Color(192, 192, 192));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);

		// Menubar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setMargin(new Insets(5, 5, 10, 5));
		menuBar.setForeground(Color.WHITE);
		menuBar.setBackground(SystemColor.menu);
		menuBar.setBounds(0, 0, 479, 23);
		contentPane.add(menuBar);

		// Menubar about section
		JButton btnAbout = new JButton("About/help");
		btnAbout.setBackground(SystemColor.activeCaptionBorder);
		btnAbout.addActionListener((ActionEvent e) -> {
			LookAndFeel laf = UIManager.getLookAndFeel();
			IJ.showMessage("<html><b>Colocalization Object Counter</b><br>"
					+ "Plugin for semi-automatic Object-Based Colocalization Analysis.<br>"
					+ "<br>"
					+ "If you use this tool for a publication, please cite us:<br>"
					+ "<a href=\"https://www.nature.com/srep/\"> Anticipated in Scientific Reports (Lunde&Glover 2020)</a><br>"
					+ "<br>"
					+ "<br>"
					+ "Read the original publication at <a href=\\\"https://www.nature.com/srep/\\\"> Anticipated in Scientific Reports (Lunde&Glover 2020)</a> for instrucitons on how to use.<br>"
					+ "<br>"
					+ "Report bugs at github: <a href=\"https://github.com/Anders-Lunde/Colocalization_Object_Counter/\"> https://github.com/Anders-Lunde/Colocalization_Object_Counter/</a> <br>"
					+ "<br>"
					+ "");
			try {
				UIManager.setLookAndFeel(laf);
			} catch (UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
			
		});
		menuBar.add(btnAbout);

		// category section (button 1)
		JLabel lblNewLabel = new JLabel("Category:\r\n");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblNewLabel.setForeground(new Color(0, 0, 128));
		lblNewLabel.setBounds(19, 38, 55, 14);
		contentPane.add(lblNewLabel);

		JLabel label = new JLabel(
				"1               2              3              4              5               6              7               8");
		label.setFont(new Font("Tahoma", Font.BOLD, 9));
		label.setForeground(new Color(60, 179, 113));
		label.setBounds(77, 38, 367, 14);
		contentPane.add(label);

		checkBox_1 = new JCheckBox("New check box");
		checkBox_1.setBackground(new Color(192, 192, 192));
		checkBox_1.setBounds(70, 50, 21, 23);
		checkBox_1.setSelected(true);
		contentPane.add(checkBox_1);

		checkBox_2 = new JCheckBox("New check box");
		checkBox_2.setBackground(new Color(192, 192, 192));
		checkBox_2.setBounds(123, 50, 21, 23);
		contentPane.add(checkBox_2);

		checkBox_3 = new JCheckBox("New check box");
		checkBox_3.setBackground(new Color(192, 192, 192));
		checkBox_3.setBounds(173, 50, 21, 23);
		contentPane.add(checkBox_3);

		checkBox_4 = new JCheckBox("New check box");
		checkBox_4.setBackground(new Color(192, 192, 192));
		checkBox_4.setBounds(223, 50, 21, 23);
		contentPane.add(checkBox_4);

		checkBox_5 = new JCheckBox("New check box");
		checkBox_5.setBackground(new Color(192, 192, 192));
		checkBox_5.setBounds(271, 50, 21, 23);
		contentPane.add(checkBox_5);

		checkBox_6 = new JCheckBox("New check box");
		checkBox_6.setBackground(new Color(192, 192, 192));
		checkBox_6.setBounds(323, 50, 21, 23);
		contentPane.add(checkBox_6);

		checkBox_7 = new JCheckBox("New check box");
		checkBox_7.setBackground(new Color(192, 192, 192));
		checkBox_7.setBounds(371, 50, 21, 23);
		contentPane.add(checkBox_7);

		checkBox_8 = new JCheckBox("New check box");
		checkBox_8.setBackground(new Color(192, 192, 192));
		checkBox_8.setBounds(423, 50, 21, 23);
		contentPane.add(checkBox_8);

		// Counts section (button 2)
		JLabel lblCounts = new JLabel("Counts:");
		lblCounts.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblCounts.setForeground(new Color(0, 0, 128));
		lblCounts.setBounds(19, 73, 46, 14);
		contentPane.add(lblCounts);

		label_1 = new JLabel("0");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_1.setBounds(70, 73, 35, 16);
		contentPane.add(label_1);

		label_2 = new JLabel("0\r\n");
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_2.setBounds(123, 73, 35, 16);
		contentPane.add(label_2);

		label_3 = new JLabel("0\r\n");
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_3.setBounds(173, 73, 38, 16);
		contentPane.add(label_3);

		label_4 = new JLabel("0\r\n");
		label_4.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_4.setBounds(223, 73, 35, 16);
		contentPane.add(label_4);

		label_5 = new JLabel("0\r\n");
		label_5.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_5.setBounds(271, 73, 35, 16);
		contentPane.add(label_5);

		label_6 = new JLabel("0\r\n");
		label_6.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_6.setBounds(323, 73, 35, 16);
		contentPane.add(label_6);

		label_7 = new JLabel("0\r\n");
		label_7.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_7.setBounds(371, 73, 35, 16);
		contentPane.add(label_7);

		label_8 = new JLabel("0\r\n");
		label_8.setFont(new Font("Tahoma", Font.PLAIN, 10));
		label_8.setBounds(423, 73, 35, 16);
		contentPane.add(label_8);

		// Modes section (button 3)
		JLabel lblModes = new JLabel("Modes:");
		lblModes.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblModes.setForeground(new Color(0, 0, 128));
		lblModes.setBounds(19, 95, 46, 14);
		contentPane.add(lblModes);

		JRadioButton rdbtnSetCatagory = new JRadioButton("Set Category");
		rdbtnSetCatagory.setForeground(new Color(60, 179, 113));
		rdbtnSetCatagory.setFont(new Font("Tahoma", Font.BOLD, 10));
		rdbtnSetCatagory.setBackground(new Color(192, 192, 192));
		rdbtnSetCatagory.setBounds(74, 95, 106, 23);
		contentPane.add(rdbtnSetCatagory);

		JRadioButton rdbtnAddCata = new JRadioButton("Add Category\r\n");
		rdbtnAddCata.setSelected(true);
		rdbtnAddCata.setForeground(new Color(60, 179, 113));
		rdbtnAddCata.setFont(new Font("Tahoma", Font.BOLD, 10));
		rdbtnAddCata.setBackground(new Color(192, 192, 192));
		rdbtnAddCata.setBounds(215, 95, 101, 23);
		contentPane.add(rdbtnAddCata);

		JRadioButton rdbtnDeleteCell = new JRadioButton("Delete Cell");
		rdbtnDeleteCell.setForeground(new Color(60, 179, 113));
		rdbtnDeleteCell.setFont(new Font("Tahoma", Font.BOLD, 10));
		rdbtnDeleteCell.setBackground(new Color(192, 192, 192));
		rdbtnDeleteCell.setBounds(361, 95, 83, 23);
		contentPane.add(rdbtnDeleteCell);

		// radio button functions (button 3)
		rdbtnSetCatagory.addActionListener((ActionEvent e) -> {
			rdbtnDeleteCell.setSelected(false);
			rdbtnAddCata.setSelected(false);
			setTool("multipoint");
		});
		rdbtnAddCata.addActionListener((ActionEvent e) -> {

			rdbtnDeleteCell.setSelected(false);
			rdbtnSetCatagory.setSelected(false);
			setTool("multipoint");
		});
		rdbtnDeleteCell.addActionListener((ActionEvent e) -> {
			rdbtnSetCatagory.setSelected(false);
			rdbtnAddCata.setSelected(false);
			setTool("multipoint");
		});

		// Selectable overlays toggle
		if (!(IJ.getVersion().compareTo("1.51v18") < 0)) {
			JLabel lblToggleOverlaySelectable = new JLabel("Selectable overlays:");
			lblToggleOverlaySelectable.setFont(new Font("Arial", Font.BOLD, 10));
			lblToggleOverlaySelectable.setForeground(new Color(0, 0, 128));
			lblToggleOverlaySelectable.setBounds(10, 121, 136, 14);
			contentPane.add(lblToggleOverlaySelectable);

			Selectable = new JCheckBox("New check box");
			Selectable.setSelected(false);
			Selectable.setBackground(Color.LIGHT_GRAY);
			Selectable.setBounds(115, 116, 21, 23);
			Selectable.addActionListener((ActionEvent e) -> {
				if (WindowManager.getCurrentImage() != null) {
					if (WindowManager.getCurrentImage().getOverlay() != null) {
						ImagePlus currentImp = WindowManager.getCurrentImage();
						if (Selectable.isSelected())
							currentImp.getOverlay().selectable(true);
						else
							currentImp.getOverlay().selectable(false);
					}
				}
			});
			contentPane.add(Selectable);
		}
		
		// Accossiate with slices: overlays/multipoints
		if (!(IJ.getVersion().compareTo("1.51v18") < 0)) {
			JLabel lblAccosiateSlices = new JLabel("Associate with slices  -  Overlays:          Multiponts:");
			lblAccosiateSlices.setFont(new Font("Arial", Font.BOLD, 10));
			lblAccosiateSlices.setForeground(new Color(0, 0, 128));
			lblAccosiateSlices.setBounds(155, 121, 300, 14);
			contentPane.add(lblAccosiateSlices);

			overlayInSlices = new JCheckBox("New check box");
			overlayInSlices.setSelected(false);
			overlayInSlices.setBackground(Color.LIGHT_GRAY);
			overlayInSlices.setBounds(324, 116, 21, 23);
			overlayInSlices.addActionListener((ActionEvent e) -> {
				ImagePlus currentImp = WindowManager.getCurrentImage();
				if (currentImp != null) {
					if (currentImp.getOverlay() != null) {
						Overlay ol = currentImp.getOverlay();
						if (overlayInSlices.isSelected()) {
							for (int i = 0; i < ol.size(); i++) {
								if (ol.get(i).getProperties() != null) {
									int a = Integer.parseInt(ol.get(i).getProperty("customZ"));
									if (currentImp.isHyperStack()) {
										ol.get(i).setPosition(0,a,0); //Set Z pos to original Z pos //Display on all channels and frames, but tied to slice
									} else {
										ol.get(i).setPosition(a); //Set Z pos to original Z pos //tied to slice
									}
								}
							}
						} else {
							for (int i = 0; i < ol.size(); i++) {
								if (ol.get(i).getPosition() != 0) { //Store original Z pos
									ol.get(i).setProperty("customZ", Integer.toString(ol.get(i).getPosition()));
								}
								if (currentImp.isHyperStack()) {
									ol.get(i).setPosition(0,0,0); //Set Z pos to 0 which means to display in all slices	//Display on all channels and frames,
									} else {
									ol.get(i).setPosition(0); //Set Z pos to 0 which means to display in all slices	
								}			
							}
						}
					}
				}
				if (WindowManager.getCurrentImage() != null) {
					WindowManager.getCurrentImage().updateAndDraw();
				}	
			});
			contentPane.add(overlayInSlices);
			
			multipointsInSlices = new JCheckBox("New check box");
			multipointsInSlices.setSelected(false);
			multipointsInSlices.setBackground(Color.LIGHT_GRAY);
			multipointsInSlices.setBounds(408, 116, 21, 23);
			multipointsInSlices.addActionListener((ActionEvent e) -> {
						if (multipointsInSlices.isSelected()) {
							Prefs.showAllPoints = false;
						}
						else {
							Prefs.showAllPoints = true;
						}
						if (WindowManager.getCurrentImage() != null) {
							WindowManager.getCurrentImage().updateAndDraw();
						}		
			});
			contentPane.add(multipointsInSlices);
		}
		
		

		// Show/Hide all overlays button
		JButton btnShowhideAllOverlays = new JButton("Show/hide all overlays");
		btnShowhideAllOverlays.setFont(new Font("Arial", Font.BOLD, 11));
		btnShowhideAllOverlays.setForeground(new Color(0, 0, 0));
		btnShowhideAllOverlays.setBounds(19, 146, 186, 23);
		contentPane.add(btnShowhideAllOverlays);

		btnShowhideAllOverlays.addActionListener((ActionEvent e) -> {

			ImagePlus currentImp = WindowManager.getCurrentImage();
			if (currentImp != null) {
				if (currentImp.getOverlay() != null) {
					if (!currentImp.getHideOverlay())
						currentImp.setHideOverlay(true);
					else if (currentImp.getHideOverlay())
						currentImp.setHideOverlay(false);
				} else
					IJ.showMessage("Please draw some cells on image");
			} else
				IJ.showMessage("Please open an image from drive");
		});

		// Show/hide current category overlays button
		JButton btnNewButton = new JButton("Show/hide category overlays");
		btnNewButton.setForeground(new Color(0, 0, 0));
		btnNewButton.setFont(new Font("Arial", Font.BOLD, 11));
		btnNewButton.setBounds(223, 146, 221, 23);
		contentPane.add(btnNewButton);

		btnNewButton.addActionListener((ActionEvent e) -> {
			if (WindowManager.getCurrentImage() != null) {
				ImagePlus currentImp = WindowManager.getCurrentImage();
				if (currentImp.getOverlay() != null) {
					if (over == null) {

						Overlay ol = currentImp.getOverlay();
						over = ol;
						Overlay ol_2 = new Overlay();
						for (int i = 0; i < ol.size(); i++) {
							int y = 0;
							String a = ol.get(i).getName();
							StringTokenizer st = new StringTokenizer(a, ",");
							while (st.hasMoreTokens()) {
								String c = st.nextToken();
								String c2 = selectedCategory();
								StringTokenizer st2 = new StringTokenizer(c2, ",");
								while (st2.hasMoreTokens()) {
									if (st2.nextToken().equals(c)) {
										y++;
									}

								}
							}
							if (y == 0)
								ol_2.add(ol.get(i));
						}
						ol_2.drawLabels(true);
						ol_2.drawNames(true);
						ol_2.setStrokeColor(ol.getLabelColor());
						ol_2.setLabelColor(ol.getLabelColor());
						if (!(IJ.getVersion().compareTo("1.51v18") < 0)) {
							ol_2.selectable(Selectable.isSelected());
						}
						currentImp.setOverlay(ol_2);

					} else {
						currentImp.setOverlay(over);
						over = null;
					}
				} else
					IJ.showMessage("Please draw some cells on image");

			} else
				IJ.showMessage("Please open an image from drive");
		});

		// Show/hide category labels button
		JButton btnShowhideCategoryLabels = new JButton("Show/hide category labels");
		btnShowhideCategoryLabels.setForeground(new Color(0, 0, 0));
		btnShowhideCategoryLabels.setFont(new Font("Arial", Font.BOLD, 11));
		btnShowhideCategoryLabels.setBounds(19, 175, 186, 23);
		contentPane.add(btnShowhideCategoryLabels);
		btnShowhideCategoryLabels.addActionListener((ActionEvent e) -> {

			ImagePlus currentImp = WindowManager.getCurrentImage();
			if (currentImp != null) {
				if (currentImp.getOverlay() != null) {
					Overlay ol = currentImp.getOverlay();
					if (ol.getDrawLabels()) {
						ol.drawLabels(false);
						currentImp.setOverlay(ol);
					} else if (!ol.getDrawLabels()) {
						ol.drawLabels(true);
						currentImp.setOverlay(ol);
					}
				} else
					IJ.showMessage("Please draw some cells on image");
			} else
				IJ.showMessage("Please open an image from drive");
		});

		// Set overlay color button
		JButton btnSetOverlayColor = new JButton("Overlay color");
		btnSetOverlayColor.setForeground(new Color(0, 0, 0));
		btnSetOverlayColor.setFont(new Font("Arial", Font.BOLD, 11));
		btnSetOverlayColor.setBounds(19, 204, 125, 23);
		contentPane.add(btnSetOverlayColor);

		btnSetOverlayColor.addActionListener((ActionEvent e) -> {
			ImagePlus currentImp = WindowManager.getCurrentImage();
			if (currentImp != null) {
				Overlay ol = currentImp.getOverlay();
				if (ol != null) {
					menu = new JPopupMenu();
					// Create JMenuItems
					m1 = new JMenuItem("RED");
					m2 = new JMenuItem("GREEN");
					m3 = new JMenuItem("BLUE");
					m4 = new JMenuItem("WHITE");
					m5 = new JMenuItem("BLACK");
					m6 = new JMenuItem("CYAN");
					m7 = new JMenuItem("MAGENTA");
					m8 = new JMenuItem("YELLOW");

					// Add JMenuItems to JPopupMenu
					menu.add(m1);
					menu.add(m2);
					menu.add(m3);
					menu.add(m4);
					menu.add(m5);
					menu.add(m6);
					menu.add(m7);
					menu.add(m8);

					// Action listener of popup color list members
					m1.addActionListener((ActionEvent ae) -> {
						ol.setStrokeColor(Color.red);
						ol.setLabelColor(Color.red);
						currentImp.setOverlay(ol);
					});
					m2.addActionListener((ActionEvent ae) -> {
						ol.setStrokeColor(Color.green);
						ol.setLabelColor(Color.green);
						currentImp.setOverlay(ol);
					});

					m3.addActionListener((ActionEvent ae) -> {
						ol.setStrokeColor(Color.blue);
						ol.setLabelColor(Color.blue);
						currentImp.setOverlay(ol);
					});
					m4.addActionListener((ActionEvent ae) -> {
						ol.setStrokeColor(Color.white);
						ol.setLabelColor(Color.white);
						currentImp.setOverlay(ol);
					});
					m5.addActionListener((ActionEvent ae) -> {
						ol.setStrokeColor(Color.black);
						ol.setLabelColor(Color.black);
						currentImp.setOverlay(ol);
					});
					m6.addActionListener((ActionEvent ae) -> {
						ol.setStrokeColor(Color.cyan);
						ol.setLabelColor(Color.cyan);
						currentImp.setOverlay(ol);
					});
					m7.addActionListener((ActionEvent ae) -> {
						ol.setStrokeColor(Color.magenta);
						ol.setLabelColor(Color.magenta);
						currentImp.setOverlay(ol);
					});
					m8.addActionListener((ActionEvent ae) -> {
						ol.setStrokeColor(Color.yellow);
						ol.setLabelColor(Color.yellow);
						currentImp.setOverlay(ol);
					});

					showPopup(e);
				}
			}
		});

		// set overlay size button
		JButton btnSetOverlaySize = new JButton("Overlay size");
		btnSetOverlaySize.setForeground(new Color(0, 0, 0));
		btnSetOverlaySize.setFont(new Font("Arial", Font.BOLD, 11));
		btnSetOverlaySize.setBounds(19, 233, 125, 23);
		contentPane.add(btnSetOverlaySize);
		btnSetOverlaySize.addActionListener((ActionEvent e) -> {
			ImagePlus currentImp = WindowManager.getCurrentImage();
			if (currentImp != null) {
				Overlay ol = currentImp.getOverlay();
				if (ol != null) {
					SizeWindow sw = new SizeWindow();
					sw.setVisible(true);
					ActiveWidth = sw.getActiveWidth();
					ActiveHeight = sw.getActiveHeight();
				}
			}

		});

		// set overlay line thickness
		JButton btnSetOverlayLine = new JButton("Set overlay line thickness");
		btnSetOverlayLine.setForeground(new Color(0, 0, 0));
		btnSetOverlayLine.setFont(new Font("Arial", Font.BOLD, 11));
		btnSetOverlayLine.addActionListener((ActionEvent e) -> {

			ImagePlus currentImp = WindowManager.getCurrentImage();
			if (currentImp != null) {
				Overlay ol = currentImp.getOverlay();
				if (ol != null) {
					menu = new JPopupMenu();
					// Create JMenuItems
					m1 = new JMenuItem("1 pixel");
					m2 = new JMenuItem("2 pixels");
					m3 = new JMenuItem("3 pixels");
					m4 = new JMenuItem("4 pixels");
					m5 = new JMenuItem("5 pixels");
					m6 = new JMenuItem("6 pixels");
					m7 = new JMenuItem("7 pixels");
					m8 = new JMenuItem("8 pixels");

					// Add JMenuItems to JPopupMenu
					menu.add(m1);
					menu.add(m2);
					menu.add(m3);
					menu.add(m4);
					menu.add(m5);
					menu.add(m6);
					menu.add(m7);
					menu.add(m8);

					// Action listener of popup color list members
					m1.addActionListener((ActionEvent ae) -> {
						for (int i = 0; i < ol.size(); i++) {
							ol.get(i).setStrokeWidth(1);
						}
						currentImp.setOverlay(ol);
					});
					m2.addActionListener((ActionEvent ae) -> {
						for (int i = 0; i < ol.size(); i++) {
							ol.get(i).setStrokeWidth(2);
						}
						currentImp.setOverlay(ol);
					});

					m3.addActionListener((ActionEvent ae) -> {
						for (int i = 0; i < ol.size(); i++) {
							ol.get(i).setStrokeWidth(3);
						}
						currentImp.setOverlay(ol);
					});
					m4.addActionListener((ActionEvent ae) -> {
						for (int i = 0; i < ol.size(); i++) {
							ol.get(i).setStrokeWidth(4);
						}
						currentImp.setOverlay(ol);
					});
					m5.addActionListener((ActionEvent ae) -> {
						for (int i = 0; i < ol.size(); i++) {
							ol.get(i).setStrokeWidth(5);
						}
						currentImp.setOverlay(ol);
					});
					m6.addActionListener((ActionEvent ae) -> {
						for (int i = 0; i < ol.size(); i++) {
							ol.get(i).setStrokeWidth(6);
						}
						currentImp.setOverlay(ol);
					});
					m7.addActionListener((ActionEvent ae) -> {
						for (int i = 0; i < ol.size(); i++) {
							ol.get(i).setStrokeWidth(7);
						}
						currentImp.setOverlay(ol);
					});
					m8.addActionListener((ActionEvent ae) -> {
						for (int i = 0; i < ol.size(); i++) {
							ol.get(i).setStrokeWidth(8);
						}
						currentImp.setOverlay(ol);
					});

					showPopup(e);
				}
			}

		});
		btnSetOverlayLine.setBounds(223, 175, 221, 23);
		contentPane.add(btnSetOverlayLine);

		// save counts  button
		JButton btnSaveCountsTo = new JButton("Save counts");
		btnSaveCountsTo.setForeground(new Color(0, 0, 0));
		btnSaveCountsTo.setFont(new Font("Arial", Font.BOLD, 10));
		btnSaveCountsTo.setBounds(19, 262, 134, 23);
		contentPane.add(btnSaveCountsTo);
		btnSaveCountsTo.addActionListener((ActionEvent e) -> {
			if (WindowManager.getCurrentImage() != null) {
				if (WindowManager.getCurrentImage().getOverlay() != null)
					SSI.save();
				else
					IJ.showMessage("Please draw cells on image");
			} else
				IJ.showMessage("No image open.");

		});

		// Detailed counts table button
		JButton btnDetailedCounts = new JButton("Count information");
		btnDetailedCounts.setForeground(new Color(0, 0, 0));
		btnDetailedCounts.setFont(new Font("Arial", Font.BOLD, 10));
		btnDetailedCounts.addActionListener((ActionEvent e) -> {
			if (WindowManager.getCurrentImage() != null) {
				menu = new JPopupMenu();
				m1 = new JMenuItem("This image only - active counts:        Counts per category");
				m2 = new JMenuItem("This image only - active counts:        Counts per spesific category");
				m3 = new JMenuItem("This image only - saved info:           Detailed view (save to update)");
				m4 = new JMenuItem("All images in folder - saved info:      Detailed view (save to update)");
				m5 = new JMenuItem("All images in folder - saved info:      Counts per spesific category (save to update)");

				
				m1.addActionListener((ActionEvent ae) -> {
					Object[][] data = { { 1, label_1.getText() }, { 2, label_2.getText() }, { 3, label_3.getText() },
							{ 4, label_4.getText() }, { 5, label_5.getText() }, { 6, label_6.getText() },
							{ 7, label_7.getText() }, { 8, label_8.getText() } };

					table.CategoryCountTable(data);
				});
				m2.addActionListener((ActionEvent ae) -> {
						table.CategoryCountTable(getSimilarCellTableArray());
				});
				m3.addActionListener((ActionEvent ae) -> {
					table.LoadInformationTable();
				});
				m4.addActionListener((ActionEvent ae) -> {
					table.SummaryAcrossImages();
				});
				m5.addActionListener((ActionEvent ae) -> {
					table.SummaryAcrossImages_per_category();
				});
				
				
				menu.add(m1);
				menu.add(m2);
				menu.add(m3);
				menu.add(m4);
				menu.add(m5);
				showPopup(e);
			} else
				IJ.showMessage("Please open an image from drive");
		});
		btnDetailedCounts.setBounds(158, 262, 141, 23);
		contentPane.add(btnDetailedCounts);

		// Set origin and north button
		JButton btnSetOriginAnd = new JButton("Save origin and north (for 3d reconstruction)\r\n");
		btnSetOriginAnd.setForeground(new Color(0, 0, 0));
		btnSetOriginAnd.setFont(new Font("Arial", Font.BOLD, 11));
		btnSetOriginAnd.addActionListener((ActionEvent e) -> {
			if (WindowManager.getCurrentImage() != null)
				SOAN.RUN("");
			else
				IJ.showMessage("ERROR: No image opened. Please open an image and try again.");
		});
		btnSetOriginAnd.setBounds(157, 204, 287, 23);
		contentPane.add(btnSetOriginAnd);

		// Load counts button
		JButton btnLoad = new JButton("Load counts");
		btnLoad.setForeground(new Color(0, 0, 0));
		btnLoad.setFont(new Font("Arial", Font.BOLD, 10));
		btnLoad.setBounds(304, 262, 141, 23);
		btnLoad.addActionListener((ActionEvent e) -> {
			if (WindowManager.getCurrentImage() != null) {
				ClearCounts_LoadCounts_GenericAlert warn2 = new ClearCounts_LoadCounts_GenericAlert("Warning: Current counts will be discarded. Continue?", 2);
				warn2.setVisible(true);
				updateCounts();
			} else
				IJ.showMessage("Open an image with saved counts and try again.");
		});
		contentPane.add(btnLoad);

		// Draw contours button
		JButton btnNewButton_3 = new JButton("Save contours (for 3D reconstruction) ");
		btnNewButton_3.setForeground(new Color(0, 0, 0));
		btnNewButton_3.setFont(new Font("Arial", Font.BOLD, 11));
		btnNewButton_3.addActionListener((ActionEvent e) -> {
			if (WindowManager.getCurrentImage() != null)
				DC.RUN("");
			else
				IJ.showMessage("ERROR: No image opened. Please open an image and try again.");

		});
		btnNewButton_3.setBounds(157, 233, 287, 23);
		contentPane.add(btnNewButton_3);

		//////////////////////////
		//Information related with Auto Detect
		//////////////////////////
		JLabel lblAutoDetect = new JLabel("Automatic detection:");
		lblAutoDetect.setFont(new Font("Arial", Font.BOLD, 11));
		lblAutoDetect.setBackground(Color.WHITE);
		lblAutoDetect.setForeground(new Color(0, 0, 0));
		lblAutoDetect.setBounds(19, 298, 124, 14);
		contentPane.add(lblAutoDetect);

		JLabel lblPreBlurRadius = new JLabel("Pre blur radius:");
		lblPreBlurRadius.setForeground(new Color(0, 0, 128));
		lblPreBlurRadius.setFont(new Font("Arial", Font.BOLD, 10));
		lblPreBlurRadius.setBounds(19, 319, 83, 14);

		contentPane.add(lblPreBlurRadius);

		NumberFormat format = NumberFormat.getNumberInstance();
		format.setGroupingUsed(false);

		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setAllowsInvalid(false);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(true);

		blurRadiusButton =  new  JFormattedTextField(formatter);
		blurRadiusButton.setText("0");
		blurRadiusButton.setColumns(10);
		blurRadiusButton.setBounds(101, 316, 52, 20);
		contentPane.add(blurRadiusButton);

		JLabel lblNewLabel_1 = new JLabel("Noise tolerance:\r\n");
		lblNewLabel_1.setForeground(new Color(0, 0, 128));
		lblNewLabel_1.setFont(new Font("Arial", Font.BOLD, 10));
		lblNewLabel_1.setBounds(19, 350, 83, 14);
		contentPane.add(lblNewLabel_1);

		noiseToleranceButton = new JFormattedTextField(formatter);
		noiseToleranceButton.setText("50");
		noiseToleranceButton.setColumns(10);
		noiseToleranceButton.setBounds(101, 347, 52, 20);
		contentPane.add(noiseToleranceButton);

		JLabel lblColorToDetect = new JLabel("Color to detect:");
		lblColorToDetect.setForeground(new Color(0, 0, 128));
		lblColorToDetect.setFont(new Font("Arial", Font.BOLD, 10));
		lblColorToDetect.setBounds(278, 319, 83, 14);
		contentPane.add(lblColorToDetect);

		String[] detectColorValues = { "Any", "Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "Gray" };
		detectColorButton = new JComboBox(detectColorValues);
		detectColorButton.setBounds(381, 316, 63, 20);
		contentPane.add(detectColorButton);

		JLabel lblNewLabel_2 = new JLabel("Exclude on edges:\r\n");
		lblNewLabel_2.setForeground(new Color(0, 0, 128));
		lblNewLabel_2.setFont(new Font("Arial", Font.BOLD, 10));
		lblNewLabel_2.setBounds(160, 319, 92, 14);
		contentPane.add(lblNewLabel_2);

		excludeOnEdgesButton = new JCheckBox("");
		excludeOnEdgesButton.setBackground(new Color(192, 192, 192));
		excludeOnEdgesButton.setBounds(249, 313, 21, 23);
		contentPane.add(excludeOnEdgesButton);

		JLabel lblNewLabel_3 = new JLabel("Light background:");
		lblNewLabel_3.setForeground(new Color(0, 0, 128));
		lblNewLabel_3.setFont(new Font("Arial", Font.BOLD, 10));
		lblNewLabel_3.setBounds(160, 350, 92, 14);
		contentPane.add(lblNewLabel_3);

		lightBackgroundButton = new JCheckBox();
		lightBackgroundButton.setBackground(new Color(192, 192, 192));
		lightBackgroundButton.setBounds(249, 343, 21, 23);
		contentPane.add(lightBackgroundButton);

		JLabel lblOnlyInsideOutlines = new JLabel("Only inside outlines:");
		lblOnlyInsideOutlines.setForeground(new Color(0, 0, 128));
		lblOnlyInsideOutlines.setFont(new Font("Arial", Font.BOLD, 10));
		lblOnlyInsideOutlines.setBounds(278, 350, 101, 14);
		contentPane.add(lblOnlyInsideOutlines);

		String[] detectInsideColorValues = { "Disable", "Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "White" };
		detectInsideColorButton = new JComboBox(detectInsideColorValues);
		detectInsideColorButton.setBounds(381, 347, 63, 20);
		contentPane.add(detectInsideColorButton);
		
		JLabel lblNewLabel_4 = new JLabel("For 3D only: radius xy:\r\n");
		lblNewLabel_4.setForeground(new Color(0, 0, 128));
		lblNewLabel_4.setFont(new Font("Arial", Font.BOLD, 10));
		lblNewLabel_4.setBounds(19, 375, 120, 14);
		contentPane.add(lblNewLabel_4);
		
		radiusxyButton = new JFormattedTextField(formatter);
		radiusxyButton.setText("1");
		radiusxyButton.setColumns(10);
		radiusxyButton.setBounds(133, 375, 52, 20);
		contentPane.add(radiusxyButton);
		
		JLabel lblNewLabel_5 = new JLabel("For 3D only: radius z:\r\n");
		lblNewLabel_5.setForeground(new Color(0, 0, 128));
		lblNewLabel_5.setFont(new Font("Arial", Font.BOLD, 10));
		lblNewLabel_5.setBounds(200, 375, 120, 14);
		contentPane.add(lblNewLabel_5);
		
		radiuszButton = new JFormattedTextField(formatter);
		radiuszButton.setText("1");
		radiuszButton.setColumns(10);
		radiuszButton.setBounds(330, 375, 52, 20);
		contentPane.add(radiuszButton);
		

		JLabel lblNewLabel_6 = new JLabel("For 3D only: ignore top slice:\r\n");
		lblNewLabel_6.setForeground(new Color(0, 0, 128));
		lblNewLabel_6.setFont(new Font("Arial", Font.BOLD, 10));
		lblNewLabel_6.setBounds(19, 400, 145, 20);
		contentPane.add(lblNewLabel_6);
		
		ignoreTopSlice = new JCheckBox("");
		ignoreTopSlice.setBackground(new Color(192, 192, 192));
		ignoreTopSlice.setBounds(170, 400, 21, 23);
		contentPane.add(ignoreTopSlice);
		
		JLabel lblNewLabel_7 = new JLabel("Show filtered output:\r\n");
		lblNewLabel_7.setForeground(new Color(0, 0, 128));
		lblNewLabel_7.setFont(new Font("Arial", Font.BOLD, 10));
		lblNewLabel_7.setBounds(320, 400, 145, 20);
		contentPane.add(lblNewLabel_7);
		
		showOutput = new JCheckBox("");
		showOutput.setBackground(new Color(192, 192, 192));
		showOutput.setBounds(426, 400, 21, 23);
		contentPane.add(showOutput);
		
		//////////////////////////
		//Auto Detect Excecute buttons (2d/3d):
		//////////////////////////
		
		// Button Find maxima (as multipoints)
		JButton btnNewButton_1 = new JButton("Find 2D maxima (as multipoints)");
		btnNewButton_1.setForeground(new Color(0, 0, 0));
		btnNewButton_1.setFont(new Font("Arial", Font.BOLD, 11));
		btnNewButton_1.setBounds(19, 430, 212, 23);
		contentPane.add(btnNewButton_1);
		btnNewButton_1.addActionListener((ActionEvent e) -> {
			disableNewImageWarnings = true;
			AutoDetect ad = new AutoDetect();
			if (ad.error == false) {
				ad.findMaxima("2d");
				ad = null;
				System.gc();
				disableNewImageWarnings = false;
			}
		});
		


		// Button 3D object detect (as multipoints)
		JButton detect3DBtn = new JButton("Find 3D maxima (as multipoints)");
		detect3DBtn.setForeground(new Color(0, 0, 0));
		detect3DBtn.setFont(new Font("Arial", Font.BOLD, 11));
		detect3DBtn.setBounds(235, 430, 212, 23);
		contentPane.add(detect3DBtn);
		detect3DBtn.addActionListener((ActionEvent e) -> {
			AutoDetect ad = new AutoDetect();
			if (ad.error == false) {
				ad.findMaxima("3d");
				ad = null;
				System.gc();
				//Show warning if "Associate with slices" is not enabled. Only show once.
				if (wasShown_find3DWarning == false && multipointsInSlices.isSelected() == false) {
					IJ.showMessage("WARNING: To see multipoint 3D positions, enable \"Associate with slices: Overlays and Multipoints\" checkboxes, in the plugin menu (near the top)"); 		
					wasShown_find3DWarning = true;
				}
			}
		});
		
		
		
		// clear multipoints button
		JButton btnClearMultipoints = new JButton("Clear multipoints");
		btnClearMultipoints.setForeground(new Color(0, 0, 0));
		btnClearMultipoints.setFont(new Font("Arial", Font.BOLD, 11));
		btnClearMultipoints.setBounds(19, 422  + added_height_space, 131, 23);
		btnClearMultipoints.addActionListener((ActionEvent e) -> {
			ImagePlus currentImp = WindowManager.getCurrentImage();
			if (currentImp != null) {
				IJ.run(currentImp, "Select None", "");
			}
		});
		contentPane.add(btnClearMultipoints);

		// Reset all counts button
		JButton btnResetAllCounts = new JButton("Reset all count");
		btnResetAllCounts.setForeground(new Color(0, 0, 0));
		btnResetAllCounts.setFont(new Font("Arial", Font.BOLD, 11));
		btnResetAllCounts.setBounds(19, 455  + added_height_space, 150, 23);
		btnResetAllCounts.addActionListener((ActionEvent e) -> {
			if (WindowManager.getCurrentImage() != null) {
				if (WindowManager.getCurrentImage().getOverlay() != null) {
					ClearCounts_LoadCounts_GenericAlert warn = new ClearCounts_LoadCounts_GenericAlert("Warning: Current counts will be discarded. Continue?", 1);
					warn.setVisible(true);
					updateCounts();
					saveAndDisplayEvent(WindowManager.getCurrentImage(), "Pressed: Reset all count");
				}
			}
		});
		contentPane.add(btnResetAllCounts);

		// Toggle multipoints inside/outside/all-overlays button
		JButton btnNewButton_2 = new JButton("Toggle multipoints inside/outside/all overlays");
		btnNewButton_2.setForeground(new Color(0, 0, 0));
		btnNewButton_2.setFont(new Font("Arial", Font.BOLD, 11));
		btnNewButton_2.setBounds(157, 422  + added_height_space, 287, 23);
		btnNewButton_2.addActionListener((ActionEvent e) -> {
			if (WindowManager.getCurrentImage() == null) {
				IJ.showMessage("Please open an image from drive");
				return;
			}
			if (WindowManager.getCurrentImage().getRoi() == null) {
				IJ.showMessage("Please draw mulipoints on image");
				return;
			}
			if (WindowManager.getCurrentImage().getOverlay() == null) {
				IJ.showMessage("No overlay drawn yet");
				return;
			}
			//Log button press
			saveAndDisplayEvent(WindowManager.getCurrentImage(), "Pressed: Toggle multipoints inside/outside/all overlays");
			// Checking whether new multipoint is created or not during toggle
			if (inside != null || outside != null) {
				ImagePlus currentImp = WindowManager.getCurrentImage();
				int[] xpoints = currentImp.getRoi().getPolygon().xpoints;
				if (toggler == 1 && inside.size() != xpoints.length) {
					toggler = 0;
					inside = null;
					outside = null;
				} else if (toggler == 2 && outside.size() != xpoints.length) {
					toggler = 0;
					inside = null;
					outside = null;
				}
			}
			switch (toggler) {
			case 0: {
				ImagePlus currentImp = WindowManager.getCurrentImage();
				int[] xpoints = currentImp.getRoi().getPolygon().xpoints;
				int[] ypoints = currentImp.getRoi().getPolygon().ypoints;
				PointRoi currentRois = (PointRoi) currentImp.getRoi(); //To get Z values of multipoints
				currentImp.saveRoi();
				inside = new ArrayList<>();
				outside = new ArrayList<>();
				//IJ.run(currentImp, "Select None", "");
				Overlay ol = currentImp.getOverlay();
				int g = ol.size();
				for (int i = 0; i < xpoints.length; i++) {
					int pointZPos;
					if (currentImp.getNSlices() == 1) {
						pointZPos = 1;
					} else {
						pointZPos = currentImp.convertIndexToPosition(currentRois.getPointPosition(i))[1];
					}
					int y = 0;
					for (int j = 0; j < g; j++) {
						int olPos = ol.get(j).getZPosition();
						if (olPos == 0 || olPos == pointZPos) {
							if (ol.get(j).contains(xpoints[i], ypoints[i])) {
								inside.add(xpoints[i] + " " + ypoints[i] + " " + pointZPos);
								y++;
							}
						}
					}
					if (y == 0) {
						outside.add(xpoints[i] + " " + ypoints[i] + " " + pointZPos);
					}
				}
				float[] xpoint = new float[inside.size()];
				float[] ypoint = new float[inside.size()];
				float[] zpoint = new float[inside.size()];
				for (int i = 0; i < inside.size(); i++) {
					StringTokenizer r = new StringTokenizer(inside.get(i), " ");
					xpoint[i] = Float.parseFloat(r.nextToken());
					ypoint[i] = Float.parseFloat(r.nextToken());
					zpoint[i] = Float.parseFloat(r.nextToken());
				}
				PointRoi multipoints = new PointRoi();
				for (int i=0; i<xpoint.length; i++) {
					currentImp.setZ((int) zpoint[i]+1);
					multipoints.addUserPoint(currentImp, xpoint[i], ypoint[i]);
				}
				currentImp.setRoi(multipoints);
				//For setting pointer apperances as selected in the pointer preferences, since they easily reset when programmaticaly created
				setPointerApperanceSettings(currentImp);
				toggler = 1;
				break;
			}
			case 1: {
				ImagePlus currentImp = WindowManager.getCurrentImage();
				IJ.run(currentImp, "Select None", "");
				float[] xpoint = new float[outside.size()];
				float[] ypoint = new float[outside.size()];
				float[] zpoint = new float[outside.size()];
				for (int i = 0; i < outside.size(); i++) {
					StringTokenizer r = new StringTokenizer(outside.get(i), " ");
					xpoint[i] = Float.parseFloat(r.nextToken());
					ypoint[i] = Float.parseFloat(r.nextToken());
					zpoint[i] = Float.parseFloat(r.nextToken());
				}
				PointRoi multipoints = new PointRoi();
				for (int i=0; i<xpoint.length; i++) {
					currentImp.setZ((int) zpoint[i]+1);
					multipoints.addUserPoint(currentImp, xpoint[i], ypoint[i]);
				}
				currentImp.setRoi(multipoints);
				//For setting pointer apperances as selected in the pointer preferences, since they easily reset when programmaticaly created
				setPointerApperanceSettings(currentImp);
				toggler = 2;
				break;
			}
			case 2: {
				ImagePlus currentImp = WindowManager.getCurrentImage();
				IJ.run(currentImp, "Select None", "");
				ArrayList<String> All = new ArrayList<>();
				All.addAll(inside);
				All.addAll(outside);
				float[] xpoint = new float[All.size()];
				float[] ypoint = new float[All.size()];
				float[] zpoint = new float[All.size()];
				for (int i = 0; i < All.size(); i++) {
					StringTokenizer r = new StringTokenizer(All.get(i), " ");
					xpoint[i] = Float.parseFloat(r.nextToken());
					ypoint[i] = Float.parseFloat(r.nextToken());
					zpoint[i] = Float.parseFloat(r.nextToken());
				}
				PointRoi multipoints = new PointRoi();
				for (int i=0; i<xpoint.length; i++) {
					currentImp.setZ((int) zpoint[i]+1);
					multipoints.addUserPoint(currentImp, xpoint[i], ypoint[i]);
				}
				currentImp.setRoi(multipoints);
				//For setting pointer apperances as selected in the pointer preferences, since they easily reset when programmaticaly created
				setPointerApperanceSettings(currentImp);
				toggler = 0;
				break;
			}
			default:
				break;
			}
		});
		contentPane.add(btnNewButton_2);

		// button 27 (Convert multipoints to overlays)
		JButton btnConvertMulitipointTo = new JButton("Convert mulitipoint to overlays (using current mode)");
		btnConvertMulitipointTo.setForeground(new Color(0, 0, 0));
		btnConvertMulitipointTo.setBackground(new Color(60, 179, 113));
		btnConvertMulitipointTo.setFont(new Font("Arial", Font.BOLD, 10));
		btnConvertMulitipointTo.addActionListener((ActionEvent e) -> {
			

			//try {
				if (WindowManager.getWindow("Filtered output") != null) { //output from "Show filtered output"
					WindowManager.getImage("Filtered output").close(); 
					}
				if(imageNotSaved(WindowManager.getCurrentImage())) { //Displays error msg if not saved
					return;
				}

				if (WindowManager.getCurrentImage().getRoi() == null) {
					IJ.showMessage("Please draw mulipoints on image");
					return;
				}
				
				if (rdbtnSetCatagory.isSelected() == false && rdbtnAddCata.isSelected() == false
						&& rdbtnDeleteCell.isSelected() == false) {
					IJ.showMessage("please select a mode");
				} else if (!checkBox_1.isSelected() && !checkBox_2.isSelected() && !checkBox_3.isSelected()
						&& !checkBox_4.isSelected() && !checkBox_5.isSelected() && !checkBox_6.isSelected()
						&& !checkBox_7.isSelected() && !checkBox_8.isSelected()) {
					IJ.showMessage("please select a category");
				} else {
					// Save scaling info
					SaveScalingInfo.save();
					//Output embedded jar files (exel and matlab files) if not present
					try {
						OutputEmbeddedJarFiles.save();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					ImagePlus currentImp = WindowManager.getCurrentImage();
					if (currentImp.getOverlay() == null) {
						if (!(IJ.getVersion().compareTo("1.51v18") < 0)) {
							Category.initializeOverlay(selectedCategory(), Selectable.isSelected(), ActiveWidth,
									ActiveHeight);
						} else {
							Category.initializeOverlay(selectedCategory(), false, ActiveWidth, ActiveHeight);
						}
						updateCounts();
					} else if (rdbtnSetCatagory.isSelected() == true && rdbtnAddCata.isSelected() == false
							&& rdbtnDeleteCell.isSelected() == false) {
						currentImp = WindowManager.getCurrentImage();
						int[] ypoints = currentImp.getRoi().getPolygon().ypoints;
						int[] xpoints = currentImp.getRoi().getPolygon().xpoints;
						PointRoi currentRois = (PointRoi) currentImp.getRoi(); //To get Z values of multipoints
						Overlay ol = currentImp.getOverlay();
						int g = ol.size();
						for (int i = 0; i < xpoints.length; i++) {
							int pointZPos;
							if (currentImp.getNSlices() == 1) {
								pointZPos = 1;
							} else {
								pointZPos = currentImp.convertIndexToPosition(currentRois.getPointPosition(i))[1];
							}
							int y = 0;
							for (int j = 0; j < g; j++) {
								int olPos = ol.get(j).getZPosition();
								if (olPos == 0 || olPos == pointZPos) {
									if (ol.get(j).contains(xpoints[i], ypoints[i])) { //If point tied to existing overlay
										ol.get(j).setName(selectedCategory()); //Rename existing overlay
										y++;
									}
								}
							}
							if (y == 0) { //If point NOT tied to existing overlay
								int xpoint = xpoints[i] - (int) (ActiveWidth / 2);
								int ypoint = ypoints[i] - (int) (ActiveHeight / 2);
								OvalRoi r = new OvalRoi(xpoint, ypoint, ActiveWidth, ActiveHeight);
								r.setName(selectedCategory());
								if (overlayInSlices.isSelected()) {
									if (currentImp.isHyperStack()) {
										r.setPosition(0, pointZPos, 0); //Display on all channels and frames, but tied to slice
									} else { 
										r.setPosition(pointZPos);
									}
								}
								r.setProperty("customZ", Integer.toString(pointZPos));
								ol.add(r); //Add new overlay
							}
						}
						ol.setStrokeColor(ol.getLabelColor());
						currentImp.setOverlay(ol);
						updateCounts();
					} else if (rdbtnSetCatagory.isSelected() == false && rdbtnAddCata.isSelected() == true
							&& rdbtnDeleteCell.isSelected() == false) {

						currentImp = WindowManager.getCurrentImage();
						int[] ypoints = currentImp.getRoi().getPolygon().ypoints;
						int[] xpoints = currentImp.getRoi().getPolygon().xpoints;
						PointRoi currentRois = (PointRoi) currentImp.getRoi(); //To get Z values of multipoints
						Overlay ol = currentImp.getOverlay();
						int g = ol.size();

						for (int i = 0; i < xpoints.length; i++) {
							int pointZPos;
							if (currentImp.getNSlices() == 1) {
								pointZPos = 1;
							} else {
								pointZPos = currentImp.convertIndexToPosition(currentRois.getPointPosition(i))[1];
							}
							int y = 0;
							for (int j = 0; j < g; j++) {
								int olPos = ol.get(j).getZPosition();
								if (olPos == 0 || olPos == pointZPos) { //If slice agnostic, or Z matches point<->overlay
									if (ol.get(j).contains(xpoints[i], ypoints[i])) { //If point tied to existing overlay
										String newCata = null;
										String a = selectedCategory();
										StringTokenizer st = new StringTokenizer(a, ",");
										int f = 0;
										while (st.hasMoreTokens()) {
											String c = st.nextToken();
											String c2 = ol.get(j).getName();
											StringTokenizer st2 = new StringTokenizer(c2, ",");
											int nf = 0;
											while (st2.hasMoreTokens()) {
												if (st2.nextToken().equals(c))
													nf++;
											}
											if (nf == 0) {
												if (newCata == null)
													newCata = c;
												else
													newCata = newCata + "," + c;
												f++;
	
											}
										}
										if (f != 0) {
											a = ol.get(j).getName() + "," + newCata;
											//Sort categories to ascending order
											String[] aUnsorted = a.split(",");
											Arrays.sort(aUnsorted);
											a = String.join(",", aUnsorted);
											//Set new name
											ol.get(j).setName(a); //Rename existing overlay
										}
										y++;
									}
								}
							}
							if (y == 0) { //If point NOT tied to existing overlay
								int xpoint = xpoints[i] - (int) (ActiveWidth / 2);
								int ypoint = ypoints[i] - (int) (ActiveHeight / 2);
								OvalRoi r = new OvalRoi(xpoint, ypoint, ActiveWidth, ActiveHeight);
								r.setName(selectedCategory());
								if (overlayInSlices.isSelected()) {
									if (currentImp.isHyperStack()) {
										r.setPosition(0, pointZPos, 0); //Display on all channels and frames, but tied to slice
									} else { 
										r.setPosition(pointZPos);
									}
								}
								r.setProperty("customZ", Integer.toString(pointZPos));
								ol.add(r); //Add new overlay
							}
						}
						ol.setStrokeColor(ol.getLabelColor());
						currentImp.setOverlay(ol);
						updateCounts();
					} else if (rdbtnSetCatagory.isSelected() == false && rdbtnAddCata.isSelected() == false
							&& rdbtnDeleteCell.isSelected() == true) {
						currentImp = WindowManager.getCurrentImage();
						int[] ypoints = currentImp.getRoi().getPolygon().ypoints;
						int[] xpoints = currentImp.getRoi().getPolygon().xpoints;
						PointRoi currentRois = (PointRoi) currentImp.getRoi(); //To get Z values of multipoints
						Overlay ol = currentImp.getOverlay();
						for (int i = 0; i < xpoints.length; i++)
							for (int j = 0; j < ol.size(); j++) {
								if (ol.get(j).contains(xpoints[i], ypoints[i])) {
									int olPos = ol.get(j).getZPosition();
									int pointZPos;
									if (currentImp.getNSlices() == 1) {
										pointZPos = 1;
									} else {
										pointZPos = currentImp.convertIndexToPosition(currentRois.getPointPosition(i))[1];
									}
									if (olPos == 0 || olPos == pointZPos) {
										ol.remove(j);
									}
								}
							}
						currentImp.setOverlay(ol);
						updateCounts();
					}
				}
			//Build log string:
			String currentMode = "";
			if (rdbtnSetCatagory.isSelected()) {
				currentMode = "Set Category";
			}
			if (rdbtnAddCata.isSelected()) {
				currentMode = "Add Category";
			}
			if (rdbtnDeleteCell.isSelected()) {
				currentMode = "Delete Cell";
			}
			
			String a = selectedCategory();
			String tmp = "Markers converted to overlays/cells. Current category = " + a + ", Current mode = " + currentMode;
			saveAndDisplayEvent(WindowManager.getCurrentImage(), tmp);
			
			//} catch (Exception ie) {
			//	IJ.showMessage("Please open an image from drive and draw multi-points to use plug-in");
			//}
		});
		btnConvertMulitipointTo.setBounds(19, 485 + added_height_space, 291, 23);
		contentPane.add(btnConvertMulitipointTo);
		
		//Button "show log"
		JButton btnShowLog = new JButton("Show log");
		btnShowLog.setForeground(new Color(0, 0, 0));
		btnShowLog.setFont(new Font("Arial", Font.BOLD, 11));
		btnShowLog.setBounds(360, 485 + added_height_space, 85, 23);
		btnShowLog.addActionListener((ActionEvent e) -> {
			/*Pseudocode:
			if click and not open:
				find path for current file
				if no log file:
					create empty log file (File created XXX datetime)
				read log file
				show text window and print all content
			if click and open:
				do nothing
			 */
			if (WindowManager.getCurrentImage() == null) {
				IJ.showMessage("Please open an image from drive");
				return;
			}
			if (WindowManager.getWindow("Counting log") == null) { //if log window is not open
				File logFilePath = createLogFileIfAbsent(WindowManager.getCurrentImage());
				try {
					//Read log file and print to "Counting log" window
					String content = new String(Files.readAllBytes(Paths.get(logFilePath.toURI())));
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					int screenHeight = screenSize.height;
					int screenWidth = screenSize.width;
					TextWindow countingLogWindow = new ij.text.TextWindow("Counting log", content, (int) (screenWidth / 2), (int) (screenWidth / 2));
				}
				catch (IOException e1) {
					e1.printStackTrace();
				} 
			}
		});
		contentPane.add(btnShowLog);

		
		
		// button .. (Make multipoints in all overlays)
		JButton btnMultipointsInOverlays = new JButton("Make multipoints in all overlays");
		btnMultipointsInOverlays.setForeground(new Color(0, 0, 0));
		btnMultipointsInOverlays.setFont(new Font("Arial", Font.BOLD, 11));
		btnMultipointsInOverlays.setBounds(167, 422 + added_height_space, 277, 23);
		btnMultipointsInOverlays.addActionListener((ActionEvent e) -> {
			if (WindowManager.getCurrentImage() == null) {
				IJ.showMessage("Please open an image from drive");
				return;
			}
			if (WindowManager.getCurrentImage().getOverlay() == null) {
				IJ.showMessage("No cell drawn yet");
				return;
			}
			ImagePlus currentImp = WindowManager.getCurrentImage();
			IJ.run(currentImp, "Select None", "");
			Overlay ol = currentImp.getOverlay();
			float[] xpoints = new float[ol.size()];
			float[] ypoints = new float[ol.size()];
			int[] zpoints = new int[ol.size()];
			for (int i = 0; i < ol.size(); i++) {
				xpoints[i] = (float) ol.get(i).getXBase() + ActiveWidth/2;
				ypoints[i] = (float) ol.get(i).getYBase() + ActiveHeight/2;
				zpoints[i] = (int) ol.get(i).getZPosition();
			}
			PointRoi multipoints = new PointRoi();
			//Add multipont to current Z in image
			for (int i=0; i<xpoints.length; i++) {
				currentImp.setZ((int) zpoints[i]);
				multipoints.addUserPoint(currentImp, xpoints[i], ypoints[i]);
			}
			currentImp.setRoi(multipoints);
			//For setting pointer apperances as selected in the pointer preferences, since they easily reset when programmaticaly created
			setPointerApperanceSettings(currentImp);
			saveAndDisplayEvent(currentImp, "Pressed: Make multipoints in all overlays. " + multipoints.size() + " multipoints created");
		});

		btnMultipointsInOverlays.setBounds(173, 455 + added_height_space, 271, 23);
		contentPane.add(btnMultipointsInOverlays);

		JLabel label_9 = new JLabel("_______________________________________________________________________________");
		label_9.setForeground(new Color(105, 105, 105));
		label_9.setBounds(0, 127, 498, 14);
		contentPane.add(label_9);

		JLabel label_10 = new JLabel("______________________________________________________________________________");
		label_10.setForeground(new Color(105, 105, 105));
		label_10.setBounds(0, 278, 498, 20);
		contentPane.add(label_10);

		JLabel label_11 = new JLabel("______________________________________________________________________________");
		label_11.setForeground(new Color(128, 128, 128));
		label_11.setBounds(0, 397 + added_height_space, 498, 14);
		contentPane.add(label_11);

		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 469, 553 + added_height_space);
		setContentPane(contentPane);
		setResizable(false);
		setTitle("Colocalization cell counter");

	}

	public static void setPointerApperanceSettings(ImagePlus inputImp) {
		//ImagePlus currentImp = WindowManager.getCurrentImage();
		if (inputImp != null) {
			Roi r = inputImp.getRoi();
			if (r != null) {
				if (r.getTypeAsString().equals("Point")) {
					PointRoi rr = (PointRoi) r;
					rr.setSize(PointRoi.getDefaultSize());
					rr.setPointType(PointRoi.getDefaultType());
					rr.setColor(PointRoi.getColor());
				}		
			}
		}
	}
	

	// Method for getting selected category
	public String selectedCategory() {
		String a = null;
		int i = 0;
		if (checkBox_1.isSelected()) {
			if (i == 0) {
				a = "1";
				i++;
			} else
				a = a + ",1";
		}
		if (checkBox_2.isSelected()) {
			if (i == 0) {
				a = "2";
				i++;
			} else
				a = a + ",2";
		}
		if (checkBox_3.isSelected()) {
			if (i == 0) {
				a = "3";
				i++;
			} else
				a = a + ",3";
		}
		if (checkBox_4.isSelected()) {
			if (i == 0) {
				a = "4";
				i++;
			} else
				a = a + ",4";
		}
		if (checkBox_5.isSelected()) {
			if (i == 0) {
				a = "5";
				i++;
			} else
				a = a + ",5";
		}
		if (checkBox_6.isSelected()) {
			if (i == 0) {
				a = "6";
				i++;
			} else
				a = a + ",6";
		}
		if (checkBox_7.isSelected()) {
			if (i == 0) {
				a = "7";
				i++;
			} else
				a = a + ",7";
		}
		if (checkBox_8.isSelected()) {
			if (i == 0) {
				a = "8";
				i++;
			} else
				a = a + ",8";
		}
		return a;

	}

	private void showPopup(ActionEvent ae) {
		Component b = (Component) ae.getSource();
		Point p = b.getLocationOnScreen();
		menu.show(this, 0, 0);
		menu.setLocation(p.x, p.y + b.getHeight());
	}

	// Method for updating counts
	public void updateCounts() {
		label_1.setText("0");
		label_2.setText("0");
		label_3.setText("0");
		label_4.setText("0");
		label_5.setText("0");
		label_6.setText("0");
		label_7.setText("0");
		label_8.setText("0");
		ImagePlus currentImp = WindowManager.getCurrentImage();
		Overlay ol = currentImp.getOverlay();
		if (ol == null)
			return;
		for (int i = 0; i < ol.size(); i++) {
			String a = ol.get(i).getName();
			StringTokenizer st = new StringTokenizer(a, ",");
			while (st.hasMoreTokens()) {
				int count = Integer.parseInt(st.nextToken());
				switch (count) {
				case 1: {
					int label = Integer.parseInt(label_1.getText()) + 1;
					label_1.setText(String.valueOf(label));
					break;
				}
				case 2: {
					int label = Integer.parseInt(label_2.getText()) + 1;
					label_2.setText(String.valueOf(label));
					break;
				}
				case 3: {
					int label = Integer.parseInt(label_3.getText()) + 1;
					label_3.setText(String.valueOf(label));
					break;
				}

				case 4: {
					int label = Integer.parseInt(label_4.getText()) + 1;
					label_4.setText(String.valueOf(label));
					break;
				}
				case 5: {
					int label = Integer.parseInt(label_5.getText()) + 1;
					label_5.setText(String.valueOf(label));
					break;
				}
				case 6: {
					int label = Integer.parseInt(label_6.getText()) + 1;
					label_6.setText(String.valueOf(label));
					break;
				}
				case 7: {
					int label = Integer.parseInt(label_7.getText()) + 1;
					label_7.setText(String.valueOf(label));
					break;
				}
				case 8: {
					int label = Integer.parseInt(label_8.getText()) + 1;
					label_8.setText(String.valueOf(label));
					break;
				}
				default:
					break;
				}
			}

		}
	}
	// For table 2
	public Object[][] getSimilarCellTableArray() {
		ImagePlus currentImp = WindowManager.getCurrentImage();
		Overlay ol = currentImp.getOverlay();
		ArrayList<String> arr = new ArrayList<>();
		for (int i = 0; i < ol.size(); i++) {
			String a = ol.get(i).getName();
			String h = a.replaceAll(",", "");
			arr.add(sortString(h));
		}
		ArrayList<Integer> count = new ArrayList<>();
		ArrayList<String> categories = new ArrayList<>();
		for (int i = 0; i < arr.size(); i++) {
			if (!arr.get(i).equals("-1")) {
				count.add(Collections.frequency(arr, arr.get(i)));
				categories.add(arr.get(i));
				Collections.replaceAll(arr, arr.get(i), "-1");
			}
		}
		Object[][] data = new Object[categories.size()][2];
		for (int i = 0; i < categories.size(); i++) {
			data[i][0] = categories.get(i);
			data[i][1] = count.get(i);
		}
		return data;
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
	

	public static boolean imageNotSaved(ImagePlus img) {
		if(img == null) {
			IJ.showMessage("Error: No image open");
			return true;
		}
		else if(img.getOriginalFileInfo().directory == null || img.getOriginalFileInfo().directory == "") {
			IJ.showMessage("Error: This image is not saved to a hard drive. Must save to disk before plugin can be used.");
			return true;
		} else {
			return false;
		}
	}
	

	
	public static File createLogFileIfAbsent (ImagePlus inputImp) {
		try {
			//Get path of potential log file
			ImagePlus currentImp = inputImp; //WindowManager.getCurrentImage();
			String rootPath = currentImp.getOriginalFileInfo().directory;
			File logFilePath = new File(rootPath + "/Counts/Counting_log.txt");
			//Create file if it doesnt exist
			logFilePath.getParentFile().mkdirs(); 
			if (!logFilePath.exists()) {
				DateTimeFormatter timeStampPattern = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM); //ofPattern("yyyy MM dd HH:mm:ss");
		        String timestamp = timeStampPattern.format(java.time.LocalDateTime.now());
				String logTextHeading = "Counting log file created " + timestamp + " - for images in path: " + rootPath;
				logTextHeading = logTextHeading + System.lineSeparator() + "File is automatically updated on every event, and saved to " + rootPath + "Counting_log.txt";
				FileWriter fr;
				fr = new FileWriter(logFilePath, true);
				fr.write(logTextHeading + System.lineSeparator());
				fr.close();
			}
			return logFilePath;
		}
		catch (IOException e1) {
			IJ.log("Error: Could not save log file. Is this image saved to hard drive? \n(code: saveAndDisplayEvent)");
			e1.printStackTrace();
		}
		return null; 
	}
	

	
	public static void saveAndDisplayEvent(ImagePlus inputImp, String eventString) {
		/*
		an image window must be open (i think this is haldeled from before)
		logfilepath = createLogFileIfAbsent
		create eventStringPrefix
		append eventString to eventStringPrefix
		append to logFilePath
		if logwindow open:
			print to new line
		*/
		
		//try {
			//ImagePlus inputImp = WindowManager.getCurrentImage();
			if(imageNotSaved(inputImp)) { //Displays error msg if not saved
				return;
			}
			File logFilePath = UserInterface.createLogFileIfAbsent(inputImp);
			
	
			//Make infostring
			String infoString = ""; //[timepoint] [filename] [img channel] [img slice] [event type]
			//Add date
			DateTimeFormatter timeStampPattern = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM); //ofPattern("yyyy MM dd HH:mm:ss");
	        String timestamp = timeStampPattern.format(java.time.LocalDateTime.now());
	        infoString = infoString + "[" + timestamp + "]" + ", ";
	        
	        //Add filename
	        infoString = infoString + "[" + inputImp.getTitle() + "]" + ", ";
	
	        //Add active channel
	        infoString = infoString + "[" +  "ActiveChannel:" + inputImp.getChannel() + "]" + ", ";
	        
	        //Add active slice
	        infoString = infoString + "[" + "ActiveSlice:" + inputImp.getSlice() + "]" + ", ";
	        
	        //Add event information
	        infoString =  infoString + "[" + eventString + "]";
	        
	        //Add newline
	        infoString = infoString + System.lineSeparator();
	        
	        //Write to logfile:
	        try {
	        FileWriter fr;
			fr = new FileWriter(logFilePath, true);
			fr.write(infoString);
			fr.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	        //Print to Counting log window if open
	        Window logWindow = WindowManager.getWindow("Counting log");
	        if (logWindow != null) {
	        	TextWindow tw = (TextWindow) logWindow;
	        	tw.append(infoString);
	        }
	        
		//} catch (Exception e){
		//	IJ.showMessage("Error: Could not save log file. Is this image saved to hard drive? \n(code: saveAndDisplayEvent)");
		//	return;
		//}
	}
	

	

}
