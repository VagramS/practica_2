package simulator.view;

import javax.swing.*;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {
	String _title;
	TableModel _tableModel;
	
	InfoTable(String title, TableModel tableModel) {
		_title = title;
		_tableModel = tableModel;
		initGUI();
	}
	
	private void initGUI() {
	// TODO cambiar el layout del panel a BorderLayout()
	// TODO añadir un borde con título al JPanel, con el texto _title
	// TODO añadir un JTable (con barra de desplazamiento vertical) que use
	// _tableModel
	}
	

}
