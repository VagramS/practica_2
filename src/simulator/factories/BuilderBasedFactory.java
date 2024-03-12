package simulator.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {
	private Map<String, Builder<T>> _builders;
	private List<JSONObject> _builders_info;

	public BuilderBasedFactory() {
		// Create a HashMap for _builders, and a LinkedList _builders_info

		_builders = new HashMap<>();
		_builders_info = new LinkedList<JSONObject>();
	}

	public BuilderBasedFactory(List<Builder<T>> builders) {
		this();
		// call add_builder(b) for each builder b in builder
		builders.forEach(this::add_builder);
	}

	public void add_builder(Builder<T> b) {
		// add an entry “b.getTag() |−> b” to _builders.
		// add b.get_info() to _builders_info

		_builders.put(b.get_type_tag(), b);
		_builders_info.add(b.get_info());
	}

	public T create_instance(JSONObject info) {
		if (info == null) {
			throw new IllegalArgumentException("’info’ cannot be null");
		}

		String type = info.getString("type");
		Builder<T> builder = _builders.get(type);

		if (builder != null) {
			JSONObject data = info.has("data") ? info.getJSONObject("data") : new JSONObject();
			T inst = builder.create_instance(data);
			if (inst != null)
				return inst;
		}
		// Look for a builder with a tag equals to info.getString("type"), in the
		// map _builder, and call its create_instance method and return the result
		// if it is not null. The value you pass to create_instance is the following
		// because ‘data’ is optional:
		//
		// info.has("data") ? info.getJSONObject("data") : new getJSONObject()
		// …
		// If no builder is found or the result is null ...
		throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());
	}

	@Override
	public List<JSONObject> get_info() {
		return Collections.unmodifiableList(_builders_info);
	}

}
