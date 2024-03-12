package simulator.model;

public class DefaultRegion extends Region {

	final static double FOOD_PARAM1 = 60.0;
	final static double FOOD_PARAM2 = 5.0;
	final static double FOOD_PARAM3 = 2.0;

	public DefaultRegion() {
		super();
	}

	public double get_food(Animal a, double dt) {
		double _food = 0.0;
		int n = 0;

		for (Animal animal : _animals) {
			if (animal._diet == Diet.HERBIVORE)
				n++;
		}

		if (a._diet == Diet.HERBIVORE)
			_food = FOOD_PARAM1 * Math.exp(-Math.max(0, n - FOOD_PARAM2) * FOOD_PARAM3) * dt; // n es el número de
																								// animales herbívoros
																								// en la región
		else
			_food = 0.0;

		return _food;
	}

	public void update(double dt) {
	}
	
	public String toString()
	{
		return "Default region";
	}
}
