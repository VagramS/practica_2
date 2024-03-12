package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Dynamic food supply");
	}

	protected Region create_instance(JSONObject data) {
		double factor = 2.0;
		double food = 1000.0;

		if (data.has("factor"))
			factor = data.getDouble("factor");

		if (data.has("food"))
			food = data.getDouble("food");

		return new DynamicSupplyRegion(food, factor);
	}
	
	public JSONObject get_info()
	{
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		
		obj.put("type", "dynamic");
		obj.put("desc", "Dynamic food supply");
		
		data.put("factor", "food increase factor (optional, default 2.0)");
		data.put("food", "initial amount of food (optional, default 100.0)");
		obj.put("data", data);
		
		return obj;
	}
}
