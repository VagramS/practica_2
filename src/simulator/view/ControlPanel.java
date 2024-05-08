package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;

import javax.swing.*;

import simulator.control.Controller;

@SuppressWarnings("serial")
class ControlPanel extends JPanel {

	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;

	private JToolBar _toolaBar;
	private JFileChooser _fc;

	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _quitButton;
	private JButton _openButton;
	private JButton _regionsButton;
	private JButton _runButton;
	private JButton _stopButton;
	private JButton _viewerButton;

	private static final int STEPS_DEFAULT_VALUE = 10000;
	private static final double DELTA_TIME_DEFAULT_VALUE = 0.03;

	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		_toolaBar = new JToolBar();
		add(_toolaBar, BorderLayout.PAGE_START);

		// File chooser
		_fc = new JFileChooser();
		_fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

		// Open Button
		_toolaBar.addSeparator();
		_openButton = new JButton();
		_openButton.setToolTipText("Load an input file into the simulator");
		_openButton.setIcon(new ImageIcon("resources/icons/open.png"));
		_openButton.addActionListener((e) -> {
			int returnVal = _fc.showOpenDialog(ViewUtils.getWindow(this));
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = _fc.getSelectedFile();
				_ctrl.reset(_ctrl.getSimulator().get_map_info().get_cols(), 
						_ctrl.getSimulator().get_map_info().get_rows(), 
						_ctrl.getSimulator().get_map_info().get_width(), 
						_ctrl.getSimulator().get_map_info().get_height());
				_ctrl.loadFile(file);
			}
		});
		_toolaBar.add(_openButton);
		_toolaBar.addSeparator();

		// Viewer Button
		_viewerButton = new JButton();
		_viewerButton.setToolTipText("Map Viewer");
		_viewerButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		_viewerButton.addActionListener(e -> {
			new MapWindow(ViewUtils.getWindow(this), _ctrl);
		});
		_toolaBar.add(_viewerButton);

		// Regions Button
		_regionsButton = new JButton();
		_regionsButton.setToolTipText("Change Regions");
		_regionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
		_regionsButton.addActionListener(e -> {
			_changeRegionsDialog.setVisible(true);
		});
		_toolaBar.add(_regionsButton);
		_toolaBar.addSeparator();

		// Run Button
		_runButton = new JButton();
		_runButton.setToolTipText("Run the simulation");
		_runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		_toolaBar.add(_runButton);

		// Stop Button
		_stopButton = new JButton();
		_stopButton.setToolTipText("Stop the simulation");
		_stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		_stopButton.addActionListener(e -> {
			_stopped = true;
		});
		_toolaBar.add(_stopButton);

		// Steps label
		JLabel steps = new JLabel("Steps:  ");
		_toolaBar.addSeparator(new Dimension(25, 0));
		steps.setFont(new Font("Arial", Font.BOLD, 13));
		_toolaBar.add(steps);

		// Steps Spinner
		JSpinner stepsInput = new JSpinner(new SpinnerNumberModel(STEPS_DEFAULT_VALUE, 100, 300000, 100));
		stepsInput.setToolTipText("Simulation steps to run: 1 - 10000");

		Dimension fixedSize_steps = new Dimension(90, 35);
		stepsInput.setPreferredSize(fixedSize_steps);
		stepsInput.setMinimumSize(fixedSize_steps);
		stepsInput.setMaximumSize(fixedSize_steps);

		JComponent editor = stepsInput.getEditor();
		JFormattedTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
		textField.setFont(new Font("Arial", Font.BOLD, 14));
		_toolaBar.add(stepsInput);

		// Delta-time label
		JLabel delta_time = new JLabel("Delta-time:  ");
		_toolaBar.addSeparator(new Dimension(10, 0));
		delta_time.setFont(new Font("Arial", Font.BOLD, 13));
		_toolaBar.add(delta_time);

		// Delta-time label
		JTextField delta_timeInput = new JTextField(String.valueOf(DELTA_TIME_DEFAULT_VALUE));
		delta_timeInput.setToolTipText("Real time (seconds) corresponding to a step");
		delta_timeInput.setFont(new Font("Arial", Font.BOLD, 13));

		// Fixed size of the field
		Dimension fixedSize_delta = new Dimension(70, 35);
		delta_timeInput.setPreferredSize(fixedSize_delta);
		delta_timeInput.setMinimumSize(fixedSize_delta);
		delta_timeInput.setMaximumSize(fixedSize_delta);

		_toolaBar.add(delta_timeInput);
		_toolaBar.addSeparator(new Dimension(10, 0));

		// Run button event
		_runButton.addActionListener(e -> {
			_stopped = false;
			Deactivate();
			int steps_value = (Integer) stepsInput.getValue();
			double deltaTime_value = Double.parseDouble(delta_timeInput.getText());
			run_sim(steps_value, deltaTime_value);
		});

		// Quit Button
		_toolaBar.add(Box.createGlue()); // this aligns the button to the right
		_toolaBar.addSeparator();
		_quitButton = new JButton();
		_quitButton.setToolTipText("Exit");
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		_quitButton.addActionListener((e) -> System.exit(0));
		_toolaBar.add(_quitButton);

		_changeRegionsDialog = new ChangeRegionsDialog(_ctrl);
	}

	private void run_sim(int n, double dt) {
		if (n > 0 && !_stopped) {
			try {
				long startTime = System.currentTimeMillis();
				_ctrl.advance(dt);
				long stepTimeMs = System.currentTimeMillis() - startTime;
				long delay = (long) (dt * 1000 - stepTimeMs);
				Thread.sleep(delay > 0 ? delay : 0);
				SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
			} catch (Exception e) {
				ViewUtils.showErrorMsg(e.getMessage());
				Activate();
				_stopped = true;
			}
		} else {
			Activate();
			_stopped = true;
		}
	}

	private void Activate() {
		_quitButton.setEnabled(true);
		_openButton.setEnabled(true);
		_regionsButton.setEnabled(true);
		_runButton.setEnabled(true);
		_stopButton.setEnabled(true);
		_viewerButton.setEnabled(true);
	}

	private void Deactivate() {
		_quitButton.setEnabled(false);
		_openButton.setEnabled(false);
		_regionsButton.setEnabled(false);
		_runButton.setEnabled(false);
		_stopButton.setEnabled(true);
		_viewerButton.setEnabled(false);
	}
}
