package com.gmail.guushamm.plebbit;

/**
 * Created by guushamm on 07/04/16.
 */
public class RedditApi {
	private final String URL_TEMPLATE_AFTER = "http://www.reddit.com/r/SUBREDDIT_NAME/.json_?after=AFTER";
	private final String URL_TEMPLATE = "http://www.reddit.com/r/SUBREDDIT_NAME/.json";
	private String after = "";
	private static RedditApi instance;

	public static RedditApi getInstance(){
		if (instance == null){
			instance = new RedditApi();
		}
		return instance;
	}


	private RedditApi() {
	}

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	public String generateURL(String subreddit){
		if (after.isEmpty()){
			return URL_TEMPLATE.replace("SUBREDDIT_NAME",subreddit);
		}else {
			return URL_TEMPLATE_AFTER.replace("SUBREDDIT_NAME",subreddit).replace("AFTER",after);
		}

	}
}
