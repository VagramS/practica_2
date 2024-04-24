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
		setLayout(new BorderLayout());

		setBorder(BorderFactory.createTitledBorder(_title));

		JTable table = new JTable(_tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);
	}

}
