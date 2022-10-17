import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class Program {

	public static void main(String[] args) throws IOException {
		// sending request to research web-site:
		String url = "https://www.elibrary.ru/query_results.asp";
		Connection connection = Jsoup.connect(url);
		connection.timeout(100000);
		
		connection.header("Content-Type", "application/x-www-form-urlencoded");
		connection.header("accept-encoding", "gzip, deflate, br");
		connection.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
		connection.header("Cookie", "_ym_uid=1662708035623638858; _ym_d=1662708035; __utmz=216042306.1663582577.2.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); SCookieGUID=59480AC2%2D5802%2D481A%2D8343%2D2B293215C7E6; SUserID=499194467; _ym_isad=2; __utma=216042306.466657490.1662708035.1666020841.1666032780.10; __utmc=216042306; __utmt=1; __utmb=216042306.10.10.1666032780");
		
		connection.data("ftext", "многопоточность");
		connection.data("where_name", "on");
		connection.data("type_article", "on");
		//connection.data("type_book", "on");
		Document docCustomConn = connection.post();
		//System.out.print(docCustomConn);
		// parsing data
		List<Article> articles = new ArrayList<>();
		Element table = docCustomConn.getElementById("restab");
		Elements trs = table.getElementsByTag("tr");
		Element td;
		Elements tagsA;
		String name, author, journal, year, pages;
		for(Element tr : trs)
		{
			td = tr.getElementsByAttributeValue("align", "left").first();
			if (td == null) continue;
			Article newArticle = new Article();
			tagsA = td.getElementsByAttributeValueStarting("href", "/contents.asp?id=");
			
			newArticle.name = td.getElementsByTag("span").text();
			newArticle.author = td.getElementsByTag("i").text();
			if (tagsA.first() != null)
			{
				newArticle.journal = tagsA.first().text();
				newArticle.year = tagsA.first().nextSibling().toString();
				newArticle.year = newArticle.year.replace(".", "").replace(" ", "").substring(0, 4);
				newArticle.pages = tagsA.last().nextSibling().toString().replace(".", "");
			}
						
			articles.add(newArticle);
			
		}
		
		//save info in file:
		try {
		      FileWriter file = new FileWriter("result.txt");
		      
		      file.write("Результаты поиска статей ресурса www.elibrary.ru по запросу 'многопоточность': \n");
		      int k = 0;
		      for(Article a : articles)
		      {
		    	  k++;
		    	  
		    	  file.write(k + ")\n");
		    	  file.write("Название: " + a.name + '\n');
		    	  file.write("Автор: " + a.author + '\n');
		    	  file.write("Журнал: " + a.journal + ", " + a.pages + '\n');
		    	  file.write("Год поблукации:" + a.year + '\n');
		    	  
		      }
		      file.close();
		      
	    } catch (IOException e) {
		      System.out.println("Возникла ошибка");
		      e.printStackTrace();
	    }
		System.out.println("Файл успешно создан!");
	}
	
	public static class Article
	{
		public String name;
		public String author;
		public String journal;
		public String year;
		public String pages;	
	}

}
