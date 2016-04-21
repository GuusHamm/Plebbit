package com.gmail.guushamm.plebbit.model;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guushamm on 07/04/16.
 */
public class Post implements Serializable {

	String subreddit;
	String title;
	String author;
	int points;
	int numComments;
	String permalink;
	String url;
	String domain;
	String id;

	//Saving test stuff
	String pathToFile;

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

		this.pathToFile = "";
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

	public String getPathToFile() {
		return pathToFile;
	}
	//endregion


	public void setPathToFile(String pathToFile) {
		this.pathToFile = pathToFile;
	}

	public void save(Context context, File f) {
		List<Post> posts = read(f);
		if (posts == null) {
			posts = new ArrayList<Post>();
		}

		posts.add(this);

		ObjectOutput out;
		try {
			f.createNewFile();
			out = new ObjectOutputStream(new FileOutputStream(f));
			out.writeObject(posts);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Post> read(File f) {
		ObjectInput in;
		List<Post> posts = null;
		try {
			in = new ObjectInputStream(new FileInputStream(f));
			posts = (List<Post>) in.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return posts;
	}

	public Bitmap getSavedBitmap(String path) {
		try {
			File f = new File(path, id);
			Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
			return b;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}
