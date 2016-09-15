package com.limwoon.musicwriter.parse;

import android.util.Log;

import com.limwoon.musicwriter.data.NoteData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

/**
 * Created by ejdej on 2016-08-02.
 */

// 노트를 JSON 에서 파싱하는 클래스

public class NoteParser{

    String data;
    JSONArray jsonArray;
    JSONObject jsonObject;
    public NoteParser(String data){
        this.data = data;
        init();
    }

    public String getData2() {
        return data;
    }

    public void setData2(String data) {
        this.data = data;
        try {
            this.jsonArray = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        try {
            jsonObject = new JSONObject(data);
            jsonArray = new JSONArray(jsonObject.getString("note"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getID(){
        try {
            return jsonObject.getInt("beats");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getBeats(){
        try {
            return jsonObject.getInt("beats");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getTitle(){
        try {
            return jsonObject.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAuthor(){
        try {
            return jsonObject.getString("author");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNotes(){
        try {
            return jsonObject.getString("note");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getNoteLength(){
        return jsonArray.length();
    }

    public NoteData getNoteAt(int pos){
        try {
            JSONObject noteJSONObject = jsonArray.getJSONObject(pos);
            NoteData noteData = new NoteData();

            noteData.node= noteJSONObject.getBoolean("isNode");
            noteData.rest=noteJSONObject.getBoolean("isRest");
            noteData.duration=noteJSONObject.getInt("duration");
            for(int i=0; i<6; i++){
                noteData.tone[i]=noteJSONObject.getJSONObject("tone").getInt("tone"+i);
            }

            return noteData;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getAllData(){
        return jsonObject.toString();
    }
}
