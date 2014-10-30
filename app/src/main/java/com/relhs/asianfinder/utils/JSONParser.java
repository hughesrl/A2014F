package com.relhs.asianfinder.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
public class JSONParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    private JSONObject jObjAssets;
  // constructor
    public JSONParser() { }

    public JSONObject getJSONFromUrl(String url, List<NameValuePair> parameters) {
        // Making HTTP request
        try {
          // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            if(parameters != null) {
                httpPost.setEntity(new UrlEncodedFormEntity(parameters));
            }
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        } catch (ClientProtocolException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          BufferedReader reader = new BufferedReader(new InputStreamReader(
              is, "iso-8859-1"), 8);
          StringBuilder sb = new StringBuilder();
          String line = null;
          while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
          }
          is.close();
          json = sb.toString();
        } catch (Exception e) {
          Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
        try {
          jObj = new JSONObject(json);
        } catch (JSONException e) {
          Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // return JSON String
        return jObj;
    }
  public JSONObject loadJSONFromAsset(Activity activity, String fileName) {
	    String json = null;
	    try {
	        InputStream is = activity.getAssets().open(fileName);
	        int size = is.available();
	        byte[] buffer = new byte[size];
	        is.read(buffer);
	        is.close();
	        json = new String(buffer, "UTF-8");
	        
	        jObjAssets = new JSONObject(json);
	    } catch (IOException ex) {
	        ex.printStackTrace();
	        return null;
	    } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return jObjAssets;

	}

}