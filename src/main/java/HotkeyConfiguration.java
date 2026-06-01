public final class HotkeyConfiguration {
	private HotkeyConfiguration() {
	}

	public static String dialogMessage() {
		StringBuilder msg = new StringBuilder();
		msg.append("Enable the following hotkeys?\r\n\r\n");
		msg.append("Q -> Select channel 1\r\n");
		msg.append("W -> Select channel 2\r\n");
		msg.append("E -> Select channel 3\r\n");
		msg.append("A -> Select channel 4\r\n");
		msg.append("S -> Select channel 5\r\n");
		msg.append("D -> Select channel 6\r\n");
		msg.append("Z -> Select channel 7\r\n");
		msg.append("X -> Select channel 8\r\n");
		msg.append("C -> Select channel 9\r\n");
		msg.append("\r\n");
		msg.append("R -> Zoom in\r\n");
		msg.append("F -> Zoom out\r\n");
		msg.append("T -> Previous Z-slice\r\n");
		msg.append("G -> Next Z-slice\r\n");
		msg.append("\r\n");
		msg.append("\r\n");
		msg.append("Hints:\r\n");
		msg.append("-Overlays/cells can be deleted by selecting and pressing \"del\" on the keyboard:\r\n");
		msg.append("-Alt + left click to delete multipoints\r\n");
		msg.append("-Hold spacebar + drag mouse button to pan\r\n");
		msg.append("-Scroll mouse wheel to change Z-stack position\r\n");
		msg.append("-Restore multipoint selection with Ctrl+Shift+E\r\n");
		return msg.toString();
	}

	public static String macroScript() {
		StringBuilder sb = new StringBuilder();
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
		sb.append("macro \"zoom out [f]\" {run(\"Out [-]\");}\n");
		sb.append("macro \"previous slice [t]\" {run(\"Previous Slice [<]\");}\n");
		sb.append("macro \"next slice [g]\" {run(\"Next Slice [>]\");}\n");
		return sb.toString();
	}
}
