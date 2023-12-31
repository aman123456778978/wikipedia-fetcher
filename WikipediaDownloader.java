package tech.codingclub.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;

public class WikipediaDownloader implements Runnable{
    private String keyword;
    public WikipediaDownloader() {

    }
    public WikipediaDownloader(String keyword) {
        this.keyword = keyword;
    }


    @Override
    public void run() {

        //1. get clean keyword
        //2. get the url for wikipedia
        //3. make a get request to wikipedia
        //4. parsing the useful results using jsoup
        //5. showing results
        //step 1
        this.keyword=this.keyword.trim().replaceAll("[ ]+","_");
        //replace all continiuous space to underscore
        //The trim() method removes whitespace from both ends of a string.

        //step 2
        String wikiUrl=getWikipediaUrlForQuery(this.keyword);

        String response="";
        String result;
        String imageUrl = null;
        try {
            //step 3
            String wikipediaResponseHTML = HttpURLConnectionExample.sendGet(wikiUrl);
//            System.out.println(wikipediaResponseHTML); // we are getting html...but its not readable yet..for that we will use jsoup

            //step 4
            Document document = Jsoup.parse(wikipediaResponseHTML, "https://en.wikipedia.org");

            Elements childElements = document.body().select(".mw-parser-output > *"); // for getting child nodes

            int state=0;
            //automator
            for (Element childElement : childElements)
            {
                if(state==0)
                {
                    if(childElement.tagName().equals("table")){
                        state=1;
                    }
                }
                else if(state==1){
                    if(childElement.tagName().equals("p")){
                        state=2;
                        response=childElement.text();
                        break;
                    }
                }
            }

            try{
                imageUrl=document.body().select(".infobox img").get(0).attr("src");
            }
            catch(Exception ex)
            {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        result=response;
        WikiResult wikiResult=new WikiResult(this.keyword,response,imageUrl);
        // push result into database
        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        String json=gson.toJson(wikiResult);
        System.out.println(json);
    }

    private String getWikipediaUrlForQuery(String cleanKeyword) {
        return "https://en.wikipedia.org/wiki/"+cleanKeyword;
    }

    public static void main(String[] args) {
        //multithreaded wikipedia downloader
        TaskManager taskManager=new TaskManager(20);
        String arr[]={"India","United States"};
        System.out.println("this side is aman singh gautam "+new Date().toString());
        for(String keyword:arr) {
            WikipediaDownloader wikipediaDownloader = new WikipediaDownloader(keyword);
            taskManager.waitTillQueueIsFreeAndAddTask(wikipediaDownloader);
        }
    }
}
