package simulator.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import simulator.control.Controller;
import simulator.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
class ChangeRegionsDialog extends JDialog implements EcoSysObserver {

	private Controller _ctrl;
	private JTable _dataTable;
	private JComboBox<String> _regionsComboBox;
	private JComboBox<Integer> _fromRowComboBox, _toRowComboBox, _fromColComboBox, _toColComboBox;
	private JButton _okButton, _cancelButton;

	ChangeRegionsDialog(Controller ctrl) {
		super((Frame) null, "Change Regions", true);
		this._ctrl = ctrl;
		this._ctrl.addObserver(this);
		initGUI();
	}

	private void initGUI() {
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
				return column == 1; // Only the "Value" column is editable
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
		//////////////////////////////////////
		_regionsComboBox.addItem("default");
		_regionsComboBox.addItem("dynamic");
		/////////////////////////////////////
		_regionsComboBox.addActionListener(
				e -> updateTableBasedOnRegionType((String) _regionsComboBox.getSelectedItem(), model));
		_regionsComboBox.setPreferredSize(new Dimension(90, 25));
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
//        int fromRow = (Integer) _fromRowComboBox.getSelectedItem();
//        int toRow = (Integer) _toRowComboBox.getSelectedItem();
//        int fromCol = (Integer) _fromColComboBox.getSelectedItem();
//        int toCol = (Integer) _toColComboBox.getSelectedItem();
//        
//        for (int row = fromRow; row <= toRow; row++) {
//            for (int col = fromCol; col <= toCol; col++) {
//                applyRegionSettings(row, col);
//            }
//        }
		setVisible(false);
	}

	public void open(Frame parent) {
		setLocation(parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

	private void updateTableBasedOnRegionType(String regionType, DefaultTableModel model) {
		model.setRowCount(0); // Clear existing data
		if ("dynamic".equals(regionType)) {
			model.addRow(new Object[] { "factor", "", "food increase factor (optional, default 2.0)" });
			model.addRow(new Object[] { "food", "", "Initial amount of food (optional, default 100.0)" });
		}
	}

	private void updateComboBoxes(MapInfo map) {
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

	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// updateComboBoxes(map);
	}

	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		updateComboBoxes(map);
	}

	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {}

	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {}

	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {}
}
