package simulator.model;

import java.util.Random;

public class DynamicSupplyRegion extends Region {

	final static double FOOD_PARAM1 = 60.0;
	final static double FOOD_PARAM2 = 5.0;
	final static double FOOD_PARAM3 = 2.0;

	protected double _food;
	protected double _factor;
	protected Random rand = new Random();

	public DynamicSupplyRegion(double food, double factor) {
		super();
		this._food = food;
		this._factor = factor;
	}

	public void update(double dt) {
		if (rand.nextDouble() < 0.5)
			_food += dt * _factor;
	}

	public double get_food(Animal a, double dt) {
		int n = 0;

		for (Animal animal : _animals)
			if (animal._diet == Diet.HERBIVORE)
				n++;

		double food = 0.0;

		if (a.get_diet() == Diet.HERBIVORE) {
			food = Math.min(_food, FOOD_PARAM1 * Math.exp(-Math.max(0, n - FOOD_PARAM2) * FOOD_PARAM3) * dt);
			_food -= food;
		}

		return food;
	}
	
	public String toString()
	{
		return "dynamic";
	}

}
