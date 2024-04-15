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
import java.util.ArrayList;

@SuppressWarnings("serial")
class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	 private Controller _ctrl;
	 private List<AnimalInfo> animals;
	 private HashMap<String, HashMap<State, Integer>> speciesStateCounts;

    private final String[] columnNames = {"Species", "NORMAL", "MATE", "HUNGER", "DANGER", "DEAD"};

    SpeciesTableModel(Controller ctrl) {
    	this._ctrl = ctrl;
        this.animals = new ArrayList<>();
        this.speciesStateCounts = new HashMap<>();
        ctrl.addObserver(this);
    }

    public int getRowCount() {
        return speciesStateCounts.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
    	if (rowIndex < 0 || rowIndex >= getRowCount()) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + rowIndex);
        }
        if (columnIndex < 0 || columnIndex >= getColumnCount()) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + columnIndex);
        }
        
        String species = new ArrayList<>(speciesStateCounts.keySet()).get(rowIndex);
        HashMap<State, Integer> counts = speciesStateCounts.get(species);
        
        if (columnIndex == 0) {
            return species;
        } else {
            State state = State.values()[columnIndex - 1];
            return counts.getOrDefault(state, 0);
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
            HashMap<State, Integer> counts = speciesStateCounts.get(animal.get_genetic_code());
            counts.put(state, counts.getOrDefault(state, 0) + 1);
        }
    }
}
