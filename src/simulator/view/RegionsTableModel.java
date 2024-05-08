package simulator.view;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import simulator.control.Controller;
import simulator.model.*;
import simulator.model.MapInfo.RegionData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	private Controller _ctrl;
	private List<MapInfo.RegionData> regionDataList;
	private List<String> columnNames;
	private List<Info> regionInfoList = new ArrayList<>();
	
	static class Info {
		int row;
		int col;
		String desc;
		int[] dietCounts;
		
		Info(RegionData r){
			this.row = r.row();
			this.col = r.col();
			this.desc = r.r().toString();
			this.dietCounts = new int[Diet.values().length];
			
			for(AnimalInfo a: r.r().getAnimalsInfo())
			{
				dietCounts[a.get_diet().ordinal()]++;
			}
		}
	}
	
	RegionsTableModel(Controller ctrl) {
		this._ctrl = ctrl;
		this.regionDataList = new ArrayList<>();
		this.columnNames = new ArrayList<>();
		this.columnNames.add("Row");
		this.columnNames.add("Col");
		this.columnNames.add("Desc.");

		for (Diet diet : Diet.values()) {
			this.columnNames.add(diet.toString());
		}

		ctrl.addObserver(this);
		initializeRegionData();
	}

	private void initializeRegionData() {
		regionDataList.clear();
		Iterator<MapInfo.RegionData> regionIterator = _ctrl.getSimulator().get_map_info().iterator();
		while (regionIterator.hasNext()) {
			regionDataList.add(regionIterator.next());
		}
	}

	public int getRowCount() {
		return regionInfoList.size(); 
	}

	public int getColumnCount() {
		return columnNames.size();
	}

	public String getColumnName(int index) {
		return columnNames.get(index);
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
	    if (rowIndex < 0 || rowIndex >= regionInfoList.size()) 
	        throw new IndexOutOfBoundsException("Row index out of bounds or list is empty: " + rowIndex);
	    
	    if (columnIndex < 0 || columnIndex >= getColumnCount()) 
	        throw new IndexOutOfBoundsException("Column index out of bounds: " + columnIndex);
	    
	    if (regionInfoList.isEmpty())
	        return null;
	    
	    Info info = regionInfoList.get(rowIndex);
	    switch (columnIndex) {
	    case 0:
	        return info.row;
	    case 1:
	        return info.col;
	    case 2:
	        return info.desc;
	    default:
	        return info.dietCounts[columnIndex - 3];
	    }
	}

	public void refreshData() {
		SwingUtilities.invokeLater(() -> {
			regionInfoList.clear();
            Iterator<MapInfo.RegionData> regionIterator = _ctrl.getSimulator().get_map_info().iterator();
            while (regionIterator.hasNext()) {
                regionInfoList.add(new Info(regionIterator.next()));
            }
			fireTableDataChanged();
		});
	}

	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		refreshData();
	}

	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		refreshData();
	}

	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		refreshData();
	}

	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		refreshData();
	}

	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		refreshData();
	}
}
