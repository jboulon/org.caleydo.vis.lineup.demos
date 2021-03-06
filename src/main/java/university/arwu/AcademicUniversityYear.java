/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package university.arwu;

import static demo.RankTableDemo.toDouble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.lineup.data.ADoubleFunction;
import org.caleydo.vis.lineup.data.DoubleInferrers;
import org.caleydo.vis.lineup.data.IDoubleSetterFunction;
import org.caleydo.vis.lineup.model.DoubleRankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.StackedRankColumnModel;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class AcademicUniversityYear {
	// ranking institution country national total alumini award hici nands pub pcb

	public static final int COL_ranking = 0;
	public static final int COL_national = 1;
	public static final int COL_total = 2;
	public static final int COL_alumini = 3;
	public static final int COL_award = 4;
	public static final int COL_hici = 5;
	public static final int COL_nands = 6;
	public static final int COL_pub = 7;
	public static final int COL_pcb = 8;

	private double ranking;
	private double national;
	private double total;
	private double alumini;
	private double award;
	private double hici;
	private double nands;
	private double pub;
	private double pcb;

	public AcademicUniversityYear(String[] l) {
		ranking = toDouble(l, 0);
		national = toDouble(l, 3);
		total = toDouble(l, 4);
		alumini = toDouble(l, 5);
		award = toDouble(l, 6);
		hici = toDouble(l, 7);
		nands = toDouble(l, 8);
		pub = toDouble(l, 9);
		pcb = toDouble(l, 10);
	}

	public double get(int index) {
		switch (index) {
		case COL_ranking:
			return ranking;
		case COL_alumini:
			return alumini;
		case COL_award:
			return award;
		case COL_hici:
			return hici;
		case COL_nands:
			return nands;
		case COL_national:
			return national;
		case COL_pcb:
			return pcb;
		case COL_pub:
			return pub;
		case COL_total:
			return total;
		}
		return 0;
	}

	public double set(int index, double value) {
		switch (index) {
		case COL_ranking:
			return ranking = value;
		case COL_alumini:
			return alumini = value;
		case COL_award:
			return award = value;
		case COL_hici:
			return hici = value;
		case COL_nands:
			return nands = value;
		case COL_national:
			return national = value;
		case COL_pcb:
			return pcb = value;
		case COL_pub:
			return pub = value;
		case COL_total:
			return total = value;
		}
		return 0;
	}

	/**
	 * @param table
	 */
	public static StackedRankColumnModel addYear(RankTableModel table, String title,
			final Function<IRow, AcademicUniversityYear> map) {
		final StackedRankColumnModel stacked = new StackedRankColumnModel();
		stacked.setTitle(title);
		table.add(stacked);
		// Criteria Indicator Code Weight
		// Quality of Education Alumni of an institution winning Nobel Prizes and Fields Medals Alumni 10%
		// Quality of Faculty Staff of an institution winning Nobel Prizes and Fields Medals Award 20%
		// Highly cited researchers in 21 broad subject categories HiCi 20%
		// Research Output Papers published in Nature and Science* N&S 20%
		// Papers indexed in Science Citation Index-expanded and Social Science Citation Index PUB 20%
		// Per Capita Performance Per capita academic performance of an institution PCP 10%

		stacked.add(col(map, COL_alumini,
				"Quality of Education\nAlumni of an institution winning Nobel Prizes and Fields Medals", "#FC9272",
				"#FEE0D2"));
		stacked.add(col(map, COL_award,
				"Quality of Faculty\nStaff of an institution winning Nobel Prizes and Fields Medals", "#9ECAE1",
				"#DEEBF7"));
		stacked.add(col(map, COL_hici, "Quality of Faculty\nHighly cited researchers in 21 broad subject categories",
				"#A1D99B", "#E5F5E0"));
		stacked.add(col(map, COL_nands, "Research Output\nPapers published in Nature and Science", "#C994C7", "#E7E1EF"));
		stacked.add(col(map, COL_pub,
				"Research Output\nPapers indexed in Science Citation Index-expanded and Social Science Citation Index",
				"#FDBB84", "#FEE8C8"));
		stacked.add(col(map, COL_pcb, "Per Capita Performance\nPer capita academic performance of an institution",
				"#DFC27D", "#F6E8C3"));

		stacked.setWeights(new float[] { 10, 20, 20, 20, 20, 10 });
		stacked.setWidth(300);

		return stacked;
	}

	private static DoubleRankColumnModel col(Function<IRow, AcademicUniversityYear> year, int col, String text,
			String color, String bgColor) {
		return new DoubleRankColumnModel(new ValueGetter(year, col), GLRenderers.drawText(text, VAlign.CENTER),
				new Color(color), new Color(bgColor), percentage(), DoubleInferrers.MEDIAN);
	}

	protected static PiecewiseMapping percentage() {
		return new PiecewiseMapping(0, 100);
	}

	public static Map<String, Pair<String, AcademicUniversityYear[]>> readData(int... years) throws IOException {
		Map<String, Pair<String, AcademicUniversityYear[]>> data = new LinkedHashMap<>();
		for (int i = 0; i < years.length; ++i) {
			String year = String.format("argu%4d.txt", years[i]);
			try (BufferedReader r = new BufferedReader(new InputStreamReader(
					AcademicUniversityYear.class.getResourceAsStream(year), Charset.forName("UTF-8")))) {
				String line;
				r.readLine(); // header
				while ((line = r.readLine()) != null) {
					String[] l = line.split("\t");
					String school = l[1];
					String country = l[2];

					AcademicUniversityYear universityYear = new AcademicUniversityYear(l);
					if (!data.containsKey(school)) {
						Pair<String, AcademicUniversityYear[]> s = Pair.make(country,
								new AcademicUniversityYear[years.length]);
						data.put(school, s);
					}
					data.get(school).getSecond()[i] = universityYear;
				}
			}
		}
		return data;
	}

	static class ValueGetter extends ADoubleFunction<IRow> implements IDoubleSetterFunction<IRow> {
		private final int subindex;
		private final Function<IRow, AcademicUniversityYear> year;

		public ValueGetter(Function<IRow, AcademicUniversityYear> year, int column) {
			this.year = year;
			this.subindex = column;
		}

		@Override
		public double applyPrimitive(IRow in) {
			AcademicUniversityYear y = year.apply(in);
			if (y == null)
				return Double.NaN;
			return y.get(subindex);
		}

		@Override
		public void set(IRow in, double value) {
			AcademicUniversityYear y = year.apply(in);
			if (y == null)
				return;
			y.set(subindex, value);
		}
	}
}
