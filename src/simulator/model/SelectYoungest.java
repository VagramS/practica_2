package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy {

	public Animal select(Animal a, List<Animal> as) {
		Animal youngest = null;
		double min = Double.MAX_VALUE;

		if (as == null || as.isEmpty())
			return null;
		else
			for (Animal animal : as) {
				double currAge = animal.get_age();
				if (currAge < min) {
					min = currAge;
					youngest = animal;
				}
			}
		return youngest;
	}
}
