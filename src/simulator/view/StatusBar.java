package simulator.view;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class StatusBar extends JPanel implements EcoSysObserver {

	private static final long serialVersionUID = 1L;
	private JLabel _time;
	private JLabel _totalAnimals;
	private JLabel _dimension;

	StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));

		Component spacer = Box.createRigidArea(new Dimension(25, 0));
		Component spacer2 = Box.createRigidArea(new Dimension(40, 0));

		_time = new JLabel("Time: 0.0  ");
		_time.setHorizontalAlignment(SwingConstants.LEFT); // label left alignmnet
		this.add(_time);
		this.add(spacer);

		JSeparator sep1 = new JSeparator(JSeparator.VERTICAL);
		sep1.setPreferredSize(new Dimension(5, 20));
		this.add(sep1);

		_totalAnimals = new JLabel("Total Animals: 0  ");
		_totalAnimals.setHorizontalAlignment(SwingConstants.LEFT);
		this.add(_totalAnimals);
		this.add(spacer2);

		JSeparator sep2 = new JSeparator(JSeparator.VERTICAL);
		sep2.setPreferredSize(new Dimension(5, 20));
		this.add(sep2);

		_dimension = new JLabel("Dimension: ");
		_dimension.setHorizontalAlignment(SwingConstants.LEFT);
		this.add(_dimension);
	}

	public void Update(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
			_time.setText(String.format("Time: %.2f", time));
			_totalAnimals.setText("Total Animals: " + animals.size());
			_dimension.setText(String.format("Dimension: %dx%d %dx%d", map.get_width(), map.get_height(),
					map.get_cols(), map.get_rows()));
		});
	}

	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		Update(time, map, animals);
	}

	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		Update(time, map, animals);
	}

	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		Update(time, map, animals);
	}

	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		Update(time, map, animals);
	}
}
