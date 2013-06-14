package com.hartland.sampling.assistant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class OelEditActivity extends ListActivity implements
		OnItemSelectedListener, OnItemLongClickListener {
	public static final String TAG = "OelEditActivity";
	private static final boolean D = AssistantApp.D;
	// public static final int ID = AssistantApp.EDIT_OEL_ID;
	public static final int EDIT_OEL = 1;

	public static final String EDITABLE_OEL_SOURCE = "USER";
	public static final double DEFAULT_OEL_VALUE = 1.0;
	public static final String DEFAULT_UNIT = "mg/m3";
	public static final String DEFAULT_OEL_TYPE = "TWA";

	private static LinkedList<Oel> sOelsToSave = new LinkedList<Oel>();
	private static LinkedList<Oel> sOelsToAdd = new LinkedList<Oel>();

	private PlanMaterial mPlanMaterial;
	private ArrayList<Oel> mOels;
	private ArrayList<Unit> mUnits;

	private TextView textViewMaterial, textViewCas;
	private Spinner spinnerUnits, spinnerTypes;
	private EditText editTextValue;

	private OelsAdapter oelsAdapter;
	private ArrayAdapter<Unit> spinnerUnitsAdapter;
	ArrayAdapter<OelType> spinnerTypesAdapter;
	private TextView textViewValueHeader, textViewUnitHeader,
			textViewOelTypeHeader;

	private int planMaterialIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (D)
			Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_oel);

		textViewMaterial = (TextView) findViewById(R.id.textViewEditOelMaterial);
		textViewCas = (TextView) findViewById(R.id.textViewEditOelCas);

		spinnerUnits = (Spinner) findViewById(R.id.spinnerEditOelUnits);
		spinnerTypes = (Spinner) findViewById(R.id.spinnerEditOelTypes);

		editTextValue = (EditText) findViewById(R.id.editTextEditOelValue);
		editTextValue.addTextChangedListener(new TextWatcherValue());

		ListView listView = getListView();

		listView.setOnItemLongClickListener(this);

		textViewValueHeader = (TextView) findViewById(R.id.textViewEditOelValueHeader);
		textViewUnitHeader = (TextView) findViewById(R.id.textViewEditOelUnitHeader);
		textViewOelTypeHeader = (TextView) findViewById(R.id.textViewEditOelTypeHeader);

	}

	@Override
	protected void onStart() {
		if (D)
			Log.d(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		if (D)
			Log.d(TAG, "onResume");
		super.onResume();
		planMaterialIndex = getIntent().getIntExtra("planMaterialIndex", -1);
		if (mPlanMaterial == null) {
			mPlanMaterial = PlanEditActivity.sPlan
					.getPlanMaterial(planMaterialIndex);
			if (mPlanMaterial == null) {
				if (D)
					Log.w(TAG, "mPlanMaterial == null");
				finish();
			} else {
				onDataReady();
			}
		}
	}

	@Override
	protected void onPause() {
		if (D)
			Log.d(TAG, "onPause");
		super.onPause();
		saveOel(this, mPlanMaterial.getPlanOel());
		// TODO: update planMaterial?

	}

	@Override
	protected void onStop() {
		if (D)
			Log.d(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (D)
			Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	public static void saveOel(Context context, Oel oel) {
		if (D)
			Log.d(TAG, "saveOel");

		sOelsToSave.add(oel.copy());

		Intent i = new Intent(context, WorkerService.class);
		i.putExtra(WorkerService.REQUEST_CODE, WorkerService.SAVE_OEL_EDIT);
		i.putExtra("caller", TAG);
		context.startService(i);
	}

	public static Oel getOelToSave(String caller) {
		if (D)
			Log.d(TAG, "getOelToSave caller: " + caller);
		Oel oel = null;
		try {
			oel = sOelsToSave.getFirst();
		} catch (NoSuchElementException e) {
			Log.e(TAG, "caught " + e + "--> returning null");
			return null;
		}
		sOelsToSave.removeFirst();
		return oel;
	}

	public static Oel getOelToAdd(String caller) {
		if (D)
			Log.d(TAG, "getOelToAdd caller: " + caller);
		Oel oel = null;
		try {
			oel = sOelsToAdd.getFirst();
		} catch (NoSuchElementException e) {
			Log.e(TAG, "caught " + e + "--> returning null");
			return null;
		}
		sOelsToAdd.removeFirst();
		return oel;
	}

	private void onDataReady() {
		if (D)
			Log.d(TAG, "onDataReady");
		mOels = mPlanMaterial.getPlanOels();
		mUnits = Unit.getVolumeUnits(AssistantApp.allUnits);

		textViewMaterial.setText(mPlanMaterial.getParentListMaterial()
				.getName());
		textViewCas.setText(mPlanMaterial.getParentListMaterial().getCas());

		oelsAdapter = new OelsAdapter(this);
		setListAdapter(oelsAdapter);

		// TODO: limit units to volumes only
		spinnerUnitsAdapter = new Unit.SpinnerAdapter(this,
				R.layout.my_spinner_row, R.id.textViewSpinnerRow, mUnits);

		spinnerTypesAdapter = new OelType.SpinnerAdapter(this,
				R.layout.my_spinner_row, R.id.textViewSpinnerRow,
				AssistantApp.allOelTypes);

		spinnerUnits.setAdapter(spinnerUnitsAdapter);
		spinnerTypes.setAdapter(spinnerTypesAdapter);

		setOel(mPlanMaterial.getPlanOel());

		spinnerUnits.setOnItemSelectedListener(this);
		spinnerTypes.setOnItemSelectedListener(this);

		showOrHideViews();

	}

	private void showOrHideViews() {

		if (mPlanMaterial.getPlanOels().size() == 0) {
			if (D)
				Log.d(TAG, "size == 0, hiding all views");
			hideViews();
			return;
		}

		if (mPlanMaterial.getPlanOel().isReadOnly()) {
			if (D)
				Log.d(TAG, "isReadyOnly, disabling related views");
			hideViews();
		} else {
			if (D)
				Log.d(TAG, "!isReadOnly, enabling related views");
			showViews();

		}
	}

	private void hideViews() {
		spinnerUnits.setVisibility(View.GONE);
		spinnerTypes.setVisibility(View.GONE);
		editTextValue.setVisibility(View.GONE);

		textViewValueHeader.setVisibility(View.GONE);
		textViewUnitHeader.setVisibility(View.GONE);
		textViewOelTypeHeader.setVisibility(View.GONE);
		oelsAdapter.notifyDataSetChanged();
	}

	private void showViews() {
		spinnerUnits.setVisibility(View.VISIBLE);
		spinnerTypes.setVisibility(View.VISIBLE);
		editTextValue.setVisibility(View.VISIBLE);

		textViewValueHeader.setVisibility(View.VISIBLE);
		textViewUnitHeader.setVisibility(View.VISIBLE);
		textViewOelTypeHeader.setVisibility(View.VISIBLE);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonOelEditDone:
			if (D)
				Log.d(TAG, "onClick DONE");

			finish();
			break;
		case R.id.buttonOelEditNewOel:
			if (D)
				Log.d(TAG, "onClick NEW OEL");
			newOel();
			break;
		}
	}

	private void newOel() {
		Oel newOel = AssistantApp.defaultOel.copy();
		oelsAdapter.add(newOel);

		newOel.setMaterial(mPlanMaterial.getParentListMaterial().getId());

		sOelsToAdd.add(newOel);

		Intent i = new Intent(this, WorkerService.class);
		i.putExtra(WorkerService.REQUEST_CODE, WorkerService.SAVE_NEW_OEL);
		i.putExtra("caller", TAG);
		startService(i);

	}

	public void setOel(Oel newOel) {
		mPlanMaterial.setPlanOel(newOel);

		showOrHideViews();

		if (mPlanMaterial.getPlanOel().isReadOnly()) {
			// do nothing
		} else {
			editTextValue.setText(String.valueOf(mPlanMaterial.getPlanOel()
					.getValue()));
			spinnerUnits.setSelection(spinnerUnitsAdapter
					.getPosition(mPlanMaterial.getPlanOel().getUnit()));
			spinnerTypes.setSelection(spinnerTypesAdapter
					.getPosition(mPlanMaterial.getPlanOel().getOelType()));
		}
	}

	public void deleteOel(Oel oelToDelete) {
		if (oelToDelete.isReadOnly()) {
			Log.e(TAG,
					"attempted to delete read-only OEL. Returning without delete.");
			return;
		}
		oelsAdapter.remove(oelToDelete);
		Oel lowest = mPlanMaterial.getLowestPlanOel();
		mPlanMaterial.setPlanOel(lowest);
		oelsAdapter.notifyDataSetChanged();

		Intent i = new Intent(this, WorkerService.class);
		i.putExtra(WorkerService.REQUEST_CODE, WorkerService.DELETE_OEL);
		i.putExtra("caller", TAG);
		i.putExtra("_id", oelToDelete.getId());
		startService(i);

		showOrHideViews();
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (D)
			Log.d(TAG, "onItemSelected");

		switch (parent.getId()) {
		case R.id.spinnerEditOelUnits:
			if (D)
				Log.d(TAG, "onItemSelected spinnerUnits");
			Unit selectedUnit = (Unit) spinnerUnits.getSelectedItem();
			if (mPlanMaterial.getPlanOel().isReadOnly()) {
				Log.e(TAG,
						"Attempted to set unit on read-only OEL. Returning without change.");
				return;
			} else {
				mPlanMaterial.getPlanOel().setUnit(selectedUnit);
				oelsAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.spinnerEditOelTypes:
			if (D)
				Log.d(TAG, "onItemSelected spinnerTypes");
			OelType selectedOelType = (OelType) spinnerTypes.getSelectedItem();
			if (mPlanMaterial.getPlanOel().isReadOnly()) {
				Log.e(TAG,
						"Attempted to set OEL type on read-only OEL. Returning without change.");
				return;
			} else {
				mPlanMaterial.getPlanOel().setOelType(selectedOelType);
				oelsAdapter.notifyDataSetChanged();
			}
			break;
		}

	}

	@Override
	protected void onListItemClick(ListView l, View view, int position, long id) {
		if (D)
			Log.d(TAG, "OnListItemClick position: " + position + " id: " + id);
		super.onListItemClick(l, view, position, id);
		// oelsAdapter.onClick((View) view.getTag());
		setOel((Oel) getListView().getItemAtPosition(position));
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view,
			int position, long id) {
		final Oel oel = (Oel) getListView().getItemAtPosition(position);

		if (oel.isReadOnly()) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.no_delete)
					.setMessage(R.string.no_delete_message)
					.setPositiveButton(android.R.string.ok, null).show();
		} else {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.delete)
					.setMessage(R.string.delete_confirm)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (D)
										Log.d(TAG, "AlertDialog onClick");
									deleteOel(oel);

								}
							}).setNegativeButton(android.R.string.no, null)
					.show();

		}
		return false;
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private class TextWatcherValue implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// do nothing

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int after) {
			if (D)
				Log.d(TAG, "onTextChanged");

			String string = s.toString();
			double value;
			try {
				value = Double.valueOf(string);
			} catch (NumberFormatException e) {
				value = 0;
			}
			if (mPlanMaterial.getPlanOel().isReadOnly()) {
				Log.e(TAG,
						"attempted to reset value on read-only OEL. Returning without change.");
			} else {
				mPlanMaterial.getPlanOel().setOelValue(value);
				oelsAdapter.notifyDataSetChanged();
			}
		}

	}

	private class OelsAdapter extends ArrayAdapter<Oel> {
		public OelsAdapter(Context context) {
			super(context, R.layout.oel_edit_row, mOels);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				LayoutInflater inflator = getLayoutInflater();
				view = inflator.inflate(R.layout.oel_edit_row, null);
			} else {
				view = convertView;
			}

			RadioButton rb = (RadioButton) view
					.findViewById(R.id.radioButtonOelEditName);
			rb.setText(mOels.get(position).getName());

			long oelId = mOels.get(position).getId();
			if (PlanEditActivity.sPlan.getPlanMaterial(planMaterialIndex)
					.getPlanOel().getId() == oelId) {
				// rb.setSelected(true);
				rb.setChecked(true);
			} else {
				// rb.setSelected(false);
				rb.setChecked(false);

			}

			return view;
		}

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

}
