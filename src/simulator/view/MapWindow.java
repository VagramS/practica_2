package simulator.view;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.*;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class MapWindow extends JFrame implements EcoSysObserver {
	private Controller _ctrl;
	private MapViewer _viewer;
	private Frame _parent;
	
	MapWindow(Frame parent, Controller ctrl) {
		super("[MAP VIEWER]");
		_ctrl = ctrl;
		_parent = parent;
		intiGUI();
		ctrl.addObserver(this);
	}
	private void intiGUI() 
	{
		JPanel mainPanel = new JPanel(new BorderLayout());
        this.setContentPane(mainPanel);

        _viewer = new MapViewer();
        mainPanel.add(_viewer, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                _ctrl.removeObserver(MapWindow.this);
                dispose();
            }
        });

        if (_parent != null) 
            setLocationRelativeTo(_parent);

        setResizable(false);
        pack();
        setVisible(true);	
	}
	
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
            _viewer.reset(time, map, animals);
            pack();
        });
	}
	
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> {
            _viewer.reset(time, map, animals);
            pack();
        });
	}
	
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {}

	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {}

	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater(() -> {
            _viewer.update(animals, time);
        });
	}
}
