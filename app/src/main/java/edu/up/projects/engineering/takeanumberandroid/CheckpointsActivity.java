package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;

public class CheckpointsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener
{

    static String staticRoster;//a string containing the contents of the .csv file entered as the roster
    static int staticChecks = 5;//the number of checkpoints for this lab, set to 5 by default for testing
    static Hashtable<String, Boolean> checkpointSaved;//a saved list of checkpoints that is accessed every time the activity is loaded, so checks persist through navigation
    static CheckBox[][] checkList;
    static String sessionId;

    private static final String TAG = "CheckpointsActivity";

    WebSocket client = null;

    //checkbox to compare when syncing
    //used to tell if the professor wants to send an "uncheck" to the server
    //as opposed to checking someone mistakenly and unchecking them
    boolean[][] lastUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        int dpValue = 4; // margin in dips
        float d = getResources().getDisplayMetrics().density;
        int margin = (int)(dpValue * d); // margin in pixels

        try
        {
            sessionId = getIntent().getExtras().getString("session");
            QueueActivity2.sessionID = sessionId;
        }
        catch(Exception sessionIDNotSet){
        }

        if (checkpointSaved == null)//to avoid overwriting existing checkpoint data
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
                //placeholder, used for testing
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


        //for reference, format of roster expected is:
        //UP ID, first name, lastname newline...
        final String[] rooster = staticRoster.split("\\r?\\n");
        String[] studentNames = new String[rooster.length];
        String[] studentIds = new String[rooster.length];//not currently used, but can be useful if you need student ids
        int index = 0;
        for (String x : rooster)
        {
            String[] temp = x.split(",");
            studentIds[index] = temp[0].trim();
            studentNames[index] = temp[1] + temp[2];
            index++;
        }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, R.layout.mylist, studentNames);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            theRoster.setAdapter(adapter);

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
        //create and draw the checkpoint list
        for (String x : rooster)
        {
            LinearLayout column = new LinearLayout(this);

            column.setOrientation(LinearLayout.HORIZONTAL);
            CheckBox[] checkRow = new CheckBox[staticChecks];
            for (int i = 0; i < staticChecks; i++)
            {
                int secondDigit = i+1;
                String id = "" + counter + "" + secondDigit; //counter = row, secondDigit = column
                CheckBox check = new CheckBox(this);
                check.setOnClickListener(this);
                check.setOnLongClickListener(this);
                LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //left, top, right, bottom margins
                par.setMargins(5, 0, 5, margin);

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

                //client.sendSecure(toSend);
                //System.out.println("INFO-checkpointSync Button :" + toSend);

            }
        });

        //converts tablet's checkpoint list into a .csv
        exportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    PrintWriter writer = new PrintWriter("/sdcard/TAN/abc.csv");
                    int counter = 0;
                    for (String x : rooster)
                    {
                        CheckBox[] oneRow = checkList[counter];
                        writer.print(x + ",");
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
    //assumes the only checkboxes on the page are for checkpoints
    public void onClick(View view)
    {
        if (view instanceof CheckBox)
        {
            CheckBox temp = ((CheckBox) view);
            temp.setChecked(true);
            String s = checkboxToString(staticRoster.split("\\r?\\n"));
            String id = "" + temp.getId();
            checkpointSaved.put(id, true);
            client.sendSecure(s);
        }
    }

    /**
     * updateCheckpoints - merge the server's checkpoint list with ours, then refresh the page to apply the changes
     *
     * @param updatedList - the updated checkpoint list that should come from the server
     */
    public void updateCheckpoints(String updatedList)
    {
        if(updatedList.equals(""))
        {
            return;
        }
        String[] classData = updatedList.split("#");
        String[] checkpointData = Arrays.copyOfRange(classData, 2, classData.length);
        int studentNumber = 0;
        for (String data : checkpointData)
        {
            String checkpoints[] = data.split(",");
            for (int i = 3; i < checkpoints.length; i++)
            {
                CheckBox currBox = checkList[studentNumber][i-3];
                if (checkpoints[i].equals("1"))
                {
                    currBox.setChecked(true);
                    String id = "" + currBox.getId();
                    checkpointSaved.put(id, true);
                }
                else
                {
                    currBox.setChecked(false);
                    String id = "" + currBox.getId();
                    checkpointSaved.put(id, false);
                }
                currBox.refreshDrawableState();
            }
            studentNumber++;
        }
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
            CheckBox temp = ((CheckBox) v);
            temp.setChecked(false);
            String s = checkboxToString(staticRoster.split("\\r?\\n"));
            String id = "" + temp.getId();
            checkpointSaved.put(id, true);
            client.sendSecure(s);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {

        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(!MainActivity.isTesting) {

            this.client = WebSocketHandler.getWebSocket();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        while (!client.needUpdate) {
                            try
                            {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateCheckpoints(client.getLastMessage());
                            }
                        });
                        client.needUpdate = false;
                    }
                }
            }).start();

        }
        Log.i(TAG, "onResume reached");
    }

    /**
     * converts the array of student checkpoint data to a condensed string
     *
     * @param roster array of string containing each student's name and checkpoint data
     * @return the condensed string
     */
    public String checkboxToString(String[] roster)
    {
        //convert into format then send to server

        //format is: CHECKPOINT#SESSION ID#rest
        String toSend = "checkpointSync#" + sessionId;

        //convert the contents into the proper format
        //format will be:
        //full name#checkpoint#checkpoint#checkpoint#...fullname#checkpoint#checkpoint#checkpoint...etc
        int counter = 0;
        for (String x : roster)
        {
            //full name#
            String[] names = x.split(",");
            String newNames = names[0].trim() + "," + names[1].trim() + "," + names[2].trim();

            toSend = toSend + "#" + newNames + ",";
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
        return toSend;
    }

}

