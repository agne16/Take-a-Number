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
    public String roster;
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
                staticRoster = "Micah, Alconcel \n Teolo, Agne \n Matthew, Farr \n Nick, Sohm \n Andrew, Vegdahl \n Steven, Nuxoll";
            }
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

        HorizontalScrollView checkCont = (HorizontalScrollView) findViewById(R.id.checkContainer);
        LinearLayout rows = (LinearLayout) findViewById(R.id.checkRows);


        String[] rooster = staticRoster.split("\\r?\\n");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, rooster);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        theRoster.setAdapter(adapter);

        for(String x: rooster){
            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.HORIZONTAL);
            for(int i = 0;i<10;i++){
                CheckBox check = new CheckBox(this);
                LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                //ltrb
                par.setMargins(5, 0, 5, 10);
                check.setLayoutParams(par);
                column.addView(check);
            }
            rows.addView(column);
        }







        setupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(CheckpointsActivity.this,
                        MainActivity.class);
                CheckpointsActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        queueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(CheckpointsActivity.this,
                        QueueActivity2.class);
                CheckpointsActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        checkpointsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMain = new Intent(CheckpointsActivity.this,
                        CheckpointsActivity.class);
                CheckpointsActivity.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub


    }
}
