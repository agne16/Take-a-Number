package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class ImportActivity extends AppCompatActivity
{
    String sessionID;
    int[] layoutParams = new int[4];
    private String content = "";
    private String courseId;
    private String courseSection;
    private String courseName;
    EditText rosterPreview;


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
                //TODO - is sessionID given by server?
                intentMain.putExtra("session", sessionID);

                SendfeedbackJob job = new SendfeedbackJob();
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
                job.execute(outMessage);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ImportActivity.this.startActivity(intentMain);
            }
        });

        //decided to make the specified folder "Tan" and it should be in the root of the device
        File csvFolder = new File("/sdcard/TAN");
        File[] csvList = csvFolder.listFiles();
        System.out.println("/sdcard/TAN");
        for(File x : csvList){
            System.out.println(x);
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


    private class SendfeedbackJob extends AsyncTask<String, Void, String>
    {
        String message2 = "";
        String outMessage = "";
        String serverResponse = "";
        PrintWriter out;

        @Override
        protected String doInBackground(String[] params)
        {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            try
            {
                socket = new Socket(MainActivity.IP, MainActivity.port);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());

                outMessage = params[0];
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(outMessage);
                out.println("");
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            String x = "";
            try
            {
                while (true)
                {
                    x = "";

                    x = dataInputStream.readLine();
                    System.out.println(x);


                    if (x.equals(""))
                    {
                        dataInputStream.close();
                        dataOutputStream.close();
                        socket.close();
                        out.println("");
                        System.out.println("CLOSING AHHHHHHHHH");
                        break;
                    }
                    else
                    {
                        //means they sent us data, rather than the closing message
                        serverResponse = x;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return x;
        }

        @Override
        protected void onPostExecute(String message)
        {
            message2 = message;
        }

        protected String getServerResponse()
        {
            return serverResponse;
        }
    }
}
