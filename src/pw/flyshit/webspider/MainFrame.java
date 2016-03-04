package pw.flyshit.webspider;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JTable;

/*
 * ����: �������UI��������
 * ˵��: ��������UI������
 * Mail: Flyshit@cqu.edu.cn
 */

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 233L;
	private JPanel contentPane;
	private JButton startBtn;
	private JButton stopBtn;
	private JTextField siteUrl;
	private JTextField sqlConnStr;
	private JLabel waittingUrlCountText;
	private JLabel completeUrlCountText;
	private JTable waittingUrlTable;
	private JTable completeUrlTable;
	private String[][] waittingRows;
	private String[] waittingColumns;
	private String[][] completeRows;
	private String[] completeColumns;
	private DefaultTableModel waittingUrlModel;
	private DefaultTableModel completeUrlModel;
	private boolean isSpiderRunning;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public MainFrame() {
		setTitle("Web Spider");
		this.isSpiderRunning = false;
		this.waittingRows = new String[][]{};
		this.waittingColumns = new String[]{"����ȡ����"};
		this.completeRows = new String[][]{};
		this.completeColumns = new String[]{"����ȡ����"};
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 643, 592);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		siteUrl = new JTextField();
		siteUrl.setText("http://news.cqu.edu.cn");
		siteUrl.setBounds(117, 36, 448, 21);
		contentPane.add(siteUrl);
		siteUrl.setColumns(10);
		
		waittingUrlTable = new JTable()
		{
			private static final long serialVersionUID = 233L;
			public boolean isCellEditable(int row, int column)//��Ϊ���ɱ༭
			{
				return false;
				}
		};
		waittingUrlModel = new DefaultTableModel(this.waittingRows,this.waittingColumns);
		waittingUrlTable.setModel(this.waittingUrlModel);
		JScrollPane waittingScroll = new JScrollPane(this.waittingUrlTable);
		waittingScroll.setBounds(59,154,506,139);
		contentPane.add(waittingScroll);
		
		completeUrlTable = new JTable()
		{
			private static final long serialVersionUID = 233L;
			public boolean isCellEditable(int row, int column)//��Ϊ���ɱ༭
			{
				return false;
				}
		};
		completeUrlModel = new DefaultTableModel(this.completeRows,this.completeColumns);
		completeUrlTable.setModel(completeUrlModel);
		JScrollPane completeScroll = new JScrollPane(this.completeUrlTable);
		completeScroll.setBounds(59, 335, 506, 139);
		contentPane.add(completeScroll);
		
		
		
		sqlConnStr = new JTextField();
		sqlConnStr.setText("jdbc:sqlserver://db.flyshit.pw:1433;databaseName=NewsWeb;user=flyshit;password=123457");
		sqlConnStr.setBounds(117, 79, 448, 21);
		contentPane.add(sqlConnStr);
		sqlConnStr.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Site");
		lblNewLabel.setBounds(59, 39, 30, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("SQL");
		lblNewLabel_1.setBounds(59, 82, 54, 15);
		contentPane.add(lblNewLabel_1);
		
		startBtn = new JButton("\u5F00\u59CB");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!isSpiderRunning)
				{
					new Thread(new Runnable() {
						public void run() {
							spiderWebsite();
						}
					}).start();
				}
			}
		});
		startBtn.setBounds(250, 500, 118, 54);
		contentPane.add(startBtn);
		
		stopBtn = new JButton("\u505C\u6B62");
		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(isSpiderRunning)
				{
					isSpiderRunning = false;
				}
			}
		});
		stopBtn.setEnabled(false);
		stopBtn.setBounds(442, 500, 123, 54);
		contentPane.add(stopBtn);
		
		waittingUrlCountText = new JLabel("\u5F85\u722C\u53D6\u6570\u91CF: 0");
		waittingUrlCountText.setBounds(450, 135, 115, 15);
		contentPane.add(waittingUrlCountText);
		
		completeUrlCountText = new JLabel("\u5DF2\u722C\u53D6\u6570\u91CF: 0");
		completeUrlCountText.setBounds(450, 316, 115, 15);
		contentPane.add(completeUrlCountText);
		
		JButton createIndexBtn = new JButton("\u521B\u5EFA\u7D22\u5F15");
		createIndexBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				IndexFrame indexFrame = new IndexFrame(sqlConnStr.getText());
				indexFrame.setVisible(true);
			}
		});
		createIndexBtn.setBounds(59, 500, 118, 54);
		contentPane.add(createIndexBtn);
	}
	
	private void spiderWebsite()
	{
		this.startBtn.setEnabled(false);
		this.isSpiderRunning = true;
		this.stopBtn.setEnabled(true);
		
		Spider spiderObj = new Spider(this.siteUrl.getText(),this.sqlConnStr.getText());
		this.waittingUrlModel.addRow(new String[]{this.siteUrl.getText()});
		while(this.isSpiderRunning)
		{
			try
			{
				if(this.waittingUrlModel.getRowCount()<=0) //����ȴ�URL����С�ڵ���0����˵���������
				{
					this.isSpiderRunning = false;
					break;
				}
				String url = String.valueOf(this.waittingUrlModel.getValueAt(0, 0));
				spiderObj.getDataFormUrl(url);
				this.waittingUrlModel.removeRow(0);
				this.completeUrlModel.addRow(new String[]{url});
				ArrayList<String> newUrls = spiderObj.getAllUrl();
				for(String newUrl : newUrls)
				{
					int i;
					for(i=0;i<this.completeUrlModel.getRowCount();i++) //������URL�Ƿ��Ѿ���ȡ����
					{
						if(newUrl.equals(this.completeUrlModel.getValueAt(i, 0))) //�������ȡ��
						{
							break;
						}
					}
					if(i == this.completeUrlModel.getRowCount()) //������ϣ���URLδ��ȡ��
					{	
						int j;
						for(j=0;j<this.waittingUrlModel.getRowCount();j++) //������URL�Ƿ��Ѿ��ڴ���ȡ�б���
						{
							if(newUrl.equals(this.waittingUrlModel.getValueAt(j, 0))) //����Ѿ������ڴ���ȡ�б���
							{
								break;
							}
							
						}
						if(j == this.waittingUrlModel.getRowCount()) //������ϣ���URL���ڴ���ȡ�б���
						{
							this.waittingUrlModel.addRow(new String[]{newUrl}); //��ӵ�����ȡ�б�
						}
					}
				}
				this.waittingUrlCountText.setText("\u5F85\u722C\u53D6\u6570\u91CF: "+String.valueOf(this.waittingUrlModel.getRowCount())); //���´���ȡURL������ʾ
				this.completeUrlCountText.setText("\u5DF2\u722C\u53D6\u6570\u91CF: "+String.valueOf(this.completeUrlModel.getRowCount())); //��������ȡURL������ʾ
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		this.stopBtn.setEnabled(false);
		this.startBtn.setEnabled(true);
		JOptionPane.showMessageDialog(null, "��վ��ȡ���!", "���", 1);
	}
}
