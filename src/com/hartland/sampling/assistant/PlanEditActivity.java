package com.hartland.sampling.assistant;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.commons.lang3.StringEscapeUtils;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PlanEditActivity extends ListActivity implements
		OnSeekBarChangeListener, OnItemSelectedListener {
	public final String TAG = "PlanEditActivity";
	static final boolean D = AssistantApp.D;

	private boolean registered = false;
	private IntentFilter mIntentFilter = new IntentFilter(
			AssistantApp.BROADCAST_ACTION);

	public static final int NEW_PLAN = 1;
	public static final int EDIT_PLAN = 2;

	public static final int SEEK_BAR_MAX = 60;

	public static final int SEEK_BAR_MOD1 = 1;
	public static final int SEEK_BAR_INT1 = 15;

	public static final int SEEK_BAR_MOD2 = 10;
	public static final int SEEK_BAR_INT2 = 10;

	public static final int SEEK_BAR_MOD3 = 50;
	public static final int SEEK_BAR_INT3 = 3;

	public static final int SEEK_BAR_MOD4 = 100;

	private Button buttonName;
	private TextView textViewDuration;
	private SeekBar seekBarDuration;

	private static int mPlanIndex;

	private SharedPreferences mSharedPreferences;

	public static Plan sPlan;
	public static LinkedList<PlanMaterial> planMaterialsToAdd;
	public static LinkedList<Long> planMaterialsToRemove;

	public static SelectedMaterialsAdapter sSelectedMaterialsAdapter;

	private int mOnStartTrackingTouchTime, mCurrentTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (D)
			Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_edit);

		buttonName = (Button) findViewById(R.id.buttonPlanEditName);

		textViewDuration = (TextView) findViewById(R.id.textViewPlanEditDurationValue);

		seekBarDuration = (SeekBar) findViewById(R.id.seekBarPlanEditDuration);

		Intent intent = getIntent();
		mPlanIndex = intent.getIntExtra("planIndex", -1);

	}

	@Override
	protected void onStart() {
		if (D)
			Log.d(TAG, "onStart");
		super.onStart();

		mSharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);

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

		sPlan = PlanListActivity.sPlans.get(mPlanIndex);

		if (sPlan == null) {
			if (D)
				Log.w(TAG, "sPlan == null");
			finish();
		} else {
			onDataReady();
		}

	}

	@Override
	protected void onPause() {
		if (D)
			Log.d(TAG, "onPause");
		super.onPause();

		if (sPlan.getId() == 0) {
			// TODO: wut?
		}

		sPlan.setName(StringEscapeUtils.escapeJava(buttonName.getText()
				.toString()));
		sPlan.setPrefTime(mCurrentTime);

		PlanListActivity.savePlan(this, mPlanIndex, TAG);
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

	@Override
	protected void onDestroy() {
		if (D)
			Log.d(TAG, "onDestroy");
		super.onDestroy();
		sPlan = null;
		sSelectedMaterialsAdapter = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult with requestCode: " + requestCode
					+ " and resultCode: " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);

	}

	public void onClick(View v) {
		if (D)
			Log.d(TAG, "onClick");

		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.buttonPlanEditSave:
			// TODO: handle manual flow
			if (D)
				Log.d(TAG, "case buttonPlanEditSave");
			if (isBlank(buttonName))
				return;
			if (notUnique(buttonName))
				return;

			setResult(RESULT_OK);
			finish();
			break;

		case R.id.buttonPlanEditMaterials:
			if (D)
				Log.d(TAG, "case buttonPlanEditAddMaterial");

			intent.setClass(this, MaterialSelectActivity.class);
			startActivityForResult(intent,
					MaterialSelectActivity.SELECT_MATERIALS);
			break;

		case R.id.buttonPlanEditName:
			if (D)
				Log.d(TAG, "case textViewPlanEditName");

			onCreateDialog();
			break;

		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent intent = new Intent(this, MaterialDetailActivity.class);
		intent.putExtra("planMaterialIndex", position);
		startActivityForResult(intent, MaterialDetailActivity.VIEW_MATERIAL);

	}

	private Boolean isBlank(TextView textViewName2) {
		if (textViewName2.getText().toString().trim().matches("")) {
			Toast.makeText(this,
					this.getResources().getString(R.string.no_blank),
					Toast.LENGTH_SHORT).show();
			textViewName2.requestFocus();
			textViewName2.setHintTextColor(Color.RED);
			return true;
		}
		return false;
	}

	private Boolean notUnique(TextView textViewName2) {
		try {
			for (String planName : PlanListActivity.getPlanNames(TAG)) {
				if (StringEscapeUtils.escapeJava(
						textViewName2.getText().toString()).equals(planName)
						&& !planName.equals(sPlan.getName())) {
					Toast.makeText(
							this,
							this.getResources().getString(
									R.string.must_be_unique),
							Toast.LENGTH_SHORT).show();
					textViewName2.requestFocus();
					TextView tv = (TextView) findViewById(R.id.buttonPlanEditName);
					tv.setTextColor(Color.RED);
					return true;
				}
			}
		} catch (NotFoundException e) {
			throw e;
		} catch (Exception e) {
			if (D)
				Log.w(TAG, "caught " + e);
		}

		return false;
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

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			onHandleIntent(intent);
		}
	};

	private void onHandleIntent(Intent intent) {
		if (D)
			Log.d(TAG, "onHandleIntent");

		// switch based on code corresponding
		switch (intent.getIntExtra(WorkerService.DONE_CODE, -1)) {
		// case WorkerService.SELECT_PLAN:
		// onDataReady();
		// break;
		case WorkerService.PLAN_MODIFIED:
			addRemovePlanMaterials(this);
			onDataReady();
			refreshListView();
			break;
		}
	}

	public static void addRemovePlanMaterials(Context context) {
		synchronized (context) {
			if (planMaterialsToAdd != null) {
				for (PlanMaterial planMaterial : planMaterialsToAdd) {
					// sPlan.addPlanMaterial(planMaterial);
					sSelectedMaterialsAdapter.add(planMaterial);
				}
				planMaterialsToAdd.clear();
			}

			if (planMaterialsToRemove != null) {
				for (Long materialId : planMaterialsToRemove) {
					// sPlan.removePlanMaterial(materialId);
					sSelectedMaterialsAdapter.remove(sPlan
							.getPlanMaterial(materialId));
				}
				planMaterialsToRemove.clear();
			}
		}
	}

	private void onDataReady() {
		if (D)
			Log.d(TAG, "onDataReady");

		mCurrentTime = sPlan.getPrefTime();

		textViewDuration.setText(String.valueOf(mCurrentTime) + " "
				+ getResources().getString(R.string.minutes));

		if (sPlan.mPlanMaterials != null) {

			sPlan.sortPlanMaterials(PlanMaterial.NAME);
			if (sSelectedMaterialsAdapter == null) {
				sSelectedMaterialsAdapter = new SelectedMaterialsAdapter(this,
						R.layout.plan_edit_row, sPlan.mPlanMaterials,
						mSharedPreferences);
				setListAdapter(sSelectedMaterialsAdapter);
			} else {
				sSelectedMaterialsAdapter.notifyDataSetChanged();
			}

		}

		if (sPlan.getName().equals("")) {

			buttonName.setText(PlanListActivity.buildNewPlanName("New Plan",
					PlanListActivity.getPlanNames(TAG), 1, TAG));

		} else {
			buttonName.setText(sPlan.getName());
		}

		seekBarDuration.setMax(SEEK_BAR_MAX);
		seekBarDuration.setProgress(SEEK_BAR_MAX / 2);
		seekBarDuration.setOnSeekBarChangeListener(this);
	}

	private void refreshListView() {
		textViewDuration.setText(String.valueOf(mCurrentTime) + " "
				+ getResources().getString(R.string.minutes));

		if (sPlan.planMaterialsNotNull()) {
			sSelectedMaterialsAdapter.notifyDataSetChanged();
		}

	}

	// OnProgressChange methods
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		if (fromUser) {
			int newTime = sPlan.getPrefTime();
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

			if (newTime < 1) {
				newTime = 1;
			}

			mCurrentTime = newTime;
			sPlan.setPrefTime(mCurrentTime);

			refreshListView();
		}

	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		mOnStartTrackingTouchTime = sPlan.getPrefTime();
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		seekBarDuration.setProgress(SEEK_BAR_MAX / 2);
	}

	private void onCreateDialog() {
		Log.d(TAG, "onCreateDialog");
		AlertDialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// TODO: fix hard-coded
		builder.setTitle("Enter New Plan Name:");

		final EditText input = new EditText(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		input.setText(sPlan.getName());
		input.setSelectAllOnFocus(true);
		builder.setView(input);
		builder.setPositiveButton("DONE",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newName = input.getText().toString();
						sPlan.setName(newName);
						buttonName.setText(newName);

					}
				});
		dialog = builder.create();

		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();

	}

	public class SelectedMaterialsAdapter extends ArrayAdapter<PlanMaterial> {

		private ArrayList<PlanMaterial> mPlanMaterials;
		private SharedPreferences mSharedPreferences;
		private Context mContext;

		public SelectedMaterialsAdapter(Context context,
				int textViewResourceId, ArrayList<PlanMaterial> planMaterials,
				SharedPreferences sharedPreferences) {
			super(context, textViewResourceId, planMaterials);
			// if (D)
			// Log.d(TAG, "new SelectedMaterialsAdapter");
			mPlanMaterials = planMaterials;
			mContext = context;
			mSharedPreferences = sharedPreferences;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.plan_edit_row, null);
			}
			PlanMaterial planMaterial = mPlanMaterials.get(position);

			if (planMaterial != null) {
				Monster monster = planMaterial.getPlanMonster();
				Oel oel = planMaterial.getPlanOel();

				TextView textViewName = (TextView) v
						.findViewById(R.id.textViewPlanEditRowName);
				TextView textViewFlow = (TextView) v
						.findViewById(R.id.textViewPlanEditRowFlow);
				TextView textViewMethod = (TextView) v
						.findViewById(R.id.textViewPlanEditRowMethod);
				TextView textViewMedia = (TextView) v
						.findViewById(R.id.textViewPlanEditRowMedia);
				TextView textViewOelName = (TextView) v
						.findViewById(R.id.textViewPlanEditRowOelValue);
				TextView textViewOelPercent = (TextView) v
						.findViewById(R.id.textViewPlanEditRowOelPercent);

				textViewName.setText(planMaterial.getParentListMaterial()
						.getName());

				textViewMethod.setText(monster.getMethodName());
				textViewMedia.setText(monster.getMediaName());

				Monster bestPlanMonster = planMaterial.getBestPlanMonster();

				textViewOelName.setText(oel.getName());
				// if (planMaterial.lowestOel()) {
				// textViewOelName.setBackgroundColor(Color.TRANSPARENT);
				// } else {
				// textViewOelName.setBackgroundColor(Color.YELLOW);
				// }

				// find appropriate flow
				double flow = planMaterial.getFlow();
				if (flow > 0) {

					if (flow >= monster.getMaxFlow()) {
						flow = monster.getMaxFlow();
						// textViewFlow.setBackgroundColor(Color.TRANSPARENT);
					} else if (flow <= monster.getMinFlow()) {
						flow = monster.getMinFlow();
						// textViewFlow.setBackgroundColor(Color.YELLOW);
					} else {
						flow = planMaterial.getFlow();
						// textViewFlow.setBackgroundColor(Color.YELLOW);
					}

					// default to max flow
				} else {
					flow = monster.getMaxFlow();
					// textViewFlow.setBackgroundColor(Color.TRANSPARENT);
				}

				// change UI to reflect monster and flow
				if (monster.getId() == bestPlanMonster.getId()) {
					textViewMethod.setBackgroundColor(Color.TRANSPARENT);
					textViewMedia.setBackgroundColor(Color.TRANSPARENT);
					if (flow == monster.getMaxFlow()) {
						textViewFlow.setBackgroundColor(Color.TRANSPARENT);
					} else {
						textViewFlow.setBackgroundColor(Color.YELLOW);
					}
				} else if (MyMath.compareDouble(monster.getLoqPerMaxFlow(),
						bestPlanMonster.getLoqPerMaxFlow()) == 0) {

					textViewMethod.setBackgroundColor(Color.TRANSPARENT);
					textViewMedia.setBackgroundColor(Color.TRANSPARENT);
					if (flow == monster.getMaxFlow()) {
						textViewFlow.setBackgroundColor(Color.TRANSPARENT);
					} else {
						textViewFlow.setBackgroundColor(Color.YELLOW);
					}

				} else {
					textViewMethod.setBackgroundColor(Color.YELLOW);
					textViewMedia.setBackgroundColor(Color.YELLOW);
					textViewFlow.setBackgroundColor(Color.YELLOW);
				}
				textViewFlow
						.setText(String.valueOf(Math.round(flow * 1000.0) / 1000.0)
								+ " lpm");

				int oelPercent = MyMath.calcPlanOelPercent(planMaterial, oel,
						monster, flow, mCurrentTime);
				// adjust UI based on oel percent
				// textViewOelPercent.setBackgroundColor(PlanMaterial
				// .getOelPercentColor(mSharedPreferences, oelPercent));
				planMaterial.setOelBackgroundColors(mSharedPreferences,
						oelPercent, textViewOelPercent, mCurrentTime,
						textViewOelName);
				textViewOelPercent.setText("<" + String.valueOf(oelPercent)
						+ "%");

			}
			return v;
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
