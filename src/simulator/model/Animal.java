package simulator.model;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {
	final static double INIT_ENERGY = 100.0;
	final static double SPEED_PARAM = 0.1;
	final static double POS_PARAM = 60.0;
	final static double SIGHT_RANGE_SPEED_PARAM = 0.2;

	protected String _genetic_code;
	protected Diet _diet;
	protected State _state;
	protected Vector2D _pos;
	protected Vector2D _dest;
	protected Double _energy;
	protected Double _speed;
	protected Double _age;
	protected Double _desire;
	protected Double _sight_range;
	protected Animal _mate_target;
	protected Animal _baby;
	protected AnimalMapView _region_mngr;
	protected SelectionStrategy _mate_strategy;

	protected Animal(String genetic_code, Diet diet, double sight_range, double init_speed,
			SelectionStrategy mate_strategy, Vector2D pos) {
		if (genetic_code == null || genetic_code == "" || sight_range < 0 || init_speed < 0 || mate_strategy == null)
			throw new IllegalArgumentException("Argumentos inválidos para el constructor de Animal");

		this._genetic_code = genetic_code;
		this._diet = diet;
		this._sight_range = sight_range;
		this._speed = Utils.get_randomized_parameter(init_speed, SPEED_PARAM);
		this._mate_strategy = mate_strategy;
		this._pos = pos;

		this._state = State.NORMAL;
		this._energy = INIT_ENERGY;
		this._age = 0.0;
		this._desire = 0.0;
		this._dest = null;
		this._mate_target = null;
		this._baby = null;
		this._region_mngr = null;
	}

	protected Animal(Animal p1, Animal p2) {
		this._dest = null;
		this._age = 0.0;
		this._baby = null;
		this._mate_target = null;
		this._region_mngr = null;
		this._state = State.NORMAL;
		this._desire = 0.0;
		this._genetic_code = p1._genetic_code;
		this._diet = p1._diet;
		this._energy = (p1._energy + p2._energy) / 2;
		this._mate_strategy = p2._mate_strategy;
		this._pos = p1.get_position()
				.plus(Vector2D.get_random_vector(-1, 1).scale(POS_PARAM * (Utils._rand.nextGaussian() + 1)));
		this._sight_range = Utils.get_randomized_parameter((p1.get_sight_range() + p2.get_sight_range()) / 2,
				SIGHT_RANGE_SPEED_PARAM);
		this._speed = Utils.get_randomized_parameter((p1.get_speed() + p2.get_speed()) / 2, SIGHT_RANGE_SPEED_PARAM);
	}

	void init(AnimalMapView reg_mngr) {
		double x, y;
		this._region_mngr = reg_mngr;

		if (_pos == null) {
			x = Utils._rand.nextDouble(0, _region_mngr.get_width() - 1);
			y = Utils._rand.nextDouble(0, _region_mngr.get_height() - 1);
		} else {
			x = Utils.constrain_value_in_range(_pos.getX(), 0, _region_mngr.get_width() - 1);
			y = Utils.constrain_value_in_range(_pos.getY(), 0, _region_mngr.get_height() - 1);
		}
		_pos = new Vector2D(x, y);

		double destX = Utils._rand.nextDouble(0, _region_mngr.get_width() - 1);
		double destY = Utils._rand.nextDouble(0, _region_mngr.get_height() - 1);
		_dest = new Vector2D(destX, destY);
	}

	Animal deliver_baby() {
		Animal baby = this._baby;
		this._baby = null;

		return baby;
	}

	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
	}

	public JSONObject as_JSON() {
		JSONObject obj = new JSONObject();
		JSONArray posArray = new JSONArray(Arrays.asList(_pos.getX(), _pos.getY()));

		obj.put("pos", posArray.toString());
		obj.put("gcode", _genetic_code);
		obj.put("diet", _diet);
		obj.put("state", _state);

		return obj;

		// "pos": [28.90696391797469,22.009772194487613],
		// "gcode": "Sheep",
		// "diet": "HERBIVORE",
		// "state": "NORMAL"
	}
}
