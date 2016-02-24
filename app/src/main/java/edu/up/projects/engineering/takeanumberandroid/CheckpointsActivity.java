package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class CheckpointsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, View.OnLongClickListener{

    static String staticRoster;
    static int staticChecks = 5;
    public String roster;
    public CheckBox[][] checkList;

    //checkbox to compare when syncing
    //used to tell if the professor wants to send an "uncheck" to the server
    //as opposed to checking someone mistakenly and unchecking them
    boolean[][] lastUpdated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try{
            staticRoster = getIntent().getExtras().getString("roster");
        }
        catch(NullPointerException extraNotSet){
            //means the roster hasn't been set yet
            //there might still be an old roster stored, so don't set it unless it's null
            if(staticRoster == null){
                //placeholder
                //maaaaybe remove before release?
                staticRoster = "Micah, Alconcel \n Teolo, Agne \n Matthew, Farr \n Nick, Sohm \n Andrew, Vegdahl \n Steven, Nuxoll";
            }
        }

        try{
            staticChecks = getIntent().getExtras().getInt("numChecks");
        }
        catch(NullPointerException extraNotSet){
            //means roster's already been set
            //or they haven't created a lab yet
        }


        try{
            QueueActivity2.layout = getIntent().getExtras().getIntArray("layout");
        }
        catch (NullPointerException extraNotSet){
            //means the layout hasn't been set yet

        }



        setContentView(R.layout.activity_checkpoints);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Button setupB = (Button) findViewById(R.id.setupButton);
        Button queueB = (Button) findViewById(R.id.queueButton);
        Button checkpointsB = (Button) findViewById(R.id.checkpointsButton);
        ListView theRoster = (ListView) findViewById(R.id.rosterList);
        Button syncB = (Button) findViewById(R.id.syncButton);
        Button exportButton = (Button) findViewById(R.id.exportButton);

        HorizontalScrollView checkCont = (HorizontalScrollView) findViewById(R.id.checkContainer);
        LinearLayout rows = (LinearLayout) findViewById(R.id.checkRows);


        final String[] rooster = staticRoster.split("\\r?\\n");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, rooster);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        theRoster.setAdapter(adapter);

        //initialize it to a completely empty checkbox at the beginning
        if(lastUpdated == null){
            lastUpdated = new boolean[rooster.length][];
            boolean[] initChecks = new boolean[staticChecks];
            for(int i = 0;i<initChecks.length; i++){
                initChecks[i] = false;
            }

            for(int j = 0; j<lastUpdated.length;j++){
                lastUpdated[j] = initChecks;
            }
        }


        //create a row for each name in the list
        int counter = 0;
        checkList = new CheckBox[rooster.length][];
        for(String x: rooster){
            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.HORIZONTAL);
            CheckBox[] checkRow = new CheckBox[staticChecks];
            for(int i = 0;i<staticChecks;i++){
                CheckBox check = new CheckBox(this);
                check.setOnClickListener(this);
                check.setOnLongClickListener(this);
                LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //left, top, right, bottom margins
                par.setMargins(5, 0, 5, 10);

                check.setLayoutParams(par);
                //give each checkbox a unique id so we can access it when it's time to output
                int id = counter * staticChecks + i;
                check.setId(id);
                checkRow[i] = check;
                column.addView(check);
            }
            checkList[counter] = checkRow;
            counter++;
            rows.addView(column);
        }







        //these listeners are for the three buttons at the top
        setupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(CheckpointsActivity.this,
                        MainActivity.class);
                CheckpointsActivity.this.startActivity(intentMain);

            }
        });

        queueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(CheckpointsActivity.this,
                        QueueActivity2.class);
                CheckpointsActivity.this.startActivity(intentMain);

            }
        });

        checkpointsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(CheckpointsActivity.this,
                        CheckpointsActivity.class);
                CheckpointsActivity.this.startActivity(intentMain);

            }
        });

        syncB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //convert into format then send to server

                //TODO - add ip address, etc.
                //format is: CHECKPOINT#SESSION ID#IP ADDRESS#rest
                String toSend = "CHECKPOINT#64378#";

                //convert the contents into the proper format
                //format will be:
                //full name#checkpoint#checkpoint#checkpoint#...fullname#checkpoint#checkpoint#checkpoint...etc
                int counter = 0;
                for(String x : rooster){
                    CheckBox[] oneRow = checkList[counter];
                    //full name#
                    toSend = toSend+x+"#";
                    //TODO add check if freshly unchecked
                    for(CheckBox y : oneRow){
                        if(y.isChecked()){
                            //checkpoint#
                            toSend = toSend+"1#";
                        }
                        else{
                            toSend = toSend+"0#";
                        }
                    }
                }
                SendfeedbackJob job = new SendfeedbackJob();
                job.execute(toSend);
                System.out.println(toSend);

                //TODO send it to server

            }
        });


        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //convert into csv then save to folder here
                File exportedList = new File("abc.txt");
                try{
                    PrintWriter writer = new PrintWriter("/sdcard/TAN/abc.txt");
                    int counter = 0;
                    for(String x : rooster){
                        CheckBox[] oneRow = checkList[counter];
                        writer.print(x + "'s checkpoints: ");
                        for(CheckBox y : oneRow){
                            if(y.isChecked()){
                                writer.print("1, ");
                            }
                            else{
                                writer.print("0, ");
                            }
                        }
                        writer.println();
                        counter++;
                    }
                    writer.close();
                }
                catch(FileNotFoundException fnfe){

                }

            }
        });


    }

    //a short click should always leave it checked
    public void onClick(View view){
        if(view instanceof CheckBox){
            CheckBox temp = (CheckBox) view;
            temp.setChecked(true);
        }
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    //a long click should always leave it unchecked
    @Override
    public boolean onLongClick(View v) {
        if(v instanceof CheckBox){
            CheckBox temp = (CheckBox) v;
            temp.setChecked(false);
        }
        return false;
    }

    /**
     * A helper class to send/receive messages from a java server
     */
    private class SendfeedbackJob extends AsyncTask<String, Void, String> {
        String message2 = "";
        String receivedMessage = "";
        PrintWriter out;
        @Override
        protected String doInBackground(String[] params) {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            message2 = params[0];
            try {
                //socket = new Socket("10.17.3.72", 8081);
               socket = new Socket("192.168.1.144", 8080);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message2);
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            try {
                //wait for the server's response
                while (true) {
                    String x = "";

                    x = dataInputStream.readLine();
                    receivedMessage = x;
                    System.out.println(x);


                    if (x.equals("")) {
                        //close the connection with server
                        dataInputStream.close();
                        dataOutputStream.close();
                        socket.close();
                        //tell the server you're closing the connection
                        out.println("");
                        //AHHHHHHHHHHHHHHHH!!! CLOSING!
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

        //sets the message to send to the passed in message
        @Override
        protected void onPostExecute(String message) {
        }
    }
}
