package com.hartland.sampling.assistant;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class MaterialSelectActivity extends ListActivity {
	public static final String TAG = "MaterialSelectActivity";
	// public static final int ID = AssistantApp.MATERIAL_SELECT_ID;
	private static final boolean D = AssistantApp.D;

	static final int SELECT_MATERIALS = 1;

	// private AssistantApp mAssistantApp;
	private MaterialsAdapter mMaterialsAdapter;
	// private Plan mTempPlan;
	// private ArrayList<PlanMaterial> mTempPlanMaterials;
	private EditText editTextSearchName, editTextSearchCas;
	private CheckBox checkBoxSelected;
	private TextWatcherSearch mTextWatcherSearch;

	private TextView textViewFilterName, textViewFilterCas,
			textViewFilterSelected, textViewFilterNone, textViewFilterHeader;

	private ArrayList<ListMaterial> mFilteredListMaterials;
	private ArrayList<ListMaterial> mAllListMaterials;

	// private Plan mPlan;

	boolean registered = false;
	private IntentFilter mIntentFilter = new IntentFilter(
			AssistantApp.BROADCAST_ACTION);

	// private int planIndex;

	// int planIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (D)
			Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		// Debug.stopMethodTracing();

		setContentView(R.layout.activity_material_select);

		// mAssistantApp = (AssistantApp) getApplication();

		mFilteredListMaterials = new ArrayList<ListMaterial>();
		// planIndex = getIntent().getIntExtra("planIndex", -1);

		editTextSearchName = (EditText) findViewById(R.id.editTextMaterialSelectSearchName);
		editTextSearchCas = (EditText) findViewById(R.id.editTextMaterialSelectSearchCas);
		checkBoxSelected = (CheckBox) findViewById(R.id.checkBoxMaterialSelectSelected);

		mTextWatcherSearch = new TextWatcherSearch();

		editTextSearchCas.setRawInputType(InputType.TYPE_CLASS_NUMBER);
		editTextSearchName.addTextChangedListener(mTextWatcherSearch);
		editTextSearchCas.addTextChangedListener(mTextWatcherSearch);
		checkBoxSelected.setOnCheckedChangeListener(mTextWatcherSearch);

		textViewFilterName = (TextView) findViewById(R.id.textViewMaterialSelectFiltersName);
		textViewFilterCas = (TextView) findViewById(R.id.textViewMaterialSelectFiltersCas);
		textViewFilterSelected = (TextView) findViewById(R.id.textViewMaterialSelectFiltersSelected);
		textViewFilterNone = (TextView) findViewById(R.id.textViewMaterialSelectFiltersNone);
		textViewFilterHeader = (TextView) findViewById(R.id.textViewMaterialSelectFilters);

		// mTempPlan = Plan;
		// mTempPlanMaterials = Plan.getTempPlanMaterials();

	}

	@Override
	protected void onStart() {
		if (D)
			Log.d(TAG, "onStart");
		super.onStart();

		if (!registered) {
			if (D)
				Log.d(TAG, "registering broadcast reciever");
			registerReceiver(broadcastReceiver, mIntentFilter);
			registered = true;
		}

	}

	@Override
	protected void onResume() {
		if (D)
			Log.d(TAG, "onResume");
		super.onResume();

		if (AssistantApp.allListMaterials != null) {
			mAllListMaterials = AssistantApp.allListMaterials;
			onDataReady();
		} else {
			if (D)
				Log.w(TAG,
						"Data.allListMaterials is null. Should have been selected in AssistantApp.onCreate().");
			Intent i = new Intent(this, WorkerService.class);
			i.putExtra(WorkerService.REQUEST_CODE,
					WorkerService.SELECT_ALL_LIST_MATERIALS);
			i.putExtra("caller", TAG);
			startService(i);

			i.putExtra(WorkerService.REQUEST_CODE, WorkerService.NOTIFY_DONE);
			i.putExtra(WorkerService.DONE_CODE,
					WorkerService.SELECT_ALL_LIST_MATERIALS);
			startService(i);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (D)
			Log.d(TAG, "onStop");
		super.onStop();

		if (registered) {
			if (D)
				Log.d(TAG, "unregistering broadcast reciever");
			unregisterReceiver(broadcastReceiver);
			registered = false;
		}
	}

	public void onClick(View v) {
		if (D)
			Log.d(TAG, "onClick");
		switch (v.getId()) {
		case R.id.buttonMaterialSelectDone:
			if (D)
				Log.d(TAG, "case buttonMaterialSelectDone");
			finish();
			break;
		}
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			onHandleIntent(intent);
		}
	};

	private void onHandleIntent(Intent intent) {
		if (D)
			Log.d(TAG, "onHandleIntent");
		switch (intent.getIntExtra(WorkerService.DONE_CODE, -1)) {
		case WorkerService.SELECT_ALL_LIST_MATERIALS:
			if (AssistantApp.allListMaterials != null) {
				mAllListMaterials = AssistantApp.allListMaterials;
				onDataReady();
			} else {
				Log.e(TAG,
						"WorkerService returned done code with object still null. Exiting.");
				finish();
			}
			break;
		case WorkerService.PLAN_MODIFIED:
			PlanEditActivity.addRemovePlanMaterials(this);
			break;
		}
	}

	private void onDataReady() {
		mMaterialsAdapter = new MaterialsAdapter(MaterialSelectActivity.this,
				mAllListMaterials);
		setListAdapter(mMaterialsAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_plan_list, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent i = new Intent(this, PreferencesActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			return true;
		default:
			return false;

		}
	}

	private class TextWatcherSearch implements TextWatcher,
			OnCheckedChangeListener {
		public boolean mSelected;

		public void afterTextChanged(Editable arg0) {
		}

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (D)
				Log.d(TAG, "onTextChanged");

			String name = editTextSearchName.getText().toString()
					.toLowerCase(Locale.US);
			String cas = editTextSearchCas.getText().toString()
					.toLowerCase(Locale.US);

			if (name.length() == 0 && cas.length() == 0 && !mSelected) {
				mMaterialsAdapter = new MaterialsAdapter(
						MaterialSelectActivity.this, mAllListMaterials);
				setListAdapter(mMaterialsAdapter);
				textViewFilterName.setVisibility(TextView.GONE);
				textViewFilterCas.setVisibility(TextView.GONE);
				textViewFilterSelected.setVisibility(TextView.GONE);
				textViewFilterNone.setVisibility(TextView.VISIBLE);
				textViewFilterHeader.setBackgroundColor(Color.TRANSPARENT);
				return;
			}
			mFilteredListMaterials.clear();
			textViewFilterNone.setVisibility(TextView.GONE);
			textViewFilterHeader.setBackgroundColor(Color.YELLOW);
			if (name.length() > 0) {
				textViewFilterName.setVisibility(TextView.VISIBLE);
			} else {
				textViewFilterName.setVisibility(TextView.GONE);

			}
			if (cas.length() > 0) {
				textViewFilterCas.setVisibility(TextView.VISIBLE);
			} else {
				textViewFilterCas.setVisibility(TextView.GONE);

			}
			if (mSelected) {
				textViewFilterSelected.setVisibility(TextView.VISIBLE);

				for (ListMaterial listMaterial : mAllListMaterials) {
					if (PlanEditActivity.sPlan.hasPlanMaterial(listMaterial
							.getId())
							&& listMaterial.getName().toLowerCase(Locale.US)
									.contains(name)
							&& listMaterial.getCas().toLowerCase(Locale.US)
									.contains(cas)) {
						mFilteredListMaterials.add(listMaterial);
					}
				}
			} else {
				textViewFilterSelected.setVisibility(TextView.GONE);

				for (ListMaterial listMaterial : mAllListMaterials) {
					if (listMaterial.getName().toLowerCase(Locale.US)
							.contains(name)
							&& listMaterial.getCas().toLowerCase(Locale.US)
									.contains(cas)) {
						mFilteredListMaterials.add(listMaterial);
					}
				}
			}

			if (mFilteredListMaterials.isEmpty()) {
				// TODO fix hard-coded
				mFilteredListMaterials.add(new ListMaterial(-1, "No Materials",
						"", 0));
			}

			mMaterialsAdapter = new MaterialsAdapter(
					MaterialSelectActivity.this, mFilteredListMaterials);
			setListAdapter(mMaterialsAdapter);
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (D)
				Log.d(TAG, "onCheckedChanged");
			mSelected = isChecked;
			onTextChanged("", -1, -1, -1);

		}
	}

}