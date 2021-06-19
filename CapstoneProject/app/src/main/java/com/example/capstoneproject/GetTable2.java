package com.example.capstoneproject;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class GetTable2 extends GetRequest{
    final static String TAG = "FinalProjectApp";
    String urlStr;
    public String result = null;

    public GetTable2(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr + "/table/entrance/section2";
    }

    @Override
    protected void onPreExecute() {
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            activity.finish();
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        if (jsonString == null || jsonString.equals("")) {
            return;
        }

        result = jsonString(jsonString);

        TextView jsontext = activity.findViewById(R.id.curseat);
        jsontext.setText(result);

        ImageView seatImage = activity.findViewById(R.id.seat);
        VectorChildFinder vector = new VectorChildFinder(MainActivity.context, R.drawable.sspseat, seatImage);

        try {
            for(int i = 0; i < MainActivity.tableArray.size(); i++) {
                if(MainActivity.tableArray.get(i) != 0) {
                    VectorDrawableCompat.VFullPath path1 = vector.findPathByName("seat"+(i+1));
                    path1.setStrokeColor(Color.BLACK);
                    seatImage.invalidate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String jsonString(String jsonString) {
        String output = null;
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("body");

            final int array = jsonArray.length()-1;

            JSONObject jsonObject = (JSONObject)jsonArray.get(array);
            JSONArray jsonTable = jsonObject.getJSONArray("table");

            int tableObject, total = 0;

            for(int i = 0; i < jsonTable.length(); i++) {
                tableObject = Integer.parseInt(jsonTable.getString(i));
                MainActivity.tableArray.add(tableObject);
            }

            for(int i = 0; i < MainActivity.tableArray.size(); i++) {
                if(MainActivity.tableArray.get(i) != 0) {
                    total += 1;
                }
            }

            Tag thing = new Tag(jsonObject.getString("section"),
                    total,
                    jsonObject.getString("time"));

            output = thing.toString();

        } catch (JSONException e) {
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }

    class Tag {
        String section;
        int table;
        String timestamp;

        public Tag(String section, int table, String time) {
            this.section = section;
            this.table = table;
            timestamp = time;
        }

        public String toString() {
            return String.format("%d", table);
        }
    }
}
