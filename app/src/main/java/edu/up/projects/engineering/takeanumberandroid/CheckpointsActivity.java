package edu.up.projects.engineering.takeanumberandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class CheckpointsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpoints);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //AYYYY should do saveinstancestate later so we don't have to keep track of this shtuff
        String roster = getIntent().getExtras().getString("roster");

        Button setupB = (Button) findViewById(R.id.setupButton);
        Button queueB = (Button) findViewById(R.id.queueButton);
        Button checkpointsB = (Button) findViewById(R.id.checkpointsButton);


                  /*
         * AYYYY for these to work, ya gotsta set whatever data you need for the page or something.
         */
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

}