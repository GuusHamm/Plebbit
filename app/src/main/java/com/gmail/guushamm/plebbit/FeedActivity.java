package com.gmail.guushamm.plebbit;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.gmail.guushamm.plebbit.model.Post;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FeedActivity extends AppCompatActivity {

	private List<Post> posts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		posts = null;

		try {
			posts = new RedditApi().execute("meirl").get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		if (posts.size() > 0 ){
			Toast toast = Toast.makeText(getApplicationContext(),posts.get(0).getTitle(),Toast.LENGTH_LONG);
			toast.show();
		}
		setRecycleView();

	}

	public void setRecycleView() {
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(llm);

		RVAdapter adapter = new RVAdapter(posts, getWindowManager().getDefaultDisplay());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_feed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
