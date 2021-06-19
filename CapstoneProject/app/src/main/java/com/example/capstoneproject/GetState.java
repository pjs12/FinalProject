package com.example.capstoneproject;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class GetState extends GetRequest{
    final static String TAG = "FinalProjectApp";
    String urlStr;
    final static int MAX_PEOPLE = 25;
    int current;

    public GetState(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr + "/state/entrance";
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

        String result = jsonString(jsonString);
        int parseint = Integer.parseInt(result);
        current = parseint*100/MAX_PEOPLE;

        TextView jsontext = activity.findViewById(R.id.textView1);
        jsontext.setText(current + " %");
    }

    protected String jsonString(String jsonString) {
        String output = null;
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("body");

            int array = jsonArray.length()-1;
            JSONObject jsonObject = (JSONObject)jsonArray.get(array);

            Tag thing = new Tag(jsonObject.getString("section"),
                    jsonObject.getString("table"),
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
        String person;
        String timestamp;

        public Tag(String section, String person, String time) {
            this.section = section;
            this.person = person;
            timestamp = time;
        }

        public String toString() {
            return String.format("%s", person);
        }
    }
}
