package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "FinalProjectApp";
    public static Context context;
    public static ArrayList<Integer> tableArray;
    private String mURL = "https://lgv73lq1y1.execute-api.ap-northeast-2.amazonaws.com/prod";

    ArrayAdapter<CharSequence> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        final TextView section = findViewById(R.id.section);

        final SwipeRefreshLayout refreshlayout = findViewById(R.id.refresh_layout);
        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String urlstr = mURL;
                tableArray = new ArrayList<>();
                Log.i(TAG, "TableStateURL=" + mURL);
                if (urlstr == null || urlstr.equals("")) {
                    Toast.makeText(MainActivity.this, "상태조회 API URL 입력이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                new GetState(MainActivity.this, mURL).execute();
                new GetTable1(MainActivity.this, mURL).execute();
                new GetTable2(MainActivity.this, mURL).execute();

                refreshlayout.setRefreshing(false);

                //Intent intent = new Intent(MainActivity.this, StateActivity.class);
                //intent.putExtra("TableStateURL", mURL);
                //startActivity(intent);
            }
        });

        final Spinner spinner = findViewById(R.id.bottomspiner);
        arrayAdapter = ArrayAdapter.createFromResource(this,R.array.planets_array,
                android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),String.valueOf(parent.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
                section.setText(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}