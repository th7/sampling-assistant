package com.hartland.sampling.assistant;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		String[] colors = new String[] { "red", "orange", "yellow" };

		for (String color : colors) {
			String key = "pref_key_show_" + color;
			refreshOelPercentSummary(key);
		}
		refreshHighlightUnselectedStelSummary("pref_key_highlight_stel");
		refreshHighlightUnselectedCSummary("pref_key_highlight_c");

	}

	@Override
	protected void onResume() {
		super.onResume();

		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this)
				.unregisterOnSharedPreferenceChangeListener(this);

	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals("pref_key_show_red")
				|| key.equals("pref_key_show_orange")
				|| key.equals("pref_key_show_yellow")) {
			refreshOelPercentSummary(key);
		} else if (key.equals("pref_key_highlight_stel")) {
			refreshHighlightUnselectedStelSummary(key);
		} else if (key.equals("pref_key_highlight_c")) {
			refreshHighlightUnselectedCSummary(key);
		}
	}

	@SuppressWarnings("deprecation")
	private void refreshOelPercentSummary(String key) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		String fromRes = null;
		String fromPref = sp.getString(key, "ERROR");
		String newSummary = null;
		if (Build.VERSION.SDK_INT >= 14) {
			fromRes = getResources().getString(R.string.oel_percent_summary);
			newSummary = fromRes + " " + fromPref + "%%";
		} else {
			fromRes = getResources()
					.getString(R.string.oel_percent_summary_old);
			newSummary = fromRes + " " + fromPref + "%";
		}

		Preference pref = findPreference(key);
		pref.setSummary(newSummary);
	}

	@SuppressWarnings("deprecation")
	private void refreshHighlightUnselectedStelSummary(String key) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		String fromRes = getResources().getString(
				R.string.highlight_stel_summary);
		;
		String fromPref = sp.getString(key, "ERROR");
		String minutes = getResources().getString(R.string.minutes);
		String newSummary = null;
		newSummary = fromRes + " " + fromPref + " " + minutes;

		Preference pref = findPreference(key);
		pref.setSummary(newSummary);
	}

	@SuppressWarnings("deprecation")
	private void refreshHighlightUnselectedCSummary(String key) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		String fromRes = getResources().getString(R.string.highlight_c_summary);
		String fromPref = sp.getString(key, "ERROR");
		String minutes = getResources().getString(R.string.minutes);

		String newSummary = null;
		newSummary = fromRes + " " + fromPref + " " + minutes;

		Preference pref = findPreference(key);
		pref.setSummary(newSummary);
	}

}
