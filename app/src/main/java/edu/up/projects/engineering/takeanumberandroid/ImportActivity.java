package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ImportActivity extends AppCompatActivity
{
    String sessionID;
    int[] layoutParams = new int[4];
    private String content = "";
    private String courseId;
    private String courseSection;
    private String courseName;
    EditText rosterPreview;

    WebSocket client = null;
//    String host = "http://192.168.1.144:8080";

    private static final String TAG = "ImportActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button setupB = (Button) findViewById(R.id.setupButton);
        Button queueB = (Button) findViewById(R.id.queueButton);
        Button checkpointsB = (Button) findViewById(R.id.checkpointsButton);
        rosterPreview = (EditText) findViewById(R.id.nameList);
        Button createButton = (Button) findViewById(R.id.createButton);
        Button saveButton = (Button) findViewById(R.id.saveButton); //not implemented yet
        Spinner selectCSV = (Spinner) findViewById(R.id.selectCSV);


        setupB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(ImportActivity.this,
                        MainActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ImportActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        queueB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(ImportActivity.this,
                        QueueActivity2.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ImportActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        checkpointsB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(ImportActivity.this,
                        CheckpointsActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ImportActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });


        try
        {
            sessionID = getIntent().getExtras().getString("session");
        }
        catch (NullPointerException notLoadingSession)
        {
            //TODO generate session ID as though they didn't put in a session id (cause they didn't)
        }

        try
        {
            layoutParams = getIntent().getExtras().getIntArray("layout");
        }
        catch (NullPointerException notLoadingSession)
        {
            //layout params need to come from server in this case
            //TODO get a session id from server in this case - this case should only happen if they selected "load lab session"
        }


        if (layoutParams != null)
        {
            //handler if they input layout parameters
            //0 = left row, 1 = right row, 2 = left cols, 3 = right cols
        }
        else
        {
            //handler for if they chose a preset lab session

        }

        createButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(ImportActivity.this,
                        CheckpointsActivity.class);
                intentMain.putExtra("layout", layoutParams);
                intentMain.putExtra("roster", rosterPreview.getText().toString());
                EditText numChecks = (EditText) findViewById(R.id.numChecks);

                final int numberOfCheckpoints = Integer.parseInt(numChecks.getText().toString());
                intentMain.putExtra("numChecks", numberOfCheckpoints);

                String outMessage = "checkpointInit#";
                String checkpoints = "";

                //TODO pull from static hashtable
                for (int i = 0; i < numberOfCheckpoints; i++)
                {
                    checkpoints += ",0";
                }

                String labNumber = "01";//TODO lab number hardcoded for now
                String rosterString = content;

                outMessage += courseId + "," + courseSection + "," + labNumber + "," + courseName + "," + numberOfCheckpoints;
                String[] studentNames = rosterString.split("\\n");
                for (String s : studentNames)
                {
                    String[] names = s.split(",");
                    outMessage += "#";
                    outMessage += names[0].trim();
                    outMessage += "," + names[1].trim();
                    outMessage += "," + names[2].trim();
                    outMessage += checkpoints;
                }
                client.sendSecure(outMessage);

                String message = "";
                while (message.equals(""))
                {
                    message = client.getLastMessage();
                }
                String[] messageParams = message.split("#");
                String sess = messageParams[1];//TODO here's the session ID. do what you want with it.
                Log.i(TAG, "Session ID Obtained: " + sess);
                sessionID = sess;
                intentMain.putExtra("session", sessionID);

                client.sendSecure("positionInit#" + sessionID + "#4,4,4,3");

                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ImportActivity.this.startActivity(intentMain);
            }
        });

        //decided to make the specified folder "Tan" and it should be in the root of the device
        File csvFolder = new File("/sdcard/TAN");
        File[] csvList = csvFolder.listFiles();
        for(File x : csvList){
            Log.i(TAG, "File found in /sd/TAN: " + x);
        }
        if (csvList != null)
        {
            ArrayAdapter<File> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, csvList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            final Spinner sItems = (Spinner) findViewById(R.id.selectCSV);
            sItems.setAdapter(adapter);
            sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
                {
                    File xc = (File) sItems.getSelectedItem();
                    String cont = "";
                    try
                    {
                        cont = readFile(xc.toString());
                    }
                    catch (IOException noFile)
                    {
                        noFile.printStackTrace();
                    }
                    rosterPreview.setText(cont);


                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView)
                {
                    // your code here
                }

            });
            // String selected = sItems.getSelectedItem().toString();
//            if (selected.equals("what ever the option was"))
//            {
//            }
            File selected2 = (File) selectCSV.getSelectedItem();
            try
            {
                String[] path = selected2.getPath().split("/");
                String fileName = path[path.length - 1];
                courseId = fileName.split("-")[0].substring(2, 5);
                courseSection = fileName.split("-")[0].substring(5);
                courseName = fileName.split("-")[1].split("\\.")[0];
                content = readFile(selected2.toString());
                Log.i(TAG, "Content: " + content);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            rosterPreview.setText(content);
        }
        else
        {
            rosterPreview.setText("NO CSV FILES FOUND");
        }
    }

    /**
     * A helper method to read a file.
     *
     * @param file - the file to read
     * @return - the string containing the file's contents
     * @throws IOException
     */
    private String readFile(String file) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        try
        {
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        }
        finally
        {
            reader.close();
        }
    }

    private void setRosterPreview(String roster)
    {
        rosterPreview.setText(roster);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.client = WebSocketHandler.getWebSocket();

        Log.i(TAG, "onResume reached");
    }
}
