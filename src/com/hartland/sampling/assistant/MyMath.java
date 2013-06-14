package com.hartland.sampling.assistant;

import android.util.Log;

public class MyMath {
	private static final String TAG = "MyMath";
	@SuppressWarnings("unused")
	private static final boolean D = AssistantApp.D;

	// used evaluate doubles as equal if they match to this many places
	private static final int DEC_PLACES = 10000000;

	private static final String OEL_STANDARD_UNIT = "mg/m3";
	private static final String LOQ_STANDARD_UNIT = "mg";

	public static final int compareLoq(Monster m1, Monster m2) {

		// convert loq1 to units of loq2
		double loq1 = convertUnits(m1.getLoq(), m1.getLoqUnitName(),
				m2.getLoqUnitName(), -1d);
		double loq2 = m2.getLoq();

		return compareDouble(loq1, loq2);
	}

	public static final int compareLoqPerMaxFlow(Monster m1, Monster m2) {

		// convert loq1 to units of loq2
		double loq1 = convertUnits(m1.getLoq(), m1.getLoqUnitName(),
				m2.getLoqUnitName(), -1d);

		double maxFlow1 = m1.getMaxFlow();
		double loq2 = m2.getLoq();
		double maxFlow2 = m2.getMaxFlow();

		double d1 = loq1 / maxFlow1;
		double d2 = loq2 / maxFlow2;

		return compareDouble(d1, d2);
	}

	// public static final int compareLoqPerFlow(double flow1, Monster m1,
	// Monster m2) {
	//
	// // convert loq1 to units of loq2
	// double loq1 = convertUnits(m1.getLoq(), m1.getLoqUnitName(),
	// m2.getLoqUnitName(), -1d);
	//
	// double loq2 = m2.getLoq();
	// double maxFlow2 = m2.getMaxFlow();
	//
	// double d1 = loq1 / flow1;
	// double d2 = loq2 / maxFlow2;
	//
	// return compareDouble(d1, d2);
	// }

	public static final int compareLoqPerMaxFlowPerOel(PlanMaterial m1,
			PlanMaterial m2) {

		// convert units of loq1 to units of loq2
		double loq1 = convertUnits(m1.getPlanMonster().getLoq(), m1
				.getPlanMonster().getLoqUnitName(), m2.getPlanMonster()
				.getLoqUnitName(), -1d);
		double maxFlow1 = m1.getPlanMonster().getMaxFlow();
		// convert units of oel1 to units of oel2
		double oel1 = convertUnits(m1.getPlanOel().getValue(), m1.getPlanOel()
				.getUnit().getName(), m2.getPlanOel().getUnit().getName(), m1
				.getParentListMaterial().getMolW());

		double loq2 = m2.getPlanMonster().getLoq();
		double maxFlow2 = m2.getPlanMonster().getMaxFlow();
		double oel2 = m2.getPlanOel().getValue();

		double d1 = loq1 / maxFlow1 / oel1;
		double d2 = loq2 / maxFlow2 / oel2;

		return compareDouble(d1, d2);

	}

	public static final int compareOel(Oel o1, Oel o2, double molW) {
		// convert units of oel1 to units of oel2
		double oel1 = convertUnits(o1.getValue(), o1.getUnit().getName(), o2
				.getUnit().getName(), molW);
		double oel2 = o2.getValue();

		return compareDouble(oel1, oel2);
	}

	public static final int calcPlanOelPercent(PlanMaterial mat, Oel o,
			Monster mon, double flow, int time) {

		// convert oel and loq to standard units
		double oel = convertUnits(o.getValue(), o.getUnit().getName(),
				OEL_STANDARD_UNIT, mat.getParentListMaterial().getMolW());
		double loq = convertUnits(mon.getLoq(), mon.getLoqUnitName(),
				LOQ_STANDARD_UNIT, mat.getParentListMaterial().getMolW());

		// convert flow from l/min to m3/min

		flow = flow / 1000;

		// loq in mg
		// flow in m3/min

		// oel in mg/m3
		// time in min

		// mg/m3/m over mg/m3/m
		double unrounded = loq / flow / oel / time;

		// rounds up to nearest percent
		return (int) Math.ceil(unrounded);
	}

	public static final int compareDouble(double d1, double d2) {
		if (Math.round((d1 - d2) * DEC_PLACES) == 0) {
			return 0;
		} else if (d1 > d2) {
			return 1;
		} else if (d1 < d2) {
			return -1;
		} else {
			Log.e(TAG, "compareDouble error");
			return -2;
		}
	}

	/**
	 * Converts a value from a volume (exa. *g/m3 or pp*) to another volume.
	 * Also can convert a mass (exa. *g) to another mass. Returns unchanged
	 * value if units are the same. Pass a molW for conversions across *g/m3
	 * <--> pp* or -1d if it is not needed.
	 **/
	private static final double convertUnits(double value, String fromUnit,
			String toUnit, Double fromMolW) {

		// determine unit relationship, convert
		if (fromUnit.endsWith("g/m3") && toUnit.endsWith("g/m3")
				&& fromUnit.length() == 5 && toUnit.length() == 5) {
			if (fromUnit.startsWith("m")) {
				if (toUnit.startsWith("m")) {
					return value;
				} else if (toUnit.startsWith("u")) {
					return value * 1000;
				} else if (toUnit.startsWith("n")) {
					return value * 1000000;
				} else {
					Log.e(TAG, "unrecognized unit prefix to *g/m3: " + toUnit);
				}
			} else if (fromUnit.startsWith("u")) {
				if (toUnit.startsWith("m")) {
					return value / 1000;
				} else if (toUnit.startsWith("u")) {
					return value;
				} else if (toUnit.startsWith("n")) {
					return value * 1000;
				} else {
					Log.e(TAG, "unrecognized unit prefix to *g/m3: " + toUnit);
				}
			} else if (fromUnit.startsWith("n")) {
				if (toUnit.startsWith("u")) {
					return value / 1000;
				} else if (toUnit.startsWith("n")) {
					return value;
				} else if (toUnit.startsWith("m")) {
					return value / 1000000;
				} else {
					Log.e(TAG, "unrecognized unit prefix to *g/m3: " + toUnit);
				}
			} else {
				Log.e(TAG, "unrecognized unit prefix to *g/m3: " + fromUnit);
			}
		} else if (fromUnit.startsWith("pp") && toUnit.startsWith("pp")
				&& fromUnit.length() == 3 && toUnit.length() == 3) {
			if (fromUnit.endsWith("m")) {
				if (toUnit.endsWith("m")) {
					return value;
				} else if (toUnit.endsWith("b")) {
					return value * 1000;
				} else if (toUnit.endsWith("t")) {
					return value * 1000000;
				} else {
					Log.e(TAG, "unrecognized unit suffix to pp*: " + toUnit);
				}
			} else if (fromUnit.endsWith("b")) {
				if (toUnit.endsWith("m")) {
					return value / 1000;
				} else if (toUnit.endsWith("b")) {
					return value;
				} else if (toUnit.endsWith("t")) {
					return value * 1000;
				} else {
					Log.e(TAG, "unrecognized unit suffix to pp*: " + toUnit);
				}
			} else if (fromUnit.endsWith("t")) {
				if (toUnit.endsWith("m")) {
					return value / 1000000;
				} else if (toUnit.endsWith("b")) {
					return value / 1000;
				} else if (toUnit.endsWith("t")) {
					return value;
				} else {
					Log.e(TAG, "unrecognized unit suffix to pp*: " + toUnit);
				}
			} else {
				Log.e(TAG, "unrecognized unit suffix to pp*: " + fromUnit);
			}
		} else if (fromUnit.endsWith("g") && fromUnit.length() == 2
				&& toUnit.endsWith("g") && toUnit.length() == 2) {
			if (fromUnit.startsWith("m")) {
				if (toUnit.startsWith("m")) {
					return value;
				} else if (toUnit.startsWith("u")) {
					return value * 1000;
				} else if (toUnit.startsWith("n")) {
					return value * 1000000;
				} else {
					Log.e(TAG, "unrecognized prefix to *g: " + toUnit);
				}
			} else if (fromUnit.startsWith("u")) {
				if (toUnit.startsWith("m")) {
					return value / 1000;
				} else if (toUnit.startsWith("u")) {
					return value;
				} else if (toUnit.startsWith("n")) {
					return value * 1000;
				} else {
					Log.e(TAG, "unrecognized prefix to *g: " + toUnit);
				}
			} else if (fromUnit.startsWith("n")) {
				if (toUnit.startsWith("m")) {
					return value / 1000000;
				} else if (toUnit.startsWith("u")) {
					return value / 1000;
				} else if (toUnit.startsWith("n")) {
					return value;
				} else {
					Log.e(TAG, "unrecognized prefix to *g: " + toUnit);
				}
			} else {
				Log.e(TAG, "unrecognized unit prefix to *g: " + fromUnit);
			}
		} else {
			if (fromMolW == -1d) {
				Log.e(TAG, "complex unit conversion passed without molW. "
						+ fromUnit + " to " + toUnit);
				return -1;
			}
			// convert *g/m3 to mg/m3
			if (fromUnit.endsWith("g/m3") && fromUnit.length() == 5) {
				// convert value to mg/m3
				value = convertUnits(value, fromUnit, "mg/m3", -1d);
				if (toUnit.startsWith("pp") && toUnit.length() == 3) {
					// convert value from mg/m3 to ppm
					value = 24.45 * value / fromMolW;
					// finish simple conversion and return
					return convertUnits(value, "ppm", toUnit, -1d);
				} else {
					Log.e(TAG, "unrecognized unit: " + toUnit);
				}
				// convert pp* to ppm
			} else if (fromUnit.startsWith("pp") && fromUnit.length() == 3) {
				// convert value to ppm
				value = convertUnits(value, fromUnit, "ppm", -1d);
				if (toUnit.endsWith("g/m3") && toUnit.length() == 5) {
					// convert value from ppm to mg/m3
					value = fromMolW * value / 24.45;
					// finish simple conversion and return
					return convertUnits(value, "mg/m3", toUnit, -1d);
				} else {
					Log.e(TAG, "unrecognized unit: " + toUnit);
				}
			} else {
				Log.e(TAG, "unrecognized unit: " + fromUnit);
			}
		}
		// errors logged above
		// TODO: what does this do?
		return Double.NaN;
	}
//
//	// TODO: improve this method
//	public static ArrayList<String> filterUnits(ArrayList<String> units) {
//		int size = units.size();
//		for (int i = size - 1; i >= 0; i--) {
//			if (units.get(i).length() < 3) {
//				units.remove(i);
//			}
//		}
//		return units;
//	}
}
