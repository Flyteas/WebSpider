package pw.flyshit.webspider;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

/*
 * ����: ������
 * ˵��: ����lucene�����Ĵ�����ά��
 * Mail: Flyshit@cqu.edu.cn
 */
public class Index {
	private String indexPath; //��������·��
	private String sqlConnStr; //sql���ݿ������ַ���
	private String sqlStr; //sql��ѯ���
	private Connection sqlConn = null;
	private Statement sqlState = null;
	
	public Index(String sqlConnStrInput,String sqlStrInput,String indexPathInput)
	{
		this.sqlConnStr = sqlConnStrInput;
		this.sqlStr = sqlStrInput;
		this.indexPath = indexPathInput;
	}
	
	public void setIndexPath(String indexPathInput)
	{
		this.indexPath = indexPathInput;
	}
	
	public void setSqlConnStr(String sqlConnStrInput)
	{
		this.sqlConnStr = sqlConnStrInput;
	}
	
	public void setSqlStr(String sqlStrInput)
	{
		this.sqlStr = sqlStrInput;
	}
	
	public String getIndexPath()
	{
		return this.indexPath;
	}
	
	public boolean createIndex() //��������
	{
		IndexWriter indexW = null;
		try
		{
			if(!this.openSqConnection()) //��SQL���ݿ�����
			{
				return false;
			}
			ResultSet sqlResult = this.sqlState.executeQuery(this.sqlStr); //��ȡSQL��ѯ���
			Directory indexDir = new SimpleFSDirectory(Paths.get(this.indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig indexWriterConf = new IndexWriterConfig(analyzer);
			indexWriterConf.setOpenMode(OpenMode.CREATE);
			indexW = new IndexWriter(indexDir,indexWriterConf);
			while(sqlResult.next()) //��������
			{
				Document indexDoc = new Document();
				indexDoc.add(new StringField("id",sqlResult.getString("id"),Field.Store.YES));
				indexDoc.add(new StringField("title",sqlResult.getString("title"),Field.Store.YES));
				indexDoc.add(new TextField("contents",sqlResult.getString("contents"),Field.Store.YES));
				indexDoc.add(new StringField("newstype",sqlResult.getString("newstype"),Field.Store.YES));
				indexDoc.add(new StringField("newsdate",sqlResult.getString("newsdate"),Field.Store.YES));
				indexW.addDocument(indexDoc);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(indexW!=null)
			{
				try
				{
					indexW.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			this.closeSqlConnection(); //�ر����ݿ�����
		}
		return true;
	}

	private boolean openSqConnection() //ִ��SQL��䲢��ȡ����
	{
		try
		{	
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			sqlConn = DriverManager.getConnection(this.sqlConnStr);
			sqlState = sqlConn.createStatement();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			if(sqlConn != null)
			{
				try
				{
					sqlConn.close();
				}
				catch(Exception exceptionClose)
				{
					exceptionClose.printStackTrace();
				}
			}
			if(sqlState != null)
			{
				try
				{
					sqlState.close();
				}
				catch(Exception exceptionClose)
				{
					exceptionClose.printStackTrace();
				}
			}
			return false;
		}
		return true;
	}
	
	private void closeSqlConnection() //�ر�SQL����
	{
		if(sqlConn != null)
		{
			try
			{
				sqlConn.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(sqlState != null)
		{
			try
			{
				sqlState.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
