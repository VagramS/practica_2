package simulator.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.*;

import java.util.List;

@SuppressWarnings("serial")
class ChangeRegionsDialog extends JDialog implements EcoSysObserver {

	private Controller _ctrl;
	private JTable _dataTable;
	private JComboBox<String> _regionsComboBox;
	private JComboBox<Integer> _fromRowComboBox, _toRowComboBox, _fromColComboBox, _toColComboBox;
	private JButton _okButton, _cancelButton;

	ChangeRegionsDialog(Controller ctrl) {
		super((Frame) null, true);
		this._ctrl = ctrl;
		this._ctrl.addObserver(this);
		initGUI();
	}

	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel helpPanel = new JPanel(new BorderLayout());
		JLabel helpText = new JLabel(
				"<html>Select a region type, the rows/cols interval, and provide values for the parameters in the Value Column (default values are used for parameters without a specified value). </html>");
		helpText.setPreferredSize(new Dimension(750, 60));
		helpPanel.add(helpText, BorderLayout.CENTER);
		mainPanel.add(helpPanel);

		_dataTable = new JTable();
		DefaultTableModel model = new DefaultTableModel(new Object[][] {},
				new String[] { "Key", "Value", "Description" }) {
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};

		_dataTable.setModel(model);
		mainPanel.add(new JScrollPane(_dataTable));

		_dataTable.getColumnModel().getColumn(0).setPreferredWidth(80);
		_dataTable.getColumnModel().getColumn(1).setPreferredWidth(60);
		_dataTable.getColumnModel().getColumn(2).setPreferredWidth(300);

		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new FlowLayout());

		JLabel region_text = new JLabel("Region type: ");
		_regionsComboBox = new JComboBox<>();

		for (int i = 0; i < Main._regions_factory.get_info().size(); i++)
			_regionsComboBox.addItem(Main._regions_factory.get_info().get(i).getString("type"));

		_regionsComboBox.addActionListener(
				e -> updateTableBasedOnRegionType((String) _regionsComboBox.getSelectedItem(), model));
		_regionsComboBox.setPreferredSize(new Dimension(100, 25));
		comboPanel.add(region_text);
		comboPanel.add(_regionsComboBox);

		Component spacer = Box.createRigidArea(new Dimension(15, 0));
		comboPanel.add(spacer);

		JLabel row_text = new JLabel("Row from/to: ");
		_fromRowComboBox = new JComboBox<>();
		_fromRowComboBox.setPreferredSize(new Dimension(45, 25));
		comboPanel.add(row_text);
		comboPanel.add(_fromRowComboBox);

		_toRowComboBox = new JComboBox<>();
		_toRowComboBox.setPreferredSize(new Dimension(45, 25));
		comboPanel.add(_toRowComboBox);

		Component spacer2 = Box.createRigidArea(new Dimension(15, 0));
		comboPanel.add(spacer2);

		JLabel col_text = new JLabel("Column from/to: ");
		_fromColComboBox = new JComboBox<>();
		_fromColComboBox.setPreferredSize(new Dimension(45, 25));
		comboPanel.add(col_text);
		comboPanel.add(_fromColComboBox);
		_toColComboBox = new JComboBox<>();
		_toColComboBox.setPreferredSize(new Dimension(45, 25));
		comboPanel.add(_toColComboBox);

		for (int i = 0; i < _ctrl.getSimulator().get_map_info().get_rows(); i++) {
			_fromRowComboBox.addItem(i);
			_toRowComboBox.addItem(i);
		}

		for (int i = 0; i < _ctrl.getSimulator().get_map_info().get_cols(); i++) {
			_fromColComboBox.addItem(i);
			_toColComboBox.addItem(i);
		}

		mainPanel.add(comboPanel);

		Component spacer3 = Box.createRigidArea(new Dimension(0, 15));
		mainPanel.add(spacer3);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		_okButton = new JButton("OK");
		_okButton.addActionListener(e -> applyChanges());
		_cancelButton = new JButton("Cancel");
		_cancelButton.addActionListener(e -> setVisible(false));
		buttonPanel.add(_okButton);
		buttonPanel.add(_cancelButton);

		mainPanel.add(buttonPanel);

		setContentPane(mainPanel);
		setPreferredSize(new Dimension(750, 440));
		pack();
		setLocationRelativeTo(null); // Center on screen
		setResizable(false);
		setVisible(false);
	}

	private void applyChanges() {
		int fromRow = (Integer) _fromRowComboBox.getSelectedItem();
		int toRow = (Integer) _toRowComboBox.getSelectedItem();
		int fromCol = (Integer) _fromColComboBox.getSelectedItem();
		int toCol = (Integer) _toColComboBox.getSelectedItem();

		for (int row = fromRow; row <= toRow; row++)
			for (int col = fromCol; col <= toCol; col++)
				applyRegionSettings(row, col);

		setVisible(false);
	}

	private void applyRegionSettings(int row, int col) {
		JSONObject regionData = new JSONObject();
		for (int i = 0; i < _dataTable.getRowCount(); i++) {
			String key = _dataTable.getValueAt(i, 0).toString();
			String value = _dataTable.getValueAt(i, 1).toString();
			if (!value.isEmpty()) {
				regionData.put(key, value);
			}
		}

		String regionType = _regionsComboBox.getSelectedItem().toString();

		JSONObject region = new JSONObject();
		region.put("row", new JSONArray(new int[] { row, row }));
		region.put("col", new JSONArray(new int[] { col, col }));
		JSONObject spec = new JSONObject();
		spec.put("type", regionType);
		spec.put("data", regionData);
		region.put("spec", spec);

		// Wrap the single region object in an array
		JSONArray regions = new JSONArray();
		regions.put(region);

		try {
			_ctrl.set_regions(regions);
		} catch (Exception e) {
			ViewUtils.showErrorMsg(e.getMessage());
		}
	}

	private void updateTableBasedOnRegionType(String regionType, DefaultTableModel model) {
		model.setRowCount(0); // Clear existing data
		JSONObject info = null;

		for (int i = 0; i < Main._regions_factory.get_info().size(); i++)
			info = Main._regions_factory.get_info().get(i);

		if (info != null) {
			JSONObject data = info.getJSONObject("data");
			for (String key : data.keySet()) {
				String description = data.getString(key);
				model.addRow(new Object[] { key, "", description });
			}
		}
	}

	public void open(Frame parent) {
		setLocation(parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

	private void updateComboBoxes(MapInfo map) {
		if (_fromRowComboBox != null || _toRowComboBox != null || _fromColComboBox != null || _toColComboBox != null) {
			_fromRowComboBox.removeAllItems();
			_toRowComboBox.removeAllItems();
			_fromColComboBox.removeAllItems();
			_toColComboBox.removeAllItems();

			for (int i = 0; i < map.get_rows(); i++) {
				_fromRowComboBox.addItem(i);
				_toRowComboBox.addItem(i);
			}

			for (int i = 0; i < map.get_cols(); i++) {
				_fromColComboBox.addItem(i);
				_toColComboBox.addItem(i);
			}
		}
	}

	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		updateComboBoxes(map);
	}

	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		updateComboBoxes(map);
	}

	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
	}

	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
	}
}