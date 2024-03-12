package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy> {

	public SelectYoungestBuilder() {
		super("youngest", "Elige el animal mas joven");
	}

	protected SelectionStrategy create_instance(JSONObject data) {
		return new SelectYoungest();
	}

}
