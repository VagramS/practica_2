package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal> {

	private Factory<SelectionStrategy> selectionStrategyFactory;

	public WolfBuilder(Factory<SelectionStrategy> selection_strategy_factory) {
		super("wolf", "Un tipo de animal carnivore");
		this.selectionStrategyFactory = selection_strategy_factory;
	}

	protected Animal create_instance(JSONObject data) {
		SelectionStrategy mate_strategy = new SelectFirst();
		SelectionStrategy hunt_strategy = new SelectFirst();
		Vector2D pos = null;

		if (data.has("mate_strategy")) {
			JSONObject mateStrategyData = data.getJSONObject("mate_strategy");
			mate_strategy = selectionStrategyFactory.create_instance(mateStrategyData);
		}

		if (data.has("hunt_strategy")) {
			JSONObject dangerStrategyData = data.getJSONObject("hunt_strategy");
			hunt_strategy = selectionStrategyFactory.create_instance(dangerStrategyData);
		}

		if (data.has("pos")) {
			JSONObject posObj = data.getJSONObject("pos");
			JSONArray xRange = posObj.getJSONArray("x_range");
			JSONArray yRange = posObj.getJSONArray("y_range");
			double x = Utils._rand.nextDouble(xRange.getDouble(0), xRange.getDouble(1));
			double y = Utils._rand.nextDouble(yRange.getDouble(0), yRange.getDouble(1));
			pos = new Vector2D(x, y);
		}
		return new Wolf(mate_strategy, hunt_strategy, pos);
	}

}
