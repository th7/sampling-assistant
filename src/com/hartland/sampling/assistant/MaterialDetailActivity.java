package com.hartland.sampling.assistant;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class MaterialDetailActivity extends Activity implements
		OnSeekBarChangeListener, OnItemSelectedListener {
	public static final String TAG = "MaterialDetailActivity";
	// public static final int ID = AssistantApp.MATERIAL_DETAIL_ID;
	private static final boolean D = AssistantApp.D;

	public static final int VIEW_MATERIAL = 11;
	public static final int OELS = 1;

	private static final int SEEK_BAR_MAX = PlanEditActivity.SEEK_BAR_MAX;

	private static final int SEEK_BAR_MOD1 = PlanEditActivity.SEEK_BAR_MOD1;
	private static final int SEEK_BAR_INT1 = PlanEditActivity.SEEK_BAR_INT1;

	private static final int SEEK_BAR_MOD2 = PlanEditActivity.SEEK_BAR_MOD2;
	private static final int SEEK_BAR_INT2 = PlanEditActivity.SEEK_BAR_INT2;

	private static final int SEEK_BAR_MOD3 = PlanEditActivity.SEEK_BAR_MOD3;
	private static final int SEEK_BAR_INT3 = PlanEditActivity.SEEK_BAR_INT3;

	private static final int SEEK_BAR_MOD4 = PlanEditActivity.SEEK_BAR_MOD4;

	private TextView textViewMaterial, textViewCas, textViewMediaType,
			textViewFlowValue, textViewOelPercent, textViewDurationValue,
			textViewEquipmentType, textViewLoq, textViewLab;

	private SeekBar seekBarDuration, seekBarFlow;
	private Spinner spinnerMethodNames, spinnerMediaNames, spinnerOelNames;

	private int mCurrentTime, mOelPercent, mOnStartTrackingTouchTime;
	private double mCurrentFlow;

	// // private AssistantApp mAssistantApp;
	// // private Plan mTempPlan;
	// private PlanMaterial mTempMaterial;

	private String mMethodName;
	private String mMediaName;

	private SharedPreferences mSharedPreferences;

	private Plan mPlan;
	private PlanMaterial mPlanMaterial;
	private Monster mPlanMonster;

	// used to ignore first programatic calls to spinners because stupid android
	// doesn't have built in differentiation between user and non-user changes
	private int spinnerBullshit = 0;
	private int planMaterialIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_material_detail);

		planMaterialIndex = getIntent().getIntExtra("planMaterialIndex", -1);

		// mAssistantApp = (AssistantApp) getApplication();
		// mTempPlan = mAssistantApp.getTempPlan();
		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

		// textViewWarnings = (TextView)
		// findViewById(R.id.textViewLimitingWarnings);
		textViewMaterial = (TextView) findViewById(R.id.textViewLimitingMaterial);
		textViewCas = (TextView) findViewById(R.id.textViewLimitingCas);
		textViewMediaType = (TextView) findViewById(R.id.textViewLimitingMediaType);
		textViewFlowValue = (TextView) findViewById(R.id.textViewLimitingFlowValue);
		textViewOelPercent = (TextView) findViewById(R.id.textViewLimitingOelPercent);
		textViewDurationValue = (TextView) findViewById(R.id.textViewLimitingDurationValue);
		textViewEquipmentType = (TextView) findViewById(R.id.textViewLimitingEquipmentType);
		textViewLoq = (TextView) findViewById(R.id.textViewLimitingLoq);
		textViewLab = (TextView) findViewById(R.id.textViewLimitingLab);

		seekBarDuration = (SeekBar) findViewById(R.id.seekBarLimitingDuration);
		seekBarFlow = (SeekBar) findViewById(R.id.seekBarLimitingFlow);

		spinnerMethodNames = (Spinner) findViewById(R.id.spinnerLimitingMethodNames);
		spinnerMediaNames = (Spinner) findViewById(R.id.spinnerLimitingMediaNames);
		spinnerOelNames = (Spinner) findViewById(R.id.spinnerLimitingOelNames);

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		if (D)
			Log.d(TAG, "onResume");
		super.onResume();

		mPlan = PlanEditActivity.sPlan;
		if (mPlan == null) {
			if (D)
				Log.w(TAG, "mPlan == null");
			finish();
		} else {
			mPlanMaterial = mPlan.getPlanMaterial(planMaterialIndex);
			mPlanMonster = mPlanMaterial.getPlanMonster();

			onDataReady();
		}

	}

	private void onDataReady() {

		mCurrentTime = mPlan.getPrefTime();

		if (mPlanMaterial.getFlow() > 0) {
			mCurrentFlow = mPlanMaterial.getFlow();
		} else {
			mCurrentFlow = mPlanMonster.getMaxFlow();
		}

		textViewMaterial.setText(mPlanMaterial.getParentListMaterial()
				.getName());
		textViewCas.setText(mPlanMaterial.getParentListMaterial().getCas());

		ArrayAdapter<String> spinnerMethodNamesAdapter = new ArrayAdapter<String>(
				this, R.layout.my_spinner_row, R.id.textViewSpinnerRow,
				mPlanMaterial.getMethodNames());
		spinnerMethodNames.setAdapter(spinnerMethodNamesAdapter);

		ArrayAdapter<String> spinnerMediaNamesAdapter = new ArrayAdapter<String>(
				this, R.layout.my_spinner_row, R.id.textViewSpinnerRow,
				mPlanMaterial.getMediaNames(mPlanMonster.getMethodName()));
		spinnerMediaNames.setAdapter(spinnerMediaNamesAdapter);

		ArrayAdapter<String> spinnerOelsAdapter = new ArrayAdapter<String>(
				this, R.layout.my_spinner_row, R.id.textViewSpinnerRow,
				Oel.getOelNames(mPlanMaterial.getPlanOels()));
		spinnerOelNames.setAdapter(spinnerOelsAdapter);

		mMediaName = mPlanMonster.getMediaName();
		spinnerMediaNames.setSelection(spinnerMediaNamesAdapter
				.getPosition(mMediaName));

		mMethodName = mPlanMonster.getMethodName();
		spinnerMethodNames.setSelection(spinnerMethodNamesAdapter
				.getPosition(mMethodName));

		spinnerOelNames.setSelection(spinnerOelsAdapter
				.getPosition(mPlanMaterial.getPlanOel().getName()));

		spinnerMethodNames.setOnItemSelectedListener(this);
		spinnerMediaNames.setOnItemSelectedListener(this);
		spinnerOelNames.setOnItemSelectedListener(this);

		seekBarDuration.setMax(SEEK_BAR_MAX);
		seekBarDuration.setProgress(SEEK_BAR_MAX / 2);
		seekBarDuration.setOnSeekBarChangeListener(this);

		seekBarFlow.setOnSeekBarChangeListener(this);

		refreshViews();
	}

	/**
	 * Refresh information in views
	 **/
	private void refreshViews() {
		textViewMediaType.setText(mPlanMonster.getMediaTypeName());

		refreshOelPercent();

		textViewDurationValue.setText(mCurrentTime + " "
				+ getResources().getString(R.string.minutes));

		textViewEquipmentType.setText(mPlanMonster.getEquipmentTypeName());
		textViewLoq.setText(String.valueOf(mPlanMonster.getLoq()) + " "
				+ mPlanMonster.getLoqUnitName());
		textViewLab.setText(mPlanMonster.getLabName());

		if (mPlanMonster.getMinFlow() == mPlanMonster.getMaxFlow()) {
			textViewFlowValue.setText(String.valueOf(Math
					.round(mCurrentFlow * 1000.0) / 1000.0) + " lpm");
			seekBarFlow.setVisibility(View.GONE);
		} else {
			textViewFlowValue.setText(String.valueOf(Math
					.round(mCurrentFlow * 100.0) / 100.0) + " lpm");
			seekBarFlow.setVisibility(View.VISIBLE);
			seekBarFlow
					.setMax((int) Math.round((mPlanMonster.getMaxFlow() - mPlanMonster
							.getMinFlow()) * 100));
			seekBarFlow.setProgress((int) Math
					.round((mCurrentFlow - mPlanMonster.getMinFlow()) * 100));
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		mPlanMaterial.setFlow(mCurrentFlow);
		mPlanMaterial.setPlanMonster(mPlanMonster);
		// TODO:
		// mPlanMaterial.setPlanOel(mPlanOel);
		mPlan.setPrefTime(mCurrentTime);

		Intent intent = new Intent(this, WorkerService.class);
		intent.putExtra(WorkerService.REQUEST_CODE,
				WorkerService.SAVE_MATERIAL_DETAIL);
		intent.putExtra("caller", TAG + " updateDb");
		intent.putExtra("planId", mPlan.getId());
		intent.putExtra("materialId", mPlanMaterial.getParentListMaterial()
				.getId());
		intent.putExtra("oelId", mPlanMaterial.getPlanOel().getId());
		intent.putExtra("monsterId", mPlanMaterial.getPlanMonster().getId());
		intent.putExtra("flow", mPlanMaterial.getFlow());
		intent.putExtra("duration", mPlan.getPrefTime());
		startService(intent);

		spinnerBullshit = 0;
	}

	@Override
	protected void onStop() {
		super.onStop();
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

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		if (!fromUser)
			return;

		switch (seekBar.getId()) {
		case R.id.seekBarLimitingDuration:
			int newTime = mCurrentTime;
			int sign;
			int prog = progress - SEEK_BAR_MAX / 2;
			if (prog < 0) {
				sign = -1;
			} else {
				sign = 1;
			}
			prog = Math.abs(prog);

			if (prog < SEEK_BAR_INT1) {
				newTime = mOnStartTrackingTouchTime + sign * prog
						* SEEK_BAR_MOD1;
			} else if (prog < SEEK_BAR_INT1 + SEEK_BAR_INT2) {
				newTime = mOnStartTrackingTouchTime
						+ sign
						* (SEEK_BAR_INT1 * SEEK_BAR_MOD1 + (prog - SEEK_BAR_INT1)
								* SEEK_BAR_MOD2);
			} else if (prog < SEEK_BAR_INT1 + SEEK_BAR_INT2 + SEEK_BAR_INT3) {
				newTime = mOnStartTrackingTouchTime
						+ sign
						* (SEEK_BAR_INT1 * SEEK_BAR_MOD1 + SEEK_BAR_INT2
								* SEEK_BAR_MOD2 + (prog - SEEK_BAR_INT1 - SEEK_BAR_INT2)
								* SEEK_BAR_MOD3);
			} else {
				newTime = mOnStartTrackingTouchTime
						+ sign
						* (SEEK_BAR_INT1 * SEEK_BAR_MOD1 + SEEK_BAR_INT2
								* SEEK_BAR_MOD2 + SEEK_BAR_INT3 * SEEK_BAR_MOD3 + (prog
								- SEEK_BAR_INT1 - SEEK_BAR_INT2 - SEEK_BAR_INT3)
								* SEEK_BAR_MOD4);
			}

			mCurrentTime = newTime;

			if (mCurrentTime < 1) {
				mCurrentTime = 1;
			}
			// mCurrentTime = (progress + mMinTime);
			textViewDurationValue.setText(String.valueOf(mCurrentTime) + " "
					+ getResources().getString(R.string.minutes));
			refreshOelPercent();
			break;
		case R.id.seekBarLimitingFlow:
			mCurrentFlow = (Math.round((progress / 100.0 + mPlanMonster
					.getMinFlow()) * 100.0) / 100.0);
			textViewFlowValue.setText(String.valueOf(mCurrentFlow) + " "
					+ getResources().getString(R.string.lpm));
			refreshOelPercent();
			break;
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		if (seekBar.getId() == R.id.seekBarLimitingDuration) {
			mOnStartTrackingTouchTime = mCurrentTime;
		}
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		if (seekBar.getId() == R.id.seekBarLimitingDuration) {
			seekBarDuration.setProgress(SEEK_BAR_MAX / 2);
		}
	}

	private void refreshOelPercent() {
		mOelPercent = MyMath.calcPlanOelPercent(mPlanMaterial,
				mPlanMaterial.getPlanOel(), mPlanMaterial.getPlanMonster(),
				mCurrentFlow, mCurrentTime);

		int backGroundColor = PlanMaterial.getOelPercentColor(
				mSharedPreferences, mOelPercent);
		textViewOelPercent.setBackgroundColor(backGroundColor);
		if (backGroundColor == Color.YELLOW) {
			textViewOelPercent.setTextColor(Color.BLACK);
		} else {
			textViewOelPercent.setTextColor(Color.WHITE);
		}
		textViewOelPercent.setText("<" + String.valueOf(mOelPercent) + "% OEL");
	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.buttonMaterialDetailDone:
			if (D)
				Log.d(TAG, "onClick Done");

			setResult(RESULT_OK);
			finish();
			break;
		case R.id.buttonMaterialDetailEditOels:
			if (D)
				Log.d(TAG, "onClick Edit");

			Intent i = new Intent(this, OelEditActivity.class);
			i.putExtra("planMaterialIndex", planMaterialIndex);
			startActivityForResult(i, OelEditActivity.EDIT_OEL);
		}
	}

	// private Dialog onCreateDialog() {
	// Log.d(TAG, "onCreateDialog");
	// Dialog dialog = null;
	// AlertDialog.Builder builder = new Builder(this);
	// // TODO: fix hard-coded
	// final String[] OPTIONS = new String[] { "Oels" };
	// final int[] OPTIONS_CODE = new int[] { OELS };
	//
	// // TODO: fix hard-coded
	// builder.setTitle("Edit...");
	//
	// builder.setItems(OPTIONS, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// edit(OPTIONS_CODE[which]);
	// }
	// });
	//
	// dialog = builder.show();
	// return dialog;
	// }
	//
	// private void edit(int code) {
	// if (D)
	// Log.d(TAG, "edit " + code);
	// switch (code) {
	// case OELS:
	// Intent i = new Intent(this, OelEditActivity.class);
	// i.putExtra("planMaterialIndex", planMaterialIndex);
	// startActivityForResult(i, OelEditActivity.EDIT_OEL);
	// break;
	//
	// }
	// }

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (D)
			Log.d(TAG, "onItemSelected");

		if (spinnerBullshit < 3) {
			if (D)
				Log.d(TAG, "ignoring");
			spinnerBullshit++;
			return;
		}

		switch (parent.getId()) {
		case R.id.spinnerLimitingMethodNames:
			mMethodName = parent.getItemAtPosition(position).toString();
			if (D)
				Log.v(TAG, "mMethodName: " + mMethodName);

			ArrayAdapter<String> spinnerMediaNamesAdapter = new ArrayAdapter<String>(
					this, R.layout.my_spinner_row, R.id.textViewSpinnerRow,
					mPlanMaterial.getMediaNames(mMethodName));
			spinnerMediaNames.setAdapter(spinnerMediaNamesAdapter);
			spinnerMediaNames.setOnItemSelectedListener(this);
			break;
		case R.id.spinnerLimitingMediaNames:
			mMediaName = parent.getItemAtPosition(position).toString();
			if (D)
				Log.v(TAG, "mMediaName: " + mMediaName);

			mPlanMaterial.setPlanMonster(mPlanMaterial.getPlanMonster(
					(String) spinnerMethodNames.getSelectedItem(),
					(String) spinnerMediaNames.getSelectedItem()));

			mPlanMonster = mPlanMaterial.getPlanMonster();
			mCurrentFlow = mPlanMonster.getMaxFlow();

			refreshViews();
			break;
		case R.id.spinnerLimitingOelNames:
			mPlanMaterial.setPlanOel(mPlanMaterial
					.getPlanOel((String) spinnerOelNames.getSelectedItem()));

			refreshViews();
		}

	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
