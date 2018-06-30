package com.example.ekemini.journalapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ekemini on 6/30/2018.
 */

public class EkePrefUtils {
    private final String UISTORAGE = "com.example.ekemini.journalapp.UISTORAGE";
    private SharedPreferences preferences;
    private Context context;

    public EkePrefUtils(Context context) {
        this.context = context;
    }

    public void storeSelectedPost(String postToEdit){
        preferences = context.getSharedPreferences(UISTORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("postEdit", postToEdit);
        editor.apply();
    }

    public String loadSelectedPost(){
        preferences = context.getSharedPreferences(UISTORAGE, Context.MODE_PRIVATE);
        return preferences.getString("postEdit", null);
    }

    public void storeSelectedItemPosition(int itemPos){
        preferences = context.getSharedPreferences(UISTORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("item", itemPos);
        editor.apply();
    }

    public int loadSelectedItemPosition(){
        preferences = context.getSharedPreferences(UISTORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("item", 0);
    }

    public void storeSelectedDatabaseId(String postId){
        preferences = context.getSharedPreferences(UISTORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("postDataId", postId);
        editor.apply();
    }

    public String loadSelectedDatabaseId(){
        preferences = context.getSharedPreferences(UISTORAGE, Context.MODE_PRIVATE);
        return preferences.getString("postDataId", null);
    }

}
