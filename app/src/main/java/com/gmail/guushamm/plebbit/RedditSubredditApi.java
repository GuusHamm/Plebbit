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
 * Created by GuusHamm on 8-4-2016.
 */
public class RedditSubredditApi extends AsyncTask<String, Integer, List<Post>> {
	@Override
	protected List<Post> doInBackground(String... params) {
			String raw = RemoteData.readContents(RedditApi.getInstance().generateURL(params[0]));
			List<Post> posts = new ArrayList<>();

			try {
				JSONObject data = new JSONObject(raw).getJSONObject("data");
				JSONArray children = data.getJSONArray("children");

				RedditApi.getInstance().setAfter(data.getString("after"));

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
}
