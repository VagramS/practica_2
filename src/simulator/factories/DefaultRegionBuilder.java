package simulator.factories;

import org.json.JSONObject;
import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region> {

	public DefaultRegionBuilder() {
		super("default", "Infinite food supply");
	}

	protected Region create_instance(JSONObject data) {
		return new DefaultRegion();
	}

	public JSONObject get_info() {
		JSONObject obj = new JSONObject();

		obj.put("type", "default");
		obj.put("desc", "Infinite food supply");
		obj.put("data", new JSONObject());
		return obj;
	}

}
