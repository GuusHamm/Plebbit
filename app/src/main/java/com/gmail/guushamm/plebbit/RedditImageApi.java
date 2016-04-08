package com.gmail.guushamm.plebbit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by guushamm on 07/04/16.
 */
public class RedditImageApi extends AsyncTask<String,Integer,Bitmap>{

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream((InputStream)new URL(params[0]).getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
}
