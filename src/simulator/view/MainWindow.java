package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import simulator.control.Controller;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private Controller _ctrl;

	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		_ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(800, 600)); // Main window size
		setContentPane(mainPanel);

		// ControlPanel
		ControlPanel control = new ControlPanel(_ctrl);
		mainPanel.add(control, BorderLayout.PAGE_START);

		// StatusBar
		StatusBar status = new StatusBar(_ctrl);
		mainPanel.add(status, BorderLayout.PAGE_END);

		// ContentPanel
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);

		// Spieces Table
		JPanel speciesPanel = new JPanel(new BorderLayout(5, 5));
		speciesPanel.setPreferredSize(new Dimension(500, 200));
		JTable speciesTable = new JTable(new SpeciesTableModel(_ctrl));
		JScrollPane speciesScrollPane = new JScrollPane(speciesTable);
		speciesPanel.add(speciesScrollPane, BorderLayout.CENTER);
		contentPanel.add(speciesPanel);

		TitledBorder speciesBorder = new TitledBorder(new LineBorder(Color.black, 2), "Species", TitledBorder.LEFT,
				TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.black);
		speciesPanel.setBorder(speciesBorder);


		// Regions Table
		JPanel regionsPanel = new JPanel(new BorderLayout(5, 5));
		regionsPanel.setPreferredSize(new Dimension(500, 200));
		JTable regionsTable = new JTable(new RegionsTableModel(_ctrl));
		JScrollPane regionsScrollPane = new JScrollPane(regionsTable);
		regionsPanel.add(regionsScrollPane, BorderLayout.CENTER);
		contentPanel.add(regionsPanel);

		TitledBorder regionsBorder = new TitledBorder(new LineBorder(Color.black, 2), "Regions", TitledBorder.LEFT,
				TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.black);
		regionsPanel.setBorder(regionsBorder);

		new InfoTable("Species", new SpeciesTableModel(_ctrl));
		new InfoTable("Regions", new RegionsTableModel(_ctrl));

		// llama a ViewUtils.quit(MainWindow.this) en el método windowClosing
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				ViewUtils.quit(MainWindow.this);
			}
		});

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setLocationRelativeTo(null); // Always opens the main window at the center of display
		setVisible(true);
	}
}
