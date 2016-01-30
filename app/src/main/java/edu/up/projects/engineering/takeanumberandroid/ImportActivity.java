package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ImportActivity extends AppCompatActivity {
    String sessionID;
    int[] layoutParams = new int[4];
    private String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        Button setupB = (Button) findViewById(R.id.setupButton);
        Button queueB = (Button) findViewById(R.id.queueButton);
        Button checkpointsB = (Button) findViewById(R.id.checkpointsButton);
        EditText rosterPreview = (EditText) findViewById(R.id.nameList);
        Button createButton = (Button) findViewById(R.id.createButton);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        Spinner selectCSV= (Spinner) findViewById(R.id.selectCSV);

        setupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(ImportActivity.this,
                        MainActivity.class);
                ImportActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        queueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(ImportActivity.this,
                        QueueActivity2.class);
                ImportActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        checkpointsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(ImportActivity.this,
                        CheckpointsActivity.class);
                ImportActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        sessionID = getIntent().getExtras().getString("session");
        layoutParams = getIntent().getExtras().getIntArray("layout");


        if(sessionID != null){
            //handler if they input a sessionID
            System.out.println("Session iD:" + sessionID);
        }
        else if(layoutParams != null){
            //handler if they input layout parameters
            //0 = left row, 1 = right row, 2 = left cols, 3 = right cols
            System.out.println("Here: " + layoutParams[0] + " and " + layoutParams[1]);
        }
        else{
            //handler for if they chose a preset lab session

        }

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(ImportActivity.this,
                        QueueActivity2.class);
                intentMain.putExtra("layout", layoutParams);
                intentMain.putExtra("roster", content);
                ImportActivity.this.startActivity(intentMain);
            }
        });
        List<File> spinnerArray =  new ArrayList<File>();
        File csvFolder = new File("/sdcard/TAN");
        File[] csvList = csvFolder.listFiles();

        ArrayList<String> fileNames = new ArrayList<String>();
        for(int i = 0; i<csvList.length; i++){
            fileNames.add(i, csvList[i].getName());
        }
        for(String x:fileNames){
            System.out.println(x);
        }

        ArrayAdapter<File> adapter = new ArrayAdapter<File>(
                this, android.R.layout.simple_spinner_item, csvList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.selectCSV);
        sItems.setAdapter(adapter);
        String selected = sItems.getSelectedItem().toString();
        if (selected.equals("what ever the option was")) {
        }
        File selected2 = (File) selectCSV.getSelectedItem();
        content = "AYY";
        try {
            content = readFile(selected2.toString());
        }
        catch (Exception e){

        }
        System.out.println(content);
        rosterPreview.setText(content);






    }
    private String readFile( String file ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader(file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while( ( line = reader.readLine() ) != null ) {
                stringBuilder.append( line );
                stringBuilder.append( ls );
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }


}
