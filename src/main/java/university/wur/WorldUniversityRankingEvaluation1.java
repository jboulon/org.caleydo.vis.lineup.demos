/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package university.wur;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.ARow;
import org.caleydo.vis.rank.model.CategoricalRankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.StringRankColumnModel;

import com.google.common.base.Function;

import demo.RankTableDemo;
import demo.RankTableDemo.IModelBuilder;
import demo.ReflectionData;

/**
 * @author Samuel Gratzl
 *
 */
public class WorldUniversityRankingEvaluation1 implements IModelBuilder {
	@Override
	public void apply(RankTableModel table) throws Exception {
		Map<String, String> countries = WorldUniversityYear.readCountries();

		Map<String, WorldUniversityYear[]> data = WorldUniversityYear.readData(2012);
		// countries.keySet().retainAll(data.keySet());

		List<UniversityRow> rows = new ArrayList<>(data.size());
		for (Map.Entry<String, WorldUniversityYear[]> entry : data.entrySet()) {
			rows.add(new UniversityRow(entry.getKey(), entry.getValue(), countries.get(entry.getKey())));
		}
		table.addData(rows);
		data = null;

		RankRankColumnModel rank = new RankRankColumnModel();
		rank.setWidth(40);
		table.add(rank);
		StringRankColumnModel label = new StringRankColumnModel(GLRenderers.drawText("School Name", VAlign.CENTER),
				StringRankColumnModel.DEFAULT);
		label.setWidth(240);
		table.add(label);

		CategoricalRankColumnModel<String> cat = CategoricalRankColumnModel
				.createSimple(GLRenderers.drawText(
"Country",
				VAlign.CENTER), new ReflectionData<>(UniversityRow.class.getDeclaredField("country"), String.class),
						countries.values());
		table.add(cat);

		WorldUniversityYear.addYear(table, "World University Ranking", new YearGetter(0), false, true);
	}

	@Override
	public Iterable<? extends ARankColumnModel> createAutoSnapshotColumns(RankTableModel table, ARankColumnModel model) {
		Collection<ARankColumnModel> ms = new ArrayList<>(2);
		ms.add(new RankRankColumnModel());
		return ms;
	}

	static class YearGetter implements Function<IRow, WorldUniversityYear> {
		private final int year;

		public YearGetter(int year) {
			this.year = year;
		}

		@Override
		public WorldUniversityYear apply(IRow in) {
			UniversityRow r = (UniversityRow) in;
			return r.years[year];
		}
	}

	static class UniversityRow extends ARow {
		public String schoolname;
		private String country;

		public WorldUniversityYear[] years;

		public UniversityRow(String school, WorldUniversityYear[] years, String country) {
			this.schoolname = school;
			this.years = years;
			this.country = country;
		}

		/**
		 * @return the country, see {@link #country}
		 */
		public String getCountry() {
			return country;
		}

		@Override
		public String toString() {
			return schoolname;
		}
	}

	public static void main(String[] args) {
		// dump();
		GLSandBox.main(args, RankTableDemo.class, "WUR Eval 2012",
				new WorldUniversityRankingEvaluation1());
	}
}