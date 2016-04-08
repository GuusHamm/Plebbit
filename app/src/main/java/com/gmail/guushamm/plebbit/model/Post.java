package com.gmail.guushamm.plebbit.model;

/**
 * Created by guushamm on 07/04/16.
 */
public class Post {

	String subreddit;
	String title;
	String author;
	int points;
	int numComments;
	String permalink;
	String url;
	String domain;
	String id;

	public Post(String subreddit, String title, String author, int points, int numComments, String permalink, String url, String domain, String id) {
		this.subreddit = subreddit;
		this.title = title;
		this.author = author;
		this.points = points;
		this.numComments = numComments;
		this.permalink = permalink;
		this.url = url;
		this.domain = domain;
		this.id = id;
	}
	//region Getters
	public String getTitle() {
		return title;
	}

	public String getSubreddit() {
		return subreddit;
	}

	public String getAuthor() {
		return author;
	}

	public int getPoints() {
		return points;
	}

	public int getNumComments() {
		return numComments;
	}

	public String getPermalink() {
		return permalink;
	}

	public String getUrl() {
		return url;
	}

	public String getDomain() {
		return domain;
	}

	public String getId() {
		return id;
	}
	//endregion
}