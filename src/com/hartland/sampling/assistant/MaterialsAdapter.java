package com.hartland.sampling.assistant;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MaterialsAdapter extends ArrayAdapter<ListMaterial> implements
		OnClickListener {
	private static final String TAG = "MaterialsAdapter";
	// public static final int ID = AssistantApp.MATERIAL_SELECT_ID;
	static final boolean D = AssistantApp.D;

	// private AssistantApp mAssistantApp;

	// materials associated with this plan

	private final ArrayList<ListMaterial> allListMaterials;
	private final Activity context;

	public MaterialsAdapter(Activity context,
			ArrayList<ListMaterial> allListMaterials) {
		super(context, R.layout.select_material_row, allListMaterials);
		this.context = context;
		this.allListMaterials = allListMaterials;

	}

	static class ViewHolder {
		protected CheckBox checkBox;
		protected TextView tvName;
		protected TextView tvCas;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		//
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.select_material_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.checkBox = (CheckBox) view
					.findViewById(R.id.checkBoxMaterialSelectRowSelected);
			viewHolder.tvName = (TextView) view
					.findViewById(R.id.textViewMaterialSelectRowName);
			viewHolder.tvCas = (TextView) view
					.findViewById(R.id.textViewMaterialSelectRowCas);
			viewHolder.checkBox.setOnClickListener(this);
			view.setTag(viewHolder);
			viewHolder.checkBox.setTag(allListMaterials.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkBox.setTag(allListMaterials
					.get(position));
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		holder.tvName.setText(allListMaterials.get(position).getName());
		holder.tvCas.setText(allListMaterials.get(position).getCas());

		long planMaterialId = allListMaterials.get(position).getId();
		holder.checkBox.setChecked(PlanEditActivity.sPlan
				.hasPlanMaterial(planMaterialId));

		return view;
	}

	public void onClick(View view) {
		if (D)
			Log.v(TAG, "onClick");
		Plan.log(TAG, PlanEditActivity.sPlan);
		// mPlanMaterials = Data.plan.getPlanMaterials();
		ListMaterial listMaterial = (ListMaterial) view.getTag();
		CheckBox cb = (CheckBox) view;
		Intent intent = new Intent(context, WorkerService.class);
		if (cb.isChecked()) {
			Log.v(TAG, " isChecked material name: " + listMaterial.getName());

			intent.putExtra(WorkerService.REQUEST_CODE,
					WorkerService.SAVE_NEW_PLAN_MATERIAL);
			intent.putExtra("caller", TAG);
			intent.putExtra("planId", PlanEditActivity.sPlan.getId());
			intent.putExtra("materialId", listMaterial.getId());
			context.startService(intent);

			intent.putExtra(WorkerService.REQUEST_CODE,
					WorkerService.SELECT_PLAN_MATERIAL);
			context.startService(intent);

			intent.putExtra(WorkerService.REQUEST_CODE,
					WorkerService.NOTIFY_DONE);
			intent.putExtra(WorkerService.DONE_CODE,
					WorkerService.PLAN_MODIFIED);
			context.startService(intent);

		} else {
			Log.v(TAG, " not Checked material name: " + listMaterial.getName());

			intent.putExtra(WorkerService.REQUEST_CODE,
					WorkerService.DELETE_PLAN_MATERIAL);
			intent.putExtra("caller", TAG);
			intent.putExtra("planId", PlanEditActivity.sPlan.getId());
			intent.putExtra("materialId", listMaterial.getId());
			context.startService(intent);

			intent.putExtra(WorkerService.REQUEST_CODE,
					WorkerService.NOTIFY_DONE);
			intent.putExtra(WorkerService.DONE_CODE,
					WorkerService.PLAN_MODIFIED);
			context.startService(intent);

		}

	}

}
