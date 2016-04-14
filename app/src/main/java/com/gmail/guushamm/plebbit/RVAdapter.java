package com.gmail.guushamm.plebbit;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gmail.guushamm.plebbit.model.Post;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.squareup.picasso.Picasso;

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
    public void onBindViewHolder(PostViewHolder holder, int position) {
        holder.name.setText(posts.get(position).getTitle());
        Bitmap bitmap = null;

//        if (posts.get(position).getDomain().contains("imgur")){
//			try {
//				redditImageApi = new RedditImageApi();
//				bitmap = redditImageApi.execute(posts.get(position).getTitle()).get();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} catch (ExecutionException e) {
//				e.printStackTrace();
//			}
//
//			if (bitmap != null) {
//				holder.image.setImageBitmap(bitmap);
//			} else {
//				System.out.println("Bitmap is null");
//			}
//		}

        String isGif = "";
        isGif = posts.get(position).getTitle();
        if (isGif.endsWith(".gif") || isGif.endsWith(".gifv")) {
            Ion.with(holder.image).error(R.drawable.error).load(posts.get(position).getUrl());
        } else {
            Picasso.with(context).load(posts.get(position).getUrl()).into(holder.image);
        }


        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float scWidth = outMetrics.widthPixels;
        holder.image.getLayoutParams().width = (int) scWidth;

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
        TextView name;
        ImageView image;

        PostViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cardView);
            name = (TextView) itemView.findViewById(R.id.text);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}