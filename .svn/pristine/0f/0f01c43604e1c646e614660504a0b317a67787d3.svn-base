package com.hartland.sampling.assistant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringEscapeUtils;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PlanListActivity extends ListActivity implements
		OnItemLongClickListener {
	public final String TAG = "PlanListActivity";
	static final boolean D = AssistantApp.D;

	private PlansAdapter mPlansAdapter;

	private Button buttonNewPlan;

	// tracks whether broadcast reciever is registered
	private boolean registered = false;
	private IntentFilter mIntentFilter = new IntentFilter(
			AssistantApp.BROADCAST_ACTION);

	public static ArrayList<Plan> sPlans;
	private static LinkedList<Plan> sPlansToSave = new LinkedList<Plan>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (D)
			Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_list);

		getListView().setOnItemLongClickListener(this);

		buttonNewPlan = (Button) findViewById(R.id.buttonPlanListNewPlan);

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

		if (sPlans != null) {
			onDataReady();
		} else {
			Intent i = new Intent(this, WorkerService.class);
			i.putExtra(WorkerService.REQUEST_CODE, WorkerService.SELECT_PLANS);
			i.putExtra("caller", TAG);
			startService(i);

			i.putExtra(WorkerService.REQUEST_CODE, WorkerService.NOTIFY_DONE);
			i.putExtra(WorkerService.DONE_CODE, WorkerService.SELECT_PLANS);
			i.putExtra("caller", TAG);
			startService(i);

			buttonNewPlan.setClickable(false);
		}

	}

	@Override
	protected void onPause() {
		if (D)
			Log.d(TAG, "onPause");
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

	@Override
	protected void onDestroy() {
		if (D)
			Log.d(TAG, "onDestroy");
		super.onDestroy();
		sPlans = null;
		// sPlansToSave = null;
	}

	public static void savePlan(Context context, int planIndex, String tag) {
		if (D)
			Log.d(tag, "savePlan");
		Plan planToSave = sPlans.get(planIndex);
		if (D)
			Plan.log(tag, planToSave);
		planToSave.setEdited(Calendar.getInstance().getTimeInMillis());
		sPlansToSave.add(planToSave.copy());

		Intent i = new Intent(context, WorkerService.class);
		i.putExtra(WorkerService.REQUEST_CODE, WorkerService.SAVE_PLAN);
		i.putExtra("caller", tag);
		i.putExtra("planIndex", planIndex);
		context.startService(i);
	}

	public static Plan getPlanToSave(String tag) {
		if (D)
			Log.d(tag, "getListPlanToSave caller: " + tag);
		Plan p = null;
		try {
			p = sPlansToSave.getFirst();
		} catch (NoSuchElementException e) {
			Log.e(tag, "caught " + e + "--> returning null");
			return null;
		}
		sPlansToSave.removeFirst();
		if (D)
			Plan.log(tag, p);
		return p;
	}

	public static String[] getPlanNames(String tag) {
		if (D)
			Log.d(tag, "getPlanNames called by: " + tag);
		if (sPlans == null) {
			return new String[] {};
		} else {
			int size = sPlans.size();
			String[] planNames = new String[size];
			// used to ensure unique name
			for (int i = 0; i < size; i++) {
				planNames[i] = StringEscapeUtils.unescapeJava(sPlans.get(i)
						.getName());
			}
			return planNames;
		}
	}

	public void onClick(View v) {
		if (D)
			Log.d(TAG, "onClick");
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.buttonPlanListNewPlan:
			if (D)
				Log.d(TAG, "case buttonPlanListNewPlan");

			Plan newPlan = new Plan();
			newPlan.setName(buildNewPlanName(newPlan.getName(),
					getPlanNames(TAG), 1, TAG));
			sPlans.add(newPlan);
			int listPlanIndex = sPlans.size() - 1;

			savePlan(this, listPlanIndex, TAG);
			// Data.listPlan = Data.listPlans.get(listPlanIndex);

			intent.setClass(this, PlanEditActivity.class);
			intent.putExtra("planIndex", listPlanIndex);

			startActivityForResult(intent, PlanEditActivity.NEW_PLAN);
			break;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (D)
			Log.d(TAG, "OnListItemClick position: " + position + " id: " + id);
		super.onListItemClick(l, v, position, id);

		// TODO: possible case where new item is re-selected before ID is
		// updated from worker thread. Check for ID == 0, wait until update to
		// start next activity
		Plan plan = (Plan) getListView().getItemAtPosition(position);

		int size = sPlans.size();
		String[] planNames = new String[size];
		for (int i = 0; i < size; i++) {
			planNames[i] = StringEscapeUtils.unescapeJava(sPlans.get(i)
					.getName());
		}

		int planIndex = (int) id;

		// Data.listPlan = Data.listPlans.get(listPlanIndex);

		Intent intent = new Intent(this, PlanEditActivity.class);
		intent.putExtra("planIndex", planIndex);
		if (plan.equals(sPlans.get(planIndex))) {
			if (D)
				Log.v(TAG,
						"item at this id matches corresponding item at ArrayList<ListPlan> index");
		} else {
			Log.e(TAG, "item at id(" + planIndex
					+ ") does not match plans.get(" + planIndex + ")");
			finish();
		}

		startActivityForResult(intent, PlanEditActivity.EDIT_PLAN);
	}

	public boolean onItemLongClick(final AdapterView<?> arg0, final View view,
			final int position, long id) {
		if (D)
			Log.d(TAG, "onItemLongClick");
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
								long listPlanId = sPlans.get(position).getId();

								mPlansAdapter.remove(sPlans.get(position));
								// sPlans.remove(position);
								// refreshListView();

								Intent i = new Intent(view.getContext(),
										WorkerService.class);
								i.putExtra(WorkerService.REQUEST_CODE,
										WorkerService.DELETE_PLAN);
								i.putExtra("listPlanId", listPlanId);
								i.putExtra("caller", TAG);
								startService(i);

							}
						}).setNegativeButton(android.R.string.no, null).show();
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

	private class PlansAdapter extends ArrayAdapter<Plan> {

		// private ArrayList<ListPlan> listPlans;

		public PlansAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId, sPlans);
			// this.listPlans = mListPlans;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.plan_list_row, null);
			}
			Plan plan = sPlans.get(position);
			if (plan != null) {
				TextView textViewName = (TextView) v
						.findViewById(R.id.textViewPlanListName);
				TextView textViewMaterials = (TextView) v
						.findViewById(R.id.textViewPlanListMaterials);
				TextView textViewEdited = (TextView) v
						.findViewById(R.id.textViewPlanListEdited);

				if (textViewName != null) {
					textViewName.setText(plan.getName());
				}
				if (textViewMaterials != null) {
					textViewMaterials.setText(plan.getMaterialsString());
				}
				if (textViewEdited != null) {
					textViewEdited.setText(plan.getEditedString());
				}
			}
			return v;
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
		case -1:
			Log.e(TAG, "invalid done code");
			break;
		case WorkerService.SELECT_PLANS:
			if (sPlans != null) {
				onDataReady();
			} else {
				Log.e(TAG,
						"WorkerService returned notify done with object still null. Exiting.");
				finish();
			}

			break;
		}
	}

	private void onDataReady() {
		buttonNewPlan.setClickable(true);

		// if (mPlansAdapter == null) {
		mPlansAdapter = new PlansAdapter(this, R.layout.plan_list_row);
		setListAdapter(mPlansAdapter);
		// } else {
		// mPlansAdapter.notifyDataSetChanged();
		// mPlansAdapter.
		// }
	}

	public static String buildNewPlanName(String newPlanName,
			String[] planNames, int attempt, String tag) {
		if (D)
			Log.d(tag, "buildNewPlanName");
		if (D)
			Log.v(tag,
					"newPlanName: " + StringEscapeUtils.escapeJava(newPlanName)
							+ " attempt: " + attempt);
		int nameModifier = attempt + 1;
		if (planNames.length == 0) {
			// TODO: hard-coded
			newPlanName = "New Plan";
		} else {

			for (String planName : planNames) {
				// if (D)
				// Log.v(TAG,
				// "planName: " + StringEscapeUtils.escapeJava(planName));
				if (newPlanName.equals(planName)) {
					// if (D)
					// Log.v(TAG, "equal");
					newPlanName = buildNewPlanName(
							// TODO: hard-coded
							"New Plan(" + String.valueOf(nameModifier) + ")",
							planNames, nameModifier, tag);
					break;
				}
			}
		}
		return newPlanName;
	}

	// private void refreshListView() {
	// if (D)
	// Log.d(TAG, "refreshListView");
	// if (sPlans != null) {
	// plansAdapter = new PlansAdapter(this, R.layout.plan_list_row);
	// setListAdapter(plansAdapter);
	// } else {
	// if (D)
	// Log.d(TAG, "no plans");
	// }
	// }

}
