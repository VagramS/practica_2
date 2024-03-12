package simulator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegionManager implements AnimalMapView {
	protected int _cols;
	protected int _rows;
	protected int _width;
	protected int _height;
	protected int _regionWidth;
	protected int _regionHeight;
	protected Region[][] _regions;
	protected Map<Animal, Region> _animal_region;

	public RegionManager(int cols, int rows, int width, int height) {
		this._cols = cols;
		this._rows = rows;
		this._width = width;
		this._height = height;
		this._regionWidth = _width / _cols + (width % cols != 0 ? 1 : 0);
		this._regionHeight = _height / _rows + (height % rows != 0 ? 1 : 0);

		this._regions = new Region[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				this._regions[i][j] = new DefaultRegion();
			}
		}

		this._animal_region = new HashMap<>();
	}

	void set_region(int row, int col, Region r) {
		if (col >= 0 && col < _cols && row >= 0 && row < _rows) {
			Region old_reg = _regions[row][col];
			_regions[row][col] = r;

			if (old_reg != null) {
				List<Animal> toTransfer = old_reg.getAnimals();

				for (Animal a : toTransfer) {
					r.add_animal(a);
					old_reg.remove_animal(a);
					_animal_region.put(a, r);
				}
			}
		} else
			System.out.println("Specified location is out of map.");
	}

	void register_animal(Animal a) {
		a.init(this);
		int col = (int) a._pos.getX() / this.get_region_width();
		int row = (int) a._pos.getY() / this.get_region_height();

		if (col >= 0 && col < _cols && row >= 0 && row < _rows) {
			Region reg = _regions[row][col];
			reg.add_animal(a);
			this._animal_region.put(a, reg);
		}
	}

	void unregister_animal(Animal a) {
		int col = (int) a._pos.getX() / this.get_region_width();
		int row = (int) a._pos.getY() / this.get_region_height();

		if (col >= 0 && col < _cols && row >= 0 && row < _rows) {
			Region region = _regions[row][col];
			region.remove_animal(a);
			_animal_region.remove(a);
		}
	}

	void update_animal_region(Animal a) {
		if (_animal_region.containsKey(a)) {
			Region reg_anim = _animal_region.get(a);
			_animal_region.remove(a);
			reg_anim.remove_animal(a);
		}
	}

	public double get_food(Animal a, double dt) {
		int col = (int) a.get_position().getX() / _regionWidth;
		int row = (int) a.get_position().getY() / _regionHeight;

		if (row >= 0 && row < _rows && col >= 0 && col < _cols) {
			Region region = _regions[row][col];
			return region.get_food(a, dt);
		} else {
			System.err.println("Animal's position is out of the valid region range.");
			return 0.0;
		}
	}

	void update_all_regions(double dt) {
		for (int i = 0; i < _rows; i++) {
			for (int j = 0; j < _cols; j++) {
				if (_regions[i][j] != null)
					_regions[i][j].update(dt);
			}
		}
	}

	public List<Animal> get_animals_in_range(Animal a, Predicate<Animal> filter) {
		List<Animal> animalsInRange = _animal_region.keySet().stream()
				.filter(animal -> animal.get_position().distanceTo(a.get_position()) <= a.get_sight_range())
				.filter(filter).collect(Collectors.toList());

		return animalsInRange;
	}

	public JSONObject as_JSON() {
		JSONArray regArray = new JSONArray();

		for (int i = 0; i < _rows; i++) {
			for (int j = 0; j < _cols; j++) {
				JSONObject region = new JSONObject();
				region.put("row", i);
				region.put("col", j);
				region.put("data", _regions[i][j].as_JSON());
				regArray.put(region);
			}
		}

		JSONObject res = new JSONObject();
		res.put("regiones", regArray);

		return res;

//        	"regiones":[𝑜1, ,...],
//        	"row": i,
//        	"col": j,
//        	"data": r
	}

	public int get_cols() {
		return this._cols;
	}

	public int get_rows() {
		return this._rows;
	}

	public int get_width() {
		return this._width;
	}

	public int get_height() {
		return this._height;
	}

	public int get_region_width() {
		return this._regionWidth;
	}

	public int get_region_height() {
		return this._regionHeight;
	}
}
