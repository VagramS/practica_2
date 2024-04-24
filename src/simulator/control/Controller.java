package simulator.control;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;

public class Controller {
	protected Simulator _sim;

	public Controller(Simulator sim) {
		this._sim = sim;
	}

	public void loadFile(File file) {
		try {
			String content = new String(Files.readAllBytes(file.toPath()));
			JSONObject jsonObj = new JSONObject(content);
			load_data(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load_data(JSONObject data) 
	{
		if (data.has("width") && data.has("height") && data.has("cols") && data.has("rows")) {
            int width = data.getInt("width");
            int height = data.getInt("height");
            int cols = data.getInt("cols");
            int rows = data.getInt("rows");
            _sim.reset(cols, rows, width, height);
        }
		
		if (data.has("regions")) {
			JSONArray regions = data.getJSONArray("regions");
			set_regions(regions);
		}

		if (data.has("animals")) {
			JSONArray animals = data.getJSONArray("animals");
			for (int i = 0; i < animals.length(); i++) {
				JSONObject animal = animals.getJSONObject(i);
				int amount = animal.getInt("amount");
				JSONObject spec = animal.getJSONObject("spec");

				for (int j = 0; j < amount; j++)
					_sim.add_animal(spec);
			}
		}
	}

	public void run(double t, double dt, boolean sv, OutputStream out) {
		SimpleObjectViewer view = null;
		if (sv) {
			MapInfo m = _sim.get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.get_width(), m.get_height(), m.get_cols(), m.get_rows());
			view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}

		JSONObject init_state = _sim.as_JSON();

		while (_sim.get_time() < t) {
			_sim.advance(dt);
			if (sv)
				view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}
		JSONObject final_state = _sim.as_JSON();

		JSONObject res = new JSONObject();
		res.put("in", init_state);
		res.put("out", final_state);

		PrintStream p = new PrintStream(out);
		p.println(res.toString(4));

		view.close();
	}

	private List<SimpleObjectViewer.ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<SimpleObjectViewer.ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals) {
			ol.add(new SimpleObjectViewer.ObjInfo(a.get_genetic_code(), (int) a.get_position().getX(),
					(int) a.get_position().getY(), (int) Math.round(a.get_age()) + 2));
		}
		return ol;
	}

	public void reset(int cols, int rows, int width, int height) {
		_sim.reset(cols, rows, width, height);
	}

	public void set_regions(JSONArray regions) {
		for (int i = 0; i < regions.length(); i++) {
			JSONObject region = regions.getJSONObject(i);
			JSONArray rowRange = region.getJSONArray("row");
			JSONArray colRange = region.getJSONArray("col");
			JSONObject spec = region.getJSONObject("spec");

			for (int r = rowRange.getInt(0); r <= rowRange.getInt(1); r++)
				for (int c = colRange.getInt(0); c <= colRange.getInt(1); c++)
					_sim.set_region(r, c, spec);
		}
	}

	public void advance(double dt) {
		_sim.advance(dt);
	}

	public void addObserver(EcoSysObserver o) {
		_sim.addObserver(o);
	}

	public void removeObserver(EcoSysObserver o) {
		_sim.removeObserver(o);
	}

	public Simulator getSimulator() {
		return _sim;
	}
}
