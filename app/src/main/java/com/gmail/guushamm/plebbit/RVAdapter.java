package com.gmail.guushamm.plebbit;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gmail.guushamm.plebbit.model.Post;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * Created by Nekkyou on 7-4-2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PostViewHolder> {

    private List<Post> posts;
    private Display display;
    private RedditImageApi redditImageApi;
	private Context context;

    public RVAdapter(List<Post> posts, Display display, Context context) {
        this.posts = posts;
        this.display = display;
		this.context = context;
        redditImageApi = new RedditImageApi();
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post_card, viewGroup, false);
        PostViewHolder pvh = new PostViewHolder(v);
        return pvh;
    }

	@Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {
        holder.title.setText(posts.get(position).getTitle());
        holder.score.setText(String.valueOf(posts.get(position).getPoints()));
        Bitmap bitmap = null;

        Post post = posts.get(position);

        if (post.getSubreddit().matches("tifu")) {
            Picasso.with(context).load(R.drawable.ic_text_format_black_24dp).into(holder.image);
             holder.image.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(posts.get(position).getUrl()));
                     browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     context.startActivity(browserIntent);
                 }
             });
        }
        else if (post.getSubreddit().matches("MyItems")) {
            Picasso.with(context).load(new File(post.getPathToFile(), post.getId())).into(holder.image);

            holder.saveButton.setEnabled(false);
            holder.saveButton.setVisibility(View.INVISIBLE);
        }
        else {
            //For normal posts
            String isGif = posts.get(position).getTitle();
            if (isGif.endsWith(".gif") || isGif.endsWith(".gifv")) {
                Ion.with(holder.image).error(R.drawable.error).load(posts.get(position).getUrl());
            } else {
				String url = posts.get(position).getUrl();

				if (url.contains("imgur") && (!url.endsWith(".png") && !url.endsWith(".jpg") && !url.endsWith(".jpeg"))){
					url += ".png";
				}
				Picasso.with(context).load(url).centerInside().fit().into(holder.image);

            }

            holder.saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post p = posts.get(position);
                    Bitmap b = null;
                    try {
                        b = BitmapFactory.decodeStream((InputStream)new URL(p.getUrl()).getContent());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    savePost(p, b);
                }
            });
        }

		holder.imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String permalink = String.format("http://reddit.com%s",posts.get(position).getPermalink());
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(permalink));
				browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(browserIntent);
			}
		});

        holder.upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(context,"Upvoted",Toast.LENGTH_SHORT);
                toast.show();
//                RedditRestApi redditRestApi = new RedditRestApi(context);
//                redditRestApi.castVote(posts.get(position).getId(),1);
            }
        });


        holder.downvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(context,"Downvoted",Toast.LENGTH_SHORT);
                toast.show();
//                RedditRestApi redditRestApi = new RedditRestApi(context);
//                redditRestApi.castVote(posts.get(position).getId(),-1);
            }
        });
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float scWidth = outMetrics.widthPixels;
        holder.image.getLayoutParams().width = (int) scWidth;

    }

    public void savePost(Post p, Bitmap bitmap) {
        p.setPathToFile(saveImageToPostID(bitmap, p.getId()));
        File outFile = new File(context.getFilesDir(), "plebbitSaveData.data");
        p.save(context, outFile);
    }

    public String saveImageToPostID(Bitmap bitmap, String fileName) {
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return directory.getAbsolutePath();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView score;
		ImageButton imageButton;
        ImageButton upvoteButton;
        ImageButton downvoteButton;
        ImageView image;
        ImageButton saveButton;

        PostViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.text);
            score = (TextView) itemView.findViewById(R.id.score);
			imageButton = (ImageButton) itemView.findViewById(R.id.commentsButton);
			upvoteButton = (ImageButton) itemView.findViewById(R.id.upVoteButton);
			downvoteButton = (ImageButton) itemView.findViewById(R.id.downVoteButton);
            image = (ImageView) itemView.findViewById(R.id.image);
            saveButton = (ImageButton) itemView.findViewById(R.id.saveButton);
        }
    }
}