package com.hartland.sampling.assistant.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import com.hartland.sampling.assistant.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hartland.sampling.assistant.AssistantApp;
import com.hartland.sampling.assistant.AssistantData;
import com.hartland.sampling.assistant.PlanListActivity;
import com.hartland.sampling.assistant.R;
import com.hartland.sampling.assistant.WorkerService;

@SuppressWarnings("unused")
public class MainActivity extends Activity implements OnItemSelectedListener {
	public static final String TAG = "MainActivity";
	static final boolean D = AssistantApp.D;

	private Spinner spinnerTables;
	private TextView textViewSelectedFile;
	private TextView textViewParsedFile;
	private AssistantData mAssistantData;

	private String mSelectedTable;

	// holds data from most recently parsed .csv file
	private ArrayList<ContentValues> mLastParsed;

	// private DataService mDataService;
	private Boolean mBound = false;

	private String[] mFileList;
	private File mPath = new File(Environment.getExternalStorageDirectory()
			+ "//Sampling Assistant//");
	private String mChosenFile;
	private static final int DIALOG_LOAD_FILE = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// new ExportDatabaseFileTask().execute("");

		mAssistantData = new AssistantData(this);

		// try {
		// installDb();
		// } catch (IOException e) {
		// Log.e(TAG, "insallDb failed with exception: " + e);
		// finish();
		// }

		mAssistantData.dbOpenBegin();
		String[] tables = mAssistantData.getTables();
		mAssistantData.dbEndClose();

		spinnerTables = (Spinner) findViewById(R.id.spinnerMainTables);
		ArrayAdapter<String> spinnerTablesAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_dropdown_item, tables);
		spinnerTables.setAdapter(spinnerTablesAdapter);
		spinnerTables.setOnItemSelectedListener(this);

		textViewSelectedFile = (TextView) findViewById(R.id.textViewMainSelectedFile);
		textViewParsedFile = (TextView) findViewById(R.id.textViewMainParsedFile);

	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
		case R.id.menuMainNuke:
			mAssistantData.nuke();
			refreshViews();
			break;
		case R.id.menuMainDropTable:
			mAssistantData.dropTable(mSelectedTable);
			refreshViews();
		}
		return super.onOptionsItemSelected(item);
	}

	public void refreshViews() {
		mAssistantData.dbOpenBegin();
		String[] tables = mAssistantData.getTables();
		mAssistantData.dbEndClose();
		int p = spinnerTables.getSelectedItemPosition();
		ArrayAdapter<String> spinnerTablesAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_dropdown_item, tables);
		spinnerTables.setAdapter(spinnerTablesAdapter);
		spinnerTables.setSelection(p);
	}

	public void onClick(View v) {
		Log.d(TAG, "onClick...");
		Intent intent = new Intent(this, WorkerService.class);
		switch (v.getId()) {
		case R.id.buttonMainSelectFile:
			Log.d(TAG, "...buttonMainSelectFile");
			loadFileList();
			onCreateDialog(DIALOG_LOAD_FILE);
			break;
		case R.id.buttonMainParse:
			if (mChosenFile == null) {
				Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			Log.d(TAG, "...buttonMainParse");
			intent.putExtra("caller", TAG + " buttonMainParse");
			intent.putExtra(WorkerService.REQUEST_CODE, WorkerService.PARSE_CSV);
			intent.putExtra(WorkerService.FILE_PATH, mPath.getPath() + "/"
					+ mChosenFile);
			startService(intent);
			textViewParsedFile.setText(mChosenFile);
			String table = convertFilenameToTablename(mChosenFile);
			Log.d(TAG, "table = " + table);
			int items = spinnerTables.getAdapter().getCount();
			for (int i = 0; i < items; i++) {
				if (table.equals(spinnerTables.getAdapter().getItem(i))) {
					spinnerTables.setSelection(i);
					break;
				}
			}
			break;
		case R.id.buttonMainInsert:
			Log.d(TAG, "...buttonMainInsert");
			intent.putExtra("caller", TAG + " buttonMainInsert");
			intent.putExtra(WorkerService.REQUEST_CODE,
					WorkerService.INSERT_CSV);
			intent.putExtra(WorkerService.TABLE_NAME, mSelectedTable);
			startService(intent);
			break;
		case R.id.buttonMainParseInsertAll:
			Log.d(TAG, "...buttonMainParseInsertAll");
			parseInsertAll();
			break;
		case R.id.buttonMainTestQuery:
			Log.d(TAG, "...buttonTestQuery");
			intent.putExtra(WorkerService.REQUEST_CODE,
					WorkerService.TEST_QUERY);
			intent.putExtra("caller", TAG + " buttonTestQuery");
			intent.putExtra(WorkerService.TABLE_NAME, mSelectedTable);
			startService(intent);
			break;
		case R.id.buttonMainStartApp:
			Log.d(TAG, "...buttonStartApp");
			intent.setClass(this, PlanListActivity.class);
			startActivity(intent);
			break;
		case R.id.buttonMainExportDb:
			new ExportDatabaseFileTask().execute("");
			break;
		}
	}

	private String convertFilenameToTablename(String filename) {
		return filename.toLowerCase(Locale.US).split("\\.")[0].replaceAll(
				"\\d", "");
	}

	public void onItemSelected(AdapterView<?> spinner, View view, int position,
			long id) {
		Log.d(TAG, "onItemSelected...");

		switch (spinner.getId()) {
		case R.id.spinnerMainTables:
			Log.d(TAG, "...spinnerMainTables...");
			mSelectedTable = spinner.getItemAtPosition(position).toString();
			Log.d(TAG, "..." + mSelectedTable);
			break;
		}

	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private void loadFileList() {
		Log.d(TAG, "loadFileList...");
		try {
			Boolean mkdirs = mPath.mkdirs();
			Log.d(TAG, "...mPath.mkdirs() == " + mkdirs + "...");
		} catch (SecurityException e) {
			Log.e(TAG, "unable to write on the sd card " + e.toString());
		}
		if (mPath.exists()) {
			Log.d(TAG, "...mPath.exists == true");
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					return filename.contains(".csv");
				}
			};
			mFileList = mPath.list(filter);
			sortFilenames();

		} else {
			Log.d(TAG, "...mPath.exists == false");
			mFileList = new String[0];
		}
	}

	private void sortFilenames() {
		Log.d(TAG, "sortFilenames");
		int size = mFileList.length;
		ArrayList<String> files = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			files.add(mFileList[i]);
		}
		Collections.sort(files);
		String[] orderedFiles = new String[size];
		for (int i = 0; i < size; i++) {
			orderedFiles[i] = files.get(i);
		}

		mFileList = orderedFiles;
	}

	protected Dialog onCreateDialog(int id) {
		Log.d(TAG, "onCreateDialog");
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(this);

		switch (id) {
		case DIALOG_LOAD_FILE:
			builder.setTitle("Choose your file");
			if (mFileList.length == 0) {
				Log.e(TAG, "Showing file picker before loading the file list");
				dialog = builder.create();
				return dialog;
			}
			builder.setItems(mFileList, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mChosenFile = mFileList[which];
					textViewSelectedFile.setText(mPath.getPath() + "/"
							+ mChosenFile);
				}
			});
			break;
		}
		dialog = builder.show();
		return dialog;
	}

	private void parseInsertAll() {
		Log.d(TAG, "parseInsertAll");
		loadFileList();
		String[] tables = { "oel_sources", "oel_types", "materials", "labs",
				"equipment_types", "methods", "media_types", "media", "units",
				"oels", "monsters", "plans", "plans_materials" };
		Intent intent = new Intent(this, WorkerService.class);

		for (String table : tables) {
			Log.v(TAG, "for table: " + table);
			// get files for this table
			ArrayList<String> files = new ArrayList<String>();
			for (String file : mFileList) {
				if (convertFilenameToTablename(file).equals(table)) {
					files.add(file);
					Log.v(TAG, "added file: " + file);
				}
			}

			for (String file : files) {
				Log.v(TAG, "for file: " + file);
				// parse
				intent.putExtra("caller", TAG + " parseInsertAll parse");
				intent.putExtra(WorkerService.REQUEST_CODE,
						WorkerService.PARSE_CSV);
				intent.putExtra(WorkerService.FILE_PATH, mPath.getPath() + "/"
						+ file);
				startService(intent);

				// insert
				intent.putExtra("caller", TAG + " parseInsertAll insert");
				intent.putExtra(WorkerService.REQUEST_CODE,
						WorkerService.INSERT_CSV);
				intent.putExtra(WorkerService.TABLE_NAME, table);
				startService(intent);

			}

		}
	}

	private class ExportDatabaseFileTask extends
			AsyncTask<String, Void, Boolean> {
//		private final ProgressDialog dialog = new ProgressDialog(
//				getApplicationContext());

		// can use UI thread here
		protected void onPreExecute() {
//			this.dialog.setMessage("Exporting database...");
//			this.dialog.show();
		}

		// automatically done on worker thread (separate from UI thread)
		protected Boolean doInBackground(final String... args) {

			File dbFile = new File(
					Environment.getDataDirectory()
							+ "/data/com.hartland.sampling.assistant/databases/assistant.db");

			File exportDir = new File(
					Environment.getExternalStorageDirectory(), "");
			if (!exportDir.exists()) {
				exportDir.mkdirs();
			}
			File file = new File(exportDir, dbFile.getName());

			try {
				file.createNewFile();
				this.copyFile(dbFile, file);
				return true;
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
				return false;
			}
		}

		// can use UI thread here
		protected void onPostExecute(final Boolean success) {
			// if (this.dialog.isShowing()) {
			// this.dialog.dismiss();
			// }
			if (success) {
				Toast.makeText(getApplicationContext(), "Export successful!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Export failed",
						Toast.LENGTH_SHORT).show();
			}
		}

		@SuppressWarnings("resource")
		void copyFile(File src, File dst) throws IOException {
			FileChannel inChannel = new FileInputStream(src).getChannel();
			FileChannel outChannel = new FileOutputStream(dst).getChannel();
			try {
				inChannel.transferTo(0, inChannel.size(), outChannel);
			} finally {
				if (inChannel != null)
					inChannel.close();
				if (outChannel != null)
					outChannel.close();
			}

		}

	}

	private void installDb() throws IOException {
		mAssistantData.dbOpenBegin();
		mAssistantData.dbEndClose();

		InputStream myInput = this.getAssets().open("assistant.db");

		String outFile = "/data/data/com.hartland.sampling.assistant/databases/assistant.db";
		OutputStream myOutput = new FileOutputStream(outFile);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		myOutput.flush();
		myOutput.close();
		myInput.close();

	}
}
