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
 * 类名: 爬虫类
 * 说明: 负责从用户给定的站点爬取所有新闻并存储到SQL数据库中
 * Mail: Flyshit@cqu.edu.cn
 */

public class Spider {
	private String crawlSite; //爬取的站点
	private String url; //网页URL
	private Document targetUrlDoc; //网页Document
	private String title; //新闻标题
	private String date; //新闻时间
	private String contect; //新闻内容
	private String newsType = "1"; //新闻类型
	private String sqlConnectStr; //数据库连接字符串
	private ArrayList<String> allUrl; //网页中的所有链接
	
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
	public boolean getDataFormUrl(String urlInput) //从给定URL中获取网页源码，标题，网页内容和网页中的所有链接
	{
		/*
		 * 使用Jsoup从给定URL中获取网页源码，标题，网页新闻内容和网页中的所有链接
		 */
		try 
		{
			this.url = urlInput;
			targetUrlDoc = Jsoup.connect(this.url).userAgent("Flyshit's Spider").timeout(15000).get();
			if(this.crawlContent()) //如果爬取数据成功则保存到数据库
			{
				if(!this.saveDataToSqlServer()) //如果保存到数据库失败
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
	
	private boolean crawlContent() //爬取网页里的新闻内容
	{
		try
		{
			Element titleElement = this.targetUrlDoc.select("div.title").first(); //获取第一个div class为title的元素，即新闻标题
			Element contentElement = this.targetUrlDoc.select("div#zoom").first(); //获取第一个div id为zoom的元素，即新闻内容
			Element dateElement = this.targetUrlDoc.select("span.datetime").first(); //获取第一个span class 为datetime的元素，即新闻时间
			Element newsTypeElement = this.targetUrlDoc.select("div#location").select("a").last(); //获取div id为location元素中的最后一个a连接，即新闻栏目类型
			if(titleElement == null) //如果没有这个元素，说明此页面不是新闻页
			{
				return false;
			}
			this.title = titleElement.text(); //获取新闻内容
			if(contentElement == null)
			{
				return false;
			}
			this.contect = contentElement.html();  //获取新闻内容，包含HTML格式
			this.contect = this.contect.replaceAll("src=\"/", "src=\"" + this.crawlSite + "/"); //将相对路径转成绝对路径
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
	private void crawlAllUrl() //爬取网页里的所有链接
	{
		try
		{
			Elements urlElements = this.targetUrlDoc.select("a"); //获取所有a标签超链接
			for(Element urlElement : urlElements) //遍历所有超链接
			{
				String urlSingle = urlElement.attr("href"); //获取单条超链接
				if(urlSingle.startsWith("/")) //如果是相对地址，则转换为绝对地址
				{
					urlSingle = this.crawlSite + urlSingle;
				}
				if(!urlSingle.startsWith(this.crawlSite)) //只爬去站内内容，如果不是站内链接则丢弃
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
	
	public boolean saveDataToSqlServer() //保存爬取到的数据到SQL数据库
	{
		Connection sqlConn = null;
		Statement sqlState = null;
		int sqlResult;
		String sqlStr;
		try
		{
			sqlStr = "insert into news (title,contents,newsdate,newstype,crawlurl) values ('"+this.title+"','"+this.contect+"','"+this.date+"','"+this.newsType+"','"+this.url+"');"; //拼接SQL字符串
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			sqlConn = DriverManager.getConnection(this.sqlConnectStr);
			sqlState = sqlConn.createStatement();
			sqlResult = sqlState.executeUpdate(sqlStr); //执行SQL语句
			if(sqlResult<=0) //插入失败
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
	
	public ArrayList<String> getAllUrl() //获取爬取到的所有URL
	{
		return this.allUrl;
	}

}
