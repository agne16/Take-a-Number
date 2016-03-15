package edu.up.projects.engineering.takeanumberandroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * Based on http://javatechig.com/android/android-service-example
 */
public class NetworkService extends Service {

    private static final String TAG = "Network Service";
    private static WebSocketHandler serverConnection = null;

    private boolean isRunning  = false;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                System.out.println("onStartCommand invoked");
                try
                {
                    serverConnection = new WebSocketHandler(new URI("http://10.17.141.39:8080"));

                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
                serverConnection.connect();
                serverConnection.waitForReady();

                serverConnection.send("identify#tablet");
            }
        }).start();

        stopSelf();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }

    public static WebSocketHandler getServerConnection()
    {
        return serverConnection;
    }
}

