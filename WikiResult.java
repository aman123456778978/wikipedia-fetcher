package tech.codingclub.utility;

public class WikiResult {
    private String keyword;
    private String response;
    private String imageUrl;
    public WikiResult(String keyword, String response, String imageUrl) {
        this.keyword=keyword;
        this.response=response;
        this.imageUrl=imageUrl;
    }
}
