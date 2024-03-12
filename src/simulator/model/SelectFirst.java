package simulator.model;

import java.util.List;

public class SelectFirst implements SelectionStrategy {

	public Animal select(Animal a, List<Animal> as) {
		if (as == null || as.isEmpty())
			return null;
		else
			return as.get(0);
	}
}
