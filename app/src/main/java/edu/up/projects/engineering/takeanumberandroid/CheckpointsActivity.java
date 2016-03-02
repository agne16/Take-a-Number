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


import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
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
                staticRoster = "Micah, Alconcel \n Teolo, Agne \n Matthew, Farr \n Nick, Sohm \n Andrew, Vegdahl \n Steven, Nuxoll";
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


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, studentNames);
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

        //only recreate checkList if it's null


        checkList = new CheckBox[rooster.length][];
        for (String x : rooster)
        {
            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.HORIZONTAL);
            CheckBox[] checkRow = new CheckBox[staticChecks];
            for (int i = 0; i < staticChecks; i++)
            {
                String id = "" + counter + "" + i;
                CheckBox check = new CheckBox(this);
                check.setOnClickListener(this);
                check.setOnLongClickListener(this);
                LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //left, top, right, bottom margins
                par.setMargins(5, 0, 5, 10);

                check.setLayoutParams(par);
                //give each checkbox a unique id so we can access it when it's time to output
                int ids = Integer.parseInt(id);
                check.setId(ids);
                checkRow[i] = check;
                column.addView(check);

                if (checkpointSaved.get(id) == null)
                {
                    checkpointSaved.put(id, false);
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
                SendfeedbackJob job = new SendfeedbackJob();
                job.execute(toSend, "" + staticChecks);

                //TODO need to check if this is an appropriate way of doing receiving message from the server
                System.out.println("INFO-checkpointSync Button :" + toSend);

                while (job.getMergeResult().equals(""))
                {
                    //wait until server delivers the goods
                }


                if (!serverResponse.equals(""))
                {
                    updateCheckpoints(serverResponse);
                }

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
                    PrintWriter writer = new PrintWriter("/sdcard/TAN/abc.txt");
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


    /**
     * A helper class to send/receive messages from a java server
     */
    private class SendfeedbackJob extends AsyncTask<String, Void, String>
    {
        String message2 = "";
        String receivedMessage = "";
        PrintWriter out;
        int numChecks = 0;
        int rosterLength = 0;
        String mergeResult = "";

        //TODO need to move networking to a service
        //http://stackoverflow.com/questions/7783127/keep-socket-connection-between-activities-on-android

        @Override
        protected String doInBackground(String[] params)
        {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;
            message2 = params[0];
            numChecks = Integer.parseInt(params[1]);
            //rosterLength = Integer.parseInt(params[2]);
            try
            {
                //socket = new Socket("10.17.3.72", 8080);
                socket = new Socket(MainActivity.IP, MainActivity.port);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("INFO-message being sent to server: " + message2);
                out.println(message2);
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
            try
            {
                //wait for the server's response
                while (true)
                {
                    String x = "";

                    x = dataInputStream.readLine();
                    receivedMessage = x;
                    System.out.println("INFO response from server: " + x);

                    //mergeee

                    //TODO we're getting some wonky stuff after sync.
                    //Where is the checkpoint update stuff

                    if (x.equals(""))
                    {
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
                    else
                    {
                        //this means we received the checkpoint update from the server
                        mergeResult = x;
                    }

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return mergeResult;
        }


        @Override
        protected void onPostExecute(String message)
        {
            serverResponse = message;

        }

        /**
         * mergeTwo - a function that merges two Strings (that should represent checkpoint lists) into one
         * format of the string should be CHECKPOINT#SESSION ID#name,cp1,cp2...#name,cp1,cp2... etc
         *
         * @param mine    - the string the tablet sent
         * @param servers - the string stored on the server
         */
        public void mergeTwo(String mine, String servers)
        {
            String[] myFields = mine.split("#");
            String[] serverFields = servers.split("#");
            String serversChecks = serverFields[2];


            //merge them
            //ignoring freshly checked for now

            String[] mergeResult = new String[rosterLength];

            //index 0 = type of message, index 1 = session id, everything else = checkpoint info for each student
            for (int i = 2; i < myFields.length; i++)
            {
                //should be of format fullname,cp1,cp2...
                String myCurrent = myFields[i];

                //index 0 = name, everything else = checkpoint info
                String[] parsedMine = myCurrent.split(",");
                String[] parsedServer = serversChecks.split(",");

                //should be the name
                mergeResult[i - 2] = parsedMine[0];

                for (int j = 1; j < numChecks + 1; j++)
                {
                    if (parsedMine[j].equals("1") || parsedServer[j].equals("1"))
                    {
                        mergeResult[i - 2] = mergeResult[i - 2] + ",1";
                    }
                    else
                    {
                        mergeResult[i - 2] = mergeResult[i - 2] + ",0";
                    }
                }
                mergeResult[i - 2] = mergeResult[i - 2] + "#";
            }
        }

        public String getMergeResult()
        {
            return mergeResult;
        }
    }


}

