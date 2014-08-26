package com.dpcat237.nps.helper;

import android.content.Context;
import android.util.Log;

import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.common.model.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WearHelper {
    private static final String TAG = "NPS:WearHelper";


	public static String preparePlayerData(Context context, Song song) {
        JSONObject json = new JSONObject();
        try {
            json.put("song", getSongData(song));
            json.put("labels", getLabels(context));
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
        }

        return json.toString();
    }

    public static String prepareSongData(Song song) {
        JSONObject json = new JSONObject();
        try {
            json.put("song", getSongData(song));
            json.put("labels", "");
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
        }

        return json.toString();
    }

    private static String getSongData(Song song) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", song.getId());
            json.put("item_id", song.getItemApiId());
            json.put("list_title", song.getListTitle());
            json.put("title", song.getTitle());
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);
        }

        return json.toString();
    }

    private static String getLabels(Context context) {
        LabelRepository labelRepo = new LabelRepository(context);
        labelRepo.open();
        JSONArray labels = labelRepo.getLabelsToSync();
        labelRepo.close();

        return labels.toString();
    }
}
