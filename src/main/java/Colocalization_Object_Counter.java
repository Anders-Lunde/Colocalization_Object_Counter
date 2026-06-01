import static ij.IJ.setTool;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.plugin.MacroInstaller;
import ij.plugin.PlugIn;
import ij.gui.GenericDialog;
import ij.gui.PointRoi;

/**
 * @author Anders Lunde
 * @author M. Hassan Rehan
 */

public class Colocalization_Object_Counter implements PlugIn {

	@Override
	public void run(String arg) {
		setTool("multipoint");
		CocUserInterface pluginFrame = new CocUserInterface();
		pluginFrame.setVisible(true);
		installHotkeyMacros();
		showUpdatePopup();

	}

	// For debugging from Eclipse IDE:
	public static void main(String[] args) {
		new ij.ImageJ();
		new Colocalization_Object_Counter().run("");
		showUpdatePopup();
	}

	

private static void showUpdatePopup() {
    // Get the 'do not show again' flag from ImageJ preferences.
    boolean doNotShowAgain = Prefs.get("plugin.update.do_not_show_again", false);

    // If the flag is true, return immediately without showing the popup.
    if (doNotShowAgain) return;

    // Create your update message and checkbox to 'do not show this again'.
    String message = "Update version 1.2.0! Macro support added! Click the macro help button near the top to learn more...\n";
    JCheckBox checkBox = new JCheckBox("Do not show this again.", false);
    JPanel panel = new JPanel();
    panel.add(checkBox);

    // Show the popup with the checkbox.
    JOptionPane.showMessageDialog(null, new Object[]{message, panel}, "Update", JOptionPane.INFORMATION_MESSAGE);

    // If the checkbox is checked, set the 'do not show again' flag in ImageJ preferences.
    if (checkBox.isSelected()) {
        Prefs.set("plugin.update.do_not_show_again", true);
        Prefs.savePreferences(); // Ensure the preference is saved immediately.
    }
}
	
	
	
	private void installHotkeyMacros() {
		GenericDialog hotkeyDialog = new GenericDialog("Enable hotkeys?");
		hotkeyDialog.enableYesNoCancel();

		hotkeyDialog.addMessage(HotkeyConfiguration.dialogMessage());
		hotkeyDialog.showDialog();

		if (hotkeyDialog.wasOKed()) {
			new MacroInstaller().install(HotkeyConfiguration.macroScript());
		}
	}
}
