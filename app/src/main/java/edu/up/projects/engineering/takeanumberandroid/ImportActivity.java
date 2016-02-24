package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// NICK TESTING SOCKET STUFF
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

public class ImportActivity extends AppCompatActivity {
    String sessionID;
    int[] layoutParams = new int[4];
    private String content = "";
    EditText rosterPreview;
    // NICK
    EditText textOut;
    TextView textIn;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // NICK
        textOut = (EditText)findViewById(R.id.textout);
        Button buttonSend = (Button)findViewById(R.id.send);


        buttonSend.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SendfeedbackJob job = new SendfeedbackJob();
                textIn = (TextView)findViewById(R.id.textin);
                job.execute(textIn.getText().toString());
            }
        });


        Button setupB = (Button) findViewById(R.id.setupButton);
        Button queueB = (Button) findViewById(R.id.queueButton);
        Button checkpointsB = (Button) findViewById(R.id.checkpointsButton);
        rosterPreview = (EditText) findViewById(R.id.nameList);
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


        try{
            sessionID = getIntent().getExtras().getString("session");
        }
        catch(NullPointerException notLoadingSession){
            //TODO generate session ID as though they didn't put in a session id (cause they didn't)
        }

        try{
            layoutParams = getIntent().getExtras().getIntArray("layout");
        }
        catch(NullPointerException notLoadingSession){
            //layout params need to come from server in this case
            //TODO get a session id from server in this case - this case should only happen if they selected "load lab session"
        }



        if(sessionID != null){
            //handler if they input a sessionID
        }
        else if(layoutParams != null){
            //handler if they input layout parameters
            //0 = left row, 1 = right row, 2 = left cols, 3 = right cols
        }
        else{
            //handler for if they chose a preset lab session

        }

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(ImportActivity.this,
                        CheckpointsActivity.class);
                intentMain.putExtra("layout", layoutParams);
                intentMain.putExtra("roster", rosterPreview.getText().toString());
                EditText numChecks = (EditText) findViewById(R.id.numChecks);

                final int numberOfCheckpoints = Integer.parseInt(numChecks.getText().toString());
                intentMain.putExtra("numChecks", numberOfCheckpoints);
                ImportActivity.this.startActivity(intentMain);
            }
        });
        //the list of all files in the project folder
        List<File> spinnerArray =  new ArrayList<File>();
        //decided to make the specified folder "Tan" and it should be in the root of the device
        File csvFolder = new File("/sdcard/TAN");
        File[] csvList = csvFolder.listFiles();

        if(csvList != null){
            ArrayAdapter<File> adapter = new ArrayAdapter<File>(
                    this, android.R.layout.simple_spinner_item, csvList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            final Spinner sItems = (Spinner) findViewById(R.id.selectCSV);
            sItems.setAdapter(adapter);
            sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    File xc = (File) sItems.getSelectedItem();
                    String cont = "";
                    try{
                        cont = readFile(xc.toString());
                    }
                    catch (IOException noFile){

                    }
                    rosterPreview.setText(cont);


                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            String selected = sItems.getSelectedItem().toString();
            if (selected.equals("what ever the option was")) {
            }
            File selected2 = (File) selectCSV.getSelectedItem();
            try {
                content = readFile(selected2.toString());
            }
            catch (Exception e){

            }
            rosterPreview.setText(content);
        }
        else{
            rosterPreview.setText("NO CSV FILES FOUND");
        }







    }

    /**
     * A helper method to read a file.
     * @param file - the file to read
     * @return - the string containing the file's contents
     * @throws IOException
     */
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

    private void setRosterPreview(String roster){
        rosterPreview.setText(roster);
    }


    // NICK
    //
    //
    private class SendfeedbackJob extends AsyncTask<String, Void, String> {
        String message2 = "";
        PrintWriter out;
        @Override
        protected String doInBackground(String[] params) {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            try {
                socket = new Socket("10.17.3.72", 8081);
                socket = new Socket("192.168.127.1", 9898);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("append");
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            try {
                while (true) {
                    String x = "";

                    x = dataInputStream.readLine();
                    System.out.println(x);


                    if (x.equals("ayy")) {
                        dataInputStream.close();
                        dataOutputStream.close();
                        socket.close();
                        out.println("");
                        System.out.println("CLOSING AHHHHHHHHH");
                        break;
                    }

                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return "some message";
        }

        @Override
        protected void onPostExecute(String message) {
            message2 = message;
        }
    }
}
