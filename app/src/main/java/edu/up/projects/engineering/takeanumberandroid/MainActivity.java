package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    final static String IP = "192.168.1.144";
    final static int port = 8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_setup);

        Button setupB = (Button) findViewById(R.id.setupButton);
        Button queueB = (Button) findViewById(R.id.queueButton);
        Button checkpointsB = (Button) findViewById(R.id.checkpointsButton);


        setupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        queueB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(MainActivity.this ,
                        QueueActivity2.class);
                MainActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        checkpointsB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(MainActivity.this ,
                        CheckpointsActivity.class);
                MainActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });
        //These are buttons specific to this class
        Button loadSessionButton = (Button) findViewById(R.id.loadSessionButton);
        Button presetButton = (Button) findViewById(R.id.presetButton);
        Button createLayoutButton = (Button) findViewById(R.id.createLayoutButton);




        loadSessionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText sessionIDField = (EditText) findViewById(R.id.sessionID);
                String sessionID = sessionIDField.getText().toString();
                moveToImport(sessionID);
            }
        });

        presetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //preset for 206/249
                int[] layoutParams = new int[4];
                layoutParams[0] = 5;
                layoutParams[1] = 5;
                layoutParams[2] = 4;
                layoutParams[3] = 3;
                moveToImport(layoutParams);
            }
        });

        createLayoutButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText leftRow = (EditText) findViewById(R.id.leftRowInput);
                EditText rightRow = (EditText) findViewById(R.id.rightRowInput);
                EditText leftColumn = (EditText) findViewById(R.id.leftColumnInput);
                EditText rightColumn = (EditText) findViewById(R.id.rightColumnInput);

                boolean properFormat = true;
                int leftRows = 0, rightRows = 0, leftColumns = 0, rightColumns = 0;

                try{
                    leftRows = Integer.parseInt(leftRow.getText().toString());
                    rightRows = Integer.parseInt(rightRow.getText().toString());
                    leftColumns = Integer.parseInt(leftColumn.getText().toString());
                    rightColumns = Integer.parseInt(rightColumn.getText().toString());
                }
                catch (Exception invalidInt){
                    properFormat = false;
                    // TODO: 2/5/2016  popup dialog box telling them they're bad for leaving a field empty?
                }

                if(properFormat){
                    int[] layouts = {leftRows, rightRows, leftColumns, rightColumns};
                    moveToImport(layouts);
                }

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO - double check the control flow of this - will we go to importactivity first?
    //if they're loading a sessionID
    public void moveToImport(String sessionID){
        Intent intentMain = new Intent(MainActivity.this,
                CheckpointsActivity.class);
        intentMain.putExtra("session", sessionID);

        SendfeedbackJob job = new SendfeedbackJob();
        job.execute(sessionID);
        while(job.getServerResponse().equals("")){
            //wait for server response
        }
        String params = job.getServerResponse();
        //TODO figure out how server response is formatted so we can parse it and pass the data to the other tabs

        MainActivity.this.startActivity(intentMain);
        Log.i("Content ", " Main layout ");
    }

    //if they're creating their own layout
    public void moveToImport(int[] layouts){
        Intent intentMain = new Intent(MainActivity.this,
                ImportActivity.class);
        intentMain.putExtra("layout", layouts);

        MainActivity.this.startActivity(intentMain);
        Log.i("Content ", " Main layout ");
    }

    //if they're using a preset layout
    public void moveToImport(){
        Intent intentMain = new Intent(MainActivity.this,
                ImportActivity.class);

        MainActivity.this.startActivity(intentMain);
        Log.i("Content ", " Main layout ");
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
                    else{
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

        protected String getServerResponse(){
            return serverResponse;
        }
    }
}
