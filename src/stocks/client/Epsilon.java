package stocks.client;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Epsilon extends JPanel {
	
	public static double getPrice(String ticker) {
		if (ticker.equals("CASH")) return 1.0;
		Document doc;
		boolean accept = false;
		do {
		try {
			doc = Jsoup.connect("https://www.google.com/finance/info?q=TSE%3A"+ticker).get();
			JSONArray array = new JSONArray(doc.getElementsByTag("body").text().substring(3).trim());
			JSONObject vars = array.getJSONObject(0);
			double price = Double.parseDouble(vars.getString("l"));
			accept = true;
			return Math.round(price*100.0)/100.0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		} while (!accept);
		return 0.0;
	}
	
	public static String getName(String ticker) {
		if (ticker.equals("CASH")) return "Currency CA$";
		Document doc;
		boolean accept = false;
		do {
		try {
			doc = Jsoup.connect("https://www.google.ca/finance?q=TSE:"+ticker).get();
			String name = doc.getElementById("news-sidebar-footer").getElementsByClass("g-first").text();
			name = name.substring("All news for ".length(), name.length()-2).trim();
			accept = true;
			return name;
		} catch (IOException e) {
			e.printStackTrace();
		}
		} while (!accept);
		return "?";
	}
	
	private void setPortfolio(Map<String, Integer> portfolio) {
		((DefaultTableModel)tablePort.getModel()).setRowCount(0);
		((DefaultTableModel)tablePort.getModel()).setRowCount(portfolio.size());
		new Thread(() -> {
			int i=0;
			double value = 0.0;
			for (Entry<String, Integer> entry: portfolio.entrySet()) {
				tablePort.setValueAt(entry.getKey(), i, 0);
				tablePort.setValueAt(entry.getValue(), i, 2);
				double price = getPrice(entry.getKey());
				tablePort.setValueAt(price, i, 3);
				double x = Math.round(price * entry.getValue()*100.0)/100.0;
				tablePort.setValueAt(x, i, 4);
				tablePort.setValueAt(getName(entry.getKey()), i, 1);
				value += x;
				i++;
			}
			portValue.setText("TOTAL VALUE: $ "+value);
			repaint();
		}).start();
	}
	
	private void setTransactions(List<Transaction> transactions) {
		((DefaultTableModel)tablePend.getModel()).setRowCount(0);
		((DefaultTableModel)tablePend.getModel()).setRowCount(transactions.size());
		for (int i=0;i<transactions.size();i++) {
			tablePend.setValueAt(transactions.get(i).getMessage(), i, 0);
			tablePend.setValueAt(transactions.get(i).getTicker(), i, 1);
			tablePend.setValueAt(transactions.get(i).isBuy()? "Buy":"Sell", i, 2);
			tablePend.setValueAt(transactions.get(i).getAmount(), i, 3);
		}
	}
	
	private void setRankings(Map<String, Double> ranks) {
		((DefaultTableModel)tableRank.getModel()).setRowCount(0);
		((DefaultTableModel)tableRank.getModel()).setRowCount(ranks.size());
		List<String> ranking = ranks.entrySet().stream().sorted((a,b) -> Double.compare(b.getValue(), a.getValue()))
		.map(entry -> entry.getKey()).collect(Collectors.toList());
		for (int i=0;i<ranking.size();i++) {
			String username = ranking.get(i);
			tableRank.setValueAt(i+1, i, 0);
			tableRank.setValueAt(username, i, 1);
			tableRank.setValueAt(ranks.get(username), i, 2);
		}
		rankValue.setText("RANK: "+(ranking.indexOf(user)+1));
	}
	
	public void setUser(String username) {
		this.user = username;
		
		updatePortfolio();
		updateTransactions();
		updateRankings();
	}
	
	private void updatePortfolio() {
		//Portfolio
		Map<String, String> portreq = new HashMap<>();
		portreq.put("action", "list");
		Map<String, String> portraw = Util.sendRequest(portreq);
		Map<String, Integer> port = new HashMap<>();
		portraw.forEach((a,b) -> port.put(a, Integer.parseInt(b)));
		setPortfolio(port);
	}
	
	private void updateTransactions() {
		//Transactions
		Map<String, String> transreq = new HashMap<>();
		transreq.put("action", "transactions");
		Map<String, String> transraw = Util.sendRequest(transreq);
		List<Transaction> trans = new ArrayList<>();
		JSONArray array = new JSONArray(transraw.get("transactions"));
		for (int i=0;i<array.length();i++) {
			JSONObject o = array.getJSONObject(i);
			Transaction t = new Transaction(o.getBoolean("buy"), o.getString("ticker"), o.getInt("amount"));
			t.setMessage(o.getString("message"));
			trans.add(t);
		}
		setTransactions(trans);
	}
	
	private void updateRankings() {
		//Rankings
		Map<String, String> rankreq = new HashMap<>();
		rankreq.put("action", "rank");
		Map<String, String> rankraw = Util.sendRequest(rankreq);
		Map<String, Double> rank = new HashMap<>();
		rankraw.forEach((a,b) -> rank.put(a, Double.parseDouble(b)));
		setRankings(rank);
	}
	
	private void init() {
//		Map<String, Integer> map = new HashMap<>();
//		map.put("CAM", 5);
//		map.put("CAS", 50);
//		map.put("CCA", 100);
//		setPortfolio(map);
//		
//
//		Map<String, Double> rank = new HashMap<>();
//		rank.put("Alice", 555.0);
//		rank.put("Bob", 50.0);
//		rank.put("Charlie", 100.0);
//		setRankings(rank);
	}
	
	protected void actionTransaction(String text, int amount, boolean buy) {
		Map<String, String> request = new HashMap<>();
		if (buy) {
			request.put("action", "buy");
			request.put("ticker", text);
			request.put("amount", String.valueOf(amount*100));
		}
		else { // sell
			request.put("action", "sell");
			request.put("ticker", text);
			request.put("amount", String.valueOf(amount*100));
		}
		Map<String, String> response = Util.sendRequest(request);
		updateTransactions();
		JOptionPane.showMessageDialog(this, response.get("message"), "Response", JOptionPane.PLAIN_MESSAGE);
	}

	public Epsilon() {
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setFont(new Font("Segoe UI Light", Font.PLAIN, 14));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JPanel panelPort = new JPanel();
		tabbedPane.addTab("My Portfolio", null, panelPort, null);
		
		JLabel lblMyPortfolio = new JLabel("MY PORTFOLIO");
		lblMyPortfolio.setFont(new Font("Segoe UI Light", Font.PLAIN, 28));
		
		JScrollPane scrollPane = new JScrollPane();
		
		portValue = new JLabel("TOTAL VALUE: $ 200000");
		portValue.setFont(new Font("Segoe UI Light", Font.PLAIN, 20));
		
		JButton button = new JButton("REFRESH");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updatePortfolio();
			}
		});
		button.setFont(new Font("Segoe UI Light", Font.PLAIN, 20));
		GroupLayout gl_panelPort = new GroupLayout(panelPort);
		gl_panelPort.setHorizontalGroup(
			gl_panelPort.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelPort.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelPort.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
						.addGroup(gl_panelPort.createSequentialGroup()
							.addComponent(lblMyPortfolio)
							.addPreferredGap(ComponentPlacement.RELATED, 347, Short.MAX_VALUE)
							.addComponent(button, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE))
						.addComponent(portValue))
					.addContainerGap())
		);
		gl_panelPort.setVerticalGroup(
			gl_panelPort.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelPort.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelPort.createParallelGroup(Alignment.LEADING)
						.addComponent(lblMyPortfolio)
						.addComponent(button, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(portValue)
					.addContainerGap())
		);
		
		//		Object[][] data = {
		//				{"1", "2", "3"},
		//				{"1", "2", "3"},
		//				{"1", "2", "3"}
		//		};
		//		Object[] col = {"a","b","c"};
				tablePort = new JTable();
				tablePort.setFillsViewportHeight(true);
				scrollPane.setViewportView(tablePort);
				tablePort.setModel(new DefaultTableModel(
					new Object[][] {
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
						{null, null, null, null, null, null},
					},
					new String[] {
						"Ticker", "Name", "Qty", "Unit Price", "Total Value"
					}
				) {
					private static final long serialVersionUID = 1L;
					boolean[] columnEditables = new boolean[] {
						false, false, false, false, false
					};
					public boolean isCellEditable(int row, int column) {
						return columnEditables[column];
					}
				});
				tablePort.getColumnModel().getColumn(1).setPreferredWidth(350);
		panelPort.setLayout(gl_panelPort);
		
		JPanel panelTrans = new JPanel();
		tabbedPane.addTab("New Transaction", null, panelTrans, null);
		
		JLabel lblNewTransaction = new JLabel("NEW TRANSACTION");
		lblNewTransaction.setFont(new Font("Segoe UI Light", Font.PLAIN, 28));
		
		JLabel lblStockTicker = new JLabel("Stock Ticker");
		
		JComboBox<String> tickerfield = new JComboBox<String>();
		tickerfield.setModel(new DefaultComboBoxModel<String>(new String[] {"ABX", "AC", "AGF.B", "AGU", "ALA", "ATD.B", "BB", "BBD.B", "BCE", "BLD", "BMO", "BNS", "BPY.UN", "CAE", "CAM", "CAS", "CCA", "CCO", "CM", "CNQ", "CNR", "CP", "CPG", "CS", "CTC.A", "CUF.UN", "DOL", "DSG", "ECA", "EMA", "EMP.A", "ENB", "FSZ", "FTS", "G", "GIB.A", "GIL", "HGD", "HGU", "HND", "HNU", "HOD", "HOU", "HRX", "HSD", "HSE", "HSU", "HXD", "HXU", "IAG", "IMO", "INE", "IRG", "JNX", "L", "LB", "MFC", "MFI", "MG", "MNT", "MNW", "MRU", "MSL", "NA", "OCX", "OR", "ORL", "ORT", "OSB", "OTC", "OVI.A", "PD", "PEY", "PJC.A", "PLI", "POT", "POW", "PWF", "QBR.B", "QSR", "RCH", "RCI.B", "RON", "RUS", "RX", "RY", "SAP", "SJ", "SLF", "SNC", "SU", "T", "TCK.B", "TCL.A", "TD", "TFI", "TMB", "TPX.B", "TRP", "TRZ", "UFS", "VNR", "VRX", "WJA", "WSP", "X", "Y"}));
		
		JLabel lblAmountinHundreds = new JLabel("Amount (in hundreds)");
		
		hundreds = new JTextField();
		hundreds.setColumns(10);
		
		JLabel lblTransactionType = new JLabel("Transaction Type");
		
		JComboBox<String> type = new JComboBox<>();
		type.setModel(new DefaultComboBoxModel<>(new String[] {"Buy", "Sell"}));
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int amount;
				try {
					amount = Integer.parseInt(hundreds.getText());
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(Epsilon.this, "Invalid Amount", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				actionTransaction((String)tickerfield.getSelectedItem(), amount, ((String)type.getSelectedItem()).equalsIgnoreCase("Buy"));
			}
		});
		GroupLayout gl_panelTrans = new GroupLayout(panelTrans);
		gl_panelTrans.setHorizontalGroup(
			gl_panelTrans.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelTrans.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelTrans.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewTransaction)
						.addGroup(gl_panelTrans.createSequentialGroup()
							.addComponent(lblStockTicker)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(tickerfield, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelTrans.createSequentialGroup()
							.addComponent(lblAmountinHundreds)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(hundreds, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelTrans.createSequentialGroup()
							.addComponent(lblTransactionType)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(type, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnSubmit))
					.addContainerGap(423, Short.MAX_VALUE))
		);
		gl_panelTrans.setVerticalGroup(
			gl_panelTrans.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelTrans.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewTransaction)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelTrans.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblStockTicker)
						.addComponent(tickerfield, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelTrans.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAmountinHundreds)
						.addComponent(hundreds, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelTrans.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTransactionType)
						.addComponent(type, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnSubmit)
					.addContainerGap(396, Short.MAX_VALUE))
		);
		panelTrans.setLayout(gl_panelTrans);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Transactions", null, panel, null);
		
		JLabel lblPendingTransactions = new JLabel("TRANSACTIONS");
		lblPendingTransactions.setFont(new Font("Segoe UI Light", Font.PLAIN, 28));
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		JButton button_1 = new JButton("REFRESH");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTransactions();
			}
		});
		button_1.setFont(new Font("Segoe UI Light", Font.PLAIN, 20));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(lblPendingTransactions)
							.addPreferredGap(ComponentPlacement.RELATED, 220, Short.MAX_VALUE)
							.addComponent(button_1, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblPendingTransactions)
						.addComponent(button_1, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		tablePend = new JTable();
		scrollPane_2.setViewportView(tablePend);
		tablePend.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Message", "Ticker", "Type", "Amount"
			}
		) {
			private static final long serialVersionUID = 1L;
			boolean[] columnEditables = new boolean[] {
				false, false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tablePend.getColumnModel().getColumn(0).setPreferredWidth(350);
		panel.setLayout(gl_panel);
		
		JPanel panelRank = new JPanel();
		tabbedPane.addTab("Rankings", null, panelRank, null);
		
		JLabel lblRankings = new JLabel("RANKINGS");
		lblRankings.setFont(new Font("Segoe UI Light", Font.PLAIN, 28));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		rankValue = new JLabel("RANK: -1");
		rankValue.setFont(new Font("Segoe UI Light", Font.PLAIN, 20));
		
		JButton btnRefresh = new JButton("REFRESH");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateRankings();
			}
		});
		btnRefresh.setFont(new Font("Segoe UI Light", Font.PLAIN, 20));
		GroupLayout gl_panelRank = new GroupLayout(panelRank);
		gl_panelRank.setHorizontalGroup(
			gl_panelRank.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelRank.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelRank.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
						.addGroup(gl_panelRank.createSequentialGroup()
							.addComponent(lblRankings)
							.addPreferredGap(ComponentPlacement.RELATED, 423, Short.MAX_VALUE)
							.addComponent(btnRefresh))
						.addComponent(rankValue, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panelRank.setVerticalGroup(
			gl_panelRank.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelRank.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelRank.createParallelGroup(Alignment.LEADING)
						.addComponent(lblRankings)
						.addComponent(btnRefresh))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(rankValue, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		tableRank = new JTable();
		tableRank.setFillsViewportHeight(true);
		tableRank.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
				{null, null, null},
			},
			new String[] {
				"Rank", "Name", "Value"
			}
		) {
			private static final long serialVersionUID = 1L;
			boolean[] columnEditables = new boolean[] {
				false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tableRank.getColumnModel().getColumn(0).setPreferredWidth(5);
		tableRank.getColumnModel().getColumn(1).setPreferredWidth(350);
		scrollPane_1.setViewportView(tableRank);
		panelRank.setLayout(gl_panelRank);
		setLayout(groupLayout);
		
		init();
	}
	
	private static final long serialVersionUID = 1L;
	private JTable tablePort;
	private JTextField hundreds;
	private JTable tableRank;
	private JTable tablePend;
	private JLabel portValue;
	private JLabel rankValue;
	private String user;
}
