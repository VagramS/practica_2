package simulator.view;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
    private Controller _ctrl;
    private List<RegionInfo> regions;
    private List<String> columnNames;

    RegionsTableModel(Controller ctrl) {
        this._ctrl = ctrl;
        this.regions = new ArrayList<>();
        this.columnNames = new ArrayList<>();
        this.columnNames.add("Row");
        this.columnNames.add("Col");
        this.columnNames.add("Desc.");
        
        for (Diet diet : Diet.values()) 
            this.columnNames.add(diet.toString());
        
        ctrl.addObserver(this);
    }

    public int getRowCount() {
        return regions.size();
    }

    public int getColumnCount() {
        return columnNames.size();
    }

    public String getColumnName(int index) {
        return columnNames.get(index);
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
		return columnIndex;
//        if (rowIndex < 0 || rowIndex >= regions.size()) {
//            throw new IndexOutOfBoundsException("Index " + rowIndex + " is out of bounds.");
//        }
//
//        RegionInfo region = regions.get(rowIndex);
//        switch (columnIndex) {
//            case 0:
//                return region.getRow();
//            case 1:
//                return region.getCol();
//            case 2:
//                return region.toString();
//            default:
//                return getAnimalCountByDiet(region, Diet.values()[columnIndex - 3]);
//        }
    }

    private int getAnimalCountByDiet(RegionInfo region, Diet diet) {
        int count = 0;
        for (AnimalInfo animal : region.getAnimalsInfo()) {
            if (animal.get_diet() == diet) 
                count++;
        }
        return count;
    }

    public void refreshData(List<RegionInfo> newRegions) {
    	 SwingUtilities.invokeLater(() -> {
             regions = new ArrayList<>(newRegions); // Use the new list, not the model's list
             fireTableDataChanged(); // Notify the table that data has changed
         });
    }

	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		//refreshData(map.getRegions());
	}

	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		//refreshData(map.getRegions());
	}

	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		//refreshData(map.getRegions());
	}

	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		//refreshData(map.getRegions());
	}

	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		//refreshData(map.getRegions());
	}
}
