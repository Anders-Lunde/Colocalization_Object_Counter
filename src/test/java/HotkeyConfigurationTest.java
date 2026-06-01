import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HotkeyConfigurationTest {
	@Test
	public void dialogMessageIncludesZSliceShortcuts() {
		String message = HotkeyConfiguration.dialogMessage();

		assertContains(message, "T -> Previous Z-slice");
		assertContains(message, "G -> Next Z-slice");
	}

	@Test
	public void macroScriptIncludesZSliceShortcuts() {
		String script = HotkeyConfiguration.macroScript();

		assertContains(script, "macro \"previous slice [t]\" {run(\"Previous Slice [<]\");}");
		assertContains(script, "macro \"next slice [g]\" {run(\"Next Slice [>]\");}");
	}

	private static void assertContains(String text, String expected) {
		assertTrue("Expected to find: " + expected + "\nActual text:\n" + text, text.contains(expected));
	}
}
