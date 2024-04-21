package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Region implements Entity, FoodSupplier, RegionInfo {
	protected List<Animal> _animals;

	public Region() {
		this._animals = new ArrayList<>();
	}

	final void add_animal(Animal a) {
		_animals.add(a);
	}

	final void remove_animal(Animal a) {
		_animals.remove(a);
	}

	final List<Animal> getAnimals() {
		return new ArrayList<Animal>(_animals);
	}

	public List<AnimalInfo> getAnimalsInfo() {
		return new ArrayList<>(_animals); // se puede usar Collections.unmodifiableList(_animals);
	}

	public JSONObject as_JSON() {
		JSONArray animalsArray = new JSONArray();

		for (int i = 0; i < _animals.size(); i++)
			animalsArray.put(_animals.get(i).as_JSON());

		JSONObject result = new JSONObject();
		result.put("animals", animalsArray);

		return result;

		// "animals": a1, a2...
	}
}
