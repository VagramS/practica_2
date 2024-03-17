package simulator.view;

import java.awt.BorderLayout;

import javax.swing.*;
import simulator.control.Controller;

public class MainWindow extends JFrame {
	
	private Controller _ctrl;
	
	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		_ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() 
	{
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);
		// TODO crear ControlPanel y añadirlo en PAGE_START de mainPanel
		// TODO crear StatusBar y añadirlo en PAGE_END de mainPanel
		// Definición del panel de tablas (usa un BoxLayout vertical)
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		// TODO crear la tabla de especies y añadirla a contentPanel.
		// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
		// TODO crear la tabla de regiones.
		// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño
		// TODO llama a ViewUtils.quit(MainWindow.this) en el método windowClosing
		addWindowListener( … );
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	private void run_sim(int n, double dt) 
	{
		if (n > 0 && !_stopped) {
		try {
		_ctrl.advance(dt);
		SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
		} catch (Exception e) {
		// TODO llamar a ViewUtils.showErrorMsg con el mensaje de error
		// que corresponda
		// TODO activar todos los botones
		_stopped = true;
		}
		} else {
		// TODO activar todos los botones
		_stopped = true;
		}
	}


}
