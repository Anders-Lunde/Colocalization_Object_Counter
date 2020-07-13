import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.OvalRoi;
import ij.gui.Overlay;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.Label;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
/**
 *
 * @author M. Hassan Rehan
 */
public class SizeWindow extends JDialog {
	private JPanel contentPane;
	JComboBox comboBox, comboBox_1;
	public SizeWindow() {
		super((Window) null);
		setModal(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);

		JLabel lblChoseWidth = new JLabel("Select Width");
		lblChoseWidth.setBounds(10, 37, 78, 14);
		contentPane.add(lblChoseWidth);
		String[] values = { "50", "80", "100", "120", "150", "180", "210", "250" };
		comboBox = new JComboBox(values);
		comboBox.setEditable(true);
		comboBox.setBounds(105, 31, 72, 20);
		contentPane.add(comboBox);

		Label label = new Label("Select Height");
		label.setBounds(10, 81, 78, 22);
		contentPane.add(label);

		comboBox_1 = new JComboBox(values);
		comboBox_1.setEditable(true);
		comboBox_1.setBounds(105, 81, 72, 20);
		contentPane.add(comboBox_1);

		JButton btnNewButton = new JButton("OK");
		btnNewButton.setBounds(121, 130, 56, 23);
		contentPane.add(btnNewButton);
		btnNewButton.addActionListener((ActionEvent e) -> {
			String width = String.valueOf(comboBox.getSelectedItem());
			String height = String.valueOf(comboBox_1.getSelectedItem());

			ImagePlus currentImp = WindowManager.getCurrentImage();
			Overlay ol = currentImp.getOverlay();
			Overlay ol_2 = new Overlay();
			ol_2.drawLabels(true);
			ol_2.drawNames(true);

			int g = ol.size();
			for (int i = 0; i < g; i++) {
				double x = ol.get(i).getXBase() + (ol.get(i).getFloatWidth() / 2);
				double y = ol.get(i).getYBase() + (ol.get(i).getFloatHeight() / 2);
				OvalRoi n = new OvalRoi(x - (Integer.parseInt(width) / 2), y - (Integer.parseInt(height) / 2),
						Integer.parseInt(width), Integer.parseInt(height));
				n.setName(ol.get(i).getName());
				ol_2.add(n);
			}
			ol_2.setStrokeColor(ol.getLabelColor());
			ol_2.setLabelColor(ol.getLabelColor());
			if (!(IJ.getVersion().compareTo("1.51v18") < 0)) {
				ol_2.selectable(ol.isSelectable());
			}
			currentImp.setOverlay(ol_2);
		});

		JButton btnClose = new JButton("Close");
		btnClose.setBounds(231, 130, 67, 23);
		btnClose.addActionListener((ActionEvent e) -> {
			dispose();
		});
		contentPane.add(btnClose);
		setContentPane(contentPane);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 324, 203);
		setResizable(false);
		setTitle("Resize");
	}
	public int getActiveWidth() {
		String width = String.valueOf(comboBox.getSelectedItem());
		return Integer.parseInt(width);
	}
	public int getActiveHeight() {
		String height = String.valueOf(comboBox_1.getSelectedItem());
		return Integer.parseInt(height);
	}
}
