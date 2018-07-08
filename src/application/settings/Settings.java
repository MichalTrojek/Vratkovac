package application.settings;

import java.util.prefs.Preferences;

public class Settings {

	private Preferences prefs;

	public Settings() {
		prefs = Preferences.userNodeForPackage(Settings.class);
	}

	public String getPath() {
		return prefs.get("path", System.getProperty("user.dir"));
	}

	public void savePath(String path) {
		prefs.put("path", path);
	}

}
