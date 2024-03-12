package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {

	public Animal select(Animal a, List<Animal> as) {
		double minDistance = Double.MAX_VALUE;
		Animal closest = null;

		for (Animal animal : as) {
			double currentDistance = a.get_position().distanceTo(animal.get_position());
			if (currentDistance < minDistance) {
				minDistance = currentDistance;
				closest = animal;
			}
		}
		return closest;
	}

}
