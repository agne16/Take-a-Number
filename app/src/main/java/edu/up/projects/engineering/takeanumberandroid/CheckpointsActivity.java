package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.LinearLayout;
import android.widget.ListView;


import org.java_websocket.client.WebSocketClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Hashtable;

public class CheckpointsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener
{

    static String staticRoster;
    static int staticChecks = 5;
    static String serverResponse = "";
    static Hashtable<String, Boolean> checkpointSaved;
    CheckBox[][] checkList;

    String mergeResult = "";

    String host = "http://192.168.1.144:8080";

    //checkbox to compare when syncing
    //used to tell if the professor wants to send an "uncheck" to the server
    //as opposed to checking someone mistakenly and unchecking them
    boolean[][] lastUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (checkpointSaved == null)
        {
            checkpointSaved = new Hashtable<String, Boolean>();
        }

        try
        {
            staticRoster = getIntent().getExtras().getString("roster");
        }
        catch (NullPointerException extraNotSet)
        {
            //means the roster hasn't been set yet
            //there might still be an old roster stored, so don't set it unless it's null
            if (staticRoster == null)
            {
                //placeholder
                //maaaaybe remove before release?
                staticRoster = "alconcel16,Micah, Alconcel \n agne16,Teolo, Agne \n farr16,Matthew, Farr \n sohm16,Nick, Sohm \n vegdahl,Andrew, Vegdahl \n nuxoll,Steven, Nuxoll \n" +
                        " agne16,Teolo, Agne \n" +
                        " farr16,Matthew, Farr \n" +
                        " sohm16,Nick, Sohm \n" +
                        " vegdahl,Andrew, Vegdahl \n" +
                        " nuxoll,Steven, Nuxoll\n" +
                        " agne16,Teolo, Agne \n" +
                        " farr16,Matthew, Farr \n" +
                        " sohm16,Nick, Sohm \n" +
                        " vegdahl,Andrew, Vegdahl \n" +
                        " nuxoll,Steven, Nuxoll \n"+
                        "fennekin, fennekin, fennekin \n" +
                        "fennekin, fennekin, fennekin \n" +
                        "fennekin, fennekin, fennekin \n" +
                        "fennekin, fennekin, fennekin \n";
                ;
            }
        }

        try
        {
            staticChecks = getIntent().getExtras().getInt("numChecks");
        }
        catch (NullPointerException extraNotSet)
        {
            //means roster's already been set
            //or they haven't created a lab yet
        }


        try
        {
            QueueActivity2.layout = getIntent().getExtras().getIntArray("layout");
        }
        catch (NullPointerException extraNotSet)
        {
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

        LinearLayout rows = (LinearLayout) findViewById(R.id.checkRows);


        final String[] rooster = staticRoster.split("\\r?\\n");
        String[] studentNames = new String[rooster.length];
        String[] studentIds = new String[rooster.length];
        int index = 0;
        for (String x : rooster)
        {
            String[] temp = x.split(",");
            studentIds[index] = temp[0].trim();
            studentNames[index] = temp[1] + temp[2];
            index++;
        }
        int botMar = 17;
        boolean isSmall = true;
            CheckBox che = (CheckBox) findViewById(R.id.smallSize);
        if(che == null){
            isSmall = false;
        }

        if(!isSmall){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, studentNames);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            theRoster.setAdapter(adapter);
        }
        else{
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, R.layout.mylist, studentNames);
            adapter.setDropDownViewResource(R.layout.mylist);
            theRoster.setAdapter(adapter);
            botMar = 8;
        }


        //initialize it to a completely empty checkbox at the beginning
        if (lastUpdated == null)
        {
            lastUpdated = new boolean[rooster.length][];
            boolean[] initChecks = new boolean[staticChecks];
            for (int i = 0; i < initChecks.length; i++)
            {
                initChecks[i] = false;
            }

            for (int j = 0; j < lastUpdated.length; j++)
            {
                lastUpdated[j] = initChecks;
            }
        }


        //create a row for each name in the list
        int counter = 1;




        checkList = new CheckBox[rooster.length][];
        for (String x : rooster)
        {
            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.HORIZONTAL);
            CheckBox[] checkRow = new CheckBox[staticChecks];
            for (int i = 0; i < staticChecks; i++)
            {
                int secondDigit = i+1;
                String id = "" + counter + "" + secondDigit;
                CheckBox check = new CheckBox(this);
                check.setOnClickListener(this);
                check.setOnLongClickListener(this);
                LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //left, top, right, bottom margins
                par.setMargins(5, 0, 5, botMar);

                check.setLayoutParams(par);
                //give each checkbox a unique id so we can access it when it's time to output
                int ids = Integer.parseInt(id);
                check.setId(ids);
                checkRow[i] = check;
                column.addView(check);

                if (checkpointSaved.get(id) == null)
                {
                    checkpointSaved.put(id, false);
                    check.setChecked(false);
                }
                else
                {
                    check.setChecked(checkpointSaved.get(id));
                }
            }
            checkList[counter - 1] = checkRow;
            counter++;
            rows.addView(column);
        }


        //these listeners are for the three buttons at the top
        setupB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(CheckpointsActivity.this,
                        MainActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                CheckpointsActivity.this.startActivity(intentMain);

            }
        });

        queueB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(CheckpointsActivity.this,
                        QueueActivity2.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                CheckpointsActivity.this.startActivity(intentMain);

            }
        });

        checkpointsB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(CheckpointsActivity.this,
                        CheckpointsActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                CheckpointsActivity.this.startActivity(intentMain);

            }
        });

        syncB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //convert into format then send to server

                //format is: CHECKPOINT#SESSION ID#rest
                String toSend = "checkpointSync#777A01";//TODO Hardcoded. Will need to provide sessionId

                //convert the contents into the proper format
                //format will be:
                //full name#checkpoint#checkpoint#checkpoint#...fullname#checkpoint#checkpoint#checkpoint...etc
                int counter = 0;
                for (String x : rooster)
                {
                    //full name#
                    String[] names = x.split(",");
                    String newNames = names[0].trim() + "," + names[1].trim() + "," + names[2].trim();

                    toSend = toSend + "#" + newNames + ",";
                    //TODO add check if freshly unchecked
                    CheckBox[] oneRow = checkList[counter];
                    for (CheckBox y : oneRow)
                    {
                        if (y.isChecked())
                        {
                            //checkpoint#
                            toSend = toSend + "1,";
                        }
                        else
                        {
                            toSend = toSend + "0,";
                        }
                    }
                    counter++;
                }
                //SendfeedbackJob job = new SendfeedbackJob();
                WebSocketHandler client = null;
                try
                {
                    client = new WebSocketHandler(new URI(host));
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
                client.connect();
                client.waitForReady();
                client.send(toSend);

                System.out.println("INFO-checkpointSync Button :" + toSend);

                String response = client.getLastMessage();
                while (response.equals(""))
                {
                    response = client.getLastMessage();
                }
                System.out.println(response);
                updateCheckpoints(response);
                client.close();

            }
        });


        exportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //convert into csv then save to folder here
                try
                {
                    PrintWriter writer = new PrintWriter("/sdcard/TAN/abc.csv");
                    int counter = 0;
                    for (String x : rooster)
                    {
                        CheckBox[] oneRow = checkList[counter];
                        writer.print(x + "'s checkpoints: ");
                        for (CheckBox y : oneRow)
                        {
                            if (y.isChecked())
                            {
                                writer.print("1, ");
                            }
                            else
                            {
                                writer.print("0, ");
                            }
                        }
                        writer.println();
                        counter++;
                    }
                    writer.close();
                }
                catch (FileNotFoundException fnfe)
                {
                    fnfe.printStackTrace();
                }

            }
        });


    }

    //a short click should always leave it checked
    public void onClick(View view)
    {
        if (view instanceof CheckBox)
        {

            CheckBox temp = (CheckBox) view;
            temp.setChecked(true);
            String id = "" + temp.getId();
            checkpointSaved.put(id, true);
        }
    }

    /**
     * updateCheckpoints - merge the server's checkpoint list with ours, then refresh the page to apply the changes
     *
     * @param updatedList - the updated checkpoint list that should come from the server
     */
    public void updateCheckpoints(String updatedList)
    {
        String[] initSplit = updatedList.split("#");
        int counter = 1;

        //checkpoints should start at 2
        //TODO clarify whether the name is split into first, last
        //TODO double check format
        //checkpoint#session id#id,firstname,lastname,1,1,1,....#id,firstname,lastname,
        for (int i = 2; i < initSplit.length; i++)
        {
            String[] currentList = initSplit[i].split(",");
            //3 = start of checkpoints
            for (int j = 3; j < currentList.length; j++)
            {
                int secondDigit = j - 2;
                String id = "" + counter + secondDigit;
                if (currentList[j].equals("1"))
                {
                    checkpointSaved.put(id, true);
                }
                else
                {
                    checkpointSaved.put(id, false);
                }
            }
            counter++;
        }
        Intent intentMain = new Intent(CheckpointsActivity.this,
                CheckpointsActivity.class);
        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        CheckpointsActivity.this.startActivity(intentMain);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }

    //a long click should always leave it unchecked
    @Override
    public boolean onLongClick(View v)
    {
        if (v instanceof CheckBox)
        {
            CheckBox temp = (CheckBox) v;
            temp.setChecked(false);
            String id = "" + temp.getId();
            checkpointSaved.put(id, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {

        return false;
    }
}

