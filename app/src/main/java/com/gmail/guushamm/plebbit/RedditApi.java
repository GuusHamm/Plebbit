package com.gmail.guushamm.plebbit;

import android.os.AsyncTask;
import com.gmail.guushamm.plebbit.controller.RemoteData;
import com.gmail.guushamm.plebbit.model.Post;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guushamm on 07/04/16.
 */
public class RedditApi extends AsyncTask<String, Integer, List<Post>> {
	private final String URL_TEMPLATE_AFTER = "http://www.reddit.com/r/SUBREDDIT_NAME/.json_?after=AFTER";
	private final String URL_TEMPLATE = "http://www.reddit.com/r/SUBREDDIT_NAME/.json";
	private String after;


	private String generateURL(String subreddit){
		if (after.isEmpty()){
			return URL_TEMPLATE.replace("SUBREDDIT_NAME",subreddit);
		}else {
			return URL_TEMPLATE_AFTER.replace("SUBREDDIT_NAME",subreddit).replace("AFTER",after);
		}

	}

	public List<Post>getSubredditPost(String subreddit){
		String raw = RemoteData.readContents(generateURL(subreddit));
		List<Post> posts = new ArrayList<>();

		try {
			JSONObject data = new JSONObject(raw).getJSONObject("data");
			JSONArray children = data.getJSONArray("children");

			this.after = data.getString("after");

			for (int i = 0;i < children.length(); i++){
				JSONObject currentPost = children.getJSONObject(i).getJSONObject("data");
				Post post = new Post(currentPost.getString("title"),currentPost.getString("url"),currentPost.getString("author"),currentPost.getInt("score"),currentPost.getInt("num_comments"),currentPost.getString("subreddit"),currentPost.getString("permalink"),currentPost.getString("domain"),currentPost.getString("id"));
				posts.add(post);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return posts;
	}

	@Override
	protected List<Post> doInBackground(String... params) {
		return getSubredditPost(params[0]);
	}
}
