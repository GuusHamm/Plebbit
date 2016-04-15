package com.gmail.guushamm.plebbit;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.gmail.guushamm.plebbit.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FeedActivity extends AppCompatActivity {

	private List<Post> posts;
	private RecyclerView recyclerView;
	private LinearLayoutManager llm;
	private RVAdapter adapter;
	private String subreddit;
	private String sorting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

		posts = new ArrayList<>();

		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		// Get a support ActionBar corresponding to this toolbar
		ActionBar ab = getSupportActionBar();

		View view = getLayoutInflater().inflate(R.layout.actionbar_spinner, null);
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.subreddit_array, android.R.layout.simple_spinner_item);
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
					posts = new RedditSubredditApi().execute(subreddit).get();
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
		}

		return super.onOptionsItemSelected(item);
	}

	public void loadMorePosts(int startPosition) {
		try {
			ArrayList<Post> newPosts = (ArrayList<Post>) new RedditSubredditApi().execute(this.subreddit).get();
			posts.addAll(newPosts);
			adapter.notifyItemRangeInserted(startPosition, newPosts.size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
