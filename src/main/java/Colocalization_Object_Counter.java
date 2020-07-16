import static ij.IJ.setTool;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.MacroInstaller;
import ij.plugin.PlugIn;
import ij.gui.GenericDialog;

/**
 * @author Anders Lunde
 * @author M. Hassan Rehan
 */

public class Colocalization_Object_Counter implements PlugIn {

	@Override
	public void run(String arg) {
		setTool("multipoint");
		UserInterface pluginFrame = new UserInterface();
		pluginFrame.setVisible(true);
		installHotkeyMacros();

	}

	// For debugging from Eclipse IDE:
	public static void main(String[] args) {
		new ij.ImageJ();
		new Colocalization_Object_Counter().run("");
		//TODO: Remove after debug
		//IJ.openImage("//kant/uv-isp-adm-u1/andelu/pc/Pictures/0 Stack.tif").show();
	}

	private void installHotkeyMacros() {
		GenericDialog hotkeyDialog = new GenericDialog("Enable hotkeys?");
		hotkeyDialog.enableYesNoCancel();

		String msg = "Enable the following hotkeys?\r\n\r\n";
		msg = msg + "Q -> Select channel 1\r\n";
		msg = msg + "W -> Select channel 2\r\n";
		msg = msg + "E -> Select channel 3\r\n";
		msg = msg + "A -> Select channel 4\r\n";
		msg = msg + "S -> Select channel 5\r\n";
		msg = msg + "D -> Select channel 6\r\n";
		msg = msg + "Z -> Select channel 7\r\n";
		msg = msg + "X -> Select channel 8\r\n";
		msg = msg + "C -> Select channel 9\r\n";
		msg = msg + "\r\n";
		msg = msg + "R -> Zoom in\r\n";
		msg = msg + "F -> Zoom out\r\n";
		msg = msg + "\r\n";
		msg = msg + "\r\n";
		msg = msg + "Hints:\r\n";
		msg = msg + "-Overlays/cells can be deleted by selecting and pressing \"del\" on the keyboard:\r\n";
		msg = msg + "-Alt + left click to delete multipoints\r\n";
		msg = msg + "-Hold spacebar + drag mouse button to pan\r\n";
		msg = msg + "-Scroll mouse wheel to change Z-stack position\r\n";
		msg = msg + "-Restore multipoint selection with Ctrl+Shift+E\r\n";

		hotkeyDialog.addMessage(msg);
		hotkeyDialog.showDialog();

		if (hotkeyDialog.wasOKed()) {
			StringBuffer sb = new StringBuffer();
			sb.append("macro \"set chn 1 [q]\" {Stack.setChannel(1);}\n");
			sb.append("macro \"set chn 2 [w]\" {Stack.setChannel(2);}\n");
			sb.append("macro \"set chn 3 [e]\" {Stack.setChannel(3);}\n");
			sb.append("macro \"set chn 4 [a]\" {Stack.setChannel(4);}\n");
			sb.append("macro \"set chn 5 [s]\" {Stack.setChannel(5);}\n");
			sb.append("macro \"set chn 6 [d]\" {Stack.setChannel(6);}\n");
			sb.append("macro \"set chn 7 [z]\" {Stack.setChannel(7);}\n");
			sb.append("macro \"set chn 8 [x]\" {Stack.setChannel(8);}\n");
			sb.append("macro \"set chn 9 [c]\" {Stack.setChannel(9);}\n");
			sb.append("macro \"zoom in [r]\" {run(\"In [+]\");}\n");
			sb.append("macro \"zoom out 9 [f]\" {run(\"Out [-]\");}\n");
			new MacroInstaller().install(sb.toString());
		}
	}
}