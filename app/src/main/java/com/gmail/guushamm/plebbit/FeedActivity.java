package com.gmail.guushamm.plebbit;


import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.gmail.guushamm.plebbit.model.Post;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class FeedActivity extends AppCompatActivity {

	private static String CLIENT_ID = "uQyFemjU-uH93A";
	private static String CLIENT_SECRET ="";
	private static String REDIRECT_URI="http://localhost/redirect";
	private static String GRANT_TYPE="https://oauth.reddit.com/grants/installed_client";
	private static String GRANT_TYPE2="authorization_code";
	private static String TOKEN_URL ="access_token";
	private static String OAUTH_URL ="https://www.reddit.com/api/v1/authorize";
	private static String OAUTH_SCOPE="vote";
	private static String DURATION = "permanent";

	private List<Post> posts;
	private RecyclerView recyclerView;
	private LinearLayoutManager llm;
	private RVAdapter adapter;
	private String subreddit;
	private String sorting;
	private Dialog auth_dialog;
	private Intent resultIntent;

	private String DEVICE_ID = UUID.randomUUID().toString();
	private String authCode;
	private SharedPreferences pref;
	private boolean authComplete = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

		posts = new ArrayList<>();

		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);


		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setHomeAsUpIndicator(R.drawable.ic_sentiment_very_satisfied_white_24dp);
		actionBar.setDisplayShowHomeEnabled(false);

		// Get a support ActionBar corresponding to this toolbar
		ActionBar ab = getSupportActionBar();

		View view = getLayoutInflater().inflate(R.layout.actionbar_spinner, null);
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.subreddit_array, R.layout.spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				try {
					subreddit = String.valueOf(adapter.getItem(position));
					//TODO use selected type
					if (subreddit.matches("MyItems")) {
						File outFile = new File(getFilesDir(), "plebbitSaveData.data");
						posts = Post.read(outFile);
					} else {
						posts = new RedditSubredditApi().execute(RedditApi.getInstance().generateURL(subreddit, "top")).get();
					}
					setRecycleView();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		ab.setCustomView(view);
		ab.setDisplayShowCustomEnabled(true);
		// Enable the Up button
		ab.setDisplayHomeAsUpEnabled(true);


		setRecycleView();

		//Notification test
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_text_format_black_24dp)
				.setContentTitle("Notification from me")
				.setContentText("Small text here");

		mBuilder.setVibrate(new long[] {50, 50, 50, 50, 50, 50, 50});
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(alarmSound);

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(001, mBuilder.build());


	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch (keycode) {
			case KeyEvent.KEYCODE_MENU:
				menuButtonAction();
				return true;
		}

		return super.onKeyDown(keycode, e);
	}

	public void menuButtonAction() {
		Toast.makeText(this, "Menu test", Toast.LENGTH_SHORT).show();
		Spinner spinner = (Spinner) findViewById(R.id.spinner);

		spinner.setDropDownVerticalOffset(200);
		spinner.setDropDownHorizontalOffset(-500);
		spinner.performClick();

		spinner.setDropDownVerticalOffset(-200);
	}

	public void setRecycleView() {
		recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		llm = new LinearLayoutManager(this);

		recyclerView.setLayoutManager(llm);

		adapter = new RVAdapter(posts, getWindowManager().getDefaultDisplay(), getApplicationContext());
		recyclerView.setAdapter(adapter);

			recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
				@Override
				public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
					super.onScrolled(recyclerView, dx, dy);

					LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

					int visibleItemCount = recyclerView.getChildCount();
					int totalItemCount = llm.getItemCount();
					int firstVisibleItemIndex = llm.findFirstVisibleItemPosition();
					int lastVisibleItemIndex = llm.findLastVisibleItemPosition();

					if (totalItemCount - lastVisibleItemIndex <= 5) {
						//Load more posts here
						loadMorePosts(totalItemCount);
					}
				}

				@Override
				public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
					super.onScrollStateChanged(recyclerView, newState);
				}
			});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_feed, menu);
		return true;
	}

	public boolean subredditSelectorSwitch(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
//		menuInflater.inflate(R.);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		switch (id) {
			case R.id.action_settings:
				return true;
			case R.id.action_login:
				pref = getSharedPreferences("AppPref", MODE_PRIVATE);
				auth_dialog = new Dialog(FeedActivity.this);
				auth_dialog.setContentView(R.layout.auth_dialog);
				WebView web = (WebView) auth_dialog.findViewById(R.id.webv);
				web.getSettings().setJavaScriptEnabled(true);
				String url = OAUTH_URL + "?client_id=" + CLIENT_ID + "&response_type=code&state=TEST&redirect_uri=" + REDIRECT_URI + "&scope=" + OAUTH_SCOPE;
				web.loadUrl(url);
				Toast.makeText(getApplicationContext(), "" + url, Toast.LENGTH_LONG).show();

				web.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						view.loadUrl(url);
						return true;
					}
					@Override
					public void onPageStarted(WebView view, String url, Bitmap favicon) {
						super.onPageStarted(view, url, favicon);

					}
					@Override
					public void onPageFinished(WebView view, String url) {
						super.onPageFinished(view, url);

						if (url.contains("?code=") || url.contains("&code=")) {

							Uri uri = Uri.parse(url);
							authCode = uri.getQueryParameter("code");
							Log.i("", "CODE : " + authCode);
							authComplete = true;
							resultIntent = new Intent();
							resultIntent.putExtra("code", authCode);
							FeedActivity.this.setResult(Activity.RESULT_OK, resultIntent);
							setResult(Activity.RESULT_CANCELED, resultIntent);
							SharedPreferences.Editor edit = pref.edit();
							edit.putString("Code", authCode);
							edit.commit();
							auth_dialog.dismiss();
							Toast.makeText(getApplicationContext(), "Authorization Code is: " + pref.getString("Code", ""), Toast.LENGTH_SHORT).show();

							try {
								new RedditRestApi(getApplicationContext()).getToken(TOKEN_URL, GRANT_TYPE2, DEVICE_ID);
								Toast.makeText(getApplicationContext(), "Auccess Token: " + pref.getString("token", ""), Toast.LENGTH_SHORT).show();
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else if (url.contains("error=access_denied")) {
							Log.i("", "ACCESS_DENIED_HERE");
							resultIntent.putExtra("code", authCode);
							authComplete = true;
							setResult(Activity.RESULT_CANCELED, resultIntent);
							Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();

							auth_dialog.dismiss();
						}
					}
				});
				auth_dialog.show();
				auth_dialog.setTitle("Authorize");
				auth_dialog.setCancelable(true);
			case R.id.menuSortHot:
				try {
					RedditApi.getInstance().setAfter("");
					posts = new RedditSubredditApi().execute(RedditApi.getInstance().generateURL(subreddit, "hot")).get();
					setRecycleView();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				return true;
			case R.id.menuSortRising:
				try {
					RedditApi.getInstance().setAfter("");
					posts = new RedditSubredditApi().execute(RedditApi.getInstance().generateURL(subreddit, "rising")).get();
					setRecycleView();
				} catch (InterruptedException e) {e.printStackTrace();

				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				return true;
			case R.id.menuSortTop:
				try {
					RedditApi.getInstance().setAfter("");
					posts = new RedditSubredditApi().execute(RedditApi.getInstance().generateURL(subreddit, "top")).get();
					setRecycleView();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void loadMorePosts(int startPosition) {
		if (subreddit.matches("MyItems")) {
			return;
		}
		try {
//			ArrayList<Post> newPosts = (ArrayList<Post>) new RedditSubredditApi().execute(this.subreddit).get();
			ArrayList<Post> newPosts = (ArrayList<Post>) new RedditSubredditApi().execute(RedditApi.getInstance().generateURL(subreddit, "Current")).get();
			posts.addAll(newPosts);
			adapter.notifyItemRangeInserted(startPosition, newPosts.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
