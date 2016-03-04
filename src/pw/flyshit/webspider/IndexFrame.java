package pw.flyshit.webspider;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/*
 * 类名: 索引主界面类
 * 说明: 索引功能的UI主界面
 * Mail: Flyshit@cqu.edu.cn
 */
public class IndexFrame extends JFrame {

	private static final long serialVersionUID = 233L;
	private JPanel contentPane;
	private JTextField indexFilePathText;
	private JTextField sqlStrText;
	private JLabel lblNewLabel_1;
	private String sqlConnStr;


	/**
	 * Create the frame.
	 */
	public IndexFrame(String sqlStrConnInput) {
		this.sqlConnStr = sqlStrConnInput;
		setTitle("Index");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 266);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		indexFilePathText = new JTextField();
		indexFilePathText.setText("E:\\Tomcat Project\\NewsWebLuceneIndex");
		indexFilePathText.setBounds(120, 51, 362, 21);
		contentPane.add(indexFilePathText);
		indexFilePathText.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("\u7D22\u5F15\u4FDD\u5B58\u8DEF\u5F84");
		lblNewLabel.setBounds(10, 51, 76, 21);
		contentPane.add(lblNewLabel);
		
		JButton createIndexBtn = new JButton("\u521B\u5EFA\u7D22\u5F15");
		createIndexBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Index test = new Index(sqlConnStr,sqlStrText.getText(),indexFilePathText.getText());
				if(test.createIndex())
				{
					JOptionPane.showMessageDialog(null, "创建索引完毕!","完成",1);
				}
			}
		});
		createIndexBtn.setBounds(183, 163, 127, 56);
		contentPane.add(createIndexBtn);
		
		sqlStrText = new JTextField();
		sqlStrText.setText("select * from news");
		sqlStrText.setBounds(120, 94, 362, 21);
		contentPane.add(sqlStrText);
		sqlStrText.setColumns(10);
		
		lblNewLabel_1 = new JLabel("\u8981\u7D22\u5F15\u7684SQL\u8BED\u53E5");
		lblNewLabel_1.setBounds(10, 97, 100, 15);
		contentPane.add(lblNewLabel_1);
	}
}
