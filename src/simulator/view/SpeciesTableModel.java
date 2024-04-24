package simulator.view;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@SuppressWarnings("serial")
class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	@SuppressWarnings("unused")
	private Controller _ctrl;
	private List<AnimalInfo> animals;
	private Map<String, Map<State, Integer>> speciesStateCounts;
	private List<String> columnNames;

	SpeciesTableModel(Controller ctrl) {
		this._ctrl = ctrl;
		this.animals = new ArrayList<>();
		this.speciesStateCounts = new HashMap<>();
		this.columnNames = new ArrayList<>();
		this.columnNames.add("Species");

		for (State state : State.values()) {
			this.columnNames.add(state.toString());
		}

		ctrl.addObserver(this);
	}

	public int getRowCount() {
		return speciesStateCounts.size();
	}

	public int getColumnCount() {
		return columnNames.size();
	}

	public String getColumnName(int columnIndex) {
		return columnNames.get(columnIndex);
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= getRowCount()) {
			throw new IndexOutOfBoundsException("Row index out of bounds: " + rowIndex);
		}
		if (columnIndex < 0 || columnIndex >= getColumnCount()) {
			throw new IndexOutOfBoundsException("Column index out of bounds: " + columnIndex);
		}

		String species = new ArrayList<>(speciesStateCounts.keySet()).get(rowIndex);
		if (columnIndex == 0) {
			return species;
		}
		Map<State, Integer> counts = speciesStateCounts.get(species);
		State state = State.values()[columnIndex - 1];
		return counts.getOrDefault(state, 0);
	}

	private void updateData(List<AnimalInfo> newAnimals) {
		SwingUtilities.invokeLater(() -> {
			this.animals = new ArrayList<>(newAnimals);
			countStates();
			fireTableDataChanged();
		});
	}

	private void countStates() {
		speciesStateCounts.clear();
		for (AnimalInfo animal : animals) {
			speciesStateCounts.putIfAbsent(animal.get_genetic_code(), new HashMap<>());
			State state = animal.get_state();
			speciesStateCounts.get(animal.get_genetic_code()).merge(state, 1, Integer::sum);
		}
	}

	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		updateData(animals);
	}

	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		updateData(animals);
	}

	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		updateData(animals);
	}

	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		updateData(animals);
	}

	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		updateData(animals);
	}
}
