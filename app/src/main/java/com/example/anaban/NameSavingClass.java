package com.example.anaban;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import static com.example.anaban.MainActivity.TAG;

public class NameSavingClass {
    private SharedPreferences nameSharedPreferences;
    private SharedPreferences.Editor editor;

    public NameSavingClass(Context context) {
        nameSharedPreferences = context.getSharedPreferences("name", Context.MODE_PRIVATE);
        editor = nameSharedPreferences.edit();
    }

    public void insertNameData(int buttonNo, String name) {
        if (editor == null)
            return ;

        editor.putString("name_" + buttonNo, name);
        editor.commit();
    }

    public boolean queryNameDataHasValue() {
        if (nameSharedPreferences == null)
            return false;

        if (nameSharedPreferences.getString("name_0", null) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void printAllData() {
        if (nameSharedPreferences == null)
            return;

        Map<String, String> map = (Map<String, String>) nameSharedPreferences.getAll();
        Set<Map.Entry<String, String>> set = map.entrySet();
        for (Map.Entry<String, String> me : set) {
            Log.d(TAG, "printAllData: " + me.getKey());
            Log.d(TAG, "printAllData: " + me.getValue());
        }
    }

    public String queryNameData(int buttonNo) {
        if (nameSharedPreferences == null)
            return null;

        return nameSharedPreferences.getString("name_" + buttonNo, null);
    }

    public void deleteAllNameData() {
        if (editor == null)
            return ;
        editor.clear();
        editor.commit();
    }

    public int getNameDataLength() {
        if (nameSharedPreferences == null)
            return 0;
        return nameSharedPreferences.getAll().size();
    }
}
