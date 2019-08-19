package com.example.anaban;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import static com.example.anaban.MainActivity.TAG;

public class PositionSavingClass {
    private SharedPreferences positionSharedPreferences;
    private SharedPreferences.Editor editor;

    public PositionSavingClass(Context context) {
        positionSharedPreferences = context.getSharedPreferences("position", Context.MODE_PRIVATE);
        editor = positionSharedPreferences.edit();
    }

    public void insertPositionData(int buttonNo, int left, int top) {
        if (editor == null)
            return ;

        String value = left + "_" + top;
        editor.putString("position_" + buttonNo, value);
        editor.commit();
    }

    public boolean queryPositionDataHasValue() {
        if (positionSharedPreferences == null)
            return false;

        if (positionSharedPreferences.getString("position_0", null) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void printAllData() {
        if (positionSharedPreferences == null)
            return;

        Map<String, String> map = (Map<String, String>) positionSharedPreferences.getAll();
        Set<Map.Entry<String, String>> set = map.entrySet();
        for (Map.Entry<String, String> me : set) {
            Log.d(TAG, "printAllData: " + me.getKey());
            Log.d(TAG, "printAllData: " + me.getValue());
        }
    }

    /*
     * int[2] = {left, top}
     */
    public int[] queryPositionData(int buttonNo) {
        if (positionSharedPreferences == null)
            return null;

        String s = positionSharedPreferences.getString("position_" + buttonNo, null);

        if (s == null)
            return null;

        if (s.length() > 0) {
            int position[] = new int[2];
            int i = 0;
            for (String splitS : s.split("_")) {
                position[i++] = Integer.parseInt(splitS);
                if (i > 2) break;
            }
//            Log.d(TAG, "queryPositionData: " + buttonNo + " l:" + position[0] + " t:" + position[1]);

            return position;
        } else {
            return null;
        }
    }

    public void deleteAllPositionData() {
        if (editor == null)
            return ;
//        editor.remove("position");
        editor.clear();
        editor.commit();
    }

    public int getPositionDataLength() {
        if (positionSharedPreferences == null)
            return 0;
        return positionSharedPreferences.getAll().size();
    }
}
