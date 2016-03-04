package pw.flyshit.webspider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
 * ����: ������
 * ˵��: ������û�������վ����ȡ�������Ų��洢��SQL���ݿ���
 * Mail: Flyshit@cqu.edu.cn
 */

public class Spider {
	private String crawlSite; //��ȡ��վ��
	private String url; //��ҳURL
	private Document targetUrlDoc; //��ҳDocument
	private String title; //���ű���
	private String date; //����ʱ��
	private String contect; //��������
	private String newsType = "1"; //��������
	private String sqlConnectStr; //���ݿ������ַ���
	private ArrayList<String> allUrl; //��ҳ�е���������
	
	public Spider(String crawlSiteUrl,String sqlConnStr)
	{
		this.crawlSite = crawlSiteUrl;
		this.url = "";
		this.targetUrlDoc = null;
		this.title = "";
		this.contect = "";
		this.sqlConnectStr = sqlConnStr;
		this.allUrl = new ArrayList<String>();
	}
	public boolean getDataFormUrl(String urlInput) //�Ӹ���URL�л�ȡ��ҳԴ�룬���⣬��ҳ���ݺ���ҳ�е���������
	{
		/*
		 * ʹ��Jsoup�Ӹ���URL�л�ȡ��ҳԴ�룬���⣬��ҳ�������ݺ���ҳ�е���������
		 */
		try 
		{
			this.url = urlInput;
			targetUrlDoc = Jsoup.connect(this.url).userAgent("Flyshit's Spider").timeout(15000).get();
			if(this.crawlContent()) //�����ȡ���ݳɹ��򱣴浽���ݿ�
			{
				if(!this.saveDataToSqlServer()) //������浽���ݿ�ʧ��
				{
					System.out.println("Error url: "+this.url);
					System.out.println("Error message: Save crawl data to database faild!");
				}
			}
			this.crawlAllUrl();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean crawlContent() //��ȡ��ҳ�����������
	{
		try
		{
			Element titleElement = this.targetUrlDoc.select("div.title").first(); //��ȡ��һ��div classΪtitle��Ԫ�أ������ű���
			Element contentElement = this.targetUrlDoc.select("div#zoom").first(); //��ȡ��һ��div idΪzoom��Ԫ�أ�����������
			Element dateElement = this.targetUrlDoc.select("span.datetime").first(); //��ȡ��һ��span class Ϊdatetime��Ԫ�أ�������ʱ��
			Element newsTypeElement = this.targetUrlDoc.select("div#location").select("a").last(); //��ȡdiv idΪlocationԪ���е����һ��a���ӣ���������Ŀ����
			if(titleElement == null) //���û�����Ԫ�أ�˵����ҳ�治������ҳ
			{
				return false;
			}
			this.title = titleElement.text(); //��ȡ��������
			if(contentElement == null)
			{
				return false;
			}
			this.contect = contentElement.html();  //��ȡ�������ݣ�����HTML��ʽ
			this.contect = this.contect.replaceAll("src=\"/", "src=\"" + this.crawlSite + "/"); //�����·��ת�ɾ���·��
			if(dateElement != null)
			{
				this.date = dateElement.text();
			}
			if(newsTypeElement != null)
			{
				this.newsType = newsTypeElement.text();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private void crawlAllUrl() //��ȡ��ҳ�����������
	{
		try
		{
			Elements urlElements = this.targetUrlDoc.select("a"); //��ȡ����a��ǩ������
			for(Element urlElement : urlElements) //�������г�����
			{
				String urlSingle = urlElement.attr("href"); //��ȡ����������
				if(urlSingle.startsWith("/")) //�������Ե�ַ����ת��Ϊ���Ե�ַ
				{
					urlSingle = this.crawlSite + urlSingle;
				}
				if(!urlSingle.startsWith(this.crawlSite)) //ֻ��ȥվ�����ݣ��������վ����������
				{
					continue;
				}
				this.allUrl.add(urlSingle);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean saveDataToSqlServer() //������ȡ�������ݵ�SQL���ݿ�
	{
		Connection sqlConn = null;
		Statement sqlState = null;
		int sqlResult;
		String sqlStr;
		try
		{
			sqlStr = "insert into news (title,contents,newsdate,newstype,crawlurl) values ('"+this.title+"','"+this.contect+"','"+this.date+"','"+this.newsType+"','"+this.url+"');"; //ƴ��SQL�ַ���
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			sqlConn = DriverManager.getConnection(this.sqlConnectStr);
			sqlState = sqlConn.createStatement();
			sqlResult = sqlState.executeUpdate(sqlStr); //ִ��SQL���
			if(sqlResult<=0) //����ʧ��
			{
				return false; 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
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
		return true;
	}
	
	public ArrayList<String> getAllUrl() //��ȡ��ȡ��������URL
	{
		return this.allUrl;
	}

}
