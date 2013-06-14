package com.hartland.sampling.assistant;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class AssistantApp extends Application {

	private static final String TAG = "AssistantApp";
	public static final boolean D = true;

	public static final String BROADCAST_ACTION = "com.hartland.sampling.assistant.broadcastaction";

	private static AssistantData mAssistantData;

	// variables used by worker service to parse all data from .csv
	public static File mPath = new File(
			Environment.getExternalStorageDirectory()
					+ "//Sampling Assistant//");
	public static String[] mFileList;

	public static ArrayList<ListMaterial> allListMaterials;
	public static ArrayList<Unit> allUnits;
	public static ArrayList<OelSource> allOelSources;
	public static ArrayList<OelType> allOelTypes;

	public static Oel defaultOel;

	public static final String[] PROTECTED_SOURCES = new String[] { "ACGIH",
			"NIOSH", "OSHA" };
	public static final String[] NON_VOLUME_UNITS = new String[] { "ng", "ug",
			"mg" };

	@Override
	public void onCreate() {
		super.onCreate();

		mAssistantData = new AssistantData(this);
		mAssistantData.dbEndCloseAll();

		SharedPreferences preferences = getSharedPreferences("checkFIrst",
				MODE_PRIVATE);
		Intent i = new Intent(this, WorkerService.class);

		if (preferences.getBoolean("firstRun", true)) {
			if (D)
				Log.i(TAG, "first run");

			i.putExtra(WorkerService.REQUEST_CODE, WorkerService.INSTALL_DB);
			i.putExtra("caller", TAG);
			startService(i);

			PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

			Editor editor = preferences.edit();
			editor.putBoolean("firstRun", false);
			editor.commit();
		}

		i.putExtra(WorkerService.REQUEST_CODE,
				WorkerService.SELECT_ALL_LIST_MATERIALS);
		i.putExtra("caller", TAG);
		startService(i);

		i.putExtra(WorkerService.REQUEST_CODE, WorkerService.SELECT_ALL_EXTRAS);
		startService(i);

		i.putExtra(WorkerService.REQUEST_CODE, WorkerService.SELECT_DEFAULT_OEL);
		startService(i);

		// TODO: first run -- set default preferences
	}

	public static AssistantData getAssistantData() {
		return mAssistantData;
	}

}
