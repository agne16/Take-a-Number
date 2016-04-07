package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Hashtable;

public class QueueActivity2 extends AppCompatActivity implements View.OnClickListener
{
    public static int[] layout;
    public static String sessionID;
    public static Hashtable<String, Button> positions;
    public static Button[] posits;
    WebSocket client = null;
    boolean testing = false;
    int[] layoutParams;
    private static final String TAG = "QueueActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (layout == null)
        {
            //set a default layout that's all empty
            //as a reminder, 0 = left rows, 1 = right rows, 2 = left columns, 3 = right columns
            layout = new int[4];
            layout[0] = 4;
            layout[1] = 4;
            layout[2] = 4;
            layout[3] = 3;
        }

        if (sessionID == null)
        {
            try
            {
                sessionID = getIntent().getExtras().getString("session");
            }
            catch (Exception sessionIDNotSet)
            {
                sessionID = "NO SESSION ID FOUND, PLEASE TRY AGAIN NEXT YEAR";
            }
        }


        LinearLayout leftRows = (LinearLayout) findViewById(R.id.leftRows);
        LinearLayout rightRows = (LinearLayout) findViewById(R.id.rightRows);

        //create buttons for left row
        int totalLeftRows = layout[0];
        int totalLeftColumns = layout[1];
        int totalRightRows = layout[2];
        int totalRightColumns = layout[3];

        if (positions == null)
        {
            positions = new Hashtable<String, Button>();
        }
        //TODO - need to get names of students from the server
        for (int currentRow = 0; currentRow < totalLeftRows; currentRow++)
        {
            LinearLayout oneRow = new LinearLayout(this);
            oneRow.setOrientation(LinearLayout.HORIZONTAL);
            for (int currentColumn = 0; currentColumn < totalLeftColumns; currentColumn++)
            {
                int colId = currentColumn + 1;
                int rowId = currentRow + 1;

                String id = "c" + currentColumn + "r" + currentRow;
                Log.i(TAG, "Seat created: " + id);
                Button onePosition = new Button(this);
                onePosition.setOnClickListener(this);
                onePosition.setWidth(130);
                onePosition.setHeight(130);
                if (positions.get(id) == null)
                {
                    onePosition.setText("EMPTY");
                    positions.put(id, onePosition);
                }
                else
                {
                    String name = positions.get(id).getText().toString();
                    onePosition.setText(name);
                    //not sure if need to overwrite
                    positions.put(id, onePosition);
                }
                oneRow.addView(onePosition);
            }
            leftRows.addView(oneRow);

        }

        for (int currentRow = 0; currentRow < totalRightRows; currentRow++)
        {
            LinearLayout oneRow = new LinearLayout(this);
            oneRow.setOrientation(LinearLayout.HORIZONTAL);
            for (int currentColumn = 0; currentColumn < totalRightColumns; currentColumn++)
            {
                int colIdOffset = currentColumn + totalLeftColumns;
                int rowId = currentRow;

                String id = "c" + colIdOffset + "r" + currentRow;
                Log.i(TAG, "Seat created: " + id);
                Button onePosition = new Button(this);
                onePosition.setOnClickListener(this);
                onePosition.setWidth(130);
                onePosition.setHeight(130);
                if (positions.get(id) == null)
                {
                    onePosition.setText("EMPTY");
                    positions.put(id, onePosition);
                }
                else
                {
                    String name = positions.get(id).getText().toString();
                    onePosition.setText(name);
                    //not sure if need to overwrite
                    positions.put(id, onePosition);
                }
                oneRow.addView(onePosition);
            }
            rightRows.addView(oneRow);

        }

        if(positions==null && !testing){
            positions = new Hashtable<String,Button>();
            //need to get student positions from server
            String serverResponse = networkRequest("getPositions#"+sessionID);
            updateQueue(serverResponse);
        }
        else if(positions==null){
            //format is messageType#firstname,lastname,position,queueNum#...
            updateQueue("POSITIONS#first,last,c1r1,0#first,last,c132,1");
        }
        else
        {
            String serverResponse = networkRequest("getPositions#"+sessionID);
            updateQueue(serverResponse);
        }

        Button setupB = (Button) findViewById(R.id.setupButton);
        Button queueB = (Button) findViewById(R.id.queueButton);
        Button checkpointsB = (Button) findViewById(R.id.checkpointsButton);


        setupB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(QueueActivity2.this,
                        MainActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                QueueActivity2.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        queueB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });

        checkpointsB.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentMain = new Intent(QueueActivity2.this,
                        CheckpointsActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                QueueActivity2.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        Button queueSync = (Button) findViewById(R.id.queueSync);
        queueSync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String toSend = "getposition#"+sessionID;
                String serverResponse = "";
                if (!testing)
                {
                    serverResponse = networkRequest(toSend);
                }
                else
                {
                    serverResponse = "QUEUEPOSITIONS#first,last,c1r1,0#fennekin,fennekin,c3r3,1";
                }

                updateQueue(serverResponse);

            }
        });


    }


    /**
     * updateQueue -
     * helper method to update the queue UI after a response from the server
     * used to:
     * get positions of students in the room
     * get queue positions of students in the room
     *
     * @param response - the response from the server
     */
    public void updateQueue(String response)
    {
        if (response.equals("positions")){
            return;
        }

        String[] initSplit = response.split("#");
        for (int currentName = 1; currentName < initSplit.length; currentName++)
        {
            //first,last,c1r1,0
            String[] oneStudent = initSplit[currentName].split(",");
            String id = oneStudent[2];
            int queuePosition = Integer.parseInt(oneStudent[3]);
            Log.i(TAG, id);
            //POSITIONS#first,last,c1r1,0#first,last,c132,1
            positions.get(id).setText(oneStudent[0] + " " + oneStudent[1]);
            switch (queuePosition)
            {
                case 0:
                    //means not in queue
                    positions.get(id).setBackgroundColor(Color.GRAY);
                    break;
                case 1:
                    //means first in queue
                    positions.get(id).setBackgroundColor(Color.BLUE);
                    break;
                case 2:
                    //second, duh
                    positions.get(id).setBackgroundColor(Color.CYAN);
                    break;
                default:
                    //3+ in queue
                    positions.get(id).setBackgroundColor(Color.GRAY);
                    break;
            }
        }

    }

    public void removeFromQueue(Button butt)
    {
        //TODO check if that's the right format
        Button name = positions.get(butt.getId());
        String toSend = "removeFromQueue#" + name.getText();
        String serverResponse = networkRequest(toSend);

    }

    public String networkRequest(String message)
    {
        String toSend = message;
        if (this.client == null)
        {
            this.client = WebSocketHandler.getWebSocket();
        }
        client.send(toSend);

        //Log.i("INFO-checkpointSync Button :" + toSend);

        String response = client.getLastMessage();
        while (response.equals(""))
        {
            response = client.getLastMessage();
        }
        Log.i(TAG, "Message received: " + response);
        updateQueue(response);
        client.close();
        return "";
    }


    @Override
    public void onClick(View v)
    {
        //NOTE - SHOULD ONLY BE USED FOR QUEUE BUTTONS
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.client = WebSocketHandler.getWebSocket();
        Log.i(TAG, "onResume reached");
    }
}
