/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package university.top100under50;

import static demo.RankTableDemo.toDouble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.lineup.data.ADoubleFunction;
import org.caleydo.vis.lineup.data.DoubleInferrers;
import org.caleydo.vis.lineup.data.IDoubleInferrer;
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
public class Top100Under50Year {
	public static final int COL_ranking = 0;
	public static final int COL_teaching = 1;
	public static final int COL_research = 2;
	public static final int COL_citations = 3;
	public static final int COL_incomeFromIndustry = 4;
	public static final int COL_internationalMix = 5;
	public static final int COL_overall = 6;

	private double ranking;
	private double teaching;
	private double research;
	private double citations;
	private double incomeFromIndustry;
	private double internationalMix;
	private double overall;

	// 100 Under 50 rank World University Rankings 2011-2012 position Institution Lat, long Country Year founded
	// Teaching Research Citations Income from Industry International mix Overall score


	public Top100Under50Year(String[] l) {
		ranking = toDouble(l, 0);
		teaching = toDouble(l, 6);
		research = toDouble(l, 7);
		citations = toDouble(l, 8);
		incomeFromIndustry = toDouble(l, 9);
		internationalMix = toDouble(l, 10);
		overall = toDouble(l, 11);
	}

	public double get(int index) {
		switch (index) {
		case COL_ranking:
			return ranking;
		case COL_citations:
			return citations;
		case COL_incomeFromIndustry:
			return incomeFromIndustry;
		case COL_internationalMix:
			return internationalMix;
		case COL_overall:
			return overall;
		case COL_research:
			return research;
		case COL_teaching:
			return teaching;
		}
		return 0;
	}

	public void set(int index, double value) {
		switch (index) {
		case COL_ranking:
			ranking = value;
			break;
		case COL_citations:
			citations = value;
			break;
		case COL_incomeFromIndustry:
			incomeFromIndustry = value;
			break;
		case COL_internationalMix:
			internationalMix = value;
			break;
		case COL_overall:
			overall = value;
			break;
		case COL_research:
			research = value;
			break;
		case COL_teaching:
			teaching = value;
			break;
		}
	}

	/**
	 * @param table
	 */
	public static StackedRankColumnModel addYear(RankTableModel table, String title,
			final Function<IRow, Top100Under50Year> map, IDoubleInferrer inf) {
		final StackedRankColumnModel stacked = new StackedRankColumnModel();
		stacked.setTitle(title);
		table.add(stacked);
		// Research: volume, income and reputation (30 per cent)
		// Citations: research influence (30 per cent)
		// Teaching: the learning environment (30 per cent)
		// International outlook: people and research (7.5 per cent)
		// Industry income: innovation (2.5 per cent).
		stacked.add(col(map, COL_research, "Research", "#FC9272", "#FEE0D2", inf));
		stacked.add(col(map, COL_citations, "Citations", "#9ECAE1", "#DEEBF7", inf));
		stacked.add(col(map, COL_teaching, "Teaching", "#A1D99B", "#E5F5E0", inf));
		stacked.add(col(map, COL_internationalMix, "International outlook", "#C994C7", "#E7E1EF", inf));
		stacked.add(col(map, COL_incomeFromIndustry, "Industry income", "#FDBB84", "#FEE8C8", inf));

		stacked.setWeights(new float[] { 30, 30, 30, 7.5f, 2.5f });
		stacked.setWidth(400);

		return stacked;
	}

	public static void addOverallYear(RankTableModel table, String title, Function<IRow, Top100Under50Year> map) {
		table.add(col(map, COL_overall, title, "#DFC27D", "#F6E8C3", DoubleInferrers.MEDIAN));
	}

	private static DoubleRankColumnModel col(Function<IRow, Top100Under50Year> year, int col, String text,
 String color,
			String bgColor, IDoubleInferrer inf) {
		return new DoubleRankColumnModel(new ValueGetter(year, col), GLRenderers.drawText(text, VAlign.CENTER),
				new Color(color), new Color(bgColor), percentage(), inf);
	}

	protected static PiecewiseMapping percentage() {
		return new PiecewiseMapping(0, 100);
	}

	public static Map<String, Row> readData(int... years) throws IOException {
		Map<String, Row> data = new LinkedHashMap<>();
		for (int i = 0; i < years.length; ++i) {
			String year = String.format("THE100Under50rankings%4d.txt", years[i]);
			// 100 Under 50 rank World University Rankings 2011-2012 position Institution Lat, long Country Year founded
			// Teaching Research Citations Income from Industry International mix Overall score

			try (BufferedReader r = new BufferedReader(new InputStreamReader(
					Top100Under50Year.class.getResourceAsStream(year), Charset.forName("UTF-8")))) {
				String line;
				r.readLine(); // header
				while ((line = r.readLine()) != null) {
					String[] l = line.split("\t");
					String school = l[2];
					String location = l[3];
					String country = l[4];
					int yearFounded = Integer.parseInt(l[5]);

					Top100Under50Year universityYear = new Top100Under50Year(l);
					if (!data.containsKey(school)) {
						Row row = new Row(country, location, yearFounded, new Top100Under50Year[years.length]);
						data.put(school, row);
					}
					data.get(school).years[i] = universityYear;
				}
			}
		}
		return data;
	}

	static class ValueGetter extends ADoubleFunction<IRow> implements IDoubleSetterFunction<IRow> {
		private final int subindex;
		private final Function<IRow, Top100Under50Year> year;

		public ValueGetter(Function<IRow, Top100Under50Year> year, int column) {
			this.year = year;
			this.subindex = column;
		}

		@Override
		public double applyPrimitive(IRow in) {
			Top100Under50Year y = year.apply(in);
			if (y == null)
				return Double.NaN;
			return y.get(subindex);
		}

		@Override
		public void set(IRow in, double value) {
			Top100Under50Year y = year.apply(in);
			if (y == null)
				return;
			y.set(subindex, value);
		}
	}

	public static class Row {

		public String country;
		public String location;
		public int yearFounded;

		public Top100Under50Year[] years;

		public Row(String country, String location, int yearFounded2, Top100Under50Year[] top100Under50Years) {
			this.country = country;
			this.location = location;
			this.yearFounded = yearFounded2;
			this.years = top100Under50Years;
		}

	}
}
