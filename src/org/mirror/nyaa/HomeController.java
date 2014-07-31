package org.mirror.nyaa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static final String nyaa = "http://www.nyaa.se";
	private static final String sukebei = "http://sukebei.nyaa.eu";

	@RequestMapping(value = "/")
	public String nyaa(Model model, SearchDTO searchDTO) {
		String url = setUrl(searchDTO, nyaa);
		Document doc = htmlPaser(url);
		
		if(doc != null){
			model.addAttribute("contents", getContents(doc));
			model.addAttribute("pages", getPages(doc));
		}else{
			
		}
		
		return "index";
	}
	
	@RequestMapping(value = "/h")
	public String sukebei(Model model, SearchDTO searchDTO) {
		String url = setUrl(searchDTO, sukebei);
		Document doc = htmlPaser(url);
		
		if(doc != null){
			model.addAttribute("contents", getContents(doc));
			model.addAttribute("pages", getPages(doc));
		}else{
			
		}
		return "index";
	}
	
	@RequestMapping(value = "/v")
	public String view(Model model, SearchDTO searchDTO) {
		String url = searchDTO.getUrl()+ "&showfiles=1";
		Document doc = htmlPaser(url);
		
		if(doc != null){
			model.addAttribute("view", getViewHtml(doc));
		}
		return "view";
	}
	
	
	public String setUrl(SearchDTO searchDTO, String url){
		String term = searchDTO.getTerm() == null || "".equals(searchDTO.getTerm()) ? "" : "&term="+searchDTO.getTerm();
		String cats = searchDTO.getCats() == null  || "".equals(searchDTO.getCats()) ? "" : "&cats="+searchDTO.getCats();
		String fullUrl = url+"?offset="+searchDTO.getOffset()+term+cats;
		return fullUrl;
	}
	
	public Document htmlPaser(String url){
		/*
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			HttpClient httpClinet = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			String html = new String( httpClinet.execute(httpGet, responseHandler).getBytes("8859_1"), "UTF-8");
			Document doc = Jsoup.parse(html);
			
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		*/
		
		try {
			URL targetUrl = new URL(url);
			BufferedReader reader = new BufferedReader(new InputStreamReader(targetUrl.openStream(),"UTF-8"));
			String html = reader.readLine();
			Document doc = Jsoup.parse(html);
		    reader.close();
		    return doc;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getContents(Document doc) {
		Elements rows = doc.select(".tlistrow");
		// System.out.println(rows);
		StringBuilder result = new StringBuilder();
		for (Element row : rows) {
			Iterator<Element> iterEle = row.getElementsByTag("td").iterator();
			StringBuilder str = new StringBuilder();
			str.append("<tr>");
			for (int idx = 0; idx < 4; idx++) {
				str.append(iterEle.next() + "\n");
			}
			str.append("</tr>");
			result.append(str);
		}
		return result.toString();
	}

	public HashMap<String, ArrayList<Integer>> getPages(Document doc) {
		HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
		Elements rows = doc.select(".pages");
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		ArrayList<Integer> result = new ArrayList<Integer>();
		ArrayList<Integer> currentArray = new ArrayList<Integer>();
		ArrayList<Integer> nextArray = new ArrayList<Integer>();
		ArrayList<Integer> prevArray = new ArrayList<Integer>();
		
		Iterator<Element> iterEle = rows.get(0).getElementsByTag("a").iterator();
		int current = Integer.parseInt(rows.get(0).getElementsByTag("b").text());
		currentArray.add(current);
		
		int limit = 0, start = 0;
		while (iterEle.hasNext()) {
			tempList.add(Integer.parseInt(iterEle.next().text()));
		}
		int totalPageCount = tempList.size()+1;
		nextArray.add(Math.min(totalPageCount, current + 10));
		prevArray.add(Math.max(1, current - 10));
		
		start = ((current - 1) / 10) * 10 + 1;
		limit = Math.min(start + 10 - 1 , totalPageCount);
		for (int idx = start - 1; idx < limit; idx++) {
			result.add(idx + 1);
		}
		map.put("current", currentArray);
		map.put("next", nextArray);
		map.put("prev", prevArray);
		map.put("list", result);
		return map;
	}
	
	public HashMap<String,String> getViewHtml(Document doc){
		HashMap<String,String> map = new HashMap<String, String>();
		
		Elements information = doc.select(".viewtable tbody");
		StringBuilder informationHtml = new StringBuilder();
		for (Element row : information) {
			Iterator<Element> iterEle = row.getElementsByTag("tr").iterator();
			StringBuilder str = new StringBuilder();
			
			str.append("<tr>");
			while(iterEle.hasNext()){
				Element tag = iterEle.next();
				for(int idx = 0; idx < 4; idx++){
					str.append(tag.getElementsByTag("td").eq(idx));
					str.append("\n");
					if(idx == 1){
						str.append("</tr>\n<tr>");
					}
				}
				str.append("</tr>\n<tr>");
			}
			str.append("<tr>");
			informationHtml.append(str);
		}
		
		Elements txtDownBtn = doc.select(".viewdownloadtxtbutton");
		String txtDownBtnHtml = txtDownBtn.get(0).html();
		Elements torrentDownBtn = doc.select(".viewdownloadbutton");
		String torrentDownBtnHtml = torrentDownBtn.get(0).html();
		Elements description = doc.select(".viewdescription");
		String descriptionHtml = description.get(0).html();
		
		Elements showFiles = doc.select(".fileentry");
		StringBuilder filesHtml = new StringBuilder();
		for (Element row : showFiles) {
			Iterator<Element> iterEle = row.getElementsByTag("td").iterator();
			StringBuilder str = new StringBuilder();
			str.append("<tr>");
			for (int idx = 0; idx < 2; idx++) {
				str.append(iterEle.next() + "\n");
			}
			str.append("</tr>");
			filesHtml.append(str);
		}
		
		map.put("information", informationHtml.toString());
		map.put("txtDownBtn", txtDownBtnHtml);
		map.put("torrentDownBtn", torrentDownBtnHtml);
		map.put("description", descriptionHtml);
		map.put("files", filesHtml.toString());
		return map;
	}

}
