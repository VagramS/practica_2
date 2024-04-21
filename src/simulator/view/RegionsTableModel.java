package simulator.view;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import simulator.control.Controller;
import simulator.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	private Controller _ctrl;
	private List<MapInfo.RegionData> regionDataList;
	private List<String> columnNames;

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
		return regionDataList.size();
	}

	public int getColumnCount() {
		return columnNames.size();
	}

	public String getColumnName(int index) {
		return columnNames.get(index);
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= getRowCount()) {
			throw new IndexOutOfBoundsException("Row index out of bounds: " + rowIndex);
		}
		if (columnIndex < 0 || columnIndex >= getColumnCount()) {
			throw new IndexOutOfBoundsException("Column index out of bounds: " + columnIndex);
		}

		MapInfo.RegionData regionData = regionDataList.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return regionData.row();
		case 1:
			return regionData.col();
		case 2:
			return regionData.r().toString();
		default:
			return getAnimalCountByDiet(regionData.r(), Diet.values()[columnIndex - 3]);
		}
	}

	private int getAnimalCountByDiet(RegionInfo region, Diet diet) {
		return (int) region.getAnimalsInfo().stream().filter(a -> a.get_diet() == diet).count();
	}

	public void refreshData() {
		SwingUtilities.invokeLater(() -> {
			initializeRegionData();
			fireTableDataChanged();
		});
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		refreshData();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		refreshData();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		refreshData();
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		refreshData();
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		refreshData();
	}
}
