package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator implements JSONable, Observable<EcoSysObserver> {
	protected Factory<Animal> _animals_factory;
	protected Factory<Region> _regions_factory;
	protected RegionManager _region_mngr;
	protected List<Animal> animals;
	protected List<EcoSysObserver> observers;
	protected double time;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory,
			Factory<Region> regions_factory) {
		this._animals_factory = animals_factory;
		this._regions_factory = regions_factory;
		this._region_mngr = new RegionManager(cols, rows, width, height);
		this.animals = new ArrayList<>();
		this.observers = new ArrayList<>();
		time = 0.0;
	}

	private void set_region(int row, int col, Region r) {
		this._region_mngr._regions[row][col] = r;
	}

	public void set_region(int row, int col, JSONObject r_json) {
		Region R = _regions_factory.create_instance(r_json);
		set_region(row, col, R);

		for (int i = 0; i < observers.size(); i++) {
			observers.get(i).onRegionSet(row, col, this.get_map_info(), R);
		}
	}

	private void add_animal(Animal a) {
		this.animals.add(a);
		_region_mngr.register_animal(a);
	}

	public void add_animal(JSONObject a_json) {
		Animal A = _animals_factory.create_instance(a_json);
		add_animal(A);
		AnimalInfo animalInfo = A;
		for (EcoSysObserver o : observers) {
			o.onAnimalAdded(this.time, this.get_map_info(), this.get_animals(), animalInfo);
		}
	}

	public MapInfo get_map_info() {
		return _region_mngr;
	}

	public List<AnimalInfo> get_animals() {
		return Collections.unmodifiableList(this.animals);
	}

	public double get_time() {
		return this.time;
	}

	public void advance(double dt) {
		this.time += dt;

		List<Animal> deadAnimals = new ArrayList<>();
		for (Animal animal : this.animals) {
			animal.update(dt);
			if (animal.get_state() == State.DEAD) {
				deadAnimals.add(animal);
			}
		}
		for (Animal deadAnimal : deadAnimals) {
			this.animals.remove(deadAnimal);
			this._region_mngr.unregister_animal(deadAnimal);
		}

		_region_mngr.update_all_regions(dt);

		List<Animal> newBabies = new ArrayList<>();
		for (Animal animal : animals) {
			if (animal.is_pregnant()) {
				Animal baby = animal.deliver_baby();
				if (baby != null)
					newBabies.add(baby);
			}
		}
		for (Animal baby : newBabies)
			add_animal(baby);

		for (int i = 0; i < observers.size(); i++)
			observers.get(i).onAvanced(this.time, this.get_map_info(), this.get_animals(), dt);

	}

	public void reset(int cols, int rows, int width, int height) {
		animals = new ArrayList<>();
		_region_mngr = new RegionManager(cols, rows, width, height);
		time = 0.0;

		for (int i = 0; i < observers.size(); i++)
			observers.get(i).onReset(this.time, this.get_map_info(), this.get_animals());
	}

	public JSONObject as_JSON() {
		JSONObject object = new JSONObject();

		object.put("time", this.time);
		object.put("state", _region_mngr.as_JSON());

		return object;

//		"time": t,
//		"state": s,
	}

	public void addObserver(EcoSysObserver o) {
		if (!observers.contains(o)) {
			observers.add(o);
			o.onRegister(this.time, this.get_map_info(), this.get_animals());
		}
	}

	public void removeObserver(EcoSysObserver o) {
		if (observers.contains(o))
			observers.remove(o);
	}

}
