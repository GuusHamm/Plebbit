package com.gmail.guushamm.plebbit;

/**
 * Created by guushamm on 07/04/16.
 */
public class RedditApi {
	private final String URL_TEMPLATE_AFTER = "http://www.reddit.com/r/SUBREDDIT_NAME/.json?after=AFTER&limit=10";
//	private final String URL_TEMPLATE_AFTER = "http://www.reddit.com/r/SUBREDDIT_NAME/.json?after=AFTER&limit=10";
	private final String URL_TEMPLATE = "http://www.reddit.com/r/SUBREDDIT_NAME/.json";
	private final String URL_TEMPLATE_SORTING = "http://www.reddit.com/r/SUBREDDIT_NAME/SORT/.json";
	private String after = "";
	private String subreddit = "";
	private String sorting = "";
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

	public String generateURL(String subreddit,String sorting){
		String oldSubReddit = this.subreddit;
		String oldSorting = this.subreddit;

		this.subreddit = subreddit;
		this.sorting = sorting;

		if (subreddit.equals(oldSubReddit) && sorting.equals(oldSorting)){
			if (after.isEmpty()){
				return URL_TEMPLATE.replace("SUBREDDIT_NAME",subreddit);
			}else {
				String result = URL_TEMPLATE_AFTER.replace("SUBREDDIT_NAME",subreddit);
				return result.replace("AFTER",after);
			}
		}else if(sorting.isEmpty()){
			return URL_TEMPLATE.replace("SUBREDDIT_NAME",subreddit);
		}else {
			String result = URL_TEMPLATE.replace("SUBREDDIT_NAME",subreddit);
			return result.replace("SORTTYPE",sorting);
		}
	}
}
