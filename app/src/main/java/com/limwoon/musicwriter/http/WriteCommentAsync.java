package com.limwoon.musicwriter.http;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.limwoon.musicwriter.data.CommentData;
import com.limwoon.musicwriter.data.PUBLIC_APP_DATA;
import com.limwoon.musicwriter.list.CommentRecyclerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ejdej on 2016-10-11.
 */

public class WriteCommentAsync extends AsyncTask<Bundle, Void, Integer> {

    ArrayList<CommentData> list;
    CommentRecyclerAdapter adapter;
    TextView textView;
    Bundle bundle;

    public WriteCommentAsync(ArrayList<CommentData> list, CommentRecyclerAdapter adapter, TextView textView) {
        this.list = list;
        this.adapter = adapter;
        this.textView = textView;
    }

    public WriteCommentAsync(ArrayList<CommentData> list) {
        this.list = list;
    }

    @Override
    protected Integer doInBackground(Bundle... bundles) {
        bundle = bundles[0];
        String comment = bundle.getString("comment");
        long userID = bundle.getLong("userID");
        long sheetID = bundle.getLong("sheetID");
        int result=11;

        String message = "comment=" + comment + "&userID=" + userID + "&sheetID=" + sheetID + "&userStrID=" + PUBLIC_APP_DATA.getUserStrID();
        try {
            URL url = new URL(PUBLIC_APP_DATA.getServerUrl() + "writecomment.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = connection.getOutputStream();
            os.write(message.getBytes());
            os.flush();
            os.close();

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            while (true) {
                String line = reader.readLine();
                Log.d("comment write", "doInBackground: " + line);
                if (line == null) break;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        list.clear();
        new LoadComments(list, adapter, textView, textView.getContext()).execute(bundle.getLong("sheetID"), (long)0);
        super.onPostExecute(integer);
    }
}
