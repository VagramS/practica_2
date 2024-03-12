package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {

	public SelectFirstBuilder() {
		super("first", "Elige el primer animal de la region");
	}

	protected SelectionStrategy create_instance(JSONObject data) {
		return new SelectFirst();
	}

}
