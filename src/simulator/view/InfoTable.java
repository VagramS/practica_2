package simulator.view;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {

	private static final long serialVersionUID = 1L;
	String _title;
	TableModel _tableModel;

	InfoTable(String title, TableModel tableModel) {
		_title = title;
		_tableModel = tableModel;
		initGUI();
	}

	private void initGUI() {
		// TODO cambiar el layout del panel a BorderLayout()
		setLayout(new BorderLayout());

		// TODO añadir un borde con título al JPanel, con el texto _title
		setBorder(BorderFactory.createTitledBorder(_title));

		// TODO añadir un JTable (con barra de desplazamiento vertical) que use
		// _tableModel
		JTable table = new JTable(_tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);
	}

}
