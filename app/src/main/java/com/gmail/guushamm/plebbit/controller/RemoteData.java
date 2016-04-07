package com.gmail.guushamm.plebbit.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by guushamm on 07/04/16.
 */
public class RemoteData {
	public static HttpURLConnection getConnection(String url){
		HttpURLConnection httpURLConnection = null;

		try {
			httpURLConnection = (HttpURLConnection) new URL (url).openConnection();
			httpURLConnection.setReadTimeout(30000);
			httpURLConnection.setRequestProperty("User-Agent","Plebbit V1.0");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return httpURLConnection;
	}

	public static String readContents(String url){
		HttpURLConnection httpURLConnection = getConnection(url);
		if (httpURLConnection == null){
			return null;
		}

		StringBuffer stringBuffer = new StringBuffer(8192);
		String message;
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			while ((message = bufferedReader.readLine()) != null){
				stringBuffer.append(message).append("\n");
			}
			return stringBuffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
