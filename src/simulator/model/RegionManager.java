package simulator.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
			for (int j = 0; j < cols; j++) 
				this._regions[i][j] = new DefaultRegion();
		}

		this._animal_region = new HashMap<>();
	}
	

	Region get_region(Animal a)
	{
		Region reg = null;
		int col = (int) a._pos.getX() / this.get_region_width();
		int row = (int) a._pos.getY() / this.get_region_height();
		
		if(col >= 0 && col < _cols && row >= 0 && row < _rows)
			 reg = _regions[row][col];
		else
			throw new IllegalArgumentException("Region doesn't exist");
		
		return reg;
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
			throw new IllegalArgumentException("Specified location is out of map.");
	}

	void register_animal(Animal a) {
		a.init(this);
		Region reg = get_region(a);
		reg.add_animal(a);
		this._animal_region.put(a, reg);
		
	}

	void unregister_animal(Animal a) {
		Region reg = get_region(a);
		reg.remove_animal(a);
		_animal_region.remove(a);
		
	}

	void update_animal_region(Animal a) {
		Region new_region = get_region(a);
		Region current_reg = _animal_region.get(a);
		
		if (current_reg != new_region) 
		{
            if (current_reg != null) 
            	current_reg.remove_animal(a);
            
            new_region.add_animal(a);
            _animal_region.put(a, new_region);
        }
		else 
			throw new IllegalArgumentException("Animal's new position is out of map bounds.");
	}

	public double get_food(Animal a, double dt) {
		double food = 0.0;
		
		Region reg = get_region(a);
		food = reg.get_food(a, dt);
	
		return food;
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
				.filter(filter)
				.collect(Collectors.toList());

		return animalsInRange;
	}
	
	public Iterator<MapInfo.RegionData> iterator() {
		return new Iterator<MapInfo.RegionData>()
		{
			private int currentRow = 0;
			private int currentCol = 0;
			
			public boolean hasNext(){
				return currentRow < _rows && currentCol < _cols;
			}
			
			public MapInfo.RegionData next()
			{
				if(!hasNext())
					throw new NoSuchElementException();
				
				RegionInfo regInfo = _regions[currentRow][currentCol];
				MapInfo.RegionData regData = new MapInfo.RegionData(currentRow, currentCol, regInfo);
				
				currentCol++;
	            if (currentCol == _cols) {
	                currentCol = 0;
	                currentRow++;
	            }
	            return regData;
			}
			
		};
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
