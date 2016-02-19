package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
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

import java.io.File;

public class CheckpointsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    static String staticRoster;
    static int staticChecks = 5;
    public String roster;
    public CheckBox[][] checkList;
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

        //create a row for each name in the list
        int counter = 0;
        checkList = new CheckBox[rooster.length][];
        for(String x: rooster){
            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.HORIZONTAL);
            CheckBox[] checkRow = new CheckBox[staticChecks];
            for(int i = 0;i<staticChecks;i++){
                CheckBox check = new CheckBox(this);
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
                //convert into xml then send to server here

            }
        });


        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //convert into csv then save to folder here
                int counter = 0;
                for(String x : rooster){
                    CheckBox[] oneRow = checkList[counter];
                    System.out.print(x + "'s checkpoints: ");
                    for(CheckBox y : oneRow){
                        if(y.isChecked()){
                            System.out.print("1, ");
                        }
                        else{
                            System.out.print("0, ");
                        }
                    }
                    System.out.println();
                    counter++;
                }
            }
        });


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
